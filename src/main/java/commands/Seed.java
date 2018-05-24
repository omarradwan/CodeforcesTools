package commands;

import com.google.gson.Gson;
import models.AcceptedProblem;
import models.ContestPerformance;
import models.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import utils.Scanner;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Seed {

    private static Scanner sc;
    private static HashSet<String> users;
    private static ArrayList<HashMap<String, Integer>> hacksScore;
    private static ArrayList<HashMap<String, Integer>> recentRanks;
    private static ArrayList<ArrayList<Integer>> contestsPoints;
    private static ArrayList<ArrayList<Integer>> contestsPenalties;
    private static HashMap<String, AcceptedProblem> acceptedProblemsMap;
    private static HashSet<Integer> usedContests;
    private static final String dataDirectory = "src/main/resources/data/";
    private static final String objectsDirectory = "objects/";


    public static void execute() throws IOException, ParseException {
        init();
        preprocessHandles();
        preprocessContests();
        preprocessProblems();
        preprocessStatus();
        preprocessUsersRatings();
    }

    private static void preprocessProblems() throws IOException, ParseException {
        JSONObject problems = sc.readObject(dataDirectory + "problems/problems.json");
        acceptedProblemsMap = new HashMap<>();
        JSONArray problemStatistics = (JSONArray) problems.get("problemStatistics");
        for (int i = 0; i < problemStatistics.size(); i++) {
            JSONObject problem = (JSONObject) problemStatistics.get(i);
            String contestId = "" + problem.get("contestId");
            String index = "" + problem.get("index");
            long solvedCount = (long)problem.get("solvedCount");
            AcceptedProblem acceptedProblem = new AcceptedProblem(contestId, index, solvedCount);
            acceptedProblemsMap.put(contestId + index, acceptedProblem);
        }

        JSONArray problemsObject = (JSONArray) problems.get("problems");
        for (int i = 0; i < problemsObject.size(); i++) {
            JSONObject problem = (JSONObject) problemsObject.get(i);
            String contestId = "" + problem.get("contestId");
            String index = "" + problem.get("index");
            long problemPoints = (long) problem.getOrDefault("points", 1000L);
            ArrayList<String> problemTags = (ArrayList<String>) problem.getOrDefault("tags", new ArrayList<String>());
            AcceptedProblem acceptedProblem = acceptedProblemsMap.get(contestId + index);
            acceptedProblem.addTags(problemTags);
            acceptedProblem.setPoints(problemPoints);
        }

        // Create list of problems sorted by problem count
        List<Map.Entry<String, AcceptedProblem>> problemSolvedCountList = new LinkedList<>(acceptedProblemsMap.entrySet());
        Collections.sort(problemSolvedCountList, new Comparator<Object>() {
            @SuppressWarnings("unchecked")
            public int compare(Object o1, Object o2) {
                return ((AcceptedProblem) ((((Map.Entry<String, AcceptedProblem>)o1)).getValue())).compareTo((AcceptedProblem)(((Map.Entry<String, AcceptedProblem>)o2)).getValue());
            }
        });
        dumpToJson(problemSolvedCountList, "problemSolvedCountList");
    }

    public static void preprocessHandles() {
        File file = new File(dataDirectory + "users");
        String[] directories = file.list();
        for (String handle: directories)
            users.add(handle);
    }

    public static void init() {
        sc = new Scanner();
        users = new HashSet<>();
        hacksScore = new ArrayList<>();
        recentRanks = new ArrayList<>();
        contestsPoints = new ArrayList<>();
        contestsPenalties = new ArrayList<>();
        for (int i = 0; i < 6000; i++) {
            hacksScore.add(new HashMap<String, Integer>());
            recentRanks.add(new HashMap<String, Integer>());
            contestsPoints.add(new ArrayList<Integer>());
            contestsPenalties.add(new ArrayList<Integer>());
        }
    }
    /***
     * process all contests to get contestants scores.
     * @throws
     */
    public static void preprocessContests() throws IOException, ParseException {
        // read list of contests file names
        File file = new File(dataDirectory + "contests");
        String[] directories = file.list();

        usedContests = new HashSet<>();
        // process each contest
        for (String contestFile: directories) {
            // read single contest
            JSONObject contest = sc.readObject(dataDirectory + "contests/" + contestFile);
            JSONObject contestObject = (JSONObject) contest.get("contest");
            int contestId = ((Long)contestObject.get("id")).intValue();
            usedContests.add(contestId);
            JSONArray rows = (JSONArray) contest.get("rows");
            // iterate over each row
            for (Object rowObject: rows) {
                // get handle of the current user
                JSONObject row = (JSONObject) rowObject;
                JSONArray members = (JSONArray) ((JSONObject) row.get("party")).get("members");
                String handle = (String) ((JSONObject)members.get(0)).get("handle");
                // team contest or non-existing user
                if (members.size() != 1)
                    continue;
                // assign contest scores
                int successfulHacksScore = ((Long)row.get("successfulHackCount")).intValue() * 100;
                int unsuccessfulHacksScore = ((Long)row.get("unsuccessfulHackCount")).intValue() * 50;
                hacksScore.get(contestId).put(handle, successfulHacksScore - unsuccessfulHacksScore);
                recentRanks.get(contestId).put(handle, ((Long) row.get("rank")).intValue());
                contestsPoints.get(contestId).add(((Long) row.get("points")).intValue());
                contestsPenalties.get(contestId).add(((Long) row.get("penalty")).intValue());
            }
        }
    }

    public  static void preprocessUsersRatings() throws IOException, ParseException {
        TreeMap<Integer, ArrayList<String>> usersRatings = new TreeMap<>();
        for (String handle: users) {
            // read user's rating
            sc.readArray(dataDirectory + "users/" + handle + "/rating.json");
            JSONObject ratingObj = (JSONObject) sc.nextObject();
            int userRating = 1500;
            if(ratingObj != null)
                userRating = ((Long) ratingObj.getOrDefault("rating", 1500L)).intValue();
            if(!usersRatings.containsKey(userRating))
                usersRatings.put(userRating, new ArrayList<String>());
            usersRatings.get(userRating).add(handle);
        }
        dumpToJson(usersRatings, "usersRatings");

    }
    /***
     * construct new contest performance to each user
     */
    public static void preprocessStatus() throws IOException, ParseException {
        for (String handle: users) {
            // read user's submissions
            sc.readArray(dataDirectory + "users/" + handle + "/status.json");
            User user = new User(handle);
            int lastContestId = -1, curContestId = -1, points = 0, penalty = 0;
            ContestPerformance curContest = null;
            HashSet<String> takenProblems = new HashSet<>();
            // Accepted submission for user
            ArrayList<Integer> acceptedSubmissions = new ArrayList<>();
            ArrayList<AcceptedProblem> acceptedProblems = new ArrayList<>();
            HashSet<String> acceptedProblemsSet = new HashSet<>();
            // iterate over each submission
            while (true) {
                JSONObject submission = sc.nextObject();
                if (submission == null) {
                    if (lastContestId != -1) {
                        curContest.setOldUserRank(recentRanks.get(lastContestId).get(handle));
                        curContest.setUserPoints(points + hacksScore.get(lastContestId).get(handle));
                        curContest.setUserPenalty(penalty);
                    }
                    break;
                }
                // filter non-contestants
                String type = (String) ((JSONObject)submission.get("author")).get(("participantType"));
                String verdict = (String) submission.get("verdict");

                String problemId = null;
                if(verdict.equals("OK")) {
                    acceptedSubmissions.add(((Long) submission.get("creationTimeSeconds")).intValue());
                    JSONObject problem = (JSONObject) submission.get("problem");
                    problemId = "" + problem.get("contestId") + problem.get("index");
                    long problemPoints = (long) problem.getOrDefault("points", 1000L);
                    ArrayList<String> tags = (ArrayList<String>) problem.getOrDefault("tags", new ArrayList<String>());
                    if(acceptedProblemsMap.containsKey(problemId)) {
                        acceptedProblemsMap.get(problemId).setPoints(problemPoints);
                        acceptedProblemsMap.get(problemId).addTags(tags);
                    }
                }

                curContestId = ((Long) submission.getOrDefault("contestId", -1L)).intValue();

                if (!type.equals("CONTESTANT") || !usedContests.contains(curContestId))
                    continue;

                JSONObject problem = (JSONObject) submission.get("problem");
                String problemIdx = (String) problem.get("index");
                // start processing new contest
                if (curContestId != lastContestId) {
                    // save updated contest performance
                    if (lastContestId != -1) {
                        curContest.setOldUserRank(recentRanks.get(lastContestId).get(handle));
                        curContest.setUserPoints(points + hacksScore.get(lastContestId).get(handle));
                        curContest.setUserPenalty(penalty);
                    }
                    user.addContest(contestsPoints.get(curContestId), contestsPenalties.get(curContestId), curContestId);
                    curContest = user.getCurrentContest();
                    lastContestId = curContestId;
                    takenProblems.clear();
                    points = 0;
                    penalty = 0;
                }
                // already processed problem
                if (!takenProblems.add(problemIdx))
                    continue;

                // calculate problem's contribution to the score
                int secs = ((Long) submission.get("relativeTimeSeconds")).intValue();
                curContest.addSubmission(secs);
                if (problem.containsKey("points")) {
                    int problemPoints = ((Long) problem.get("points")).intValue();
                    int minutes = secs / 60;
                    points += problemPoints - problemPoints / 250 * minutes;
                }
                else {
                    points++;
                    penalty += secs / 60;
                }

                if (problemId != null &&  acceptedProblemsMap.containsKey(problemId) && !acceptedProblemsSet.contains(problemId)) {
                    acceptedProblemsMap.get(problemId).setCreationTime(acceptedSubmissions.get(acceptedSubmissions.size() - 1));
                    acceptedProblems.add(acceptedProblemsMap.get(problemId));
                    acceptedProblemsSet.add(problemId);
                }
            }


            Collections.sort(acceptedProblems, new Comparator<Object>() {
                @SuppressWarnings("unchecked")
                public int compare(Object o1, Object o2) {
                    return (int) (((AcceptedProblem) o1).getCreationTime() -  ((AcceptedProblem) o2).getCreationTime());
                }
            });
            Collections.sort(acceptedSubmissions);
            Collections.reverse(acceptedSubmissions);

            dumpToJson(user, "users/" + handle);
            dumpToJson(acceptedSubmissions, "acceptedSubmissions/" + handle);
            dumpToJson(acceptedProblems, "acceptedProblems/" + handle);
        }
    }

    public static void dumpToJson(Object object, String path) throws IOException {
        Gson gson = new Gson();
        FileWriter writer =  new FileWriter(objectsDirectory + path + ".json");
        gson.toJson(object, writer);
        writer.close();

    }

}

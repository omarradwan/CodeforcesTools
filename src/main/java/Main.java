import com.google.gson.Gson;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import utils.Scanner;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main {

    private static utils.Scanner sc;
    private static HashSet<String> users;
    private static ArrayList<HashMap<String, Integer>> hacksScore;
    private static ArrayList<HashMap<String, Integer>> recentRanks;
    private static ArrayList<ArrayList<Integer>> contestsPoints;
    private static ArrayList<ArrayList<Integer>> contestsPenalties;
    private static final String dataDirectory = "src/main/resources/data/";
    private static final String objectsDirectory = "objects/";

    public static HashMap<Integer, Integer> evaluateContestPerformance(String handle, boolean plot) {
        return null;
    }

    public static void preprocess() throws IOException, ParseException {
        init();
        preprocessHandles();
        preprocessContests();
        System.err.println(recentRanks.get(579));
        preprocessStatus();
        preprocessUsersRatings();
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
        hacksScore = new ArrayList<>(1000);
        recentRanks = new ArrayList<>();
        contestsPoints = new ArrayList<>(1000);
        contestsPenalties = new ArrayList<>(1000);
        for (int i = 0; i < 1000; i++) {
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

        // process each contest
        for (String contestFile: directories) {
            // read single contest
            JSONObject contest = sc.readObject(dataDirectory + "contests/" + contestFile);
            JSONObject contestObject = (JSONObject) contest.get("contest");
            int contestId = ((Long)contestObject.get("id")).intValue();
            JSONArray rows = (JSONArray) contest.get("rows");
            // iterate over each row
            for (Object rowObject: rows) {
                // get handle of the current user
                JSONObject row = (JSONObject) rowObject;
                JSONArray members = (JSONArray) ((JSONObject) row.get("party")).get("members");
                String handle = (String) ((JSONObject)members.get(0)).get("handle");
                // team contest or non-existing user
                if (members.size() != 1 || !users.contains(handle))
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
            int userRating = ((Long) sc.readObject(dataDirectory + "users/" + handle + "/rating.json").get("rating")).intValue();
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

            // iterate over each submission
            while (true) {
                JSONObject submission = sc.nextObject();
                if (submission == null) {
                    curContest.setOldUserRank(recentRanks.get(lastContestId).get(handle));
                    curContest.setUserPoints(points + hacksScore.get(lastContestId).get(handle));
                    curContest.setUserPenalty(penalty);
                    break;
                }
                // filter non-contestants
                String type = (String) ((JSONObject)submission.get("author")).get(("participantType"));
                String verdict = (String) submission.get("verdict");

                if(verdict.equals("OK"))
                    acceptedSubmissions.add(((Long) submission.get("creationTimeSeconds")).intValue());

                if (!type.equals("CONTESTANT"))
                    continue;


                curContestId = ((Long) submission.get("contestId")).intValue();
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
                    penalty += secs;
                }
            }
            Collections.sort(acceptedSubmissions);
            dumpToJson(user, "users/" + handle);
            Collections.reverse(acceptedSubmissions);
            dumpToJson(acceptedSubmissions, "acceptedSubmissions/" + handle);
        }
    }

    public static void dumpToJson(Object object, String path) throws IOException {
        Gson gson = new Gson();
        System.err.println(gson.toJson(object));
        FileWriter writer =  new FileWriter(objectsDirectory + path + ".json");
        gson.toJson(object, writer);
        writer.close();

    }

    public static void main(String[] args) throws IOException, ParseException {
        preprocess();
    }
}



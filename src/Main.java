import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Main {

    private static Scanner sc;
    private static HashSet<String> users;
    private static ArrayList<HashMap<String, Integer>> hacksScore;
    private static ArrayList<HashMap<String, Integer>> recentRanks;
    private static ArrayList<ArrayList<Integer>> contestsPoints;
    private static ArrayList<ArrayList<Integer>> contestsPenalties;

    HashMap<Integer, Integer> evaluateContestPerformance(String handle, boolean plot) throws JSONException {

    }

    public void preprocessHandles() {
        users = new HashSet<>();
        File file = new File("data/users");
        String[] directories = file.list();
        for (String handle: directories)
            users.add(handle);
    }

    /***
     * process all contests to get contestants scores.
     * @throws JSONException
     */
    public void preprocessContests() throws JSONException {
        // read list of contests file names
        File file = new File("data/contests");
        String[] directories = file.list();
        // initialize scores arrayLists
        hacksScore = new ArrayList<>(1000);
        recentRanks = new ArrayList<>();
        contestsPoints = new ArrayList<>(1000);
        contestsPenalties = new ArrayList<>(1000);
        // process each contest
        for (String contestFile: directories) {
            // read single contest
            JSONObject contest = sc.readObject("data/contests/" + contestFile);
            int contestId = contest.getJSONObject("contest").getInt("id");
            hacksScore.set(contestId, new HashMap<>());
            recentRanks.set(contestId, new HashMap<>());
            contestsPoints.set(contestId, new ArrayList<>());
            JSONArray rows = contest.getJSONArray("rows");
            // iterate over each row
            for (int i = 0; i < rows.length(); i++) {
                // get handle of the current user
                JSONObject row = rows.getJSONObject(i);
                JSONArray members = row.getJSONObject("party").getJSONArray("members");
                String handle = members.getJSONObject(0).getString("handle");
                // team contest or non-existing user
                if (members.length() != 1 || !users.contains(handle))
                    continue;
                // assign contest scores
                int successfulHacksScore = row.getInt("successfulHackCount") * 100;
                int unsuccessfulHacksScore = row.getInt("unsuccessfulHackCount") * 50;
                hacksScore.get(contestId).put(handle, successfulHacksScore - unsuccessfulHacksScore);
                recentRanks.get(contestId).put(handle, row.getInt("rank"));
                contestsPoints.get(contestId).add(row.getInt("points"));
                contestsPenalties.get(contestId).add(row.getInt("penalty"));
            }
        }
    }

    /***
     * construct new contest performance to each user
     * @throws JSONException
     */
    public void preprocessStatus() throws JSONException {
        for (String handle: users) {
            // read user's submissions
            sc.readArray("data/users/" + handle + "/status.json");
            User user = new User(handle);
            int lastContestId = -1, points = 0, penalty = 0;
            ContestPerformance curContest = null;
            HashSet<String> takenProblems = new HashSet<>();
            // iterate over each submission
            while (true) {
                JSONObject submission = sc.nextObject();
                if (submission == null)
                    break;
                // filter non-contestants
                String type = submission.getJSONObject("author").getString(("participantType"));
                if (!type.equals("CONTESTANT"))
                    continue;

                int curContestId = submission.getInt("contestId");
                JSONObject problem = submission.getJSONObject("problem");
                String problemIdx = problem.getString("index");
                // start processing new contest
                if (curContestId != lastContestId) {
                    // save updated contest performance
                    if (curContestId != -1) {
                        curContest.setOldUserRank(recentRanks.get(curContestId).get(handle));
                        curContest.setUserPoints(points + hacksScore.get(curContestId).get(handle));
                        curContest.setUserPenalty(penalty);
                    }
                    user.addContest(contestsPoints.get(curContestId), contestsPenalties.get(curContestId));
                    curContest = user.getCurrentContest();
                    lastContestId = curContestId;
                    takenProblems.clear();
                }
                // already processed problem
                if (!takenProblems.add(problemIdx))
                    continue;

                // calculate problem's contribution to the score
                int secs = submission.getInt("relativeTimeSeconds");
                curContest.addSubmission(secs);
                if (problem.has("points")) {
                    int problemPoints = problem.getInt("points");
                    int minutes = secs / 60;
                    points += problemPoints - problemPoints / 250 * minutes;
                }
                else {
                    points++;
                    penalty += secs;
                }
            }

        }
    }
}



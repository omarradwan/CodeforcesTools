package commands;

import algorithms.BinarySearch;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import utils.Scanner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class EvaluateContestPerformance {

    private static Scanner sc;
    private static final String usersDirectory = "objects/users/";

    public static JSONArray readUserContests(String handle) throws IOException, ParseException {
        sc = new Scanner();
        JSONObject userObject = sc.readObject(usersDirectory + handle + ".json");
        return (JSONArray) userObject.get("contests");
    }

    public static HashMap<Integer, Integer> execute(String handle, boolean plot) throws IOException, ParseException {
        JSONArray contests = readUserContests(handle);
        HashMap<Integer, Integer> result = new HashMap<>();
        for (int i = 0; i < contests.size(); i++) {
            // read contest info
            JSONObject contest = (JSONObject) contests.get(i);
            int contestId = ((Long) contest.get("contestId")).intValue();
            int userPoints = ((Long) contest.get("userPoints")).intValue();
            int userPenalty = ((Long) contest.get("userPenalty")).intValue();
            ArrayList<Long> contestPoints = (ArrayList<Long>) contest.get("contestPoints");
            ArrayList<Long> contestPenalties = (ArrayList<Long>) contest.get("contestPenalties");

            // search for the new rank
            int prevPoint = BinarySearch.reversedLowerBound(contestPoints, userPoints);
            if (prevPoint == -1 || contestPoints.get(prevPoint) != userPoints)
            {
                int newRank = prevPoint == -1? 1 : prevPoint + 1;
                result.put(contestId, newRank);
                continue;
            }
            int nextPoint = BinarySearch.reversedUpperBound(contestPoints, userPoints);
            int bestPenalty = BinarySearch.lowerBound(contestPenalties, userPenalty, prevPoint, nextPoint);
            result.put(contestId, bestPenalty + 1);
        }
        return result;
    }
}

package commands;

import algorithms.BinarySearch;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
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
        double[] oldRanks = new double[contests.size() + 1];
        double[] newRanks = new double[contests.size() + 1];
        double[] contestNumber = new double[contests.size() + 1];
        oldRanks[0] = newRanks[0] = 0;
        for (int i = 0; i < contests.size(); i++) {
            // read contest info
            contestNumber[i + 1] = i + 1;
            JSONObject contest = (JSONObject) contests.get(i);
            double oldRank = ((Long) contest.get("oldUserRank")).doubleValue();
            oldRanks[i + 1] = oldRank;
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
                newRanks[i + 1] = newRank;
                continue;
            }
            int nextPoint = BinarySearch.reversedUpperBound(contestPoints, userPoints);
            int bestPenalty = BinarySearch.lowerBound(contestPenalties, userPenalty, prevPoint, nextPoint);
            result.put(contestId, bestPenalty + 1);
            newRanks[i + 1] = bestPenalty + 1;
        }

        if (plot)
            plot(handle, oldRanks, newRanks, contestNumber);

        return result;
    }

    static void plot(String handle, double[] oldRanks, double[] newRanks, double[] contests) throws IOException {
        final XYChart chart = new XYChartBuilder().width(600).height(400).title("Contest Performance").xAxisTitle("X").yAxisTitle("Y").build();
        chart.addSeries("Old Performance", contests, oldRanks);
        chart.addSeries("New Performance", contests, newRanks);
        new SwingWrapper(chart).displayChart();
        BitmapEncoder.saveBitmapWithDPI(chart, "plots/" + handle, BitmapEncoder.BitmapFormat.PNG, 300);
    }
}

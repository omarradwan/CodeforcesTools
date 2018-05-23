package commands;

import algorithms.SCC;
import algorithms.TopologicalOrdering;
import models.AcceptedProblem;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class SelectProblems {

    private static ArrayList<Integer> adj[];
    private static HashMap<String, Integer> problemsIndx;
    private static ArrayList<String> problemsId;
    private static ArrayList<AcceptedProblem> problems;
    private static int[] deadlockIdx;
    private static ArrayList<Integer> orderedDeadlocks;
    private static utils.Scanner sc;
    private static final String objectsDirectory = "objects/";

    public static void preprocess(String[] handles) throws IOException, ParseException {
        sc.readObject(objectsDirectory+ "problemSolvedCountList.json");


    }

    public static ArrayList<ArrayList<String>> execute(String[] handles, String tag, int minSolved, int maxSolved, int p, int cnt){

        sortDeadlocks();
        return sortProblems();
    }

    private static void sortDeadlocks() {
        // run strongly connected component to group deadlocks
        SCC.setAdjList(adj);
        SCC.tarjanSCC();
        ArrayList<Integer>[] deadlockAdj = SCC.getDeadlockAdj();
        deadlockIdx = SCC.getDeadlockIdx();

        // construct importance and lexicographical order arrays to sort deadlocks
        int deadlocksCount = deadlockAdj.length;
        long[] importanceSum = new long[deadlocksCount];
        long[] importance = new long[deadlocksCount];
        String[] bestProblemId = new String[deadlocksCount];

        for (int i = 0; i < deadlockIdx.length; i++) {
            long curPoints = problems.get(i).getPoints();
            int curDeadlockIdx = deadlockIdx[i];
            String curId = problems.get(i).getId();
            importanceSum[curDeadlockIdx] += curPoints;
            if (curPoints > importance[curDeadlockIdx]) {
                bestProblemId[curDeadlockIdx] = curId;
                importance[curDeadlockIdx] = curPoints;
            }
            else if (curPoints == importance[curDeadlockIdx]) {
                String curBest = bestProblemId[curDeadlockIdx];
                bestProblemId[curDeadlockIdx] = curId.compareTo(curBest) < 0? curId : curBest;
            }
        }

        // run topological ordering
        TopologicalOrdering.setDeadlockAdj(deadlockAdj);
        TopologicalOrdering.setDeadlocksImportance(importanceSum);
        TopologicalOrdering.setDeadlocksProblemId(bestProblemId);
        orderedDeadlocks = TopologicalOrdering.sort();
    }

    private static ArrayList<ArrayList<String>> sortProblems() {

        ArrayList<AcceptedProblem>[] deadlockProblems = new ArrayList[deadlockIdx.length];
        for (int i = 0; i < deadlockProblems.length; i++)
            deadlockProblems[i] = new ArrayList<>();

        for (int i = 0; i < deadlockIdx.length; i++)
            deadlockProblems[deadlockIdx[i]].add(problems.get(i));

        for (ArrayList<AcceptedProblem> problems: deadlockProblems)
            Collections.sort(problems);

        ArrayList<ArrayList<String>> result = new ArrayList<>();

        for (int i = 0; i < orderedDeadlocks.size(); i++) {
            int curIdx = orderedDeadlocks.get(i);
            result.add(new ArrayList<>());
            for (AcceptedProblem problem: deadlockProblems[curIdx])
                result.get(i).add(problem.getId());
        }

        return result;
    }
}

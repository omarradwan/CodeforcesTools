package commands;

import algorithms.SCC;
import algorithms.TopologicalOrdering;
import models.AcceptedProblem;
import models.AcceptedProblem;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import utils.Scanner;

import java.io.IOException;
import java.util.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class SelectProblems {

    private static ArrayList<Integer> adj[];
    private static  int edgeCount[][];
    private static HashMap<String, Integer> problemsIndx;
    private static ArrayList<AcceptedProblem> problems;
    private static ArrayList<String> problemsId;
    private static int[] deadlockIdx;
    private static ArrayList<Integer> orderedDeadlocks;
    private static utils.Scanner sc;
    private static final String dataDirectory = "objects/";





    public static void preprocess(String[] handles, String tag, int minSolved, int maxSolved, int p) throws IOException, ParseException {
        sc = new Scanner();
        problems = new ArrayList<>();
        problemsIndx = new HashMap<>();

        sc.readArray(dataDirectory + "problemSolvedCountList.json");
        while (true){
            JSONObject problemJson = sc.nextObject();
            if(problemJson == null) break;
            problemJson = (JSONObject) problemJson.getOrDefault ("value", null);
            String problemId = (String) problemJson.get("id");
            long problemCount = (long) problemJson.get("solvedCount");
            long problemPoints = (long) problemJson.get("points");
            ArrayList<String> problemTags = (ArrayList<String>) problemJson.getOrDefault("tags", new ArrayList<String>());
            AcceptedProblem problem = new AcceptedProblem(problemId, problemCount);
            problem.setPoints(problemPoints);
            problem.addTags(problemTags);

            if(problemCount > maxSolved && (tag != null && !problemTags.contains(tag))) continue;
            if(problemCount < minSolved) break;

            problemsIndx.put(problem.getId(), problems.size());
            problems.add(problem);
        }
        int problemsSize = problems.size();
        edgeCount = new int[problemsSize][problemsSize];
        adj = new ArrayList[problemsSize];
        for(int i = 0; i < handles.length; i++){
            sc.readObject(dataDirectory + "acceptedProblems/" + handles + ".json");
            ArrayList<String> userProblems = new ArrayList<>();
            HashSet<String> userProplemsSet = new HashSet<>();
            while (true){
                JSONObject problem = sc.nextObject();
                if(problem == null) break;
              String  acceptedProblemId = (String) problem.get("id");
                userProblems.add(acceptedProblemId);
                userProplemsSet.add(acceptedProblemId);
            }
            for (int j = 0; j < userProblems.size(); j++)
                for (int k = 0; k < j; k++)
                    edgeCount[problemsIndx.get(userProblems.get(j))][problemsIndx.get(userProblems.get(k))]++;
            for(int j = 0; j < userProblems.size(); j++){
                for (int k = 0; k < problems.size(); k++){
                    String id = problems.get(k).getId();
                    if(!userProplemsSet.contains(id))
                        edgeCount[problemsIndx.get(userProblems.get(j))][problemsIndx.get(id)]++;
                }
            }
        }

        int percent = (int) (handles.length * ((p * 1.0) / 100));
        for (int i = 0; i < adj.length; i++) {
            adj[i] = new ArrayList<>();
            for(int j = 0; j < adj.length; j++){
                if(i == j) continue;
                if(edgeCount[i][j] >= percent)
                    adj[i].add(j);
            }
        }
    }

    public static ArrayList<ArrayList<String>> execute(String[] handles, String tag, int minSolved, int maxSolved, int p, int cnt){
        preprocess(handles, tag, minSolved, maxSolved, p);
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

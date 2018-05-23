package commands;

import models.AcceptedProblem;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import utils.Scanner;

import java.io.IOException;
import java.util.*;

public class SelectProblems {

    private static ArrayList<Integer> adj[];
    private static  int edgeCount[][];
    private static HashMap<String, Integer> problemsIndx;
    private static ArrayList<AcceptedProblem> problems;
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


    public static ArrayList<ArrayList<String>> excute(String[] handles, String tag, int minSolved, int maxSolved, int p, int cnt) throws IOException, ParseException {
        preprocess(handles, tag, minSolved, maxSolved,p);

        return null;
    }
}

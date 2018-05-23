package commands;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class SelectProblems {

    ArrayList<Integer> adj[];
    HashMap<String, Integer> problemsIndx;
    ArrayList<String> problemsId;
//    ArrayList<String> problemsId;
    private static utils.Scanner sc;
    private static final String objectsDirectory = "objects/";






    public static void preprocess(String[] handles) throws IOException, ParseException {
        sc.readObject(objectsDirectory+ "problemSolvedCountList.json");


    }


    public static ArrayList<ArrayList<String>> excute(String[] handles, String tag, int minSolved, int maxSolved, int p, int cnt){
        return null;
    }
}

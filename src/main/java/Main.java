import commands.EvaluateContestPerformance;
import commands.GetActiveUsers;
import commands.Seed;
import commands.SelectProblems;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static void seed() throws IOException, ParseException {
        Seed.execute();
    }

    public static HashMap<Integer, Integer> evaluateContestPerformance(String handle, boolean plot) throws IOException, ParseException {
        return EvaluateContestPerformance.execute(handle, plot);
    }

    public static ArrayList<String> getActiveUsers(int t1, int t2, int rLo, int rHi, int cnt) throws IOException, ParseException {
        return GetActiveUsers.execute(t1, t2, rLo, rHi, cnt);
    }

    public static ArrayList<ArrayList<String>> selectProblems(String[] handles, String tag, int minSolved, int maxSolved, int p, int cnt) throws IOException, ParseException {
        return SelectProblems.execute(handles, tag, minSolved, maxSolved, p, cnt);
    }

    public static void main(String[] args) throws IOException, ParseException {
        seed();
        System.out.println("Contest Performance: \n");

        System.out.println("KAMMOLA");
        System.out.println(evaluateContestPerformance("Kammola", true) + "\n");

        System.out.println("SOLIMAN");
        System.out.println(evaluateContestPerformance("AhmedSoliman", true) + "\n");

        System.out.println("MOSTAFA");
        System.out.println(evaluateContestPerformance("MostafaAbdullah", true) + "\n");

        System.out.println("SAGHEER");
        System.out.println(evaluateContestPerformance("Ahmad_Elsagheer", true) + "\n");

        System.out.println("\n\n\nActive Users: \n");


        System.out.println(getActiveUsers(0, 2000000000, 0, 3000, 30) + "\n");
        System.out.println(getActiveUsers(1000000000, 2000000000, 2000, 3500, 70) + "\n");
        System.out.println(getActiveUsers(1400000000, 1500000000, 0, 3000, 30) + "\n");
        System.out.println(getActiveUsers(0, 2000000000, 0, 2000, 30) + "\n");
        System.out.println(getActiveUsers(0, 2000000000, 1000, 2000, 30) + "\n");

        System.out.println("\n\n\nSelect Problems: \n");


        System.out.println(selectProblems(new String[] {"Kammola", "MeshOmarYasser"}, null, 1000, 10000, 30, 100) + "\n");
        System.out.println(selectProblems(new String[] {"Ahmad_Elsagheer", "AhmedSoliman", "MostafaAbdullah", "tourist"}, "math", 1000, 4000, 2, 36) + "\n");
        System.out.println(selectProblems(new String[] {"tourist", "Petr", "KrK", "uwi", "dreamoon"}, "math", 1000, 10000, 30, 100) + "\n");

    }
}



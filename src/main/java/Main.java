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
//        System.out.println(evaluateContestPerformance("OmarRadwan", true));
//        System.out.println(selectProblems(null, null, 0, 0, 0, 4));

    }
}



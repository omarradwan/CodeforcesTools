import commands.EvaluateContestPerformance;
import commands.GetActiveUsers;
import commands.Seed;
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



    public static void main(String[] args) throws IOException, ParseException {

    }
}



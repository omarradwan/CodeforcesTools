import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import utils.Scanner;

import java.io.IOException;
import java.util.*;

public class UserActivity {

    private static TreeMap<Integer, ArrayList<String>> usersRatings;
    private static utils.Scanner sc;
    private static final String objectsDirectory = "objects/";



    public static void processUsersRatings() throws IOException, ParseException {
        sc = new Scanner();
        usersRatings = new TreeMap<>();
       JSONObject usersRatingsJSON =  sc.readObject(objectsDirectory + "usersRatings.json");
       for (Object entry: usersRatingsJSON.entrySet()){
           Map.Entry<String, ArrayList<String>> castedEntry = ((Map.Entry<String, ArrayList<String>>) entry);
           usersRatings.put(Integer.parseInt(castedEntry.getKey()), castedEntry.getValue());
       }

    }


    public static ArrayList<String> getActiveUsers(int t1, int t2, int rLo, int rHi, int cnt) throws IOException, ParseException {
        ArrayList<Pair> usersActivity = new ArrayList<>();
        for(int i = rLo; i <= rHi; i++){
            if(!usersRatings.containsKey(i)) continue;
            ArrayList<String> users = usersRatings.get(i);
            for(String user: users) {
                sc.readArray(objectsDirectory + "acceptedSubmissions/" + user + ".json");
                ArrayList<Integer> submissions = new ArrayList<>();
                while (true) {
                    Object submission = sc.nextObjectT();
                    if (submission == null) break;
                    submissions.add(Integer.parseInt(submission.toString()));
                }
                System.out.println(submissions);
                int idxL = BS(submissions, t1 - 1);
                int idxR = BS(submissions, t2);
                int activity = idxR - idxL;
                usersActivity.add(new Pair(user, activity));
            }
        }
        Collections.sort(usersActivity);
        ArrayList<String> activeUsers = new ArrayList<>();
        for (int i = 0; i < cnt && i < usersActivity.size(); i++ )
            activeUsers.add(usersActivity.get(i).handle);
        return activeUsers;
    }
    public static int BS(ArrayList<Integer> array, int pivot){
        int lo = 0, hi = array.size() - 1;
        while (lo <= hi){
            int mid = (lo + hi)/2;
            if(array.get(mid) <= pivot)
                lo = mid + 1;
            else
                hi = mid - 1;

        }
        return lo;
    }

    static class Pair implements Comparable {
        String handle;
        int activity;

        public Pair(String handle, int activity){
            this.handle = handle;
            this.activity = activity;
        }


        @Override
        public int compareTo(Object o) {
            return (activity - ((Pair) o).activity);
        }
    }
}

import java.util.ArrayList;

public class User {

    private String handle;
    private ArrayList<ContestPerformance> contests;

    public User(String handle) {
        this.handle = handle;
        this.contests = new ArrayList<>();
    }

    public void addContest(ArrayList<Integer> contestPoints, ArrayList<Integer> contestPenalties, int contestId) {
        contests.add(new ContestPerformance(contestPoints, contestPenalties, contestId));
    }

    public ContestPerformance getCurrentContest() {
        return this.contests.get(this.contests.size() - 1);
    }
}

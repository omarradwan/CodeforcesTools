import java.util.ArrayList;

public class ContestPerformance {

    private int oldUserRank;
    private int userPoints;
    private int userPenalty;
    private ArrayList<Integer> contestPoints;
    private ArrayList<Integer> contestPenalties;
    private ArrayList<Integer> submissions;

    public ContestPerformance(ArrayList<Integer> contestPoints, ArrayList<Integer> contestPenalties) {
        submissions = new ArrayList<>();
        this.contestPoints = contestPoints;
        this.contestPenalties = contestPenalties;
    }

    public void setOldUserRank(int oldUserRank) {
        this.oldUserRank = oldUserRank;
    }

    public void addSubmission(int time) {
        this.submissions.add(time);
    }

    public void setUserPoints(int userPoints) {
        this.userPoints = userPoints;
    }

    public void setUserPenalty(int userPenalty) {
        this.userPenalty = userPenalty;
    }
}

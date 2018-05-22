package models;

import java.util.ArrayList;
import java.util.HashSet;

public class AcceptedProblem {

    String id;
    long solvedCount;
    long points;
    long creationTime;
    HashSet<String> tags;

    public AcceptedProblem(String contestId, String index, long solvedCount) {
        id = contestId + index;
        this.solvedCount = solvedCount;
        tags = new HashSet<>();
    }

    public void addTags(ArrayList<String> tags) {
        this.tags.addAll(tags);
    }

    public void setPoints(long points) {
        this.points = points;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }
}

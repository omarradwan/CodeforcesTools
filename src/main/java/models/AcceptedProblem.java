package models;

import java.util.ArrayList;
import java.util.HashSet;

public class AcceptedProblem  implements Comparable{

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

    public AcceptedProblem(String id, long solvedCount) {
        this.id = id;
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

    public String getId() {
        return id;
    }

    public long getSolvedCount() {
        return solvedCount;
    }

    public long getPoints() {
        return points;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public HashSet<String> getTags() {
        return tags;
    }

    @Override
    public int compareTo(Object o) {
        return (int) (solvedCount - ((AcceptedProblem) o).solvedCount);
    }
}

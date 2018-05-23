package algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;

public class TopologicalOrdering {

    private static int N;
    private static ArrayList<Integer>[] DAG;
    private static int[] deadlocksImportance;
    private static String[] deadlocksProblemId;
    private static Deadlock[] deadlocks;

    private static void init() {
        N = DAG.length;
        deadlocks = new Deadlock[N];
        for (int i = 0; i < N; i++)
            deadlocks[i] = new Deadlock(i, deadlocksImportance[i], deadlocksProblemId[i]);
    }

    public static ArrayList<Integer> sort() {
        init();
        PriorityQueue<Deadlock> pq = new PriorityQueue<>();
        int[] p = new int[N];
        for (int u = 0; u < N; u++)
            for (int v: DAG[u])
                p[v]++;

        System.out.println(Arrays.toString(p));
        ArrayList<Integer> sortedDeadlocks = new ArrayList<>();
        for (int i = 0; i < N; i++)
            if (p[i] == 0)
                pq.add(deadlocks[i]);

        while (!pq.isEmpty()) {
            Deadlock cur = pq.remove();
            sortedDeadlocks.add(cur.deadlockId);
            for (int v: DAG[cur.deadlockId])
                if (--p[v] == 0)
                    pq.add(deadlocks[v]);
        }

        return sortedDeadlocks;
    }

    public static void setDAG(ArrayList<Integer>[] DAG) {
        TopologicalOrdering.DAG = DAG;
    }

    public static void setDeadlocksImportance(int[] deadlocksImportance) {
        TopologicalOrdering.deadlocksImportance = deadlocksImportance;
    }

    public static void setDeadlocksProblemId(String[] deadlocksProblemId) {
        TopologicalOrdering.deadlocksProblemId = deadlocksProblemId;
    }

    static class Deadlock implements Comparable<Deadlock> {

        int deadlockId;
        int deadlockImportance;
        String deadlockProblemId;

        Deadlock(int deadlockId, int deadlockImportance, String deadlockProblemId) {
            this.deadlockId = deadlockId;
            this.deadlockImportance = deadlockImportance;
            this.deadlockProblemId = deadlockProblemId;
        }

        @Override
        public int compareTo(Deadlock o) {
            if (deadlockImportance != o.deadlockImportance)
                return o.deadlockImportance - deadlockImportance;
            return deadlockProblemId.compareTo(o.deadlockProblemId);
        }
    }
}

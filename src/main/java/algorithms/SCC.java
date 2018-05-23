package algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Stack;

public class SCC {

    private static ArrayList<Integer>[] adjList, DAG;
    private static int N, counter, SCCId, dfs_num[], dfs_low[];
    private static int[] dagIdx;
    private static Stack<Integer> stack;

    private static void init() {
        N = adjList.length;
        dfs_num = new int[N];
        dfs_low = new int[N];
        dagIdx = new int[N];
        Arrays.fill(dagIdx, -1);
        stack = new Stack<>();
        counter = 0;
        SCCId = 0;
    }

    public static void tarjanSCC()
    {
        init();

        for(int i = 0; i < N; ++i)
            if(dfs_num[i] == 0)
                tarjanSCC(i);

        compress();
    }

    static void tarjanSCC(int u)
    {
        dfs_num[u] = dfs_low[u] = ++counter;
        stack.push(u);

        for(int v: adjList[u])
        {
            if(dfs_num[v] == 0)
                tarjanSCC(v);
            if(dagIdx[v] == -1)
                dfs_low[u] = Math.min(dfs_low[u], dfs_low[v]);
        }
        if(dfs_num[u] == dfs_low[u])
        {
            while(true)
            {
                int v = stack.pop();
                dagIdx[v] = SCCId;
                if(v == u)
                    break;
            }
            SCCId++;
        }
    }

    private static void compress() {
        DAG = new ArrayList[SCCId];
        for (int i = 0; i < SCCId; i++)
            DAG[i] = new ArrayList<>();

        HashSet<Integer>[] adjSet = new HashSet[SCCId];
        for (int i = 0; i < SCCId; i++)
            adjSet[i] = new HashSet<>();

        for (int u = 0; u < N; u++)
            for (int v: adjList[u])
                if (dagIdx[u] != dagIdx[v] && !adjSet[dagIdx[u]].contains(dagIdx[v])) {
                    DAG[dagIdx[u]].add(dagIdx[v]);
                    adjSet[dagIdx[u]].add(dagIdx[v]);
                }
    }

    public static void setAdjList(ArrayList<Integer>[] adjList) {
        SCC.adjList = adjList;
    }

    public static ArrayList<Integer>[] getDeadlockAdj() {
        return DAG;
    }

    public static int[] getDeadlockIdx() {
        return dagIdx;
    }
}
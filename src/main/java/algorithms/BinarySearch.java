package algorithms;

import java.util.ArrayList;

public class BinarySearch {

    public static int upperBound(ArrayList<Integer> array, int pivot){
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

    public static int lowerBound(ArrayList<Long> array, int pivot, int lo, int hi){
        int ans = -1;
        while (lo <= hi){
            int mid = lo + hi >> 1;
            if(array.get(mid) >= pivot)
                hi = (ans = mid) - 1;
            else
                lo = mid + 1;
        }
        return ans;
    }

    public static int reversedLowerBound(ArrayList<Long> array, int pivot){
        int lo = 0, hi = array.size() - 1, ans = -1;
        while (lo <= hi){
            int mid = lo + hi >> 1;
            if (array.get(mid) <= pivot)
                hi = (ans = mid) - 1;
            else
                lo = mid + 1;
        }
        return ans;
    }

    public static int reversedUpperBound(ArrayList<Long> array, int pivot){
        int lo = 0, hi = array.size() - 1, ans = -1;
        while (lo <= hi){
            int mid = lo + hi >> 1;
            if (array.get(mid) >= pivot)
                lo = (ans = mid) + 1;
            else
                hi = mid - 1;
        }
        return ans;
    }
}

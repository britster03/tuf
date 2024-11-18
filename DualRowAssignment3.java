import java.util.*;

public class DualRowAssignment3 {
    static class Element {
        int x, y, len;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int row0_x = sc.nextInt(), row0_y = sc.nextInt();
        int row1_x = sc.nextInt(), row1_y = sc.nextInt();
        int n = sc.nextInt();
        Element[] elems = new Element[n];
        int[] prefixSum = new int[n + 1];
        for(int i = 0; i < n; i++) {
            elems[i] = new Element();
            elems[i].x = sc.nextInt();
            elems[i].y = sc.nextInt();
            elems[i].len = sc.nextInt();
            prefixSum[i + 1] = prefixSum[i] + elems[i].len;
        }
        sc.close();

        int target = prefixSum[n] / 2;
        long[][] dp = new long[n + 1][target + 1];
        for(int i = 0; i <= n; i++) Arrays.fill(dp[i], Long.MAX_VALUE);
        dp[0][0] = 0;
        int[][] choice = new int[n + 1][target + 1];

        for(int i = 0; i < n; i++) {
            for(int j = 0; j <= target; j++) {
                if(dp[i][j] == Long.MAX_VALUE) continue;
                // Assign to Row 0
                if(j + elems[i].len <= target){
                    long deltaX0 = row0_x + j - elems[i].x;
                    long deltaY0 = row0_y - elems[i].y;
                    long newCost0 = dp[i][j] + (deltaX0 * deltaX0) + (deltaY0 * deltaY0);
                    if(newCost0 < dp[i + 1][j + elems[i].len]){
                        dp[i + 1][j + elems[i].len] = newCost0;
                        choice[i + 1][j + elems[i].len] = 0;
                    }
                }
                // Assign to Row 1
                int row1_len_before = prefixSum[i] - j;
                if(row1_len_before + elems[i].len <= target){
                    long deltaX1 = row1_x + row1_len_before - elems[i].x;
                    long deltaY1 = row1_y - elems[i].y;
                    long newCost1 = dp[i][j] + (deltaX1 * deltaX1) + (deltaY1 * deltaY1);
                    if(newCost1 < dp[i + 1][j]){
                        dp[i + 1][j] = newCost1;
                        choice[i + 1][j] = 1;
                    }
                }
            }
        }

        long cost = dp[n][target];
        List<Integer> r0 = new ArrayList<>(), r1 = new ArrayList<>();
        int j = target;
        for(int i = n; i > 0; i--){
            if(choice[i][j] == 0){
                r0.add(0, i); // 1-based indexing
                j -= elems[i - 1].len;
            }
            else{
                r1.add(0, i); // 1-based indexing
            }
        }

        // Print total cost
        System.out.println(cost);
        // Print Row 1 assignments (1-based indexing)
        for(int num : r1) System.out.print(num + " ");
        System.out.println();
        // Print Row 0 assignments (1-based indexing)
        for(int num : r0) System.out.print(num + " ");
    }
}

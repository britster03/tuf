/*
 * 
 * Title :- Dual Row Dyanmic Programming Implementation.
 * 
 * Name : Ronit Chandresh Virwani (B01099810)
 * Date: 2024-11-12
 */

import java.util.*;

public class DualRow {
    // creating a inner class 'Element' to represent the properties of circuit
    // blocks
    static class Element {
        int x; // the actual X-coordinate of the block
        int y; // the actual Y-coordinate of the block
        int len; // length for each block
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // reading the starting coordinates for row 0 and row 1
        int row0_x = sc.nextInt(); 
        int row0_y = sc.nextInt();

        int row1_x = sc.nextInt();
        int row1_y = sc.nextInt();

        // n is number of blocks
        int n = sc.nextInt();

        // initializing an array to store all the blocks
        Element[] elems = new Element[n];

        // here prefixSum[i] will store the total length of the first 'i' blocks
        int[] prefixSum = new int[n + 1];
        for (int i = 0; i < n; i++) {
            elems[i] = new Element();
            elems[i].x = sc.nextInt(); // reading the actual X-coordinate
            elems[i].y = sc.nextInt(); // reading the actual Y-coordinate
            elems[i].len = sc.nextInt(); // reading the actual length of the block
            prefixSum[i + 1] = prefixSum[i] + elems[i].len; // updating the prefix sum
        }
        sc.close();

        // calculating the total length and determining the target length per row
        int totalLength = prefixSum[n];
        if (totalLength % 2 != 0) {
            // in this case where the total length is odd, it's impossible to divide into
            // two equal rows
            System.out.println("Impossible to divide blocks into two rows with equal lengths");
            return;
        }
        int target = totalLength / 2;

        // initializing a DP table where dp[i][j] represent the minimum cost
        // to assign the first 'i' blocks with a total length 'j' in Row 0
        long[][] dp = new long[n + 1][target + 1];
        for (int i = 0; i <= n; i++) {
            Arrays.fill(dp[i], Long.MAX_VALUE); // in beginning assigning all costs to infinity
        }
        dp[0][0] = 0; // base case : no blocks assigned, zero cost

        // choice[i][j] will store the choice made at state (i, j)
        // 0 indicates assigning the i-th block to Row 0
        // 1 indicates assigning the i-th block to Row 1
        int[][] choice = new int[n + 1][target + 1];

        // assigning blocks
        for (int i = 0; i < n; i++) {
            for (int j = 0; j <= target; j++) {
                // if the current state is unreachable, skip it
                if (dp[i][j] == Long.MAX_VALUE)
                    continue;

                // cs 1 : assigining the current block to Row 0
                if (j + elems[i].len <= target) {
                    // the displacement in X and Y for Row 0
                    long deltaX0 = row0_x + j - elems[i].x;
                    long deltaY0 = row0_y - elems[i].y;

                    // the new cost if this block is assigned to Row 0
                    long newCost = dp[i][j] + (deltaX0 * deltaX0) + (deltaY0 * deltaY0);
                    if (newCost < dp[i + 1][j + elems[i].len]
                            || (newCost == dp[i + 1][j + elems[i].len] && choice[i + 1][j + elems[i].len] != 0)) {
                        dp[i + 1][j + elems[i].len] = newCost;
                        choice[i + 1][j + elems[i].len] = 0;
                    }
                }

                // cs 2 : assigning the current block to Row 1 first
                // calculate the total length already assigned to Row 1 before this block
                int row1_len_before = prefixSum[i] - j;

                // checking if assigning to Row 1 keeps the total length within target
                if (row1_len_before + elems[i].len <= target) {
                    // the displacement in X and Y for Row 1
                    long deltaX1 = row1_x + row1_len_before - elems[i].x;
                    long deltaY1 = row1_y - elems[i].y;

                    // the new cost if this block is assigned to Row 1
                    long newCost = dp[i][j] + (deltaX1 * deltaX1) + (deltaY1 * deltaY1);
                    if (newCost < dp[i + 1][j] || (newCost == dp[i + 1][j] && choice[i + 1][j] != 1)) {
                        dp[i + 1][j] = newCost;
                        choice[i + 1][j] = 1;
                    }
                }
            }
        }

        // minimal total cost is stored at dp[n][target]
        long cost = dp[n][target];

        // checking if a valid assignment exists
        if (cost == Long.MAX_VALUE) {
            // if no valid assignment is found, it's impossible to divide blocks as required
            System.out.println("Impossible to divide blocks into two rows with equal lengths.");
            return;
        }

        // lists to store the assignments for Row 0 and Row 1
        List<Integer> r0 = new ArrayList<>(); // blocks which will be assigned to Row 0
        List<Integer> r1 = new ArrayList<>(); // blocks which will be assigned to Row 1

        int j = target; // starting the backtracking from the target length

        for (int i = n; i > 0; i--) {
            if (choice[i][j] == 0) {
                r0.add(0, i - 1);
                j -= elems[i - 1].len; // moving to the previous state by reducing the length
            } else {
                r1.add(0, i - 1);
                // there will be no change to 'j' since Row 1's length doesn't affect 'j'
            }
        }

        // output will show the total minimal cost
        System.out.println(cost);
        // printing the blocks assigned to Row 0
        for (int num : r0)
            System.out.print(num + " ");
        System.out.println();
        // printing the blocks assigned to Row 1
        for (int num : r1)
            System.out.print(num + " ");
    }
}

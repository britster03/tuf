import java.util.*;
import java.io.*;

public class SATVerifier2 {
    public static void main(String[] args) throws IOException {
        // Initialize Scanner to read from standard input (sr3.txt)
        Scanner sc = new Scanner(System.in);

        // Read number of variables and clauses
        int numVars = sc.nextInt();
        int numClauses = sc.nextInt();

        // Read all clauses
        List<List<Integer>> clauses = new ArrayList<>();
        for(int i = 0; i < numClauses; i++) {
            List<Integer> clause = new ArrayList<>();
            for(int j = 0; j < 3; j++) {
                clause.add(sc.nextInt());
            }
            clauses.add(clause);
        }

        // Initialize assignments array: index 1..numVars, 0 unused
        // true represents true, false represents false
        boolean[] assignments = new boolean[numVars + 1]; // 1-based indexing

        // Manually set assignments based on SATSolver output
        // Replace the following assignments with your actual SATSolver output
        assignments[1] = true;
        assignments[2] = true;
        assignments[3] = true;
        assignments[4] = false;
        assignments[5] = true;
        assignments[6] = true;
        assignments[7] = true;
        assignments[8] = true;
        assignments[9] = true;
        assignments[10] = true;
        assignments[11] = true;
        assignments[12] = false;
        assignments[13] = false;
        assignments[14] = false;
        assignments[15] = false;
        assignments[16] = true;
        assignments[17] = true;
        assignments[18] = true;
        assignments[19] = true;
        assignments[20] = false;
        assignments[21] = true;
        assignments[22] = false;
        assignments[23] = true;
        assignments[24] = true;
        assignments[25] = true;
        assignments[26] = true;
        assignments[27] = false;
        assignments[28] = false;
        assignments[29] = true;
        assignments[30] = false;
        assignments[31] = false;
        assignments[32] = false;
        assignments[33] = false;
        assignments[34] = true;
        assignments[35] = false;
        assignments[36] = false;
        assignments[37] = false;
        assignments[38] = false;
        assignments[39] = true;
        assignments[40] = false;
        assignments[41] = false;
        assignments[42] = false;
        assignments[43] = true;
        assignments[44] = false;
        assignments[45] = true;
        assignments[46] = true;
        assignments[47] = true;
        assignments[48] = false;
        assignments[49] = false;
        assignments[50] = true;

        // Verify each clause
        boolean allSatisfied = true;
        int clauseNumber = 1;
        for(List<Integer> clause : clauses) {
            boolean clauseSatisfied = false;
            for(Integer literal : clause) {
                int var = Math.abs(literal);
                boolean value = assignments[var];
                if(literal > 0 && value) {
                    clauseSatisfied = true;
                    break;
                } else if(literal < 0 && !value) {
                    clauseSatisfied = true;
                    break;
                }
            }
            if(!clauseSatisfied) {
                System.out.println("Clause " + clauseNumber + " is NOT satisfied.");
                allSatisfied = false;
            }
            clauseNumber++;
        }

        if(allSatisfied) {
            System.out.println("All clauses are satisfied by the assignments.");
        } else {
            System.out.println("Some clauses are NOT satisfied by the assignments.");
        }
    }
}

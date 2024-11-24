import java.util.*;
import java.io.*;

public class SATVerifier {
    public static void main(String[] args) throws IOException {
        // Initialize scanner to read from standard input
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

        // Read variable assignments from solver's output
        // Assuming assignments are provided in the same order as variables
        // and are read from a separate file or input stream.
        // For demonstration, we'll manually define the assignments based on your output.
        // In practice, you might read them from a file or another input source.

        // Example: Assignments based on your provided output
        boolean[] assignments = new boolean[numVars + 1]; // 1-based indexing
        assignments[1] = true;
        assignments[2] = true;
        assignments[3] = true;
        assignments[4] = true;
        assignments[5] = true;
        assignments[6] = true;
        assignments[7] = false;
        assignments[8] = true;
        assignments[9] = true;
        assignments[10] = true;
        assignments[11] = true;
        assignments[12] = false;
        assignments[13] = true;
        assignments[14] = true;
        assignments[15] = true;
        assignments[16] = true;
        assignments[17] = true;
        assignments[18] = true;
        assignments[19] = true;
        assignments[20] = false;
        assignments[21] = true;
        assignments[22] = true;
        assignments[23] = true;
        assignments[24] = true;
        assignments[25] = false;
        assignments[26] = true;
        assignments[27] = false;
        assignments[28] = true;
        assignments[29] = true;
        assignments[30] = true;
        assignments[31] = false;
        assignments[32] = false;
        assignments[33] = true;
        assignments[34] = false;
        assignments[35] = false;
        assignments[36] = true;
        assignments[37] = true;
        assignments[38] = false;
        assignments[39] = true;
        assignments[40] = true;
        assignments[41] = false;
        assignments[42] = false;
        assignments[43] = false;
        assignments[44] = true;
        assignments[45] = false;
        assignments[46] = false;
        assignments[47] = true;
        assignments[48] = false;
        assignments[49] = false;
        assignments[50] = false;
        assignments[51] = true;
        assignments[52] = false;
        assignments[53] = false;
        assignments[54] = false;
        assignments[55] = true;
        assignments[56] = false;
        assignments[57] = false;
        assignments[58] = true;
        assignments[59] = true;
        assignments[60] = true;
        assignments[61] = false;
        assignments[62] = true;
        assignments[63] = true;
        assignments[64] = false;
        assignments[65] = true;
        assignments[66] = false;
        assignments[67] = false;
        assignments[68] = false;
        assignments[69] = true;
        assignments[70] = true;
        assignments[71] = true;
        assignments[72] = false;
        assignments[73] = true;
        assignments[74] = false;
        assignments[75] = true;
        assignments[76] = false;
        assignments[77] = true;
        assignments[78] = true;
        assignments[79] = false;
        assignments[80] = true;

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


import java.util.*;
import java.io.*;

public class SATSolver {
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        
        // read number of variables and clauses
        int numVars = sc.nextInt();
        int numClauses = sc.nextInt();
        
        // read clauses
        List<List<Integer>> clauses = new ArrayList<>();
        for(int i = 0; i < numClauses; i++) {
            List<Integer> clause = new ArrayList<>();
            for(int j = 0; j < 3; j++) {
                clause.add(sc.nextInt());
            }
            clauses.add(clause);
        }
        
        // initialize assignments array: index 1..numVars, 0 unused
        // 1 = true, -1 = false, 0 = unassigned
        int[] assignments = new int[numVars + 1];
        
        // attempt to solve using DPLL
        boolean satisfiable = dpll(clauses, assignments, numVars);
        
        if(satisfiable) {
            // if satisfiable, print assignments
            System.out.println("Satisfied");
            for(int i = 1; i <= numVars; i++) {
                if(assignments[i] == 1) {
                    System.out.println("v" + i + " = true");
                }
                else {
                    System.out.println("v" + i + " = false");
                }
            }
        }
        else {
            // if not satisfiable, print Unsatisfiable
            System.out.println("Unsatisfiable.");
        }
    }
    
    // dpll recursive algorithm
    private static boolean dpll(List<List<Integer>> clauses, int[] assignments, int numVars) {
        // simplify clauses based on current assignments
        List<List<Integer>> simplified = simplifyClauses(clauses, assignments);
        
        // if all clauses are satisfied
        if(simplified.isEmpty()) {
            return true;
        }
        
        // if any clause is empty, it's unsatisfiable
        for(List<Integer> clause : simplified) {
            if(clause.isEmpty()) {
                return false;
            }
        }
        
        // pure literal elimination
        Integer pureLiteral = findPureLiteral(simplified, numVars);
        if(pureLiteral != null) {
            int var = Math.abs(pureLiteral);
            int value = pureLiteral > 0 ? 1 : -1;
            assignments[var] = value;
            return dpll(simplified, assignments, numVars);
        }
        
        // unit clause elimination
        Integer unitLiteral = findUnitClause(simplified);
        if(unitLiteral != null) {
            int var = Math.abs(unitLiteral);
            int value = unitLiteral > 0 ? 1 : -1;
            assignments[var] = value;
            return dpll(simplified, assignments, numVars);
        }
        
        // choose the next variable to assign (lowest index first)
        int varToAssign = chooseVariable(simplified, assignments, numVars);
        if(varToAssign == -1) {
            // no variable left to assign but clauses not satisfied
            return false;
        }
        
        // try assigning true first
        int[] copyAssignTrue = assignments.clone();
        copyAssignTrue[varToAssign] = 1;
        boolean resultTrue = dpll(simplified, copyAssignTrue, numVars);
        if(resultTrue) {
            // copy successful assignments back
            System.arraycopy(copyAssignTrue, 0, assignments, 0, copyAssignTrue.length);
            return true;
        }
        
        // if true doesn't work, try assigning false
        int[] copyAssignFalse = assignments.clone();
        copyAssignFalse[varToAssign] = -1;
        boolean resultFalse = dpll(simplified, copyAssignFalse, numVars);
        if(resultFalse) {
            // copy successful assignments back
            System.arraycopy(copyAssignFalse, 0, assignments, 0, copyAssignFalse.length);
            return true;
        }
        
        // neither true nor false leads to a solution
        return false;
    }
    
    // method to simplify clauses based on current assignments
    private static List<List<Integer>> simplifyClauses(List<List<Integer>> clauses, int[] assignments) {
        List<List<Integer>> simplified = new ArrayList<>();
        for(List<Integer> clause : clauses) {
            boolean satisfied = false;
            List<Integer> newClause = new ArrayList<>();
            for(Integer literal : clause) {
                int var = Math.abs(literal);
                if(assignments[var] == 0) {
                    newClause.add(literal);
                }
                else {
                    if((literal > 0 && assignments[var] == 1) ||
                       (literal < 0 && assignments[var] == -1)) {
                        // clause is satisfied
                        satisfied = true;
                        break;
                    }
                }
            }
            if(!satisfied) {
                simplified.add(newClause);
            }
        }
        return simplified;
    }
    
    // method to find a pure literal
    private static Integer findPureLiteral(List<List<Integer>> clauses, int numVars) {
        boolean[] pos = new boolean[numVars +1];
        boolean[] neg = new boolean[numVars +1];
        for(List<Integer> clause : clauses) {
            for(Integer literal : clause) {
                int var = Math.abs(literal);
                if(literal >0) {
                    pos[var] = true;
                } else {
                    neg[var] = true;
                }
            }
        }
        for(int i=1;i<=numVars;i++) {
            if(pos[i] && !neg[i]) {
                return i; // pure positive literal
            }
            if(!pos[i] && neg[i]) {
                return -i; // pure negative literal
            }
        }
        return null;
    }
    
    // method to find a unit clause
    private static Integer findUnitClause(List<List<Integer>> clauses) {
        for(List<Integer> clause : clauses) {
            if(clause.size() == 1) {
                return clause.get(0);
            }
        }
        return null;
    }
    
    // method to choose the next variable to assign
    private static int chooseVariable(List<List<Integer>> clauses, int[] assignments, int numVars) {
        // choose the first unassigned variable (lowest index first)
        for(int i = 1; i <= numVars; i++) {
            if(assignments[i] == 0) {
                return i;
            }
        }
        return -1;
    }
}

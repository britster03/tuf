/*
 * 
 * Title :- Implementation of 3CNF Satisfiability.
 * 
 * Name : Ronit Chandresh Virwani (B01099810)
 * Date: 11-24-2024
 
 */


import java.util.*;
import java.io.*;

public class SATSolver {
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        
        // reading the number of variables and clauses
        int numVars = sc.nextInt();
        int numClauses = sc.nextInt();
        
        // first we will read all the clauses
        List<List<Integer>> clauses = new ArrayList<>();
        for(int i = 0; i < numClauses; i++) {
            List<Integer> clause = new ArrayList<>();
            for(int j = 0; j < 3; j++) {
                clause.add(sc.nextInt());
            }
            clauses.add(clause);
        }
        
        // here we will initialize a assignments array 
        // here 1 represents true, -1 represents false, 0 represents unassigned
        int[] assignments = new int[numVars + 1];
        
        // the aim to solve it using the recommeded idea of Davis-Putnam-Logemann-Loveland (DPLL) algorithm
        boolean satisfiable = dpll(clauses, assignments, numVars);
        
        if(satisfiable) {
            // if satisfiable, we will print the assignments
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
            // if not satisfiable, we will print0 "Unsatisfiable"
            System.out.println("Unsatisfiable.");
        }
    }
    
    // implementing dpll recursive algorithm
    // we will pass the list of clauses in cnf, current var asgts, total num of vars
    private static boolean dpll(List<List<Integer>> clauses, int[] assignments, int numVars) {
        // first we will simplify clauses based on current assignments
        List<List<Integer>> simplified = simplifyClauses(clauses, assignments);
        // we will keep in mind some conditions here,
        // if no clauses, all are satisfied
        if(simplified.isEmpty()) {
            return true;
        }
        
        // if any clause is empty, it's unsatisfiable
        for(List<Integer> clause : simplified) {
            if(clause.isEmpty()) {
                return false;
            }
        }
        
        // we will find some pure literals
        Integer pureLiteral = findPureLiteral(simplified, numVars);
        if(pureLiteral != null) {
            int var = Math.abs(pureLiteral);
            int value = pureLiteral > 0 ? 1 : -1;
            assignments[var] = value;
            return dpll(simplified, assignments, numVars);
        }
        
        // here we will find some unit clauses
        Integer unitLiteral = findUnitClause(simplified);
        if(unitLiteral != null) {
            int var = Math.abs(unitLiteral);
            int value = unitLiteral > 0 ? 1 : -1;
            assignments[var] = value;
            return dpll(simplified, assignments, numVars);
        }
        
        // then we will choose the next variable to assign (lowest index will be assigned first)
        int varToAssign = chooseVariable(simplified, assignments, numVars);
        if(varToAssign == -1) {
            // we will return false if no variable is left to assign but still the clauses are not satisfied
            return false;
        }
        
        // we will try assigning true first
        int[] copyAssignTrue = assignments.clone();
        copyAssignTrue[varToAssign] = 1;
        boolean resultTrue = dpll(simplified, copyAssignTrue, numVars);
        if(resultTrue) {
            // if assigning true leads to a solution, we will update the assignments
            System.arraycopy(copyAssignTrue, 0, assignments, 0, copyAssignTrue.length);
            return true;
        }
        
        // if assigning true doesn't work above ,we will try assigning false
        int[] copyAssignFalse = assignments.clone();
        copyAssignFalse[varToAssign] = -1;
        boolean resultFalse = dpll(simplified, copyAssignFalse, numVars);
        if(resultFalse) {
            // if assigning false leads to a solution, we will update the assignments
            System.arraycopy(copyAssignFalse, 0, assignments, 0, copyAssignFalse.length);
            return true;
        }
        
        // return false if neither true nor false leads to a solution
        return false;
    }
    
    // this method simplifies clauses based on current assignments
    // it basically removes clauses that are already satisfied and also removes the falsified literals
    private static List<List<Integer>> simplifyClauses(List<List<Integer>> clauses, int[] assignments) {
        List<List<Integer>> simplified = new ArrayList<>();

        for(List<Integer> clause : clauses) {
            boolean clauseSatisfied = false;
            List<Integer> newClause = new ArrayList<>();

            for(Integer literal : clause) {
                int var = Math.abs(literal);
                int value = assignments[var];

                if(value == 0) {
                    // if the variable is unassigned, we will keep the literal
                    newClause.add(literal);
                } else {
                    if((literal > 0 && value == 1) || (literal < 0 && value == -1)) {
                        // if the clause is satisfied by this literal
                        clauseSatisfied = true;
                        break;
                    }
                    // else, the literal is falsified, and it is not added to newClause
                }
            }

            if(!clauseSatisfied) {
                simplified.add(newClause);
            }
        }

        return simplified;
    }
    
    // here we wilo try to find a pure literal
    // a pure literal appears with only one polarity either all positive or all negative
    private static Integer findPureLiteral(List<List<Integer>> clauses, int numVars) {
        boolean[] pos = new boolean[numVars +1];
        boolean[] neg = new boolean[numVars +1];
        
        for(List<Integer> clause : clauses) {
            for(Integer literal : clause) {
                int var = Math.abs(literal);
                if(literal > 0) {
                    pos[var] = true;
                } else {
                    neg[var] = true;
                }
            }
        }

        // identifying pure literals
        for(int i = 1; i <= numVars; i++) {
            if(pos[i] && !neg[i]) {
                return i; // pure positive literal
            }
            if(!pos[i] && neg[i]) {
                return -i; // pure negative literal
            }
        }
        return null;
    }
    
    // here we will try to find a unit clause in the clauses, a unit clause has only one unassigned literal
    private static Integer findUnitClause(List<List<Integer>> clauses) {
        for(List<Integer> clause : clauses) {
            if(clause.size() == 1) {
                return clause.get(0);
            }
        }
        return null;
    }
    
    // and finally a method to choose the next variable to assign
    // it selects the first unassigned variable based on ascending order
    private static int chooseVariable(List<List<Integer>> clauses, int[] assignments, int numVars) {
        for(int i = 1; i <= numVars; i++) {
            if(assignments[i] == 0) {
                return i;
            }
        }
        return -1; // indicator that all vars are assigned
    }
}

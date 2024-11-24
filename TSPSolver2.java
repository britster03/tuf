import java.io.*;
import java.util.*;

/**
 * Optimized TSP Solver using Nearest Neighbor and 2-opt heuristics with precomputed distances.
 * Handles large datasets efficiently by optimizing data structures and algorithms.
 */
public class TSPSolver2 {

    /**
     * Represents a point with x and y coordinates and an index.
     */
    static class Point {
        int index;
        double x;
        double y;

        Point(int index, double x, double y) {
            this.index = index;
            this.x = x;
            this.y = y;
        }
    }

    /**
     * Reads points from a file.
     *
     * @param filename The name of the input file.
     * @return Array of points.
     * @throws IOException If an I/O error occurs.
     */
    static Point[] readPoints(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        int n = Integer.parseInt(br.readLine().trim());
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            String[] parts = br.readLine().trim().split("\\s+");
            double x = Double.parseDouble(parts[0]);
            double y = Double.parseDouble(parts[1]);
            points[i] = new Point(i, x, y);
        }
        br.close();
        return points;
    }

    /**
     * Precomputes the Euclidean distance matrix for all points.
     *
     * @param points Array of points.
     * @return 2D array representing the distance matrix.
     */
    static double[][] computeDistanceMatrix(Point[] points) {
        int n = points.length;
        double[][] distance = new double[n][n];
        for (int i = 0; i < n; i++) {
            distance[i][i] = 0.0;
            for (int j = i + 1; j < n; j++) {
                double dx = points[i].x - points[j].x;
                double dy = points[i].y - points[j].y;
                double dist = Math.sqrt(dx * dx + dy * dy);
                distance[i][j] = dist;
                distance[j][i] = dist;
            }
        }
        return distance;
    }

    /**
     * Constructs an initial tour using the Nearest Neighbor heuristic.
     *
     * @param distance Distance matrix.
     * @return Array representing the initial tour.
     */
    static int[] nearestNeighborTour(double[][] distance) {
        int n = distance.length;
        int[] tour = new int[n];
        boolean[] visited = new boolean[n];
        int current = 0; // Starting at the first point
        tour[0] = current;
        visited[current] = true;

        for (int i = 1; i < n; i++) {
            double minDist = Double.MAX_VALUE;
            int next = -1;
            for (int j = 0; j < n; j++) {
                if (!visited[j] && distance[current][j] < minDist) {
                    minDist = distance[current][j];
                    next = j;
                }
            }
            if (next == -1) break; // Should not happen
            tour[i] = next;
            visited[next] = true;
            current = next;
        }
        return tour;
    }

    /**
     * Applies the 2-opt algorithm to improve the tour using the First Improvement strategy.
     *
     * @param tour     Initial tour as an array of point indices.
     * @param distance Precomputed distance matrix.
     * @return Improved tour as an array of point indices.
     */
    static int[] twoOpt(int[] tour, double[][] distance) {
        int n = tour.length;
        boolean improvement = true;
        double bestDistance = calculateTotalDistance(tour, distance);
        int iterations = 0;
        int maxIterations = 10000; // Optional: Limit to prevent excessive computation

        while (improvement && iterations < maxIterations) {
            improvement = false;
            for (int i = 1; i < n - 1; i++) {
                for (int k = i + 1; k < n; k++) {
                    double delta = distance[tour[i - 1]][tour[k]] + distance[tour[i]][tour[(k + 1) % n]] -
                                   distance[tour[i - 1]][tour[i]] - distance[tour[k]][tour[(k + 1) % n]];
                    if (delta < -1e-6) { // Allow for floating point precision
                        reverseSegment(tour, i, k);
                        bestDistance += delta;
                        improvement = true;
                        break; // First Improvement: Exit the inner loop
                    }
                }
                if (improvement) break; // Exit the outer loop to restart
            }
            iterations++;
        }
        return tour;
    }

    /**
     * Reverses the segment of the tour between indices i and k.
     *
     * @param tour Tour as an array of point indices.
     * @param i    Start index of the segment to reverse.
     * @param k    End index of the segment to reverse.
     */
    static void reverseSegment(int[] tour, int i, int k) {
        while (i < k) {
            int temp = tour[i];
            tour[i] = tour[k];
            tour[k] = temp;
            i++;
            k--;
        }
    }

    /**
     * Calculates the total distance of the tour.
     *
     * @param tour     Tour as an array of point indices.
     * @param distance Precomputed distance matrix.
     * @return Total Euclidean distance of the tour.
     */
    static double calculateTotalDistance(int[] tour, double[][] distance) {
        double total = 0.0;
        int n = tour.length;
        for (int i = 0; i < n; i++) {
            total += distance[tour[i]][tour[(i + 1) % n]];
        }
        return total;
    }

    /**
     * Writes the tour and its total distance to an output file.
     *
     * @param filename      Output file name.
     * @param tour          Tour as an array of point indices.
     * @param distance      Precomputed distance matrix.
     * @throws IOException If an I/O error occurs.
     */
    static void writeTour(String filename, int[] tour, double[][] distance) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
        double totalDistance = calculateTotalDistance(tour, distance);
        bw.write(String.format("Length: %.4f\n", totalDistance));
        for (int i = 0; i < tour.length; i++) {
            bw.write(tour[i] + (i < tour.length - 1 ? " " : ""));
        }
        bw.newLine();
        bw.close();
    }

    /**
     * Main method to execute the optimized TSP solver.
     *
     * @param args Command-line arguments: inputFile outputFile
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java OptimizedTSPSolver <inputFile> <outputFile>");
            return;
        }

        String inputFile = args[0];
        String outputFile = args[1];

        try {
            // Step 1: Read points from input file
            Point[] points = readPoints(inputFile);

            // Step 2: Precompute distance matrix
            double[][] distance = computeDistanceMatrix(points);

            // Step 3: Construct initial tour using Nearest Neighbor
            int[] tour = nearestNeighborTour(distance);

            // Step 4: Optimize tour using 2-opt
            tour = twoOpt(tour, distance);

            // Step 5: Write the final tour to the output file
            writeTour(outputFile, tour, distance);

            System.out.println("TSP tour has been successfully computed and written to " + outputFile);
        } catch (IOException e) {
            System.err.println("Error reading or writing files: " + e.getMessage());
        }
    }
}

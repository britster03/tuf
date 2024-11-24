import java.io.*;
import java.util.*;

/**
 * TSP Solver using Nearest Neighbor and 2-opt heuristics.
 * Handles large datasets efficiently and ensures a good quality tour.
 */
public class TSPSolver {

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

        /**
         * Calculates the Euclidean distance to another point.
         *
         * @param other The other point.
         * @return Euclidean distance.
         */
        double distanceTo(Point other) {
            double dx = this.x - other.x;
            double dy = this.y - other.y;
            return Math.sqrt(dx * dx + dy * dy);
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
     * Constructs an initial tour using the Nearest Neighbor heuristic.
     *
     * @param points Array of points.
     * @return Initial tour as a list of point indices.
     */
    static List<Integer> nearestNeighborTour(Point[] points) {
        int n = points.length;
        List<Integer> tour = new ArrayList<>(n);
        boolean[] visited = new boolean[n];
        int current = 0; // Starting at the first point
        tour.add(current);
        visited[current] = true;

        for (int i = 1; i < n; i++) {
            double minDist = Double.MAX_VALUE;
            int next = -1;
            for (int j = 0; j < n; j++) {
                if (!visited[j]) {
                    double dist = points[current].distanceTo(points[j]);
                    if (dist < minDist) {
                        minDist = dist;
                        next = j;
                    }
                }
            }
            if (next == -1) break; // Should not happen
            tour.add(next);
            visited[next] = true;
            current = next;
        }
        return tour;
    }

    /**
     * Applies the 2-opt algorithm to improve the tour.
     *
     * @param tour   Initial tour as a list of point indices.
     * @param points Array of points.
     * @return Improved tour as a list of point indices.
     */
    static List<Integer> twoOpt(List<Integer> tour, Point[] points) {
        int n = tour.size();
        boolean improvement = true;
        double bestDistance = calculateTotalDistance(tour, points);

        while (improvement) {
            improvement = false;
            for (int i = 1; i < n - 2; i++) {
                for (int k = i + 1; k < n - 1; k++) {
                    double delta = calculate2OptSwapDelta(tour, points, i - 1, i, k, k + 1);
                    if (delta < -1e-6) { // Allow for floating point precision
                        twoOptSwap(tour, i, k);
                        bestDistance += delta;
                        improvement = true;
                        break; // Improvement found, return to the outer loop
                    }
                }
                if (improvement) break;
            }
        }
        return tour;
    }

    /**
     * Calculates the total distance of the tour.
     *
     * @param tour   Tour as a list of point indices.
     * @param points Array of points.
     * @return Total Euclidean distance of the tour.
     */
    static double calculateTotalDistance(List<Integer> tour, Point[] points) {
        double total = 0.0;
        int n = tour.size();
        for (int i = 0; i < n; i++) {
            Point from = points[tour.get(i)];
            Point to = points[tour.get((i + 1) % n)];
            total += from.distanceTo(to);
        }
        return total;
    }

    /**
     * Calculates the change in distance if a 2-opt swap is performed.
     *
     * @param tour Tour as a list of point indices.
     * @param points Array of points.
     * @param a Index of the first point before the swap.
     * @param b Index of the first point being swapped.
     * @param c Index of the second point being swapped.
     * @param d Index of the point after the swap.
     * @return Change in total distance (negative if improvement).
     */
    static double calculate2OptSwapDelta(List<Integer> tour, Point[] points, int a, int b, int c, int d) {
        Point A = points[tour.get(a)];
        Point B = points[tour.get(b)];
        Point C = points[tour.get(c)];
        Point D = points[tour.get(d)];
        return (C.distanceTo(B) + D.distanceTo(A)) - (A.distanceTo(B) + C.distanceTo(D));
    }

    /**
     * Performs a 2-opt swap by reversing the tour segment between indices i and k.
     *
     * @param tour Tour as a list of point indices.
     * @param i    Start index of the segment to reverse.
     * @param k    End index of the segment to reverse.
     */
    static void twoOptSwap(List<Integer> tour, int i, int k) {
        while (i < k) {
            Collections.swap(tour, i, k);
            i++;
            k--;
        }
    }

    /**
     * Writes the tour and its total distance to an output file.
     *
     * @param filename Output file name.
     * @param tour     Tour as a list of point indices.
     * @param points   Array of points.
     * @throws IOException If an I/O error occurs.
     */
    static void writeTour(String filename, List<Integer> tour, Point[] points) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
        double totalDistance = calculateTotalDistance(tour, points);
        bw.write(String.format("Length: %.4f\n", totalDistance));
        for (int i = 0; i < tour.size(); i++) {
            bw.write(tour.get(i) + (i < tour.size() - 1 ? " " : ""));
        }
        bw.newLine();
        bw.close();
    }

    /**
     * Main method to execute the TSP solver.
     *
     * @param args Command-line arguments: inputFile outputFile
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java TSPSolver <inputFile> <outputFile>");
            return;
        }

        String inputFile = args[0];
        String outputFile = args[1];

        try {
            // Step 1: Read points from input file
            Point[] points = readPoints(inputFile);

            // Step 2: Construct initial tour using Nearest Neighbor
            List<Integer> tour = nearestNeighborTour(points);

            // Step 3: Optimize tour using 2-opt
            tour = twoOpt(tour, points);

            // Step 4: Write the final tour to the output file
            writeTour(outputFile, tour, points);

            System.out.println("TSP tour has been successfully computed and written to " + outputFile);
        } catch (IOException e) {
            System.err.println("Error reading or writing files: " + e.getMessage());
        }
    }
}

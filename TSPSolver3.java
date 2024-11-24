import java.io.*;
import java.util.*;

/**
 * Enhanced TSP Solver using Nearest Neighbor, 2-opt, 3-opt, and Simulated Annealing heuristics.
 * This implementation aims to provide high-quality solutions by combining multiple optimization techniques.
 */
public class TSPSolver3 {

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
                double dist = points[i].distanceTo(points[j]);
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
     * @param start    Starting vertex index.
     * @return Array representing the initial tour.
     */
    static int[] nearestNeighborTour(double[][] distance, int start) {
        int n = distance.length;
        int[] tour = new int[n];
        boolean[] visited = new boolean[n];
        int current = start;
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
     * Applies the 2-opt algorithm to improve the tour.
     *
     * @param tour     Initial tour as an array of point indices.
     * @param distance Precomputed distance matrix.
     * @return Improved tour as an array of point indices.
     */
    static int[] twoOpt(int[] tour, double[][] distance) {
        int n = tour.length;
        boolean improvement = true;
        double bestDistance = calculateTotalDistance(tour, distance);

        while (improvement) {
            improvement = false;
            for (int i = 1; i < n - 1; i++) {
                for (int k = i + 1; k < n; k++) {
                    double delta = distance[tour[i - 1]][tour[k]] + distance[tour[i]][tour[(k + 1) % n]]
                            - distance[tour[i - 1]][tour[i]] - distance[tour[k]][tour[(k + 1) % n]];
                    if (delta < -1e-6) { // Allow for floating point precision
                        reverseSegment(tour, i, k);
                        bestDistance += delta;
                        improvement = true;
                    }
                }
            }
        }
        return tour;
    }

    /**
     * Applies the 3-opt algorithm to further improve the tour.
     *
     * @param tour     Initial tour as an array of point indices.
     * @param distance Precomputed distance matrix.
     * @return Improved tour as an array of point indices.
     */
    static int[] threeOpt(int[] tour, double[][] distance) {
        int n = tour.length;
        boolean improvement = true;
        double bestDistance = calculateTotalDistance(tour, distance);

        while (improvement) {
            improvement = false;
            outerLoop:
            for (int i = 0; i < n - 2; i++) {
                for (int j = i + 2; j < n; j++) {
                    for (int k = j + 2; k < n + (i > 0 ? 1 : 0); k++) {
                        // Perform 3-opt swap and calculate the change in distance
                        int a = tour[i];
                        int b = tour[(i + 1) % n];
                        int c = tour[j % n];
                        int d = tour[(j + 1) % n];
                        int e = tour[k % n];
                        int f = tour[(k + 1) % n];

                        double currentDist = distance[a][b] + distance[c][d] + distance[e][f];
                        double newDist1 = distance[a][c] + distance[b][d] + distance[e][f];
                        double newDist2 = distance[a][d] + distance[c][b] + distance[e][f];
                        double newDist3 = distance[a][c] + distance[b][e] + distance[d][f];
                        double newDist4 = distance[a][d] + distance[e][b] + distance[c][f];
                        double newDist5 = distance[a][e] + distance[d][b] + distance[c][f];

                        double delta = newDist1 - currentDist;
                        if (delta < -1e-6) {
                            // Option 1: a-c-b-d-e-f
                            List<Integer> newTour = new ArrayList<>();
                            for (int l = 0; l <= i; l++) newTour.add(tour[l]);
                            for (int l = j; l >= i + 1; l--) newTour.add(tour[l % n]);
                            for (int l = j + 1; l < n; l++) newTour.add(tour[l]);
                            tour = listToArray(newTour, n);
                            bestDistance += delta;
                            improvement = true;
                            break outerLoop;
                        }

                        delta = newDist2 - currentDist;
                        if (delta < -1e-6) {
                            // Option 2: a-d-c-b-e-f
                            List<Integer> newTour = new ArrayList<>();
                            for (int l = 0; l <= i; l++) newTour.add(tour[l]);
                            for (int l = i + 1; l <= j; l++) newTour.add(tour[l % n]);
                            for (int l = k; l < n; l++) newTour.add(tour[l]);
                            tour = listToArray(newTour, n);
                            bestDistance += delta;
                            improvement = true;
                            break outerLoop;
                        }

                        delta = newDist3 - currentDist;
                        if (delta < -1e-6) {
                            // Option 3: a-c-e-d-b-f
                            List<Integer> newTour = new ArrayList<>();
                            for (int l = 0; l <= i; l++) newTour.add(tour[l]);
                            for (int l = j; l <= k; l++) newTour.add(tour[l % n]);
                            for (int l = i + 1; l < j; l++) newTour.add(tour[l % n]);
                            tour = listToArray(newTour, n);
                            bestDistance += delta;
                            improvement = true;
                            break outerLoop;
                        }

                        delta = newDist4 - currentDist;
                        if (delta < -1e-6) {
                            // Option 4: a-d-e-b-c-f
                            List<Integer> newTour = new ArrayList<>();
                            for (int l = 0; l <= i; l++) newTour.add(tour[l]);
                            for (int l = k; l >= j + 1; l--) newTour.add(tour[l % n]);
                            for (int l = i + 1; l <= j; l++) newTour.add(tour[l % n]);
                            tour = listToArray(newTour, n);
                            bestDistance += delta;
                            improvement = true;
                            break outerLoop;
                        }

                        delta = newDist5 - currentDist;
                        if (delta < -1e-6) {
                            // Option 5: a-e-d-c-b-f
                            List<Integer> newTour = new ArrayList<>();
                            for (int l = 0; l <= i; l++) newTour.add(tour[l]);
                            for (int l = k; l >= j + 1; l--) newTour.add(tour[l % n]);
                            for (int l = i + 1; l <= j; l++) newTour.add(tour[l % n]);
                            tour = listToArray(newTour, n);
                            bestDistance += delta;
                            improvement = true;
                            break outerLoop;
                        }
                    }
                }
            }
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
     * Converts a List<Integer> to an int[] array with a specified size.
     *
     * @param list List of integers.
     * @param size Desired size of the array.
     * @return int[] array containing the elements of the list.
     */
static int[] listToArray(List<Integer> list, int size) {
    if (list.size() != size) {
        System.err.println("Error: newTour size is " + list.size() + ", expected " + size);
        // Optionally, print the list to identify missing elements
        System.err.println("newTour contents: " + list);
        System.exit(1); // Exit the program to prevent further errors
    }
    int[] array = new int[size];
    for (int i = 0; i < size; i++) {
        array[i] = list.get(i);
    }
    return array;
}

    /**
     * Applies Simulated Annealing to further optimize the tour.
     *
     * @param tour     Initial tour as an array of point indices.
     * @param distance Precomputed distance matrix.
     * @return Optimized tour as an array of point indices.
     */
    static int[] simulatedAnnealing(int[] tour, double[][] distance) {
        int n = tour.length;
        int[] currentTour = Arrays.copyOf(tour, n);
        int[] bestTour = Arrays.copyOf(tour, n);
        double currentDistance = calculateTotalDistance(currentTour, distance);
        double bestDistance = currentDistance;

        double temperature = 10000;
        double coolingRate = 0.995;
        double absoluteTemperature = 1e-8;

        Random rand = new Random();

        while (temperature > absoluteTemperature) {
            // Create new candidate tour by swapping two cities
            int i = rand.nextInt(n);
            int j = rand.nextInt(n);
            while (j == i) {
                j = rand.nextInt(n);
            }

            // Swap two cities to create a new tour
            int[] newTour = Arrays.copyOf(currentTour, n);
            int temp = newTour[i];
            newTour[i] = newTour[j];
            newTour[j] = temp;

            double newDistance = calculateTotalDistance(newTour, distance);
            double delta = newDistance - currentDistance;

            // Decide whether to accept the new tour
            if (delta < 0 || Math.exp(-delta / temperature) > rand.nextDouble()) {
                currentTour = newTour;
                currentDistance = newDistance;

                // Update the best tour found so far
                if (currentDistance < bestDistance) {
                    bestTour = Arrays.copyOf(currentTour, n);
                    bestDistance = currentDistance;
                }
            }

            // Cool down the temperature
            temperature *= coolingRate;
        }

        return bestTour;
    }

    /**
     * Writes the tour and its total distance to an output file in a format compatible with tspcheck.c.
     *
     * @param filename      Output file name.
     * @param tour          Tour as an array of point indices.
     * @param distance      Precomputed distance matrix.
     * @throws IOException If an I/O error occurs.
     */
    static void writeTourForChecker(String filename, int[] tour, double[][] distance) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
        double totalDistance = calculateTotalDistance(tour, distance);
        // Write the number of vertices first
        bw.write(tour.length + "\n");
        // Write the sequence of vertex indices
        for (int i = 0; i < tour.length; i++) {
            bw.write(tour[i] + (i < tour.length - 1 ? " " : ""));
        }
        bw.newLine();
        bw.close();
    }

    /**
     * Writes the tour and its total distance to an output file including the length.
     *
     * @param filename      Output file name.
     * @param tour          Tour as an array of point indices.
     * @param distance      Precomputed distance matrix.
     * @throws IOException If an I/O error occurs.
     */
    static void writeDetailedTour(String filename, int[] tour, double[][] distance) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
        double totalDistance = calculateTotalDistance(tour, distance);
        bw.write(String.format("Length: %.6f\n", totalDistance));
        for (int i = 0; i < tour.length; i++) {
            bw.write(tour[i] + (i < tour.length - 1 ? " " : ""));
        }
        bw.newLine();
        bw.close();
    }

    /**
     * Main method to execute the Enhanced TSP solver.
     *
     * @param args Command-line arguments: inputFile outputFile
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java EnhancedTSPSolver <inputFile> <outputFile>");
            return;
        }

        String inputFile = args[0];
        String outputFile = args[1];

        try {
            // Step 1: Read points from input file
            Point[] points = readPoints(inputFile);

            // Step 2: Precompute distance matrix
            double[][] distance = computeDistanceMatrix(points);

            // Step 3: Construct initial tours using Nearest Neighbor from multiple starting points
            int n = points.length;
            List<int[]> initialTours = new ArrayList<>();
            for (int start = 0; start < Math.min(n, 10); start++) { // Limit to 10 starting points for efficiency
                int[] tour = nearestNeighborTour(distance, start);
                initialTours.add(tour);
            }

            // Step 4: Optimize each initial tour using 2-opt and 3-opt
            List<int[]> optimizedTours = new ArrayList<>();
            for (int[] tour : initialTours) {
                int[] optimizedTour = twoOpt(tour, distance);
                optimizedTour = threeOpt(optimizedTour, distance);
                optimizedTours.add(optimizedTour);
            }

            // Step 5: Select the best tour among optimized tours
            double bestDistance = Double.MAX_VALUE;
            int[] bestTour = null;
            for (int[] tour : optimizedTours) {
                double dist = calculateTotalDistance(tour, distance);
                if (dist < bestDistance) {
                    bestDistance = dist;
                    bestTour = tour;
                }
            }

            // Step 6: Further optimize the best tour using Simulated Annealing
            bestTour = simulatedAnnealing(bestTour, distance);
            bestDistance = calculateTotalDistance(bestTour, distance);

            // Step 7: Write the final tour to the output file in checker-compatible format
            writeTourForChecker(outputFile, bestTour, distance);
            writeDetailedTour("detailed_" + outputFile, bestTour, distance); // Optional: Detailed output with length

            System.out.printf("Optimized TSP tour has been successfully computed and written to %s\n", outputFile);
            System.out.printf("Detailed tour with length is written to detailed_%s\n", outputFile);
            System.out.printf("Best tour length: %.6f\n", bestDistance);

        } catch (IOException e) {
            System.err.println("Error reading or writing files: " + e.getMessage());
        }
    }
}

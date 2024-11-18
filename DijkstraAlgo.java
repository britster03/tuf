/*
 * 
 * Title :- Implementation of Dijkstra's algorithm.
 * 
 * Name : Ronit Chandresh Virwani (B01099810)
 * Date: 2024-09-18

 * In this implementation the focus is on Dijkstra's algorithm to determine the shortest path between two specified vertices. 
 * with the help of custom min-heap the priority queue is managed, and the operations like decrease key are also implemented. 
 * The program reads graph data from standard input and outputs the shortest distance or indicates if no connection exists between the start and end vertices.
 * PseduoCode Referred From The Book "Introductin to Algorithms" :-

 *   DIJKSTRA(G,w,s)
 *   1. INTIALIZE-SINGLE-SOURCE(G,s)
 *   2. S = ∅
 *   3. Q = ∅
 *   4. for each vertex u ∈ G.V
 *   5. INSERT(Q,u)
 *   6. while Q != ∅
 *   7. u = EXTRACT_MIN(Q)
 *   8. S = S ∪ {u}
 *   9. for each vertex v in G.Adj[u]
 *   10. RELAX(u,v,w)
 *   11. if the call of RELAX decreased v.d
 *   12. DECREASE_KEY(Q,v,v.d)

 */

import java.util.*;
import java.io.*;

public class DijkstraAlgo {

    /**
     * edge in the graph with a target vertex and weight.
     */
    static class DirectedEdge {
        int destination; // denotes vertex this edge points to
        int weight; // weight/cost of traversing this edge

        DirectedEdge(int destination, int weight) {
            this.destination = destination;
            this.weight = weight;
        }
    }


    static class Graph {
        int vertexCount; // total number of vertices in the graph
        List<List<DirectedEdge>> adjacencyList; // adjacency list for each vertex

        /**
         * the graph is initialised here with the given number of vertices
         * 'vertices' is the (Number of vertices)
         */
        Graph(int vertexCount) {
            this.vertexCount = vertexCount;
            adjacencyList = new ArrayList<>();
            for (int i = 0; i < vertexCount; i++) {
                adjacencyList.add(new ArrayList<>());
            }
        }

        /**
         * a directed edge is added from vertex 'from' to vertex 'to' with the given
         * weight.
         * 'from' is Source vertex
         * 'to' is Destination vertex
         * 'weight' is weight of the edge
         */
        void addDirectedEdge(int from, int to, int weight) {
            adjacencyList.get(from).add(new DirectedEdge(to, weight));
        }
    }

    /**
     * HeapElement is an element in the priority queue (min-heap) with a vertex and
     * its current shortest distance.
     */
    static class HeapElement {
        int vertex; // for identifying the vertex
        int distance; // this distance is the current known shortest distance from the source vertex

        HeapElement(int vertex, int distance) {
            this.vertex = vertex;
            this.distance = distance;
        }
    }

    /**
     * Here we have the most important part of the code, the MinHeap implementation
     * that supports decrease key
     */
    static class MinHeap {
        List<HeapElement> heap; // The heap array
        int[] vertexPosition; // Maps vertex to its position in the heap for quick access

        /**
         * the min-heap is initiliasied with a capacity based on number of the vertices
         * the parameter size is the Number of vertices to fit in the heap
         */
        MinHeap(int size) {
            heap = new ArrayList<>(size);
            vertexPosition = new int[size];
            Arrays.fill(vertexPosition, -1); // Initialize all positions to -1 (not in heap)
        }

        /**
         * Checking if the heap is empty.
         * returns true if heap is empty, false otherwise
         */
        boolean isEmpty() {
            return heap.isEmpty();
        }

        /**
         * to insert a new HeapElement into the heap
         */
        void insert(HeapElement element) {
            heap.add(element);
            int currentIndex = heap.size() - 1;
            vertexPosition[element.vertex] = currentIndex;
            heapUp(currentIndex);
        }

        /**
         * to extract the HeapElement with the minimum distance from the heap
         */
        HeapElement extractMin() {
            if (heap.isEmpty())
                return null;

            HeapElement minElement = heap.get(0);
            HeapElement lastElement = heap.remove(heap.size() - 1);
            vertexPosition[minElement.vertex] = -1;

            if (!heap.isEmpty()) {
                heap.set(0, lastElement);
                vertexPosition[lastElement.vertex] = 0;
                heapDown(0);
            }
            return minElement;
        }

        /**
         * the decrease key here plays an important role in decreasing the distance of a
         * given vertex and it then adjusts the position of vertex in the heap
         * parameter 'vertex' is the vertex whose distance is to be decreased
         * parameter newDistance is the new smaller distance value
         */
        void decreaseKey(int vertex, int newDistance) {
            int index = vertexPosition[vertex];
            if (index == -1)
                return; // if the vertex is not present in the heap it will return
            heap.get(index).distance = newDistance;
            heapUp(index);
        }

        /**
         * it moves the element up at the given index to maintain the property of heap
         * the parameter 'index' is the index of the element to heapify up
         */
        void heapUp(int index) {
            while (index > 0) {
                int parent = (index - 1) / 2;
                if (heap.get(parent).distance > heap.get(index).distance) {
                    swap(parent, index);
                    index = parent;
                } else {
                    break;
                }
            }
        }

        /**
         * it moves the element down at the given index to maintain the property of heap
         * the parameter 'index' is the index of the element to heapify up
         */
        void heapDown(int index) {
            int smallest = index;
            int leftChild = 2 * index + 1;
            int rightChild = 2 * index + 2;

            if (leftChild < heap.size() && heap.get(leftChild).distance < heap.get(smallest).distance) {
                smallest = leftChild;
            }
            if (rightChild < heap.size() && heap.get(rightChild).distance < heap.get(smallest).distance) {
                smallest = rightChild;
            }
            if (smallest != index) {
                swap(smallest, index);
                heapDown(smallest);
            }
        }

        /**
         * here we swap two elements in the heap and updates their positions.
         */
        void swap(int i, int j) {
            HeapElement temp = heap.get(i);
            heap.set(i, heap.get(j));
            heap.set(j, temp);

            vertexPosition[heap.get(i).vertex] = i;
            vertexPosition[heap.get(j).vertex] = j;
        }
    }

    /**
     * the most important part is here, the Dijkstra's algorithm to find the
     * shortest path between two vertices in a graph using the pseudocode mentioned
     * in the beggining
     */
    static class DijkstraAlgorithm {
        Graph graph; // given graph on which we have to test the algo
        int source; // starting vertex
        int target; // destination vertex
        int[] distances; // initialised an array to store shortest distances from source
        int[] predecessors; // initialised an array to store predecessors for path reconstruction

        /**
         * the algorithm begins with the graph, source, and target vertices.
         */
        DijkstraAlgorithm(Graph graph, int source, int target) {
            this.graph = graph;
            this.source = source;
            this.target = target;
            distances = new int[graph.vertexCount];
            predecessors = new int[graph.vertexCount];
            Arrays.fill(distances, Integer.MAX_VALUE); // Initialize all distances to infinity
            Arrays.fill(predecessors, -1); // Initialize all predecessors to -1
        }

        /**
         * using the algo to compute shortest paths from the source vertex
         */
        void findShortestPath() {
            distances[source] = 0; // Distance from source to itself is zero

            MinHeap priorityQueue = new MinHeap(graph.vertexCount);
            for (int vertex = 0; vertex < graph.vertexCount; vertex++) {
                priorityQueue.insert(new HeapElement(vertex, distances[vertex]));
            }

            while (!priorityQueue.isEmpty()) {
                HeapElement current = priorityQueue.extractMin();
                int c = current.vertex;

                // iteratinng through all adjacent vertices of c
                for (DirectedEdge edge : graph.adjacencyList.get(c)) {
                    int e = edge.destination;
                    int weight = edge.weight;

                    // the relaxation step where we update the distance if a shorter path is found
                    if (distances[c] != Integer.MAX_VALUE && distances[c] + weight < distances[e]) {
                        distances[e] = distances[c] + weight;
                        predecessors[e] = c;
                        priorityQueue.decreaseKey(e, distances[e]);
                    }
                }
            }
        }

        /**
         * here we get the shortest distance from the source to the target vertex
         * it either returns the shortest distance or gives Integer.MAX_VALUE if no path
         * exists
         */
        int getShortestDistance() {
            return distances[target];
        }

        /**
         * this function is for clarity , it reconstructs the shortest path from the
         * source to the target vertex
         * it actually helps return a list of vertices in the shortest path or returns
         * an empty list if no path exists
         */
        List<Integer> getPath() {
            List<Integer> path = new ArrayList<>();
            if (distances[target] == Integer.MAX_VALUE) {
                return path; // here indication is that no path exists
            }
            for (int at = target; at != -1; at = predecessors[at]) {
                path.add(at);
            }
            Collections.reverse(path); // uses collections to reverse and get the path from source to target vertex
            return path;
        }
    }

    /**
     * the main method
     * Accepts start and end vertices as command-line arguments and reads the graph
     */
    public static void main(String[] args) {
        // ensuring that exactly two command-line arguments are provided
        if (args.length != 2) {
            System.out.println("Usage: java DijkstraSolver <start> <end>");
            return;
        }

        // fetch the start and end vertices from command-line arguments
        int startVertex, endVertex;
        try {
            startVertex = Integer.parseInt(args[0]);
            endVertex = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Note that start and end vertices must be integers values only.");
            return;
        }

        try {
            Scanner scanner = new Scanner(System.in);

            // reading the number of vertices and edges from input
            if (!scanner.hasNextInt()) {
                System.out.println("please enter number of vertices");
                scanner.close();
                return;
            }
            int numberOfVertices = scanner.nextInt();

            if (!scanner.hasNextInt()) {
                System.out.println("please enter number of edges");
                scanner.close();
                return;
            }
            int numberOfEdges = scanner.nextInt();

            // initiating the graph
            Graph graph = new Graph(numberOfVertices);

            // reading each edge and adding to graph
            for (int i = 0; i < numberOfEdges; i++) {
                if (!scanner.hasNextInt()) {
                    System.out.println("edge start vertex expected");
                    scanner.close();
                    return;
                }
                int from = scanner.nextInt();

                if (!scanner.hasNextInt()) {
                    System.out.println("edge end vertex expected");
                    scanner.close();
                    return;
                }
                int to = scanner.nextInt();

                if (!scanner.hasNextInt()) {
                    System.out.println("edge weight expected");
                    scanner.close();
                    return;
                }
                int weight = scanner.nextInt();

                // Validate vertex numbers
                if (from < 0 || from >= numberOfVertices || to < 0 || to >= numberOfVertices) {
                    System.out.println("vertex numbers must be between 0 and " + (numberOfVertices - 1));
                    scanner.close();
                    return;
                }

                if (weight <= 0) {
                    System.out.println("edge weight must be a positive integer");
                    scanner.close();
                    return;
                }

                graph.addDirectedEdge(from, to, weight);
            }

            scanner.close();

            // validation check for vertices
            if (startVertex < 0 || startVertex >= numberOfVertices || endVertex < 0 || endVertex >= numberOfVertices) {
                System.out.println("start or end vertex must be between 0 and " + (numberOfVertices - 1));
                return;
            }

            // executing the algo
            DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph, startVertex, endVertex);
            dijkstra.findShortestPath();
            int shortestDistance = dijkstra.getShortestDistance();


            if (shortestDistance == Integer.MAX_VALUE) {
                System.out.println("not connected");
            } else {
                System.out.println(shortestDistance);
                // printing the shortest path completely
                /*
                 * List<Integer> path = dijkstra.getPath();
                 * for(int vertex : path){
                 * System.out.print(vertex + " ");
                 * }
                 * System.out.println();
                 */
            }

        } catch (Exception e) {
            System.out.println("Error processing input: " + e.getMessage());
        }
    }
}

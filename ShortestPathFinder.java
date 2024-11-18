/*
 * ShortestPathFinder.java
 * 
 * A unique implementation of Dijkstra's algorithm to find the shortest path between two vertices
 * in a directed, weighted graph. This version includes a custom min-heap with decrease key functionality
 * and supports multiple edges between the same pair of vertices.
 * 
 * Author: [Your Name]
 * Date: 2024-09-16
 */

 import java.util.*;
 import java.io.*;
 
 public class ShortestPathFinder {
     
     /**
      * Represents a directed edge in the graph with a destination vertex and associated weight.
      */
     static class DirectedEdge {
         int destination; // The vertex this edge points to
         int weight;      // The weight/cost of traversing this edge
         
         DirectedEdge(int destination, int weight){
             this.destination = destination;
             this.weight = weight;
         }
     }
     
     /**
      * Encapsulates the graph structure using an adjacency list representation.
      */
     static class Graph {
         int vertexCount; // Total number of vertices in the graph
         List<List<DirectedEdge>> adjacencyList; // Adjacency list for each vertex
         
         /**
          * Initializes the graph with the specified number of vertices.
          * @param vertexCount Number of vertices in the graph
          */
         Graph(int vertexCount){
             this.vertexCount = vertexCount;
             adjacencyList = new ArrayList<>();
             for(int i = 0; i < vertexCount; i++) {
                 adjacencyList.add(new ArrayList<>());
             }
         }
         
         /**
          * Adds a directed edge from vertex 'from' to vertex 'to' with the given weight.
          * @param from Source vertex
          * @param to Destination vertex
          * @param weight Weight of the edge
          */
         void addDirectedEdge(int from, int to, int weight){
             adjacencyList.get(from).add(new DirectedEdge(to, weight));
         }
     }
     
     /**
      * Represents an element in the priority queue (min-heap) with a vertex and its current shortest distance.
      */
     static class HeapElement {
         int vertex;    // Vertex identifier
         int distance;  // Current known shortest distance from the source vertex
         
         HeapElement(int vertex, int distance){
             this.vertex = vertex;
             this.distance = distance;
         }
     }
     
     /**
      * Custom MinHeap implementation that supports efficient decrease key operations.
      */
     static class CustomMinHeap {
         List<HeapElement> heap; // The heap list
         int[] vertexPosition;    // Maps each vertex to its current position in the heap
         
         /**
          * Initializes the min-heap with a capacity based on the number of vertices.
          * @param size Number of vertices to accommodate in the heap
          */
         CustomMinHeap(int size){
             heap = new ArrayList<>();
             vertexPosition = new int[size];
             Arrays.fill(vertexPosition, -1); // Initialize all positions to -1 indicating absence in heap
         }
         
         /**
          * Checks whether the heap is empty.
          * @return true if the heap is empty, false otherwise
          */
         boolean isEmpty(){
             return heap.isEmpty();
         }
         
         /**
          * Inserts a new HeapElement into the heap.
          * @param element The HeapElement to be inserted
          */
         void insert(HeapElement element){
             heap.add(element);
             int currentIndex = heap.size() - 1;
             vertexPosition[element.vertex] = currentIndex;
             siftUp(currentIndex);
         }
         
         /**
          * Extracts and returns the HeapElement with the minimum distance.
          * @return The HeapElement with the smallest distance
          */
         HeapElement extractMin(){
             if(heap.isEmpty()) return null;
             
             HeapElement minElement = heap.get(0);
             HeapElement lastElement = heap.remove(heap.size() - 1);
             vertexPosition[minElement.vertex] = -1; // Mark as removed
             
             if(!heap.isEmpty()){
                 heap.set(0, lastElement);
                 vertexPosition[lastElement.vertex] = 0;
                 siftDown(0);
             }
             
             return minElement;
         }
         
         /**
          * Decreases the distance value of a specific vertex and adjusts its position in the heap.
          * @param vertex The vertex whose distance is to be decreased
          * @param newDistance The new, smaller distance value
          */
         void decreaseKey(int vertex, int newDistance){
             int index = vertexPosition[vertex];
             if(index == -1) return; // Vertex not present in heap
             heap.get(index).distance = newDistance;
             siftUp(index);
         }
         
         /**
          * Sifts up the element at the given index to maintain heap property.
          * @param index The index of the element to sift up
          */
         void siftUp(int index){
             while(index > 0){
                 int parent = (index - 1) / 2;
                 if(heap.get(parent).distance > heap.get(index).distance){
                     swap(parent, index);
                     index = parent;
                 }
                 else{
                     break;
                 }
             }
         }
         
         /**
          * Sifts down the element at the given index to maintain heap property.
          * @param index The index of the element to sift down
          */
         void siftDown(int index){
             int smallest = index;
             int leftChild = 2 * index + 1;
             int rightChild = 2 * index + 2;
             
             if(leftChild < heap.size() && heap.get(leftChild).distance < heap.get(smallest).distance){
                 smallest = leftChild;
             }
             if(rightChild < heap.size() && heap.get(rightChild).distance < heap.get(smallest).distance){
                 smallest = rightChild;
             }
             if(smallest != index){
                 swap(smallest, index);
                 siftDown(smallest);
             }
         }
         
         /**
          * Swaps two elements in the heap and updates their positions.
          * @param i Index of the first element
          * @param j Index of the second element
          */
         void swap(int i, int j){
             HeapElement temp = heap.get(i);
             heap.set(i, heap.get(j));
             heap.set(j, temp);
             
             // Update positions
             vertexPosition[heap.get(i).vertex] = i;
             vertexPosition[heap.get(j).vertex] = j;
         }
     }
     
     /**
      * Implements Dijkstra's algorithm to find the shortest path between two vertices in a graph.
      */
     static class DijkstraAlgorithm {
         Graph graph;           // The graph on which to run the algorithm
         int source;            // The starting vertex
         int target;            // The destination vertex
         int[] distances;       // Array to store shortest distances from source
         int[] predecessors;    // Array to store predecessors for path reconstruction
         
         /**
          * Initializes the algorithm with the graph, source, and target vertices.
          * @param graph The graph
          * @param source The starting vertex
          * @param target The destination vertex
          */
         DijkstraAlgorithm(Graph graph, int source, int target){
             this.graph = graph;
             this.source = source;
             this.target = target;
             distances = new int[graph.vertexCount];
             predecessors = new int[graph.vertexCount];
             Arrays.fill(distances, Integer.MAX_VALUE); // Initialize all distances to infinity
             Arrays.fill(predecessors, -1);             // Initialize all predecessors to -1
         }
         
         /**
          * Executes Dijkstra's algorithm to compute shortest paths from the source vertex.
          */
         void findShortestPath(){
             distances[source] = 0; // Distance from source to itself is zero
             
             CustomMinHeap priorityQueue = new CustomMinHeap(graph.vertexCount);
             for(int vertex = 0; vertex < graph.vertexCount; vertex++){
                 priorityQueue.insert(new HeapElement(vertex, distances[vertex]));
             }
             
             while(!priorityQueue.isEmpty()){
                 HeapElement current = priorityQueue.extractMin();
                 int u = current.vertex;
                 
                 // Iterate through all adjacent vertices of u
                 for(DirectedEdge edge : graph.adjacencyList.get(u)){
                     int v = edge.destination;
                     int weight = edge.weight;
                     
                     // Relaxation step: Update distance if a shorter path is found
                     if(distances[u] != Integer.MAX_VALUE && distances[u] + weight < distances[v]){
                         distances[v] = distances[u] + weight;
                         predecessors[v] = u;
                         priorityQueue.decreaseKey(v, distances[v]);
                     }
                 }
             }
         }
         
         /**
          * Retrieves the shortest distance from the source to the target vertex.
          * @return The shortest distance, or Integer.MAX_VALUE if no path exists
          */
         int getShortestDistance(){
             return distances[target];
         }
         
         /**
          * Reconstructs the shortest path from the source to the target vertex.
          * @return A list of vertices representing the shortest path, or an empty list if no path exists
          */
         List<Integer> getPath(){
             List<Integer> path = new ArrayList<>();
             if(distances[target] == Integer.MAX_VALUE){
                 return path; // No path exists
             }
             for(int at = target; at != -1; at = predecessors[at]){
                 path.add(at);
             }
             Collections.reverse(path); // Reverse to get the path from source to target
             return path;
         }
     }
     
     /**
      * The main class to execute the ShortestPathFinder program.
      * It parses input, constructs the graph, runs Dijkstra's algorithm, and outputs the result.
      */
     public static void main(String[] args) {
         // Ensure that exactly two command-line arguments are provided
         if(args.length != 2){
             System.out.println("Usage: java ShortestPathFinder <start_vertex> <end_vertex>");
             return;
         }
         
         int startVertex, endVertex;
         try {
             startVertex = Integer.parseInt(args[0]);
             endVertex = Integer.parseInt(args[1]);
         } catch(NumberFormatException e){
             System.out.println("Error: Start and end vertices must be integers.");
             return;
         }
         
         try {
             Scanner scanner = new Scanner(System.in);
             
             // Read the number of vertices and edges
             if(!scanner.hasNextInt()){
                 System.out.println("Error: Number of vertices expected.");
                 scanner.close();
                 return;
             }
             int numberOfVertices = scanner.nextInt();
             
             if(!scanner.hasNextInt()){
                 System.out.println("Error: Number of edges expected.");
                 scanner.close();
                 return;
             }
             int numberOfEdges = scanner.nextInt();
             
             // Initialize the graph
             Graph graph = new Graph(numberOfVertices);
             
             // Read each edge and add to the graph
             for(int i = 0; i < numberOfEdges; i++){
                 if(!scanner.hasNextInt()){
                     System.out.println("Error: Edge start vertex expected.");
                     scanner.close();
                     return;
                 }
                 int from = scanner.nextInt();
                 
                 if(!scanner.hasNextInt()){
                     System.out.println("Error: Edge end vertex expected.");
                     scanner.close();
                     return;
                 }
                 int to = scanner.nextInt();
                 
                 if(!scanner.hasNextInt()){
                     System.out.println("Error: Edge weight expected.");
                     scanner.close();
                     return;
                 }
                 int weight = scanner.nextInt();
                 
                 // Validate vertex indices
                 if(from < 0 || from >= numberOfVertices || to < 0 || to >= numberOfVertices){
                     System.out.println("Error: Vertex numbers must be between 0 and " + (numberOfVertices - 1));
                     scanner.close();
                     return;
                 }
                 
                 // Validate edge weight
                 if(weight <= 0){
                     System.out.println("Error: Edge weights must be positive integers.");
                     scanner.close();
                     return;
                 }
                 
                 graph.addDirectedEdge(from, to, weight);
             }
             
             scanner.close();
             
             // Validate start and end vertices
             if(startVertex < 0 || startVertex >= numberOfVertices || endVertex < 0 || endVertex >= numberOfVertices){
                 System.out.println("Error: Start and end vertices must be between 0 and " + (numberOfVertices - 1));
                 return;
             }
             
             // Initialize and execute Dijkstra's algorithm
             DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph, startVertex, endVertex);
             dijkstra.findShortestPath();
             int shortestDistance = dijkstra.getShortestDistance();
             
             // Output the result
             if(shortestDistance == Integer.MAX_VALUE){
                 System.out.println("not connected");
             }
             else{
                 System.out.println(shortestDistance);
                 // Optional: Print the path
                 /*
                 List<Integer> path = dijkstra.getPath();
                 for(int vertex : path){
                     System.out.print(vertex + " ");
                 }
                 System.out.println();
                 */
             }
             
         } catch(Exception e){
             System.out.println("An unexpected error occurred: " + e.getMessage());
         }
     }
 }
 
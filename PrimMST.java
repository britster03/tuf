/*
 * 
 * Title :- Implementation of Prim's Algorithm.
 * 
 * Name : Ronit Chandresh Virwani (B01099810)
 * Date: 09-24-2024
 * 
 * In this implementation the focus is on Prim's algorithm to determine the minimum spanning tree of a connected,
 * undirected, weighted graph. Utilizing a custom min-heap, the priority queue is managed efficiently and also the decrease key is supported. 
 * The program reads graph data from standard input and outputs
 * the total length of Prim's minimum spanning tree. If the graph is disconnected, it outputs "not connected".
 * 
 * Pseudocode Referred From The Book "Introduction to Algorithms":
 * 
 * MST-PRIM(G, w, r)
 * 1. for each vertex u ∈ G.V
 *      u.key = ∞
 *      u.π  = NIL
 * 2. r.key = 0
 * 3. Q = ∅
 * 4. for each vertex u ∈ G.V 
 *      INSERT(Q, u)
 * 5. while Q != ∅
 *      u = EXTRACT-MIN(Q)
 *      for each vertex v in G.Adj[u]
 *          if v ∈ Q and w(u, v) < v.key
 *              v.π = u 
 *              v.key = w(u, v)
 *              DECREASE-KEY(Q, v, w(u, v))
 * 
 */

 import java.util.*;
 import java.io.*;
 
 public class PrimMST {
     
     /**
      * an undirected edge in the graph with two vertices and a weight.
      */
     static class UndirectedEdge {
         int vertex1;   // 1st edge vertex
         int vertex2;   // 2nd vertex edge
         int weight;    // cost of traversing
         
         /**
          * constructing an undirected edge between two vertices with given weight
          */
         UndirectedEdge(int vertex1, int vertex2, int weight){
             this.vertex1 = vertex1;
             this.vertex2 = vertex2;
             this.weight = weight;
         }
     }
     

     static class Graph {
         int vertexCount; // total number of vertices in the graph
         List<List<UndirectedEdge>> adjacencyList;  // adjacency list for each vertex
         
         /**
          * the graph is initialised here with the given number of vertices
          */
         Graph(int vertexCount){
             this.vertexCount = vertexCount;
             adjacencyList = new ArrayList<>();
             for(int i = 0; i < vertexCount; i++) {
                 adjacencyList.add(new ArrayList<>());
             }
         }
         
         /**
          * an undirected edge between two vertices with given weight.
          */
         void addUndirectedEdge(int from, int to, int weight){
             UndirectedEdge edge = new UndirectedEdge(from, to, weight);
             adjacencyList.get(from).add(edge);
             adjacencyList.get(to).add(edge);
         }
     }
     
      /**
      * HeapElement is an element in the priority queue (min-heap) wwith a vertex and its current key value.
      */
     static class HeapElement {
         int vertex;   //  for identifying the vertex
         int key;      // Current key value (minimum weight to connect to MST)
         
         HeapElement(int vertex, int key){
             this.vertex = vertex;
             this.key = key;
         }
     }
     
    /**
     * Here we have the most important part of the code, the MinHeap implementation
     * that supports decrease key
     */
     static class MinHeap {
         List<HeapElement> heap;    // The heap array
         int[] vertexPosition;      // Maps vertex to its position in the heap for quick access
         
        /**
         * the min-heap is initiliasied with a capacity based on number of the vertices
         * the parameter size is the Number of vertices to fit in the heap
         */
         MinHeap(int size){
             heap = new ArrayList<>(size);
             vertexPosition = new int[size];
             Arrays.fill(vertexPosition, -1); // Initialize all positions to -1 (not in heap)
         }
         
        /**
         * Checking if the heap is empty.
         * returns true if heap is empty, false otherwise
         */
         boolean isEmpty(){
             return heap.isEmpty();
         }
         
        /**
         * to insert a new HeapElement into the heap
         */
         void insert(HeapElement element){
             heap.add(element);
             int currentIndex = heap.size() - 1;
             vertexPosition[element.vertex] = currentIndex;
             heapUp(currentIndex);
         }
         
        /**
         * to extract the HeapElement with the minimum key from the heap
         */
         HeapElement extractMin(){
             if(heap.isEmpty()) return null;
             
             HeapElement minElement = heap.get(0);
             HeapElement lastElement = heap.remove(heap.size() - 1);
             vertexPosition[minElement.vertex] = -1; 
             
             if(!heap.isEmpty()){
                 heap.set(0, lastElement);
                 vertexPosition[lastElement.vertex] = 0;
                 heapDown(0);
             }
             
             return minElement;
         }
         

        /**
         * the decrease key here plays an important role in decreasing the distance of a
         * given vertex and it then adjusts the position of vertex in the heap
         * parameter 'vertex' is the vertex whose key is to be decreased
         * parameter newKey is the new smaller distance value
         */
         void decreaseKey(int vertex, int newKey){
             int index = vertexPosition[vertex];
             if(index == -1) 
             return; // Vertex not present in heap
             heap.get(index).key = newKey;
             heapUp(index);
         }
         
        /**
         * it moves the element up at the given index to maintain the property of heap
         */
         void heapUp(int index){
             while(index > 0){
                 int parent = (index - 1) / 2;
                 if(heap.get(parent).key > heap.get(index).key){
                     swap(parent, index);
                     index = parent;
                 }
                 else{
                     break;
                 }
             }
         }
         
        /**
         * it moves the element down at the given index to maintain the property of heap
         */
         void heapDown(int index){
             int smallest = index;
             int leftChild = 2 * index + 1;
             int rightChild = 2 * index + 2;
             
             if(leftChild < heap.size() && heap.get(leftChild).key < heap.get(smallest).key){
                 smallest = leftChild;
             }
             if(rightChild < heap.size() && heap.get(rightChild).key < heap.get(smallest).key){
                 smallest = rightChild;
             }
             if(smallest != index){
                 swap(smallest, index);
                 heapDown(smallest);
             }
         }
        
         /**
         * it moves the element down at the given index to maintain the property of heap
         * the parameter 'index' is the index of the element to heapify up
         */
         void swap(int i, int j){
             HeapElement temp = heap.get(i);
             heap.set(i, heap.get(j));
             heap.set(j, temp);
             
             vertexPosition[heap.get(i).vertex] = i;
             vertexPosition[heap.get(j).vertex] = j;
         }
     }
     
     /**
      * Prim's algorithm to find the Minimum Spanning Tree of a graph.
      */
     static class PrimAlgorithm {
         Graph graph;           // given graph on which we have to test the algo
         int[] key;             // initialised an array to hold the minimum weight to connect each vertex to the MST
         int[] predecessor;     // initialised an array to hold the predecessor of each vertex in the MST
         boolean[] inMST;       //  initialised an array to track vertices included in the MST
         int totalWeight;       // total weight of the mst
         

         PrimAlgorithm(Graph graph){
             this.graph = graph;
             int n = graph.vertexCount;
             key = new int[n];
             predecessor = new int[n];
             inMST = new boolean[n];
             Arrays.fill(key, Integer.MAX_VALUE);    // initially all keys are set to infinity
             Arrays.fill(predecessor, -1);            // and initially all predecessor to nil
             totalWeight = 0;
         }
         
         /**
          * we use the prims algo and compute the mst using the pseudocode given in the beginning
          */
         int computeMST(){
             // Step 1: initialising all keys as infinity and predecessors as NIL
             key[0] = 0; // the first vertex can be any vertex
             
             // Step 3: initialising a min-heap and inserting all vertices in it
             MinHeap minHeap = new MinHeap(graph.vertexCount);
             for(int v = 0; v < graph.vertexCount; v++){
                 minHeap.insert(new HeapElement(v, key[v]));
             }
             
             while(!minHeap.isEmpty()){
                 HeapElement minElement = minHeap.extractMin();
                 int u = minElement.vertex;
                 
                 if(minElement.key == Integer.MAX_VALUE){
                     // if the smallest key is still infinity, the remaining vertices are disconnected
                     return -1;
                 }
                 
                 inMST[u] = true; // include the vertex u in MST
                 totalWeight += minElement.key; // then add its key to total weight
                 
                 // Step 7: iterating over all adjacent vertices of u
                 for(UndirectedEdge edge : graph.adjacencyList.get(u)){
                     int v = (edge.vertex1 == u) ? edge.vertex2 : edge.vertex1;
                     int weight = edge.weight;
                     
                     // Step 8: the case where if v is not in MST and weight is smaller than current key[v]
                     if(!inMST[v] && weight < key[v]){
                         key[v] = weight;
                         predecessor[v] = u;
                         minHeap.decreaseKey(v, key[v]); // Step 12: the call to decrease key function
                     }
                 }
             }
             
             // Step 9: checking if all the vertices are included in the MST
             for(int v = 0; v < graph.vertexCount; v++){
                 if(!inMST[v]){
                     return -1; // this indicates a disconnected graph
                 }
             }
             
             return totalWeight;
         }
     }
     
     public static void main(String[] args) {
         try {
             Scanner scanner = new Scanner(System.in);
             
             // Read the number of vertices
             if(!scanner.hasNextInt()){
                 System.out.println("please enter number of vertice");
                 scanner.close();
                 return;
             }
             int numberOfVertices = scanner.nextInt();
             
             // Read the number of edges
             if(!scanner.hasNextInt()){
                 System.out.println("please enter number of edges");
                 scanner.close();
                 return;
             }
             int numberOfEdges = scanner.nextInt();
             
             // Initialize the graph
             Graph graph = new Graph(numberOfVertices);
             
             // Read each edge and add to the graph
             for(int i = 0; i < numberOfEdges; i++){
                 if(!scanner.hasNextInt()){
                     System.out.println("edge start vertex expected");
                     scanner.close();
                     return;
                 }
                 int from = scanner.nextInt();
                 
                 if(!scanner.hasNextInt()){
                     System.out.println("edge end vertex expected");
                     scanner.close();
                     return;
                 }
                 int to = scanner.nextInt();
                 
                 if(!scanner.hasNextInt()){
                     System.out.println("edge weight expected");
                     scanner.close();
                     return;
                 }
                 int weight = scanner.nextInt();
                 
                 // Validate vertex numbers
                 if(from < 0 || from >= numberOfVertices || to < 0 || to >= numberOfVertices){
                     System.out.println("vertex numbers must be between 0 and " + (numberOfVertices - 1) + ".");
                     scanner.close();
                     return;
                 }
                 
                 // Validate edge weight
                 if(weight <= 0){
                     System.out.println("edge weight must be a positive integer");
                     scanner.close();
                     return;
                 }
                 
                 // **Corrected Line:**
                 graph.addUndirectedEdge(from, to, weight);
             }
             
             scanner.close();
             
             // Execute Prim's algorithm
             PrimAlgorithm prim = new PrimAlgorithm(graph);
             int mstWeight = prim.computeMST();
             
             // Output the result
             if(mstWeight == -1){
                 System.out.println("not connected");
             }
             else{
                 System.out.println(mstWeight);
             }
             
         } catch(Exception e){
             System.out.println("Error processing input: " + e.getMessage());
         }
     }
 }
 
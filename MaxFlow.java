/*
 * 
 * Title :- Implementation of MaxFlow - The Edmonds Karp Version
 * 
 * Name : Ronit Chandresh Virwani (B01099810)
 * Date: 2024-10-22

 * in this program we use Edmonds-Karp algorithm to compute the maximum flow in a directed graph
 * it is a variation of the Ford-Fulkerson method
 * this algo uses Breadth-First Search (BFS) to find the shortest paths.

 */

 import java.util.*;

 public class MaxFlow {

    /**
     * edge in the graph, this edge points to a vertex and contains the capacity remaining on it,
     * it also has the index of reverse edge in the adj list of the vertex it is pointing to
     */
    static class Edge {
        int to;         // the dest vertex
        long capacity;  // capacity of edge
        int rev;        // index of rev edge

        public Edge(int to, long capacity, int rev) {
            this.to = to;
            this.capacity = capacity;
            this.rev = rev;
        }
    }

    // num of vertices in the graph
    int n;
    // adj list represting the graph
    List<List<Edge>> graph;

    /**
     * initialising a graph with the vertices given
     */
    public MaxFlow(int n) {
        this.n = n;
        graph = new ArrayList<>(n);
        // initialising adjacency lists for each vertex
        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }
    }

    /**
     * here we add a directed edge to the graph with given capacity
     * and also a reverse edge with zero capacity for the residual graph
     */
    public void addEdge(int from, int to, long capacity) {
        // Add forward edge
        Edge forward = new Edge(to, capacity, graph.get(to).size());
        graph.get(from).add(forward);
        // Add reverse edge with 0 capacity
        Edge reverse = new Edge(from, 0, graph.get(from).size() - 1);
        graph.get(to).add(reverse);
    }

    /**
     * here we calculate the maximum flow from the source to the destination using the
     * Edmonds-Karp algorithm.
     */
    public long maxFlow(int s, int t) {
        long totalFlow = 0;

        int[] parent = new int[n];          // the previous node in path
        Edge[] parentEdge = new Edge[n];    // the edge used to reach each node

        // finding the paths as long as possible using the bfs
        while (bfs(s, t, parent, parentEdge)) {
            // in this process we will find the minimum residual capacity along the path found by BFS
            long pathFlow = Long.MAX_VALUE;
            int current = t;
            while (current != s) {
                Edge edge = parentEdge[current];
                pathFlow = Math.min(pathFlow, edge.capacity);
                current = parent[current];
            }

            // this computed path flow will be added to the totalflow
            totalFlow += pathFlow;

            // updating the residual caps of edges and reverse edges
            current = t;
            while (current != s) {
                Edge edge = parentEdge[current];
                // decreasing the capacity of the forward edge
                edge.capacity -= pathFlow;
                // increasing the capacity of the reverse edge
                Edge reverseEdge = graph.get(edge.to).get(edge.rev);
                reverseEdge.capacity += pathFlow;
                current = parent[current];
            }
        }

        return totalFlow;
    }

    /**
     * bfs for finding the shortest path 
     */
    private boolean bfs(int s, int t, int[] parent, Edge[] parentEdge) {
   
        Arrays.fill(parent, -1);
        Arrays.fill(parentEdge, null);

        // queue for bfs
        Queue<Integer> queue = new LinkedList<>();
        queue.add(s);
        parent[s] = s; // the source is marked visited by setting its parent to itself

        while (!queue.isEmpty()) {
            int u = queue.poll();

            // iterating over all the edges
            for (Edge edge : graph.get(u)) {
                // if the vertex is non-visited and the edge has remaining capacity
                if (parent[edge.to] == -1 && edge.capacity > 0) {
                    parent[edge.to] = u;          // set parent of the vertex
                    parentEdge[edge.to] = edge;    // store edge used to reach it
                    queue.add(edge.to);            // and add the vertex to the queue

                    // if reached the dest vertex, stop search
                    if (edge.to == t) {
                        return true;
                    }
                }
            }
        }

        // if no path is found then we will simply return false
        return false;
    }

    public static void main(String[] args) {

        if (args.length != 2) {
            System.err.println("Usage: java MaxFlow <source> <dest>");
            return;
        }

        int source = Integer.parseInt(args[0]);
        int dest = Integer.parseInt(args[1]);

        Scanner scanner = new Scanner(System.in);

        if (!scanner.hasNextInt()) {
            System.err.println("please enter number of vertices");
            return;
        }
        int numVertices = scanner.nextInt();

        if (!scanner.hasNextInt()) {
            System.err.println("please enter number of edges");
            return;
        }
        int numEdges = scanner.nextInt();

        MaxFlow maxFlowSolver = new MaxFlow(numVertices);

        for (int i = 0; i < numEdges; i++) {
            if (!scanner.hasNextInt()) {
                System.err.println("edge start vertex expected");
                return;
            }
            int from = scanner.nextInt();

            if (!scanner.hasNextInt()) {
                System.err.println("edge end vertex expected");
                return;
            }
            int to = scanner.nextInt();

            if (!scanner.hasNextLong()) {
                System.err.println("edge capacity expected");
                return;
            }
            long capacity = scanner.nextLong();

            if (from < 0 || from >= numVertices || to < 0 || to >= numVertices) {
                System.err.println("vertex index in edge " + from + " -> " + to);
                return;
            }

            maxFlowSolver.addEdge(from, to, capacity);
        }

        long maxFlow = maxFlowSolver.maxFlow(source, dest);

        System.out.println("Max flow " + maxFlow);

        scanner.close();
    }
}

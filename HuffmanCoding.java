/*
 * 
 * Title :- Implementation of Huffman Coding.
 * 
 * Name : Ronit Chandresh Virwani (B01099810)
 * Date: 10-14-2024

 * In this implementation we will build a Huffman tree using a custom binary heap-based priority queue to efficiently manage and combine letter frequencies.
 * In this approach we try to repeatedly extract the two least frequent nodes, merge them into a new node, and reinsert it into the queue until only the root node remains, ensuring optimal bit assignments
 * The program takes inputs of letter frequencies, then it constructs the Huffman tree, and finallu traverses it to calculate the total number of bits required for encoding, and outputs the final bit count.
 * PseduoCode Referred From The Book "Introduction to Algorithms" :-
 
 * HUFFMAN(C)

 *  n = |C|
 *  Q = C
 *  for i = 1 to n - 1
 *    allocate a new node z 
 *    x = EXTRACT-MIN(Q)
 *    y = EXTRACT-MIN(Q)
 *    z.left = x
 *    z.right = y
 *    z.freq = x.freq + y.freq
 *    INSERT(Q.z)
 *  return EXTRACT-MIN(Q) // the root of the tree is the only node left
 */


import java.util.Scanner;

// using this class we will represent each node in the Huffman tree, it will have 3 internal nodes : root node, left child and right child
class HuffmanNode {
    int freq; // frequency of the node
    char letter; // the letter it represents, if it's a root node
    HuffmanNode left; // left child
    HuffmanNode right; // right child

    // root node constructor
    public HuffmanNode(int freq, char letter) {
        this.freq = freq;
        this.letter = letter;
        this.left = null;
        this.right = null;
    }

    // internal nodes constructor 
    public HuffmanNode(int freq, HuffmanNode left, HuffmanNode right) {
        this.freq = freq;
        this.letter = '\0'; // this represents that there is no specific letter for internal nodes
        this.left = left;
        this.right = right;
    }

    // implemented a check for checking if it is a root node 
    public boolean isLeaf() {
        return this.left == null && this.right == null;
    }
}

// implementing the priority queue using a binary heap
class PriorityQueue {
    private HuffmanNode[] heap; // initialising an array to store heap elements
    private int size; // denotes the current number of elements in the heap

    // initializing the heap with a maximum size
    public PriorityQueue(int maxSize) {
        heap = new HuffmanNode[maxSize + 1]; 
        size = 0;
    }

    // inserting a node into the heap
    public void insert(HuffmanNode node) {
        size++;
        if (size >= heap.length) {
            // now here if we encounter that the heap array is full, then we will resize it
            resizeHeap();
        }
        heap[size] = node; // we will place the new node at the end
        swim(size); // adjust to maintain heap property
    }

    // Method to extract the node with the minimum frequency
    public HuffmanNode extractMin() {
        if (size == 0) {
            return null; // heap is empty
        }
        HuffmanNode min = heap[1]; // the root of the heap has the minimum frequency
        heap[1] = heap[size]; // move the last node to the root
        heap[size] = null; // remove the last node
        size--;
        sink(1); // adjust to maintain heap property
        return min;
    }

    // a method to swim up the heap for maintaining the heap property
    private void swim(int k) {
        while (k > 1 && heap[k].freq < heap[k / 2].freq) {
            swap(k, k / 2); // swap with parent
            k = k / 2;
        }
    }

    // a method to sink down the heap for maintaining the heap property
    private void sink(int k) {
        while (2 * k <= size) { // condition where considering there is at least one child
            int j = 2 * k; // the left child
            // if there is a right child and it's smaller than left child, we will use the right child
            if (j < size && heap[j + 1].freq < heap[j].freq) {
                j++;
            }
            // if the current node is smaller than the smallest child, we will stop there
            if (heap[k].freq <= heap[j].freq) {
                break;
            }
            swap(k, j); // now swap with the smaller child
            k = j; 
        }
    }

    // a method to swap two nodes in the heap
    private void swap(int i, int j) {
        HuffmanNode temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }

    // a method to check if there is only one element left in the heap 
    public boolean isSingle() {
        return size == 1;
    }

    // a method to resize the heap array when it's full
    private void resizeHeap() {
        HuffmanNode[] newHeap = new HuffmanNode[heap.length * 2];
        for (int i = 0; i < heap.length; i++) {
            newHeap[i] = heap[i];
        }
        heap = newHeap;
    }
}

public class HuffmanCoding {
    // this is the mthod where we build the Huffman tree using the priority queue
    public static HuffmanNode buildHuffmanTree(int[] data) {
        int n = data.length;
        // created a priority queue with max possible nodes
        PriorityQueue pq = new PriorityQueue(2 * n);

        // inserted all letters as leaf nodes into the priority queue
        for (int i = 0; i < n; i++) {
            if (data[i] > 0) { // here we only consider letters with positive frequency
                char letter = (char) ('A' + i); // fetch the corresponding letter
                HuffmanNode node = new HuffmanNode(data[i], letter);
                pq.insert(node);
            }
        }

        // handling a case where there's only one unique letter
        if (countUniqueLetters(data) == 1) {
            // to ensure that at least one bit is used, we will duplicate the single node
            HuffmanNode onlyNode = pq.extractMin();
            HuffmanNode dummyNode = new HuffmanNode(0, '\0'); // we will initialise dummy node with zero frequency here
            HuffmanNode root = new HuffmanNode(onlyNode.freq + dummyNode.freq, onlyNode, dummyNode);
            pq.insert(root); // re inserting the root back
        }

        // here we build the huffman tree
        while (!pq.isSingle()) { // we wil continue until only one node is remaining
            // extracting the two nodes with the smallest frequencies
            HuffmanNode x = pq.extractMin();
            HuffmanNode y = pq.extractMin();

            // creating a new internal node with these two nodes as children
            HuffmanNode z = new HuffmanNode(x.freq + y.freq, x, y);

            // inserting the new node back into the priority queue
            pq.insert(z);
        }

        // the node that is left is our root node
        return pq.extractMin();
    }

    // counting the number of unique letters with frequency > 0
    public static int countUniqueLetters(int[] data) {
        int count = 0;
        for (int freq : data) {
            if (freq > 0) {
                count++;
            }
        }
        return count;
    }

    // calculating the total number of bits by traversing through the tree
    public static int calculateBits(HuffmanNode root, int depth) {
        // in case node is null, return 0
        if (root == null) {
            return 0;
        }

        // if it is a leaf node, calculate bits for this letter
        if (root.isLeaf()) {
            // if there's only one unique letter, will assign it 1 bit
            if (depth == 0) {
                return root.freq; // if depth will remain 0, so will assign 1 bit per occurrence
            }
            return root.freq * depth;
        }

        // calculating bits recursively for left and right subtrees
        int leftBits = calculateBits(root.left, depth + 1);
        int rightBits = calculateBits(root.right, depth + 1);
        return leftBits + rightBits;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        if (!scanner.hasNextInt()) { // checking if there's at least one integer
            System.out.println("0");
            scanner.close();
            return;
        }
        int n = scanner.nextInt(); 

        // validating the number of letters
        if (n <= 0 || n > 100) { 
            System.out.println("0"); // no bits needed if there are no letters or letters more than 100
            scanner.close();
            return;
        }

        int[] data = new int[n];

        for (int i = 0; i < n; i++) {
            if (scanner.hasNextInt()) {
                data[i] = scanner.nextInt();
                if (data[i] < 0) { // ensuring frequency is non-negative
                    data[i] = 0; // if negative frequencies then it will be taken as zero
                }
            } else {
                data[i] = 0; // if the input is missing, then we will treat frequency as zero
            }
        }
        scanner.close();

        // counting the number of unique letters with frequency > 0
        int uniqueLetters = countUniqueLetters(data);
        int totalFrequency = 0;
        for (int freq : data) {
            totalFrequency += freq;
        }

        // if there are no letters with positive frequency, the total bits will be 0
        if (uniqueLetters == 0) {
            System.out.println("0");
            return;
        }


        HuffmanNode root = buildHuffmanTree(data);

  
        int totalBits = calculateBits(root, 0);

        // if there's only one unique letter
        if (uniqueLetters == 1) {
            // then each occurrence requires 1 bit
            totalBits = totalFrequency;
        }


        System.out.println(totalBits);
    }
}

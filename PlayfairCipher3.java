import java.util.*;

public class PlayfairCipher3 {

    // Method to remove duplicate characters from the keyword
    private static String removeDuplicates(String keyword) {
        StringBuilder sb = new StringBuilder();
        Set<Character> seen = new HashSet<>();
        for (char c : keyword.toCharArray()) {
            if (c == 'j') c = 'i';  // Treat 'j' as 'i' in keyword for matrix setup
            if (!seen.contains(c)) {
                seen.add(c);
                sb.append(c);
            }
        }
        return sb.toString();
    }

    // Method to construct the Playfair matrix from the keyword
    private static char[][] createMatrix(String keyword) {
        char[][] matrix = new char[5][5];
        boolean[] alphabets = new boolean[26];
        int index = 0;

        for (char c : keyword.toCharArray()) {
            if (c == 'j') c = 'i'; // Convert 'j' to 'i' for matrix setup
            if (!alphabets[c - 'a']) {
                alphabets[c - 'a'] = true;
                matrix[index / 5][index % 5] = c;
                index++;
            }
        }

        // Fill matrix with remaining letters
        for (char c = 'a'; c <= 'z'; c++) {
            if (c == 'j') continue;  // Skip 'j' in matrix
            if (!alphabets[c - 'a']) {
                matrix[index / 5][index % 5] = c;
                index++;
                if (index >= 25) break;
            }
        }

        return matrix;
    }

    // // Helper method to format the input text for encryption
    // private static String formatPlainText(String text) {
    //     StringBuilder formatted = new StringBuilder();
    //     char prev = '\0'; // Track the previous character
    //     for (int i = 0; i < text.length(); i++) {
    //         char current = text.charAt(i);

    //         // Add 'x' between identical consecutive characters, or specifically between 'i' and 'j'
    //         if ((current == prev && current != 'x') || (prev == 'i' && current == 'j') || (prev == 'j' && current == 'i')) {
    //             formatted.append('x');
    //         }

    //         formatted.append(current);
    //         prev = current;
    //     }

    //     // Ensure the final text has an even number of characters for the cipher
    //     if (formatted.length() % 2 != 0) {
    //         formatted.append('x');  // Pad with 'x' if length is odd
    //     }
    //     return formatted.toString();
    // }

    // private static String formatPlainText(String text) {
    //     StringBuilder formatted = new StringBuilder();
    //     for (int i = 0; i < text.length(); i += 2) {
    //         formatted.append(text.charAt(i));
    //         if (i + 1 < text.length()) {
    //             if (text.charAt(i) == text.charAt(i + 1)) {
    //                 formatted.append('x'); // Add filler if letters are the same
    //             }
    //             formatted.append(text.charAt(i + 1));
    //         }
    //     }
    //     if (formatted.length() % 2 != 0) {
    //         formatted.append('x'); // Add filler if text length is odd
    //     }
    //     return formatted.toString();
    // }

    private static String formatPlainText(String text) {
        StringBuilder formatted = new StringBuilder();
        char prev = '\0'; // Initialize previous character to null character
    
        for (int i = 0; i < text.length(); i++) {
            char current = text.charAt(i);
    
            // Handle the insertion of 'x' between identical consecutive characters
            if (i > 0 && current == prev) {
                formatted.append('x');
            }
    
            // Handle the special case for 'i' and 'j'
            if (i > 0 && ((prev == 'i' && current == 'j') || (prev == 'j' && current == 'i'))) {
                if (formatted.charAt(formatted.length() - 1) != 'x') { // Avoid inserting multiple 'x's
                    formatted.append('x');
                }
            }
    
            formatted.append(current);
            prev = current; // Update previous character for the next iteration
        }
    
        // Ensure the string has an even number of characters
        if (formatted.length() % 2 != 0) {
            formatted.append('x'); // Append 'x' if length is odd
        }
    
        return formatted.toString();
    }
    
    
    
    


    // Method to encrypt or decrypt the text
    private static String processText(String text, char[][] matrix, boolean encrypt) {
        StringBuilder processedText = new StringBuilder();
        for (int i = 0; i < text.length(); i += 2) {
            char a = text.charAt(i);
            char b = text.charAt(i + 1);
            int[] posA = findPosition(matrix, a == 'j' ? 'i' : a);
            int[] posB = findPosition(matrix, b == 'j' ? 'i' : b);

            if (posA[0] == posB[0]) { // Same row
                processedText.append(matrix[posA[0]][(posA[1] + (encrypt ? 1 : 4)) % 5]);
                processedText.append(matrix[posB[0]][(posB[1] + (encrypt ? 1 : 4)) % 5]);
            } else if (posA[1] == posB[1]) { // Same column
                processedText.append(matrix[(posA[0] + (encrypt ? 1 : 4)) % 5][posA[1]]);
                processedText.append(matrix[(posB[0] + (encrypt ? 1 : 4)) % 5][posB[1]]);
            } else { // Rectangle
                processedText.append(matrix[posA[0]][posB[1]]);
                processedText.append(matrix[posB[0]][posA[1]]);
            }
        }
        return processedText.toString();
    }

    // Helper method to find character position in matrix
    private static int[] findPosition(char[][] matrix, char c) {
        if (c == 'j') c = 'i'; // Convert 'j' to 'i' for matrix position finding
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                if (matrix[row][col] == c) {
                    return new int[]{row, col};
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java PlayfairCipher <keyword> <enc/dec>");
            return;
        }

        String keyword = args[0].toLowerCase();
        boolean encryptMode = args[1].equals("1");
        
        String uniqueKeyword = removeDuplicates(keyword);
        System.out.println("Removing duplicates from keyword: " + uniqueKeyword);

        char[][] matrix = createMatrix(uniqueKeyword);
        System.out.println("The playfair matrix is:");
        for (char[] row : matrix) {
            for (char c : row) {
                System.out.print(c);
            }
            System.out.print(" ");
        }
        System.out.println();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the " + (encryptMode ? "plaintext" : "ciphertext") + ":");
        String inputText = scanner.nextLine().toLowerCase();
        String formattedText = formatPlainText(inputText);
        System.out.println((encryptMode ? "Plaintext with fillers: " : "Ciphertext with fillers: ") + formattedText);

        String resultText = processText(formattedText, matrix, encryptMode);
        System.out.println((encryptMode ? "Encrypted message: " : "Decrypted message: ") + resultText);

        scanner.close();
    }
}

import java.util.*;

public class PlayFairCipherFinal {

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

    private static String formatPlainText(String text) {
        StringBuilder formatted = new StringBuilder();
        int i = 0;
    
        while (i < text.length()) {
            // Current character
            char current = text.charAt(i);
    
            // Append the current character to the result
            formatted.append(current);
    
            // Check if there is a next character
            if (i + 1 < text.length()) {
                char next = text.charAt(i + 1);
    
                // Insert 'x' if the current and next characters are the same
                if (current == next) {
                    formatted.append('x');
                    // If inserting 'x' between duplicates, still append the next character
                } else if ((current == 'i' && next == 'j') || (current == 'j' && next == 'i')) {
                    // Handle specific case for consecutive 'i' and 'j'
                    formatted.append('x');
                }
    
                // Append next character unless it's a duplicate that was just handled
                if (current != next || (current == next && i + 2 == text.length())) {
                    formatted.append(next);
                    i++; // Skip the next character as it's now processed
                }
            }
    
            i++; // Move to the next character
        }
    
        // Check the last character processing
        if (formatted.length() % 2 != 0) {
            formatted.append('x'); // Append 'x' if the length is odd
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
                System.out.print(c + " ");  // Print each character followed by a space
            }
            System.out.println();  // Move to the next line after each row
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

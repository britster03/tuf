import java.util.*;

public class PlayfairCipher2 {

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

    // private static String formatPlainText(String text) {
    //     StringBuilder formatted = new StringBuilder();
    //     int i = 0;
    //     while (i < text.length()) {
    //         // Append the current character
    //         formatted.append(text.charAt(i));
    
    //         // Check if there's a next character to process
    //         if (i + 1 < text.length()) {
    //             char current = text.charAt(i);
    //             char next = text.charAt(i + 1);
    
    //             // Add 'x' if the current and next characters are the same
    //             if (current == next) {
    //                 formatted.append('x');
    //             }
    //             // Special handling for 'i' and 'j'
    //             else if ((current == 'i' && next == 'j') || (current == 'j' && next == 'i')) {
    //                 formatted.append('x');
    //                 formatted.append(next);
    //                 i++; // Skip the next character as it's already processed
    //                 if (i + 1 == text.length() - 1) { // Check if there is exactly one character left after this pair
    //                     formatted.append(text.charAt(i + 1)); // Append the last single character
    //                     break;
    //                 }
    //             } else {
    //                 // Append the next character if no 'x' was needed
    //                 formatted.append(next);
    //             }
    //             i += 2; // Move to the next pair
    //         } else {
    //             // If no next character, break the loop
    //             break;
    //         }
    //     }
    
    //     // Ensure the final text has an even number of characters for the cipher
    //     if (formatted.length() % 2 != 0) {
    //         formatted.append('x'); // Append 'x' if the length is odd
    //     }
    
    //     return formatted.toString();
    // }

    // private static String formatPlainText(String text) {
    //     StringBuilder formatted = new StringBuilder();
    //     int i = 0;
    //     while (i < text.length()) {
    //         // Append the current character
    //         char current = text.charAt(i);
    //         formatted.append(current);
    
    //         // Check if there's a next character to process
    //         if (i + 1 < text.length()) {
    //             char next = text.charAt(i + 1);
    
    //             // Handle identical characters or 'i' and 'j' specifically
    //             if (current == next || (current == 'i' && next == 'j') || (current == 'j' && next == 'i')) {
    //                 formatted.append('x');
    //             }
    
    //             // Check to append next character if not the end of processing
    //             if (i + 1 < text.length() - 1 || current != next) {
    //                 formatted.append(next);
    //             }
    //             i += 2; // Move to the next pair
    //         } else {
    //             // If it's the last character and no duplication or 'ij'/'ji' condition, just break
    //             break;
    //         }
    //     }
    
    //     // No longer append 'x' at the end unconditionally, only when needed as part of processing
    //     return formatted.toString();
    // }
    
    

    // private static String formatPlainText(String text) {
    //     StringBuilder formatted = new StringBuilder();
    //     int i = 0;
    //     while (i < text.length()) {
    //         char current = text.charAt(i);
    //         char next = (i + 1 < text.length()) ? text.charAt(i + 1) : '\0'; // Use '\0' to signify no next character
    
    //         formatted.append(current);
    
    //         // Handle identical characters or 'i' and 'j' specifically
    //         if (current == next) {
    //             formatted.append('x'); // Insert 'x' for duplicate characters
    //         } else if ((current == 'i' && next == 'j') || (current == 'j' && next == 'i')) {
    //             formatted.append('x'); // Specific case for 'i' and 'j'
    //         }
    
    //         // Move to the next character, avoiding out-of-bounds access
    //         if (next != '\0') {
    //             if (current == next || (current == 'i' && next == 'j') || (current == 'j' && next == 'i')) {
    //                 // If we added an 'x', append next character now
    //                 formatted.append(next);
    //                 i++; // Skip the next character as it's already processed
    //             }
    //         }
    
    //         i++; // General increment to proceed to the next character
    //     }
    
    //     // Check if we need to append an 'x' for an odd number of characters
    //     if (formatted.length() % 2 != 0) {
    //         formatted.append('x'); // Append 'x' if the string length is odd
    //     }
    
    //     return formatted.toString();
    // }
    
    // private static String formatPlainText(String text) {
    //     StringBuilder formatted = new StringBuilder();
    //     int i = 0;
    
    //     // Loop over each character in the text
    //     while (i < text.length()) {
    //         char current = text.charAt(i);
    //         char next = (i + 1 < text.length()) ? text.charAt(i + 1) : '\0'; // Check the next character
    
    //         formatted.append(current);
    
    //         // Check conditions for inserting 'x':
    //         if (current == next) {
    //             // Insert 'x' between two identical characters
    //             formatted.append('x');
    //             i++; // Skip the next character as it's the same
    //         } else if ((current == 'i' && next == 'j') || (current == 'j' && next == 'i')) {
    //             // Handle specific case for consecutive 'i' and 'j'
    //             formatted.append('x');
    //         }
    
    //         // Move to the next character if it hasn't been skipped
    //         if (next != '\0' && current != next && !((current == 'i' && next == 'j') || (current == 'j' && next == 'i'))) {
    //             formatted.append(next);
    //             i++; // Increment to skip the next character as it has been processed
    //         }
    
    //         i++; // Move to the next set of characters
    //     }
    
    //     // Ensure the string has an even number of characters for the cipher
    //     if (formatted.length() % 2 != 0) {
    //         formatted.append('x'); // Append 'x' if the length is odd
    //     }
    
    //     return formatted.toString();
    // }

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

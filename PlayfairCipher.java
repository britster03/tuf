import java.util.*;

public class PlayfairCipher {
    
    // Method to remove duplicate characters from the keyword
    private static String removeDuplicates(String keyword) {
        StringBuilder sb = new StringBuilder();
        Set<Character> seen = new HashSet<>();
        for (char c : keyword.toCharArray()) {
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

        // Fill matrix with keyword
        for (char c : keyword.toCharArray()) {
            if (!alphabets[c - 'a']) {
                alphabets[c - 'a'] = true;
                matrix[index / 5][index % 5] = c;
                index++;
            }
        }

        // Fill matrix with remaining letters
        for (char c = 'a'; c <= 'z'; c++) {
            if (c == 'j') continue;  // Skip 'j', it's treated as 'i'
            if (!alphabets[c - 'a']) {
                matrix[index / 5][index % 5] = c;
                index++;
            }
        }

        return matrix;
    }

    // Helper method to format the input text for encryption
    private static String formatPlainText(String text) {
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < text.length(); i += 2) {
            formatted.append(text.charAt(i));
            if (i + 1 < text.length()) {
                if (text.charAt(i) == text.charAt(i + 1)) {
                    formatted.append('x'); // Add filler if letters are the same
                }
                formatted.append(text.charAt(i + 1));
            }
        }
        if (formatted.length() % 2 != 0) {
            formatted.append('x'); // Add filler if text length is odd
        }
        return formatted.toString();
    }

    // Method to encrypt the plaintext
    private static String encrypt(String text, char[][] matrix) {
        text = formatPlainText(text);
        StringBuilder cipherText = new StringBuilder();

        for (int i = 0; i < text.length(); i += 2) {
            char a = text.charAt(i);
            char b = text.charAt(i + 1);
            int[] posA = findPosition(matrix, a);
            int[] posB = findPosition(matrix, b);

            if (posA[0] == posB[0]) { // Same row
                cipherText.append(matrix[posA[0]][(posA[1] + 1) % 5]);
                cipherText.append(matrix[posB[0]][(posB[1] + 1) % 5]);
            } else if (posA[1] == posB[1]) { // Same column
                cipherText.append(matrix[(posA[0] + 1) % 5][posA[1]]);
                cipherText.append(matrix[(posB[0] + 1) % 5][posB[1]]);
            } else { // Rectangle
                cipherText.append(matrix[posA[0]][posB[1]]);
                cipherText.append(matrix[posB[0]][posA[1]]);
            }
        }

        return cipherText.toString();
    }

    // Method to decrypt the ciphertext
    private static String decrypt(String text, char[][] matrix) {
        StringBuilder plainText = new StringBuilder();

        for (int i = 0; i < text.length(); i += 2) {
            char a = text.charAt(i);
            char b = text.charAt(i + 1);
            int[] posA = findPosition(matrix, a);
            int[] posB = findPosition(matrix, b);

            if (posA[0] == posB[0]) { // Same row
                plainText.append(matrix[posA[0]][(posA[1] + 4) % 5]);
                plainText.append(matrix[posB[0]][(posB[1] + 4) % 5]);
            } else if (posA[1] == posB[1]) { // Same column
                plainText.append(matrix[(posA[0] + 4) % 5][posA[1]]);
                plainText.append(matrix[(posB[0] + 4) % 5][posB[1]]);
            } else { // Rectangle
                plainText.append(matrix[posA[0]][posB[1]]);
                plainText.append(matrix[posB[0]][posA[1]]);
            }
        }

        return plainText.toString().replace("x", ""); // Remove filler 'x' used in encryption
    }

    // Helper method to find character position in matrix
    private static int[] findPosition(char[][] matrix, char c) {
        if (c == 'j') c = 'i'; // Treat 'j' as 'i'
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
        
        // Step 1: Remove duplicates
        String uniqueKeyword = removeDuplicates(keyword);
        System.out.println("Removing duplicates from keyword: " + uniqueKeyword);

        // Step 2: Create the playfair matrix
        char[][] matrix = createMatrix(uniqueKeyword);
        System.out.println("The playfair matrix is:");
        for (char[] row : matrix) {
            for (char c : row) {
                System.out.print(c);
            }
            System.out.print(" ");
        }
        System.out.println();

        // Step 3: Encrypt or decrypt based on user input
        Scanner scanner = new Scanner(System.in);
        if (encryptMode) {
            System.out.println("Please enter the plaintext:");
            String plaintext = scanner.nextLine().toLowerCase();
            String ciphertext = encrypt(plaintext, matrix);
            System.out.println("Encrypted message: " + ciphertext);
        } else {
            System.out.println("Please enter the ciphertext:");
            String ciphertext = scanner.nextLine().toLowerCase();
            String plaintext = decrypt(ciphertext, matrix);
            System.out.println("Decrypted message: " + plaintext);
        }
        scanner.close();
    }
}

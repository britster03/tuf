import java.util.*;

public class PlayFairCipherHmn {

    // Removes duplicate characters from the keyword and handles 'j' as 'i'
    private static String refineKeyword(String keyword) {
        StringBuilder noDuplicates = new StringBuilder();
        Set<Character> seenChars = new HashSet<>();
        for (char character : keyword.toCharArray()) {
            if (character == 'j') character = 'i'; // Treat 'j' as 'i' for the Playfair cipher matrix
            if (!seenChars.contains(character)) {
                seenChars.add(character);
                noDuplicates.append(character);
            }
        }
        return noDuplicates.toString();
    }

    // Constructs the 5x5 Playfair cipher matrix from the refined keyword
    private static char[][] createCipherMatrix(String keyword) {
        char[][] matrix = new char[5][5];
        boolean[] usedLetters = new boolean[26];
        int charIndex = 0;

        // Insert keyword characters, ensuring no duplicates and handling 'j' as 'i'
        for (char ch : keyword.toCharArray()) {
            if (ch == 'j') ch = 'i'; 
            if (!usedLetters[ch - 'a']) {
                usedLetters[ch - 'a'] = true;
                matrix[charIndex / 5][charIndex % 5] = ch;
                charIndex++;
            }
        }

        // Fill the remaining spaces in the matrix with unused letters
        for (char ch = 'a'; ch <= 'z'; ch++) {
            if (ch == 'j') continue; // Exclude 'j' from the matrix
            if (!usedLetters[ch - 'a']) {
                matrix[charIndex / 5][charIndex % 5] = ch;
                charIndex++;
                if (charIndex >= 25) break; // Only fill the matrix up to 25 cells
            }
        }

        return matrix;
    }

    // Formats the plaintext by adding 'x' between repeated characters or handling 'i' and 'j'
    private static String prepareText(String text) {
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

                // Handle 'j' as 'i' only during cipher processing, not in formatting
                // Therefore, do not convert 'j' to 'i' here

                // Insert 'x' if the current and next characters are the same
                if (current == next) {
                    formatted.append('x');
                } else if ((current == 'i' && next == 'j') || (current == 'j' && next == 'i')) {
                    // Handle specific case for consecutive 'i' and 'j'
                    formatted.append('x');
                } else {
                    // Append next character unless it's a duplicate that was just handled
                    formatted.append(next);
                    i++; // Skip the next character as it's now processed
                }
            }

            i++; // Move to the next character
        }

        // Check the length and append 'x' if necessary
        if (formatted.length() % 2 != 0) {
            formatted.append('x'); // Append 'x' if the length is odd
        }

        return formatted.toString();
    }

    // Encrypts or decrypts text based on the Playfair cipher rules
    private static String cipherText(String text, char[][] matrix, boolean encrypt) {
        StringBuilder processedText = new StringBuilder();
        for (int i = 0; i < text.length(); i += 2) {
            char char1 = text.charAt(i);
            char char2 = text.charAt(i + 1);

            int[] pos1 = findCharInMatrix(matrix, char1);
            int[] pos2 = findCharInMatrix(matrix, char2);

            if (pos1[0] == pos2[0]) { // Same row
                processedText.append(matrix[pos1[0]][(pos1[1] + (encrypt ? 1 : 4)) % 5]);
                processedText.append(matrix[pos2[0]][(pos2[1] + (encrypt ? 1 : 4)) % 5]);
            } else if (pos1[1] == pos2[1]) { // Same column
                processedText.append(matrix[(pos1[0] + (encrypt ? 1 : 4)) % 5][pos1[1]]);
                processedText.append(matrix[(pos2[0] + (encrypt ? 1 : 4)) % 5][pos2[1]]);
            } else { // Forming a rectangle
                processedText.append(matrix[pos1[0]][pos2[1]]);
                processedText.append(matrix[pos2[0]][pos1[1]]);
            }
        }
        return processedText.toString();
    }

    // Finds the position of a character in the cipher matrix
    private static int[] findCharInMatrix(char[][] matrix, char c) {
        if (c == 'j') c = 'i'; // Convert 'j' to 'i' for lookup
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
            System.out.println("Usage: java PlayFairCipher <keyword> <1 for encrypt/0 for decrypt>");
            return;
        }

        String keyword = args[0].toLowerCase();
        boolean encryptMode = args[1].equals("1");

        String uniqueKeyword = refineKeyword(keyword);
        System.out.println("Refined keyword: " + uniqueKeyword);

        char[][] matrix = createCipherMatrix(uniqueKeyword);
        System.out.println("Constructed Playfair Matrix:");
        for (char[] row : matrix) {
            for (char ch : row) {
                if (ch == 'i') {
                    System.out.print("ij ");
                } else {
                    System.out.print(ch + " ");
                }
            }
            System.out.println();
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the " + (encryptMode ? "plaintext" : "ciphertext") + ":");
        String inputText = scanner.nextLine().toLowerCase().replaceAll("[^a-zA-Z]", "");

        String formattedText;
        if (encryptMode) {
            formattedText = prepareText(inputText);
            System.out.println("Formatted text: " + formattedText);
        } else {
            formattedText = inputText;
            System.out.println("Formatted text (unchanged): " + formattedText);
        }

        String resultText = cipherText(formattedText, matrix, encryptMode);
        System.out.println("Resulting text: " + resultText);

        scanner.close();
    }
}

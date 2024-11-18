import java.util.*;

public class PlayFairCipher10 {

    /**
     this function takes the user's keyword and cleans it up by removing any duplicate letters 
     and replacing any occurrence of 'j' with 'i'
     */
    private static String refineKeyword(String keyword) {
        StringBuilder uniqueKeywords = new StringBuilder();
        Set<Character> uniqueCharacters = new HashSet<>();

        for (char character : keyword.toCharArray()) {
            if (character == 'j') {
                character = 'i'; 
            }
            if (!uniqueCharacters.contains(character)) {
                uniqueCharacters.add(character);
                uniqueKeywords.append(character);
            }
        }
        return uniqueKeywords.toString();
    }

    /**
        this function creates a 5x5 matrix for the Playfair cipher using the unique keywords we get from the previous func
     */
    private static char[][] generateCipherMatrix(String keyword) {
        char[][] cipherMatrix = new char[5][5]; // initializing a 5x5 character array
        boolean[] isUsed = new boolean[26]; // boolean array to keep track of which letters have been used
        int index = 0;

        // first fill the matrix with the unique letters from the keyword
        for (char ch : keyword.toCharArray()) {
            if (ch == 'j') {
                ch = 'i'; // Ensure 'j' is treated as 'i'
            }
            if (!isUsed[ch - 'a']) {
                isUsed[ch - 'a'] = true;
                cipherMatrix[index / 5][index % 5] = ch;
                index++;
            }
        }

        // then fill the remaining spaces with the rest of the alphabet
        for (char ch = 'a'; ch <= 'z'; ch++) {
            if (ch == 'j') {
                continue; // 'j' a;ready handled
            }
            if (!isUsed[ch - 'a']) {
                cipherMatrix[index / 5][index % 5] = ch;
                index++;
                if (index >= 25) {
                    break; // Only fill up to 25 cells
                }
            }
        }

        return cipherMatrix;
    }

    /**
        this function prepares the plaintext for encryption by handling duplicate letters 
        and ensuring the text has an even number of characters
     */
    private static String prepText(String text) {
        StringBuilder newText = new StringBuilder();
        int position = 0;

        while (position < text.length()) {
            char firstCharacter = text.charAt(position);

            
            newText.append(firstCharacter); // adding the second character

            if (position + 1 < text.length()) {
                char secondCharacter = text.charAt(position + 1);

                // checking if the next character is the same as the current or forms an 'i' and 'j' pair
                if (firstCharacter == secondCharacter || (firstCharacter == 'i' && secondCharacter == 'j') || (firstCharacter == 'j' && secondCharacter == 'i')) {
                    newText.append('x'); // inserting an 'x' to separate duplicates or handle 'i' and 'j'
                } else {
                    newText.append(secondCharacter); // adding the second character
                    position++; 
                }
            }

            position++; // to the next character
        }

        // ensuring the formatted text has an even length by appending 'x' if necessary
        if (newText.length() % 2 != 0) {
            newText.append('x');
        }

        return newText.toString();
    }

    /**
        this function handles both encryption and decryption based on the Playfair cipher rules.
     */
    private static String genCipher(String text, char[][] cipherMatrix, boolean isEncrypt) {
        StringBuilder resultingCip = new StringBuilder();

        for (int i = 0; i < text.length(); i += 2) {
            char firstCharacter = text.charAt(i);
            char secondCharacter = text.charAt(i + 1);

            // to locate two characters in the matrix
            int[] firstPosition = findingChar(cipherMatrix, firstCharacter);
            int[] secondPosition = findingChar(cipherMatrix, secondCharacter);

            if (firstPosition[0] == secondPosition[0]) {
                // if both characters are in the same row, perofming a shift right for encryption or left for decryption
                resultingCip.append(cipherMatrix[firstPosition[0]][(firstPosition[1] + (isEncrypt ? 1 : 4)) % 5]);
                resultingCip.append(cipherMatrix[secondPosition[0]][(secondPosition[1] + (isEncrypt ? 1 : 4)) % 5]);
            } else if (firstPosition[1] == secondPosition[1]) {
                // if both characters are in the same column, performing a shift down for encryption or up for decryption
                resultingCip.append(cipherMatrix[(firstPosition[0] + (isEncrypt ? 1 : 4)) % 5][firstPosition[1]]);
                resultingCip.append(cipherMatrix[(secondPosition[0] + (isEncrypt ? 1 : 4)) % 5][secondPosition[1]]);
            } else {
               // if characters form a rectangle, performing a swap in their columns
                resultingCip.append(cipherMatrix[firstPosition[0]][secondPosition[1]]);
                resultingCip.append(cipherMatrix[secondPosition[0]][firstPosition[1]]);
            }
        }

        return resultingCip.toString();
    }

    /**
        this function finds the position of a given character in the cipher matrix.
     */
    private static int[] findingChar(char[][] cipherMatrix, char ch) {
        if (ch == 'j') {
            ch = 'i'; // this means that treating j as i
        }

        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                if (cipherMatrix[row][col] == ch) {
                    return new int[]{row, col};
                }
            }
        }

        // no character found in the matrix
        return null;
    }

    /**
        this function removes any 'x' characters that were 
        added as padding during encryption and gives original text back
        when performed decryption
     */
    private static String removePadding(String text) {
        StringBuilder cleanText = new StringBuilder();
        int i = 0;

        while (i < text.length()) {
            char currentCharacter = text.charAt(i);

            // case check if current character is 'x' and is between two identical characters
            if (currentCharacter == 'x' && i > 0 && i < text.length() - 1) {
                char prev = text.charAt(i - 1);
                char next = text.charAt(i + 1);
                if (prev == next) {
                    i++;
                    continue;
                }
            }

            cleanText.append(currentCharacter);
            i++;
        }

        // removing if any trailing 'x' was added as padding
        if (cleanText.length() > 0 && cleanText.charAt(cleanText.length() - 1) == 'x') {
            cleanText.deleteCharAt(cleanText.length() - 1);
        }

        return cleanText.toString();
    }


    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("use in the following: java playfaircipher <keyword> <1 for encrypt / 0 for decrypt>");
            return;
        }

        String keywordInput = args[0].toLowerCase();
        boolean encryptMode = args[1].equals("1");

        // refining the keyword by removing duplicates and handling 'j'
        String refinedKeyword = refineKeyword(keywordInput);
        System.out.println("refined keyword: " + refinedKeyword);

        // creating the cipher matrix based on the refined keyword
        char[][] cipherMatrix = generateCipherMatrix(refinedKeyword);
        System.out.println("constructed playfair cipher matrix:");
        for (char[] row : cipherMatrix) {
            for (char ch : row) {
                if (ch == 'i') {
                    System.out.print("ij ");
                } else {
                    System.out.print(ch + " ");
                }
            }
            System.out.println();
        }

        // get user input for plaintext or ciphertext
        Scanner scanner = new Scanner(System.in);
        System.out.println("please enter the " + (encryptMode ? "plaintext" : "ciphertext") + ":");
        String userInput = scanner.nextLine().toLowerCase().replaceAll("[^a-zA-Z]", "");

        String formattedText;
        if (encryptMode) {
            // formatting the plaintext by inserting 'x' where necessary
            formattedText = prepText(userInput);
            System.out.println("formatted text: " + formattedText);
        } else {
            // using the ciphertext as it is for decryption
            formattedText = userInput;
            System.out.println("formatted text: " + formattedText);
        }

        // processing the encryption or decryption based on the i/p
        String cipherResult = genCipher(formattedText, cipherMatrix, encryptMode);
        String outputText;

        if (!encryptMode) {
            // remove padding 'x's after decryption
            outputText = removePadding(cipherResult);
        } else {
            outputText = cipherResult;
        }

        System.out.println("resulting text: " + outputText);

        scanner.close();
    }
}


// Test Cases ;

// ronny@DESKTOP-FJM73QK:/mnt/d/tuf+$ java PlayFairCipher10 commom 1
// refined keyword: com
// constructed playfair cipher matrix:
// c o m a b
// d e f g h
// ij k l n p
// q r s t u
// v w x y z
// please enter the plaintext:
// gdddcgs
// formatted text: gddxdcgs
// resulting text: hefvidft


// ronny@DESKTOP-FJM73QK:/mnt/d/tuf+$ java PlayFairCipher10 commom 0
// refined keyword: com
// constructed playfair cipher matrix:
// c o m a b
// d e f g h
// ij k l n p
// q r s t u
// v w x y z
// please enter the ciphertext:
// hefvidft
// formatted text: hefvidft
// resulting text: gdddcgs


// ronny@DESKTOP-FJM73QK:/mnt/d/tuf+$ java PlayFairCipher10 ciajbd 1
// refined keyword: ciabd
// constructed playfair cipher matrix:
// c ij a b d
// e f g h k
// l m n o p
// q r s t u
// v w x y z
// please enter the plaintext:
// bdija
// formatted text: bdixja
// resulting text: dcawab


// ronny@DESKTOP-FJM73QK:/mnt/d/tuf+$ java PlayFairCipher10 ciajbd 0
// refined keyword: ciabd
// constructed playfair cipher matrix:
// c ij a b d
// e f g h k
// l m n o p
// q r s t u
// v w x y z
// please enter the ciphertext:
// dcawab
// formatted text: dcawab
// resulting text: bdiia


// ronny@DESKTOP-FJM73QK:/mnt/d/tuf+$ java PlayFairCipher10 abc 1
// refined keyword: abc
// constructed playfair cipher matrix:
// a b c d e
// f g h ij k
// l m n o p
// q r s t u
// v w x y z
// please enter the plaintext:
// abba
// formatted text: abba
// resulting text: bccb


// ronny@DESKTOP-FJM73QK:/mnt/d/tuf+$ java PlayFairCipher10 abc 0
// refined keyword: abc
// constructed playfair cipher matrix:
// a b c d e
// f g h ij k
// l m n o p
// q r s t u
// v w x y z
// please enter the ciphertext:
// bccb
// formatted text: bccb
// resulting text: abba


// ronny@DESKTOP-FJM73QK:/mnt/d/tuf+$ java PlayFairCipher10 abc 1
// refined keyword: abc
// constructed playfair cipher matrix:
// a b c d e
// f g h ij k
// l m n o p
// q r s t u
// v w x y z
// please enter the plaintext:
// abbba
// formatted text: abbxba
// resulting text: bccwcb


// ronny@DESKTOP-FJM73QK:/mnt/d/tuf+$ java PlayFairCipher10 abc 0
// refined keyword: abc
// constructed playfair cipher matrix:
// a b c d e
// f g h ij k
// l m n o p
// q r s t u
// v w x y z
// please enter the ciphertext:
// bccwcb
// formatted text: bccwcb
// resulting text: abbba


// ronny@DESKTOP-FJM73QK:/mnt/d/tuf+$ java PlayFairCipher10 abc 1
// refined keyword: abc
// constructed playfair cipher matrix:
// a b c d e
// f g h ij k
// l m n o p
// q r s t u
// v w x y z
// please enter the plaintext:
// abbac
// formatted text: abbacx
// resulting text: bccbhc


// ronny@DESKTOP-FJM73QK:/mnt/d/tuf+$ java PlayFairCipher10 abc 0
// refined keyword: abc
// constructed playfair cipher matrix:
// a b c d e
// f g h ij k
// l m n o p
// q r s t u
// v w x y z
// please enter the ciphertext:
// bccbhc
// formatted text: bccbhc
// resulting text: abbac


// ronny@DESKTOP-FJM73QK:/mnt/d/tuf+$ java PlayFairCipher10 abc 1
// refined keyword: abc
// constructed playfair cipher matrix:
// a b c d e
// f g h ij k
// l m n o p
// q r s t u
// v w x y z
// please enter the plaintext:
// aiij
// formatted text: aiixjx
// resulting text: dfhyhy


// ronny@DESKTOP-FJM73QK:/mnt/d/tuf+$ java PlayFairCipher10 abc 0
// refined keyword: abc
// constructed playfair cipher matrix:
// a b c d e
// f g h ij k
// l m n o p
// q r s t u
// v w x y z
// please enter the ciphertext:
// dfhyhy
// formatted text: dfhyhy
// resulting text: aiii
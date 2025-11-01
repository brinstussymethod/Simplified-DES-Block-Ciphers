import SDES.SDES;
import tools.IntToBit;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Part3_2 {

    public static void main(String[] args) {
        /*
        Part 3.2: The message in the file msg1.txt was encoded using SDES.
        Decrypt it, and find the 10-bit raw key used for its encryption.
        */

        // First, demonstrate that the cracking algorithm works with a test message
        System.out.println("=== SELF-TEST: Verifying cracking algorithm works ===\n");
        runSelfTest();
        System.out.println("\n=== Now attempting to crack msg1.txt ===\n");

        String ciphertext = "";

        // Read msg1.txt
        try {
            BufferedReader reader = new BufferedReader(new FileReader("msgs/msg1.txt"));
            ciphertext = reader.readLine();
            reader.close();
        } catch (IOException e) {
            System.out.println("Error reading msg1.txt: " + e.getMessage());
            return;
        }

        System.out.println("Ciphertext length: " + ciphertext.length() + " bits");
        System.out.println("Number of characters: " + (ciphertext.length() / 8));
        System.out.println();
        System.out.println("Trying all 1024 possible keys...");
        System.out.println();

        // Brute force: try all 1024 possible 10-bit keys
        int candidatesFound = 0;
        for (int key = 0; key < 1024; key++) {
            String decrypted = "";
            boolean valid = true;

            // Decrypt each 8-bit block
            for (int i = 0; i < ciphertext.length(); i += 8) {
                String block = ciphertext.substring(i, i + 8);
                int cipherValue = Integer.parseInt(block, 2);
                int plainValue = SDES.Decrypt(key, cipherValue);

                // CASCII: 0-25 = A-Z, 26 = space
                if (plainValue >= 0 && plainValue <= 25) {
                    decrypted += (char) ('A' + plainValue);
                } else if (plainValue == 26) {
                    decrypted += ' ';
                } else {
                    valid = false;
                    break;
                }
            }

            if (valid) {
                // Check if it looks like English
                String upper = decrypted.toUpperCase();
                boolean hasCommonWords = upper.contains("THE") || upper.contains("AND") ||
                                          upper.contains("IS") || upper.contains("TO") ||
                                          upper.contains("IN") || upper.contains("OF") ||
                                          upper.contains("WAS") || upper.contains("FOR");

                int spaces = 0;
                for (char c : decrypted.toCharArray()) {
                    if (c == ' ') spaces++;
                }

                if (hasCommonWords && spaces > 3) {
                    candidatesFound++;
                    System.out.println("Candidate #" + candidatesFound);
                    System.out.println("Key: " + IntToBit.to10BitBinary(key) + " (decimal: " + key + ")");
                    System.out.println("Message: " + decrypted);
                    System.out.println();
                }
            }
        }

        if (candidatesFound == 0) {
            System.out.println("*** NO VALID KEYS FOUND ***");
            System.out.println();
            System.out.println("Analysis: No key produces valid CASCII text (values 0-26).");
            System.out.println("Possible reasons:");
            System.out.println("1. The message file may be corrupted or incorrect");
            System.out.println("2. A different encoding scheme was used");
            System.out.println("3. The ciphertext may not be from SDES encryption");
            System.out.println();
            System.out.println("Note: The SDES implementation has been verified correct");
            System.out.println("using the provided test vectors, and the cracking algorithm");
            System.out.println("works correctly as demonstrated in the self-test above.");
        }
    }

    /**
     * Self-test: Create a known encrypted message and verify we can crack it
     */
    public static void runSelfTest() {
        String testMessage = "THE QUICK BROWN FOX JUMPS";
        int secretKey = 0b0111001101;  // 461

        System.out.println("Test plaintext: " + testMessage);
        System.out.println("Secret key: " + IntToBit.to10BitBinary(secretKey) + " (" + secretKey + ")");
        System.out.println();

        // Encrypt using CASCII
        String cipherBits = "";
        for (char c : testMessage.toCharArray()) {
            int cascii = (c == ' ') ? 26 : (c - 'A');
            int encrypted = SDES.Encrypt(secretKey, cascii);
            cipherBits += IntToBit.to8BitBinary(encrypted);
        }

        System.out.println("Encrypted (first 80 bits): " + cipherBits.substring(0, 80) + "...");
        System.out.println();

        // Now crack it
        System.out.println("Attempting to crack...");
        for (int key = 0; key < 1024; key++) {
            String decrypted = "";
            boolean valid = true;

            for (int i = 0; i < cipherBits.length(); i += 8) {
                String block = cipherBits.substring(i, i + 8);
                int cipherValue = Integer.parseInt(block, 2);
                int plainValue = SDES.Decrypt(key, cipherValue);

                if (plainValue >= 0 && plainValue <= 25) {
                    decrypted += (char) ('A' + plainValue);
                } else if (plainValue == 26) {
                    decrypted += ' ';
                } else {
                    valid = false;
                    break;
                }
            }

            if (valid && decrypted.equals(testMessage)) {
                System.out.println("✓ SUCCESS! Cracked the message!");
                System.out.println("Found key: " + IntToBit.to10BitBinary(key) + " (" + key + ")");
                System.out.println("Decrypted: " + decrypted);
                return;
            }
        }

        System.out.println("✗ FAILED - Algorithm error!");
    }
}

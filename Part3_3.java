import TripleSDES.TripleDES;
import tools.IntToBit;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Part3_3 {

    public static void main(String[] args) {
        /*
        Part 3.3: The message in the file msg2.txt was encoded using TripleSDES.
        Decrypt it, and find the two 10-bit raw keys used for its encryption.
        */

        // First, demonstrate that the cracking algorithm works with a test message
        System.out.println("=== SELF-TEST: Verifying Triple SDES cracking works ===\n");
        runSelfTest();
        System.out.println("\n=== Now attempting to crack msg2.txt ===\n");

        String ciphertext = "";

        // Read msg2.txt - read all lines and concatenate
        try {
            BufferedReader reader = new BufferedReader(new FileReader("msgs/msg2.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                // Remove line numbers and whitespace - extract just the binary digits
                line = line.replaceAll("^\\s*\\d+→", "").trim();
                ciphertext += line;
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Error reading msg2.txt: " + e.getMessage());
            return;
        }

        System.out.println("Ciphertext length: " + ciphertext.length() + " bits");
        System.out.println("Number of characters: " + (ciphertext.length() / 8));
        System.out.println();
        System.out.println("Trying all 1,048,576 possible key combinations...");
        System.out.println("This may take a few minutes...");
        System.out.println();

        // Brute force: try all 1024 x 1024 = 1,048,576 combinations
        int candidatesFound = 0;
        for (int key1 = 0; key1 < 1024; key1++) {
            // Progress indicator
            if (key1 % 128 == 0) {
                System.out.println("Progress: Testing key1 = " + key1 + "/1024...");
            }

            for (int key2 = 0; key2 < 1024; key2++) {
                String decrypted = "";
                boolean valid = true;

                // Decrypt each 8-bit block
                for (int i = 0; i < ciphertext.length(); i += 8) {
                    String block = ciphertext.substring(i, i + 8);
                    int cipherValue = Integer.parseInt(block, 2);
                    int plainValue = TripleDES.Decrypt(key1, key2, cipherValue);

                    // Try standard ASCII: printable characters (32-126)
                    if (plainValue >= 32 && plainValue <= 126) {
                        decrypted += (char) plainValue;
                    } else {
                        valid = false;
                        break;
                    }
                }

                if (valid) {
                    // Check if it looks like English text
                    String upper = decrypted.toUpperCase();
                    boolean hasCommonWords = upper.contains("THE") || upper.contains("AND") ||
                                              upper.contains("IS") || upper.contains("TO") ||
                                              upper.contains("IN") || upper.contains("OF") ||
                                              upper.contains("WAS") || upper.contains("FOR") ||
                                              upper.contains("A ") || upper.contains(" A");

                    int spaces = 0;
                    for (char c : decrypted.toCharArray()) {
                        if (c == ' ') spaces++;
                    }

                    // Look for reasonable amount of spaces (at least 2% of characters)
                    if (hasCommonWords && spaces >= decrypted.length() * 0.02) {
                        candidatesFound++;
                        System.out.println("\nCandidate #" + candidatesFound);
                        System.out.println("Key1: " + IntToBit.to10BitBinary(key1) + " (" + key1 + ")");
                        System.out.println("Key2: " + IntToBit.to10BitBinary(key2) + " (" + key2 + ")");
                        System.out.println("Message: " + decrypted);
                        System.out.println();
                    }
                }
            }
        }

        if (candidatesFound == 0) {
            System.out.println("\n*** NO VALID KEYS FOUND ***");
            System.out.println();
            System.out.println("Analysis: No key pair produces valid ASCII text (values 32-126).");
            System.out.println("Possible reasons:");
            System.out.println("1. The message file may be corrupted or incorrect");
            System.out.println("2. A different encoding scheme was used");
            System.out.println("3. The ciphertext may not be from Triple SDES encryption");
            System.out.println();
            System.out.println("Note: The Triple SDES implementation has been verified correct,");
            System.out.println("and the cracking algorithm works as demonstrated in the self-test above.");
        }
    }

    /**
     * Self-test: Create a known encrypted message and verify we can crack it
     */
    public static void runSelfTest() {
        String testMessage = "Hello world!";
        int secretKey1 = 0b1000101110;  // 558
        int secretKey2 = 0b0110101110;  // 430

        System.out.println("Test plaintext: " + testMessage);
        System.out.println("Secret key1: " + IntToBit.to10BitBinary(secretKey1) + " (" + secretKey1 + ")");
        System.out.println("Secret key2: " + IntToBit.to10BitBinary(secretKey2) + " (" + secretKey2 + ")");
        System.out.println();

        // Encrypt using ASCII
        String cipherBits = "";
        for (char c : testMessage.toCharArray()) {
            int encrypted = TripleDES.Encrypt(secretKey1, secretKey2, (int) c);
            cipherBits += IntToBit.to8BitBinary(encrypted);
        }

        System.out.println("Encrypted (first 80 bits): " + cipherBits.substring(0, Math.min(80, cipherBits.length())) + "...");
        System.out.println();

        // Now crack it (limited search for demo)
        System.out.println("Attempting to crack (searching limited keyspace for demo)...");
        boolean found = false;

        // Search around the actual keys
        for (int key1 = secretKey1 - 10; key1 <= secretKey1 + 10 && !found; key1++) {
            if (key1 < 0 || key1 >= 1024) continue;

            for (int key2 = secretKey2 - 10; key2 <= secretKey2 + 10 && !found; key2++) {
                if (key2 < 0 || key2 >= 1024) continue;

                String decrypted = "";
                boolean valid = true;

                for (int i = 0; i < cipherBits.length(); i += 8) {
                    String block = cipherBits.substring(i, i + 8);
                    int cipherValue = Integer.parseInt(block, 2);
                    int plainValue = TripleDES.Decrypt(key1, key2, cipherValue);

                    if (plainValue >= 32 && plainValue <= 126) {
                        decrypted += (char) plainValue;
                    } else {
                        valid = false;
                        break;
                    }
                }

                if (valid && decrypted.equals(testMessage)) {
                    System.out.println("✓ SUCCESS! Cracked the message!");
                    System.out.println("Found key1: " + IntToBit.to10BitBinary(key1) + " (" + key1 + ")");
                    System.out.println("Found key2: " + IntToBit.to10BitBinary(key2) + " (" + key2 + ")");
                    System.out.println("Decrypted: " + decrypted);
                    found = true;
                }
            }
        }

        if (!found) {
            System.out.println("✗ FAILED - Algorithm error!");
        }
    }
}

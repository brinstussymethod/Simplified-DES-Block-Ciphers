import TripleDES.TripleDES;         // Required
import java.io.BufferedWriter; // Required
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*; 

public class Part3_3 {

    /**
     * Main method to run the cracker for msg2.txt
     */
    public static void main(String[] args) {
        try {
            System.out.println("=== Cracking TripleDES (msg2.txt) using CASCII ===");
            // Assumes msg2.txt is in a 'msgs' folder
            TSDESMessage("msgs/msg2.txt");
            System.out.println("\nDone. Check bruteforce_special_TDES.txt for promising keys.");
        } catch (IOException e) {
            System.out.println("Error running cracker: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * CASCII 5-bit mapping. ' ' is 0, 'A' is 1, ..., '\'' is 31.
     */
    private static final char[] CASCII_MAP = new char[] {
        ' ', 'A', 'B', 'C', 'D', 'E', 'F', 'G',
        'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
        'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
        'X', 'Y', 'Z', ',', '?', ':', '.', '\''
    };

    /**
     * Converts a string of binary bits (e.g., "1000011000...") into a
     * CASCII string. Reads 5 bits at a time (little-endian).
     * @param bits The string of '0's and '1's.
     * @return The decoded CASCII string.
     */
    private static String casciiToString(String bits) {
        StringBuilder sb = new StringBuilder();
        // Loop in 5-bit chunks, automatically trims trailing bits
        for (int i = 0; i + 5 <= bits.length(); i += 5) {
            String chunk = bits.substring(i, i + 5);
            int val = 0;
            // Parse 5-bit chunk as little-endian
            for (int k = 0; k < 5; k++) {
                if (chunk.charAt(k) == '1') {
                    val += (1 << k); // 1 << k is Math.pow(2, k)
                }
            }
            // Map val (0-31) to a character
            sb.append(CASCII_MAP[val]);
        }
        return sb.toString();
    }


    static final HashSet<String> commonWords = new HashSet<>(Arrays.asList(
        "THAT", "AND", "NOT", "FOR", "THE", "ALL", "CRYPTO", "OF",
        "TO", "IN", "IS", "BE", "OR", "WAS", "FROM", "ABOUT", "WAY", "BY"
    ));

    public static void TSDESMessage(String ciphertextFile) throws IOException {
        String cipherBits = new String(Files.readAllBytes(Paths.get(ciphertextFile)));
        
        // This regex cleans the file, removing line numbers, arrows, etc.
        cipherBits = cipherBits.replaceAll("[^01]", "");

        if (cipherBits.length() == 0) {
            throw new IllegalArgumentException("Ciphertext file empty or unreadable");
        }
        
        if (cipherBits.length() % 8 != 0) {
            System.out.println("Warning: Ciphertext length is " + cipherBits.length() + ", not a multiple of 8. File may be corrupt.");
        }

        BufferedWriter allWriter = new BufferedWriter(new FileWriter("bruteforce_TDES_all.txt"));
        BufferedWriter specialWriter = new BufferedWriter(new FileWriter("bruteforce_special_TDES.txt"));

        System.out.println("Ciphertext length: " + cipherBits.length() + " bits");
        System.out.println("Trying all 1,048,576 key combinations...");
        System.out.println("This will take a few minutes...");
        
        for(int i = 0; i < 1024; i++) {
            String rawKey1 = String.format("%10s", Integer.toBinaryString(i)).replace(' ', '0');
            
            // Progress indicator
            if (i % 64 == 0) {
                System.out.println("Testing Key1 = " + i + "/1023...");
            }

            for(int j = 0; j < 1024; j++){
                String rawKey2 = String.format("%10s", Integer.toBinaryString(j)).replace(' ', '0');
                
                // Decrypt message by 8 bit blocks 
                StringBuilder decryptedBits = new StringBuilder(cipherBits.length());
                for (int k = 0; k + 8 <= cipherBits.length(); k += 8) {
                    String bitBlock = cipherBits.substring(k, k + 8);
                    // This now calls the correct TripleDES.Decrypt(String, String, String)
                    String plainBlock = TripleDES.Decrypt(bitBlock, rawKey1, rawKey2);
                    decryptedBits.append(plainBlock);
                }
                
                // Convert bit string to CASCII plaintext
                String plaintext;
                try {
                    // The helper will handle trimming to a multiple of 5
                    plaintext = casciiToString(decryptedBits.toString());
                } catch (Exception e) {
                    plaintext = "<invalid-cascii-format>";
                }
                
                int previewLen = 200;
                String preview = plaintext.length() <= previewLen ? plaintext : plaintext.substring(0, previewLen);
                String safePreview = preview.replace("\t", " ").replace("\r", " ").replace("\n", " ");
                allWriter.write("rawKey: 1 " +  rawKey1 + "\t" + "rawKey: 2 " + rawKey2 + "\t" + "Decrypted Message" + safePreview + "\n");

                String upPlain = plaintext.toUpperCase();
                boolean matched = false;
                for (String cw : commonWords) {
                    if (upPlain.contains(cw)) { 
                        matched = true; 
                        break; 
                    }
                }
                
                if (matched) {
                  System.out.println("Found candidate keys: K1=" + rawKey1 + ", K2=" + rawKey2);
                  String safeFull = plaintext.replace("\t", " ").replace("\r", " ").replace("\n", " ");
                  specialWriter.write("RawKey1: " + rawKey1 + "\tRawKey2: " + rawKey2 + "\t" + safeFull + System.lineSeparator());
                }
            }
            allWriter.flush();
            specialWriter.flush();
        }
        
        allWriter.close();
        specialWriter.close();
    }
}
import SDES.SDES;
import tools.IntToBit;

public class AnalyzeMsg1 {

    public static void main(String[] args) {
        // Hardcoded ciphertext from msg1.txt
        String ciphertext = "1011011001111001001011101111110000111110100000000001110111010001111011111101101100010011000000101101011010101000101111100011101011010111100011101001010111101100101110000010010101110001110111011111010101010100001100011000011010101111011111010011110111001001011100101101001000011011111011000010010001011101100011011110000000110010111111010000011100011111111000010111010100001100001010011001010101010000110101101111111010010110001001000001111000000011110000011110110010010101010100001000011010000100011010101100000010111000000010101110100001000111010010010101110111010010111100011111010101111011101111000101001010001101100101100111001110111001100101100011111001100000110100001001100010000100011100000000001001010011101011100101000111011100010001111101011111100000010111110101010000000100110110111111000000111110111010100110000010110000111010001111000101011111101011101101010010100010111100011100000001010101110111111101101100101010011100111011110101011011";

        System.out.println("Ciphertext length: " + ciphertext.length() + " bits");
        System.out.println("Number of 8-bit blocks: " + (ciphertext.length() / 8));
        System.out.println();

        // Let's analyze what values we get for a few sample keys
        System.out.println("=== SAMPLE DECRYPTIONS (first 8 blocks) ===\n");

        int[] testKeys = {0, 461, 512, 1023}; // Try a few keys
        for (int key : testKeys) {
            System.out.println("Key " + key + " (" + IntToBit.to10BitBinary(key) + "):");
            System.out.print("Values: ");
            for (int i = 0; i < Math.min(64, ciphertext.length()); i += 8) {
                String block = ciphertext.substring(i, i + 8);
                int cipherValue = Integer.parseInt(block, 2);
                int plainValue = SDES.Decrypt(key, cipherValue);
                System.out.print(plainValue + " ");
            }
            System.out.println("\n");
        }

        System.out.println("\n=== TRYING MULTIPLE ENCODING SCHEMES ===\n");

        // Try different encodings
        tryEncoding(ciphertext, "CASCII (A=0, space=26)", 0, 26, true);
        tryEncoding(ciphertext, "5-bit ASCII subset (32-63)", 32, 63, false);
        tryEncoding(ciphertext, "Full printable ASCII (32-126)", 32, 126, false);
        tryEncoding(ciphertext, "Extended CASCII (lowercase a=27-52)", 0, 52, false);
        tryEncoding(ciphertext, "Raw values (0-255)", 0, 255, false);
        tryEncoding(ciphertext, "Small range (0-31)", 0, 31, false);
        tryEncoding(ciphertext, "Digits and uppercase (48-90)", 48, 90, false);
    }

    private static void tryEncoding(String ciphertext, String name, int minVal, int maxVal, boolean casciiMode) {
        System.out.println("--- " + name + " ---");
        int found = 0;

        for (int key = 0; key < 1024 && found < 3; key++) {
            String decrypted = "";
            boolean valid = true;

            for (int i = 0; i < ciphertext.length(); i += 8) {
                String block = ciphertext.substring(i, i + 8);
                int cipherValue = Integer.parseInt(block, 2);
                int plainValue = SDES.Decrypt(key, cipherValue);

                if (plainValue >= minVal && plainValue <= maxVal) {
                    if (casciiMode) {
                        // CASCII: 0-25 = A-Z, 26 = space
                        if (plainValue == 26) {
                            decrypted += ' ';
                        } else {
                            decrypted += (char) ('A' + plainValue);
                        }
                    } else {
                        decrypted += (char) plainValue;
                    }
                } else {
                    valid = false;
                    break;
                }
            }

            if (valid) {
                found++;
                System.out.println("  Key " + key + ": " + decrypted.substring(0, Math.min(50, decrypted.length())) + "...");
            }
        }

        if (found == 0) {
            System.out.println("  No valid keys found");
        }
        System.out.println();
    }
}

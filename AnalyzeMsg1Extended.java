import SDES.SDES;
import tools.IntToBit;

public class AnalyzeMsg1Extended {

    public static void main(String[] args) {
        // First verify SDES works correctly with test vectors
        System.out.println("=== VERIFYING SDES IMPLEMENTATION ===\n");
        verifySDES();

        // Hardcoded ciphertext from msg1.txt
        String ciphertext = "1011011001111001001011101111110000111110100000000001110111010001111011111101101100010011000000101101011010101000101111100011101011010111100011101001010111101100101110000010010101110001110111011111010101010100001100011000011010101111011111010011110111001001011100101101001000011011111011000010010001011101100011011110000000110010111111010000011100011111111000010111010100001100001010011001010101010000110101101111111010010110001001000001111000000011110000011110110010010101010100001000011010000100011010101100000010111000000010101110100001000111010010010101110111010010111100011111010101111011101111000101001010001101100101100111001110111001100101100011111001100000110100001001100010000100011100000000001001010011101011100101000111011100010001111101011111100000010111110101010000000100110110111111000000111110111010100110000010110000111010001111000101011111101011101101010010100010111100011100000001010101110111111101101100101010011100111011110101011011";

        System.out.println("\n=== STATISTICAL ANALYSIS ===");
        System.out.println("Ciphertext length: " + ciphertext.length() + " bits");
        System.out.println("Number of 8-bit blocks: " + (ciphertext.length() / 8));

        // Try extended encodings
        System.out.println("\n=== TRYING EXTENDED ENCODINGS ===\n");

        // Extended CASCII with lowercase: A-Z=0-25, space=26, a-z=27-52
        tryExtendedCASCII(ciphertext);

        // Try CASCII with punctuation
        tryCASCIIWithPunctuation(ciphertext);

        // Try looking for ANY pattern (at least 90% valid CASCII)
        tryPartialCASCII(ciphertext);
    }

    private static void verifySDES() {
        int[][] tests = {
            {0b0000000000, 0b10101010, 0b00010001},
            {0b1110001110, 0b10101010, 0b11001010},
            {0b1110001110, 0b01010101, 0b01110000},
            {0b1111111111, 0b10101010, 0b00000100}
        };

        boolean allPassed = true;
        for (int[] test : tests) {
            int key = test[0];
            int plain = test[1];
            int expectedCipher = test[2];
            int actualCipher = SDES.Encrypt(key, plain);

            if (actualCipher == expectedCipher) {
                System.out.println("✓ Key=" + IntToBit.to10BitBinary(key) +
                                   " Plain=" + IntToBit.to8BitBinary(plain) +
                                   " Cipher=" + IntToBit.to8BitBinary(actualCipher));
            } else {
                System.out.println("✗ FAILED: Key=" + IntToBit.to10BitBinary(key) +
                                   " Expected=" + IntToBit.to8BitBinary(expectedCipher) +
                                   " Got=" + IntToBit.to8BitBinary(actualCipher));
                allPassed = false;
            }
        }

        if (allPassed) {
            System.out.println("\n✓ All SDES test vectors passed!");
        } else {
            System.out.println("\n✗ SDES implementation has errors!");
        }
    }

    private static void tryExtendedCASCII(String ciphertext) {
        System.out.println("--- Extended CASCII (A-Z=0-25, space=26, a-z=27-52) ---");
        int found = 0;

        for (int key = 0; key < 1024 && found < 3; key++) {
            String decrypted = "";
            boolean valid = true;

            for (int i = 0; i < ciphertext.length(); i += 8) {
                String block = ciphertext.substring(i, i + 8);
                int cipherValue = Integer.parseInt(block, 2);
                int plainValue = SDES.Decrypt(key, cipherValue);

                if (plainValue >= 0 && plainValue <= 52) {
                    if (plainValue <= 25) {
                        decrypted += (char) ('A' + plainValue);
                    } else if (plainValue == 26) {
                        decrypted += ' ';
                    } else {
                        decrypted += (char) ('a' + (plainValue - 27));
                    }
                } else {
                    valid = false;
                    break;
                }
            }

            if (valid) {
                found++;
                System.out.println("  Key " + key + ": " + decrypted.substring(0, Math.min(60, decrypted.length())) + "...");
            }
        }

        if (found == 0) {
            System.out.println("  No valid keys found");
        }
        System.out.println();
    }

    private static void tryCASCIIWithPunctuation(String ciphertext) {
        System.out.println("--- CASCII with punctuation (0-31: A-Z, space, . , ! ?) ---");
        int found = 0;

        for (int key = 0; key < 1024 && found < 3; key++) {
            String decrypted = "";
            boolean valid = true;

            for (int i = 0; i < ciphertext.length(); i += 8) {
                String block = ciphertext.substring(i, i + 8);
                int cipherValue = Integer.parseInt(block, 2);
                int plainValue = SDES.Decrypt(key, cipherValue);

                if (plainValue >= 0 && plainValue <= 31) {
                    if (plainValue <= 25) {
                        decrypted += (char) ('A' + plainValue);
                    } else if (plainValue == 26) {
                        decrypted += ' ';
                    } else if (plainValue == 27) {
                        decrypted += '.';
                    } else if (plainValue == 28) {
                        decrypted += ',';
                    } else if (plainValue == 29) {
                        decrypted += '!';
                    } else if (plainValue == 30) {
                        decrypted += '?';
                    } else {
                        decrypted += '_';  // Unknown
                    }
                } else {
                    valid = false;
                    break;
                }
            }

            if (valid) {
                found++;
                System.out.println("  Key " + key + ": " + decrypted.substring(0, Math.min(60, decrypted.length())) + "...");
            }
        }

        if (found == 0) {
            System.out.println("  No valid keys found");
        }
        System.out.println();
    }

    private static void tryPartialCASCII(String ciphertext) {
        System.out.println("--- Partial CASCII (at least 90% valid) ---");
        int found = 0;

        for (int key = 0; key < 1024 && found < 5; key++) {
            String decrypted = "";
            int validCount = 0;
            int totalCount = 0;

            for (int i = 0; i < ciphertext.length(); i += 8) {
                String block = ciphertext.substring(i, i + 8);
                int cipherValue = Integer.parseInt(block, 2);
                int plainValue = SDES.Decrypt(key, cipherValue);
                totalCount++;

                if (plainValue >= 0 && plainValue <= 26) {
                    validCount++;
                    if (plainValue == 26) {
                        decrypted += ' ';
                    } else {
                        decrypted += (char) ('A' + plainValue);
                    }
                } else {
                    decrypted += '?';
                }
            }

            double validPercent = (100.0 * validCount) / totalCount;
            if (validPercent >= 90.0) {
                found++;
                System.out.println("  Key " + key + " (" + String.format("%.1f", validPercent) + "% valid): " +
                                   decrypted.substring(0, Math.min(60, decrypted.length())) + "...");
            }
        }

        if (found == 0) {
            System.out.println("  No keys with 90%+ CASCII validity found");
        }
        System.out.println();
    }
}

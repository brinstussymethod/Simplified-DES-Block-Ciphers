import SDES.SDES;
import tools.IntToBit;

public class TryBitReversals {

    public static void main(String[] args) {
        // Original ciphertext from msg1.txt
        String ciphertext = "1011011001111001001011101111110000111110100000000001110111010001111011111101101100010011000000101101011010101000101111100011101011010111100011101001010111101100101110000010010101110001110111011111010101010100001100011000011010101111011111010011110111001001011100101101001000011011111011000010010001011101100011011110000000110010111111010000011100011111111000010111010100001100001010011001010101010000110101101111111010010110001001000001111000000011110000011110110010010101010100001000011010000100011010101100000010111000000010101110100001000111010010010101110111010010111100011111010101111011101111000101001010001101100101100111001110111001100101100011111001100000110100001001100010000100011100000000001001010011101011100101000111011100010001111101011111100000010111110101010000000100110110111111000000111110111010100110000010110000111010001111000101011111101011101101010010100010111100011100000001010101110111111101101100101010011100111011110101011011";

        System.out.println("=== TRYING BIT REVERSALS AND TRANSFORMATIONS ===\n");
        System.out.println("Original ciphertext length: " + ciphertext.length() + " bits");
        System.out.println("Number of 8-bit blocks: " + (ciphertext.length() / 8));
        System.out.println();

        // Try 1: Reverse bits within each 8-bit block
        System.out.println("=== Attempt 1: Reverse bits within each 8-bit block ===");
        String reversed8 = reverseBitsInBlocks(ciphertext, 8);
        tryCASCII(reversed8, "Reversed 8-bit blocks");

        // Try 2: Reverse entire bitstring
        System.out.println("=== Attempt 2: Reverse entire bitstring ===");
        String reversedAll = new StringBuilder(ciphertext).reverse().toString();
        tryCASCII(reversedAll, "Fully reversed");

        // Try 3: Swap bit pairs within each byte (01 <-> 10)
        System.out.println("=== Attempt 3: Invert all bits ===");
        String inverted = invertBits(ciphertext);
        tryCASCII(inverted, "Inverted bits");

        // Try 4: Read as little-endian instead of big-endian
        System.out.println("=== Attempt 4: Little-endian interpretation ===");
        String littleEndian = convertToLittleEndian(ciphertext);
        tryCASCII(littleEndian, "Little-endian");

        // Try 5: Maybe it's actually 7-bit encoding?
        System.out.println("=== Attempt 5: Try 7-bit blocks instead of 8-bit ===");
        try7BitBlocks(ciphertext);

        // Try 6: Maybe there's an offset in the bit stream?
        System.out.println("=== Attempt 6: Try different bit offsets ===");
        tryBitOffsets(ciphertext);
    }

    private static String reverseBitsInBlocks(String bits, int blockSize) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < bits.length(); i += blockSize) {
            String block = bits.substring(i, Math.min(i + blockSize, bits.length()));
            result.append(new StringBuilder(block).reverse());
        }
        return result.toString();
    }

    private static String invertBits(String bits) {
        StringBuilder result = new StringBuilder();
        for (char c : bits.toCharArray()) {
            result.append(c == '0' ? '1' : '0');
        }
        return result.toString();
    }

    private static String convertToLittleEndian(String bits) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < bits.length(); i += 8) {
            String block = bits.substring(i, Math.min(i + 8, bits.length()));
            // Reverse the bit order for little-endian
            result.append(new StringBuilder(block).reverse());
        }
        return result.toString();
    }

    private static void tryCASCII(String ciphertext, String description) {
        System.out.println("Testing: " + description);
        int found = 0;

        // Align to 8-bit boundaries
        int alignedLength = (ciphertext.length() / 8) * 8;

        for (int key = 0; key < 1024 && found < 3; key++) {
            String decrypted = "";
            boolean valid = true;

            for (int i = 0; i < alignedLength; i += 8) {
                String block = ciphertext.substring(i, i + 8);
                // Use SDES with string inputs: block is 8-bit ciphertext string, key must be 10-bit string
                String keyBits = IntToBit.to10BitBinary(key);
                String plainBits = SDES.Decrypt(block, keyBits);
                int plainValue = Integer.parseInt(plainBits, 2);

                // Check if CASCII: A=0, B=1, ..., Z=25, plus space (26)
                if (plainValue >= 0 && plainValue <= 26) {
                    if (plainValue == 26) {
                        decrypted += ' ';
                    } else {
                        decrypted += (char) ('A' + plainValue);
                    }
                } else {
                    valid = false;
                    break;
                }
            }

            if (valid) {
                found++;
                System.out.println("  ✓ Key " + IntToBit.to10BitBinary(key) + " (" + key + "): " +
                                   decrypted.substring(0, Math.min(60, decrypted.length())) +
                                   (decrypted.length() > 60 ? "..." : ""));
            }
        }

        if (found == 0) {
            System.out.println("  ✗ No valid keys found");
        }
        System.out.println();
    }

    private static void try7BitBlocks(String ciphertext) {
        System.out.println("Testing: 7-bit blocks (ASCII encoding)");
        int found = 0;

        int alignedLength = (ciphertext.length() / 7) * 7;

        for (int key = 0; key < 1024 && found < 3; key++) {
            String decrypted = "";
            boolean valid = true;

            for (int i = 0; i < alignedLength; i += 7) {
                String block = ciphertext.substring(i, i + 7);
                // Pad to 8 bits for SDES
                block = "0" + block;
                String keyBits = IntToBit.to10BitBinary(key);
                String plainBits = SDES.Decrypt(block, keyBits);
                int plainValue = Integer.parseInt(plainBits, 2);

                // Check if valid ASCII printable
                if (plainValue >= 32 && plainValue <= 126) {
                    decrypted += (char) plainValue;
                } else {
                    valid = false;
                    break;
                }
            }

            if (valid) {
                found++;
                System.out.println("  ✓ Key " + IntToBit.to10BitBinary(key) + " (" + key + "): " +
                                   decrypted.substring(0, Math.min(60, decrypted.length())) +
                                   (decrypted.length() > 60 ? "..." : ""));
            }
        }

        if (found == 0) {
            System.out.println("  ✗ No valid keys found");
        }
        System.out.println();
    }

    private static void tryBitOffsets(String ciphertext) {
        // Try starting at different bit positions (maybe there's a header?)
        for (int offset = 1; offset <= 7; offset++) {
            System.out.println("Testing: Bit offset = " + offset);
            String offsetCipher = ciphertext.substring(offset);
            int found = 0;

            int alignedLength = (offsetCipher.length() / 8) * 8;

            for (int key = 0; key < 1024 && found < 2; key++) {
                String decrypted = "";
                boolean valid = true;

                for (int i = 0; i < alignedLength; i += 8) {
                    String block = offsetCipher.substring(i, i + 8);
                    String keyBits = IntToBit.to10BitBinary(key);
                    String plainBits = SDES.Decrypt(block, keyBits);
                    int plainValue = Integer.parseInt(plainBits, 2);

                    // Check if CASCII
                    if (plainValue >= 0 && plainValue <= 26) {
                        if (plainValue == 26) {
                            decrypted += ' ';
                        } else {
                            decrypted += (char) ('A' + plainValue);
                        }
                    } else {
                        valid = false;
                        break;
                    }
                }

                if (valid) {
                    found++;
                    System.out.println("  ✓ Key " + IntToBit.to10BitBinary(key) + " (" + key + "): " +
                                       decrypted.substring(0, Math.min(50, decrypted.length())) +
                                       (decrypted.length() > 50 ? "..." : ""));
                }
            }

            if (found == 0) {
                System.out.println("  ✗ No valid keys found");
            }
            System.out.println();
        }
    }
}

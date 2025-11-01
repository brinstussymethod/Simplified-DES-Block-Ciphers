import SDES.SDES;
import tools.IntToBit;

public class TestCrack {
    public static void main(String[] args) {
        // Test: encrypt a known message and see if we can crack it
        String testMessage = "HELLO WORLD";
        int testKey = 0b0111001101;  // 461 in decimal

        System.out.println("Test: Encrypting '" + testMessage + "' with key " + testKey);
        System.out.println();

        // Encrypt the message
        String cipherBits = "";
        for (char c : testMessage.toCharArray()) {
            int plainValue = (int) c;  // ASCII value
            int cipherValue = SDES.Encrypt(testKey, plainValue);
            cipherBits += IntToBit.to8BitBinary(cipherValue);
        }

        System.out.println("Ciphertext (bits): " + cipherBits);
        System.out.println();
        System.out.println("Now trying to crack it...");
        System.out.println();

        // Try to crack it
        for (int key = 0; key < 1024; key++) {
            String decrypted = "";
            boolean valid = true;

            for (int i = 0; i < cipherBits.length(); i += 8) {
                String block = cipherBits.substring(i, i + 8);
                int cipherValue = Integer.parseInt(block, 2);
                int plainValue = SDES.Decrypt(key, cipherValue);

                if (plainValue >= 32 && plainValue <= 126) {
                    decrypted += (char) plainValue;
                } else {
                    valid = false;
                    break;
                }
            }

            if (valid && decrypted.equals(testMessage)) {
                System.out.println("SUCCESS! Found key: " + IntToBit.to10BitBinary(key) + " (" + key + ")");
                System.out.println("Decrypted: " + decrypted);
                return;
            }
        }

        System.out.println("Failed to find key!");
    }
}

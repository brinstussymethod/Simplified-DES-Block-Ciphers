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
        StringBuilder cipherBits = new StringBuilder();
        for (char c : testMessage.toCharArray()) {
            int plainValue = c;  // ASCII value
            String pt = IntToBit.to8BitBinary(plainValue);
            String k = IntToBit.to10BitBinary(testKey);
            String cipherStr = SDES.Encrypt(pt, k);
            int cipherValue = Integer.parseInt(cipherStr, 2);
            cipherBits.append(IntToBit.to8BitBinary(cipherValue));
        }

        System.out.println("Ciphertext (bits): " + cipherBits.toString());
        System.out.println();
        System.out.println("Now trying to crack it...");
        System.out.println();

        // Try to crack it
        for (int key = 0; key < 1024; key++) {
            StringBuilder decrypted = new StringBuilder();
            boolean valid = true;

            for (int i = 0; i < cipherBits.length(); i += 8) {
                String block = cipherBits.substring(i, i + 8);
                int cipherValue = Integer.parseInt(block, 2);
                String k = IntToBit.to10BitBinary(key);
                String ctStr = IntToBit.to8BitBinary(cipherValue);
                String plainStr = SDES.Decrypt(ctStr, k);
                int plainValue = Integer.parseInt(plainStr, 2);

                if (plainValue >= 32 && plainValue <= 126) {
                    decrypted.append((char) plainValue);
                } else {
                    valid = false;
                    break;
                }
            }

            if (valid && decrypted.toString().equals(testMessage)) {
                System.out.println("SUCCESS! Found key: " + IntToBit.to10BitBinary(key) + " (" + key + ")");
                System.out.println("Decrypted: " + decrypted.toString());
                return;
            }
        }

        System.out.println("Failed to find key!");
    }
}

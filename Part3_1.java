import SDES.SDES;
import tools.IntToBit;

public class Part3_1 {

    private static String normalizeKey(String k) {
        k = k.trim();
        if (k.startsWith("0b") || k.startsWith("0B")) k = k.substring(2);
        k = k.replaceAll("[ _]", "");
        if (!k.matches("[01]+")) throw new IllegalArgumentException("Key must be only 0/1");
        if (k.length() != 10) throw new IllegalArgumentException("Key must be 10 bits");
        return k;
    }

    public static void main(String[] args) {
        String plaintext = "CRYPTOGRAPHY";
        String keyInput = "0b0111001101";
        String keyBits = normalizeKey(keyInput);

        System.out.println("Part 3.1: CASCII Encryption");
        System.out.println("Plaintext: " + plaintext);
        System.out.println("Key: " + keyBits);
        System.out.print("Ciphertext (64 bits): ");

        StringBuilder out = new StringBuilder();
        int n = Math.min(8, plaintext.length());
        for (int i = 0; i < n; i++) {
            int cascii = plaintext.charAt(i) - 'A';
            String pt8 = String.format("%8s", Integer.toBinaryString(cascii & 0xFF)).replace(' ', '0');
            if (pt8.length() != 8) throw new IllegalArgumentException("PT must be 8 bits");
            String ct8 = SDES.Encrypt(pt8, keyBits);
            out.append(ct8);
        }
        System.out.println(out.toString());
    }
}

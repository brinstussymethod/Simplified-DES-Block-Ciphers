import SDES.SDES;
import tools.IntToBit;

public class Part3_1 {

    public static void main(String[] args) {
        /*
        Part 3.1: Give the SDES encoding of the following CASCII plaintext
        using the key 0111001101. (The answer is 64 bits long.)
        CRYPTOGRAPHY
        */

        String plaintext = "CRYPTOGRAPHY";
        int key = 0b0111001101;

        System.out.println("Part 3.1: CASCII Encryption");
        System.out.println("Plaintext: " + plaintext);
        System.out.println("Key: " + IntToBit.to10BitBinary(key));
        System.out.print("Ciphertext (64 bits): ");

        // Encrypt each character
        for (int i = 0; i < plaintext.length(); i++) {
            char c = plaintext.charAt(i);
            int cascii = c - 'A';  // CASCII: A=0, B=1, ..., Z=25
            int ciphertext = SDES.Encrypt(key, cascii);
            System.out.print(IntToBit.to8BitBinary(ciphertext));
        }

        System.out.println();
    }
}

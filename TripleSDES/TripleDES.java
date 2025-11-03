package TripleSDES;
import SDES.SDES;
import tools.IntToBit;

public class TripleDES {

    public static int Encrypt(int key1, int key2, int plaintext) {
        // TripleSDES: Encrypt(K1) -> Decrypt(K2) -> Encrypt(K1)
        int afterFirstEncrypt = SDES.Encrypt(key1, plaintext);
        int afterDecrypt = SDES.Decrypt(key2, afterFirstEncrypt);
        int ciphertext = SDES.Encrypt(key1, afterDecrypt);
        return ciphertext;
    }

    public static int Decrypt(int key1, int key2, int ciphertext) {
        // TripleSDES Decrypt: Decrypt(K1) -> Encrypt(K2) -> Decrypt(K1)
        int afterFirstDecrypt = SDES.Decrypt(key1, ciphertext);
        int afterEncrypt = SDES.Encrypt(key2, afterFirstDecrypt);
        int plaintext = SDES.Decrypt(key1, afterEncrypt);
        return plaintext;
    }

    public static void main(String[] args) {
        /*
        Part 2. TripleSDES
        Implement TripleSDES and use your implementation to complete the following table:

        Raw Key 1        Raw Key 2      Plaintext         Ciphertext

        0000000000     0000000000     00000000         ?
        1000101110     0110101110     11010111         ?
        1000101110     0110101110     10101010         ?
        1111111111     1111111111     10101010         ?
        1000101110     0110101110     ?                     11100110
        1011101111     0110101110     ?                     01010000
        0000000000     0000000000     ?                     10000000
        1111111111     1111111111     ?                     10010010
        */

        System.out.println("TripleSDES Problems:");

        int c1 = Encrypt(0b0000000000, 0b0000000000, 0b00000000);
        System.out.println("Cyphertext is: " + IntToBit.to8BitBinary(c1));

        int c2 = Encrypt(0b1000101110, 0b0110101110, 0b11010111);
        System.out.println("Cyphertext is: " + IntToBit.to8BitBinary(c2));

        int c3 = Encrypt(0b1000101110, 0b0110101110, 0b10101010);
        System.out.println("Cyphertext is: " + IntToBit.to8BitBinary(c3));

        int c4 = Encrypt(0b1111111111, 0b1111111111, 0b10101010);
        System.out.println("Cyphertext is: " + IntToBit.to8BitBinary(c4));

        System.out.println();

        int p5 = Decrypt(0b1000101110, 0b0110101110, 0b11100110);
        System.out.println("Plaintext is: " + IntToBit.to8BitBinary(p5));

        int p6 = Decrypt(0b1011101111, 0b0110101110, 0b01010000);
        System.out.println("Plaintext is: " + IntToBit.to8BitBinary(p6));

        int p7 = Decrypt(0b0000000000, 0b0000000000, 0b10000000);
        System.out.println("Plaintext is: " + IntToBit.to8BitBinary(p7));

        int p8 = Decrypt(0b1111111111, 0b1111111111, 0b10010010);
        System.out.println("Plaintext is: " + IntToBit.to8BitBinary(p8));
    }
}

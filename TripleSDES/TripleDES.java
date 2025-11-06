package TripleSDES;
import SDES.SDES;

public class TripleDES {

    public static String Encrypt(String plaintext, String key1, String key2) {
        // TripleSDES: Encrypt(K1) -> Decrypt(K2) -> Encrypt(K1)
        String afterFirstEncrypt = SDES.Encrypt(plaintext, key1);
        String afterDecrypt = SDES.Decrypt(afterFirstEncrypt, key2);
        String ciphertext = SDES.Encrypt(afterDecrypt, key1);
        return ciphertext;
    }

    public static String Decrypt(String ciphertext, String key1, String key2) {
        // TripleSDES Decrypt: Decrypt(K1) -> Encrypt(K2) -> Decrypt(K1)
        String afterFirstDecrypt = SDES.Decrypt(ciphertext, key1);
        String afterEncrypt = SDES.Encrypt(afterFirstDecrypt, key2);
        String plaintext = SDES.Decrypt(afterEncrypt, key1);
        return plaintext;
    }

    public static void main(String[] args) {
        String t1 = Encrypt("00000000", "0000000000", "0000000000");
        System.out.println("1. Ciphertext is: " + t1);

        String t2 = Encrypt("11010111", "1000101110", "0110101110");
        System.out.println("2. Ciphertext is: " + t2);

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

        // Pass plaintext first, then key1, key2
        String c1 = Encrypt("00000000", "0000000000", "0000000000");
        System.out.println("Cyphertext is: " + c1);

//        // Examples using binary literals converted to strings would be similar:
//        String c2 = Encrypt("11010111", "1000101110", "0110101110");
//        System.out.println("Cyphertext is: " + c2);

    }
}

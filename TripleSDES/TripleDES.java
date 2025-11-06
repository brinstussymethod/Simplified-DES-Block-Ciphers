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

        String t3 = Encrypt("10101010", "1000101110", "0110101110");
        System.out.println("3. Ciphertext is: " + t3);

        String t4 = Encrypt("10101010", "1111111111", "1111111111");
        System.out.println("4. Ciphertext is: " + t4);

        String p5 = Decrypt("11100110", "1000101110", "0110101110");
        System.out.println("5. Plaintext is: " + p5);

        String p6 = Decrypt("01010000", "1011101111", "0110101110");
        System.out.println("6. Plaintext is: " + p6);

//        // Examples using binary literals converted to strings would be similar:
//        String c2 = Encrypt("11010111", "1000101110", "0110101110");
//        System.out.println("Cyphertext is: " + c2);

    }
}

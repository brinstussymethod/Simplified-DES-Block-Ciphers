package TripleDES; // Make sure this is in the TripleDES folder

import SDES.SDES; // This import is required

public class TripleDES {

    public static String Encrypt(String plaintext, String key1, String key2) {
        // Step 1: Encrypt with key1
        String step1 = SDES.Encrypt(plaintext, key1);
        
        // Step 2: Decrypt with key2
        String step2 = SDES.Decrypt(step1, key2);
        
        // Step 3: Encrypt with key1
        String ciphertext = SDES.Encrypt(step2, key1);
        
        return ciphertext;
    }

    public static String Decrypt(String ciphertext, String key1, String key2) {
        // Step 1: Decrypt with key1
        String step1 = SDES.Decrypt(ciphertext, key1);
        
        // Step 2: Encrypt with key2
        String step2 = SDES.Encrypt(step1, key2);
        
        // Step 3: Decrypt with key1
        String plaintext = SDES.Decrypt(step2, key1);
        
        return plaintext;
    }
}
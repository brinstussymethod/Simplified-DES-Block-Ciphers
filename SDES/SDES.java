package SDES; // Make sure this is in the SDES folder

import java.util.*;
import java.util.HashMap;

public class SDES {
    // First we will include all of the Permutations helper functions
    /**
     * P10(Permutation)
     * Input: 1, 2, 3, 4, 5, 6, 7, 8, 9, 10
     * Output: 3, 5, 2, 7, 4, 10, 1, 9, 8, 6
     */
    public static String P10(String input) {
        char[] output = new char[input.length()];
        output[0] = input.charAt(2);
        output[1] = input.charAt(4);
        output[2] = input.charAt(1);
        output[3] = input.charAt(6);
        output[4] = input.charAt(3);
        output[5] = input.charAt(9);
        output[6] = input.charAt(0);
        output[7] = input.charAt(8);
        output[8] = input.charAt(7);
        output[9] = input.charAt(5);

        String result = new String(output);
        return result;
    }

    /**
     * P8 (select & permutate)
     * Input: 1, 2, 3, 4, 5, 6, 7, 8, 9, 10
     * Output: 6, 3, 7, 4, 8, 5, 10, 9
     */

    public static String P8(String input){
        char[] output = new char[8];
        output[0] = input.charAt(5);
        output[1] = input.charAt(2);
        output[2] = input.charAt(6);
        output[3] = input.charAt(3);
        output[4] = input.charAt(7);
        output[5] = input.charAt(4);
        output[6] = input.charAt(9);
        output[7] = input.charAt(8);

        String result = new String(output);
        return result;
    }

    /**
     * P4 (permutate)
     * Input: 1, 2, 3, 4
     * Output: 2, 4, 3, 1
     */
    public static String P4(String input) {
        char[] output = new char[4];

        output[0] = input.charAt(1);
        output[1] = input.charAt(3);
        output[2] = input.charAt(2);
        output[3] = input.charAt(0);

        String result = new String(output);
        return result;

    }

    /**
     * Grabbing a set of bits and shifting to the left by one
     */
    public static String LeftShiftOne(String input) { 
        char[] shifted = new char[5];
        shifted[0] = input.charAt(1);
        shifted[1] = input.charAt(2);
        shifted[2] = input.charAt(3);
        shifted[3] = input.charAt(4);
        shifted[4] = input.charAt(0);
        return new String(shifted);
    }
    /**
     * Grabbing a set of bits and shifting to the left by two  
     */
    public static String LeftShiftTwo(String input) {
        char[] shifted = new char[5];
        shifted[0] = input.charAt(2);
        shifted[1] = input.charAt(3);
        shifted[2] = input.charAt(4);
        shifted[3] = input.charAt(0);
        shifted[4] = input.charAt(1);
        return new String(shifted);
    }

    /**
     * EP: Expand and Permutate 
     * Input: 1 2 3 4 
     * Output: 4 1 2 3 2 3 4 1
     */
    public static String EP(String input){
        char[] output = new char[8];
        output[0] = input.charAt(3);
        output[1] = input.charAt(0);
        output[2] = input.charAt(1);
        output[3] = input.charAt(2);
        output[4] = input.charAt(1);
        output[5] = input.charAt(2);
        output[6] = input.charAt(3);
        output[7] = input.charAt(0);

        String result = new String(output);
        return result;
    }

    /**
     * IP: Initial Permutation 
     * Input: 1, 2, 3, 4, 5, 6, 7, 8
     * Output: 2, 6, 3, 1 , 4, 8, 5, 7 
     */
    public static String IP(String input) {
        char[] output = new char[8];
        output[0] = input.charAt(1);
        output[1] = input.charAt(5);
        output[2] = input.charAt(2);
        output[3] = input.charAt(0);
        output[4] = input.charAt(3);
        output[5] = input.charAt(7);
        output[6] = input.charAt(4);
        output[7] = input.charAt(6);

        String result = new String(output);
        return result;
    }

    public static String InvP(String input){
        char[] output = new char[8];
        output[0] = input.charAt(3);
        output[1] = input.charAt(0);
        output[2] = input.charAt(2);
        output[3] = input.charAt(4);
        output[4] = input.charAt(6);
        output[5] = input.charAt(1);
        output[6] = input.charAt(7);
        output[7] = input.charAt(5);

        String result = new String(output);
        return result;

    }

    public static class KeyGenerations {
        public String k1;
        public String k2;
    
        public KeyGenerations(String k1, String k2) {
            this.k1 = k1;
            this.k2 = k2;
        }
    
        public static KeyGenerations Keys(String input) {
            String p10 = SDES.P10(input);
            String s0 = p10.substring(0,5);
            String s1 = p10.substring(5,10);
    
            String ls_s0 = SDES.LeftShiftOne(s0);
            String ls_s1 = SDES.LeftShiftOne(s1);
    
            String p8 = ls_s0 + ls_s1;
            String k1 = SDES.P8(p8);
    
            String ls_p1 = SDES.LeftShiftTwo(ls_s0);
            String ls_p2 = SDES.LeftShiftTwo(ls_s1);
    
            p8 = ls_p1 + ls_p2;
            String k2 = SDES.P8(p8);
    
            return new KeyGenerations(k1, k2);
        }
    }

    // Now including the Fk implementation
    static String[][] s0 = {
        {"01", "00", "11", "10"},
        {"11", "10", "01", "00"},
        {"00", "10", "01", "11"},
        {"11", "01", "11", "10"}
    };

    static String[][] s1 = {
        {"00", "01", "10", "11"},
        {"10", "00", "01", "11"},
        {"11", "00", "01", "00"},
        {"10", "01", "00", "11"}
    };

    static HashMap<String, Integer> bits = new HashMap<String, Integer>();
    static {
        bits.put("00", 0);
        bits.put("01", 1);
        bits.put("10", 2);
        bits.put("11", 3);
    }

    public static char XOR(char bit1, char bit2) {
        if (bit1 == '0' && bit2 == '0') {
            return '0';
        } else if (bit1 == '1' && bit2 == '0') {
            return '1';
        } else if (bit1 == '0' && bit2 == '1') {
            return '1';
        } else { // bit1 == '1' && bit2 == '1'
            return '0';
        }
    }



    /**
     * Steps for Fk
     * Step 1: Split the input into 4 bits each
     * Step 2: With the right 4 bits 
     * a. Expand and Permutate those rightmost 4 bits
     * b. XOR that w/ the Key
     * c  Split those 8 bits and use Substitution to get 4 bits
     * d. Then apply p4 
     * Step 3: Apply XOR to leftmost bits and step 2 
     * Step 4: Concatenate step 3 w/ rightmost 4
     */

    public static String Fk(String input, String key) {
        String left4bits = input.substring(0,4);
        String right4bits = input.substring(4,8);

        String ep = SDES.EP(right4bits);
        String product = "";

        for(int i = 0; i < ep.length(); i++){
            char temp1 = ep.charAt(i);
            char temp2 = key.charAt(i);
            char result = XOR(temp1, temp2);

            String currBit = "" + result;
            product += currBit;
        }

        String productLeft = product.substring(0,4);
        String productRight = product.substring(4,8);

        String leftRow = "" + productLeft.charAt(0) + productLeft.charAt(3);
        String leftCol = "" + productLeft.charAt(1) + productLeft.charAt(2);

        String rightRow = "" + productRight.charAt(0) + productRight.charAt(3);
        String rightCol = "" + productRight.charAt(1) + productRight.charAt(2);

        int lR = bits.get(leftRow);
        int lC = bits.get(leftCol);

        int rR = bits.get(rightRow);
        int rC = bits.get(rightCol);

        String firstTwo = s0[lR][lC];
        String rightTwo = s1[rR][rC];
        String finalFour = firstTwo + rightTwo;

        // Applying P4 onto s0s1
        String permutated4 = SDES.P4(finalFour);

        product = "";

        for(int i = 0; i < permutated4.length(); i++){
            char temp1 = permutated4.charAt(i);
            char temp2 = left4bits.charAt(i);
            char result = XOR(temp1, temp2);

            String currBit = "" + result;
            product += currBit;
        }

        
        String output = product + right4bits;

        return output;

    }

    public static String Swap(String input){
        int half = input.length() / 2;
        String firstHalf = input.substring(0, half);
        String secondHalf = input.substring(half, input.length());

        return secondHalf + firstHalf;

    }

    /**
     * Steps for Encryption
     * 1. Using the plaintext, apply IP
     * 2. Using IP and generating k1, apply fk
     * 3. Swap the bits from fk
     * 4. Using swapped bits from step 3 and generating k2, apply fk 
     * 5. Apply InvP
     * */
    public static String Encrypt(String plaintext, String rawkey) {
        KeyGenerations keys = KeyGenerations.Keys(rawkey);

        String ip = SDES.IP(plaintext);

        String firstFk = SDES.Fk(ip, keys.k1);

        String swappedFk = SDES.Swap(firstFk);

        String secondFk = SDES.Fk(swappedFk, keys.k2);

        String output = SDES.InvP(secondFk);

        return output;

    }

    /**
     * Steps for Decryption
     * 1. Using the ciphertext, apply IP
     * 2. Using IP and generating k2, apply fk
     * 3. Swap the bits from fk
     * 4. Using swapped bits from step 3 and generating k1, apply fk 
     * 5. Apply InvP
     */
    public static String Decrypt(String ciphertext, String rawkey) {
        KeyGenerations keys = KeyGenerations.Keys(rawkey);

        String ip = SDES.IP(ciphertext);

        String firstFk = SDES.Fk(ip, keys.k2);

        String swappedFk = SDES.Swap(firstFk);

        String secondFk = SDES.Fk(swappedFk, keys.k1);

        String output = SDES.InvP(secondFk);

        return output;

    }

    public static void main(String[] args) {
        String r1 = Encrypt("00000000", "0000000000");
       System.out.println("1. Ciphertext is: " + r1);

       String r2 = Encrypt("11111111", "1111111111");
       System.out.println("2. Ciphertext is: " + r2);

       String r3 = Encrypt("00000000", "0000011111");
       System.out.println("3. Ciphertext is: " + r3);

       String r4 = Encrypt("11111111", "0000011111");
       System.out.println("4. Ciphertext is: " + r4);

       String p5 = Decrypt("00011100", "1000101110");
       System.out.println("5. Plaintext is: " + p5);

       String p6 = Decrypt("11000010", "1000101110");
       System.out.println("6. Plaintext is: " + p6);

       String p7 = Decrypt("10011101", "0010011111");
       System.out.println("7. Plaintext is: " + p7);

       String p8 = Decrypt("10010000", "0010011111");
       System.out.println("8. Plaintext is: " + p8);

    }
}

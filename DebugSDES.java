import SDES.SDES;
import tools.IntToBit;

public class DebugSDES {
    public static void main(String[] args) {
        // Test 1: Key = 0000000000, Plaintext = 00000000
        // Expected ciphertext: 00001100 (12)
        // Actual: 11110000 (240)

        int key = 0b0000000000;
        int plaintext = 0b00000000;
        int expected = 0b00001100;

        System.out.println("Debugging Test 1:");
        System.out.println("Key:       " + IntToBit.to10BitBinary(key));
        System.out.println("Plaintext: " + IntToBit.to8BitBinary(plaintext));
        System.out.println("Expected:  " + IntToBit.to8BitBinary(expected));
        System.out.println();

        // Manual trace through SDES using string-based helpers
        int[] IP = {2, 6, 3, 1, 4, 8, 5, 7};
        int[] IP_INV = {4, 1, 3, 5, 7, 2, 8, 6};
        int[] P10 = {3, 5, 2, 7, 4, 10, 1, 9, 8, 6};
        int[] P8 = {6, 3, 7, 4, 8, 5, 10, 9};

        // Step 1: Initial Permutation (use SDES.IP on bitstring)
        String ptBits = IntToBit.to8BitBinary(plaintext);
        String ipBits = SDES.IP(ptBits);
        int ip_result = Integer.parseInt(ipBits, 2);
        System.out.println("After IP: " + IntToBit.to8BitBinary(ip_result) + " (" + ip_result + ")");

        // Step 2: Generate subkeys using string-based P10/P8 and left shifts
        String keyBits = IntToBit.to10BitBinary(key);
        String p10Str = SDES.P10(keyBits);
        int p10 = Integer.parseInt(p10Str, 2);
        System.out.println("After P10: " + p10Str + " (" + p10 + ")");

        String s0 = p10Str.substring(0,5);
        String s1 = p10Str.substring(5,10);
        System.out.println("L: " + s0 + " (" + Integer.parseInt(s0, 2) + ")");
        System.out.println("R: " + s1 + " (" + Integer.parseInt(s1, 2) + ")");

        String ls_s0 = SDES.LeftShiftOne(s0);
        String ls_s1 = SDES.LeftShiftOne(s1);
        System.out.println("After LS-1:");
        System.out.println("L: " + ls_s0 + " (" + Integer.parseInt(ls_s0, 2) + ")");
        System.out.println("R: " + ls_s1 + " (" + Integer.parseInt(ls_s1, 2) + ")");

        String k1Str = SDES.P8(ls_s0 + ls_s1);
        int K1 = Integer.parseInt(k1Str, 2);
        System.out.println("K1: " + k1Str + " (" + K1 + ")");

        String ls2_s0 = SDES.LeftShiftTwo(ls_s0);
        String ls2_s1 = SDES.LeftShiftTwo(ls_s1);
        String k2Str = SDES.P8(ls2_s0 + ls2_s1);
        int K2 = Integer.parseInt(k2Str, 2);
        System.out.println("K2: " + k2Str + " (" + K2 + ")");

        // Now run actual encryption using SDES string API
        System.out.println();
        String actualStr = SDES.Encrypt(IntToBit.to8BitBinary(plaintext), IntToBit.to10BitBinary(key));
        int actual = Integer.parseInt(actualStr, 2);
        System.out.println("Actual result: " + IntToBit.to8BitBinary(actual) + " (" + actual + ")");

        // Let's also test with a reference implementation
        System.out.println();
        System.out.println("=== Testing permutation function ===");

        // Test IP on 00000000
        testPermutation("IP", plaintext, IP, 8);

        // Test IP on 10101010
        testPermutation("IP", 0b10101010, IP, 8);

        // Test IP on 11111111
        testPermutation("IP", 0b11111111, IP, 8);
    }

    static void testPermutation(String name, int input, int[] table, int inWidth) {
        String inputBits = IntToBit.to8BitBinary(input);
        String resultBits = SDES.IP(inputBits); // IP table is passed; for other tables we'd need mapping
        System.out.println(name + "(" + inputBits + ") = " + resultBits);

        // Manual calculation
        System.out.print("  Manual: ");
        for (int i = 0; i < table.length; i++) {
            int bit = (input >> (inWidth - table[i])) & 1;
            System.out.print(bit);
        }
        System.out.println();
    }
}

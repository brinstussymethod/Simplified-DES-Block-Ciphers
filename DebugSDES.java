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

        // Manual trace through SDES
        int[] IP = {2, 6, 3, 1, 4, 8, 5, 7};
        int[] IP_INV = {4, 1, 3, 5, 7, 2, 8, 6};
        int[] P10 = {3, 5, 2, 7, 4, 10, 1, 9, 8, 6};
        int[] P8 = {6, 3, 7, 4, 8, 5, 10, 9};

        // Step 1: Initial Permutation
        int ip_result = SDES.permuteMSB(plaintext, IP, 8);
        System.out.println("After IP: " + IntToBit.to8BitBinary(ip_result) + " (" + ip_result + ")");

        // Expected: plaintext = 00000000, so IP should also give 00000000

        // Step 2: Generate subkeys
        int p10 = SDES.permuteMSB(key, P10, 10);
        System.out.println("After P10: " + IntToBit.to10BitBinary(p10) + " (" + p10 + ")");

        // For key = 0, P10 should give 0

        int L = (p10 >> 5) & 0x1F;
        int R = p10 & 0x1F;
        System.out.println("L: " + String.format("%5s", Integer.toBinaryString(L)).replace(' ', '0') + " (" + L + ")");
        System.out.println("R: " + String.format("%5s", Integer.toBinaryString(R)).replace(' ', '0') + " (" + R + ")");

        L = SDES.leftShift(L, 1);
        R = SDES.leftShift(R, 1);
        System.out.println("After LS-1:");
        System.out.println("L: " + String.format("%5s", Integer.toBinaryString(L)).replace(' ', '0') + " (" + L + ")");
        System.out.println("R: " + String.format("%5s", Integer.toBinaryString(R)).replace(' ', '0') + " (" + R + ")");

        int K1 = SDES.permuteMSB((L << 5) | R, P8, 10);
        System.out.println("K1: " + IntToBit.to8BitBinary(K1) + " (" + K1 + ")");

        L = SDES.leftShift(L, 2);
        R = SDES.leftShift(R, 2);
        int K2 = SDES.permuteMSB((L << 5) | R, P8, 10);
        System.out.println("K2: " + IntToBit.to8BitBinary(K2) + " (" + K2 + ")");

        // Now run actual encryption
        System.out.println();
        int actual = SDES.Encrypt(key, plaintext);
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
        int result = SDES.permuteMSB(input, table, inWidth);
        System.out.println(name + "(" + IntToBit.to8BitBinary(input) + ") = " +
                         IntToBit.to8BitBinary(result));

        // Manual calculation
        System.out.print("  Manual: ");
        for (int i = 0; i < table.length; i++) {
            int bit = (input >> (inWidth - table[i])) & 1;
            System.out.print(bit);
        }
        System.out.println();
    }
}

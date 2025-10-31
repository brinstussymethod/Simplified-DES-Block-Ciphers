package SDES;
import tools.TenBitKey;
import tools.IntToBit;

import static SDES.SDES.*;


public class MainSDES {

    public static void main(String[] args){
         /* Implement SDES. To verify that your implementation of SDES is correct, try the following test cases:
            TESTING
            Raw Key           Plaintext             Ciphertext

            0000000000     10101010            00010001

            1110001110     10101010            11001010

            1110001110     01010101            01110000

            1111111111     10101010            00000100
        */
        int answer = Encrypt(0b0000000000, 0b10101010);
        System.out.println(IntToBit.to8BitBinary(answer));

        int answer2 = Encrypt(0b1110001110, 0b10101010);
        System.out.println(IntToBit.to8BitBinary(answer2));

        int answer3 = Encrypt(0b1110001110, 0b01010101);
        System.out.println(IntToBit.to8BitBinary(answer3));

        int answer4 = Encrypt(0b1111111111, 0b10101010);
        System.out.println(IntToBit.to8BitBinary(answer4));

        int answerDecrypt = Decrypt(0b1111111111, 0b00000100);
        System.out.println("Decrypted: " + IntToBit.to8BitBinary(answerDecrypt));
        /*
        Use your implementation to complete the following table:

        Raw Key         Plaintext             Ciphertext

        0000000000     00000000                 ?
        1111111111     11111111                 ?
        0000011111     00000000                 ?
        0000011111     11111111                 ?
        1000101110     ?                       00011100
        1000101110     ?                       11000010
        0010011111     ?                       10011101
        0010011111     ?                       10010000
         */
        System.out.println("SDES Problems: ");
        int c1 = Encrypt(0b0000000000, 0b00000000);
        System.out.println(IntToBit.to8BitBinary(c1));   // 11110000

        int c2 = Encrypt(0b1111111111, 0b11111111);
        System.out.println(IntToBit.to8BitBinary(c2));   // 00001111

        int c3 = Encrypt(0b0000011111, 0b00000000);
        System.out.println(IntToBit.to8BitBinary(c3));   // 01000011

        int c4 = Encrypt(0b0000011111, 0b11111111);
        System.out.println(IntToBit.to8BitBinary(c4));   // 11100001

        int p5 = Decrypt(0b1000101110, 0b00011100);
        System.out.println(IntToBit.to8BitBinary(p5));   // 00111000

        int p6 = Decrypt(0b1000101110, 0b11000010);
        System.out.println(IntToBit.to8BitBinary(p6));   // 00001100

        int p7 = Decrypt(0b0010011111, 0b10011101);
        System.out.println(IntToBit.to8BitBinary(p7));   // 11111100

        int p8 = Decrypt(0b0010011111, 0b10010000);
        System.out.println(IntToBit.to8BitBinary(p8));   // 10100101


    }
}

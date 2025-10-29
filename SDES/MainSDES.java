package SDES;
import tools.TenBitKey;
import tools.IntToBit;
import static SDES.SDES.leftShift;
import static SDES.SDES.permuteMSB;


public class MainSDES {

    public static void main(String[] args){
        int key = 0b1010000010;
        int[] P10 = {3,5,2,7,4,10,1,9,8,6};
        int[] P8  = {6,3,7,4,8,5,10,9};

        int p10 = permuteMSB(key, P10, 10);
        int L = (p10 >> 5) & 0x1F, R = p10 & 0x1F;

        System.out.println("P10: " + IntToBit.to10BitBinary(p10));           // 1000001100
        L = leftShift(L,1); R = leftShift(R,1);
        System.out.println("LS1: " + IntToBit.to5BitBinary(L) + " " + IntToBit.to5BitBinary(R)); // 00001 11000
        int K1 = permuteMSB((L<<5)|R, P8, 10);
        System.out.println("K1:  " + IntToBit.to8BitBinary(K1));             // 10100100

        L = leftShift(L,2); R = leftShift(R,2);
        System.out.println("LS2: " + IntToBit.to5BitBinary(L) + " " + IntToBit.to5BitBinary(R)); // 00100 00011
        int K2 = permuteMSB((L<<5)|R, P8, 10);
        System.out.println("K2:  " + IntToBit.to8BitBinary(K2));

        // ADD THE SDES KEY ENCRYPTION SECOND PART 

    }
}

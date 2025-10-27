package SDES;
import tools.TenBitKey; 
import tools.IntToBit;
public class SDES
{
    private String plaintext; 
    private TenBitKey key; 
    private int eightBitKey = 0; 

    public SDES(String plaintext, int key) {
        this.plaintext = plaintext;
        this.key = new TenBitKey(key);  
    }

    public TenBitKey getKey() {
        return key; 
    }

    public void setKey(TenBitKey newKey) {
        this.key = newKey; 
    }

    public String getPlaintext() {
        return plaintext; 
    }

    public void setPlainText(String newPlainText) {
        this.plaintext = newPlainText; 
    }
    public int getEightBit(){
        return eightBitKey; 
    }
    public void setEightBit(int newEightBitKey) {
        this.eightBitKey = newEightBitKey & 0xFF; 
    }

    public static int permuteMSB(int value, int[] table, int inWidth) {
        int outWidth = table.length;
        int result = 0;
        for (int i = 0; i < outWidth; i++) {
            int srcBit = (value >> (inWidth - table[i])) & 1;     // table[i] is MSB-first
            result |= (srcBit << (outWidth - 1 - i));              // write MSB-first
        }
        return result;
    }

    public static int leftShift(int value, int s) {
        return ((value << s) | (value >>> (5 - s))) & 0x1F; 
    }

    public static int[] subkeys(int key10, int[] P10, int[] P8) {
        key10 &= 0x3FF; 
        int p10 = permuteMSB(key10, P10, 10);
        int L = (p10 >> 5) & 0x1F, R = p10 & 0x1F;

        L = leftShift(L, 1); R = leftShift(R, 1);
        int ls1 = (L << 5) | R;
        int K1 = permuteMSB(ls1, P8, 10);

        L = leftShift(L, 2); R = leftShift(R, 2);
        int ls2 = (L << 5) | R;
        int K2 = permuteMSB(ls2, P8, 10);

        return new int[]{K1, K2}; 
    }

    public static void main(String[] args) {
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

    }
}
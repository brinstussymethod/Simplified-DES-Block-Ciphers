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


    }
}
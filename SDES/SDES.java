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
    // Begin encryption algorithm
    static final int[][] S0 = {
            {1, 0, 3, 2},
            {3, 2, 1, 0},
            {0, 2, 1, 3},
            {3, 1, 3, 2}
    };
    static final int[][] S1 = {
            {0, 1, 2, 3},
            {2, 0, 1, 3},
            {3, 0, 1, 0},
            {2, 1, 0, 3}
    };

    static final int[] EP = {4,1,2,3,2,3,4,1};
    static final int[] P4 = {2,4,3,1};

    static int getBit(int v, int posFromLeft, int inSize) {
        int shift = inSize - posFromLeft;
        return (v >> shift) & 1;
    }
    static int permute(int v, int inSize, int[] table) {
        int m = table.length, r = 0;
        for (int i = 0; i < m; i++) {
            int b = getBit(v, table[i], inSize);
            r |= b << (m - 1 - i);
        }
        return r;
    }
    static int sbox(int fourBits, int[][] box) {
        int b1 = (fourBits >> 3) & 1;
        int b2 = (fourBits >> 2) & 1;
        int b3 = (fourBits >> 1) & 1;
        int b4 = fourBits & 1;
        int row = (b1 << 1) | b4;
        int col = (b2 << 1) | b3;
        return box[row][col] & 0b11;
    }

    static int F(int r4, int subkey8) {
        int ep = permute(r4 & 0xF, 4, EP);
        int x = ep ^ (subkey8 & 0xFF);
        int left4 = (x >> 4) & 0xF;
        int right4 = x & 0xF;
        int s0 = sbox(left4, S0);
        int s1 = sbox(right4, S1);
        int out4 = ((s0 & 0x3) << 2) | (s1 & 0x3);
        return permute(out4, 4, P4) & 0xF;
    }

    static int fK(int in8, int subkey8) {
        int L = (in8 >> 4) & 0xF;
        int R = in8 & 0xF;
        int f = F(R, subkey8);
        int Lp = L ^ f;
        return ((Lp & 0xF) << 4) | (R & 0xF);
    }

    static int SW(int in8) {
        int L = (in8 >> 4) & 0xF;
        int R = in8 & 0xF;
        return (R << 4) | L;
    }


    public static int Encrypt(int key, int plaintext) {
        int[] P8Initial = {2, 6, 3, 1, 4, 8, 5, 7};
        int[] P8Final = {4, 1, 3, 5, 7, 2, 8, 6};
        int[] P10 = {3,5,2,7,4,10,1,9,8,6}; // for key
        int[] P8  = {6,3,7,4,8,5,10,9}; // for key

        int initialPermutation = permuteMSB(plaintext, P8Initial, 8);

        // Generate K1 and K2
        int p10 = permuteMSB(key, P10, 10);
        int L = (p10 >> 5) & 0x1F, R = p10 & 0x1F;
        L = leftShift(L,1); R = leftShift(R,1);
        int K1 = permuteMSB((L<<5)|R, P8, 10);
        L = leftShift(L,2); R = leftShift(R,2);
        int K2 = permuteMSB((L<<5)|R, P8, 10);

        //S-DES Process
        int afterFK1 = fK(initialPermutation, K1);
        int afterSW = SW(afterFK1);
        int afterFK2 = fK(afterSW, K2);

        int finalpermutation = permuteMSB(afterFK2, P8Final, 8);

        return finalpermutation;

    }

    public static int Decrypt(int key, int ciphertext) {
        int[] P8Initial = {2, 6, 3, 1, 4, 8, 5, 7};
        int[] P8Final = {4, 1, 3, 5, 7, 2, 8, 6};
        int[] P10 = {3,5,2,7,4,10,1,9,8,6};
        int[] P8  = {6,3,7,4,8,5,10,9};

        int initialPermutation = permuteMSB(ciphertext, P8Initial, 8);

        int p10 = permuteMSB(key, P10, 10);
        int L = (p10 >> 5) & 0x1F, R = p10 & 0x1F;
        L = leftShift(L,1); R = leftShift(R,1);
        int K1 = permuteMSB((L<<5)|R, P8, 10);
        L = leftShift(L,2); R = leftShift(R,2);
        int K2 = permuteMSB((L<<5)|R, P8, 10);

        int afterFK1 = fK(initialPermutation, K2);
        int afterSW = SW(afterFK1);
        int afterFK2 = fK(afterSW, K1);

        int finalpermutation = permuteMSB(afterFK2, P8Final, 8);

        return finalpermutation;
    }


    public static void main(String[] args) {



    }
}
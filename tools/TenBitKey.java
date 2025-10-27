package tools;


public class TenBitKey {
    private int key; 

    public TenBitKey(int initialValue) {
        this.key = initialValue & 0x3FF; 
    }

    public int getkey() {
        return key; 
    }

    public void setkey(int newValue) {
        this.key = newValue & 0x3FF; 
    }
}

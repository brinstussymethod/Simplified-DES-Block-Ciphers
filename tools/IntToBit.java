package tools;

public class IntToBit {
    
    public IntToBit() {}

    public static String to10BitBinary(int value) {
    return String.format("%10s",
            Integer.toBinaryString(value & 0x3FF)).replace(' ', '0');
    }

    public static String to8BitBinary(int value) {
        return String.format("%8s",
            Integer.toBinaryString(value & 0xFF)).replace(' ', '0');
    }

    public static String to4BitBinary(int value) {
        return String.format("%4s",
                Integer.toBinaryString(value & 0xF)).replace(' ', '0');
    }

    public static String to5BitBinary(int value) {
        return String.format("%5s", 
                Integer.toBinaryString(value & 0x1F)).replace(' ', '0'); 
    }

}

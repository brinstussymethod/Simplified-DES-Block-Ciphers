package SDES;

import tools.IntToBit;

public class MainSDES {
    public static void main(String[] args){
        System.out.println("First 10 bit key permutation: ");
        int[] P10 = { 3, 5, 2, 7, 4, 10, 1, 9, 8, 6 }; // 10 bit permutation ! 
        int key = 0b1010000010; // (just an example)
        String Binarykey = IntToBit.to10BitBinary(key); 
        System.out.println(Binarykey); 
        
        int result = SDES.permuteMSB(key, P10, 10); 
        String binaryResult = IntToBit.to10BitBinary(result);  
        System.out.println(binaryResult); 
    }
}

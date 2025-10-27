public class bitpractice
{
    public static void main(String[] args){
        int num = 10;
        int reversed = 0;

        while(num > 0) {
            reversed = (reversed << 1) | (num & 1);
            num >>= 1;
        }
        System.out.println("Reversed bits: " + reversed);
    }
}

/**
 * Write a description of class Flags here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Flags
{
    private int Z = 7; //Zero Flag, bit 7 of F reg, indicates whether the result of the last operation was 0
    private int N = 6; //Subtract Flag, bit 6 of F reg, indicates whether the last operation was a subtraction
    private int H = 5; //Half-Carry Flag, bit 5 of F reg, indicates whether a carry occurred from the lower nibble during the last arithmetic operation
    private int C = 4; //Carry Flag, bit 4 of F reg, indicates whether a carry occurred during the last arithmetic operation
    private int fByte;
    
    public int getFByte() {
        return fByte;
    }
    public int setZ(boolean bit) {
        if (bit)
            return (fByte | (1 << Z));
        return (fByte & ~(1 << Z));
    }
    public int setN(boolean bit) {
        if (bit)
            return (fByte | (1 << N));
        return (fByte & ~(1 << N));
    }
    public int setH(boolean bit) {
        if (bit)
            return (fByte | (1 << H));
        return (fByte & ~(1 << H));
    }
    public int setC(boolean bit) {
        if (bit)
            return (fByte | (1 << C));
        return (fByte & ~(1 << C));
    }
    public boolean checkZ() {
        return (fByte & (1 << Z)) != 0; //Compares the bit at the position of Z to the f register
    }
    public boolean checkN() {
        return (fByte & (1 << N)) != 0;
    }
    public boolean checkH() {
        return (fByte & (1 << H)) != 0;
    }
    public boolean checkC() {
        return (fByte & (1 << C)) != 0;
    }
    public void setFByte(int fByte) {
        this.fByte = fByte & 0xf0;
    }
}

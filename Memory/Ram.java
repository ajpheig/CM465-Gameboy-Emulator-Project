package Memory;

public class Ram {

    // memory space being used for ram. Set the size this.space = new int[0x2000]; // 8KB. Size is usually 8KB or 32KB
    private int[] space;
    // starting address of ram in the memory map
    private int offset;

    public Ram(int offset) {
        this.offset = offset;
        this.space = new int[0x2000]; // 8KB
    }

    // returns true if the address passed in is in the range of memory address assigned to ram
    public boolean accepts(int address) {
        return (address >= offset && address < offset + 0x2000);
    }

    // writes specified byte value to teh address passed in by subtracting offset from the address to get the index of
    // the space array where the byte should be written
    public void setByte(int address, int value) {
        space[address - offset] = value;
    }

    // reads byte from memory at address passed in by subtracting the offset value from the address
    public int getByte(int address) {
        return space[address - offset];
    }
}

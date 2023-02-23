package Memory;

public class Memory {

    private byte[] memory;

    public Memory() {
        memory = new byte[0x10000];// This should initialize memory size to 64 kb
        /*
         * For reference, 0x0000 - 0x00FF should load the boot rom initially to play the
         * splash screen
         * After booting, 0x0000 - 0x3FFF should hold Game ROM Bank 0, data from the
         * game rom
         * Of this,it should be noted that 0x0000 should hold the Interrupt Table to
         * handle interrupt events
         * 0x0100 - 0x014F should hold the cartridge header
         * 0x4000 - 0x7FFF should hold Game ROM Bank N, which holds part of the game's
         * rom and can be switched for other parts, unlike bank 0
         * 0x8000 - 0x97FF should hold Tile RAM, which stores data regarding which tile
         * graphics can be displayed
         * 0x9800 - 0x9FFF should hold the Background Map, which maps data from the Tile
         * RAM onto screen
         * 0xA000 - 0xBFFF should hold the cartridge RAM, which reflects extra RAM that
         * may have been installed on a cartridge
         * 0xC000 - 0xDFFF should hold the working RAM, which is just RAM, an array of
         * memory the game can work with
         * 0xE000 - 0xFDFF should hold Echo RAM, which should mirror working RAM. Not
         * normally used
         * 0xFE00 - 0xFE9F should hold OAM, which holds tileset data for sprites, which
         * are like dynamic tiles with more capablities
         * 0xFEA0 - 0xFEFF should be completely unused. Nothing should be stored here,
         * and changing it should have no effect on gameplay
         * 0xFFFF should hold the Interrupt Enable (IE) Register
         */
    }

    public byte readByte(int address) {
        return memory[address];
    }

    public void writeByte(int address, int value) {
        memory[address] = (byte) value;
    }

    public void writeWord(int location, int u16) {
        writeByte(location, u16 & 0xff);
        writeByte(location + 1, u16 >> 8);
    }

    public int readWord(int location) {

        return readByte(location + 1) << 8 | readByte(location);
    }

    public void writeBytes(int address, byte[] values) {
        System.arraycopy(values, 0, memory, address, values.length);
    }

}

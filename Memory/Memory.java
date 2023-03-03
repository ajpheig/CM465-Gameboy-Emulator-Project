package Memory;

import java.util.Base64;
import java.io.*;
import java.io.File.*;

public class Memory {

    private byte[] memory;
    private byte[] romData;
    private boolean bootRomEnabled = false;
    private File bootFile = new File("C:/Users/ajphe/Documents/Homework/CM465 CIS Capstone/GBVStest/dmg_boot.bin");
    private byte[] bootRom = new byte[(int) bootFile.length()];

    public final static int IE = 0xffff;
    public final static int IF = 0xff0f;

    public Memory(byte[] romData) {
        memory = new byte[0x10000];// This should initialize memory size to 64 kb
        writeBytes(0, romData);
        try {
            FileInputStream is = new FileInputStream(bootFile);
            is.read(bootRom);
            is.close();
        } catch (IOException ioe) {
            ;
        }
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
    // need CPU reference for interrupts
    /*
     * public void setCPU(CPU cpu) {
     * this.cpu = cpu;
     * }
     */

    public int readByte(int address) {
        if (bootRomEnabled == true && address < bootRom.length) {
            System.out.println("| read: " + Integer.toHexString(bootRom[address]) + "|");
            return ((int) bootRom[address] & 0xff);
        }
        return (int) memory[address] & 0xff;
    }

    public void writeByte(int address, int value) {
        if (address == 0xff50) {
            bootRomEnabled = false;
            System.out.println("boot rom disabled");
        }
        if (address == IE) {
            // cpu.interruptManager.intEnableHandler(value);
        }
        if (address == IF) {
            // cpu.interruptManager.intFlagHandler(value);
        }
        memory[address & 0xffff] = (byte) value;
    }

    public void writeWord(int location, int u16) {
        writeByte(location, u16 & 0xff);
        writeByte(location + 1, u16 >> 8);
    }

    public int readWord(int location) {
        return ((readByte(location + 1) << 8) | readByte(location));
    }

    public void writeBytes(int address, byte[] values) {
        System.arraycopy(values, 0, memory, address, values.length);
    }

}

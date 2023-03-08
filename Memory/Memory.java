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
    
    IFRegister IF = new IFRegister();
    IERegister IR = new IERegister();
    LCDC lcdc = new LCDC();
    Stat stat = new Stat();
    WX wx = new WX();
    WY wy = new WY();
    SCX scx = new SCX();
    SCY scy = new SCY();
    BGP bgp = new BGP();
    OBP0 obp0 = new OBP0();
    OBP1 obp1 = new OBP1();
    LY ly = new LY();
    LYC lyc = new LYC();
    DIV div = new DIV();
    TIMA tima = new TIMA();
    TMA tma = new TMA();
    TAC tac = new TAC();
    
    public final static int IE = 0xffff;
    // public final static int IF = 0xff0f;

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
        if (address == 0xff0f) {
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
    private class IERegister extends MemRegisters
    {
        public IERegister()
        {
            location = 0xFFFF;
        }
        public void setVBlank(boolean value)
        {
            this.setBit(0, value);
            memory[location]=this.getByte();
        }
        public void setLCDStat(boolean value)
        {
            this.setBit(1, value);
            memory[location]=this.getByte();
        }
        public void setTimer(boolean value)
        {
            this.setBit(2, value);
            memory[location]=this.getByte();
        }
        public void setSerial(boolean value)
        {
            this.setBit(3, value);
            memory[location]=this.getByte();
        }
        public void setJoypad(boolean value)
        {
            this.setBit(4, value);
            memory[location]=this.getByte();
        }
        /*  IE Register - Controls which interrupts are enabled, an 8-bit register at 0xFFFF
         *      Bit 1 refers to V-Blank Interrupt enable, occurs when LCD Controller enters or leaves V-Blank state
         *      Bit 2 refers to LCD STAT interrupt enable, occurs when LCD Controllers enters a specific mode
         *      Bit 3 refers to Timer Interrupt Enable, occurs when the timer overflows
         *      Bit 4 refers to Serial Interrupt enable, occurs when data is ready to be sent/recieved over serial port
         *      Bit 5 refers to the Joypad Interrupt Enable, occurs when a button on the joypad is pressed
         *      Bits 6-8 are not used*/
    }
    private class IFRegister extends MemRegisters
    {
        public IFRegister()
        {
            location = 0xFF0F;
        }
        public void setVBlank(boolean value)
        {
            this.setBit(0, value);
            memory[location]=this.getByte();
        }
        public void setLCDStat(boolean value)
        {
            this.setBit(1, value);
            memory[location]=this.getByte();
        }
        public void setTimer(boolean value)
        {
            this.setBit(2, value);
            memory[location]=this.getByte();
        }
        public void setSerial(boolean value)
        {
            this.setBit(3, value);
            memory[location]=this.getByte();
        }
        public void setJoypad(boolean value)
        {
            this.setBit(4, value);
            memory[location]=this.getByte();
        }
        public void setIME(boolean value)
        {
            if(value)
                this.setBits(this.getByte(), true, 0, 4);
            else
                this.setBits(this.getByte(), false, 5, 7);
        }
        /* IF Register - Requests interrupts from different sources in the gameboy by enabling a corresponding bit. Located at 0xFF0F
         *      Bit 1 refers to the V-Blank Interrupt Request (INT 40h)
         *      Bit 2 refers to the LCD STAT Interrupt Request (INT 48h)
         *      Bit 3 refers to the Timer Interrupt Request (INT 50h)
         *      Bit 4 refers to the Serial Interrupt Request (INT 58h)
         *      Bit 5 refers to the Joypad Interrupt Request (INT 60H)
         *      Bits 6-8 are not used.*/
    }
    private class LCDC extends MemRegisters
    {
        public LCDC()
        {
            location = 0xFF40;
        }
        public void setBGDisplay(boolean value)
        {
            this.setBit(0, value);
            memory[location]=this.getByte();
        }
        public void setObjDisplay(boolean value)
        {
            this.setBit(1, value);
            memory[location]=this.getByte();
        }
        public void setObjSize(boolean value)
        {
            this.setBit(2, value);
            memory[location]=this.getByte();
        }
        public void setBGTileMap(boolean value)
        {
            this.setBit(3, value);
            memory[location]=this.getByte();
        }
        public void setBGWinSel(boolean value)
        {
            this.setBit(4, value);
            memory[location]=this.getByte();
        }
        public void setWinDisplay(boolean value)
        {
            this.setBit(5, value);
            memory[location]=this.getByte();
        }
        public void setWinTileSel(boolean value)
        {
            this.setBit(6, value);
            memory[location]=this.getByte();
        }
        public void setLCDDisplay(boolean value)
        {
            this.setBit(7, value);
            memory[location]=this.getByte();
        }
        /* LCDC (LCD Control) Register: This 8-bit register controls the display and the behavior of the graphics system
         *          Bit 1: BG Display (Background) Enable
         *          Bit 2: OBJ (Sprite) Display Enable
         *          Bit 3: OBJ (Sprite) Size
         *          Bit 4: BG Tile Map Display Select
         *          Bit 5: BG & Window Tile Data Select
         *          Bit 6: Window Display Enable
         *          Bit 7: WIndow Tile Map Display Select
         *          Bit 8: LCD Display Enable */
    }
    private class Stat extends MemRegisters
    {
        public Stat()
        {
            location = 0xFF41;
        }
        public void setMF1(boolean value)
        {
            this.setBit(0, value);
            memory[location]=this.getByte();
        }
        public void setMF2(boolean value)
        {
            this.setBit(1, value);
            memory[location]=this.getByte();
        }
        public void setCoincidenceFlag(boolean value)
        {
            this.setBit(2, value);
            memory[location]=this.getByte();
        }
        public void setM0HBlank(boolean value)
        {
            this.setBit(3, value);
            memory[location]=this.getByte();
        }
        public void setM1VBlank(boolean value)
        {
            this.setBit(4, value);
            memory[location]=this.getByte();
        }
        public void setM2OAM(boolean value)
        {
            this.setBit(5, value);
            memory[location]=this.getByte();
        }
        public void setLYCCoincidenceInt(boolean value)
        {
            this.setBit(6, value);
            memory[location]=this.getByte();
        }
        
        /* STAT (LCD Status): This register provides information about the current LCD state and controls behavior of LCD interrupt
         *          Bit 1 and 2: Mode Flag
         *          Bit 3: Coincidence Flag
         *          Bit 4: Mode 0 H-Blank Interrupt Enable
         *          Bit 5: Mode 1 V-Blank Interrupt Enable
         *          Bit 6: Mode 2 OAM interrupt Enable
         *          Bit 7: LYC=LY Coincidence Interrupt Enable
         *          Bit 8 is unused*/
    }
    private class WX extends MemRegisters
    {
        public WX()
        {
            location = 0xFF4B;
        }
        public void setWX(byte value)
        {
            this.setByte(value);
            memory[location]=this.getByte();
        }
        //should range from 0 to 166
    }
    private class WY extends MemRegisters
    {
        public WY()
        {
            location = 0xFF4A;
        }
        public void setWY(byte value)
        {
            this.setByte(value);
            memory[location]=this.getByte();
        }
        //should range from 0 to 143
    }
    private class SCX extends MemRegisters
    {
        public SCX()
        {
            location = 0xFF43;
        }
        public void setSCX(byte value)
        {
            this.setByte(value);
            memory[location]=this.getByte();
        }
    }
    private class SCY extends MemRegisters
    {
        public SCY()
        {
            location = 0xFF42;
        }
        public void setSCY(byte value)
        {
            this.setByte(value);
            memory[location]=this.getByte();
        }
    }
    
    private class BGP extends MemRegisters
    {
        public BGP()
        {
            location = 0xFF47;
        }
        public void setWhite(boolean value)
        {
            this.setBit(0, value);
            this.setBit(1, value);
            memory[location]=this.getByte();
        }
        public void setLightGray(boolean value)
        {
            this.setBit(2, value);
            this.setBit(3, value);
            memory[location]=this.getByte();
        }
        public void setDarkGray(boolean value)
        {
            this.setBit(4, value);
            this.setBit(5, value);
            memory[location]=this.getByte();
        }
        public void setBlack(boolean value)
        {
            this.setBit(6, value);
            this.setBit(7, value);
            memory[location]=this.getByte();
        }
    }
    private class LY extends MemRegisters
    {
        public LY()
        {
            location = 0xFF44;
        }
        public void setLY(byte value)
        {
            this.setByte(value);
            memory[location]=this.getByte();
        }
    }
    private class LYC extends MemRegisters
    {
        public LYC()
        {
            location = 0xFF45;
        }
        public void setLYC(byte value)
        {
            this.setByte(value);
            memory[location]=this.getByte();
        }
        public boolean compareLY(byte ly)
        {
            return this.getByte() == ly;
        }
    }
    /*private class OAM
    {
        public OAM()
        {
             oam = new byte[0xA0];
        }
    }*/
    private class OBP0 extends MemRegisters
    {
        public OBP0()
        {
            location = 0xFF48;
        }
        //object palette data 
        public void setWhite(boolean value)
        {
            this.setBit(0, value);
            this.setBit(1, value);
            memory[location]=this.getByte();
        }
        public void setLightGray(boolean value)
        {
            this.setBit(2, value);
            this.setBit(3, value);
            memory[location]=this.getByte();
        }
        public void setDarkGray(boolean value)
        {
            this.setBit(4, value);
            this.setBit(5, value);
            memory[location]=this.getByte();
        }
        public void setBlack(boolean value)
        {
            this.setBit(6, value);
            this.setBit(7, value);
            memory[location]=this.getByte();
        }
    }
    private class OBP1 extends MemRegisters
    {
        public OBP1()
        {
            location = 0xFF49;
        }
        public void setWhite(boolean value)
        {
            this.setBit(0, value);
            this.setBit(1, value);
            memory[location]=this.getByte();
        }
        public void setLightGray(boolean value)
        {
            this.setBit(2, value);
            this.setBit(3, value);
            memory[location]=this.getByte();
        }
        public void setDarkGray(boolean value)
        {
            this.setBit(4, value);
            this.setBit(5, value);
            memory[location]=this.getByte();
        }
        public void setBlack(boolean value)
        {
            this.setBit(6, value);
            this.setBit(7, value);
            memory[location]=this.getByte();
        }
    }
    private class DIV extends MemRegisters
    {
        public DIV()
        {
            location = 0xFF04;
        }
        public void setDIV(byte value)
        {
            this.setByte(value);
            memory[location]=this.getByte();
        }
    }
    private class TIMA extends MemRegisters
    {
        public TIMA()
        {
            location = 0xFF05;
        }
        public void setTIMA(byte value)
        {
            this.setByte(value);
            memory[location]=this.getByte();
        }
    }
    private class TMA extends MemRegisters
    {
        public TMA()
        {
            location = 0xFF06;
        }
        public void setTMA(byte value)
        {
            this.setByte(value);
            memory[location]=this.getByte();
        }
    }
    private class TAC extends MemRegisters
    {
        public TAC()
        {
            location = 0xFF07;
        }
        public void TimerStop(boolean value)
        {
            // 0 stop, 1 go
            this.setBit(2, value);
            memory[location]=this.getByte();
        }
        public void set00()
        {
            //4096Hz
            this.setBit(0, false);
            this.setBit(1, false);
            memory[location]=this.getByte();
        }
        public void set01()
        {
            //262144Hz
            this.setBit(0, true);
            this.setBit(1, false);
            memory[location]=this.getByte();
        }
        public void set10()
        {
            //65536Hz
            this.setBit(0, false);
            this.setBit(1, true);
            memory[location]=this.getByte();
        }
        public void set11()
        {
            //16384Hz
            this.setBit(0, true);
            this.setBit(1, true);
            memory[location]=this.getByte();
        }
    }
    //Sound Registers from here on out
    private class NR10 extends MemRegisters
    {
        public NR10()
        {
            location = 0xFF10;
        }
        public void setSweepChange(boolean value)
        {
            this.setBit(3, value);
            memory[location]=this.getByte();
        }
        public void setSweepShift(byte shift)
        {
            value &= 0xF8;
            value |= shift;
            memory[location]=this.getByte();
        }
        public void setSweepTime(byte sweepTime)
        {
            value = (byte)((value & 0xF) | (sweepTime << 4));
            memory[location]=this.getByte();
        }
    }
    private class NR11 extends MemRegisters
    {
        public NR11()
        {
            location = 0xFF11;
        }
        public void setWavePatternDuty(byte value)
        {
            value <<= 6;
            value &= 0xC0;
            setByte((byte)((getByte() & 0x3F) | value));
            memory[location]=this.getByte();
        }
        public void setSoundLength(byte length)
        {
            value &= 0x3F;
            setByte((byte)((getByte() & 0xC0) | value));
            memory[location]=this.getByte();            
        }
    }
    private class NR12 extends MemRegisters
    {
        public NR12()
        {
            location = 0xFF12;
        }
        public void setInitialVolume(int volume)
        {
            this.setByte((byte)((this.getByte() & 0x0F) | ((volume << 4) & 0xF0)));
            memory[location] = this.getByte();
        }
        public void setEnvelopeDirection(boolean bit)
        {
            this.setBit(3, bit);
            memory[location] = this.getByte();
        }
        public void setEnvelopeSweep(byte period)
        {
            this.setByte((byte)((this.getByte() & 0xF8) | (period & 0x07)));
            memory[location] = this.getByte();
        }
        public boolean isOn()
        {
            return (this.getByte() & 0xF8) != 0;
        }
    }
    private class NR13 extends MemRegisters
    {
        public NR13()
        {
            location = 0xFF13;
        }
        
    }
    private class NR14 extends MemRegisters
    {
        public NR14()
        {
            location = 0xFF14;
        }
    }
    private class NR21 extends MemRegisters
    {
        public NR21()
        {
            location = 0xFF16;
        }
    }
    private class NR22 extends MemRegisters
    {
        public NR22()
        {
            location = 0xFF17;
        }
    }
    private class NR23 extends MemRegisters
    {
        public NR23()
        {
            location = 0xFF18;
        }
    }
    private class NR24 extends MemRegisters
    {
        public NR24()
        {
            location = 0xFF19;
        }
    }
    private class NR30 extends MemRegisters
    {
        public NR30()
        {
            location = 0xFF1A;
        }
    }
    private class NR31 extends MemRegisters
    {
        public NR31()
        {
            location = 0xFF1B;
        }
    }
    private class NR32 extends MemRegisters
    {
        public NR32()
        {
            location = 0xFF1C;
        }
    }
    private class NR33 extends MemRegisters
    {
        public NR33()
        {
            location = 0xFF1D;
        }
    }
    private class NR34 extends MemRegisters
    {
        public NR34()
        {
            location = 0xFF1E;
        }
    }
    private class NR41 extends MemRegisters
    {
        public NR41()
        {
            location = 0xFF20;
        }
    }
    private class NR42 extends MemRegisters
    {
        public NR42()
        {
            location = 0xFF21;
        }
    }
    private class NR43 extends MemRegisters
    {
        public NR43()
        {
            location = 0xFF22;
        }
    }
    private class NR44 extends MemRegisters
    {
        public NR44()
        {
            location = 0xFF23;
        }
    }
    private class NR50 extends MemRegisters
    {
        public NR50()
        {
            location = 0xFF24;
        }
    }
    private class NR51 extends MemRegisters
    {
        public NR51()
        {
            location = 0xFF25;
        }
    }
    private class NR52 extends MemRegisters
    {
        public NR52()
        {
            location = 0xFF26;
        }
    }

}

package Memory;

import java.awt.*;
import java.util.Scanner;
import java.io.*;
import java.io.File.*;
import java.util.Arrays;
import CPU.*;
import GPU.Tile;
import GPU.Sprite;

public class Memory {

    private static final int ROM_BANK_SIZE = 0x4000;
    private static final int RAM_BANK_SIZE = 0x2000;

    private byte[][] romBanks;
    private byte[][] ramBanks;
    private byte[] mbc2Ram;
    private boolean mbc1Enabled;
    private boolean mbc2Enabled;
    private boolean mbc3Enabled;

    private boolean batteryEn;

    private File saveFile;

    private int romBankNumber;
    private int ramBankNumber;
    private boolean ramEnabled;
    private boolean romBankingMode;

    private byte[] memory;
    private boolean bootRomEnabled = true;
    //private File bootFile = new File("C:/Users/ajphe/Documents/Homework/CM465CISCapstone/GBTESTPROJ/dmg_boot.bin");
    private File relBootFile = new File(System.getProperty("user.dir") + "/dmg_boot.bin");
    private File jarredFile = new File("jar:file:WashburnGB.jar!/dmg_boot.bin");
    private byte[] bootRom = new byte[(int) relBootFile.length()];
    CPU cpu;
    Timer timer;
    InterruptManager intMan;
    Tile[] tileSet =new Tile[384];
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
    Joypad joypad = new Joypad();
    LY ly = new LY();
    LYC lyc = new LYC();
    DIV div = new DIV();
    TIMA tima = new TIMA();
    TMA tma = new TMA();
    TAC tac = new TAC();
    OAM oam = new OAM();
    VRAM vram = new VRAM(0x8000);

    public Memory(byte[] romData, String romName) {
        memory = new byte[0x10000 + 1];// This should initialize memory size to 64 kb
        saveFile = new File(romName + ".sav");



        //init MBC
        mbc1Enabled = false;
        mbc2Enabled = false;
        mbc3Enabled = false;
        romBankNumber = 1;
        ramBankNumber = 0;
        ramEnabled = false;
        romBankingMode = true;

        mbc1Enabled = (romData[0x147] == 0x01 || romData[0x147] == 0x02 || romData[0x147] == 0x03);
        mbc2Enabled = (romData[0x147] == 0x05 || romData[0x147] == 0x06);
        mbc3Enabled = (romData[0x147] == 0x0F || romData[0x147] == 0x10 || romData[0x147] == 0x11 || romData[0x147] == 0x12 || romData[0x147] == 0x13);
        batteryEn = (romData[0x147] == 0x03 || romData[0x147] == 0x06 || romData[0x147] == 0x09 || romData[0x147] == 0x0F || romData[0x147] == 0x10 || romData[0x147] == 0x13);

        if(mbc1Enabled || mbc2Enabled || mbc3Enabled)
            System.arraycopy(romData, 0, memory, 0, 0x4000);
        else
            writeBytes(0, romData);

        //writeBytes(0, romData);
        try {
            FileInputStream is = new FileInputStream(relBootFile);
            is.read(bootRom);
            is.close();
        } catch (IOException ioe) {
            ;
        }

        int numRomBanks = 0;

        if(memory[0x148] == 0x00)
            numRomBanks = 2;
        else if(memory[0x148] == 0x01)
            numRomBanks = 4;
        else if(memory[0x148] == 0x02)
            numRomBanks = 8;
        else if(memory[0x148] == 0x03)
            numRomBanks = 16;
        else if(memory[0x148] == 0x04)
            numRomBanks = 32;
        else if(memory[0x148] == 0x05)
            numRomBanks = 64;
        else if(memory[0x148] == 0x6)
            numRomBanks = 128;
        else if(memory[0x148] == 0x07)
            numRomBanks = 256;
        else if(memory[0x148] == 0x08)
            numRomBanks = 512;

        int numRamBanks = 0;

        if(memory[0x149] == 0x00)
            numRamBanks = 0;
        else if(memory[0x149] == 0x02)
            numRamBanks = 1;
        else if(memory[0x149] == 0x03)
            numRamBanks = 4;
        else if(memory[0x149] == 0x04)
            numRamBanks = 16;
        else if(memory[0x149] == 0x05)
            numRamBanks = 8;

        romBanks = new byte[numRomBanks][ROM_BANK_SIZE];
        ramBanks = new byte[numRamBanks][RAM_BANK_SIZE];

        if(saveFile.exists()&&saveFile.length()!=0&&batteryEn)
        {
            //System.out.print("test");
            try {
                FileInputStream in = new FileInputStream(saveFile);
                /*for (int i = 0; i < ramBanks.length; i++) {
                        in.read(ramBanks[i]);
                    }*/
                byte[] rb = new byte[ramBanks.length*ramBanks[0].length];
                for(int i = 0; i < ramBanks.length*ramBanks[0].length; i++) {
                    in.read(rb);
                    //System.out.print(rb[i] + " ");
                }

                for(int i = 0; i < ramBanks.length; i++) {
                    for(int j = 0; j < ramBanks[i].length; j++) {
                        ramBanks[i][j] = rb[i*ramBanks[i].length + j];
                    }
                }
                in.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            catch(IOException ioe){}
        }
        /*for(int q = 0; q < romBanks.length; q++)
        {
            for(int j = 0; j<romBanks[q].length;j++)
            {
                System.out.print(romBanks[q][j]);
            }
        }*/

        if (!saveFile.exists() && batteryEn) {
            try {
                saveFile.createNewFile();

            } catch(IOException e){
                e.printStackTrace();
            }
        }

        mbc2Ram = new byte[0x200];
        for(int i = 0; i < numRomBanks; i++)
        {
            for(int j = 0; j < ROM_BANK_SIZE; j++)
            {
                romBanks[i][j] = romData[i * ROM_BANK_SIZE + j];
            }
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

    // getters for the PPU


    public byte[][] getRamBanks() {
        return ramBanks;
    }

    public Stat getStat() {
        return stat;
    }


    public LCDC getLcdc() {
        return lcdc;
    }
    public OBP0 getObp0(){return obp0;}
    public OBP1 getObp1(){return obp1;}

    public OAM getOam() {
        return oam;
    }

    public BGP getBgp() {
        return bgp;
    }
    public LYC getLYC() {return lyc;}
    public LY getLY() {return ly;}
    public VRAM getVram() {
        return this.vram;
    }

    public void setCPU(CPU cpu, InterruptManager intMan, Timer timer) {
        this.cpu = cpu;
        this.intMan = intMan;
        this.timer=timer;
    }

    public int readByte(int address) {
        if (bootRomEnabled == true && address < bootRom.length) {
            // System.out.println("| read: " + Integer.toHexString(bootRom[address]) + "|");
            return ((int) bootRom[address] & 0xff);
        }
        if(address>=0x8000&&address<=0x97ff) {
            return vram.getByte(address);
        }
        if (address == 0xff00) {// JOYPAD
            if(((memory[0xff00]>>5)&1)==0)//if button keys
            {
                return joypad.readActionButtons();
            }
            if(((memory[0xff00]>>4)&1)==0)//if direction keys
            {
                //System.out.println(Integer.toBinaryString(joypad.readDirButtons()));
                return joypad.readDirButtons();
            }
            else return 0xff;
        }
        if (address == 0xff04) {//DIV register
            return timer.getDiv();
        }
        if (address == 0xff05) {//TIMA register
            return timer.getTIMA();
        }
        if (address == 0xff0f) {
            return (memory[address]|0xE0);
        }
        if (address == 0xff40) {
            return lcdc.getByte();
        }
        if (address == 0xff42) {
            return scy.getByte();
        }
        if (address == 0xff43) {
            return scx.getByte();
        }
        if (address == 0xff45) {
            return lyc.getByte();
        }
        if (address == 0xff47) {
            return bgp.getByte();
        }
        if (address == 0xff48) {//obp0
            return obp0.getByte();
        }
        if (address == 0xff49) {//obp1
            return obp1.getByte();
        }
        if (address == 0xff4a) {
            return wy.getByte();
        }
        if (address == 0xff4b) {
            return wx.getByte();
        }
        //if (address >= 0xFE00 & address < 0xFEA0)
        //    return oam.readByte(address);
        if(mbc1Enabled == true)
        {
            if(address < 0x4000)
                return romBanks[0][address] & 0xff;
            else if(address < 0x8000)
            {
                int bank = romBankNumber & 0x7F;
                if (bank == 0x00 || bank == 0x20 || bank == 0x40 || bank == 0x60)
                    bank++;
                if (romBankingMode)
                    return romBanks[bank][address - 0x4000] & 0xff;
                else
                {
                    return romBanks[romBankNumber << 5 | (romBankNumber & 0x1F)][address - 0x4000] & 0xff;
                }

            }
            else if(address >= 0xA000 && address < 0xC000 && ramEnabled)
            {
                int bank = ramBankNumber & 0x3;
                return ramBanks[bank][address - 0xA000] & 0xff;
            }
            else
                return Byte.toUnsignedInt(memory[address]) & 0xff;
        }
        else if(mbc2Enabled)
        {
            if (address < 0x4000)
                return romBanks[0][address];
            else if (address < 0x8000)
                return romBanks[romBankNumber & 0x0F][address - 0x4000] & 0xff;
            else if(address >= 0xA000 && address < 0xC000 && ramEnabled)
            {
                return mbc2Ram[address & 0x1FFF] & 0xff;
            }
        }
        else if(mbc3Enabled)
        {
            if(address < 0x4000)
                return romBanks[0][address] & 0xff;
            else if(address < 0x8000)
            {
                int bank;
                if(romBankingMode)
                    bank = romBankNumber;
                else
                    bank = ramBankNumber & 0x3;
                return romBanks[bank][address-0x4000] & 0xff;
            }
            else if (address >= 0xA000 && address < 0xC000 && ramEnabled)
            {
                return ramBanks[ramBankNumber & 0x03][address - 0xA000] & 0xff;
            }
            else
                return Byte.toUnsignedInt(memory[address]) & 0xff;
        }
        if(address>=65536) return 0xff;
            //return memory[address];
        else
            return  Byte.toUnsignedInt(memory[address]) & 0xff;
    }
    public void writeByte(int address, int value) {
        if (memory[0x0147]==0&&address ==0x2000) return;//0x147 is the cartridge type, if =ZERO then MBC0(no mem)
        if (address == 0xff50) {
            bootRomEnabled = false;
            //System.out.println("boot rom disabled");
        }
        if (address == 0xffff) {
            intMan.intEnableHandler(value);
        }
        if (address == 0xff0f) {//IF Flag
            boolean interrupted = intMan.intFlagHandler(value);
            if (interrupted)
                return;// interrupted=whether the IME and a IE flag are on
        }
        if (address == 0xff44) {// LY
            ly.setLY((byte)value);
            return;
        }
        if(address == 0xff46) { //DMA transfer register
            performDMA((byte)value);
        }
        if (address == 0xff00) {// joypad
            memory[0xff00]=(byte)value;//cpu POLLS for DIRECTION and ACTION with this
            return;//Prevent cpu from writing to joypad
        }
        if(address>=0x8000&&address<=0x97ff) {
            vram.setByte(address,value);
            //System.out.print(Integer.toHexString(address)+" ");
        }
        if (address == 0xff04) {// any value written set DIV to zero
            timer.resetDiv();
            memory[0xff04] = 0;
            return;
        }
        if (address == 0xff40) {
            lcdc.setByte((byte)value);
        }
        if (address == 0xff42) {
            scy.setByte((byte) value);
        }
        if (address == 0xff43) {
            scx.setByte((byte) value);
        }
        if (address == 0xff45) {
            lyc.setByte((byte) value);
        }
        if (address == 0xff47) {
            bgp.setByte((byte)value);
        }
        if (address == 0xff48) {
            obp0.setByte((byte)value);
        }
        if (address == 0xff49) {
            obp1.setByte((byte)value);
        }
        if (address == 0xff4a) {
            wy.setByte((byte) value);
        }
        if (address == 0xff4b) {
            wx.setByte((byte) value);
        }
        if (address >= 0xFE00 && address < 0xFEA0)
            oam.writeByte(address, (byte) value);
        if(mbc1Enabled == true)
        {
            if(address < 0x2000)
                ramEnabled = (value & 0x0A) == 0x0A;
            else if(address < 0x4000)
            {
                romBankNumber = value &0x1F;
                if(romBankNumber == 0x00)
                    romBankNumber = 0x01;
            }
            else if(address < 0x6000)
            {
                if(romBankingMode)
                    ramBankNumber = value & 0x03;
                else
                    romBankNumber = (romBankNumber & 0x1F) | ((value & 0x03) << 5);
            }
            else if(address < 0x8000)
                romBankingMode = (value & 0x01) == 0x01;
            else if(address >= 0xA000 & address < 0xC000 && ramEnabled)
            {
                int bank = ramBankNumber & 0x3;
                ramBanks[bank][address - 0xA000] = (byte)value;
                /*if(batteryEn)
                {
                    try {
                        FileWriter w = new FileWriter(saveFile);
                        for (int i = 0; i < ramBanks.length; i++) {
                            for (int j = 0; j < ramBanks[i].length; j++) {
                                w.write(ramBanks[i][j] + " ");
                            }
                            w.write("\n");
                        }
                        w.close();
                    }
                    catch(IOException e) {
                        System.out.println("An error occurred while writing to the save file: " + e.getMessage());
                    }

                }*/
            }
            else
                memory[address & 0xffff] = (byte) value;
        }
        else if(mbc2Enabled == true)
        {
            if(address < 0x2000)
            {
                if((address & 0x0100) == 0)
                {
                    ramEnabled = ((value & 0x0F) == 0x0A);
                }
            }
            else if(address >= 0xA000 && address < 0xC000 && ramEnabled)
            {
                mbc2Ram[address & 0x01FF] =(byte)(value & 0x0F);
            }
            else
                memory[address & 0xffff] = (byte) value;
        }
        else if(mbc3Enabled == true)
        {
            if(address < 0x2000)
                ramEnabled = ((value & 0x0A) == 0x0A);
            else if(address < 0x4000)
            {
                int romBankNumberLower7Bits = value & 0x7F;
                if(romBankNumberLower7Bits == 0x00)
                    romBankNumberLower7Bits = 0x01;
                romBankNumber = (romBankNumber & 0x180) | romBankNumberLower7Bits;
            }
            else if(address < 0x6000)
            {
                if(value >= 0x00 && value <= 0x03)
                    ramBankNumber = value;
                else if(value >= 0x08 && value <= 0x0C)
                {
                    //This is where RTC lives

                }
            }
            else if(address < 0x8000)
            {
                //latch clock data
            }
            else if(address >= 0xA000 && address < 0xC000 && ramEnabled)
            {
                ramBanks[ramBankNumber & 0x03][address - 0xA000] = (byte)value;
                /*if(batteryEn)
                {
                    try {
                        FileWriter w = new FileWriter(saveFile);
                        for (int i = 0; i < ramBanks.length; i++) {
                            for (int j = 0; j < ramBanks[i].length; j++) {
                                w.write(ramBanks[i][j] + " ");
                            }
                            w.write("\n");
                        }
                        w.close();
                    }
                    catch(IOException e) {
                        System.out.println("An error occurred while writing to the save file: " + e.getMessage());
                    }

                }*/
            }
            else
                memory[address & 0xffff] = (byte)value;
        }
        else
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

    private class IERegister extends MemRegisters {
        public IERegister() {
            location = 0xFFFF;
        }

        public void setVBlank(boolean value) {
            this.setBit(0, value);
            memory[location] = (byte)this.getByte();
        }

        public void setLCDStat(boolean value) {
            this.setBit(1, value);
            memory[location] = (byte)this.getByte();
        }

        public void setTimer(boolean value) {
            this.setBit(2, value);
            memory[location] = (byte)this.getByte();
        }

        public void setSerial(boolean value) {
            this.setBit(3, value);
            memory[location] = (byte)this.getByte();
        }

        public void setJoypad(boolean value) {
            this.setBit(4, value);
            memory[location] = (byte)this.getByte();
        }
        /*
         * IE Register - Controls which interrupts are enabled, an 8-bit register at
         * 0xFFFF
         * Bit 1 refers to V-Blank Interrupt enable, occurs when LCD Controller enters
         * or leaves V-Blank state
         * Bit 2 refers to LCD STAT interrupt enable, occurs when LCD Controllers enters
         * a specific mode
         * Bit 3 refers to Timer Interrupt Enable, occurs when the timer overflows
         * Bit 4 refers to Serial Interrupt enable, occurs when data is ready to be
         * sent/recieved over serial port
         * Bit 5 refers to the Joypad Interrupt Enable, occurs when a button on the
         * joypad is pressed
         * Bits 6-8 are not used
         */
    }

    private class IFRegister extends MemRegisters {
        public IFRegister() {
            location = 0xFF0F;
        }

        public void setVBlank(boolean value) {
            this.setBit(0, value);
            memory[location] = (byte)this.getByte();
        }

        public void setLCDStat(boolean value) {
            this.setBit(1, value);
            memory[location] = (byte)this.getByte();
        }

        public void setTimer(boolean value) {
            this.setBit(2, value);
            memory[location] = (byte)this.getByte();
        }

        public void setSerial(boolean value) {
            this.setBit(3, value);
            memory[location] = (byte)this.getByte();
        }

        public void setJoypad(boolean value) {
            this.setBit(4, value);
            memory[location] = (byte)this.getByte();
        }

        public void setIME(boolean value) {
            if (value)
                this.setBits((byte)this.getByte(), true, 0, 4);
            else
                this.setBits((byte)this.getByte(), false, 5, 7);
        }
        /*
         * IF Register - Requests interrupts from different sources in the gameboy by
         * enabling a corresponding bit. Located at 0xFF0F
         * Bit 1 refers to the V-Blank Interrupt Request (INT 40h)
         * Bit 2 refers to the LCD STAT Interrupt Request (INT 48h)
         * Bit 3 refers to the Timer Interrupt Request (INT 50h)
         * Bit 4 refers to the Serial Interrupt Request (INT 58h)
         * Bit 5 refers to the Joypad Interrupt Request (INT 60H)
         * Bits 6-8 are not used.
         */
    }

    // video ram
    public class VRAM extends MemRegisters {
        private byte[] space;

        // starting address of VRAM in the memory map
        private int offset;

        public VRAM(int offset) {
            this.offset = offset;
            this.space = new byte[0x2000]; // 8KB
            for(int i=0;i<tileSet.length;i++) {
                tileSet[i]=new Tile();
            }
        }

        // returns true if the address passed in is in the range of memory address
        // assigned to VRAM
        public boolean accepts(int address) {
            return (address >= offset && address < offset + 8192);
        }

        // writes specified byte value to the address passed in by subtracting offset
        // from the address to get the index of
        // the space array where the byte should be written
        public void setByte(int address, int value) {
            int index=address-offset;
            space[index] = (byte) value;
            int normIndex=index&0xfffe;
            int byte1=space[normIndex]&0xff;
            int byte2=space[normIndex+1]&0xff;
            int tileIndex=index/16;
            int rowIndex=(index%16)/2;
            for (int i=0;i<8;i++) {
                int mask=1<<(7-i);
                int lsb=(byte1&0xff)&mask;
                int msb=(byte2&0xff)&mask;
                int val=-1;
                if((msb!=0)&&(lsb!=0)) val=3;
                if((msb!=0)&&(lsb==0)) val=2;
                if((msb==0)&&(lsb!=0)) val=1;
                if((msb==0)&&(lsb==0)) val=0;
                //System.out.println(Integer.toHexString(tileIndex)+" b1:"+Integer.toHexString(byte1)+" b2:"+Integer.toHexString(byte2)
                //  +" val:"+val+" @row:"+rowIndex+", col:"+i+" MSB:"+msb+" LSB:"+lsb);
                if(tileSet[tileIndex]!=null)tileSet[tileIndex].setVal(rowIndex,i,val);
                //NOTE: i= pixelindex, x, col#
            }
        }

        // reads byte from memory at address passed in by subtracting the offset value
        // from the address
        public int getByte(int address) {
            return space[address - offset] & 0xFF;
        }
        public Tile[] getTileSet() {
            return tileSet;
        }
    }
    public Joypad getJoypad() {
        return joypad;
    }

    public class LCDC extends MemRegisters {
        public LCDC() {
            location = 0xFF40;
        }

        public void setBGDisplay(boolean value) {
            this.setBit(0, value);
            memory[location] = (byte)this.getByte();
        }

        public void setObjDisplay(boolean value) {
            this.setBit(1, value);
            memory[location] = (byte)this.getByte();
        }

        public void setObjSize(boolean value) {
            this.setBit(2, value);
            memory[location] = (byte)this.getByte();
        }

        public void setBGTileMap(boolean value) {
            this.setBit(3, value);
            memory[location] = (byte)this.getByte();
        }

        public void setBGWinSel(boolean value) {
            this.setBit(4, value);
            memory[location] = (byte)this.getByte();
        }

        public void setWinDisplay(boolean value) {
            this.setBit(5, value);
            memory[location] = (byte)this.getByte();
        }

        public void setWinTileSel(boolean value) {
            this.setBit(6, value);
            memory[location] = (byte)this.getByte();
        }

        public void setLCDDisplay(boolean value) {
            this.setBit(7, value);
            memory[location] = (byte)this.getByte();
        }

        public boolean getBGTileDataSelect() {
            return this.getBit(4);
        }
        public boolean getWinTileSelect() {
            return this.getBit(6);
        }
        /*
         * LCDC (LCD Control) Register: This 8-bit register controls the display and the
         * behavior of the graphics system
         * Bit 1: BG Display (Background) Enable
         * Bit 2: OBJ (Sprite) Display Enable
         * Bit 3: OBJ (Sprite) Size
         * Bit 4: BG Tile Map Display Select
         * Bit 5: BG & Window Tile Data Select
         * Bit 6: Window Display Enable
         * Bit 7: Window Tile Map Display Select
         * Bit 8: LCD Display Enable
         */
    }

    public class Stat extends MemRegisters {
        public Stat() {
            location = 0xFF41;
            setBit(7,true);//read as on but does nothing
        }

        public void setMF1(boolean value) {
            this.setBit(0, value);
            memory[location] = (byte)this.getByte();
        }

        public void setMF2(boolean value) {
            this.setBit(1, value);
            memory[location] = (byte)this.getByte();
        }

        public boolean isM2OAMEnabled() {
            return ((getByte() >> 5) & 0x1) == 1;
        }

        public void setMF3(boolean value) {
            this.setBit(1, true);
            this.setBit(2, true);
            memory[location] = (byte)this.getByte();
        }

        public void setCoincidenceFlag(boolean value) {
            this.setBit(2, value);
            memory[location] = (byte)this.getByte();
        }

        public void setM0HBlank(boolean value) {
            this.setBit(3, value);
            memory[location] = (byte)this.getByte();
        }

        public void setM1VBlank(boolean value) {
            this.setBit(4, value);
            memory[location] = (byte)this.getByte();
        }

        public void setM2OAM(boolean value) {
            this.setBit(5, value);
            memory[location] = (byte)this.getByte();
        }

        public void setLYCCoincidenceInt(boolean value) {
            this.setBit(6, value);
            memory[location] = (byte)this.getByte();
        }

        /*
         * STAT (LCD Status): This register provides information about the current LCD
         * state and controls behavior of LCD interrupt
         * Bit 1 and 2: Mode Flag
         * Bit 3: Coincidence Flag
         * Bit 4: Mode 0 H-Blank Interrupt Enable
         * Bit 5: Mode 1 V-Blank Interrupt Enable
         * Bit 6: Mode 2 OAM interrupt Enable
         * Bit 7: LYC=LY Coincidence Interrupt Enable
         * Bit 8 is unused
         */
    }

    private class WX extends MemRegisters {
        public WX() {
            location = 0xFF4B;
        }

        public void setWX(byte value) {
            this.setByte(value);
            memory[location] = (byte)this.getByte();
        }
        // should range from 0 to 166
    }

    private class WY extends MemRegisters {
        public WY() {
            location = 0xFF4A;
        }

        public void setWY(byte value) {
            this.setByte(value);
            memory[location] = (byte)this.getByte();
        }
        // should range from 0 to 143
    }

    private class SCX extends MemRegisters {
        public SCX() {
            location = 0xFF43;
        }

        public void setSCX(byte value) {
            this.setByte(value);
            memory[location] = (byte)this.getByte();
        }
    }

    private class SCY extends MemRegisters {
        public SCY() {
            location = 0xFF42;
        }

        public void setSCY(byte value) {
            this.setByte(value);
            memory[location] = (byte)this.getByte();
        }
    }

    public class BGP extends MemRegisters {
        private int[] colorMap =new int[4];
        public BGP() {
            location = 0xFF47;
        }
        @Override
        public void setByte(int value) {
            this.value = value&0xff;
            int index = this.value&3;
            for (int i = 0; i < 4 ; i++) {
                index=(this.value>>(2*i))&3;
                colorMap[i] = index;
                //System.out.println("BGP"+Integer.toBinaryString(this.value)+" "+index);
            }
        }
        public void setWhite(boolean value) {
            this.setBit(0, value);
            this.setBit(1, value);
            memory[location] = (byte)this.getByte();
        }

        public void setLightGray(boolean value) {
            this.setBit(2, value);
            this.setBit(3, value);
            memory[location] = (byte)this.getByte();
        }

        public void setDarkGray(boolean value) {
            this.setBit(4, value);
            this.setBit(5, value);
            memory[location] = (byte)this.getByte();
        }

        public void setBlack(boolean value) {
            this.setBit(6, value);
            this.setBit(7, value);
            memory[location] = (byte)this.getByte();
        }

        public int getColor(int palette, int colorMode) {
            //int color = (this.getByte() >> (palette * 2)) & 0x3;
            //System.out.println("pal"+palette+"actClr:"+getColorFromArray(colorMap[color]));
            return getColorFromArray(colorMap[palette]);
        }

    }

    public class LY extends MemRegisters {
        public LY() {
            location = 0xFF44;
        }

        public void setLY(byte value) {
            this.setByte(value);
            memory[location] = (byte)this.getByte();
        }
    }

    public class LYC extends MemRegisters {
        public LYC() {
            location = 0xFF45;
        }

        public void setLYC(byte value) {
            this.setByte(value);
            memory[location] = (byte)this.getByte();
        }

        public boolean compareLY(byte ly) {
            return this.getByte() == ly;
        }
    }

    public class OAM {
        byte[] data;
        private int location;
        private final int OAM_SIZE = 0xA0;
        private final int SPRITE_COUNT = 40;
        private final int SPRITE_SIZE = 4;
        private final int ATTR_PALETTE = 0x10;
        private final int ATTR_FLIP_H = 0x20;
        private final int ATTR_FLIP_V = 0x40;
        private final int ATTR_PRIORITY = 0x80;

        public OAM() {
            this.data = new byte[0xA0];//0xA0 = 160 spaces
            this.location = 0xFE00;
            // located between 0xFE00 and 0xFE9F
        }

        public void setSpriteData(byte[] data) {
            this.data = data;
            // System.out.println(Arrays.toString(this.data));
        }
        public byte readByte(int address) {
            return data[address];
        }

        public void writeByte(int address, byte value) {
            data[address - location] = value;
            memory[address] = value;
        }

        public byte[] getSpriteData(int spriteIndex) {
            byte[] spriteData = new byte[SPRITE_SIZE];
            int spriteStartAddress = spriteIndex * SPRITE_SIZE;
            for (int i = 0; i < SPRITE_SIZE; i++) {
                spriteData[i] = data[spriteStartAddress + i];
            }
            return spriteData;
            // given one of the 40 sprites in OAM, returns details about it with
            // byte 1 being y location
            // byte 2 being x location
            // byte 3 being tile number
            // byte 4 being flags
        }

        // makes sprite address objects and adds them to  array in sprite class if they are on
        // the current scanline to be used during VRAM read. curY is the current scanline.
        public void checkSpriteY(int curY){
            for (int i = 0; i < 160; i+=4)
            {
                //System.out.println(curY);IS GOOD! ALL 144 cury
                //if (between(curY - 16, this.data[i * 4] & 0xff, curY))
                {
                    //if(this.data[i * 4] == curY){
                    // can make this more efficient by setting variables instead of using
                    // this.data[i + number] every time
                    // the sprite overlaps with the current scanline, so we make a sprite object and add it to the array
                    // have to add an extra check for the case where the sprites y cord is 0 and the current scanline is
                    // 0, so it doesn't think an empty oam has every sprite on the first line
                    // cant check if they are != 0 because of how they bytes can be read as negative signed ints
                    // System.out.println("SPRITE OVERLAPPING WITH SCNALINE");

                    byte y = this.data[i];
                    byte x = this.data[i + 1];
                    byte tileNum = this.data[i + 2];
                    byte flags = this.data[i + 3];
                    // if (((y == (byte)0) && (x == (byte)0) && (tileNum == (byte)0) && (flags == (byte)0) && (this.data[i + 3] != (byte)0 || curY != 16)) )
                    {
                        //do nothing
                        //System.out.println("all 0 vals");
                    }
                    // check if sprite's x cord is on the screen
                    if((x&0xff) >= 0 && (x&0xff) <= 168)
                    {
                        //System.out.println("MAKING SPRITE OBJ");
                        Sprite sprite = new Sprite(this.data[i], this.data[i + 1], this.data[i + 2], this.data[i + 3]);
                        //printSpriteData();
                        // print in decimal but matches the hex value
                        //System.out.println("Y val " + (sprite.getY()&0xff) + " X value " + (sprite.getX()&0xff) + " tile # " + (sprite.getTileNumber()&0xff) + " flages " + (sprite.getFlags()&0xff) + " on scanline " + curY);
                        //System.out.println();
                    }
                }
            }
        }

        private  boolean between(int from, int x, int to) {
            boolean result;

            result =  from <= x && x < to;
            return result;
        }

        public void printSpriteData() {
            StringBuilder sb = new StringBuilder();
            int pipeCount = 0;
            for (int i = 0; i < this.data.length; i++) {
                sb.append(String.format("%02X ", this.data[i]));
                if ((i + 1) % 4 == 0) {
                    sb.append("| ");
                    pipeCount++;
                    if (pipeCount % 8 == 0) {
                        sb.append("\n");
                    }
                }
            }
            System.out.println(sb.toString());
        }
        public boolean isSpriteEnabled(int spriteIndex) {
            int spriteStartAddress = spriteIndex * SPRITE_SIZE;
            return data[spriteStartAddress] != 0;
        }

        public int getSpriteX(int spriteIndex) {
            int spriteStartAddress = spriteIndex * SPRITE_SIZE;
            return Byte.toUnsignedInt(data[spriteStartAddress + 1]) - 8;
        }

        public int getSpriteY(int spriteIndex) {
            int spriteStartAddress = spriteIndex * SPRITE_SIZE;
            return Byte.toUnsignedInt(data[spriteStartAddress]) - 16;
        }

        public int getSpriteFlags(int spriteIndex) {
            int spriteStartAddress = spriteIndex * SPRITE_SIZE;
            return Byte.toUnsignedInt(data[spriteStartAddress + 3]);
        }

        public int getSpriteHeight(int spriteIndex) {
            byte[] spriteData = getSpriteData(spriteIndex);
            int tileHeight = 8;
            int height = 0;
            for (int i = 0; i < tileHeight; i++) {
                int tileRowOffset = i * 2;
                if ((spriteData[tileRowOffset] | spriteData[tileRowOffset + 1]) != 0) {
                    height = (i + 1) * tileHeight;
                }
            }
            return height;
        }

        public int getSpritePalette(int spriteIndex) {
            int attributes = getSpriteFlags(spriteIndex);
            return (attributes & ATTR_PALETTE) == 0 ? 0 : 1;
        }

        public boolean isSpriteFlippedHorizontally(int spriteIndex) {
            int attributes = getSpriteFlags(spriteIndex);
            return (attributes & ATTR_FLIP_H) != 0;
        }

        public boolean isSpriteFlippedVertically(int spriteIndex) {
            int attributes = getSpriteFlags(spriteIndex);
            return (attributes & ATTR_FLIP_V) != 0;
        }

        public boolean hasSpritePriority(int spriteIndex) {
            int attributes = getSpriteFlags(spriteIndex);
            return (attributes & ATTR_PRIORITY) == 0;
        }
    }//

    public void performDMA(byte value)
    {
        int baseAddress = (value << 8)&0xffff;
        for (int i = 0; i < 0xA0; i++)
        {
            memory[0xFE00 + i] = memory[baseAddress + i];
        }
    }

    public class OBP0 extends MemRegisters {
        private int[] colorMap =new int[4];
        public OBP0() {
            location = 0xFF48;
        }
        @Override
        public void setByte(int value) {
            this.value = value&0xff;
            int index = this.value&3;
            for (int i = 0; i < 4 ; i++) {
                index=(this.value>>(2*i))&3;
                colorMap[i] = index;
                //System.out.println("OBP0"+Integer.toBinaryString(this.value)+" "+index);
            }
        }


        // object palette data
        public void setWhite(boolean value) {
            this.setBit(0, value);
            this.setBit(1, value);
            memory[location] = (byte)this.getByte();
        }

        public void setLightGray(boolean value) {
            this.setBit(2, value);
            this.setBit(3, value);
            memory[location] = (byte)this.getByte();
        }

        public void setDarkGray(boolean value) {
            this.setBit(4, value);
            this.setBit(5, value);
            memory[location] = (byte)this.getByte();
        }

        public void setBlack(boolean value) {
            this.setBit(6, value);
            this.setBit(7, value);
            memory[location] = (byte)this.getByte();
        }
        public int getByte() {
            return memory[location];
        }
        public int getColor(int palette, int colorMode) {
            //int color = (this.getByte() >> (palette * 2)) & 0x3;
            //System.out.println("pal"+palette+"actClr:"+getColorFromArray(colorMap[color]));
            return getColorFromArray(colorMap[palette]);
        }

    }

    public class OBP1 extends MemRegisters {
        private int[] colorMap =new int[4];
        public OBP1() {
            location = 0xFF49;
        }
        @Override
        public void setByte(int value) {
            this.value = value&0xff;
            int index = this.value&3;
            for (int i = 0; i < 4 ; i++) {
                index=(this.value>>(2*i))&3;
                colorMap[i] = index;
                //System.out.println("OBP1"+Integer.toBinaryString(this.value)+" "+index);
            }
        }


        public void setWhite(boolean value) {
            this.setBit(0, value);
            this.setBit(1, value);
            memory[location] = (byte)this.getByte();
        }

        public void setLightGray(boolean value) {
            this.setBit(2, value);
            this.setBit(3, value);
            memory[location] = (byte)this.getByte();
        }

        public void setDarkGray(boolean value) {
            this.setBit(4, value);
            this.setBit(5, value);
            memory[location] = (byte)this.getByte();
        }

        public void setBlack(boolean value) {
            this.setBit(6, value);
            this.setBit(7, value);
            memory[location] = (byte)this.getByte();
        }
        public int getByte() {
            return memory[location];
        }

        public int getColor(int palette, int colorMode) {
            //int color = (this.getByte() >> (palette * 2)) & 0x3;

            return getColorFromArray(colorMap[palette]);
        }
    }

    private class DIV extends MemRegisters {
        public DIV() {
            location = 0xFF04;
        }

        public void setDIV(byte value) {
            this.setByte(value);
            memory[location] = (byte)this.getByte();
        }
    }

    private class TIMA extends MemRegisters {
        public TIMA() {
            location = 0xFF05;
        }

        public void setTIMA(byte value) {
            this.setByte(value);
            memory[location] = (byte)this.getByte();
        }
    }

    private class TMA extends MemRegisters {
        public TMA() {
            location = 0xFF06;
        }

        public void setTMA(byte value) {
            this.setByte(value);
            memory[location] = (byte)this.getByte();
        }
    }

    private class TAC extends MemRegisters {
        public TAC() {
            location = 0xFF07;
        }

        public void TimerStop(boolean value) {
            // 0 stop, 1 go
            this.setBit(2, value);
            memory[location] = (byte)this.getByte();
        }

        public void set00() {
            // 4096Hz
            this.setBit(0, false);
            this.setBit(1, false);
            memory[location] = (byte)this.getByte();
        }

        public void set01() {
            // 262144Hz
            this.setBit(0, true);
            this.setBit(1, false);
            memory[location] = (byte)this.getByte();
        }

        public void set10() {
            // 65536Hz
            this.setBit(0, false);
            this.setBit(1, true);
            memory[location] = (byte)this.getByte();
        }

        public void set11() {
            // 16384Hz
            this.setBit(0, true);
            this.setBit(1, true);
            memory[location] = (byte)this.getByte();
        }
    }

    // Sound Registers from here on out
    private class NR10 extends MemRegisters {
        public NR10() {
            location = 0xFF10;
        }

        public void setSweepChange(boolean value) {
            this.setBit(3, value);
            memory[location] = (byte)this.getByte();
        }

        public void setSweepShift(byte shift) {
            value &= 0xF8;
            value |= shift;
            memory[location] = (byte)this.getByte();
        }

        public void setSweepTime(byte sweepTime) {
            value = (byte) ((value & 0xF) | (sweepTime << 4));
            memory[location] = (byte)this.getByte();
        }
    }

    private class NR11 extends MemRegisters {
        public NR11() {
            location = 0xFF11;
        }

        public void setWavePatternDuty(byte value) {
            value <<= 6;
            value &= 0xC0;
            setByte((byte) ((getByte() & 0x3F) | value));
            memory[location] = (byte)this.getByte();
        }

        public void setSoundLength(byte length) {
            value &= 0x3F;
            setByte((byte) ((getByte() & 0xC0) | value));
            memory[location] = (byte)this.getByte();
        }
    }

    private class NR12 extends MemRegisters {
        public NR12() {
            location = 0xFF12;
        }

        public void setInitialVolume(int volume) {
            this.setByte((byte) ((this.getByte() & 0x0F) | ((volume << 4) & 0xF0)));
            memory[location] = (byte)this.getByte();
        }

        public void setEnvelopeDirection(boolean bit) {
            this.setBit(3, bit);
            memory[location] = (byte)this.getByte();
        }

        public void setEnvelopeSweep(byte period) {
            this.setByte((byte) ((this.getByte() & 0xF8) | (period & 0x07)));
            memory[location] = (byte)this.getByte();
        }

        public boolean isOn() {
            return (this.getByte() & 0xF8) != 0;
        }
    }

    private class NR13 extends MemRegisters {
        public NR13() {
            location = 0xFF13;
        }

    }

    private class NR14 extends MemRegisters {
        public NR14() {
            location = 0xFF14;
        }
    }

    private class NR21 extends MemRegisters {
        public NR21() {
            location = 0xFF16;
        }
    }

    private class NR22 extends MemRegisters {
        public NR22() {
            location = 0xFF17;
        }
    }

    private class NR23 extends MemRegisters {
        public NR23() {
            location = 0xFF18;
        }
    }

    private class NR24 extends MemRegisters {
        public NR24() {
            location = 0xFF19;
        }
    }

    private class NR30 extends MemRegisters {
        public NR30() {
            location = 0xFF1A;
        }
    }

    private class NR31 extends MemRegisters {
        public NR31() {
            location = 0xFF1B;
        }
    }

    private class NR32 extends MemRegisters {
        public NR32() {
            location = 0xFF1C;
        }
    }

    private class NR33 extends MemRegisters {
        public NR33() {
            location = 0xFF1D;
        }
    }

    private class NR34 extends MemRegisters {
        public NR34() {
            location = 0xFF1E;
        }
    }

    private class NR41 extends MemRegisters {
        public NR41() {
            location = 0xFF20;
        }
    }

    private class NR42 extends MemRegisters {
        public NR42() {
            location = 0xFF21;
        }
    }

    private class NR43 extends MemRegisters {
        public NR43() {
            location = 0xFF22;
        }
    }

    private class NR44 extends MemRegisters {
        public NR44() {
            location = 0xFF23;
        }
    }

    private class NR50 extends MemRegisters {
        public NR50() {
            location = 0xFF24;
        }
    }

    private class NR51 extends MemRegisters {
        public NR51() {
            location = 0xFF25;
        }
    }

    private class NR52 extends MemRegisters {
        public NR52() {
            location = 0xFF26;
        }
    }
    public int getColorFromArray(int index) {
        int[] clrs= {0xbbe9cd, 0x3fd87b, 0x10863f,0x043b19};
        return clrs[index];
    }
}

package GPU;
import CPU.*;
import Memory.*;
import Memory.Memory.LCDC;
import Memory.Memory.OAM;
import Memory.Memory.BGP;
import Memory.Memory.Stat;


public class PPU {
    byte[] romData;


    CPU cpu;
    Memory memory;
    Ram ram;
    InterruptManager interruptManager;
    private int mode;
    LCDC lcdc;
    OAM oam;
    BGP bgp;
    Stat stat;
    // OuterClass.InnerClass innerObject = outerObject.new InnerClass();


    // dma to transfer sections of memory from one area to another without involving cpu

    // modes
    // 0 H-Blank - default mode. PPU is active during horizontal blanking period of each scanline and is used to render
    //      the background and window tiles
    // 1 V-Blank - renders sprites and updates background and window tiles
    // 2 OAM Read - reads data from OAM to determine which sprites to render during the first 80 cycles of each scanline
    // 3 VRAM Read - during the last 172 cycles of each scanline reads from VRAM to get background and window tiles
    public static final int HBLANK = 0;
    public static final int VBLANK = 1;
    public static final int OAM_READ = 2;
    public static final int VRAM_READ = 3;

    // number of cycles that have elapsed since the start of the current mode
    int modeTicks = 0;
    // current scanline that the PPU is drawing
    int line = 0;


    // LCDC, background, and dma are public classes inside Memory class
    // We can do everything in VRAM with get/setByte in the Memory from PPU, or we can make a VRAM class
    public PPU(byte[] romData, CPU cpu, Ram ram, InterruptManager interruptManager) {
        this.romData = romData;
        this.cpu = cpu;
        this.memory = new Memory(romData);
        this.ram = ram;
        this.interruptManager = interruptManager;
        this.lcdc = memory.getLcdc();
        this.oam = memory.getOam();
        this.bgp = memory.getBgp();
        this.stat = memory.getStat();

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

        // filler variable to keep track of ticks from timer for now
        int tick = 0;
        // OAM read mode, scanline active

    }

    // called every cycle from the CPU before an opcode is ran to cycle through modes. Every cycle counts as one tick.
    public void updateModeAndCycles() {
        switch (mode) {
            case 2:
                // move to VRAM READ if it is not the first cycle in OAM read
                if (modeTicks == 0) {
                    // OAM search begins at cycle 80 and lasts for 20 cycles
                    // determine which sprites are on this line
                    // check sprite size
                    int spriteHeight = ((lcdc.getByte() >> 2) & 0x1) ==1  ? 16 : 8;
                    int spritesFound = 0;
                    for (int i = 0; i < 40; i++) {
                        // get Y cord of the sprite from OAM. Each sprite is 4 bytes so * the index by 4 to get offset
                        // then subtract 16 because the top of the screen is y=16
                        int spriteY = oam.readByte((i * 4) - 16);
                        // check if the sprite processed is on the current scanline being processed by PPU
                        if (spriteY <= line && spriteY + spriteHeight > line) {
                            // sprite is on this line, add to list because only 10 sprites can apprear on one scanline
                            // ,so we may need to hide some
                            spritesFound++;
                        }
                    }

                    // update STAT register
                    stat.setMF2(true);
                    // check if OAM interrupt is enabled in the stat register
                    if (((stat.getByte() >> 5) & 0x1) == 1) {
                        //interruptManager.requestInterrupt(LCD_STAT);
                        System.out.println("Request LCD STAT interrupt");
                    }
                } else if (modeTicks == 20) {
                    // end of OAM search
                    // enter VRAM read mode
                    mode = VRAM_READ;

                    // update STAT register
                    stat.setMF3(true);
                    // move to VRAM_READ
                } else if (modeTicks >= 80) {
                    modeTicks = 0;
                    mode = VRAM_READ;
                }
                break;
            case 3:
                if (modeTicks >= 172) {
                    modeTicks = 0;
                    mode = HBLANK;
                }
                break;
            case 0:
                if (modeTicks >= 204) {
                    modeTicks = 0;
                    line++;

                    if (line == 143) {
                        mode = VBLANK;
                    } else {
                        mode = OAM_READ;
                    }
                }
                break;
            case 1:
                if (modeTicks >= 456) {
                    modeTicks = 0;
                    line++;

                    if (line > 153) {
                        mode = OAM_READ;
                        line = 0;
                    }
                }
                break;
        }
        modeTicks++;
    }


    public PPU getPPU(){return this;}

}

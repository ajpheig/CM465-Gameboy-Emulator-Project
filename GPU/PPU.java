package GPU;

import CPU.*;
import Memory.*;
import Memory.Memory.LCDC;
import Memory.Memory.OAM;
import Memory.Memory.BGP;

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
    // OuterClass.InnerClass innerObject = outerObject.new InnerClass();

    // dma to transfer sections of memory from one area to another without involving
    // cpu

    // modes
    // 0 H-Blank - default mode. PPU is active during horizontal blanking period of
    // each scanline and is used to render
    // the background and window tiles
    // 1 V-Blank - renders sprites and updates background and window tiles
    // 2 OAM Read - reads data from OAM to determine which sprites to render during
    // the first 80 cycles of each scanline
    // 3 VRAM Read - during the last 172 cycles of each scanline reads from VRAM to
    // get background and window tiles
    public static final int HBLANK = 0;
    public static final int VBLANK = 1;
    public static final int OAM_READ = 2;
    public static final int VRAM_READ = 3;

    // number of cycles that have elapsed since the start of the current mode
    int modeCycles = 0;
    // current scanline that the PPU is drawing
    int line = 0;

    // LCDC, background, and dma are public classes inside Memory class
    // We can do everything in VRAM with get/setByte in the Memory from PPU or we
    // can make a VRAM class
    public PPU(byte[] romData, CPU cpu, Ram ram, InterruptManager interruptManager, Memory memory) {
        this.romData = romData;
        this.cpu = cpu;
        this.memory = memory;
        this.ram = ram;
        this.interruptManager = interruptManager;
        this.lcdc = memory.getLcdc();
        this.oam = memory.getOam();
        this.bgp = memory.getBgp();

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
        switch (mode) {
            case 2:
                if (modeCycles >= 80) {
                    // move to mode 3
                    modeCycles = 0;
                    mode = VRAM_READ;
                }
                break;
            case 3:
                if (modeCycles >= 172) {
                    // enter HBLANK
                    modeCycles = 0;
                    mode = HBLANK;
                }
                break;
            case 0:
                if (modeCycles >= 204) {
                    modeCycles = 0;
                    line++;

                    if (line == 143) {
                        // enter VBLANK
                        mode = VBLANK;
                    } else {
                        // enter OAM
                        mode = OAM_READ;
                    }
                }
                break;
            case 1:
                if (modeCycles >= 456) {
                    modeCycles = 0;
                    line++;

                    if (line > 153) {
                        // enter OAM
                        mode = OAM_READ;
                        line = 0;
                    }
                }
                break;
        }
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getMode() {
        return mode;
    }

}

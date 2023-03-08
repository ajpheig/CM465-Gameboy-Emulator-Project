import Memory.*;
public class PPU {
    byte[] romData;
    CPU cpu;
    Memory memory;
    Ram ram;
    InterruptManager interruptManager;
    private int mode;

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


    // LCDC and background are inside the memory class
    // We can do everything in VRAM with get/setByte in the Memory from PPU or we can make a VRAM class
    public PPU(byte[] romData, CPU cpu, Ram ram, InterruptManager interruptManager){
        this.romData = romData;
        this.cpu = cpu;
        this.memory = new Memory(romData);
        this.ram = ram;
        this.interruptManager = interruptManager;


    }

    public void setMode(int mode){
        this.mode = mode;
    }

    public int getMode() {
        return mode;
    }


}

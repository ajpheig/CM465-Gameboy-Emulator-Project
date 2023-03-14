package GPU;
import CPU.*;
import Memory.*;
import Memory.Memory.LCDC;
import Memory.Memory.OAM;
import Memory.Memory.BGP;
import Memory.Memory.Stat;
import Memory.Memory.VRAM;
import Memory.Memory.OBP0;
import Memory.Memory.OBP1;

import java.util.ArrayList;
import java.util.List;


public class PPU {
    byte[] romData;
    CPU cpu;
    Memory memory;
    Ram ram;
    InterruptManager interruptManager;
    private int mode;
    LCDC lcdc;
    OBP0 obp0;
    OBP1 obp1;
    OAM oam;
    BGP bgp;
    Stat stat;
    VRAM vram;
    Display display;
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
    public PPU(byte[] romData, CPU cpu, Ram ram, InterruptManager interruptManager, Display display) {
        this.romData = romData;
        this.cpu = cpu;
        this.memory = new Memory(romData);
        this.ram = ram;
        this.interruptManager = interruptManager;
        this.lcdc = memory.getLcdc();
        this.oam = memory.getOam();
        this.bgp = memory.getBgp();
        this.stat = memory.getStat();
        this.vram = memory.getVram();
        this.display = display;
        this.obp1 = memory.getObp1();
        this.obp0 = memory.getObp0();


        cpu.setPPU(this);
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

    // called every cycle from the CPU before an opcode is ran to cycle through modes. Every cycle counts as one tick.
    public void updateModeAndCycles() {
        List<Integer> spriteIndexesOnLine = new ArrayList<>();


        switch (mode) {
            case 2: // OAM read
                // get sprites if it is the first tick of OAM read otherwise
                // move to VRAM READ if it is not the first cycle in OAM read
                // clear sprites from last scanline
                spriteIndexesOnLine.clear();
                if (modeTicks == 0) {
                    // OAM search begins at cycle 80 and lasts for 20 cycles
                    // determine which sprites are on this line
                    // check sprite size
                    int spriteHeight = ((memory.readByte(0xFF40) >> 2) & 0x1) == 1 ? 16 : 8;
                    int spritesFound = 0;
                    // i is the sprite index in the OAM class
                    for (int i = 0; i < 40; i++) {
                        int spriteY = oam.readByte((i * 4) - 16);
                        if (spriteY <= line && spriteY + spriteHeight > line) {
                            // Sprite is on this line, add its index to the list
                            spriteIndexesOnLine.add(i);
                            spritesFound++;
                            if (spritesFound > 10) {
                                if (stat.isM2OAMEnabled()) {
                                    stat.setM2OAM(true);
                                }
                            }
                        }
                    }

                    // update STAT register
                    stat.setMF2(true);
                    if (((stat.getByte() >> 5) & 0x1) == 1) {
                        memory.writeByte(0xff0f, 0b10);// mmu sends interrupt
                        System.out.println("Request LCDSTAT interrupt");
                    }
                } else if (modeTicks == 20) {
                    // end of OAM search
                    // enter VRAM read mode
                    mode = VRAM_READ;

                    // update STAT register
                    stat.setMF3(true);
                }
                break;
            case 3: // VRAM read also known as pixel transfer mode
                // read background tile data and attributes
                int scrollX = memory.readByte(0xFF43);
                int scrollY = memory.readByte(0xFF42);
                int backgroundTileIndex = memory.readByte(0x9800 + 32 * (line + memory.readByte(0xFF42)) / 8 + (memory.readByte(0xFF43) / 8) / 8);
                int backgroundTileAttributes = memory.readByte(0x9800 + 32 * (line + scrollY) / 8 + (scrollX / 8) + 0x2000);
                // read corresponding tile data from either 0x9000 or 0x8000 depending on LCDC bit 4
                int tileDataAddress = ((lcdc.getByte() >> 4) & 0x1) == 1 ? 0x8000 : 0x9000;
                int tileDataIndex = memory.readByte(tileDataAddress + (backgroundTileIndex * 16) + ((line + scrollY) % 8) * 2);
                int tileDataAttributes = memory.readByte(tileDataAddress + (backgroundTileIndex * 16) + ((line + scrollY) % 8) * 2 + 1);
                // update background color palette based on attributes
                int backgroundPalette = (backgroundTileAttributes >> ((scrollX / 8) % 2 == 0 ? 0 : 4)) & 0x3;
                int colorPaletteIndex = (tileDataAttributes >> ((scrollX / 8) % 2 == 0 ? 0 : 4)) & 0x3;
                int backgroundColor = bgp.getColor(backgroundPalette, colorPaletteIndex);
                // write the pixel to the screen buffer
                display.setPixel(modeTicks, line, backgroundColor);

                // loop through the sprites on this line and display them if they overlap with the current pixel
                for (int i = 0; i < spriteIndexesOnLine.size(); i++) {
                    if (spriteIndexesOnLine.size() > 10) {
                        // Sort the sprite indexes by priority
                        spriteIndexesOnLine.sort((a, b) -> {
                            boolean aPriority = oam.hasSpritePriority(a);
                            boolean bPriority = oam.hasSpritePriority(b);
                            if (aPriority && !bPriority) {
                                return -1;
                            } else if (!aPriority && bPriority) {
                                return 1;
                            } else {
                                return 0;
                            }
                        });
                    }
                    // Remove any sprites that are not in the top 10 highest priority
                    while (spriteIndexesOnLine.size() > 10) {
                        spriteIndexesOnLine.remove(10);
                    }
                    // offest of the sprite in OAM spriteIndexesOnLine.get(i)
                    byte[] spriteData = oam.getSpriteData(spriteIndexesOnLine.get(i));
                    int spriteTileIndex = Byte.toUnsignedInt(spriteData[2]);
                    int spriteTileAttributes = spriteData[3];
                    int spritePalette = (spriteTileAttributes >> 4) & 0x1;
                    int spriteXFlip = (spriteTileAttributes >> 5) & 0x1;
                    int spriteYFlip = (spriteTileAttributes >> 6) & 0x1;
                    int spriteX = spriteData[1];
                    int spriteY = spriteData[0];
                    int spriteHeight = oam.getSpriteHeight(spriteIndexesOnLine.get(i));

                    // adjust sprite coordinates for any flipping
                    if (oam.isSpriteFlippedHorizontally(spriteIndexesOnLine.get(i))) {
                        spriteX = 7 - spriteX;
                    }
                    if (oam.isSpriteFlippedVertically(spriteIndexesOnLine.get(i))) {
                        spriteY = spriteHeight - spriteY - 1;
                    }

                    // read the appropriate tile data from VRAM
                    int spriteTileDataAddress = ((lcdc.getByte() & 0x4) == 0) ? 0x8000 : 0x8800;
                    int spriteTileDataIndex = memory.readByte(spriteTileDataAddress + (spriteTileIndex * 16) + ((line - spriteY) % spriteHeight) * 2);
                    int spriteTileDataAttributes = memory.readByte(spriteTileDataAddress + (spriteTileIndex * 16) + ((line - spriteY) % spriteHeight) * 2 + 1);

                    // apply any necessary flipping
                    if (spriteXFlip == 1) {
                        spriteTileDataIndex = reverseBits(spriteTileDataIndex);
                    }
                    if (spriteYFlip == 1) {
                        spriteTileDataIndex = flipVertical(spriteTileDataIndex, spriteHeight);
                    }

                    // get the color palette index for the current pixel
                    int spriteColorPaletteIndex = (spriteTileDataAttributes >> 3) & 0x7;

                    // if the sprite pixel is not transparent, display it on the screen
                    if (spriteTileDataIndex != 0) {
                        // need to do something different on this line
                        int spritePaletteIndex = (spritePalette == 0) ? obp0.getByte() : obp1.getByte();
                        int spriteColorIndex = (spritePaletteIndex >> (spriteColorPaletteIndex * 2)) & 0x3;
                        int spriteColor = oam.getSpritePalette(spriteIndexesOnLine.get(i));
                        display.setPixel(modeTicks, line, spriteColor);
                    }
                    if (modeTicks >= 160) {
                        // end of scanline
                        modeTicks = 0;
                        line++;
                        if (line >= 144) {
                            // end of visible screen area, enter VBLANK
                            mode = VBLANK;
                            //interruptManager.requestInterrupt(InterruptManager.INTERRUPT_VBLANK);
                            memory.writeByte(0xff0f, 0b1);// writs 1st bit to ff0f, mem sends interrupt
                            System.out.println("request VBLANK INTERRUPT");
                        } else {
                            // start next scanline
                            mode = OAM_READ;
                        }

                    }
                    // update the display with the sprite data
                    if (spriteTileDataIndex != 0) {
                        // need to do something different on this line
                        int spritePaletteIndex = (spritePalette == 0) ? obp0.getByte() : obp1.getByte();
                        int spriteColorIndex = (spritePaletteIndex >> (spriteColorPaletteIndex * 2)) & 0x3;
                        int spriteColor = oam.getSpritePalette(spriteIndexesOnLine.get(i));
                        // update the screen buffer with the sprite info with setPixel method
                        display.setPixel(modeTicks, line, spriteColor);
                    }
                }
                break;
            case 0: // HBLANK
                if (modeTicks >= 204) {
                    modeTicks = 0;
                    line++;

                    // Check if LYC=LY
                    if (line == memory.readByte(0xFF41)) {
                        // Set the LYC=LY flag in STAT register
                        stat.setCoincidenceFlag(true);

                        // Check if LYC=LY interrupt is enabled
                        if ((memory.readByte(0xFF41) & 0x40) == 0x40) {
                        } else {
                            // Request STAT/mode2 interrupt
                            System.out.println("STAT interupt");
                        }
                    } else {
                        // Clear the LYC=LY flag in STAT register
                        stat.setCoincidenceFlag(false);
                    }

                    if (line == 143) {
                        mode = VBLANK;
                    } else {
                        mode = OAM_READ;
                    }
                }
                break;
            case 1: // VBLANK
                if (modeTicks >= 456) {
                    modeTicks = 0;
                    line++;

                    if (line >= 154) {
                        mode = OAM_READ;
                        line = 0;

                        // Set the coincidence flag if LYC=LY
                        if (line == memory.readByte(0xFF45)) {
                            stat.setCoincidenceFlag(true);
                        } else {
                            stat.setCoincidenceFlag(false);
                        }

                        // Check if LYC=LY interrupt is enabled
                        if ((memory.readByte(0xFF41) & 0x40) == 0x40 && line == memory.readByte(0xFF45)) {
                            // Request the LYC=LY interrupt
                            System.out.println("STAT interupt");
                        }

                        // Request VBLANK interrupt
                        memory.writeByte(0xff0f, 0b1);
                    } else {
                        mode = VBLANK;
                    }
                }
                break;
        }
        modeTicks++;
    }







    // Reverses the bit order of a byte value
    public static int reverseBits(int n) {
        int result = 0;
        for (int i = 0; i < 8; i++) {
            result = (result << 1) | (n & 1);
            n >>= 1;
        }
        return result;
    }


    // Flips a tile vertically and returns the resulting tile data
    private int flipVertical(int tileData, int spriteHeight) {
        int[] rows = new int[spriteHeight];
        for (int i = 0; i < spriteHeight; i++) {
            int row = getRow(tileData, i, spriteHeight);
            rows[spriteHeight - 1 - i] = row;
        }
        int flipped = 0;
        for (int i = 0; i < spriteHeight; i++) {
            flipped |= rows[i] << (8 * i);
        }
        return flipped;
    }

    private int getRow(int tileData, int rowNumber, int spriteHeight) {
        int rowStartBit = 8 * rowNumber;
        int rowEndBit = rowStartBit + 7;
        int row = 0;
        for (int bit = rowStartBit; bit <= rowEndBit; bit++) {
            row |= ((tileData >> bit) & 0x01) << (bit - rowStartBit);
        }
        int paddingStartBit = 8 * spriteHeight;
        int paddingEndBit = 7;
        for (int bit = paddingStartBit; bit <= paddingEndBit; bit++) {
            row |= ((tileData >> bit) & 0x01) << (bit - paddingStartBit + rowEndBit - rowStartBit + 1);
        }
        return row;
    }



    public PPU getPPU(){
        return this;
    }
}

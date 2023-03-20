package GPU;
import CPU.*;
import Memory.*;
import Memory.Memory.LCDC;
import Memory.Memory.LYC;
import Memory.Memory.LY;
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
    private int mode=2;
    LCDC lcdc;
    OBP0 obp0;
    OBP1 obp1;
    OAM oam;
    BGP bgp;
    Stat stat;
    VRAM vram;
    LYC lyc;
    LY ly;
    Display display;
    Tile[] tileSet;
    int curX;
    int curY;
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
    int scrollX;
    int scrollY;
    private TileMap map;


    // LCDC, background, and dma are public classes inside Memory class
    // We can do everything in VRAM with get/setByte in the Memory from PPU, or we can make a VRAM class
    public PPU(byte[] romData, CPU cpu, Ram ram, InterruptManager interruptManager, Display display,Memory mem) {
        this.romData = romData;
        this.cpu = cpu;
        this.memory = mem;
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
        this.lyc=memory.getLYC();
        this.ly=memory.getLY();
        display.setMemInDisplay(mem);
        curX=0;
        curY=0;
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
        //System.out.println(curY+" "+mode);
        //ly.setLY((byte)curY);
        switch (mode) {
            case 2: // OAM read
                tileSet=vram.getTileSet();//readies tile set for PPU
                // get sprites if it is the first tick of OAM read otherwise
                // move to VRAM READ if it is not the first cycle in OAM read
                // clear sprites from last scanline
                spriteIndexesOnLine.clear();
                if (modeTicks <=1) {//changed to 1 because it is modeTicks is ++ after setting it to zero
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
                    ly.setLY((byte)curY);
                    stat.setMF2(true);
                    if (((stat.getByte() >> 5) & 0x1) == 1) {
                        memory.writeByte(0xff0f, 0b10);// mmu sends interrupt
                        System.out.println("Request LCDSTAT interrupt");
                    }
                    curX=0;
                    scrollX = memory.readByte(0xFF43);
                    scrollY = memory.readByte(0xFF42);
                    boolean useTileSet0 = lcdc.getBit(4);
                    boolean useBackgroundMap0 = ((lcdc.getByte())&0b1000)==1;
                    this.loadMap(useTileSet0, useBackgroundMap0);
                    //printRAM();//for debugging RAM/Tile/Map values
                } else if (modeTicks >= 20) {
                    // end of OAM search
                    // enter VRAM read mode
                    modeTicks=0;
                    mode = VRAM_READ;

                    // update STAT register
                    stat.setMF3(true);
                }
                break;
            case 3: // VRAM read also known as pixel transfer mode
                // read background tile data and attributes
                if(modeTicks<=1){int status = memory.readByte(0xFF41) & 0x3F;
                    memory.writeByte(0xFF41, status | 0xC0);}
                tileSet=vram.getTileSet();
                int xPos=scrollX+curX;
                int yPos=scrollY+curY;
                Tile currtile;
                int pixel;
                int bgTileIndex = map.getTile(xPos/8,yPos/8);
                currtile = tileSet[bgTileIndex];
                pixel = currtile.getVal(yPos % 8, xPos % 8);
                int backgroundColor = bgp.getColor(pixel,2);
                // write the pixel to the screen buffer
                display.setPixel(xPos, yPos, backgroundColor);
                curX++;
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

                    // update the display with the sprite data
                    if (spriteTileDataIndex != 0) {
                        // need to do something different on this line
                        int spritePaletteIndex = (spritePalette == 0) ? obp0.getByte() : obp1.getByte();
                        int spriteColorIndex = (spritePaletteIndex >> (spriteColorPaletteIndex * 2)) & 0x3;
                        int spriteColor = oam.getSpritePalette(spriteIndexesOnLine.get(i));
                        // update the screen buffer with the sprite info with setPixel method
                        display.setPixel(modeTicks, line, spriteColor);
                    }
                } // sprite for
                if (modeTicks >= 160) {
                    // end of scanline
                    modeTicks = 0;
                    line++;
                    curY++;
                    if (line >= 145&&curY >= 145) {
                        // end of visible screen area, enter VBLANK
                        mode = VBLANK;
                         memory.writeByte(0xff0f, 0b1);// writs 1st bit to ff0f, mem sends interrupt
                        //System.out.println("request VBLANK INTERRUPT");
                    } else {
                        // start next scanline
                        mode = HBLANK;
                    }
                }
                break;
            case 0: // HBLANK
                if (modeTicks >= 204) {
                    modeTicks = 0;
                    curX=0;
                    // Check if LYC=LY
                    if (curY == memory.readByte(0xFF45)) {
                        System.out.println("LYC=LY");
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

                    if (line > 144&&curY>144) {
                        mode = VBLANK;
                    } else {
                        modeTicks=0;
                        mode = OAM_READ;
                    }
                }
                break;
            case 1: // VBLANK
                if (modeTicks >= 456) {
                    modeTicks = 0;
                    line++;
                    curY++;
                    //ly.setLY((byte)curY);
                    if (line >= 154&&curY>=154) {
                        // update the display with the pixel buffer
                        display.render();
                        mode = OAM_READ;
                        line = 0;
                        curY=0;
                        // Set the coincidence flag if LYC=LY
                        if (line == (int)lyc.getByte()) {
                            stat.setCoincidenceFlag(true);
                        } else {
                            stat.setCoincidenceFlag(false);
                        }

                        // Check if LYC=LY interrupt is enabled
                        if ((memory.readByte(0xFF41) & 0x40) == 0x40 && curY == memory.readByte(0xFF45)) {
                            // Request the LYC=LY interrupt
                            System.out.println("STAT interupt");
                            int ifreg= memory.readByte(0xff0f);
                            memory.writeByte(0xff0f,ifreg|02);
                        }

                        // Request VBLANK interrupt
                    } else {
                        memory.writeByte(0xff0f, 0b1);
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

    public void loadMap(boolean useTileSet0, boolean useMap1) {
        int ts = useTileSet0 ? 0 : 1;
        int address = useMap1 ? 0x9c00 : 0x9800;
        this.map = new TileMap(memory, address, ts);
    }
    public void printRAM() {//Proof of RAM working
        /*for(int i=0;i<0x1800;i++) {//print ram hex values
            if(i%16==0)System.out.println();
            if((i&0xf)==0)System.out.print(" "+Integer.toHexString(0x8000+i));
            if(i%8==0)System.out.print(" | ");
            System.out.print(" "+Integer.toHexString(memory.readByte(0x8000+i))+" ");
         }
        for(int y=0;y<8;y++) {//print a tile's values from set
            for(int x=0;x<8;x++) {

                System.out.print(" "+tileSet[map.getTile(0x0d,0x09)].getVal(y,x)+" ");
                if(x==7)System.out.println();
            }
            if(y==7)System.out.println("-------------"+map.getTile(0x0d,0x09));
        }*/
    }


    public PPU getPPU(){
        return this;
    }
}

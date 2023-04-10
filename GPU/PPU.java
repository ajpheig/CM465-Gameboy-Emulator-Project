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
import java.util.Arrays;
import java.util.List;


public class PPU {
    byte[] romData;
    CPU cpu;
    Memory memory;
    Ram ram;
    InterruptManager interruptManager;
    public DebugPane bugPanel;
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
    int windowX;
    int windowY;
    boolean displayWindow;
    private TileMap map;


    // LCDC, background, and dma are public classes inside Memory class
    // We can do everything in VRAM with get/setByte in the Memory from PPU, or we can make a VRAM class
    public PPU(byte[] romData, CPU cpu, Ram ram, InterruptManager interruptManager, Display display,Memory mem) {
        this.romData = romData;
        this.cpu = cpu;
        this.memory = mem;
        //this.ram = ram;
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
        bugPanel = new DebugPane(cpu,mem);
        display.setMemInDisplay(mem, interruptManager);
        //oam.loadSprites(getSpriteData());
        curX=0;
        curY=0;
        cpu.setPPU(this);
        lcdc.setLCDDisplay(true);
         // getSpritesData();
        //initOAM();
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
        if(modeTicks<=5)
            ly.setLY((byte)curY);
        switch (mode) {
            case 2: // OAM read
                //tileSet=vram.getTileSet();//readies tile set for PPU
                // get sprites if it is the first tick of OAM read otherwise
                // move to VRAM READ if it is not the first cycle in OAM read
                if (modeTicks <= 5) {//changed to 1 because it is modeTicks is ++ after setting it to zero
                    boolean useTileSet0 = lcdc.getBit(4);
                    boolean useBackgroundMap0 =(lcdc.getBit(3));
                    boolean useWindowMap0=((lcdc.getBit(6)));
                    if(windowY<=curY&&windowX<=curX) displayWindow = lcdc.getBit(5);
                    //load tile map
                    if(displayWindow) this.loadMap(useTileSet0, useWindowMap0);
                    else this.loadMap(useTileSet0, useBackgroundMap0);
                    //System.out.println(memory.readByte(0x8027));
                    tileSet = vram.getTileSet();//readies tile set for PPU
                    // OAM search begins at cycle 80 and lasts for 20 cycles

                    // clear sprite array of previously rendered sprites
                    //Sprite.clearSprites();
                    // get data of sprites on screen from mem/ perform dma transfer unofficially
                    initOAM();
                    // determine which sprites are on this line
                    // System.out.println("curY " + curY);
                    // read the sprite size from the lcdc
                    int spriteSize = memory.readByte(0xFF40) & 0b100;
                    int spriteHeight = spriteSize == 0 ? 8 : 16;
                    if(lcdc.getBit(2)) spriteHeight=16;
                    else spriteHeight=8;
                    //System.out.println("sprite size " + spriteHeight);
                    oam.checkSpriteY(curY, spriteHeight);
                    // update STAT register
                    //ly.setLY((byte)curY); //trying out setting LY at start if modeTick==1
                    stat.setMF2(true);
                    if (((stat.getByte() >> 5) & 0x1) == 1) {
                        memory.writeByte(0xff0f, 0b10);// mmu sends interrupt
                        //System.out.println("Request LCDSTAT interrupt");
                    }
                    curX = 0;
                    scrollX = memory.readByte(0xFF43);
                    scrollY = memory.readByte(0xFF42);
                    windowX = memory.readByte(0xFF4B)-7;
                    windowY = memory.readByte(0xFF4A);
                    printRAM();//for debugging RAM/Tile/Map values
                } else if (modeTicks >= 20) {
                    // end of OAM search
                    // enter VRAM read mode
                    modeTicks = 0;
                    mode = VRAM_READ;
                    stat.setMF1(true);
                    stat.setMF2(true);
                    memory.writeByte(0xff0f, 0b10);//set IF LCD flag
                }
                break;
            case 3: // VRAM read also known as pixel transfer mode
                // read background tile data and attributes
                if (modeTicks <= 1) {
                    int status = memory.readByte(0xFF41) & 0x3F;
                    memory.writeByte(0xFF41, status | 0xC0);
                }
                int xPos = scrollX + curX;
                int yPos = scrollY + curY;
                Tile currtile;
                int pixel=0;
                int bgTileIndex = map.getTile(xPos / 8, yPos / 8);
                currtile = tileSet[bgTileIndex];
                if(currtile!=null)pixel = currtile.getVal(yPos % 8, xPos % 8);
                int backgroundColor = bgp.getColor(pixel, 2);
                int pixelColor=backgroundColor;
                // write the pixel to the screen buffer
                //System.out.println("curx:"+curX+" cury:"+curY+" xPos:"+xPos+" yPos:"+yPos+"tileIndex:"+Integer.toHexString(bgTileIndex));
                //System.out.println("curx:"+curX+" cury:"+curY+" xPos:"+xPos+" yPos:"+yPos+"tileIndex:"+Integer.toHexString(bgTileIndex)+" clr:"+backgroundColor);


                display.setPixel(curX, curY, backgroundColor);
                curX++;

                int windowPixel=0;
                if(displayWindow&&windowY<=curY&&windowX<=curX) {
                    Tile windowTile = tileSet[map.getTile((curY - windowY) / 8, (curX - windowX) / 8)];
                    windowPixel = windowTile.getVal((curY - windowY) % 8, (curX - windowX) % 8);
                    display.setPixel(curX,curY,bgp.getColor(windowPixel,2));
                }

                int sY;
                int sX;
                int sNum;
                int sFlag;
                List<Sprite> spriteList = Sprite.getAllSprites();
                //System.out.println("spriteList size " +  spriteList.size());
                // loop though visible sprites and create the spriteTiles to render them
                if(!spriteList.isEmpty()) {
                    //oam.printSpriteData();
                    //System.out.println("Render sprite loop");
                    for (int i = 0; i < spriteList.size(); i++) {
                        // get sprite obj out of list
                        Sprite sprite = spriteList.get(i);
                        sY = sprite.getY() & 0xFF;
                        sX = sprite.getX() & 0xFF;
                        sNum = sprite.getTileNumber() & 0xFF;
                        sFlag = sprite.getFlags() & 0xFF;
                        Tile spriteTile = tileSet[sNum];
                        // flip x and y are both set
                        if ((sFlag & 0x20) != 0 && (sFlag & 0x40) != 0) {
                            // Sprite should be flipped vertically and horizontally
                            for (int y = 0; y < 8; y++) {
                                for (int x = 0; x < 8; x++) {
                                    int sPixel = spriteTile.getVal(y, x);
                                    int color;
                                    if((sFlag&16)==0) color = obp0.getColor(sPixel, 2);
                                    else color = obp1.getColor(sPixel, 2);
                                    // calculate the pixel coordinates based on sprite position and current tile pixel position, flipping horizontally and vertically
                                    int xPosS = sX + (7 - x);
                                    int yPosS = sY + (7 - y);

                                    // write the pixel to the screen buffer
                                    if(sPixel!=0&&((sFlag&0x80)==0||pixel==0))display.setPixel(xPosS - 8, yPosS - 16, color);
                                }
                            }
                        }
                        else
                            // only flipped vertically not horizontally
                            if ((sFlag & 0x20) != 0 &&  (sFlag & 0x40) == 0) {
                                // Sprite should be flipped vertically
                                for (int y = 0; y < 8; y++) {
                                    for (int x = 7; x >= 0; x--) {
                                        int sPixel = spriteTile.getVal(y, x);
                                        int color;
                                        if((sFlag&16)==0) {
                                            color = obp0.getColor(sPixel, 2);
                                        }else color = obp1.getColor(sPixel, 2);
                                        // calculate the pixel coordinates based on sprite position and current tile pixel position
                                        int xPosS = sX + (7 - x);
                                        int yPosS = sY + y;

                                        // write the pixel to the screen buffer
                                        if(sPixel!=0&&((sFlag&0x80)==0||pixel==0))display.setPixel(xPosS - 8, yPosS - 16, color);
                                    }
                                }
                            }
                            else
                            if ((sFlag & 0x40) != 0 && (sFlag & 0x20) == 0) {
                                // Sprite should be flipped horizontally
                                //System.out.println("horizontal");
                                for (int y = 0; y < 8; y++) {
                                    for (int x = 0; x < 8; x++) {
                                        int sPixel = spriteTile.getVal(y, x);
                                        int color;
                                        if((sFlag&16)==0) color = obp0.getColor(sPixel, 2);
                                        else color = obp1.getColor(sPixel, 2);

                                        // calculate the pixel coordinates based on sprite position and current tile pixel position, flipping horizontally
                                        int xPosS = sX + x;
                                        int yPosS = sY + (7 - y); // flip the y-coordinate

                                        // write the pixel to the screen buffer
                                        if(sPixel!=0&&((sFlag&0x80)==0||pixel==0))display.setPixel(xPosS - 8, yPosS - 16, color);
                                    }
                                }
                            }

//                        // no flipping on the sprites
                            else {

                                for (int y = 0; y < 8; y++) {
                                    for (int x = 0; x < 8; x++) {
                                        int sPixel = spriteTile.getVal(y, x);
                                        int color;
                                        if((sFlag&16)==0) color = obp0.getColor(sPixel, 2);
                                        else {
                                            color = obp1.getColor(sPixel, 2);
                                           // System.out.println("ob1:"+sPixel);
                                        }
                                        // calculate the pixel coordinates based on sprite position and current tile pixel position
                                        int xPosS = sX + x;
                                        int yPosS = sY + y;
                                        // write the pixel to the screen buffer
                                        //System.out.println(" setting pixel at " +xPosS+ ","+yPosS );
                                        if(sPixel!=0&&((sFlag&0x80)==0||pixel==0||windowPixel==0))display.setPixel(xPosS - 8, yPosS - 16, color);
                                        //display.setPixel(xPosS, yPosS, color);
                                    }
                                } // render sprite for
                            }
                        // no checking flipping
//                                                    for (int y = 0; y < 8; y++) {
//                                for (int x = 0; x < 8; x++) {
//                                    int sPixel = spriteTile.getVal(y, x);
//                                    int color = obp0.getColor(sPixel, 2);
//
//                                    // calculate the pixel coordinates based on sprite position and current tile pixel position
//                                    int xPosS = sX + x;
//                                    int yPosS = sY + y;
//                                    // write the pixel to the screen buffer
//                                    //System.out.println(" setting pixel at " +xPosS+ ","+yPosS );
//                                    display.setPixel(xPosS - 8, yPosS - 16, color);
//                                    //display.setPixel(xPosS, yPosS, color);
//                                }
//                            } // render sprite for
                    }
                    Sprite.clearSprites();
                }

                if (modeTicks >= 160&&curX>=160) {
                    // end of scanline
                    modeTicks = 0;
                    line++;
                    curY++;
                    if (line > 143&&curY > 143) {
                        // end of visible screen area, enter VBLANK
                        mode = VBLANK;
                        //ly.setLY((byte)curY);
                        stat.setM1VBlank(true);
                        stat.setMF1(true);
                        stat.setMF2(false);
                        memory.writeByte(0xff0f, 0b1);// writs 1st bit to ff0f, mem sends interrupt
                        //System.out.println("request VBLANK INTERRUPT");
                    } else {
                        // start next scanline
                        mode = HBLANK;
                        stat.setMF1(false);
                        stat.setMF2(false);
                        memory.writeByte(0xff0f,0b10);//set IF LCD flag
                    }
                }
                break;
            case 0: // HBLANK
                if (modeTicks >= 204) {
                    modeTicks = 0;
                    curX=0;
                    // Check if LYC=LY
                    if (curY == memory.readByte(0xFF45)&!stat.getBit(2)) {
                        //System.out.println("LYC=LY");
                        // Set the LYC=LY flag in STAT register
                        stat.setCoincidenceFlag(true);
                        //System.out.println("STAT interupt");

                        // Check if LYC=LY interrupt is enabled
                        if ((memory.readByte(0xFF41) & 0x40) == 0x40) {
                        } else {
                            // Request STAT/mode2 interrupt
                            stat.setBit(2,true);
                            //System.out.println("STAT interupt");
                        }
                    } else {
                        // Clear the LYC=LY flag in STAT register
                        stat.setCoincidenceFlag(false);
                    }
                    {
                        modeTicks=0;
                        mode = OAM_READ;
                        stat.setMF1(false);
                        stat.setMF2(true);
                        memory.writeByte(0xff0f,0b10);//set IF LCD flag
                    }
                    if(Sprite.getSpriteCount() > 10)
                    {
                        //System.out.println("getAll sprites size " + Sprite.getAllSprites().size());
                        //System.out.println("OVER 10 sprites on a line");
                        //int j = 9 / 0;
                        Sprite.sortSprites();
                        stat.setM2OAM(true);
                        //System.out.println("getAll sprites size " + Sprite.getAllSprites().size());
                    }
                }
                break;
            case 1: // VBLANK
                if (modeTicks >= 456) {
                    modeTicks = 0;
                    line++;
                    curY++;
                    //ly.setLY((byte)curY);
                    if (line >= 153&&curY>=153) {
                        // update the display with the pixel buffer
                        bugPanel.updatePane(map,tileSet);
                        display.render();
                        mode = OAM_READ;
                        stat.setMF1(false);
                        stat.setMF2(true);
                        memory.writeByte(0xff0f,0b10);//set IF LCD flag
                        line = 0;
                        curY=0;
                        // Set the coincidence flag if LYC=LY
                        if (line == (int)lyc.getByte()&!stat.getBit(2)) {
                            stat.setCoincidenceFlag(true);
                        } else {
                            stat.setCoincidenceFlag(false);
                        }

                        // Check if LYC=LY interrupt is enabled
                        if ((memory.readByte(0xFF41) & 0x40) == 0x40 && curY == memory.readByte(0xFF45)) {
                            // Request the LYC=LY interrupt
                            //System.out.println("STAT interupt");
                            int ifreg= memory.readByte(0xff0f);
                            stat.setBit(2,true);
                            memory.writeByte(0xff0f,ifreg|02);
                        }

                        // Request VBLANK interrupt
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

    // intialize the oam with the data from memory
    public void initOAM() {
        byte[] bytes = new byte[160];
        for (int i = 0xFE00; i <= 0xFE9F; i++) {
            //System.out.println("spriteBite value " + memory.readByte(i));
            bytes[i - 0xFE00] = (byte) memory.readByte(i);
        }
        // set the data array in oam to the bytes array here
        oam.setSpriteData(bytes);
    }
    public void loadMap(boolean useTileSet0, boolean useMap1) {
        int ts = useTileSet0 ? 0 : 1;
        int address = useMap1 ? 0x9c00 : 0x9800;
        this.map = new TileMap(memory, address, ts);
    }
    public void showDebug() {
        bugPanel.showPane();
    }
    public void printRAM() {//Proof of RAM working
        /*for(int i=0;i<0x180;i++) {//print ram hex values
            if(i%16==0)System.out.println();
            if((i&0xf)==0)System.out.print(" "+Integer.toHexString(0x8000+i));//maybe offset by location
            if(i%8==0)System.out.print(" | ");
            System.out.print(" "+Integer.toHexString(memory.readByte(0x8000+i))+" ");
        }*//*
        for(int y=0;y<8;y++) {//print a tile's values from set
            for(int x=0;x<8;x++) {
                        //Can use map: map.getTile(X,Y) OR regular tile Index
                System.out.print(" "+tileSet[map.getTile(0x08,0x02)].getVal(y,x)+" ");
                if(x==7)System.out.println();
            }
            boolean useTileSet0 = lcdc.getBit(4);
            boolean useBackgroundMap0 = ((lcdc.getByte())&0b1000)==1;
            boolean isStat3 = (stat.getBit(1)&&stat.getBit(0));
             if(y==7)System.out.println("-------------"+map.getTile(0x08,0x02)+" set:"+useTileSet0+" map:"+useBackgroundMap0
                +" statMode3? "+isStat3);
        }*//*
        System.out.println();
        for(int i=0;i<32;i++) {
            for (int j=0;j<32;j++) {
                if(j==0)System.out.print(i+":" );
                    System.out.print(" "+Integer.toHexString(map.getTile(j,i))+" ");
                if(j==31)System.out.println();
            }
        }*/
    }
    public void updateBugPane() {
        bugPanel.updatePane(map,tileSet);
    }


    public PPU getPPU(){
        return this;
    }

}

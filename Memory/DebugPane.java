package Memory;
import CPU.CPU;
import GPU.Sprite;
import GPU.TileMap;
import GPU.Tile;
import CPU.Registers;
import Memory.Memory.LY;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class DebugPane extends JFrame {
    CPU cpu;
    LY ly;
    Memory mem;
    JLabel regString= new JLabel("");
    int xMap=0;
    int yMap=0;
    BufferedImage image;
    BufferedImage spriteImage;
    Image spriteScaled;
    BufferedImage regImage;
    int[] clrs= {0xbbe9cd, 0x3fd87b, 0x10863f,0x043b19};

    public DebugPane(CPU cpu, Memory mem) {
        this.mem=mem;
        this.cpu=cpu;
        this.ly=mem.getLY();
        JPanel jp = new JPanel();
        jp.setLayout(null);
        setLayout(null);
        image = new BufferedImage(32*8,32*8,BufferedImage.TYPE_3BYTE_BGR);
        spriteImage = new BufferedImage(8*8+7,5*8+4,BufferedImage.TYPE_3BYTE_BGR);
        regImage = new BufferedImage(8*12+7,5*8,BufferedImage.TYPE_3BYTE_BGR);
        add(jp);
        add(regString);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setSize(550,400);
        setVisible(false);
    }
    public void showPane() {
        this.setVisible(true);
    }
    public void updatePane(TileMap map, Tile[] ts) {
        if (this.isVisible()) {
            for (int i = 0; i < 32; i++) {//Draws the TileMap
                for (int j = 0; j < 32; j++) {
                    //if(j==0)System.out.print(i+":" );
                    //System.out.print(" "+Integer.toHexString(map.getTile(j,i))+" ");
                    //if(j==31)System.out.println();
                    for (int x = 0; x < 8; x++) {
                        for (int y = 0; y < 8; y++) {
                            Tile t = ts[map.getTile(j, i)];
                            int color = 0;
                            if (t != null) color = mem.bgp.getColor(t.getVal(x, y), 2);
                            image.setRGB(j * 8 + y, i * 8 + x, color);
                        }
                    }
                }
            }
            int scrollX = mem.readByte(0xFF43);
            int scrollY = mem.readByte(0xFF42);
            for (int b = 0; b < 160; b++) {//DRAWS RED BORDER vvvv horizontal lines
                image.setRGB((scrollX + b)%(32*8), (scrollY), 0xff0000);
                image.setRGB((scrollX + b)%(32*8), (scrollY + 144)%(32*8), 0xff0000);

            }//vertical lines
            for (int b = 0; b < 144; b++) {
                    image.setRGB((scrollX), (scrollY + b)%(32*8), 0xff0000);
                    image.setRGB((scrollX + 159)%(32*8), (scrollY + b)%(32*8), 0xff0000);
            }
            int oamStart=0xfe00;
            if(true) {
                for (int sr = 0; sr < 8; sr++) {
                    for (int sc = 0; sc < 5; sc++) {
                        int spriteIndex = mem.readByte(0xfe02+(sr+sc*8)*4);
                        Tile obj = ts[spriteIndex];
                        int objpal = (mem.readByte(0xfe03+(sr+sc*8)*4)&8)==0? 0:1;
                        for (int x = 0; x < 8; x++) {
                            for (int y = 0; y < 8; y++) {
                                int color = 0;
                                if (objpal == 0) color = mem.obp0.getColor(obj.getVal(x, y), 2);
                                else color = mem.obp1.getColor(obj.getVal(x, y), 2);
                                spriteImage.setRGB( y+sr*9 , x+sc*9, color);
                            }
                        }
                    }
                }
            }
            spriteScaled=spriteImage.getScaledInstance(32*8,32*5,Image.SCALE_DEFAULT);
            this.getGraphics().drawImage(spriteScaled, 280, 50, this);//offset to get all of Frame on screen
            this.getGraphics().drawImage(image, 10, 32, this);//offset to get all of Frame on screen
        }
        Registers regs = cpu.getRegs();
        int currentPC=regs.getPC();
        String s = String.format(
                "A:%1$02X F:%2$02X B:%3$02X C:%4$02X D:%5$02X E:%6$02X H:%7$02X L:%8$02X SP:%9$04X PC:%10$04X PCMEM:%11$02X,%12$02X,%13$02X,%14$02X LY:%15$02X",
                regs.getA(), regs.fByte.getFByte(),
                regs.getB(), regs.getC(), regs.getD(), regs.getE(), regs.getH(), regs.getL(), regs.getSP(),
                regs.getPC(),
                mem.readByte(currentPC), mem.readByte(currentPC + 1), mem.readByte(currentPC + 2),
                mem.readByte(currentPC + 3),ly.getByte());

        //removeAll();
        //revalidate();
        regString.setText(s);
        //repaint();
        regString.setLocation(20,280);
        regString.setSize(475,20);
    }
}

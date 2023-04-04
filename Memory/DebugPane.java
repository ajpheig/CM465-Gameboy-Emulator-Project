package Memory;
import CPU.CPU;
import GPU.TileMap;
import GPU.Tile;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class DebugPane extends JFrame {
    CPU cpu;
    Memory mem;
    int xMap=0;
    int yMap=0;
    BufferedImage image;
    int[] clrs= {0xbbe9cd, 0x3fd87b, 0x10863f,0x043b19};

    public DebugPane(CPU cpu, Memory mem) {
        this.mem=mem;
        this.cpu=cpu;
        JPanel jp = new JPanel();
        image = new BufferedImage(32*8,32*8,BufferedImage.TYPE_3BYTE_BGR);
        add(jp);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500,500);
        setVisible(true);
    }
    public void destroyPane() {

    }
    public void updatePane(TileMap map, Tile[] ts) {
        for(int i=0;i<32;i++) {//Draws the TileMap
            for (int j=0;j<32;j++) {
                //if(j==0)System.out.print(i+":" );
                //System.out.print(" "+Integer.toHexString(map.getTile(j,i))+" ");
                //if(j==31)System.out.println();
                for (int x=0;x<8;x++) {
                    for (int y=0;y<8;y++) {
                        Tile t =ts[map.getTile(j, i)];
                        int color=0;
                        if(t!=null) color = mem.bgp.getColor(t.getVal(x,y),2);
                        image.setRGB(j*8+y, i*8+x, color);
                    }
                }
            }
        }
        int scrollX = mem.readByte(0xFF43);
        int scrollY = mem.readByte(0xFF42);
        for(int b=0;b<160;b++){
            if(scrollY+144<32*8) {
                image.setRGB(scrollX + b, scrollY, 0xff0000);
                image.setRGB(scrollX + b, scrollY + 144, 0xff0000);
            }
        }
        for(int b=0;b<144;b++){
            if(scrollX+160<32*8&&scrollY+b<32*8) {
                image.setRGB(scrollX,scrollY+b,0xff0000);
                image.setRGB(scrollX+160,scrollY+b,0xff0000);
            }
        }
        this.getGraphics().drawImage(image, 10, 32, this);//offset to get all of Frame on screen
    }
}

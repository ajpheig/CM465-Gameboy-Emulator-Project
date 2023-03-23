package GPU;
import Memory.*;
import Memory.Memory.VRAM;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.*;

public class Display {
    // array of all the pixels in the screen
    private int[] screenBuffer;
    private int[] screenAlphabuff;
    private int screenWidth;
    private int screenHeight;
    private JFrame frame;
    Tile[] tileSet;
    Memory mem;
    VRAM vram;
    BufferedImage image = new BufferedImage(160, 144, BufferedImage.TYPE_3BYTE_BGR);;

    public Display(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        screenBuffer = new int[screenHeight * screenWidth];
        frame = new JFrame();
        frame.add(new JLabel(new ImageIcon(image)));
        frame.setSize(200,200);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
        clearFrame();
        frame.getGraphics().drawImage(image, 0, 0, frame);
        frame.repaint();
    }

    // update the display with new pixels passed in
    public void render() {
        //System.out.println("render");
        for (int y = 0; y < screenHeight; y++) {
            for (int x = 0; x < screenWidth; x++) {
                int color = screenBuffer[x + y * screenWidth];
                this.image.setRGB(x, y, color);
            }
        }
        clearFrame();
        frame.getGraphics().drawImage(image, 20, 39, frame);//offset to get all of Frame on screen
        frame.repaint();
    }

    // set the color of specific pixel passed in at (x,y) to the color passed in
    public void setPixel(int x, int y, int color) {
        if (x >= 0 && x < screenWidth && y >= 0 && y < screenHeight) {
            screenBuffer[x + y * 160] = color;//write values to buffer
        }
    }

    public void setMemInDisplay(Memory mem){//for debugging tilesets
        this.mem=mem;
        vram=mem.getVram();
        tileSet=vram.getTileSet();
    }

    public void clearFrame() {
        for(int x=0;x<160;x++) {//sets pixels to white to start
            for (int y=0;y<144;y++){
                setPixel(x, y, 0xffffff);
            }
        }
    }
}

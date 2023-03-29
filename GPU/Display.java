package GPU;
import Memory.*;
import Memory.Memory.VRAM;
import CPU.*;
import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
    int scale=3;
    Joypad joypad;
    InterruptManager intMan;
    KeyHandler kh;
    BufferedImage image = new BufferedImage(160, 144, BufferedImage.TYPE_3BYTE_BGR);;

    public Display(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        screenBuffer = new int[screenHeight * screenWidth];
        frame = new JFrame();
        frame.setSize(screenWidth * scale+20, screenHeight * scale+40);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Image scaledImage = image.getScaledInstance(screenWidth * scale, screenHeight * scale, Image.SCALE_DEFAULT);
        //frame.add(new JLabel(new ImageIcon(scaledImage)));
        frame.setVisible(true);
        //clearFrame();
        render();
        frame.getGraphics().drawImage(scaledImage, 0, 0, frame);
        frame.repaint();
        kh = new KeyHandler();
        frame.addKeyListener(kh);

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
        Image scaledImage = image.getScaledInstance(screenWidth * scale, screenHeight * scale, Image.SCALE_DEFAULT);
        //clearFrame();
        frame.getGraphics().drawImage(scaledImage, 10, 32, frame);//offset to get all of Frame on screen
        //frame.repaint();
        frame.requestFocus();
    }

    // set the color of specific pixel passed in at (x,y) to the color passed in
    public void setPixel(int x, int y, int color) {
        if (x >= 0 && x < screenWidth && y >= 0 && y < screenHeight) {
            screenBuffer[x + y * 160] = color;//write values to buffer
        }
    }

    public void setMemInDisplay(Memory mem, InterruptManager intMan){//for debugging tilesets
        this.mem=mem;
        vram=mem.getVram();
        tileSet=vram.getTileSet();
        this.joypad=mem.getJoypad();
        this.intMan=intMan;
    }

    public void clearFrame() {
        for(int x=0;x<160;x++) {//sets pixels to white to start
            for (int y=0;y<144;y++){
                setPixel(x, y, 0xeeeeee);
            }
        }
    }
    private class KeyHandler extends KeyAdapter {
        public void keyPressed(KeyEvent ke) {
            //System.out.println("something pressed"+joypad.getControlSelect());
            switch (ke.getKeyCode()){
                case KeyEvent.VK_ENTER:
                    if(joypad.start==1)intMan.postInterrupt(InterruptManager.JOYPAD);
                    joypad.setPadBits(5,3);
                    break;
                case KeyEvent.VK_BACK_SPACE:
                    if(joypad.select==1)intMan.postInterrupt(InterruptManager.JOYPAD);
                    joypad.setPadBits(5,2);
                    break;
                case KeyEvent.VK_X://a button
                    if(joypad.a==1)intMan.postInterrupt(InterruptManager.JOYPAD);
                    joypad.setPadBits(5,0);
                    break;
                case KeyEvent.VK_Z://b button
                    if(joypad.b==1)intMan.postInterrupt(InterruptManager.JOYPAD);
                    joypad.setPadBits(5,1);
                    break;
                case KeyEvent.VK_DOWN:
                    if(joypad.down==1)intMan.postInterrupt(InterruptManager.JOYPAD);
                    joypad.setPadBits(4,3);
                    break;
                case KeyEvent.VK_UP:
                    if(joypad.up==1)intMan.postInterrupt(InterruptManager.JOYPAD);
                    joypad.setPadBits(4,2);
                    break;
                case KeyEvent.VK_LEFT:
                    if(joypad.left==1)intMan.postInterrupt(InterruptManager.JOYPAD);
                    joypad.setPadBits(4,1);
                    break;
                case KeyEvent.VK_RIGHT:
                    if(joypad.right==1)intMan.postInterrupt(InterruptManager.JOYPAD);
                    joypad.setPadBits(4,0);
                    break;
            }
        }
        public void keyReleased(KeyEvent ke) {
            switch (ke.getKeyCode()){
                case KeyEvent.VK_ENTER:
                    joypad.resetActionBits(3);
                    break;
                case KeyEvent.VK_BACK_SPACE:
                    joypad.resetActionBits(2);
                    break;
                case KeyEvent.VK_X://a button
                    joypad.resetActionBits(0);
                    break;
                case KeyEvent.VK_Z://b button
                    joypad.resetActionBits(1);
                    break;
                case KeyEvent.VK_DOWN:
                    joypad.resetDirBits(3);
                    break;
                case KeyEvent.VK_UP:
                    joypad.resetDirBits(2);
                    break;
                case KeyEvent.VK_LEFT:
                    joypad.resetDirBits(1);
                    break;
                case KeyEvent.VK_RIGHT:
                    joypad.resetDirBits(0);
                    break;
            }
        } //
    }
}

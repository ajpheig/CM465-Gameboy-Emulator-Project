package GPU;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class Display {
    // array of all the pixels in the screen
    private int[] screenBuffer;
    private int screenWidth;
    private int screenHeight;
    private JFrame frame;
    BufferedImage image = new BufferedImage(160, 144, BufferedImage.TYPE_3BYTE_BGR);;

    public Display(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        screenBuffer = new int[screenHeight * screenWidth];
        frame = new JFrame();
        image = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_3BYTE_BGR); // adjust image size
        frame.add(new JLabel(new ImageIcon(image)));
        frame.pack();
        frame.setVisible(true);
    }


    // update the display with new pixels passed in
    public void render() {
        System.out.println("render");
        frame.getGraphics().drawImage(image, 0, 0, null);
        frame.repaint();
    }

    // set the color of specific pixel passed in at (x,y) to the color passed in
    public void setPixel(int x, int y, int color) {
        if (x >= 0 && x < screenWidth && y >= 0 && y < screenHeight) {
            screenBuffer[x % screenWidth + y * 160] = color;
            // or do you want to setRGB
            image.setRGB(x - 12, y, color);
        }
    }
}

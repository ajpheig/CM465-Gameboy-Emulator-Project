package gameboy;

import java.awt.*;
import javax.swing.*;

public class GBScreen extends JPanel {

    private int width = 160;
    private int height = 144;
    private int[] frameData = new int[160 * 144 * 4];
    int modeclock = 0;
    int mode = 2;

    public GBScreen() {
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.BLACK);
        for (int i = 0; i < 160 * 144 * 4; i++) {// initialize to white value
            frameData[i] = 255;
        }
        /*
         * 144 rows, 144-154 VBLANK period
         * Scanline (OAM) 80 clocks
         * Scanline (VRAM) 172 clocks
         * hblank 204 clocks
         * one line 456 clocks
         * VBLANK 4560 clocks
         * full frame 70224
         */
    }

    public void paint(int cpuTicks) {

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int r = 0; r < 160; r++) {
            for (int c = 0; c < 144; c++) {

            }
        }
    }
}
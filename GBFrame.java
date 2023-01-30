import java.awt.*;
import javax.swing.*;

public class GBFrame extends JFrame {
    private int width = 500;
    private int height = 500;

    public GBFrame() {
        GBScreen gbScreen = new GBScreen(width, height);
        add(gbScreen);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        new GBFrame();
    }
}
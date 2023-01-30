import java.awt.*;
import javax.swing.*;

public class GBScreen extends JPanel {
    public GBScreen(int maxX, int maxY) {
        setPreferredSize(new Dimension(maxX, maxY));
        setBackground(Color.BLACK);
    }
}
//package gameboy;
//
//import javax.swing.*;
//
//public class GBFrame extends JFrame {
//    private int width = 500;
//    private int height = 500;
//
//    public GBFrame() {
//        GBScreen gbScreen = new GBScreen(width, height);
//        add(gbScreen);
//        JMenuBar menubar = new JMenuBar();
//        JMenu file = new JMenu("file");
//        menubar.add(file);
//        JMenuItem load = new JMenuItem("Load ROM");
//        file.add(load);
//        setJMenuBar(menubar);
//        setDefaultCloseOperation(EXIT_ON_CLOSE);
//        pack();
////        setVisible(true);
//    }
//
//    public static void main(String[] args) {
//        new GBFrame();
//    }
//}
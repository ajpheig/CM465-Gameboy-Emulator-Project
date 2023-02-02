import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
//this class reads the Game Boy game rom file as binary data

public class ReadGBFC{
    JFrame frame;
    JMenu m;
    JMenuBar mb;
    JMenuItem m1;
    File romFile;
    JFileChooser fc;
    public ReadGBFC(){
        
        frame = new JFrame("GameBoy");
        FCListener fcl = new FCListener();
        mb = new JMenuBar();
        m = new JMenu("File");
        fc = new JFileChooser();
        
        
        m1 = new JMenuItem("Load ROM");
        
        m.add(m1);
        m1.addActionListener(fcl);
        mb.add(m); 
        
        frame.setJMenuBar(mb);
        
        frame.setSize(500,500);
        frame.setVisible(true);
        //File romFile = new File("/Users/brettkulp/Desktop/Capstone/Emulator/src/Tetris (World) (Rev A).gb");
        
        //romFile = new File("dmg_boot.bin");
        
        
        
        
        
        
    }
    public class FCListener implements ActionListener  {
        public void actionPerformed(ActionEvent e)
        {
            if(e.getSource() == m1)
            {
                int ret = -1;
                while(ret != JFileChooser.APPROVE_OPTION)
                {
                    ret = fc.showOpenDialog(frame);
                }
                if(ret == JFileChooser.APPROVE_OPTION)
                {
                    romFile = fc.getSelectedFile();
                }
                byte[] romData = new byte[(int)romFile.length()];
                try
                {
                    FileInputStream romStream = new FileInputStream(romFile);
                    romStream.read(romData);
                    romStream.close();
                }
                catch (IOException ioe)
                {
                    ioe.printStackTrace();
                }
        //here is where we do something with the ROM data currently just printing it out in here
        int i = 0;
        for (byte b : romData) {
            if(i  % 10 == 0 && i != 0){
                System.out.println();
            }
            System.out.print(String.format("%02X ", b & 0xFF));
            i++;
            }
            }
        }
    }
        
        public static void main(String[] args){
            new ReadGBFC();
        }
}
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.*;

public class ReadGBFC{
    JFrame frame;
    JMenu m;
    JMenuBar mb;
    JMenuItem m1;
    File romFile;
    JFileChooser fc;
    byte[] romData;
	JLabel gameInfoHead = new JLabel("Game info that is read from the ROM will appear below after selecting file.");
	JLabel gameInfo = new JLabel("Select ROM file");
	

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
		frame.add(gameInfo);
		frame.add(BorderLayout.NORTH, gameInfoHead);
        frame.setJMenuBar(mb);
        frame.setSize(500,500);
        frame.setVisible(true);
    }

    public class FCListener implements ActionListener {
        public void actionPerformed(ActionEvent e)
        {
            if(e.getSource() == m1)
            {
                int ret = fc.showOpenDialog(frame);
                if(ret == JFileChooser.APPROVE_OPTION)
                {
                    romFile = fc.getSelectedFile();
                    try {
                        FileInputStream romStream = new FileInputStream(romFile);
                        romData = new byte[(int)romFile.length()];
                        romStream.read(romData);
                        romStream.close();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                    // call function to print the rom data
                    pullCartHeader();
					printROMData();
                }
            }
        }
        // we can pass the array with the rom data like this
        // methodName(romData)
    }

    public void printROMData() {
        for (int i = 0; i < romData.length; i++) {
            if (i % 10 == 0 && i != 0) {
                System.out.println();
            }
            System.out.print(String.format("%02X ", romData[i] & 0xFF));
            //System.out.print(romData[i]);
        }
    }
	public void pullCartHeader() {
		try {
			byte[] titleBytes = new byte[9];
			for(int i=0;i<9;i++) { titleBytes[i] = romData[308+i]; }
			String title = new String(titleBytes, "ASCII");
			title += "   $" + String.format("%02X",romData[328]& 0xFF); 
			title+= "  ROM Checksum: "+ String.format("%02X",romData[0x014D]& 0xFF);
			byte checksum = 0;
			for(int b =0x0134;b<=0x014C;b++){ checksum-=(romData[b]+1); }
			title+= "  Computed Checksum: " + String.format("%02X",checksum& 0xFF);
			gameInfo.setText(title);
		}
		catch(UnsupportedEncodingException uee) {}
	}

    public static void main(String[] args){
        new ReadGBFC();
    }
}

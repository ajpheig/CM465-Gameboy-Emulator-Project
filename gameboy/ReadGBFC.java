package gameboy;

import java.io.*;
import javax.swing.*;

import GPU.Display;
import Memory.*;
import CPU.CPU;
import CPU.InterruptManager;
import CPU.Registers;
import Memory.Memory;
import GPU.PPU;

import java.awt.*;

import java.awt.event.*;

public class ReadGBFC {
    JFrame frame;
    JMenu m;
    JMenu debug;
    JMenuBar mb;
    JMenuItem m1;
    JMenuItem debugItem;
    JMenuItem stepThruMode;
    JPanel debugP;
    JLabel reg;
    JLabel flagsLabel;
    JLabel memRegsLabel;
    File romFile;
    JFileChooser fc;
    byte[] romData;
    Registers regs = new Registers();
    CPU cpu;
    Memory mem;
    Ram ram = new Ram(0x8000);// 2KiB Vram
    InterruptManager interruptManager = new InterruptManager();
    PPU ppu;
    boolean running=false;
    boolean stepMode=false;
    Display display;
    Worker w;
    public ReadGBFC() {
        frame = new JFrame("WUBoy");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        FCListener fcl = new FCListener();
        mb = new JMenuBar();
        m = new JMenu("File");
        debug = new JMenu("Debug");
        fc = new JFileChooser("C:/Users/ajphe/Documents/Homework/CM465 CIS Capston/GBVStest/blargg/cpu_instrs");
        KeyHandler kh = new KeyHandler();// to step thru cpu instructions
        frame.addKeyListener(kh);
        //debugPanel();
        m1 = new JMenuItem("Load ROM");
        debugItem = new JMenuItem("Show Debugger");
        stepThruMode = new JMenuItem("Step Through Mode");
        m.add(m1);
        debug.add(debugItem);
        debug.add(stepThruMode);
        m1.addActionListener(fcl);
        debugItem.addActionListener(fcl);
        stepThruMode.addActionListener(fcl);
        mb.add(m);
        mb.add(debug);
        frame.setJMenuBar(mb);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        display = new Display(160, 144, this);
        display.setBackground(Color.LIGHT_GRAY);
        display.setPreferredSize(new Dimension(500,500));
        frame.add(display);
        frame.setVisible(true);
        //while(!running){
            ;//do nothing until listener sets running
            //System.out.print("");

         //   if(this.running==true) {
         //       cpu.setRun();
          //      cpu.runUntil(-1);
        //    }
        //}
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                // define the behavior when the window is closed
                if(mem!=null) {
                    if(mem.getRamBanks()!=null) {
                        try {

                            byte[][] ramBanks = mem.getRamBanks();
                            FileOutputStream w = new FileOutputStream(new File(romFile.toString().substring(0, romFile.toString().length() - 3) + ".sav"));
                            for (int i = 0; i < ramBanks.length; i++) {
                                w.write(ramBanks[i]);
                            }


                            w.close();

                        } catch (IOException se) {
                            System.out.println("An error occurred while writing to the save file: " + se.getMessage());
                        }
                    }
                }
                System.exit(0);
            }
        });

}
    public class FCListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == m1) {

                int ret = fc.showOpenDialog(frame);
                if (ret == JFileChooser.APPROVE_OPTION) {
                    romFile = fc.getSelectedFile();
                    try {
                        FileInputStream romStream = new FileInputStream(romFile);
                        romData = new byte[(int) romFile.length()];
                        romStream.read(romData);
                        //System.out.print(romFile.toString().substring(0,romFile.toString().length()-3));
                        romStream.close();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                    if(running==true) {
                        synchronized (cpu){cpu.running=false;}
                        if(w!=null)w.interrupt();
                        w = new Worker();
                        w.start();
                    }//resetGameboy(romData);
                    else {
                        mem = new Memory(romData, romFile.toString().substring(0,romFile.toString().length()-3));
                        cpu = new CPU(romData, regs, interruptManager, mem, ReadGBFC.this);
                        ppu = new PPU(romData, cpu, ram, interruptManager, display, mem);
                        ReadGBFC.this.running = true;
                        if(w!=null)w.interrupt();
                        w = new Worker();
                        w.start();
                    }
                    // call the executeOpcodes method in the instance of the Opcode class that calls
                    // the funcitons for
                    // the opcodes currently the whole array of rom data is passed. We might want to
                    // call this method
                    // one opcode at a time
                    // runOpcodes.executeOpcodes(romData);
                }
            }
            if(e.getSource()==debugItem){
                if(ppu!=null)ppu.showDebug();
            }
            if (e.getSource() == stepThruMode) {
                if(cpu.running==true)
                {
                    stepMode=true;
                 //stop CPU
                 cpu.running=false;
                }

            }
        }
    }

    public void printROMData() {
        for (int i = 0; i < romData.length; i++) {
            if (i % 10 == 0 && i != 0) {
                //System.out.println();
            }
            System.out.print(String.format("%02X ", romData[i] & 0xFF));
            // System.out.print(romData[i]);
        }
    }

    public void pullCartHeader() {
        try {
            byte[] titleBytes = new byte[9];
            for (int i = 0; i < 9; i++) {
                titleBytes[i] = romData[308 + i];
            }
            String title = new String(titleBytes, "ASCII");
            title += "   $" + String.format("%02X", romData[328] & 0xFF);
            title += "  ROM Checksum: " + String.format("%02X", romData[0x014D] & 0xFF);
            byte checksum = 0;
            for (int b = 0x0134; b <= 0x014C; b++) {
                checksum -= (romData[b] + 1);
            }
            title += "  Computed Checksum: " + String.format("%02X", checksum & 0xFF);
        } catch (UnsupportedEncodingException uee) {
        }
    }
    public JFrame getFrame() {
        return this.frame;
    }

    public void debugPanel() {
        debugP = new JPanel();
        reg = new JLabel(" af= " + regs.getAF() + " bc= " + regs.getBC() + " de= "
                + regs.getDE() + " hl= " + regs.getHL() + " sp= " + regs.getSP() + " pc= " + regs.getPC());
        reg.setPreferredSize(new Dimension(400, 10));
        flagsLabel = new JLabel(" z= " + regs.fByte.checkZ() + " n= " + regs.fByte.checkN() + " h= "
                + regs.fByte.checkH() + " c= " + regs.fByte.checkC() + " ");
        flagsLabel.setPreferredSize(new Dimension(400, 10));
        memRegsLabel = new JLabel(" lcdc= " + " stat= " + " ly= "
                + " if= " + " ");
        memRegsLabel.setPreferredSize(new Dimension(400, 10));
        debugP.setLayout(new BoxLayout(debugP, BoxLayout.PAGE_AXIS));
        debugP.add(reg);
        debugP.add(flagsLabel);
        debugP.add(memRegsLabel);
        // frame.add(BorderLayout.NORTH, debugP);
    }

    public void refreshPanel() {
        reg.setText(" af= " + Integer.toHexString(regs.getAF()) + " bc= " + Integer.toHexString(regs.getBC()) + " de= "
                + Integer.toHexString(regs.getDE()) + " hl= " + Integer.toHexString(regs.getHL()) + " sp= "
                + Integer.toHexString(regs.getSP()) + " pc= " + Integer.toHexString(regs.getPC()));
        reg.setPreferredSize(new Dimension(500, 10));
        flagsLabel.setText(" z= " + regs.fByte.checkZ() + " n= " + regs.fByte.checkN() + " h= "
                + regs.fByte.checkH() + " c= " + regs.fByte.checkC() + " ");
        flagsLabel.setPreferredSize(new Dimension(400, 10));
        memRegsLabel.setText(" lcdc= " + mem.readByte(0xff40) + " stat= " + mem.readByte(0xff41) + " ly= "
                + mem.readByte(0xff44) + " if= " + mem.readByte(0xff0f) + " ");
        memRegsLabel.setPreferredSize(new Dimension(400, 10));
        debugP.add(reg);
        debugP.add(flagsLabel);
        debugP.add(memRegsLabel);
        frame.add(BorderLayout.NORTH, debugP);
        frame.repaint();
    }

    private class KeyHandler extends KeyAdapter {
        public void keyPressed(KeyEvent ke) {
            if (ke.getKeyCode() == KeyEvent.VK_ENTER&&
                    stepMode) {
                cpu.step();
                ppu.updateBugPane();
            }
            if (ke.getKeyCode() == KeyEvent.VK_SPACE&&
                    stepMode) {
                w = new Worker();
                w.start();
                //cpu.setRun();
                //cpu.runUntil(0-1);// insert PC you want to stop at
            } //
        }
    }
    public void resetGameboy(byte[] romData) {
        if(interruptManager!=null) interruptManager = new InterruptManager();;
        if(regs!=null) regs = new Registers();
        display.setBackground(Color.LIGHT_GRAY);
        display.setPreferredSize(new Dimension(500,500));
        frame.add(display);
        ppu.bugPanel.dispose();//will duplicate if not here
        mem = new Memory(romData, romFile.toString().substring(0,romFile.toString().length()-3));
        cpu = new CPU(romData, regs, interruptManager, mem, ReadGBFC.this);
        ppu = new PPU(romData, cpu, ram, interruptManager, display, mem);
        ReadGBFC.this.running = true;
        if(this.running==true) {
            cpu.setRun();
            cpu.runUntil(0xffff);
        }
    }
    public void resumeGameboy() {
        cpu.running=true;
        cpu.setRun();
        cpu.runUntil(0xffff);
    }
    private class Worker extends Thread {

        public void run() {

            if(!stepMode) resetGameboy(romData);
            else if(stepMode)
            {
                stepMode=false;
                resumeGameboy();
            }
        }
    }
    public static String getFileNameFromPath(String path) {
        File file = new File(path);
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex <= fileName.length() - 2) {
            fileName = fileName.substring(0, dotIndex);
        }
        return fileName;
    }
    public static String getRomName(File rom) {
        String romName = getFileNameFromPath(rom.toString());
        return romName;
    }

    // old
    public static void main(String[] args) {
        new ReadGBFC();
    }
}

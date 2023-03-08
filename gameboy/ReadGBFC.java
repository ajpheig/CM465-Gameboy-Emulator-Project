package gameboy;

import java.io.*;
import javax.swing.*;

import CPU.CPU;
import CPU.InterruptManager;
import CPU.Registers;
import Memory.Memory;

import java.awt.*;
import java.awt.event.*;

public class ReadGBFC {
    JFrame frame;
    JMenu m;
    JMenuBar mb;
    JMenuItem m1;
    JPanel debugP;
    JLabel reg;
    JLabel flagsLabel;
    JLabel memRegsLabel;
    File romFile;
    JFileChooser fc;
    byte[] romData;
    JLabel gameInfoHead = new JLabel("Game info that is read from the ROM will appear below after selecting file.");
    JLabel gameInfo = new JLabel("Select ROM file");
    // Opcodes runOpcodes = new Opcodes();
    Registers regs = new Registers();
    CPU cpu;
    Memory mem;
    Ram ram;
    InterruptManager interruptManager = new InterruptManager();
    PPU ppu;

    public ReadGBFC() {
        frame = new JFrame("GameBoy");
        FCListener fcl = new FCListener();
        mb = new JMenuBar();
        m = new JMenu("File");
        fc = new JFileChooser("C:/Users/ajphe/Documents/Homework/CM465 CIS Capston/GBVStest/blargg/cpu_instrs");
        KeyHandler kh = new KeyHandler();// to step thru cpu instructions
        frame.addKeyListener(kh);
        debugPanel();
        m1 = new JMenuItem("Load ROM");
        m.add(m1);
        m1.addActionListener(fcl);
        mb.add(m);
        // frame.add(gameInfo);
        // frame.add(BorderLayout.NORTH, gameInfoHead);
        frame.setJMenuBar(mb);
        frame.setSize(500, 500);
        frame.setVisible(true);
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
                        romStream.close();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                    // call function to print the rom data
                    pullCartHeader();
                    // printROMData();
                    // printOpcodes();
                    mem = new Memory(romData);
                    cpu = new CPU(romData, regs, interruptManager, mem, ReadGBFC.this);
                    // ppu = new PPU(romData, cpu, ram, interruptManager);
                    // call the executeOpcodes method in the instance of the Opcode class that calls
                    // the funcitons for
                    // the opcodes currently the whole array of rom data is passed. We might want to
                    // call this method
                    // one opcode at a time
                    // runOpcodes.executeOpcodes(romData);
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
            gameInfo.setText(title);
        } catch (UnsupportedEncodingException uee) {
        }
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
        frame.add(BorderLayout.NORTH, debugP);
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
    }

    private class KeyHandler extends KeyAdapter {
        public void keyPressed(KeyEvent ke) {
            if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                cpu.step();
            }
            if (ke.getKeyCode() == KeyEvent.VK_SPACE) {
                cpu.setRun();
                cpu.runUntil(0x210);//
            }
        }
    }

    // old
    public static void main(String[] args) {
        new ReadGBFC();
    }
}

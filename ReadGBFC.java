import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class ReadGBFC {
    JFrame frame;
    JMenu m;
    JMenuBar mb;
    JMenuItem m1;
    File romFile;
    JFileChooser fc;
    byte[] romData;
    JLabel gameInfoHead = new JLabel("Game info that is read from the ROM will appear below after selecting file.");
    JLabel gameInfo = new JLabel("Select ROM file");
    // Opcodes runOpcodes = new Opcodes();
    Registers regs = new Registers();
    CPU cpu;

    public ReadGBFC() {
        frame = new JFrame("GameBoy");
        FCListener fcl = new FCListener();
        mb = new JMenuBar();
        m = new JMenu("File");
        fc = new JFileChooser("C:/Users/ajphe/Documents/Homework/CM465 CIS Capston/GBVStest/blargg/cpu_instrs");
        KeyHandler kh = new KeyHandler();// to step thru cpu instructions
        frame.addKeyListener(kh);

        m1 = new JMenuItem("Load ROM");
        m.add(m1);
        m1.addActionListener(fcl);
        mb.add(m);
        frame.add(gameInfo);
        frame.add(BorderLayout.NORTH, gameInfoHead);
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
                    cpu = new CPU(romData, regs);
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

    // currently prints the opcodes we will need to make these so they call the
    // correct function
    /*
     * public void printOpcodes() {
     * String[] operations = new String[256];
     * operations[0x0] = "NOP";
     * operations[0x1] = "LD BC,d16";
     * operations[0x2] = "LD (BC),A";
     * operations[0x3] = "INC BC";
     * operations[0x4] = "INC B";
     * operations[0x5] = "DEC B";
     * operations[0x6] = "LD B,d8";
     * operations[0x7] = "RLCA";
     * operations[0x8] = "LD (a16),SP";
     * operations[0x9] = "ADD HL,BC";
     * operations[0xa] = "LD A,(BC)";
     * operations[0xb] = "DEC BC";
     * operations[0xc] = "INC C";
     * operations[0xd] = "DEC C";
     * operations[0xe] = "LD C,d8";
     * operations[0xf] = "RRCA";
     * operations[0x10] = "STOP";
     * operations[0x11] = "LD DE,d16";
     * operations[0x12] = "LD (DE),A";
     * operations[0x13] = "INC DE";
     * operations[0x14] = "INC D";
     * operations[0x15] = "DEC D";
     * operations[0x16] = "LD D,d8";
     * operations[0x17] = "RLA";
     * operations[0x18] = "JR r8";
     * operations[0x19] = "ADD HL,DE";
     * operations[0x1a] = "LD A,(DE)";
     * operations[0x1b] = "DEC DE";
     * operations[0x1c] = "INC E";
     * operations[0x1d] = "DEC E";
     * operations[0x1e] = "LD E,d8";
     * operations[0x1f] = "RRA";
     * operations[0x20] = "JR NZ,r8";
     * operations[0x21] = "LD HL,d16";
     * operations[0x22] = "LD (HL+),A";
     * operations[0x23] = "INC HL";
     * operations[0x24] = "INC H";
     * operations[0x25] = "DEC H";
     * operations[0x26] = "LD H,d8";
     * operations[0x27] = "DAA";
     * operations[0x28] = "JR Z,r8";
     * operations[0x29] = "ADD HL,HL";
     * operations[0x2a] = "LD A,(HL+)";
     * operations[0x2b] = "DEC HL";
     * operations[0x2c] = "INC L";
     * operations[0x2d] = "DEC L";
     * operations[0x2e] = "LD L,d8";
     * operations[0x2f] = "CPL";
     * operations[0x30] = "JR NC,r8";
     * operations[0x31] = "LD SP,d16";
     * operations[0x32] = "LD (HL-),A";
     * operations[0x33] = "INC SP";
     * operations[0x34] = "INC (HL)";
     * operations[0x35] = "DEC (HL)";
     * operations[0x36] = "LD (HL),d8";
     * operations[0x37] = "SCF";
     * operations[0x38] = "JR C(cond),r8";
     * operations[0x39] = "ADD HL,SP";
     * operations[0x3a] = "LD A,(HL-)";
     * operations[0x3b] = "DEC SP";
     * operations[0x3c] = "INC A";
     * operations[0x3d] = "DEC A";
     * operations[0x3e] = "LD A,d8";
     * operations[0x3f] = "CCF";
     * operations[0x40] = "LD B,B";
     * operations[0x41] = "LD B,C";
     * operations[0x42] = "LD B,D";
     * operations[0x43] = "LD B,E";
     * operations[0x44] = "LD B,H";
     * operations[0x45] = "LD B,L";
     * operations[0x46] = "LD B,(HL)";
     * operations[0x47] = "LD B,A";
     * operations[0x48] = "LD C,B";
     * operations[0x49] = "LD C,C";
     * operations[0x4a] = "LD C,D";
     * operations[0x4b] = "LD C,E";
     * operations[0x4c] = "LD C,H";
     * operations[0x4d] = "LD C,L";
     * operations[0x4e] = "LD C,(HL)";
     * operations[0x4f] = "LD C,A";
     * operations[0x50] = "LD D,B";
     * operations[0x51] = "LD D,C";
     * operations[0x52] = "LD D,D";
     * operations[0x53] = "LD D,E";
     * operations[0x54] = "LD D,H";
     * operations[0x55] = "LD D,L";
     * operations[0x56] = "LD D,(HL)";
     * operations[0x57] = "LD D,A";
     * operations[0x58] = "LD E,B";
     * operations[0x59] = "LD E,C";
     * operations[0x5a] = "LD E,D";
     * operations[0x5b] = "LD E,E";
     * operations[0x5c] = "LD E,H";
     * operations[0x5d] = "LD E,L";
     * operations[0x5e] = "LD E,(HL)";
     * operations[0x5f] = "LD E,A";
     * operations[0x60] = "LD H,B";
     * operations[0x61] = "LD H,C";
     * operations[0x62] = "LD H,D";
     * operations[0x63] = "LD H,E";
     * operations[0x64] = "LD H,H";
     * operations[0x65] = "LD H,L";
     * operations[0x66] = "LD H,(HL)";
     * operations[0x67] = "LD H,A";
     * operations[0x68] = "LD L,B";
     * operations[0x69] = "LD L,C";
     * operations[0x6a] = "LD L,D";
     * operations[0x6b] = "LD L,E";
     * operations[0x6c] = "LD L,H";
     * operations[0x6d] = "LD L,L";
     * operations[0x6e] = "LD L,(HL)";
     * operations[0x6f] = "LD L,A";
     * operations[0x70] = "LD (HL),B";
     * operations[0x71] = "LD (HL),C";
     * operations[0x72] = "LD (HL),D";
     * operations[0x73] = "LD (HL),E";
     * operations[0x74] = "LD (HL),H";
     * operations[0x75] = "LD (HL),L";
     * operations[0x76] = "HALT";
     * operations[0x77] = "LD (HL),A";
     * operations[0x78] = "LD A,B";
     * operations[0x79] = "LD A,C";
     * operations[0x7a] = "LD A,D";
     * operations[0x7b] = "LD A,E";
     * operations[0x7c] = "LD A,H";
     * operations[0x7d] = "LD A,L";
     * operations[0x7e] = "LD A,(HL)";
     * operations[0x7f] = "LD A,A";
     * operations[0x80] = "ADD A,B";
     * operations[0x81] = "ADD A,C";
     * operations[0x82] = "ADD A,D";
     * operations[0x83] = "ADD A,E";
     * operations[0x84] = "ADD A,H";
     * operations[0x85] = "ADD A,L";
     * operations[0x86] = "ADD A,(HL)";
     * operations[0x87] = "ADD A,A";
     * operations[0x88] = "ADC A,B";
     * operations[0x89] = "ADC A,C";
     * operations[0x8a] = "ADC A,D";
     * operations[0x8b] = "ADC A,E";
     * operations[0x8c] = "ADC A,H";
     * operations[0x8d] = "ADC A,L";
     * operations[0x8e] = "ADC A,(HL)";
     * operations[0x8f] = "ADC A,A";
     * operations[0x90] = "SUB B";
     * operations[0x91] = "SUB C";
     * operations[0x92] = "SUB D";
     * operations[0x93] = "SUB E";
     * operations[0x94] = "SUB H";
     * operations[0x95] = "SUB L";
     * operations[0x96] = "SUB (HL)";
     * operations[0x97] = "SUB A";
     * operations[0x98] = "SBC B";
     * operations[0x99] = "SBC C";
     * operations[0x9a] = "SBC D";
     * operations[0x9b] = "SBC E";
     * operations[0x9c] = "SBC H";
     * operations[0x9d] = "SBC L";
     * operations[0x9e] = "SBC (HL)";
     * operations[0x9f] = "SBC A";
     * operations[0xa0] = "AND B";
     * operations[0xa1] = "AND C";
     * operations[0xa2] = "AND D";
     * operations[0xa3] = "AND E";
     * operations[0xa4] = "AND H";
     * operations[0xa5] = "AND L";
     * operations[0xa6] = "AND (HL)";
     * operations[0xa7] = "AND A";
     * operations[0xa8] = "XOR B";
     * operations[0xa9] = "XOR C";
     * operations[0xaa] = "XOR D";
     * operations[0xab] = "XOR E";
     * operations[0xac] = "XOR H";
     * operations[0xad] = "XOR L";
     * operations[0xae] = "XOR (HL)";
     * operations[0xaf] = "XOR A";
     * operations[0xb0] = "OR B";
     * operations[0xb1] = "OR C";
     * operations[0xb2] = "OR D";
     * operations[0xb3] = "OR E";
     * operations[0xb4] = "OR H";
     * operations[0xb5] = "OR L";
     * operations[0xb6] = "OR (HL)";
     * operations[0xb7] = "OR A";
     * operations[0xb8] = "CP B";
     * operations[0xb9] = "CP C";
     * operations[0xba] = "CP D";
     * operations[0xbb] = "CP E";
     * operations[0xbc] = "CP H";
     * operations[0xbd] = "CP L";
     * operations[0xbe] = "CP (HL)";
     * operations[0xbf] = "CP A";
     * operations[0xc0] = "RET NZ";
     * operations[0xc1] = "POP BC";
     * operations[0xc2] = "JP NZ,a16";
     * operations[0xc3] = "JP a16";
     * operations[0xc4] = "CALL NZ,a16";
     * operations[0xc5] = "PUSH BC";
     * operations[0xc6] = "ADD A,d8";
     * operations[0xc7] = "RST 00H";
     * operations[0xc8] = "RET Z";
     * operations[0xc9] = "RET";
     * operations[0xca] = "JP Z,a16";
     * operations[0xcb] = "new CB()?????";
     * operations[0xcc] = "CALL Z,a16";
     * operations[0xcd] = "CALL a16";
     * operations[0xce] = "ADC A,d8";
     * operations[0xcf] = "RST 08H";
     * operations[0xd0] = "RET NC";
     * operations[0xd1] = "POP DE";
     * operations[0xd2] = "JP NC,a16";
     * operations[0xd3] = "XXX";
     * operations[0xd4] = "CALL NC,a16";
     * operations[0xd5] = "PUSH DE";
     * operations[0xd6] = "SUB d8";
     * operations[0xd7] = "RST 10H";
     * operations[0xd8] = "RET C(cond)";
     * operations[0xd9] = "RETI";
     * operations[0xda] = "JP C(cond),a16";
     * operations[0xdb] = "XXX";
     * operations[0xdc] = "CALL C(cond),a16";
     * operations[0xdd] = "XXX";
     * operations[0xde] = "SBC d8";
     * operations[0xdf] = "RST 18H";
     * operations[0xe0] = "LD (a8),A";
     * operations[0xe1] = "POP HL";
     * operations[0xe2] = "LD (C),A";
     * operations[0xe3] = "XXX";
     * operations[0xe4] = "XXX";
     * operations[0xe5] = "PUSH HL";
     * operations[0xe6] = "AND d8";
     * operations[0xe7] = "RST 20H";
     * operations[0xe8] = "ADD SP,r8";
     * operations[0xe9] = "JP HL";
     * operations[0xea] = "LD (a16),A";
     * operations[0xeb] = "XXX";
     * operations[0xec] = "XXX";
     * operations[0xed] = "XXX";
     * operations[0xee] = "XOR d8";
     * operations[0xef] = "RST 28H";
     * operations[0xf0] = "LD A,(a8)";
     * operations[0xf1] = "POP AF";
     * operations[0xf2] = "LD A,(C)";
     * operations[0xf3] = "DI";
     * operations[0xf4] = "XXX";
     * operations[0xf5] = "PUSH AF";
     * operations[0xf6] = "OR d8";
     * operations[0xf7] = "RST 30H";
     * operations[0xf8] = "LD HL,SP+r8";
     * operations[0xf9] = "LD SP,HL";
     * operations[0xfa] = "LD A,(a16)";
     * operations[0xfb] = "EI";
     * operations[0xfc] = "XXX";
     * operations[0xfd] = "XXX";
     * operations[0xfe] = "CP d8";
     * operations[0xff] = "RST 38H";
     * int i = 0;
     * // System.out.println("print OPcodes");
     * 
     * while (i < romData.length) {
     * // fetch opcode
     * // make a 16-bit int out of the data
     * int opcode = (romData[i] & 0xff) | ((romData[i + 1] & 0xff) << 8);
     * // get the opcode description from the operations array
     * String opcodeDescription = operations[opcode & 0xff];
     * System.out.println(String.format("%04X: %04X %s", i, opcode,
     * opcodeDescription));
     * // increment by two because we are reading pairs of bytes
     * i += 2;
     * }
     * }
     */
    private class KeyHandler extends KeyAdapter {
        public void keyPressed(KeyEvent ke) {
            if (ke.getKeyCode() == KeyEvent.VK_ENTER)
                cpu.step();
        }
    }

    // old
    public static void main(String[] args) {
        new ReadGBFC();
    }
}

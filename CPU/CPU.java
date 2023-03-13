package CPU;

import Memory.Memory;
import gameboy.*;
import GPU.*;
import javax.swing.*;

import java.io.*;

//class to collect regs,ram,rom,opcodes and have execute cycle
public class CPU {
    Opcodes operations;
    byte[] romData;
    Registers regs;
    InterruptManager interruptManager;
    Memory mem;
    Timer timer;
    private boolean halted;
    private boolean interrupted = false;
    int interruptType;
    ReadGBFC parent;
    boolean running = false;
    PrintWriter out;
    int ticks = 1;
    PPU ppu;

    public CPU(byte[] romData, Registers regs,
            InterruptManager interruptManager, Memory mem, ReadGBFC parent) {
        this.romData = romData;
        this.regs = regs;
        this.mem = mem;
        this.interruptManager = interruptManager;
        mem.setCPU(this, interruptManager);
        operations = new Opcodes(regs, romData, interruptManager, mem, this);
        regs.setPC(0x00);// sets it to 0x100 in ROM to start testing opcode
        interruptManager.setCPU(this);
        this.parent = parent;
        timer = new Timer(this, mem);
        // hardSetRegs();
        mem.writeByte(0xff44, 0x90);// hardset for bootROM for now
        // mem.writeByte();
        try {
            out = new PrintWriter(new File("output.txt"));
        } catch (FileNotFoundException fne) {
        }
    }

    public void hardSetRegs() {
        regs.setA(1);
        regs.setBC(0x0013);
        regs.setDE(0x00d8);
        regs.setHL(0x014d);
        regs.setSP(0xfffe);
        regs.fByte.setZ(true);
        regs.fByte.setN(false);
        regs.fByte.setH(true);
        regs.fByte.setC(true);
        mem.writeByte(0xff44, 0x90);
    }

    public CPU getCPU() {
        return this;
    }

    public void setPPU(PPU ppu) {
        this.ppu = ppu;
    }

    public void step() {// takes 1 (fetch/decode/execute)cycle in execution
        if (halted) {
            timer.handleTimer(4);
            serviceInterrupts();
            // do stuff;
            ticks = 0;
            // System.out.println("halted...");
            return;// end step, service interrupts should turn halt to false
        }
        int currentPC = regs.getPC();
        int opcode = mem.readByte(currentPC);//
        Runnable operation = operations.opcodeHandlers.get(opcode & 0xff);
        // print
        String s = String.format(
                "A:%1$02X F:%2$02X B:%3$02X C:%4$02X D:%5$02X E:%6$02X H:%7$02X L:%8$02X SP:%9$04X PC:%10$04X PCMEM:%11$02X,%12$02X,%13$02X,%14$02X",
                regs.getA(), regs.fByte.getFByte(),
                regs.getB(), regs.getC(), regs.getD(), regs.getE(), regs.getH(), regs.getL(), regs.getSP(),
                regs.getPC(),
                mem.readByte(currentPC), mem.readByte(currentPC + 1), mem.readByte(currentPC + 2),
                mem.readByte(currentPC + 3));
        out.println(s);
        // System.out.println(Integer.toHexString(mem.readByte(0x80a0)));
        // System.out.println(regs.getPC());
        operation.run();
        timer.handleTimer(ticks);
        ppu.updateModeAndCycles();
        serviceInterrupts();
        ticks = 0;

        if (mem.readByte(0xff02) == 0x81) {// prints blarrg test results

            char c = (char) mem.readByte(0xff01);

            System.out.printf("%c", c);

            mem.writeByte(0xff02, 0x0);

        }
        parent.refreshPanel();
    }

    public void setCycle(int i) {
        this.ticks = i;
    }

    public void runUntil(int pc) {
        while (regs.getPC() != pc) {
            step();
            // if ((mem.readByte(0x8020)) == 0xC3)//checking VRAM
            // break;
        }
        out.close();
    }

    public void setRun() {
        if (running == false)
            running = true;
        else
            running = false;
    }

    public void setHalt(boolean state) {
        this.halted = state;
    }

    public void interrupt(int interruptType) {
        this.interrupted = true;
        this.interruptType = interruptType;// tells cpu which int to travel to in mem
    }

    public void serviceInterrupts() {
        // set interrupt and halt to false
        if (interrupted) {
            // System.out.println("pc: " + regs.getPC());
            interrupted = false;
            this.halted = false;
            if (interruptType > 0) {// if interrupted but IME is false, just rusumes exe without jump
                operations.PUSH(regs.getPC());// jump back position
                regs.setPC(interruptType);// travel to int
                interruptManager.setInterruptsEnabled(false);
            }
        }
    }
}

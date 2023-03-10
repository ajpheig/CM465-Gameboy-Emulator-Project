package CPU;
import GPU.PPU;
import Memory.Memory;
import gameboy.*;

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
    PPU ppu;
    boolean running = false;
    PrintWriter out;
    int ticks = 1;

    public CPU(byte[] romData, Registers regs,
            InterruptManager interruptManager, Memory mem, ReadGBFC parent, PPU ppu) {
        this.romData = romData;
        this.regs = regs;
        this.mem = mem;
        this.ppu = ppu;
        this.interruptManager = interruptManager;
        mem.setCPU(this, interruptManager);
        operations = new Opcodes(regs, romData, interruptManager, mem, this);
        regs.setPC(0x100);// sets it to 0x100 in ROM to start testing opcode
        interruptManager.setCPU(this);
        this.parent = parent;
        timer = new Timer(this, mem);
        hardSetRegs();
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
        // System.out.println(mem.readByte(0x8190));
        // System.out.println(regs.getPC());
        // call ppu method before each opcode is ran to keep it going through the correct modes
        ppu.updateModeAndCycles();
        operation.run();
        timer.handleTimer(ticks);
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
            if ((char) (mem.readByte(0xff01)) == 'f')
                break;
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

    /*
     * public void handleTimer(int ticks) {
     * divCount += (ticks / 4);// inc DIV register
     * if (divCount >= 256) {
     * divCount -= 256;
     * mem.writeByte(0xff04, mem.readByte(0xff04) + 1);
     * }
     * // is timer enabled?
     * if (((mem.readByte(0xff07) >> 2) & 0x1) == 1) {// grab the 2nd bit
     * timerCounter += ticks;
     * int freq = 4096;// base freq Hz
     * if ((mem.readByte(0xff07) & 0b11) == 1) {
     * freq = 262144;// 0x01 frequency
     * } else if ((mem.readByte(0xff07) & 0b11) == 2)
     * freq = 65536;// 0x10 frequency
     * else if ((mem.readByte(0xff07) & 0b11) == 3)
     * freq = 16834;// 0x11 frequency
     * while (timerCounter >= (4194304 / freq)) {// while so that it can be ++ twice
     * during one cycle
     * // increase TIMA register
     * // System.out.printf("| %d |", mem.readByte(0xff05));
     * mem.writeByte(0xff05, mem.readByte(0xff05) + 1);
     * // did TIMA overflow? ==0?
     * if (mem.readByte(0xff05) == 0) {
     * mem.writeByte(0xff0f, mem.readByte(0xff0f) | 0b100);// set Timer interrupt in
     * // IF
     * // System.out.println("TIMA overflow, set int");
     * mem.writeByte(0xff05, mem.readByte(0xff06));// reset TIMA to value in TMA
     * }
     * timerCounter = 0;// -= (4194304 / freq);
     * }
     * }
     * }
     */
}

package CPU;

import Memory.Memory;
import gameboy.*;
import GPU.*;
import javax.swing.*;
import javax.swing.plaf.synth.SynthTextAreaUI;

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
    int totalT=0;
    PPU ppu;

    public CPU(byte[] romData, Registers regs,
            InterruptManager interruptManager, Memory mem, ReadGBFC parent) {
        this.romData = romData;
        this.regs = regs;
        this.mem = mem;
        this.interruptManager = interruptManager;
        timer = new Timer(this, mem);
        mem.setCPU(this, interruptManager,timer);
        operations = new Opcodes(regs, romData, interruptManager, mem, this);
        regs.setPC(0x00);// sets it to 0x100 in ROM to start testing opcode
        interruptManager.setCPU(this);
        this.parent = parent;
        // hardSetRegs();
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
        mem.writeByte(0xff00,0xff);
    }

    public CPU getCPU() {
        return this;
    }

    public void setPPU(PPU ppu) {
        this.ppu = ppu;
    }

    public void step() {// takes 1 (fetch/decode/execute)cycle in execution
        ticks = 0;
        if (halted) {
            this.ticks=4;
            timer.handleTimer(ticks);
            {  ppu.updateModeAndCycles();}
            serviceInterrupts();
            // System.out.println("halted...");
            return;// end step, service interrupts should turn halt to false
        }
        int currentPC = regs.getPC();
        int opcode = mem.readByte(currentPC);//
        Runnable operation = operations.opcodeHandlers.get(opcode & 0xff);
        // print
        String s = String.format(
                "A:%1$02X F:%2$02X B:%3$02X C:%4$02X D:%5$02X E:%6$02X H:%7$02X L:%8$02X SP:%9$04X PC:%10$04X PCMEM:%11$02X,%12$02X,%13$02X,%14$02X LY:%15$02X",
                regs.getA(), regs.fByte.getFByte(),
                regs.getB(), regs.getC(), regs.getD(), regs.getE(), regs.getH(), regs.getL(), regs.getSP(),
                regs.getPC(),
                mem.readByte(currentPC), mem.readByte(currentPC + 1), mem.readByte(currentPC + 2),
                mem.readByte(currentPC + 3),mem.readByte(0xff44));
        //out.println(s);
        // System.out.println(Integer.toHexString(mem.readByte(0x80a0)));
        //System.out.println(s);
        operation.run();
        timer.handleTimer(this.ticks);
        for(int o=0;o<ticks;o++){
               ppu.updateModeAndCycles();}
        serviceInterrupts();
        //parent.refreshPanel();
    }

    public void setCycle(int i) {
        this.ticks = i;
    }

    public void runUntil(int pc) {
        while (regs.getPC() != pc) {
            long timeStart= System.currentTimeMillis();
            step();
            totalT+=this.ticks;
            if (totalT>=17476){
                totalT=0;
                //while(System.currentTimeMillis()-timeStart<.07) {
                    //wait loop
                //}
            }
            //if(regs.getPC()>0xcb00)ppu.printRAM();
        }
        //out.close();
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

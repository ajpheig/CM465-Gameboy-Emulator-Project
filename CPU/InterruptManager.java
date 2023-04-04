package CPU;

import java.util.HashMap;

public class InterruptManager {
    private CPU cpu;
    boolean imeFlag = false; // Interrupt Master Enable
    public static final int VBLANK = 0X40;
    public static final int LCDSTAT = 0X48;
    public static final int TIMER = 0X50;
    public static final int SERIAL = 0X58;
    public static final int JOYPAD = 0X60;

    public HashMap<Integer, Boolean> interruptTable = new HashMap<>();
    { // Hashmap to hold the interrupts and their states
        interruptTable.put(VBLANK, false);
        interruptTable.put(LCDSTAT, false);
        interruptTable.put(TIMER, false);
        interruptTable.put(SERIAL, false);
        interruptTable.put(JOYPAD, false);
    }

    public void setCPU(CPU cpu) {
        this.cpu = cpu;
    }

    public void setInterruptsEnabled(boolean ime) {
        this.imeFlag = ime; // set the master enable flag
    }

    public void setInterrupt(int interruptType, boolean state) {
        interruptTable.put(interruptType, state);// set interrupt in table
    }

    public boolean postInterrupt(int interruptType) {
        if (!imeFlag) {
            cpu.interrupt(-1);// break out of HALT with this
            return false; // If interrupt master is disabled return false
        }
        if (!interruptTable.getOrDefault(interruptType, false)) {
            return false;// if interrupt is disabled, return false
        }
        if(interruptType==JOYPAD)System.out.println("joypad int posted");
        // Must be a interrupt enabled, give cpu interruptType to do something
        // System.out.println("flagged in IE");
        cpu.interrupt(interruptType);
        return true;
    }

    public boolean intFlagHandler(int ifFlag)// takes in the byte from 0xFF0F in mem
    { // checks to see if any ifFlage is enabled, if so, then postInterrupt that calls
      // CPU method
        if ((ifFlag & 1) == 1) {
            // System.out.println("VBLANK interrupt");
            return postInterrupt(VBLANK);// if VBLANK bit is 1 return
        }
        ifFlag >>= 1;// shift bits to the right to get the next interrupt in the 0 slot
        if ((ifFlag & 1) == 1) {
            //System.out.println("LCDSTAT interrupt");
            return postInterrupt(LCDSTAT);
        }
        ifFlag >>= 1;// rinse repeat for other bit slots
        if ((ifFlag & 1) == 1) {
            // System.out.println("TIMER interrupt");
            return postInterrupt(TIMER);
        }
        ifFlag >>= 1;
        if ((ifFlag & 1) == 1) {
            System.out.println("SERIAL interrupt");
            return postInterrupt(SERIAL);
        }
        ifFlag >>= 1;
        if ((ifFlag & 1) == 1) {
            System.out.println("JOYPAD interrupt");
            return postInterrupt(JOYPAD);
        }
        return false;// nothing is set return false
    }

    public void intEnableHandler(int ieFlag)// takes byte from 0xFFFF in memory
    { // this will relay the flags to the interruptTable
        setInterrupt(VBLANK, (ieFlag & 1) == 1);
        ieFlag >>= 1;
        setInterrupt(LCDSTAT, (ieFlag & 1) == 1);
        ieFlag >>= 1;
        setInterrupt(TIMER, (ieFlag & 1) == 1);
        ieFlag >>= 1;
        setInterrupt(SERIAL, (ieFlag & 1) == 1);
        ieFlag >>= 1;
        setInterrupt(JOYPAD, (ieFlag & 1) == 1);
    }
    public InterruptManager getIntMan() {
        return this;
    }

}

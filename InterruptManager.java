import java.util.HashMap;

public class InterruptManager {
    boolean imeFlag = false; // Interrupt Master Enable
    public static final int VBLANK = 0X40;
    public static final int LCDSTAT = 0X48;
    public static final int TIMER = 0X50;
    public static final int SERIAL = 0X58;
    public static final int JOYPAD = 0X60;

    public HashMap<Integer, Boolean> interruptTable = new HashMap<>();
    { // Hashmap to hold the interrupts
        interruptTable.put(VBLANK, false);
        interruptTable.put(LCDSTAT, false);
        interruptTable.put(TIMER, false);
        interruptTable.put(SERIAL, false);
        interruptTable.put(JOYPAD, false);
    }

    public void setInterruptsEnabled(boolean ime) {
        this.imeFlag = ime; // set the master enable flag
    }

    public void setInterrupt(int interruptType, boolean option) {
        interruptTable.put(interruptType, option);// set interrupt in table
    }

    public boolean postInterrupt(int interruptType) {
        if (!imeFlag)
            return false; // If interrupt master is disabled return false
        if (!interruptTable.getOrDefault(interruptType, false))
            return false;// if interrupt is disabled, return false
        // Must be a interrupt enabled, give cpu interruptType to do something
        return true;
    }

    public boolean intFlagHandler(int ifFlag)// takes int the byte from FF0F
    { // checks to see if any ifFlage is enabled, if so, then postInterrupt that calls
      // CPU method
        if ((ifFlag & 1) == 1)
            return postInterrupt(VBLANK);// if VBLANK bit is 1 return
        ifFlag >>= 1;// shift bits to the right to get the next interrupt in the 0 slot
        if ((ifFlag & 1) == 1)
            return postInterrupt(LCDSTAT);
        ifFlag >>= 1;// rinse repeat for other bit slots
        if ((ifFlag & 1) == 1)
            return postInterrupt(TIMER);
        ifFlag >>= 1;
        if ((ifFlag & 1) == 1)
            return postInterrupt(SERIAL);
        ifFlag >>= 1;
        if ((ifFlag & 1) == 1)
            return postInterrupt(JOYPAD);
        return false;// nothing is set return false
    }

    public void intEnableHandler(int ieFlag)// takes byte from FFFF in memory
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
}

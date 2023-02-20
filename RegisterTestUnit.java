import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

public class RegisterTestUnit {

    Registers regs = new Registers();
    InterruptManager intMan = new InterruptManager();

    @Test
    public void setA() {
        regs.setA(0x30);

        assertEquals(0x30, regs.getA());
    }

    @Test
    public void setB() {
        regs.setB(0x30);

        assertEquals(0x30, regs.getB());
    }

    @Test
    public void setFlags() {
        regs.fByte.setZ(true);
        regs.fByte.setN(true);
        regs.fByte.setH(true);
        regs.fByte.setC(false);
        // 1110 0000 = 0xE0
        assertEquals(0xE0, regs.fByte.getFByte());
    }

    @Test
    public void testFByte() {
        regs.fByte.setFByte(0xE0);
        int fb = regs.fByte.getFByte();
        assertEquals(0xE0, fb);
    }

    @Test
    public void setC() {
        regs.setC(0x30);

        assertEquals(0x30, regs.getC());
    }

    @Test
    public void setD() {
        regs.setD(0x30);

        assertEquals(0x30, regs.getD());
    }

    @Test
    public void setE() {
        regs.setE(0x30);

        assertEquals(0x30, regs.getE());
    }

    @Test
    public void setH() {
        regs.setH(0x30);

        assertEquals(0x30, regs.getH());
    }

    @Test
    public void setL() {
        regs.setL(0x30);

        assertEquals(0x30, regs.getL());
    }

    @Test
    public void setSP() {
        regs.setSP(0x3030);

        assertEquals(0x3030, regs.getSP());
    }

    @Test
    public void setPC() {
        regs.setPC(0xAAAA);

        assertEquals(0xAAAA, regs.getPC());
    }

    // Interrupts Testing
    public static final int VBLANK = 0X40;
    public static final int LCDSTAT = 0X48;
    public static final int TIMER = 0X50;
    public static final int SERIAL = 0X58;
    public static final int JOYPAD = 0X60;

    @Test
    public void setVBLANK() {
        intMan.setInterruptsEnabled(true);
        intMan.setInterrupt(VBLANK, true);
        assertEquals(true, intMan.postInterrupt(VBLANK));
    }

    @Test
    public void setLCDSTAT() {
        intMan.setInterruptsEnabled(true);
        intMan.setInterrupt(LCDSTAT, true);
        assertEquals(true, intMan.postInterrupt(LCDSTAT));
    }

    @Test
    public void setSERIAL() {
        intMan.setInterruptsEnabled(true);
        intMan.setInterrupt(SERIAL, true);
        assertEquals(true, intMan.postInterrupt(SERIAL));
    }

    @Test
    public void setJOYPAD() {
        intMan.setInterruptsEnabled(true);
        intMan.setInterrupt(JOYPAD, true);
        assertEquals(true, intMan.postInterrupt(JOYPAD));
    }

}

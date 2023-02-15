import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class RegisterTestUnit {

    Registers regs = new Registers();

    @Test
    public void setA() {
        regs.setA(0x30);

        assertEquals(0x30, regs.getA());
    }

    public void setB() {
        regs.setB(0x30);

        assertEquals(0x30, regs.getB());
    }

    public void setFlags() {
        Flags flags = regs.getF();
        flags.setZ(true);
        flags.setN(true);
        flags.setH(true);
        flags.setC(false);
        // 1110 0000 = 0xE0
        assertEquals(0xE0, flags.getFByte());
    }

    public void setC() {
        regs.setC(0x30);

        assertEquals(0x30, regs.getC());
    }

    public void setD() {
        regs.setD(0x30);

        assertEquals(0x30, regs.getD());
    }

    public void setE() {
        regs.setE(0x30);

        assertEquals(0x30, regs.getE());
    }

    public void setH() {
        regs.setH(0x30);

        assertEquals(0x30, regs.getH());
    }

    public void setL() {
        regs.setL(0x30);

        assertEquals(0x30, regs.getL());
    }

    public void setSP() {
        regs.setSP(0x3030);

        assertEquals(0x3030, regs.getSP());
    }

    public void setPC() {
        regs.setPC(0xAAAA);

        assertEquals(0xAAAA, regs.getPC());
    }

}

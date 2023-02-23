import org.junit.Test;
import static org.junit.Assert.*;

public class OpcodeTestUnit {
    Registers regs = new Registers();
    byte[] romData = new byte[100];
    InterruptManager intMan = new InterruptManager();
    Memory mem;// non functional
    CPU cpu;
    Opcodes operations = new Opcodes(regs, romData, intMan, mem, cpu);

    @Test
    public void LD_AB() {// from one reg to another
        regs.setB(0x55);
        Runnable operation = operations.opcodeHandlers.get(0x78 & 0xff);// opcode 0x78 LD a b
        operation.run();
        assertEquals(0x55, regs.getA());// actually grabs the value from b but not all reg to reg ld have been added
    }

    @Test
    public void LD_B_C() {// from c to b
        regs.setC(0x55);
        Runnable operation = operations.opcodeHandlers.get(0x41 & 0xff);// opcode 0x78 LD a b
        operation.run();
        assertEquals(0x55, regs.getB());// actually grabs the value from b but not all reg to reg ld have been added
    }

    @Test
    public void testRRCA() { // right rotate bits in a
        // Test with A = 0b10011010
        regs.setA(0b10011010);
        Runnable operation = operations.opcodeHandlers.get(0xf); // opcode RRCA()
        operation.run();
        assertEquals(0b01001101, regs.getA());
        assertFalse(regs.fByte.checkC());
    }

    @Test
    public void testSCF() {
        regs.fByte.setC(false);
        regs.fByte.setC(true);
        assertTrue(regs.fByte.checkC());

        regs.fByte.setC(true);
        assertTrue(regs.fByte.checkC());
    }

    @Test
    public void testDA() {
        // Test with carry flag and no half-carry flag
        regs.setA(0x9C);
        regs.fByte.setN(false);
        regs.fByte.setH(false);
        regs.fByte.setC(true);
        Runnable operation = operations.opcodeHandlers.get(0x27);
        operation.run();
        assertEquals(0x02, regs.getA()); // 0x9C -> 0x02 with carry flag set

        // Test with carry flag and half-carry flag
        regs.setA(0x3E);
        regs.fByte.setN(false);
        regs.fByte.setH(true);
        regs.fByte.setC(true);
        Runnable operation1 = operations.opcodeHandlers.get(0x27);
        operation1.run();
        assertEquals(0xA4, regs.getA()); // 3E -> 44 with carry flag set and
        // half-carry flag set

        // Test with no carry flag and no half-carry flag
        regs.setA(0xA5);
        regs.fByte.setN(false);
        regs.fByte.setH(false);
        regs.fByte.setC(false);
        operation.run();
        assertEquals(0x05, regs.getA()); // A5 -> 05 with no flags set

        // Test with no carry flag and half-carry flag
        regs.setA(0x25);
        regs.fByte.setN(false);
        regs.fByte.setH(true);
        regs.fByte.setC(false);
        operation.run();
        assertEquals(0x2B, regs.getA()); // 25 -> 30 with half-carry flag set
    }

    @Test
    public void testRR() {
        // Test when carry flag is set
        regs.setA(0b10010000); // 0x90
        regs.fByte.setC(true);
        Runnable operation = operations.opcodeHandlers.get(0x1f);
        operation.run();
        assertEquals(0b11001000, regs.getA()); // 0xC8
        assertEquals(false, regs.fByte.checkC()); // carry flag should be reset to 0

        regs.setA(0b10010001); // 0x91
        regs.fByte.setC(false);
        Runnable operation1 = operations.opcodeHandlers.get(0x1f);
        operation1.run();
        assertEquals(0b01001000, regs.getA()); // 0x48
        assertEquals(true, regs.fByte.checkC()); // carry flag should be set to 1
    }

    public static final int SERIAL = 0X58;

    @Test
    public void setEI() {// broken
        intMan.setInterruptsEnabled(true);
        intMan.setInterrupt(SERIAL, true);
        Runnable operation = operations.opcodeHandlers.get(0xfb & 0xff);// opcode 0xfb enable interrupts
        operation.run();
        assertEquals(true, intMan.postInterrupt(SERIAL));
    }

    @Test
    public void setDI() {
        intMan.setInterruptsEnabled(true);
        intMan.setInterrupt(SERIAL, true);
        Runnable operation = operations.opcodeHandlers.get(0xf3 & 0xff);// opcode 0xf3 disable interrupts
        operation.run();
        assertEquals(false, intMan.postInterrupt(SERIAL));// returns false because IME flag is false from DI
    }
}
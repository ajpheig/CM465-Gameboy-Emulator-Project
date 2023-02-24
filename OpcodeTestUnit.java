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
    public void testRLCA() {
        // Test with A = 0b10011010
        regs.setA(0b10011010);
        Runnable operation = operations.opcodeHandlers.get(0x7);
        operation.run();
        assertEquals(0b00110101, regs.getA());
        assertTrue(regs.fByte.checkC());

        // Test with A = 0b00000001
        regs.setA(0b00000001);
        operation.run();
        assertEquals(0b00000010, regs.getA());
        assertFalse(regs.fByte.checkC());

        // Test with A = 0b11111111
        regs.setA(0b11111111);
        operation.run();
        assertEquals(0b11111110, regs.getA());
        assertTrue(regs.fByte.checkC());

        // Test with A = 0b00000000
        regs.setA(0b00000000);
        operation.run();
        assertEquals(0b00000000, regs.getA());
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
    public void testDAA() {
        // Test with carry flag and no half-carry flag
        regs.setA(0x9C);
        regs.fByte.setN(false);
        regs.fByte.setH(false);
        regs.fByte.setC(true);
        Runnable operation = operations.opcodeHandlers.get(0x27);
        operation.run();
        assertEquals(0xA2, regs.getA()); // 0x9C -> 0xA2 with carry flag set

        // Test with carry flag and half-carry flag
        regs.setA(0x3E);
        regs.fByte.setN(false);
        regs.fByte.setH(true);
        regs.fByte.setC(true);
        Runnable operation1 = operations.opcodeHandlers.get(0x27);
        operation1.run();
        assertEquals(0x44, regs.getA()); // 3E -> 44 with carry flag set and half-carry flag set

        // Test with no carry flag and no half-carry flag
        regs.setA(0xA5);
        regs.fByte.setN(false);
        regs.fByte.setH(false);
        regs.fByte.setC(false);
        Runnable operation2 = operations.opcodeHandlers.get(0x27);
        operation2.run();
        assertEquals(0x05, regs.getA()); // A5 -> 05 with no flags set

        // Test with no carry flag and half-carry flag
        regs.setA(0x25);
        regs.fByte.setN(false);
        regs.fByte.setH(true);
        regs.fByte.setC(false);
        Runnable operation3 = operations.opcodeHandlers.get(0x27);
        operation3.run();
        assertEquals(0x2B, regs.getA()); // 25 -> 2B with half-carry flag set
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
    public void testRLA() {
        // Test with carry flag not set
        regs.setA(0b00101010); // A = 0x2A
        regs.fByte.setC(false);
        Runnable operation = operations.opcodeHandlers.get(0x17); // opcode RLA()
        operation.run();
        assertEquals(0b01010100, regs.getA()); // A should be 0x54 after the rotate
        assertFalse(regs.fByte.checkC()); // carry flag should be reset

        // Test with carry flag set
        regs.setA(0b10000001); // A = 0x81
        regs.fByte.setC(true);
        Runnable operation2 = operations.opcodeHandlers.get(0x17); // opcode RLA()
        operation2.run();
        assertEquals(0b00000011, regs.getA()); // A should be 0x03 after the rotate
        assertTrue(regs.fByte.checkC()); // carry flag should be set
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

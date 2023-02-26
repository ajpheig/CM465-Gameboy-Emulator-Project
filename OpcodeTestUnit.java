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
    public void testSRA() {
        // Test SRA on register A
        regs.setA(0b11010101);
        Runnable operation = operations.opcodeHandlers.get(0x2f);
        operation.run();
        assertEquals(0b11101010, regs.getA());
        assertTrue(regs.fByte.checkC());
        assertFalse(regs.fByte.checkN());
        assertFalse(regs.fByte.checkH());
        assertFalse(regs.fByte.checkZ());
    }

    @Test
    public void testSWAPRegister() {
        // Test swapping a register value

        regs.setA(0xAB);
        Runnable operation = operations.opcodeHandlers.get(0x37);
        operation.run();
        assertEquals(0xBA, regs.getA());
        assertFalse(regs.fByte.checkZ());
        assertFalse(regs.fByte.checkC());
        assertFalse(regs.fByte.checkH());
        assertFalse(regs.fByte.checkN());
    }
    @Test
    public void LD_B_C() {// from c to b
        regs.setC(0x55);
        Runnable operation = operations.opcodeHandlers.get(0x41 & 0xff);// opcode 0x78 LD a b
        operation.run();
        assertEquals(0x55, regs.getB());// actually grabs the value from b but not all reg to reg ld have been added
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
    public void testRLC() {
        // Test RLC on register B with initial value 0x80

        cpu.regs.setRegisterValue("b", 0x80);
        Runnable operation = operations.extendedOpcodeHandlers.get(0x00);
        operation.run();
        assertEquals(0x01, cpu.regs.getRegisterValue("b"));
        assertTrue(regs.fByte.checkC());
        assertFalse(regs.fByte.checkN());
        assertFalse(regs.fByte.checkH());
        assertTrue(regs.fByte.checkC());

        // Test RLC on register A with initial value 0x00
        cpu.regs.setRegisterValue("c", 0x00);
        operation = operations.extendedOpcodeHandlers.get(0x01);
        operation.run();
        assertEquals(0x00, cpu.regs.getRegisterValue("a"));
        assertTrue(regs.fByte.checkC());
        assertFalse(regs.fByte.checkN());
        assertFalse(regs.fByte.checkH());
        assertFalse(regs.fByte.checkC());
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
    }
    @Test
    public void testRLWithCarry() {
        cpu.regs.setRegisterValue("b", 0x80); // binary 10000000
        regs.fByte.setC(true);
        Runnable operation = operations.extendedOpcodeHandlers.get(0x10);
        operation.run();
        assertEquals(0x01, cpu.regs.getRegisterValue("b")); // binary 00000001
        assertFalse(regs.fByte.checkZ());
        assertFalse(regs.fByte.checkN());
        assertFalse(regs.fByte.checkH());
        assertTrue(regs.fByte.checkC());
    }

    @Test
    public void testSRL() {
        // Test case 1: SRL with a register value of 0x80 should set the zero and carry flags, and clear the half carry and negative flags.
        cpu.regs.setRegisterValue("b", 0x80);
        Runnable operation = operations.extendedOpcodeHandlers.get(0x38);
        operation.run();

        assertTrue(cpu.regs.fByte.checkZ());
        assertTrue(cpu.regs.fByte.checkC());
        assertFalse(cpu.regs.fByte.checkH());
        assertFalse(cpu.regs.fByte.checkN());
        assertEquals(0x40, cpu.regs.getRegisterValue("b"));
    }

    @Test
    public void testBIT0B() {


        // Load the value 0x01 into register B
        regs.setB(0x01);
        // Execute the "BIT 0,B" instruction
        Runnable operation = operations.extendedOpcodeHandlers.get(0x40);
        operation.run();

        // Check the flags
        assertTrue(regs.fByte.checkZ()); // Expect Z flag to be set since bit 0 is not set
        assertFalse(regs.fByte.checkN()); // Expect N flag to be cleared
        assertTrue(regs.fByte.checkH()); // Expect H flag to be set
    }

    @Test
    public void testRLWithoutCarry() {
        cpu.regs.setRegisterValue("c", 0x7F); // binary 01111111
        regs.fByte.setC(false);
        Runnable operation = operations.extendedOpcodeHandlers.get(0x11);
        operation.run();
        assertEquals(0xFE, cpu.regs.getRegisterValue("c")); // binary 11111110
        assertFalse(regs.fByte.checkZ());
        assertFalse(regs.fByte.checkN());
        assertFalse(regs.fByte.checkH());
        assertFalse(regs.fByte.checkC());
    }

    @Test
    public void testSLARegister() {
        // Initialize CPU and register B with value 0x81

        regs.setB(0x81);

        // Perform SLA on register B
       Runnable operation = operations.extendedOpcodeHandlers.get(0x20);
        operation.run();
        // Assert that register B has been shifted left and zero bit has been set
        assertEquals(0x02, cpu.regs.getB());
        assertTrue(cpu.regs.fByte.checkZ());
        assertFalse(cpu.regs.fByte.checkN());
        assertFalse(cpu.regs.fByte.checkH());
        assertTrue(cpu.regs.fByte.checkC());


    }

    @Test
    public void testRRB() {

        regs.setB(0x80); // binary 10000000
        cpu.regs.fByte.setC(true);
        Runnable operation = operations.extendedOpcodeHandlers.get(0x18);
        operation.run();
        assertEquals(0xC0, cpu.regs.getB()); // binary 11000000
        assertTrue(cpu.regs.fByte.checkC());
        assertFalse(cpu.regs.fByte.checkZ());
        assertFalse(cpu.regs.fByte.checkN());
        assertFalse(cpu.regs.fByte.checkH());
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

public class OpcodeTestUnit {
    Registers regs = new Registers();
    byte[] romData = new byte[100];
    InterruptManager intMan = new InterruptManager();
    Opcodes operations = new Opcodes(regs, romData, intMan);

    @Test
    public void LD_A() {
        regs.setA(0x32);
        Runnable operation = operations.opcodeHandlers.get(0x3E & 0xff);// opcode 0x3E LD a u8
        operation.run();
        assertEquals(0x45, regs.getA());// hardcoded to load 0x45 into A just for test
    }

    @Test
    public void LD_AB() {// from one reg to another
        regs.setB(0x55);
        Runnable operation = operations.opcodeHandlers.get(0x78 & 0xff);// opcode 0x78 LD a b
        operation.run();
        assertEquals(0x55, regs.getA());// actually grabs the value from b but not all reg to reg ld have been added
    }

    public static final int SERIAL = 0X58;

    @Test
    public void setEI() {
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
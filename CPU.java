import Memory.Memory;

//class to collect regs,ram,rom,opcodes and have execute cycle
public class CPU {
    Opcodes operations;
    byte[] romData;
    Registers regs;
    InterruptManager interruptManager;
    Memory mem;
    private boolean halted;
    private boolean interrupted = false;
    int interruptType;

    public CPU(byte[] romData, Registers regs,
            InterruptManager interruptManager, Memory mem) {
        this.romData = romData;
        this.regs = regs;
        this.mem = mem;
        this.interruptManager = interruptManager;
        operations = new Opcodes(regs, romData, interruptManager, mem, this);
        regs.setPC(0x100);// sets it to 0x100 in ROM to start testing opcode
        interruptManager.setCPU(this);
    }


    public void step() {// takes 1 (fetch/decode/execute)cycle in execution
        if (halted) {
            serviceInterrupts();
            // do stuff
            return;// end step, service interrupts should turn halt to false
        }
        int currentPC = regs.getPC();
        int opcode = mem.readByte(currentPC);// would be memory, placehold as romData rn
        // print
        System.out.print(Integer.toHexString((opcode)));
        Runnable operation = operations.opcodeHandlers.get(opcode & 0xff);
        operation.run();
        System.out.print(" pc: " + Integer.toHexString(regs.getPC()) + " ");

        if (mem.readByte(0xff02) == 0x81) {

            String c = Integer.toString(mem.readByte(0xff01));

            System.out.printf("%s", c);

            mem.writeByte(0xff02, 0x0);

        }
    }

    public void interrupt(int interruptType) {
        this.interrupted = true;
        this.interruptType = interruptType;
    }

    public void serviceInterrupts() {
        // set interrupt and halt to false
        if (interrupted) {
            interrupted = false;
            halted = false;
            operations.PUSH(regs.getPC());
            regs.setPC(interruptType);
            interruptManager.setInterruptsEnabled(false);
        }
    }
}

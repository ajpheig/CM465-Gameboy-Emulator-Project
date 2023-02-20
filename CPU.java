public class CPU {
    Opcodes operations;
    byte[] romData;
    Registers regs;
    static final int NOJUMP = -1;
    // CPU cpu;

    public CPU(byte[] romData, Registers regs, InterruptManager interruptManager) {
        this.romData = romData;
        this.regs = regs;
        operations = new Opcodes(regs, romData, operations.interruptmanager);
        regs.setPC(0x100);// sets it to 100 to start testing opcode

    }

    public void step() {// takes 1 (fetch/decode/execute)cycle in execution
        int currentPC = regs.getPC();
        int opcode = romData[currentPC];
        // print
        System.out.print(Integer.toHexString(opcode & 0xff));
        Runnable operation = operations.opcodeHandlers.get(opcode & 0xff);
        operation.run();
        System.out.print(" pc: " + Integer.toHexString(regs.getPC()) + " ");
        // int result = operation.execute(this);
    }
}

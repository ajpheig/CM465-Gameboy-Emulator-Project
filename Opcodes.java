import java.util.HashMap;
import java.util.Map;

public class Opcodes {
    Registers regs;
    byte[] romData;
    InterruptManager interruptmanager;

    // pass in Regs class and romData to operate on. Will need to give RAM(memory)
    // object when created
    public Opcodes(Registers regs, byte[] romData,
            InterruptManager interruptmanager) {
        this.regs = regs;
        this.romData = romData;
        this.interruptmanager = interruptmanager;// to set interrupt flags
    }

    public void executeOpcodes(byte[] romData) {
        // maybe dont need this, idk, i just wanted the map to be a part of the class
        // and not a method
    }

    public Map<Integer, Runnable> opcodeHandlers = new HashMap<>();

    // this is a map where the opcodes are key and an opcode will call the
    // corresponding runnable object
    // like this Runnable handler = opcodeHandlers.get(0x1);
    // which can be run with handler.run
    // one of two ways to call the method
    {
        opcodeHandlers.put(0x0, this::nop);
        // opcodeHandlers.put(0x1, () -> ldBCd16);
        /*
         * opcodeHandlers.put(0x2, () -> LD_(BC),A);
         * opcodeHandlers.put(0x3, () -> INC_BC);
         * opcodeHandlers.put(0x4, () -> INC_B);
         * opcodeHandlers.put(0x5, () -> DEC_B);
         * opcodeHandlers.put(0x6, () -> LD_B,d8);
         * // rotate bits right reg A
         * opcodeHandlers.put(0x7, () -> RLCA);
         * opcodeHandlers.put(0x8, () -> LD_(a16),SP);
         * opcodeHandlers.put(0x9, () -> ADD_HL,BC);
         * opcodeHandlers.put(0xa, () -> LD_A,(BC));
         * opcodeHandlers.put(0xb, () -> DEC_BC);
         * opcodeHandlers.put(0xc, () -> INC_C);
         * opcodeHandlers.put(0xd, () -> DEC_C);
         * opcodeHandlers.put(0xe, () -> LD_C,d8);
         * opcodeHandlers.put(0xf, () -> RRCA);
         * opcodeHandlers.put(0x10, () -> STOP);
         * opcodeHandlers.put(0x11, () -> LD_DE,d16);
         * opcodeHandlers.put(0x12, () -> LD_(DE),A);
         * opcodeHandlers.put(0x13, () -> INC_DE);
         * opcodeHandlers.put(0x14, () -> INC_D);
         * opcodeHandlers.put(0x15, () -> DEC_D);
         * opcodeHandlers.put(0x16, () -> LD_D,d8);
         * // shift bits left reg A
         * opcodeHandlers.put(0x17, () -> RLA);
         * // jump to an offset by adding r8 to the current program counter to determing
         * the address to jump to
         * opcodeHandlers.put(0x18, () -> JR_r8);
         * // add DE to HL and store result in HL
         * opcodeHandlers.put(0x19, () -> ADD_HL,DE);
         * opcodeHandlers.put(0x1a, () -> LD_A,(DE));
         * opcodeHandlers.put(0x1b, () -> DEC_DE);
         * opcodeHandlers.put(0x1c, () -> INC_E);
         * opcodeHandlers.put(0x1d, () -> DEC_E);
         * opcodeHandlers.put(0x1e, () -> LD_E,d8);
         * // right shift reg A
         * opcodeHandlers.put(0x1f, () -> RRA);
         * // jump to new address if teh zero flag is not set
         * opcodeHandlers.put(0x20, () -> JR_NZ,r8);
         * opcodeHandlers.put(0x21, () -> LD_HL,d16);
         * // increment HL after loading A into memory
         * opcodeHandlers.put(0x22, () -> LD_(HL+),A);
         * opcodeHandlers.put(0x23, () -> INC_HL);
         * opcodeHandlers.put(0x24, () -> INC_H);
         * opcodeHandlers.put(0x25, () -> DEC_H);
         * opcodeHandlers.put(0x26, () -> LD_H,d8);
         * // Decimal adjust accumulator. Adjusts a to the correct representation of a
         * binary-coded decimal using the flags
         * opcodeHandlers.put(0x27, () -> DAA);
         * // jump to new address if zero flag is set
         * opcodeHandlers.put(0x28, () -> JR_Z,r8);
         * opcodeHandlers.put(0x29, () -> ADD_HL,HL);
         * // load HL into A then increment it
         * opcodeHandlers.put(0x2a, () -> LD_A,(HL+));
         * opcodeHandlers.put(0x2b, () -> DEC_HL);
         * opcodeHandlers.put(0x2c, () -> INC_L);
         * opcodeHandlers.put(0x2d, () -> DEC_L);
         * opcodeHandlers.put(0x2e, () -> LD_L,d8);
         * opcodeHandlers.put(0x2f, () -> CPL);
         * opcodeHandlers.put(0x30, () -> JR_NC,r8);
         * opcodeHandlers.put(0x31, () -> LD_SP,d16);
         * // load A into HL then DEC HL
         * opcodeHandlers.put(0x32, () -> LD_(HL-),A);
         * opcodeHandlers.put(0x33, () -> INC_SP);
         * opcodeHandlers.put(0x34, () -> INC_(HL));
         * opcodeHandlers.put(0x35, () -> DEC_(HL));
         * opcodeHandlers.put(0x36, () -> LD_(HL),d8);
         * // set carry flag to 1
         * opcodeHandlers.put(0x37, () -> SCF);
         * // perform relative jump if if the carry flag is set
         * opcodeHandlers.put(0x38, () -> JR_C,r8);
         * opcodeHandlers.put(0x39, () -> ADD_HL,SP);
         * opcodeHandlers.put(0x3a, () -> LD_A,(HL-));
         * opcodeHandlers.put(0x3b, () -> DEC_SP);
         * opcodeHandlers.put(0x3c, () -> INC_A);
         * opcodeHandlers.put(0x3d, () -> DEC_A);
         */ opcodeHandlers.put(0x3e, () -> LD("a", 0x45));
        // hardcoding just for test. Need to retrieve nect byte of data at mem(pc+1)
        // complement the carry flag
        /*
         * opcodeHandlers.put(0x3f, () -> CCF);
         * opcodeHandlers.put(0x40, () -> LD_B,B);
         * opcodeHandlers.put(0x41, () -> LDBC);
         *
         * opcodeHandlers.put(0x42, () -> LD_B,D);
         * opcodeHandlers.put(0x43, () -> LD_B,E);
         * opcodeHandlers.put(0x44, () -> LD_B,H);
         * opcodeHandlers.put(0x45, () -> LD_B,L);
         * opcodeHandlers.put(0x46, () -> LD_B,(HL));
         * opcodeHandlers.put(0x47, () -> LD_B,A);
         * opcodeHandlers.put(0x48, () -> LD_C,B);
         * opcodeHandlers.put(0x49, () -> LD_C,C);
         * opcodeHandlers.put(0x4a, () -> LD_C,D);
         * opcodeHandlers.put(0x4b, () -> LD_C,E);
         * opcodeHandlers.put(0x4c, () -> LD_C,H);
         * opcodeHandlers.put(0x4d, () -> LD_C,L);
         * opcodeHandlers.put(0x4e, () -> LD_C,(HL));
         * opcodeHandlers.put(0x4f, () -> LD_C,A);
         * opcodeHandlers.put(0x50, () -> LD_D,B);
         * opcodeHandlers.put(0x51, () -> LD_D,C);
         * opcodeHandlers.put(0x52, () -> LD_D,D);
         * opcodeHandlers.put(0x53, () -> LD_D,E);
         * opcodeHandlers.put(0x54, () -> LD_D,H);
         * opcodeHandlers.put(0x55, () -> LD_D,L);
         * opcodeHandlers.put(0x56, () -> LD_D,(HL));
         * opcodeHandlers.put(0x57, () -> LD_D,A);
         * opcodeHandlers.put(0x58, () -> LD_E,B);
         * opcodeHandlers.put(0x59, () -> LD_E,C);
         * opcodeHandlers.put(0x5a, () -> LD_E,D);
         * opcodeHandlers.put(0x5b, () -> LD_E,E);
         * opcodeHandlers.put(0x5c, () -> LD_E,H);
         * opcodeHandlers.put(0x5d, () -> LD_E,L);
         * opcodeHandlers.put(0x5e, () -> LD_E,(HL));
         * opcodeHandlers.put(0x5f, () -> LD_E,A);
         * opcodeHandlers.put(0x60, () -> LD_H,B);
         * opcodeHandlers.put(0x61, () -> LD_H,C);
         * opcodeHandlers.put(0x62, () -> LD_H,D);
         * opcodeHandlers.put(0x63, () -> LD_H,E);
         * opcodeHandlers.put(0x64, () -> LD_H,H);
         * opcodeHandlers.put(0x65, () -> LD_H,L);
         * opcodeHandlers.put(0x66, () -> LD_H,(HL));
         * opcodeHandlers.put(0x67, () -> LD_H,A);
         * opcodeHandlers.put(0x68, () -> LD_L,B);
         * opcodeHandlers.put(0x69, () -> LD_L,C);
         * opcodeHandlers.put(0x6a, () -> LD_L,D);
         * opcodeHandlers.put(0x6b, () -> LD_L,E);
         * opcodeHandlers.put(0x6c, () -> LD_L,H);
         * opcodeHandlers.put(0x6d, () -> LD_L,L);
         * opcodeHandlers.put(0x6e, () -> LD_L,(HL));
         * opcodeHandlers.put(0x6f, () -> LD_L,A);
         * opcodeHandlers.put(0x70, () -> LD_(HL),B);
         * opcodeHandlers.put(0x71, () -> LD_(HL),C);
         * opcodeHandlers.put(0x72, () -> LD_(HL),D);
         * opcodeHandlers.put(0x73, () -> LD_(HL),E);
         * opcodeHandlers.put(0x74, () -> LD_(HL),H);
         * opcodeHandlers.put(0x75, () -> LD_(HL),L);
         * opcodeHandlers.put(0x76, () -> HALT);
         * opcodeHandlers.put(0x77, () -> LD_(HL),A);
         */ opcodeHandlers.put(0x78, () -> LD("a", "b"));
        /*
         * opcodeHandlers.put(0x79, () -> LD_A,C);
         * opcodeHandlers.put(0x7a, () -> LD_A,D);
         * opcodeHandlers.put(0x7b, () -> LD_A,E);
         * opcodeHandlers.put(0x7c, () -> LD_A,H);
         * opcodeHandlers.put(0x7d, () -> LD_A,L);
         * opcodeHandlers.put(0x7e, () -> LD_A,(HL));
         * opcodeHandlers.put(0x7f, () -> LD_A,A);
         * opcodeHandlers.put(0x80, () -> ADD_A,B);
         * opcodeHandlers.put(0x81, () -> ADD_A,C);
         * opcodeHandlers.put(0x82, () -> ADD_A,D);
         * opcodeHandlers.put(0x83, () -> ADD_A,E);
         * opcodeHandlers.put(0x84, () -> ADD_A,H);
         * opcodeHandlers.put(0x85, () -> ADD_A,L);
         * opcodeHandlers.put(0x86, () -> ADD_A,(HL));
         * opcodeHandlers.put(0x87, () -> ADD_A,A);
         * opcodeHandlers.put(0x88, () -> ADC_A,B);
         * opcodeHandlers.put(0x89, () -> ADC_A,C);
         * opcodeHandlers.put(0x8a, () -> ADC_A,D);
         * opcodeHandlers.put(0x8b, () -> ADC_A,E);
         * // add contents of H into A and adds the value of the carry flag
         * opcodeHandlers.put(0x8c, () -> ADC_A,H);
         * opcodeHandlers.put(0x8d, () -> ADC_A,L);
         * opcodeHandlers.put(0x8e, () -> ADC_A,(HL));
         * opcodeHandlers.put(0x8f, () -> ADC_A,A);
         * // subtract teh contents of B from contents of A and updates the carry and
         * zero flags and half carry flag
         * // and puts the value in A
         * opcodeHandlers.put(0x90, () -> SUB_B);
         * opcodeHandlers.put(0x91, () -> SUB_C);
         * opcodeHandlers.put(0x92, () -> SUB_D);
         * opcodeHandlers.put(0x93, () -> SUB_E);
         * opcodeHandlers.put(0x94, () -> SUB_H);
         * opcodeHandlers.put(0x95, () -> SUB_L);
         * opcodeHandlers.put(0x96, () -> SUB_(HL));
         * opcodeHandlers.put(0x97, () -> SUB_A);
         * // subtracts contents of B and the carry flag from A stores it in A and
         * updates the carry, zero, and half carry
         * // flag
         * opcodeHandlers.put(0x98, () -> SBC_B);
         * opcodeHandlers.put(0x99, () -> SBC_C);
         * opcodeHandlers.put(0x9a, () -> SBC_D);
         * opcodeHandlers.put(0x9b, () -> SBC_E);
         * opcodeHandlers.put(0x9c, () -> SBC_H);
         * opcodeHandlers.put(0x9d, () -> SBC_L);
         * opcodeHandlers.put(0x9e, () -> SBC_(HL));
         * opcodeHandlers.put(0x9f, () -> SBC_A);
         * // AND A and B
         * opcodeHandlers.put(0xa0, () -> AND_B);
         * opcodeHandlers.put(0xa1, () -> AND_C);
         * opcodeHandlers.put(0xa2, () -> AND_D);
         * opcodeHandlers.put(0xa3, () -> AND_E);
         * opcodeHandlers.put(0xa4, () -> AND_H);
         * opcodeHandlers.put(0xa5, () -> AND_L);
         * opcodeHandlers.put(0xa6, () -> AND_(HL));
         * opcodeHandlers.put(0xa7, () -> AND_A);
         * // XOR B with A
         * opcodeHandlers.put(0xa8, () -> XOR_B);
         * opcodeHandlers.put(0xa9, () -> XOR_C);
         * opcodeHandlers.put(0xaa, () -> XOR_D);
         * opcodeHandlers.put(0xab, () -> XOR_E);
         * opcodeHandlers.put(0xac, () -> XOR_H);
         * opcodeHandlers.put(0xad, () -> XOR_L);
         * opcodeHandlers.put(0xae, () -> XOR_(HL));
         * opcodeHandlers.put(0xaf, () -> XOR_A);
         * // or with A
         * opcodeHandlers.put(0xb0, () -> OR_B);
         * opcodeHandlers.put(0xb1, () -> OR_C);
         * opcodeHandlers.put(0xb2, () -> OR_D);
         * opcodeHandlers.put(0xb3, () -> OR_E);
         * opcodeHandlers.put(0xb4, () -> OR_H);
         * opcodeHandlers.put(0xb5, () -> OR_L);
         * opcodeHandlers.put(0xb6, () -> OR_(HL));
         * opcodeHandlers.put(0xb7, () -> OR_A);
         * // compare A and B without modifying either. zero flag is set to 1 if if
         * result is 0. Carry flag is set to 1 if
         * // A is smaller than B abd 0 otherwise. Half carry flag is set to 1 if a
         * carry occured from the lower nibble
         * //Subtract flag is set to 1
         * opcodeHandlers.put(0xb8, () -> CP_B);
         * opcodeHandlers.put(0xb9, () -> CP_C);
         * opcodeHandlers.put(0xba, () -> CP_D);
         * opcodeHandlers.put(0xbb, () -> CP_E);
         * opcodeHandlers.put(0xbc, () -> CP_H);
         * opcodeHandlers.put(0xbd, () -> CP_L);
         * opcodeHandlers.put(0xbe, () -> CP_(HL));
         * opcodeHandlers.put(0xbf, () -> CP_A);
         * // return to the address at the top of the stack if 0 flag is not set
         * opcodeHandlers.put(0xc0, () -> RET_NZ);
         * // pop two bytes from the top of the stack and store them in BC
         * opcodeHandlers.put(0xc1, () -> POP_BC);
         * // jump if zero flag is not set
         * opcodeHandlers.put(0xc2, () -> JP_NZ,a16);
         */ opcodeHandlers.put(0xc3, () -> JP(romData[regs.getPC() + 1] | (romData[regs.getPC() + 2] << 8)));
        //
        // test
        /*
         * / call a subroutine if zero flag is not set
         * opcodeHandlers.put(0xc4, () -> CALL_NZ,a16);
         * // push contents of BC onto the stack
         * opcodeHandlers.put(0xc5, () -> PUSH_BC);
         * opcodeHandlers.put(0xc6, () -> ADD_A,d8);
         * // software reset. Moves the program counter to the start of the program by
         * loading it with 0x00
         * opcodeHandlers.put(0xc7, () -> RST_00H);
         * // return from a subroutine by popping two bytes from the stack and loading
         * them into the program counter. Only
         * // executes if the zero flag is set
         * opcodeHandlers.put(0xc8, () -> RET_Z);
         * // returns from subroutine no matter what
         * opcodeHandlers.put(0xc9, () -> RET);
         * opcodeHandlers.put(0xca, () -> JP_Z,a16);
         * // initiates a different set of opcodes that allows for more complex
         * operations
         * opcodeHandlers.put(0xcb, () -> new_CB());
         * // call subroutine if zero flag is set
         * opcodeHandlers.put(0xcc, () -> CALL_Z,a16);
         * // call subroutine
         * opcodeHandlers.put(0xcd, () -> CALL_a16);
         * // ADD with carry on register A
         * opcodeHandlers.put(0xce, () -> ADC_A,d8);
         * // call a specific routine in memory located as address 0x08 that pushes the
         * address of teh next instruction
         * // onto the stack and transfers program control to the specified routine
         * opcodeHandlers.put(0xcf, () -> RST_08H);
         * // return from a subroutine if carry flag is not set
         * opcodeHandlers.put(0xd0, () -> RET_NC);
         * opcodeHandlers.put(0xd1, () -> POP_DE);
         * // Jump if carry flag is not set
         * opcodeHandlers.put(0xd2, () -> JP_NC,a16);
         * // not a valid opcode
         * opcodeHandlers.put(0xd3, () -> XXX);
         * // call a subroutine if the carry flag is not set
         * opcodeHandlers.put(0xd4, () -> CALL_NC,a16);
         * opcodeHandlers.put(0xd5, () -> PUSH_DE);
         * // subtract d8 from A
         * opcodeHandlers.put(0xd6, () -> SUB_d8);
         * // call routine located at 0x10
         * opcodeHandlers.put(0xd7, () -> RST_10H);
         * // returns control of the program after a call instruction if carry flag is
         * set
         * opcodeHandlers.put(0xd8, () -> RET_C);
         * // return from interrupt
         * opcodeHandlers.put(0xd9, () -> RETI);
         * // jump if carry flag is set
         * opcodeHandlers.put(0xda, () -> JP_C,a16);
         * opcodeHandlers.put(0xdb, () -> XXX);
         * // call subroutine at a16 is carry flag is set
         * opcodeHandlers.put(0xdc, () -> CALL_C,a16);
         * opcodeHandlers.put(0xdd, () -> XXX);
         * // subtraction with carry then stored in register A. Zeroe, carry,
         * half-carry, and negative flags are updated
         * opcodeHandlers.put(0xde, () -> SBC_d8);
         * // call subroutine at 0x18
         * opcodeHandlers.put(0xdf, () -> RST_18H);
         */
        // load contents of the memory address pointed to by a8 into the high0bytes of
        // memory address $FF00 + a8 and
        // stores it into A
        /*
         * opcodeHandlers.put(0xe0, () -> LDH_(a8),A);
         * opcodeHandlers.put(0xe1, () -> POP_HL);
         * opcodeHandlers.put(0xe2, () -> LD_(C),A);
         * opcodeHandlers.put(0xe3, () -> XXX);
         * opcodeHandlers.put(0xe4, () -> XXX);
         * opcodeHandlers.put(0xe5, () -> PUSH_HL);
         * // and d8 with A
         * opcodeHandlers.put(0xe6, () -> AND_d8);
         * // resets the program counter to address 0x0020
         * opcodeHandlers.put(0xe7, () -> RST_20H);
         * opcodeHandlers.put(0xe8, () -> ADD_SP,r8);
         * // jumps to address stored in HL
         * opcodeHandlers.put(0xe9, () -> JP_HL);
         * opcodeHandlers.put(0xea, () -> LD_(a16),A);
         * opcodeHandlers.put(0xeb, () -> XXX);
         * opcodeHandlers.put(0xec, () -> XXX);
         * opcodeHandlers.put(0xed, () -> XXX);
         * // XOR d8 with A
         * opcodeHandlers.put(0xee, () -> XOR_d8);
         * // call to 0x28 saving the program counter to the stack so teh execution can
         * resume after the routine is complete
         * opcodeHandlers.put(0xef, () -> RST_28H);
         * opcodeHandlers.put(0xf0, () -> LD_A,(a8));
         * opcodeHandlers.put(0xf1, () -> POP_AF);
         * opcodeHandlers.put(0xf2, () -> LD_A,(C));
         * // disables interrupts
         */ opcodeHandlers.put(0xf3, () -> DI());
        /*
         * opcodeHandlers.put(0xf4, () -> XXX);
         * opcodeHandlers.put(0xf5, () -> PUSH_AF);
         * // OR A and d8. I think it is inclusive
         * opcodeHandlers.put(0xf6, () -> OR_d8);
         * // call to 0x30
         * opcodeHandlers.put(0xf7, () -> RST_30H);
         * opcodeHandlers.put(0xf8, () -> LD_HL,SP+r8);
         * opcodeHandlers.put(0xf9, () -> LD_SP,HL);
         * opcodeHandlers.put(0xfa, () -> LD_A,(a16));
         * // enables interrupts
         */ opcodeHandlers.put(0xfb, () -> EI());
        /*
         * opcodeHandlers.put(0xfc, () -> XXX);
         * opcodeHandlers.put(0xfd, () -> XXX);
         * opcodeHandlers.put(0xfe, () -> CP_d8);
         * // call to 0x28
         * opcodeHandlers.put(0xff, () -> RST_38H);
         */
    }

    /*
     * int i = 0;
     * // System.out.println("print OPcodes");
     * 
     * 
     * while (i < romData.length) {
     * // fetch opcode
     * int opcode = (romData[i] & 0xff) | ((romData[i + 1] & 0xff) << 8);
     * //
     * Runnable handler = opcodeHandlers.get(opcode & 0xff);
     * if (handler != null) {
     * handler.run();
     * }
     * // increment by two because we are reading pairs of bytes
     * i += 2;
     * }
     */

    public void nop() {
        System.out.println("nop");
        regs.setPC(regs.getPC() + 1);
    }

    // load value into intoRegister
    public void LD(String register, int value) {
        switch (register) {
            case "a":
                regs.setA(value);
                break;
            case "b":
                regs.setB(value);
                break;
            case "c":
                regs.setC(value);
                break;
            case "d":
                regs.setD(value);
                break;
            case "e":
                regs.setE(value);
                break;
            case "h":
                regs.setH(value);
                break;
            case "l":
                regs.setL(value);
                break;
            case "sp":
                regs.setSP(value);
                break;
            case "pc":
                regs.setPC(value);
                break;
            // case "ad": regs.setAD(value);
            // break;
            case "bc":
                regs.setBC(value);
                break;
            case "af":
                regs.setAF(value);
                break;
            case "de":
                regs.setDE(value);
                break;
            case "hl":
                regs.setHL(value);
                break;
        }
    }

    // loag the value of valueRegister into register need to do something like a
    // nested switch or better
    public void LD(String register, String valueRegister) {
        switch (register) {
            case "a":
                switch (valueRegister) {
                    case "b":
                        regs.setA(regs.getB());
                        break;
                }
                break;
            case "b":
                regs.setB(0);
                break;
            case "c":
                regs.setC(0);
                break;
            case "d":
                regs.setD(0);
                break;
            case "e":
                regs.setE(0);
                break;
            case "h":
                regs.setH(0);
                break;
            case "l":
                regs.setL(0);
                break;
            case "sp":
                regs.setSP(0);
                break;
            case "pc":
                regs.setPC(0);
                break;
            // case "ad": regs.setAD(value);
            // break;
            case "bc":
                regs.setBC(0);
                break;
            case "af":
                regs.setAF(0);
                break;
            case "de":
                regs.setDE(0);
                break;
            case "hl":
                regs.setHL(0);
                break;
        }
    }

    // increment the register
    public void INC(String register) {
        switch (register) {
            case "a":
                regs.setA(regs.getA() + 1);
                break;
            case "b":
                regs.setB(regs.getB() + 1);
                break;
            case "c":
                regs.setC(regs.getC() + 1);
                break;
            case "d":
                regs.setD(regs.getD() + 1);
                break;
            case "e":
                regs.setE(regs.getE() + 1);
                break;
            case "h":
                regs.setH(regs.getH() + 1);
                break;
            case "l":
                regs.setL(regs.getL() + 1);
                break;
            case "sp":
                regs.setSP(regs.getSP() + 1);
                break;
            case "pc":
                regs.setPC(regs.getPC() + 1);
                break;
            // case "ad": regs.setAD(value);
            // break;
            case "bc":
                regs.setBC(regs.getBC() + 1);
                break;
            case "af":
                regs.setAF(regs.getAF() + 1);
                break;
            case "de":
                regs.setDE(regs.getDE() + 1);
                break;
            case "hl":
                regs.setHL(regs.getHL() + 1);
                break;

        }
    }

    // decrement register
    public void DEC(int register) {
        System.out.println("decrement register");
    }

    // right rotates bits in A
    public void RRCA() {
        System.out.println("RRCS");
    }

    // stop cpu
    public void STOP() {
        System.out.println("STOP");
    }

    // jump relative by the amount of the value passed in
    public void JR(int value) {
        System.out.println("jump relative" + value);
    }

    // jump to adress location
    public void JP(int value) {
        System.out.println("jump" + value);
        regs.setPC(value);
    }

    // add the value of addRegister into intoRegister and store it in intoRegister
    public void ADD(int intoRegister, int addRegister) {
        System.out.println("add");
    }

    // right shift register
    public void RR(int register) {
        System.out.println("right shit" + register);
    }

    // jump if zero flag is not set the amount that is passed in e.g. r8
    public void JRNZ(int value) {
        System.out.println("jump" + value);
    };

    // load loadRegister into incRegister then increment incRegister
    public void LDINCFIRST(int incRegister, int loadRegister) {
        System.out.println("load then inc register");
    }

    // decimal adjust register
    public void DA(int register) {
        System.out.println("decimal addjust " + register);
    }

    // jump if zero flag is set
    public void JRZ(int offset) {
        System.out.println("Jump not zero" + offset);
    }

    // load loadRegister into intoRegister then increment loadRegister
    public void LDINCSECOND(int intoRegister, int loadRegister) {
        System.out.println("Load " + loadRegister + " into " +
                intoRegister + "then increment " + loadRegister);
    }

    // load loadRegister into intoRegister then decrement into
    public void LDDECFIRST(int intoRegister, int loadRegister) {
        System.out.println("");
    }

    // set carry flag
    public void SFC() {
        System.out.println("");
    }

    // jump if the carry flag is set
    public void JRC() {
        System.out.println("");
    }

    // load loadRegister into intoRegister then decrement loadRegister
    public void LDDECSECOND(int intoRegister, int loadRegister) {
        System.out.println("");
    }

    // compliment carry flag
    public void CCF() {
        System.out.println("");
    }

    // halt cpu temporarily?
    public void HALT() {
        System.out.println("");
    }

    // add the second register to the first register with the carry flag and store
    // it in the first register
    public void ADC(int intoRegister, int loadRegister) {
        System.out.println("");
    }

    // subtracts contents of register passed in and puts result in A and sets the
    // carry, zero, and negative flags
    public int SUB(int register) {// for SUB A, u8
        System.out.println(" Subu8 ");
        int diff = regs.getA() - register;
        int result = diff & 0xff;
        if (result == 0)
            regs.fByte.setZ(true);// zero
        if (diff < 0)
            regs.fByte.setC(true);// carry
        regs.fByte.setN(true);// negative
        if ((regs.getA() & 0xf) - (register & 0xf) < 0)
            regs.fByte.setH(true);// half carry
        return result;
    }

    public int SUB(String register) {// for SUB A, register
        System.out.println(" SUB ");
        int value = 0;
        switch (register) {
            case "A":
                value = regs.getA();
            case "B":
                value = regs.getB();
            case "C":
                value = regs.getC();
            case "D":
                value = regs.getD();
            case "E":
                value = regs.getE();
            case "H":
                value = regs.getH();
            case "L":
                value = regs.getL();
            case "HL":
                value = regs.getHL();
        }
        int diff = regs.getA() - value;
        int result = diff & 0xff;
        if (result == 0)
            regs.fByte.setZ(true);// zero
        if (diff < 0)
            regs.fByte.setC(true);// carry
        regs.fByte.setN(true);// negative
        if ((regs.getA() & 0xf) - (value & 0xf) < 0)
            regs.fByte.setH(true);// half carry
        return result;
    }

    // AND A with register passed in
    public void AND(int A, int register) {
        System.out.println("");
    }

    // XOR A with register passed in
    public void XOR(int A, int register) {
        System.out.println(" XOR ");
        int result = regs.getA() ^ register;
        if (result == 0)
            regs.fByte.setZ(true);
        regs.setA(result);
    }

    // OR A with register passed in
    public void OR(int register) {
        System.out.println(" OR ");
        int result = regs.getA() | register;
        if (result == 0)
            regs.fByte.setZ(true);
        regs.setA(result);
    }

    // compare contents of a register with A, sets flags, but does not modify either
    public void CP(int register) {
        System.out.println("CP");
        int atemp = regs.getA();
        int compare = SUB(register);
        if (compare == 0)
            regs.fByte.setZ(true);
        regs.fByte.setN(true);
        if (compare < 0)
            regs.fByte.setC(true);
        regs.setA(atemp);
    }

    public void CP(String register) {
        System.out.println("CP");
        int atemp = regs.getA();
        int value = 0;
        switch (register) {
            case "A":
                value = regs.getA();
            case "B":
                value = regs.getB();
            case "C":
                value = regs.getC();
            case "D":
                value = regs.getD();
            case "E":
                value = regs.getE();
            case "H":
                value = regs.getH();
            case "L":
                value = regs.getL();
            case "HL":
                value = regs.getHL();
        }
        int compare = SUB(value);
        if (compare == 0)
            regs.fByte.setZ(true);
        regs.fByte.setN(true);
        if (compare < 0)
            regs.fByte.setC(true);
        regs.setA(atemp);
    }

    // pops value of top of stack and stores it into the register specified

    // needs memory access for stack pointer

    /*
     * public void POP(String register) {
     * System.out.println("");
     * int sp =regs.getSP();
     * switch(register){
     * case "AF":
     * {
     * regs.fByte(mem.read(sp)&~0xf); sp++;//lower F nibble stays 0
     * regs.setA(mem.read(sp)); sp++;
     * //maybe need to set flags, dont know how though
     * }
     * case "BC":
     * { regs.setC(mem.read(sp)); sp++;
     * regs.setB(mem.read(sp)); sp++;
     * regs.setSP(sp);
     * }
     * case "DE":
     * { regs.setE(mem.read(sp)); sp++;
     * regs.setD(mem.read(sp)); sp++;
     * regs.setSP(sp);
     * }
     * case "HL":
     * { regs.setL(mem.read(sp)); sp++;
     * regs.setH(mem.read(sp)); sp++;
     * regs.setSP(sp);
     * }
     * }
     * }
     */

    // push contents of a register onto the stack, need memory access
    public void PUSH(int register) {
        System.out.println("");
        int stackp = regs.getSP();
        stackp--;
        // mem.write(register>>8);//sets upper
        stackp--;
        // mem.write(register&
        regs.setSP(stackp);
    }

    // software reset. Moves the program counter to the start of the program by
    // loading it with 0x00
    public void RST() {
        System.out.println("");
    }

    // return from a subroutine by popping two bytes from the stack and loading them
    // into the program counter. Only
    // executes if the zero flag is set
    public void RETZ() {
    }

    // returns from subroutine
    public void RET() {
    }

    // call subroutine if zero flag is set
    public void CALLZ(int location) {
    }

    // calls subroutine
    public void CALL(int location) {
    }

    // call a specific routine in memory located as address passed in e.g. 0x08 that
    // pushes the address of the next instruction
    // onto the stack and transfers program control to the specified routine
    public void RST(int address) {
        PUSH(regs.getPC() + 1);

        JP(address);
    }

    // return to the address at the top of the stack if 0 flag is not set
    // or return control to the calling routine if zero flag is not set. or are
    // these the same thing?
    public void RETNZ() {
    }

    // return from a subroutine if carry flag is not set
    public void RETNC() {
    }

    // return from a subroutine if carry flag is set
    public void RETC() {
    }

    // jump to address passed in if carry flag is not set
    public void JPNC(int address) {
    }

    // call a subroutine if zero flag is not set
    public void CALLNZ() {
    }

    /// call a subroutine if the carry flag is not set
    public void CALLNC() {
    }

    // call subroutine if carry flag is set
    public void CALLC() {
    }

    // subtract d8 from A
    public void SUBD8(int A, int D8) {
    }

    // return from interrupt
    public void RETI() {
    }

    // subtracts contents of the register and the carry flag from A stores it in A
    // and updates the carry, zero, and half carry
    // flag
    public void SBC(int A, int register) {
    }

    // disable interupts
    public void DI() {
        interruptmanager.setInterruptsEnabled(false);
    }

    // endable interupts
    public void EI() {
        interruptmanager.setInterruptsEnabled(true);
    }

}
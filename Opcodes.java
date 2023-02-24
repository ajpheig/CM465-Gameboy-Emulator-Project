import Memory.Memory;

import java.util.HashMap;
import java.util.Map;

public class Opcodes {
    Registers regs;
    byte[] romData;
    InterruptManager interruptmanager;
    CPU cpu;
    Memory mem;// Can cahnge just place holding with small features just to make Opcode
    boolean halted;

    // pass in Regs class and romData to operate on. Will need to give RAM(memory)
    // object when created
    public Opcodes(Registers regs, byte[] romData,
            InterruptManager interruptmanager, Memory mem, CPU cpu) {
        this.regs = regs;
        this.romData = romData;
        this.mem = mem;
        this.cpu = cpu;
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
        opcodeHandlers.put(0x1, () -> LDu("bc", mem.readWord(regs.getPC() + 1), 3));
        opcodeHandlers.put(0x2, () -> LD(regs.getBC(), "a", 1));
        opcodeHandlers.put(0x3, () -> INC("bc"));
        opcodeHandlers.put(0x4, () -> INC("b"));
        opcodeHandlers.put(0x5, () -> DEC("b"));
        opcodeHandlers.put(0x6, () -> LDu("b", mem.readByte(regs.getPC() + 1), 2));
        opcodeHandlers.put(0x7, () -> RLCA()); //RLCA);
        opcodeHandlers.put(0x8, () -> LD(mem.readWord(regs.getPC() + 1), "sp", 3));
        opcodeHandlers.put(0x9, () -> ADD("hl", regs.getBC())); //ADD_HL,BC);
        opcodeHandlers.put(0xa, () -> LD("a", mem.readByte(regs.getBC())));
        opcodeHandlers.put(0xb, () -> DEC("bc"));
        opcodeHandlers.put(0xc, () -> INC("c"));
        opcodeHandlers.put(0xd, () -> DEC("C"));
        opcodeHandlers.put(0xe, () -> LDu("c", mem.readByte(regs.getPC() + 1), 2));
        opcodeHandlers.put(0xf, () -> RRCA());
        opcodeHandlers.put(0x10, () -> STOP());
        opcodeHandlers.put(0x11, () -> LDu("de", mem.readWord(regs.getPC() + 1), 3));
        opcodeHandlers.put(0x12, () -> LD(regs.getDE(), "a", 1));// (DE), A);
        opcodeHandlers.put(0x13, () -> INC("de"));
        opcodeHandlers.put(0x14, () -> INC("d"));
        opcodeHandlers.put(0x15, () -> DEC("d"));
        opcodeHandlers.put(0x16, () -> LDu("d", mem.readByte(regs.getPC() + 1), 2));
        opcodeHandlers.put(0x17, () -> ()());
        opcodeHandlers.put(0x18, () -> JR(mem.readWord(regs.getPC() + 1)));
        opcodeHandlers.put(0x19, () -> ADD("hl", regs.getDE())); //ADD_HL,DE);
        opcodeHandlers.put(0x1a, () -> LD("a", mem.readByte(regs.getDE())));// A,(DE));
        opcodeHandlers.put(0x1b, () -> DEC("de"));
        opcodeHandlers.put(0x1c, () -> INC("e"));
        opcodeHandlers.put(0x1d, () -> DEC("e"));
        opcodeHandlers.put(0x1e, () -> LDu("e", mem.readByte(regs.getPC() + 1), 2)); //LD_E,d8
        opcodeHandlers.put(0x1f, () -> RR());
        opcodeHandlers.put(0x20, () -> JRNZ(mem.readByte(regs.getPC() + 1))); // r8);
        opcodeHandlers.put(0x21, () -> LDu("hl", mem.readWord(regs.getPC() + 1), 3));
        opcodeHandlers.put(0x22, () -> LDINCDECHL("LD_(HL+),A)"));
        opcodeHandlers.put(0x23, () -> INC("hl"));
        opcodeHandlers.put(0x24, () -> INC("h"));
        opcodeHandlers.put(0x25, () -> DEC("h"));
        opcodeHandlers.put(0x26, () -> LDu("h", mem.readByte(regs.getPC() + 1), 2)); //LD_H,d8);
        opcodeHandlers.put(0x27, () -> DAA());
        opcodeHandlers.put(0x28, () -> JRZ(mem.readByte(regs.getPC() + 1)));   // JR_Z,r8
        opcodeHandlers.put(0x29, () -> ADD("hl", regs.getHL())); // ADD_HL,HL);
        opcodeHandlers.put(0x2a, () -> LDINCDECHL("LD_A,(HL+)")); // LD_A,(HL+)
        opcodeHandlers.put(0x2b, () -> DEC("hl")); // decrement value of hl
        opcodeHandlers.put(0x2c, () -> INC("l"));
        opcodeHandlers.put(0x2d, () -> DEC("l"));
        opcodeHandlers.put(0x2e, () -> LDu("l", mem.readByte(regs.getPC() + 1), 2));
        opcodeHandlers.put(0x2f, () -> CPL());
        opcodeHandlers.put(0x30, () -> JRNC(mem.readByte(regs.getPC() + 1))); // JR_NC,r8)
        opcodeHandlers.put(0x31, () -> LD("sp", mem.readWord(regs.getPC() + 1)));// SP,d16);
        opcodeHandlers.put(0x32, () -> LDINCDECHL("LD_(HL-),A)"));
        opcodeHandlers.put(0x33, () -> INC("sp"));
        opcodeHandlers.put(0x34, () -> INC("(hl)")); // (hl) means the memory value at hl
        opcodeHandlers.put(0x35, () -> DEC("(hl)"));
        opcodeHandlers.put(0x36, () -> LD(regs.getHL(), mem.readByte(regs.getPC() + 1), 2));// _(HL),d8);
        opcodeHandlers.put(0x37, () -> SCF());
        opcodeHandlers.put(0x38, () -> JRC( mem.readByte(regs.getPC() +1 ))); // JR_C,r8
        opcodeHandlers.put(0x39, () -> ADD("hl", regs.getSP())); //ADD_HL,SP
        opcodeHandlers.put(0x3a, () -> LDINCDECHL("LD_A,(HL-)"));
        opcodeHandlers.put(0x3b, () -> DEC("sp"));
        opcodeHandlers.put(0x3c, () -> INC("a"));
        opcodeHandlers.put(0x3d, () -> DEC("a"));
        opcodeHandlers.put(0x3e, () -> LDu("a", mem.readByte(regs.getPC() + 1), 2));
        opcodeHandlers.put(0x3f, () -> CCF());
        opcodeHandlers.put(0x40, () -> LD("b", regs.getB()));
        opcodeHandlers.put(0x41, () -> LD("b", regs.getC()));
        opcodeHandlers.put(0x42, () -> LD("b", regs.getD()));// LD_B,D);
        opcodeHandlers.put(0x43, () -> LD("b", regs.getE()));// LD_B,E);
        opcodeHandlers.put(0x44, () -> LD("b", regs.getH()));// LD_B,H);
        opcodeHandlers.put(0x45, () -> LD("b", regs.getL()));// LD_B,L);
        opcodeHandlers.put(0x46, () -> LD("b", mem.readByte(regs.getPC() + 1)));// LD_B,(HL));
        opcodeHandlers.put(0x47, () -> LD("b", regs.getA()));
        opcodeHandlers.put(0x48, () -> LD("c", regs.getB()));// LD_C,B);
        opcodeHandlers.put(0x49, () -> LD("c", regs.getC()));
        opcodeHandlers.put(0x4a, () -> LD("c", regs.getD()));
        opcodeHandlers.put(0x4b, () -> LD("c", regs.getE()));
        opcodeHandlers.put(0x4c, () -> LD("c", regs.getH()));
        opcodeHandlers.put(0x4d, () -> LD("c", regs.getL()));
        opcodeHandlers.put(0x4e, () -> LD("c", mem.readByte(regs.getPC() + 1)));
        opcodeHandlers.put(0x4f, () -> LD("c", regs.getA()));
        opcodeHandlers.put(0x50, () -> LD("d", regs.getB()));
        opcodeHandlers.put(0x51, () -> LD("d", regs.getC()));
        opcodeHandlers.put(0x52, () -> LD("d", regs.getD()));
        opcodeHandlers.put(0x53, () -> LD("d", regs.getE()));
        opcodeHandlers.put(0x54, () -> LD("d", regs.getH()));
        opcodeHandlers.put(0x55, () -> LD("d", regs.getL()));
        opcodeHandlers.put(0x56, () -> LD("d", mem.readByte(regs.getPC() + 1)));
        opcodeHandlers.put(0x57, () -> LD("d", regs.getA()));
        opcodeHandlers.put(0x58, () -> LD("e", regs.getB()));
        opcodeHandlers.put(0x59, () -> LD("e", regs.getC()));
        opcodeHandlers.put(0x5a, () -> LD("e", regs.getD()));
        opcodeHandlers.put(0x5b, () -> LD("e", regs.getE()));
        opcodeHandlers.put(0x5c, () -> LD("e", regs.getH()));
        opcodeHandlers.put(0x5d, () -> LD("e", regs.getL()));
        opcodeHandlers.put(0x5e, () -> LD("e", mem.readByte(regs.getPC() + 1)));
        opcodeHandlers.put(0x5f, () -> LD("e", regs.getA()));
        opcodeHandlers.put(0x60, () -> LD("h", regs.getB()));
        opcodeHandlers.put(0x61, () -> LD("h", regs.getC()));
        opcodeHandlers.put(0x62, () -> LD("h", regs.getD()));
        opcodeHandlers.put(0x63, () -> LD("h", regs.getE()));
        opcodeHandlers.put(0x64, () -> LD("h", regs.getH()));
        opcodeHandlers.put(0x65, () -> LD("h", regs.getL()));
        opcodeHandlers.put(0x66, () -> LD("h", mem.readByte(regs.getPC() + 1)));
        opcodeHandlers.put(0x67, () -> LD("h", regs.getA()));
        opcodeHandlers.put(0x68, () -> LD("l", regs.getB()));
        opcodeHandlers.put(0x69, () -> LD("l", regs.getC()));
        opcodeHandlers.put(0x6a, () -> LD("l", regs.getD()));
        opcodeHandlers.put(0x6b, () -> LD("l", regs.getE()));
        opcodeHandlers.put(0x6c, () -> LD("l", regs.getH()));
        opcodeHandlers.put(0x6d, () -> LD("l", regs.getL()));
        opcodeHandlers.put(0x6e, () -> LD("l", mem.readByte(regs.getPC() + 1)));
        opcodeHandlers.put(0x6f, () -> LD("l", regs.getA()));
        opcodeHandlers.put(0x70, () -> LD(regs.getHL(), "b", 1));// _(HL),B);
        opcodeHandlers.put(0x71, () -> LD(regs.getHL(), "c", 1));// _(HL),C);
        opcodeHandlers.put(0x72, () -> LD(regs.getHL(), "d", 1));// _(HL),D);
        opcodeHandlers.put(0x73, () -> LD(regs.getHL(), "e", 1));// _(HL),E);
        opcodeHandlers.put(0x74, () -> LD(regs.getHL(), "h", 1));// _(HL),H);
        opcodeHandlers.put(0x75, () -> LD(regs.getHL(), "l", 1));// _(HL),L);
        opcodeHandlers.put(0x76, () -> HALT());
        opcodeHandlers.put(0x77, () -> LD(regs.getHL(), "a", 1));
        opcodeHandlers.put(0x78, () -> LD("a", regs.getB()));
        opcodeHandlers.put(0x79, () -> LD("a", regs.getC()));
        opcodeHandlers.put(0x7a, () -> LD("a", regs.getD()));
        opcodeHandlers.put(0x7b, () -> LD("a", regs.getE()));
        opcodeHandlers.put(0x7c, () -> LD("a", regs.getH()));
        opcodeHandlers.put(0x7d, () -> LD("a", regs.getL()));
        opcodeHandlers.put(0x7e, () -> LD("a", mem.readByte(regs.getPC() + 1)));
        opcodeHandlers.put(0x7f, () -> LD("a", regs.getA()));
        opcodeHandlers.put(0x80, () -> ADD("a", regs.getB())); // ADD_A,B);
        opcodeHandlers.put(0x81, () -> ADD("a", regs.getC())); // ADD_A,C);
        opcodeHandlers.put(0x82, () -> ADD("a", regs.getD())); // ADD_A,D);
        opcodeHandlers.put(0x83, () -> ADD("a", regs.getE())); // ADD_A,E);
        opcodeHandlers.put(0x84, () -> ADD("a", regs.getH())); // ADD_A,H);
        opcodeHandlers.put(0x85, () -> ADD("a", regs.getL())); // ADD_A,L);
        opcodeHandlers.put(0x86, () -> ADD("a", mem.readByte(regs.getHL()))); // ADD_A,(HL));
        opcodeHandlers.put(0x87, () -> ADD("a", regs.getA())); // ADD_A,A);
        // opcodeHandlers.put(0x88, () -> ADC_A, B);
        // opcodeHandlers.put(0x89, () -> ADC_A, C);
        // opcodeHandlers.put(0x8a, () -> ADC_A, D);
        // opcodeHandlers.put(0x8b, () -> ADC_A, E);
        // add contents of H into A and adds the value of the carry flag
        // opcodeHandlers.put(0x8c, () -> ADC_A, H);
        // opcodeHandlers.put(0x8d, () -> ADC_A, L);
        // opcodeHandlers.put(0x8e, () -> ADC_A, (HL));
        // opcodeHandlers.put(0x8f, () -> ADC_A, A);
        // subtract teh contents of B from contents of A and updates the carry and
        // zero flags and half carry flag
        // and puts the value in A
        opcodeHandlers.put(0x90, () -> SUB("b")); // SUB_B);
        opcodeHandlers.put(0x91, () -> SUB("c")); // SUB_C);
        opcodeHandlers.put(0x92, () -> SUB("d")); // SUB_D);
        opcodeHandlers.put(0x93, () -> SUB("e")); // SUB_E);
        opcodeHandlers.put(0x94, () -> SUB("h")); // SUB_H);
        opcodeHandlers.put(0x95, () -> SUB("l")); // SUB_L);
        opcodeHandlers.put(0x96, () -> SUB("(hl)")); // SUB_(HL));
        opcodeHandlers.put(0x97, () -> SUB("a")); // SUB_A);
        // subtracts contents of B and the carry flag from A stores it in A and
        // updates the carry, zero, and half carry
        opcodeHandlers.put(0x98, () -> SBC("b")); // SBC_B);
        opcodeHandlers.put(0x99, () -> SBC("c")); // SBC_C);
        opcodeHandlers.put(0x9a, () -> SBC("d")); // SBC_D);
        opcodeHandlers.put(0x9b, () -> SBC("e")); // SBC_E);
        opcodeHandlers.put(0x9c, () -> SBC("h")); // SBC_H);
        opcodeHandlers.put(0x9d, () -> SBC("l")); // SBC_L);
        opcodeHandlers.put(0x9e, () -> SBC("(HL)")); // SBC_(HL));
        opcodeHandlers.put(0x9f, () -> SBC("a")); // SBC_A);
        /// AND A and B
        opcodeHandlers.put(0xa0, () -> AND("b")); // AND_B);
        opcodeHandlers.put(0xa1, () -> AND("c")); // AND_C);
        opcodeHandlers.put(0xa2, () -> AND("d")); // AND_D);
        opcodeHandlers.put(0xa3, () -> AND("e")); // AND_E);
        opcodeHandlers.put(0xa4, () -> AND("h")); // AND_H);
        opcodeHandlers.put(0xa5, () -> AND("l")); // AND_L);
        opcodeHandlers.put(0xa6, () -> AND("(hl)")); // AND_(HL));
        opcodeHandlers.put(0xa7, () -> AND("a")); // AND_A);
        // XOR B with A
        opcodeHandlers.put(0xa8, () -> XOR("b"));
        opcodeHandlers.put(0xa9, () -> XOR("c"));
        opcodeHandlers.put(0xaa, () -> XOR("d"));
        opcodeHandlers.put(0xab, () -> XOR("e"));
        opcodeHandlers.put(0xac, () -> XOR("h"));
        opcodeHandlers.put(0xad, () -> XOR("l"));
        opcodeHandlers.put(0xae, () -> XOR("hl"));
        opcodeHandlers.put(0xaf, () -> XOR("a"));
        // or with A
        opcodeHandlers.put(0xb0, () -> OR("b"));
        opcodeHandlers.put(0xb1, () -> OR("c"));
        opcodeHandlers.put(0xb2, () -> OR("d"));
        opcodeHandlers.put(0xb3, () -> OR("e"));
        opcodeHandlers.put(0xb4, () -> OR("h"));
        opcodeHandlers.put(0xb5, () -> OR("l"));
        opcodeHandlers.put(0xb6, () -> OR("hl"));
        opcodeHandlers.put(0xb7, () -> OR("a"));
        /*
         * // compare A and B without modifying either. zero flag is set to 1 if if
         * result is 0. Carry flag is set to 1 if
         * // A is smaller than B abd 0 otherwise. Half carry flag is set to 1 if a
         * carry occured from the lower nibble
         */ // Subtract flag is set to 1
        opcodeHandlers.put(0xb8, () -> CP("b"));
        opcodeHandlers.put(0xb9, () -> CP("c"));
        opcodeHandlers.put(0xba, () -> CP("d"));
        opcodeHandlers.put(0xbb, () -> CP("e"));
        opcodeHandlers.put(0xbc, () -> CP("h"));
        opcodeHandlers.put(0xbd, () -> CP("l"));
        opcodeHandlers.put(0xbe, () -> CP("hl"));
        opcodeHandlers.put(0xbf, () -> CP("a"));
        /*
         * // return to the address at the top of the stack if 0 flag is not set
         */opcodeHandlers.put(0xc0, () -> RETNZ());
        // pop two bytes from the top of the stack and store them in BC
        /*
         */ opcodeHandlers.put(0xc1, () -> POP("bc"));
        /*
         * // jump if zero flag is not set
         * opcodeHandlers.put(0xc2, () -> JP_NZ,a16);
         */ opcodeHandlers.put(0xc3, () -> JP(mem.readWord(regs.getPC() + 1)));
        // JP([regs.getPC() + 1] | (romData[regs.getPC() + 2] << 8)));
        // replace with next8() method??
        // test
        /*
         * / call a subroutine if zero flag is not set
         * opcodeHandlers.put(0xc4, () -> CALL_NZ,a16);
         * // push contents of BC onto the stack
         * opcodeHandlers.put(0xc5, () -> PUSH_BC);
         */opcodeHandlers.put(0xc6, () -> ADD("a", "d8"));
        // ADD_A,d8);
        /*
         * // software reset. Moves the program counter to the start of the program by
         * loading it with 0x00
         */ opcodeHandlers.put(0xc7, () -> RST(00));
        // RST 00
        /*
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
         */ opcodeHandlers.put(0xcf, () -> RST(0x08));
        // RST 08

        // return from a subroutine if carry flag is not set
        opcodeHandlers.put(0xd0, () -> RETNC());
        opcodeHandlers.put(0xd1, () -> POP("de"));
        // Jump if carry flag is not set
        opcodeHandlers.put(0xd2, () -> JPNC(mem.readWord(regs.getPC() + 1)));
        // not a valid opcode
        // opcodeHandlers.put(0xd3, () -> XXX);
        // call a subroutine if the carry flag is not set
        opcodeHandlers.put(0xd4, () -> CALLNC(mem.readWord(regs.getPC() + 1), regs.fByte.checkC()));
        opcodeHandlers.put(0xd5, () -> PUSH("de"));
        // subtract d8 from A
        opcodeHandlers.put(0xd6, () -> SUB(mem.readByte(regs.getPC() + 1)));
        // call routine located at 0x10
        opcodeHandlers.put(0xd7, () -> RST(0x10));
        // RST 10

        // returns control of the program after a call instruction if carry flag is
        // set
        opcodeHandlers.put(0xd8, () -> RETC());
        // return from interrupt
        opcodeHandlers.put(0xd9, () -> RETI());
        // jump if carry flag is set
        opcodeHandlers.put(0xda, () -> JPC(mem.readWord(regs.getPC() + 1)));
        // opcodeHandlers.put(0xdb, () -> XXX);
        // call subroutine at a16 is carry flag is set
        opcodeHandlers.put(0xdc, () -> CALLC(mem.readWord(regs.getPC() + 1), regs.fByte.checkC()));// ,a16);
        // opcodeHandlers.put(0xdd, () -> XXX);
        // subtraction with carry then stored in register A. Zeroe, carry,
        // half-carry, and negative flags are updated
        opcodeHandlers.put(0xde, () -> SBC(mem.readByte(regs.getPC() + 1)));// d8
        // call subroutine at 0x18
        opcodeHandlers.put(0xdf, () -> RST(0x18));// RST 18
        // load contents of the memory address pointed to by a8 into the high0bytes
        // of
        // memory address $FF00 + a8 and
        // stores it into A
        opcodeHandlers.put(0xe0, () -> LD(0xFF00 + mem.readByte(regs.getPC() + 1), "a", 2));// _(a8),A);
        opcodeHandlers.put(0xe1, () -> POP("hl"));
        opcodeHandlers.put(0xe2, () -> LD(0xFF00 + regs.getC(), "a", 1));// _(C),A);
        // opcodeHandlers.put(0xe3, () -> XXX);
        // opcodeHandlers.put(0xe4, () -> XXX);
        opcodeHandlers.put(0xe5, () -> PUSH(regs.getHL()));// _HL);
        // and d8 with A
        opcodeHandlers.put(0xe6, () -> AND(mem.readByte(regs.getPC() + 1)));// _d8);
        // resets the program counter to address 0x0020
        opcodeHandlers.put(0xe7, () -> RST(0x20));

        opcodeHandlers.put(0xe8, () -> ADD("sp", "d8"));
        /*
         * // jumps to address stored in HL
         */ opcodeHandlers.put(0xe9, () -> JP(regs.getHL()));
        opcodeHandlers.put(0xea, () -> LD(mem.readWord(regs.getPC() + 1), "a", 3));// _(a16),A);
        // opcodeHandlers.put(0xeb, () -> XXX);
        // opcodeHandlers.put(0xec, () -> XXX);
        // opcodeHandlers.put(0xed, () -> XXX);
        // XOR d8 with A
        opcodeHandlers.put(0xee, () -> XOR(mem.readByte(regs.getPC() + 1)));// _d8);
        // call to 0x28 saving the program counter to the stack so teh execution can
        // resume after the routine is complete
        opcodeHandlers.put(0xef, () -> RST(0x28));
        // RST28

        opcodeHandlers.put(0xf0, () -> LDu("a", 0xFF00 + mem.readByte(regs.getPC() + 1), 2));// A,(a8));
        opcodeHandlers.put(0xf1, () -> POP("af"));
        opcodeHandlers.put(0xf2, () -> LDu("a", 0xFF00 + regs.getC(), 1));
        // disables interrupts
        opcodeHandlers.put(0xf3, () -> DI());

        opcodeHandlers.put(0xf4, () -> nop());
        opcodeHandlers.put(0xf5, () -> PUSH("af"));
        // OR A and d8. I think it is inclusive
        opcodeHandlers.put(0xf6, () -> OR(mem.readByte(regs.getPC() + 1)));
        // call to 0x30 in memory
        opcodeHandlers.put(0xf7, () -> RST(0x30));

        opcodeHandlers.put(0xf8, () -> LDu("hl", regs.getSP() + (byte) mem.readByte(regs.getPC() + 1), 2));// _HL,SP+r8);
        opcodeHandlers.put(0xf9, () -> LD("sp", regs.getHL()));// _SP,HL);
        opcodeHandlers.put(0xfa, () -> LDu("a", mem.readByte(mem.readWord(regs.getPC() + 1)), 3));// _A,(a16));
        // enables interrupts
        opcodeHandlers.put(0xfb, () -> EI());

        opcodeHandlers.put(0xfc, () -> nop());
        opcodeHandlers.put(0xfd, () -> nop());
        opcodeHandlers.put(0xfe, () -> CP(mem.readByte(regs.getPC() + 1)));
        // call to 0x28
        opcodeHandlers.put(0xff, () -> RST(0x38));//

    }

    public void nop() {
        System.out.println("nop");
        regs.setPC(regs.getPC() + 1);
    }

        public void CPL() {
        int a = regs.getA();
        // flip the bits in A with an XOR with FF
        a ^= 0xff;
        regs.setA(a);
        regs.fByte.setN(true);
        regs.fByte.setH(true);
        regs.setPC(regs.getPC() + 1);
    }
    
        public void RLA() {
        int a = regs.getA();
        boolean carry = regs.fByte.checkC(); // Get the carry flag
        regs.fByte.setC((a & 0x80) == 0x80); // Set the carry flag to the value of the most significant bit of A
        a = (a << 1) | (carry ? 1 : 0); // Left shift A by 1 and insert the carry flag into the least significant bit
        regs.setA(a);
        // Set the zero flag if A is now zero
        regs.fByte.setZ(a == 0);
    }
    
    // load value into intoRegister
    public void LD(String register, int value) {
        int length = 1;
        switch (register) {
            case "a":
                regs.setA(value & 0xff);
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
                length++;
                break;
            case "pc":
                regs.setPC(value);
                length += 2;
                break;
            case "bc":
                regs.setBC(value);
                length += 2;
                break;
            case "af":
                regs.setAF(value);
                length += 2;
                break;
            case "de":
                regs.setDE(value);
                length += 2;
                break;
            case "hl":
                regs.setHL(value);
                length++;
                break;
        }
        regs.setPC(regs.getPC() + length);
    }

    public void LDu(String register, int value, int length) {// For use with Loading bytes after op into register
        switch (register) {
            case "a":
                regs.setA(value & 0xff);
                break;
            case "b":
                regs.setB(value & 0xff);
                break;
            case "c":
                regs.setC(value & 0xff);
                break;
            case "d":
                regs.setD(value & 0xff);
                break;
            case "e":
                regs.setE(value & 0xff);
                break;
            case "h":
                regs.setH(value & 0xff);
                break;
            case "l":
                regs.setL(value & 0xff);
                break;
            case "sp":
                regs.setSP(value & 0xffff);
                break;
            case "pc":
                regs.setPC(value & 0xffff);
                break;
            case "bc":
                regs.setBC(value & 0xffff);
                break;
            case "af":
                regs.setAF(value & 0xffff);
                break;
            case "de":
                regs.setDE(value & 0xffff);
                break;
            case "hl":
                regs.setHL(value & 0xffff);
                break;
        }
        regs.setPC(regs.getPC() + length);
    }

    // load the value of valueRegister into register need to do something like a
    // nested switch or better

    public void LD(int address, String register, int length) {// Load register value into address pointed to by register
        switch (register) {
            case "a":
                mem.writeByte(address, regs.getA());
                break;
            case "b":
                mem.writeByte(address, regs.getB());
                break;
            case "c":
                mem.writeByte(address, regs.getC());
                break;
            case "d":
                mem.writeByte(address, regs.getD());
                break;
            case "e":
                mem.writeByte(address, regs.getE());
                break;
            case "h":
                mem.writeByte(address, regs.getH());
                break;
            case "l":
                mem.writeByte(address, regs.getL());
                break;
            case "sp":// 0x08
                mem.writeWord(address, regs.getSP());
                length += 2;
                break;
        }
        regs.setPC(regs.getPC() + length);
    }

    public void LD(int address, int value, int length) {// 0x36 address<-u8
        mem.writeByte(address, value);
        regs.setPC(regs.getPC() + length);
    }

    // increment the register
    // MAYBE GOOD!--

    public void INC(String register) {
        int result = 0;
        switch (register) {
            case "a":
                result = regs.getA() + 1;
                regs.setA(result);
                break;
            case "b":
                result = regs.getB() + 1;
                regs.setB(result);
                break;
            case "c":
                result = regs.getC() + 1;
                regs.setC(result);
                break;
            case "d":
                result = regs.getD() + 1;
                regs.setD(result);
                break;
            case "e":
                result = regs.getE() + 1;
                regs.setE(result);
                break;
            case "h":
                result = regs.getH() + 1;
                regs.setH(result);
                break;
            case "l":
                result = regs.getL() + 1;
                regs.setL(result);
                break;
            case "(hl)":
                result = mem.readByte(regs.getHL()) + 1;
                // this one does update any flags because it decrements the byte at the
                // memory address stored in hl
                mem.writeByte(regs.getHL(), result);
            case "hl:":
                result = regs.getHL() + 1;
                regs.setHL(result);
            case "sp":
                regs.incrementSP();
                result = regs.getSP();
                break;
            case "de":
                result = regs.getDE() + 1;
                regs.setDE(result);
                break;
            case "bc":
                result = regs.getBC() + 1;
                regs.setBC(result);
                break;
        }
        if (register.length() < 2 || register == "(hl)") {
            // update the Zero flag (Z)
            if (result == 0) {
                regs.fByte.setZ(true);
            } else {
                regs.fByte.setZ(false);
            }
            // update the Subtract flag (N)
            regs.fByte.setN(false);
            // update the Half carry flag (H)
            if ((result & 0x0F) == 0x0F) {
                regs.fByte.setH(true);
            } else {
                regs.fByte.setH(false);
            }
        }
        regs.setPC(regs.getPC() + 1);
    }

    // decrement register
    // when pairs of registers are incremented the flags are not updated
    public void DEC(String register) {
        int result = 0;
        switch (register) {
            case "a":
                result = regs.getA() - 1;
                regs.setA(result);
                break;
            case "b":
                result = regs.getB() - 1;
                regs.setB(result);
                break;
            case "c":
                result = regs.getC() - 1;
                regs.setC(result);
                break;
            case "d":
                result = regs.getD() - 1;
                regs.setD(result);
                break;
            case "e":
                result = regs.getE() - 1;
                regs.setE(result);
                break;
            case "h":
                result = regs.getH() - 1;
                regs.setH(result);
                break;
            case "l":
                result = regs.getL() - 1;
                regs.setL(result);
                break;
            case "(hl)":
                result = mem.readByte(regs.getHL()) - 1;
                // this one does update any flags because it decrements the byte at the
                // memory address stored in hl
                mem.writeByte(regs.getHL(), result);
                break;
            case "sp":
                regs.decrementSP();
                result = regs.getSP();
                break;
            case "hl":
                result = regs.getHL() - 1;
                regs.setHL(result);
                break;
            case "de":
                result = regs.getDE() - 1;
                regs.setDE(result);
                break;
            case "bc":
                result = regs.getBC() - 1;
                regs.setBC(result);
                break;
        }
        if (register.length() < 2 || register == "(hl)") {
            // update the Zero flag (Z)
            if (result == 0) {
                regs.fByte.setZ(true);
            } else {
                regs.fByte.setZ(false);
            }
            // update the Subtract flag (N)
            regs.fByte.setN(true);
            // update the Half carry flag (H)
            if ((result & 0x0F) == 0x0F) {
                regs.fByte.setH(true);
            } else {
                regs.fByte.setH(false);
            }
        }
        regs.setPC(regs.getPC() + 1);
    }

    // right rotates bits in A
    public void RRCA() {

        int a = regs.getA();
        System.out.println(a);
        int carry = a & 0x01; // Get the least significant bit of A
        a = (a >> 1) | (carry << 7); // Right shift A by 1 and insert carry into the most significant bit
        regs.setA(a);

        int msb = (a >> 7) & 0x01; // Get the most significant bit of A
        // set the carry flag to true if most significant bit of result is 1 and false
        // otherwise
        regs.fByte.setC(msb == 1);
        System.out.println(regs.getA());
    }
    
    // one bit left rotation of A
    public void RLCA() {
        int a = regs.getA();
        int carry = (a >> 7) & 0x01; // Get the MSB of A and convert it to an integer
        a = (a << 1) | carry; // Left shift A by 1 and insert carry into the least significant bit
        regs.setA(a);
        regs.fByte.setC(carry == 1); // Set the carry flag to the value of the MSB of A
        regs.fByte.setZ(a == 0); // Set the zero flag if A is now zero
    }

    
    // jump to adress location
    public void JP(int value) {
        System.out.println("jump" + value);
        regs.setPC(value);
    }

   
    // add the value of addRegister into intoRegister and store it in intoRegister
    public void ADD(String register, int value) {
        int result = 0;
        int addValue = 0;

        // instructions adding to A
        if(register.equals("a")){
            result = regs.getA() + value;
            regs.setA(result);
        }
        else if(register.equals("hl")) {
            // instructions adding to h
                result = regs.getHL() + value;
                regs.setHL(value);
            }
        else {
         // something went wrong
         System.out.println("error with ADD " + value +" to " + " register");
        }
        // set the flags
        // zero flag and negative flag do not depend on whether we are addign the value
        // to A or HL like carry and half
        // carry
        if (result == 0)
            regs.fByte.setZ(true);
        // subtract flag is always reset to false
        regs.fByte.setN(false);

        // carry flag is set if result of addition is larger than 8 bits if we are the
        // results into A
        if (register.equals("a")) {
            if (result > 255) {
                regs.fByte.setC(true);
            } else {
                regs.fByte.setC(false);
            }
            // check if the half carry flag needs to be set for an 8 bit register
            // get lower 4 bits w AND the sum them, then and the sum w 0x10 to see if it has
            // a 1 bit then compare with
            // 0x10 to see if we should set the half carry flag or not
            boolean halfCarry = ((regs.getA() & 0x0F) + (addValue & 0x0F) & 0x10) == 0x10;
            regs.fByte.setH(halfCarry);
        } // if we aren't adding to A, we are adding to HL and need to check for 16 bit carry
        else {
            // check if we need to set the carry flag
            boolean carry = ((regs.getHL() & 0xFFFF) + (addValue & 0xFFFF) > 0xFFFF);
            regs.fByte.setC(carry);

            // Check if there is a carry from the lower 4 bits to the upper 4 bits
            boolean halfCarry = ((regs.getHL() & 0x0FFF) + (addValue & 0x0FFF)) > 0x0FFF;
            regs.fByte.setH(halfCarry);
        }
    }

    public void LDINCDECHL(String opcode) {
        // I think it works how it should now - ALEX
        switch (opcode) {
            case "LD A, (HL+)":
                regs.setA(mem.readByte(regs.getHL()));
                regs.setHL(regs.getHL() + 1);
                break;
            case "LD (HL+), A":
                mem.writeByte(regs.getHL(), regs.getA());
                regs.setHL(regs.getHL() + 1);
                break;
            case "LD A, (HL-)":
                regs.setA(mem.readByte(regs.getHL()));
                regs.setHL(regs.getHL() - 1);
                break;
            case "LD (HL-), A":
                mem.writeByte(regs.getHL(), regs.getA());
                regs.setHL(regs.getHL() - 1);
                break;
        }
        regs.setPC(regs.getPC() + 1);
    }

        public void RLA() {
        int a = regs.getA();
        boolean carry = regs.fByte.checkC(); // Get the carry flag
        regs.fByte.setC((a & 0x80) == 0x80); // Set the carry flag to the value of the most significant bit of A
        a = (a << 1) | (carry ? 1 : 0); // Left shift A by 1 and insert the carry flag into the least significant bit
        regs.setA(a);
        // Set the zero flag if A is now zero
        regs.fByte.setZ(a == 0);
    }
    
    // right rotates bits in A
    public void RRCA() {

        int a = regs.getA();
        System.out.println(a);
        int carry = a & 0x01; // Get the least significant bit of A
        a = (a >> 1) | (carry << 7); // Right shift A by 1 and insert carry into the most significant bit
        regs.setA(a);

        int msb = (a >> 7) & 0x01; // Get the most significant bit of A
        // set the carry flag to true if most significant bit of result is 1 and false
        // otherwise
        regs.fByte.setC(msb == 1);
        System.out.println(regs.getA());
    }
    
       // https://forums.nesdev.org/viewtopic.php?f=20&t=15944 psuedoCode for DAA
    // decimal adjust register A
    public void DAA() {
        int a = regs.getA();
        int adjust = 0;
        if (regs.fByte.checkH() || (a & 0x0F) > 9) {
            adjust |= 0x06;
        }
        if (regs.fByte.checkC() || a > 0x99 || (a > 0x8F && (a & 0x0F) > 9)) {
            adjust |= 0x60;
            regs.fByte.setC(true);
        }
        if (regs.fByte.checkH() && !regs.fByte.checkN() && (a & 0x0F) < 6) {
            adjust |= 0x06;
        }
        int result = a + adjust;
        regs.fByte.setZ(result == 0);
        regs.fByte.setH((a & 0x0F) + (adjust & 0x0F) > 0x0F);
        regs.setA(result & 0xFF);
        regs.setPC(regs.getPC() + 1);
    }

        // right shift register
    public void RR() {
        int a = regs.getA();
        int carry = a & 0x01; // Get the least significant bit of A
        a = (a >> 1) | (regs.fByte.checkC() ? 0x80 : 0x00); // Right shift A by 1 and insert previous carry into the
                                                            // most significant bit
        regs.setA(a);

        // Set the carry flag to the least significant bit of the register being rotated
    }
    
    // jump relative by the amount of the value passed in
    public void JR(int value) {
        System.out.println("jump relative");
        int address = regs.getPC() + 2 + (byte) value;// 2 for opcode and byte
        regs.setPC(address);
    }

    // jump if zero flag is set
    public void JRZ(int value) {
        System.out.println("Jump not zero");
        if (regs.fByte.checkZ()) {
            JR(value);
        } else
            regs.setPC(regs.getPC() + 2);
    }

    // jump if zero flag is not set the amount that is passed in e.g. r8
    public void JRNZ(int value) {
        System.out.println("jump");
        if (!regs.fByte.checkZ()) {
            JR(value);
        } else
            regs.setPC(regs.getPC() + 2);
    }

    // jump if the carry flag is set
    public void JRC(int value) {
        System.out.println("");
        if (regs.fByte.checkC()) {
            JR(value);
        } else
            regs.setPC(regs.getPC() + 2);
    }

    // jump if the carry flag is NOT set
    public void JRNC(int value) {
        System.out.println("");
        if (!regs.fByte.checkC()) {
            JR(value);
        } else
            regs.setPC(regs.getPC() + 2);
    }

    // set carry flag
    public void SCF() {
        regs.fByte.setC(true);
        regs.fByte.setN(false);
        regs.fByte.setH(false);
        regs.setPC(regs.getPC() + 1);
    }

    // compliment carry flag
    public void CCF() {
        System.out.println("");
        regs.fByte.setC(!regs.fByte.checkC());
        regs.fByte.setN(false);
        regs.fByte.setH(false);
        regs.setPC(regs.getPC() + 1);
    }

    // stop cpu
    public void STOP() {
        System.out.println("STOP");
        halted = true;
        regs.setPC(regs.getPC() + 1);
    }

    // halt cpu temporarily?
    public void HALT() {
        System.out.println("");
        halted = true;
        regs.setPC(regs.getPC() + 1);
    }

    // add the second register to the first register with the carry flag and store
    // it in the first register
    public void ADC(String register) {
        int result = 0;
        int addValue = regs.getRegisterValue(register);
        int carry = 0;

        // set carry to 1 is the carry flag is set to true
        if (regs.fByte.checkC())
            carry = 1;

        if (!register.equals("d8")) {
            // set A to the result
            result = regs.getA() + regs.getRegisterValue(register) + carry;
            regs.setA(result);
        } else {
            // add d8 value to A
            regs.setA(0);
        }

        // set the flags
        if (result == 0)
            regs.fByte.setZ(true);
        regs.fByte.setN(false);

        // check for half carry
        if (((regs.getA() & 0x0F) + (addValue & 0x0F) + carry) > 0x0F)
            regs.fByte.setH(true);

        // check for carry
        if ((regs.getA() + addValue + carry) > 0xFF)
            regs.fByte.setC(true);

    }

    public void ADC(int u8) {
        System.out.println(" ADC ");
        int a = regs.getA();
        int result = 0;
        int value = u8;
        int carry = regs.fByte.checkC() ? 1 : 0;
        result = value + a + carry;
        if (result == 0)
            regs.fByte.setZ(true);
        regs.fByte.setZ(false);
        if ((result & 0xff) != result)
            regs.fByte.setC(true);
        if ((value & 0xf) + (a & 0xf) + carry > 0xf)
            regs.fByte.setH(true);
        regs.setA(result & 0xff);
        regs.setPC(regs.getPC() + 2);
    }

    // subtracts contents of register passed in and puts result in A and sets the
    // carry, zero, and negative flags
    public int SUB(int u8) {// for SUB A, u8
        System.out.println(" Subu8 ");
        int diff = regs.getA() - u8;
        int result = diff & 0xff;
        if (result == 0)
            regs.fByte.setZ(true);// zero
        if (diff < 0)
            regs.fByte.setC(true);// carry
        regs.fByte.setN(true);// negative
        if ((regs.getA() & 0xf) - (u8 & 0xf) < 0)
            regs.fByte.setH(true);// half carry
        regs.setPC(regs.getPC() + 2);
        return result;
    }

    public int SUB(String register) {// for SUB A, register
        System.out.println(" SUB ");
        int value = 0;
        switch (register) {
            case "a":
                value = regs.getA();
            case "b":
                value = regs.getB();
            case "c":
                value = regs.getC();
            case "d":
                value = regs.getD();
            case "e":
                value = regs.getE();
            case "h":
                value = regs.getH();
            case "l":
                value = regs.getL();
            case "hl":// (HL) byte where hl points to
                value = mem.readByte(regs.getHL());
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
        regs.setPC(regs.getPC() + 1);
        return result;
    }

    // AND A with register passed in
    public void AND(String register) {
        System.out.println("");
        int result = 0;
        switch (register) {
            case "a":
                result = regs.getA() & regs.getA();
            case "b":
                result = regs.getA() & regs.getB();
            case "c":
                result = regs.getA() & regs.getC();
            case "d":
                result = regs.getA() & regs.getD();
            case "e":
                result = regs.getA() & regs.getE();
            case "h":
                result = regs.getA() & regs.getH();
            case "l":
                result = regs.getA() & regs.getL();
            case "hl":
                result = regs.getA() & mem.readByte(regs.getHL());
        }
        if (result == 0)
            regs.fByte.setZ(true);
        regs.fByte.setN(false);
        regs.fByte.setH(true);
        regs.fByte.setC(false);
        regs.setA(result);
        regs.setPC(regs.getPC() + 1);
    }

    // AND A with u8 passed in
    public void AND(int u8) {
        System.out.println("");
        int result = 0;
        result = regs.getA() & u8;
        if (result == 0)
            regs.fByte.setZ(true);
        regs.fByte.setN(false);
        regs.fByte.setH(true);
        regs.fByte.setC(false);
        regs.setA(result);
        regs.setPC(regs.getPC() + 2);
    }

    // XOR A with register passed in
    public void XOR(String register) {
        System.out.println(" XOR ");
        int result = 0;
        result = regs.getA() ^ regs.getRegisterValue(register);
        if (result == 0) {
            regs.fByte.setZ(true);
        }
        regs.fByte.setN(false);
        regs.fByte.setN(false);
        regs.fByte.setN(false);
        regs.setA(result);
        regs.setPC(regs.getPC() + 1);
    }

    public void XOR(int u8) {// 0xEE op
        System.out.println(" XOR ");
        int result = 0;
        result = regs.getA() ^ (u8 & 0xff);
        if (result == 0) {
            regs.fByte.setZ(true);
        }
        regs.fByte.setN(false);
        regs.fByte.setN(false);
        regs.fByte.setN(false);
        regs.setA(result);
        regs.setPC(regs.getPC() + 2);
    }

    // OR A with register
    public void OR(String register) {
        System.out.println(" OR ");
        int result = 0;
        switch (register) {
            case "a":
                result = regs.getA() | regs.getA();
            case "b":
                result = regs.getA() | regs.getB();
            case "c":
                result = regs.getA() | regs.getC();
            case "d":
                result = regs.getA() | regs.getD();
            case "e":
                result = regs.getA() | regs.getE();
            case "h":
                result = regs.getA() | regs.getH();
            case "l":
                result = regs.getA() | regs.getL();
            case "hl":
                result = regs.getA() | mem.readByte(regs.getHL());
        }
        if (result == 0) {
            regs.fByte.setZ(true);
        }
        regs.fByte.setN(false);
        regs.fByte.setN(false);
        regs.fByte.setN(false);
        regs.setA(result);
        regs.setPC(regs.getPC() + 1);
    }

    // OR A with u8 passed in
    public void OR(int u8) {
        System.out.println(" OR ");
        int result = regs.getA() | u8;
        if (result == 0) {
            regs.fByte.setZ(true);
        }
        regs.fByte.setN(false);
        regs.fByte.setN(false);
        regs.fByte.setN(false);
        regs.setA(result);
        regs.setPC(regs.getPC() + 2);
    }

    // compare contents of a register with A, sets flags, but does not modify either
    public void CP(int u8) {
        System.out.println("CP");
        int atemp = regs.getA();// need temp cuz sub method will change A
        SUB(u8);
        regs.setA(atemp);
        regs.setPC(regs.getPC() + 2);
    }

    public void CP(String register) {
        System.out.println("CP");
        int value = 0;
        int atemp = regs.getA();// need temp cuz sub method will change A
        switch (register) {
            case "a":
                value = regs.getA();
            case "b":
                value = regs.getB();
            case "c":
                value = regs.getC();
            case "d":
                value = regs.getD();
            case "e":
                value = regs.getE();
            case "h":
                value = regs.getH();
            case "l":
                value = regs.getL();
            case "hl": {// (HL) points to address in memory
                value = regs.getHL();
                value = mem.readByte(value);
            }
        }
        SUB(value);// SUB method will set flags
        regs.setA(atemp);
        regs.setPC(regs.getPC() + 1);
    }

    // pops value of top of stack and stores it into the register specified

    // needs memory access for stack pointer

    public void POP(String register) {
        System.out.println("");
        int sp = regs.getSP();
        switch (register) {
            case "af": {
                regs.fByte.setFByte(mem.readByte(sp) & ~0xf);
                sp++;// lower F nibble stays 0
                regs.setA(mem.readByte(sp));
                sp++;
                // maybe need to set flags, dont know how though
            }
            case "bc": {
                regs.setC(mem.readByte(sp));
                sp++;
                regs.setB(mem.readByte(sp));
                sp++;
            }
            case "de": {
                regs.setE(mem.readByte(sp));
                sp++;
                regs.setD(mem.readByte(sp));
                sp++;
            }
            case "hl": {
                regs.setL(mem.readByte(sp));
                sp++;
                regs.setH(mem.readByte(sp));
                sp++;
            }
        }
        regs.setSP(sp);
    }

    // push contents of a register onto the stack, need memory access
    public void PUSH(String register) {// 16bit registers
        System.out.println("");
        int stackp = regs.getSP();
        int upper = 0;
        int lower = 0;
        switch (register) {
            case "af": {
                upper = regs.getA();
                lower = regs.fByte.getFByte();
                // maybe need to set flags, dont know how though
            }
            case "bc": {
                upper = regs.getB();
                lower = regs.getC();
            }
            case "de": {
                upper = regs.getD();
                lower = regs.getE();
            }
            case "hl": {
                upper = regs.getH();
                lower = regs.getL();
            }
            case "nextPC":// for RST instruction
            {
                int nextpc = regs.getPC();
                upper = regs.getPC() & 0xff;
                lower = regs.getPC() << 8;
            }
        }
        stackp--;
        mem.writeByte(stackp, upper);// sets upper
        stackp--;
        mem.writeByte(stackp, lower);
        regs.setSP(stackp);
        regs.setPC(regs.getPC() + 1);
    }

    public void PUSH(int address) {
        int stackp = regs.getSP();
        mem.writeWord(stackp, address);
        regs.setSP(stackp - 2);
        regs.setPC(regs.getPC() + 1);
    }

    // software reset. Moves the program counter to the start of the program by
    // loading it with 0x00

    // return from a subroutine by popping two bytes from the stack and loading them
    // into the program counter. Only

    // returns from subroutine
    public void RET() {
        int sp = 0;
        int lower = (mem.readByte(sp));
        sp++;
        int upper = (mem.readByte(sp));
        sp++;
        regs.setSP(sp);
        JP(upper << 8 | lower);
    }

    // call subroutine if zero flag is set
    public void CALLZ(int location, boolean flag) {
        if (flag)
            CALL(location);
    }

    // call a subroutine if zero flag is not set
    public void CALLNZ(int location, boolean flag) {
        if (flag)
            CALL(location);
    }

    /// call a subroutine if the carry flag is not set
    public void CALLNC(int location, boolean flag) {
        if (flag)
            CALL(location);
    }

    // call subroutine if carry flag is set
    public void CALLC(int location, boolean flag) {
        if (flag)
            CALL(location);
    }

    // calls subroutine
    public void CALL(int location) {
        int nextPC = regs.getPC() + 3;// 3 bytes long for CALL
        PUSH(nextPC);// pointer for next program on stack
        JP(location);
    }

    // call a specific routine in memory located as address passed in e.g. 0x08 that
    // pushes the address of the next instruction
    // onto the stack and transfers program control to the specified routine
    public void RST(int address) {// address specific to opcode
        PUSH(regs.getPC() + 1);// rst is 1 byte long

        JP(address);
    }

    // return to the address at the top of the stack if 0 flag is not set
    // or return control to the calling routine if zero flag is not set. or are
    // these the same thing?
    public void RETNZ() {
        if (!regs.fByte.checkZ() == true)
            ;
        RET();
    }

    // executes if the zero flag is set
    public void RETZ() {
        if (regs.fByte.checkZ() == true)
            ;
        RET();
    }

    // return from a subroutine if carry flag is not set
    public void RETNC() {
        if (!regs.fByte.checkC() == true)
            ;
        RET();
    }

    // return from a subroutine if carry flag is set
    public void RETC() {
        if (regs.fByte.checkC() == true)
            ;
        RET();
    }

    // jump to address passed in if carry flag is not set
    public void JPNC(int address) {
        if (!regs.fByte.checkC())
            JP(address);
        regs.setPC(regs.getPC() + 3);
    }

    // jump to address passed in if carry flag is not set
    public void JPNZ(int address) {
        if (!regs.fByte.checkZ())
            JP(address);
        regs.setPC(regs.getPC() + 3);
    }

    // jump to address passed in if carry flag is not set
    public void JPZ(int address) {
        if (regs.fByte.checkZ())
            JP(address);
        regs.setPC(regs.getPC() + 3);
    }
    
        // right shift register
    public void RR() {
        int a = regs.getA();
        int carry = a & 0x01; // Get the least significant bit of A
        a = (a >> 1) | (regs.fByte.checkC() ? 0x80 : 0x00); // Right shift A by 1 and insert previous carry into the
                                                            // most significant bit
        regs.setA(a);

        // Set the carry flag to the least significant bit of the register being rotated
    }
    
    // jump to address passed in if carry flag is not set
    public void JPC(int address) {
        if (regs.fByte.checkC())
            JP(address);
        regs.setPC(regs.getPC() + 3);
    }

    // return from interrupt
    public void RETI() {
        interruptmanager.setInterruptsEnabled(true);
        RET();
    }

    // subtracts contents of the register and the carry flag from A stores it in A
    // and updates the carry, zero, and half carry
    // flag
    public void SBC(String register) {
        int carry = regs.fByte.checkC() ? 1 : 0;// short hand conditional if carry set, then 1
        int value = 0;
        int diff, result = 0;
        switch (register) {
            case "a":
                value = regs.getA();
            case "b":
                value = regs.getB();
            case "c":
                value = regs.getC();
            case "d":
                value = regs.getD();
            case "e":
                value = regs.getE();
            case "h":
                value = regs.getH();
            case "l":
                value = regs.getL();
            case "hl":
                value = mem.readByte(regs.getPC());// gets value in mem pointed to by pc
        }
        diff = regs.getA() - value - carry;
        result = diff & 0xff;
        if (result == 0)
            regs.fByte.setZ(true);
        regs.fByte.setN(true);
        if (diff < 0)
            regs.fByte.setC(true);
        if ((regs.getA() & 0xf) - (value & 0xf) - carry < 0)
            regs.fByte.setH(true);
        regs.setA(result);
        regs.setPC(regs.getPC() + 1);
    }

    // SBC for u8
    public void SBC(int u8) {
        int carry = regs.fByte.checkC() ? 1 : 0;// short hand conditional if carry set, then 1
        int value = u8;
        int diff, result = 0;
        diff = regs.getA() - value - carry;
        result = diff & 0xff;
        if (result == 0)
            regs.fByte.setZ(true);
        regs.fByte.setN(true);
        if (diff < 0)
            regs.fByte.setC(true);
        if ((regs.getA() & 0xf) - (value & 0xf) - carry < 0)
            regs.fByte.setH(true);
        regs.setA(result);
        regs.setPC(regs.getPC() + 2);
    }

    // disable interupts
    public void DI() {
        interruptmanager.setInterruptsEnabled(false);
        regs.setPC(regs.getPC() + 1);
    }

    // endable interupts
    public void EI() {
        interruptmanager.setInterruptsEnabled(true);
        regs.setPC(regs.getPC() + 1);
    }

}

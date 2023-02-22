import Memory.Memory;

import java.util.HashMap;
import java.util.Map;

public class Opcodes {
    Registers regs;
    byte[] romData;
    InterruptManager interruptmanager;
    Memory mem;// Can change just place holding with small features just to make Opcode

    // pass in Regs class and romData to operate on. Will need to give RAM(memory)
    // object when created
    public Opcodes(Registers regs, byte[] romData,
            InterruptManager interruptmanager, Memory mem) {
        this.regs = regs;
        this.romData = romData;
        this.mem = mem;
        this.interruptmanager = interruptmanager;// to set interrupt flags
    }

    public Opcodes(Registers regs, byte[] romData, InterruptManager interruptmanager) {
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
          // opcodeHandlers.put(0x1, () -> lD_(BC),d16);
          //opcodeHandlers.put(0x2, () -> LD_(BC),A);
          opcodeHandlers.put(0x3, () -> INC("bc"));
          opcodeHandlers.put(0x4, () -> INC("b"));
          opcodeHandlers.put(0x5, () -> DEC("b"));
         //opcodeHandlers.put(0x6, () -> LD_B,d8);
         // rotate bits right reg A
         /* opcodeHandlers.put(0x7, () -> RLCA);
         * opcodeHandlers.put(0x8, () -> LD_(a16),SP);*/
          opcodeHandlers.put(0x9, () ->  ADD("hl","bc")); //ADD_HL,BC);
         // opcodeHandlers.put(0xa, () -> LD_A,(BC));
          opcodeHandlers.put(0xb, () -> DEC("bc"));
          opcodeHandlers.put(0xc, () -> INC("c"));
          opcodeHandlers.put(0xd, () -> DEC("C"));
         /* opcodeHandlers.put(0xe, () -> LD_C,d8);*/
          opcodeHandlers.put(0xf, () -> RRCA());
         /* opcodeHandlers.put(0x10, () -> STOP);
         * opcodeHandlers.put(0x11, () -> LD_DE,d16);
         * opcodeHandlers.put(0x12, () -> LD_(DE),A);*/
          opcodeHandlers.put(0x13, () -> INC("de"));
          opcodeHandlers.put(0x14, () -> INC("d"));
          opcodeHandlers.put(0x15, () -> DEC("d"));
         /* opcodeHandlers.put(0x16, () -> LD_D,d8);
         * // shift bits left reg A
         * opcodeHandlers.put(0x17, () -> RLA);
         * // jump to an offset by adding r8 to the current program counter to determing
         * the address to jump to
         * opcodeHandlers.put(0x18, () -> JR_r8);
          */// add DE to HL and store result in HL
          opcodeHandlers.put(0x19, () -> ADD("hl", "de")); //ADD_HL,DE;
         /* opcodeHandlers.put(0x1a, () -> LD_A,(DE));*/
          opcodeHandlers.put(0x1b, () -> DEC("de"));
          opcodeHandlers.put(0x1c, () -> INC("e"));
          opcodeHandlers.put(0x1d, () -> DEC("e"));
         /* opcodeHandlers.put(0x1e, () -> LD_E,d8);
         * // right shift reg A*/
           opcodeHandlers.put(0x1f, () -> RR()); // opcode is RRA
         /*
         * // jump to new address if teh zero flag is not set
         * opcodeHandlers.put(0x20, () -> JR_NZ,r8);
         * opcodeHandlers.put(0x21, () -> LD_HL,d16);
         * // increment HL after loading A into memory*/
          opcodeHandlers.put(0x22, () -> LDINCDECHL("LD_(HL+),A)"));
          opcodeHandlers.put(0x23, () -> INC("hl"));
          opcodeHandlers.put(0x24, () -> INC("h"));
          opcodeHandlers.put(0x25, () -> DEC("h"));
         /* opcodeHandlers.put(0x26, () -> LD_H,d8);
         * // Decimal adjust accumulator. Adjusts A to the correct representation of a
         * binary-coded decimal using the flags*/
          opcodeHandlers.put(0x27, () -> DAA());
         /* // jump to new address if zero flag is set
         */// opcodeHandlers.put(0x28, () -> JR_Z,r8);
          opcodeHandlers.put(0x29, () -> ADD("hl","hl")); //ADD_HL,HL);
          // load HL into A then increment it*/
          opcodeHandlers.put(0x2a, () -> LDINCDECHL("LD_A,(HL+)")); // LD_A,(HL+)
          opcodeHandlers.put(0x2b, () -> DEC("hl")); // decrement value of hl
          opcodeHandlers.put(0x2c, () -> INC("l"));
          opcodeHandlers.put(0x2d, () -> DEC("l"));
         /* opcodeHandlers.put(0x2e, () -> LD_L,d8);
         * opcodeHandlers.put(0x2f, () -> CPL);
         * opcodeHandlers.put(0x30, () -> JR_NC,r8);
         * opcodeHandlers.put(0x31, () -> LD_SP,d16);
         * // load A into HL then DEC HL*/
          opcodeHandlers.put(0x32, () -> LDINCDECHL("LD_(HL-),A)"));
          opcodeHandlers.put(0x33, () -> INC("sp"));
          opcodeHandlers.put(0x34, () -> INC("(hl)")); // (hl) means the memory value at hl
          opcodeHandlers.put(0x35, () -> DEC("(hl)"));
         /* opcodeHandlers.put(0x36, () -> LD_(HL),d8);
         * // set carry flag to 1 */
         opcodeHandlers.put(0x37, () -> SCF());
         /* // perform relative jump if if the carry flag is set
         */// opcodeHandlers.put(0x38, () -> JR_C,r8);
         opcodeHandlers.put(0x39, () -> ADD("hl","sp")); //ADD_HL,SP);*/
         opcodeHandlers.put(0x3a, () -> LDINCDECHL("LD_A,(HL-)"));
         opcodeHandlers.put(0x3b, () -> DEC("sp"));
         opcodeHandlers.put(0x3c, () -> INC("a"));
         opcodeHandlers.put(0x3d, () -> DEC("a"));
         opcodeHandlers.put(0x3e, () -> LD("a", 0x45));
        /* hardcoding just for test. Need to retrieve nect byte of data at mem(pc+1)
        // complement the carry flag
         * opcodeHandlers.put(0x3f, () -> CCF);*/
          opcodeHandlers.put(0x40, () -> LD("b","b")); //LD_B,B);
          opcodeHandlers.put(0x41, () -> LD("b","c")); //LD_B,C);
          opcodeHandlers.put(0x42, () -> LD("b","d")); //LD_B,D);
          opcodeHandlers.put(0x43, () -> LD("b","e")); //LD_B,E);
          opcodeHandlers.put(0x44, () -> LD("b","h")); //LD_B,H);
          opcodeHandlers.put(0x45, () -> LD("b","l")); //LD_B,L);
          opcodeHandlers.put(0x46, () -> LD("b","(hl)")); //LD_B,(HL));
          opcodeHandlers.put(0x47, () -> LD("b","a")); //LD_B,A);
          opcodeHandlers.put(0x48, () -> LD("c","b")); //LD_C,B);
          opcodeHandlers.put(0x49, () -> LD("c","c")); //LD_C,C);
          opcodeHandlers.put(0x4a, () -> LD("c","d")); //LD_C,D);
          opcodeHandlers.put(0x4b, () -> LD("c","e")); //LD_C,E);
          opcodeHandlers.put(0x4c, () -> LD("c","h")); //LD_C,H);
          opcodeHandlers.put(0x4d, () -> LD("c","l")); //LD_C,L);
          opcodeHandlers.put(0x4e, () -> LD("c","(HL)")); //LD_C,(HL));
          opcodeHandlers.put(0x4f, () -> LD("c","a")); //LD_C,A);
          opcodeHandlers.put(0x50, () -> LD("d","b")); //LD_D,B);
          opcodeHandlers.put(0x51, () -> LD("d","c")); //LD_D,C);
          opcodeHandlers.put(0x52, () -> LD("d","d")); //LD_D,D);
          opcodeHandlers.put(0x53, () -> LD("d","e")); //LD_D,E);
          opcodeHandlers.put(0x54, () -> LD("d","h")); //LD_D,H);
          opcodeHandlers.put(0x55, () -> LD("d","l")); //LD_D,L);
          opcodeHandlers.put(0x56, () -> LD("d","(hl)")); //LD_D,(HL));
          opcodeHandlers.put(0x57, () -> LD("d","a")); //LD_D,A);
          opcodeHandlers.put(0x58, () -> LD("e","b")); //LD_E,B);
          opcodeHandlers.put(0x59, () -> LD("e","c")); //LD_E,C);
          opcodeHandlers.put(0x5a, () -> LD("e","d")); //LD_E,D);
          opcodeHandlers.put(0x5b, () -> LD("e","e")); //LD_E,E);
          opcodeHandlers.put(0x5c, () -> LD("e","h")); //LD_E,H);
          opcodeHandlers.put(0x5d, () -> LD("e","l")); //LD_E,L);
          opcodeHandlers.put(0x5e, () -> LD("e","(hl)")); //LD_E,(HL));
          opcodeHandlers.put(0x5f, () -> LD("e","a")); //LD_E,A);
          opcodeHandlers.put(0x60, () -> LD("h","b")); //LD_H,B);
          opcodeHandlers.put(0x61, () -> LD("h","c")); //LD_H,C);
          opcodeHandlers.put(0x62, () -> LD("h","d")); //LD_H,D);
          opcodeHandlers.put(0x63, () -> LD("h","e")); //LD_H,E);
          opcodeHandlers.put(0x64, () -> LD("h","h")); //LD_H,H);
          opcodeHandlers.put(0x65, () -> LD("h","l")); //LD_H,L);
          opcodeHandlers.put(0x66, () -> LD("h","(hl)")); //LD_H,(HL));
          opcodeHandlers.put(0x67, () -> LD("h","a")); //LD_H,A);
          opcodeHandlers.put(0x68, () -> LD("l","b")); //LD_L,B);
          opcodeHandlers.put(0x69, () -> LD("l","c")); //LD_L,C);
          opcodeHandlers.put(0x6a, () -> LD("l","d")); //LD_L,D);
          opcodeHandlers.put(0x6b, () -> LD("l","e")); //LD_L,E);
          opcodeHandlers.put(0x6c, () -> LD("l","h")); //LD_L,H);
          opcodeHandlers.put(0x6d, () -> LD("l","l")); //LD_L,L);
          opcodeHandlers.put(0x6e, () -> LD("l","(hl)")); //LD_L,(HL));
          opcodeHandlers.put(0x6f, () -> LD("l","a")); //LD_L,A);
          opcodeHandlers.put(0x70, () -> LD("(hl)","b")); //LD_(HL),B);
          opcodeHandlers.put(0x71, () -> LD("(hl)","c")); //LD_(HL),C);
          opcodeHandlers.put(0x72, () -> LD("(hl)","d")); //LD_(HL),D);
          opcodeHandlers.put(0x73, () -> LD("(hl)","e")); //LD_(HL),E);
          opcodeHandlers.put(0x74, () -> LD("h","h")); //LD_(HL),H);
          opcodeHandlers.put(0x75, () -> LD("l","l")); //LD_(HL),L);
          //opcodeHandlers.put(0x76, () -> HALT);
          opcodeHandlers.put(0x77, () -> LD("(HL)","a")); //LD_(HL),A);
          opcodeHandlers.put(0x78, () -> LD("a", "b")); //LD_A,B
          opcodeHandlers.put(0x79, () -> LD("a","c")); //LD_A,C);
          opcodeHandlers.put(0x7a, () -> LD("a","d")); //LD_A,D);
          opcodeHandlers.put(0x7b, () -> LD("a","e")); //LD_A,E);
          opcodeHandlers.put(0x7c, () -> LD("a","h")); //LD_A,H);
          opcodeHandlers.put(0x7d, () -> LD("a","l")); //LD_A,L);
          opcodeHandlers.put(0x7e, () -> LD("a","(hl)")); //LD_A,(HL));
          opcodeHandlers.put(0x7f, () -> LD("a","a")); //LD_A,A); ////////////////////////
          opcodeHandlers.put(0x80, () -> ADD("a","b")); //ADD_A,B);
          opcodeHandlers.put(0x81, () -> ADD("a","c")); // ADD_A,C);
          opcodeHandlers.put(0x82, () -> ADD("a","d")); // ADD_A,D);
          opcodeHandlers.put(0x83, () -> ADD("a","e")); // ADD_A,E);
          opcodeHandlers.put(0x84, () -> ADD("a","h")); // ADD_A,H);
          opcodeHandlers.put(0x85, () -> ADD("a","l")); // ADD_A,L);
          opcodeHandlers.put(0x86, () -> ADD("a","(hl)")); // ADD_A,(HL));
          opcodeHandlers.put(0x87, () -> ADD("a","a")); // ADD_A,A);
         /* opcodeHandlers.put(0x88, () -> ADC_A,B);
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
         * // and puts the value in A */
          opcodeHandlers.put(0x90, () -> SUB("b")); //SUB_B);
          opcodeHandlers.put(0x91, () -> SUB("c")); //SUB_C);
          opcodeHandlers.put(0x92, () -> SUB("d")); //SUB_D);
          opcodeHandlers.put(0x93, () -> SUB("e")); //SUB_E);
          opcodeHandlers.put(0x94, () -> SUB("h")); //SUB_H);
          opcodeHandlers.put(0x95, () -> SUB("l")); //SUB_L);
          opcodeHandlers.put(0x96, () -> SUB("(hl)")); //SUB_(HL));
          opcodeHandlers.put(0x97, () -> SUB("a")); //SUB_A);
          // subtracts contents of B and the carry flag from A stores it in A and
          // updates the carry, zero, and half carry
          opcodeHandlers.put(0x98, () -> SBC("b")); //SBC_B);
          opcodeHandlers.put(0x99, () -> SBC("c")); //SBC_C);
          opcodeHandlers.put(0x9a, () -> SBC("d")); //SBC_D);
          opcodeHandlers.put(0x9b, () -> SBC("e")); //SBC_E);
          opcodeHandlers.put(0x9c, () -> SBC("h")); //SBC_H);
          opcodeHandlers.put(0x9d, () -> SBC("l")); //SBC_L);
          opcodeHandlers.put(0x9e, () -> SBC("(HL)")); //SBC_(HL));
          opcodeHandlers.put(0x9f, () -> SBC("a")); //SBC_A);
          /// AND A and B
          opcodeHandlers.put(0xa0, () -> AND("b")); //AND_B);
          opcodeHandlers.put(0xa1, () -> AND("c")); //AND_C);
          opcodeHandlers.put(0xa2, () -> AND("d")); //AND_D);
          opcodeHandlers.put(0xa3, () -> AND("e")); //AND_E);
          opcodeHandlers.put(0xa4, () -> AND("h")); //AND_H);
          opcodeHandlers.put(0xa5, () -> AND("l")); //AND_L);
          opcodeHandlers.put(0xa6, () -> AND("(hl)")); //AND_(HL));
          opcodeHandlers.put(0xa7, () -> AND("a")); //AND_A);
          opcodeHandlers.put(0xa8, () -> XOR("b")); //XOR_B);
          opcodeHandlers.put(0xa9, () -> XOR("c")); //XOR_C);
          opcodeHandlers.put(0xaa, () -> XOR("d")); //XOR_D);
          opcodeHandlers.put(0xab, () -> XOR("e")); //XOR_E);
          opcodeHandlers.put(0xac, () -> XOR("h")); //XOR_H);
          opcodeHandlers.put(0xad, () -> XOR("l")); //XOR_L);
          opcodeHandlers.put(0xae, () -> XOR("(HL)")); //XOR_(HL));
          opcodeHandlers.put(0xaf, () -> XOR("A")); //XOR_A);

          opcodeHandlers.put(0xb0, () -> OR("b")); //OR_B);
          opcodeHandlers.put(0xb1, () -> OR("c")); //OR_C);
          opcodeHandlers.put(0xb2, () -> OR("d")); //OR_D);
          opcodeHandlers.put(0xb3, () -> OR("e")); //OR_E);
          opcodeHandlers.put(0xb4, () -> OR("h")); //OR_H);
          opcodeHandlers.put(0xb5, () -> OR("l")); //OR_L);
          opcodeHandlers.put(0xb6, () -> OR("(HL)")); //OR_(HL));opcodeHandlers.put(0xb7, () -> OR_A);
         /* // compare A and B without modifying either. zero flag is set to 1 if if
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
         * opcodeHandlers.put(0xc5, () -> PUSH_BC); */
          opcodeHandlers.put(0xc6, () -> ADD("a","d8")); //ADD_A,d8);
          // software reset. Moves the program counter to the start of the program by
         /* loading it with 0x00
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
         * // subtract d8 from A*/
          //opcodeHandlers.put(0xd6, () -> SUB("d8")); //SUB_d8);
          // call routine located at 0x10
         /* opcodeHandlers.put(0xd7, () -> RST_10H);
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
         * // and d8 with A */
          opcodeHandlers.put(0xe6, () -> AND("d8")); //AND_d8);
          // resets the program counter to address 0x0020
         // opcodeHandlers.put(0xe7, () -> RST_20H);*/
          opcodeHandlers.put(0xe8, () -> ADD("sp","d8")); //ADD_SP,r8);
         // jumps to address stored in HL
         /* opcodeHandlers.put(0xe9, () -> JP_HL);
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
         * opcodeHandlers.put(0xf8, () -> LD_HL,SP+r8);*/
          opcodeHandlers.put(0xf9, () -> LD("sp", "hl")); //LD_SP,HL);
         //* opcodeHandlers.put(0xfa, () -> LD_A,(a16));
          // enables interruptsopcodeHandlers.put(0xfb, () -> EI());
         opcodeHandlers.put(0xfc, () -> nop());
         opcodeHandlers.put(0xfd, () -> nop());
         opcodeHandlers.put(0xfe, () -> CP(mem.readByte(regs.getPC() + 1)));
         // call to 0x28
         opcodeHandlers.put(0xff, () -> RST(0x38));//

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
                regs.setA(regs.getRegisterValue(valueRegister));
                break;
            case "b":
                regs.setB(regs.getRegisterValue(valueRegister));
                break;
            case "c":
                regs.setC(regs.getRegisterValue(valueRegister));
                break;
            case "d":
                regs.setD(regs.getRegisterValue(valueRegister));
                break;
            case "e":
                regs.setE(regs.getRegisterValue(valueRegister));
                break;
            case "h":
                regs.setH(regs.getRegisterValue(valueRegister));
                break;
            case "l":
                regs.setL(regs.getRegisterValue(valueRegister));
                break;
            case "sp":
                regs.setSP(regs.getRegisterValue(valueRegister));
                break;
            case "pc":
                regs.setPC(regs.getRegisterValue(valueRegister));
                break;
            // case "ad": regs.setAD(value);
            // break;
            case "bc":
                regs.setBC(regs.getRegisterValue(valueRegister));
                break;
            case "af":
                regs.setAF(regs.getRegisterValue(valueRegister));
                break;
            case "de":
                regs.setDE(regs.getRegisterValue(valueRegister));
                break;
            case "hl":
                regs.setHL(regs.getRegisterValue(valueRegister));
                break;
            case "(hl)":
                System.out.println("set the value pointed to by (HL)");
                break;
        }
    }

    // increment the register
    // INCOMPLETE need to make it so it incrememnts the byte pointed to by HL not
    // the value of HL when (HL) is passed in

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
                result = regs.getHL() + 1;
                // this one does not update any flags because it decrements the byte at the
                // memory address stored in hl
                // regs.setHL(result );
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
        if (register.length() > 1) {
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
    }

    // decrement register
    // INCOMPLETE
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
                result = regs.getHL() - 1;
                // this one does not update any flags because it decrements the byte at the
                // memory address stored in hl
                // regs.setHL(result );
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
        if (register.length() > 1) {
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
    public void ADD(String intoRegister, String addRegister) {
        int result = 0;
        int addValue = 0;

        switch (intoRegister) {
            case "a":
                switch (addRegister) {
                    case "a":
                        result = regs.getA() + regs.getA();
                        addValue = regs.getA();
                        regs.setA(result);
                        break;
                    case "b":
                        result = regs.getA() + regs.getB();
                        addValue = regs.getB();
                        regs.setA(result);
                        break;
                    case "c":
                        result = regs.getA() + regs.getC();
                        addValue = regs.getC();
                        regs.setA(result);
                        break;
                    case "d":
                        result = regs.getA() + regs.getD();
                        addValue = regs.getD();
                        regs.setA(result);
                        break;
                    case "e":
                        result = regs.getA() + regs.getE();
                        regs.setA(result);
                        addValue = regs.getE();
                        break;
                    case "h":
                        result = regs.getA() + regs.getH();
                        addValue = regs.getH();
                        regs.setA(result);
                        break;
                    case "l":
                        result = regs.getA() + regs.getL();
                        addValue = regs.getL();
                        regs.setA(result);
                        break;
                    // this one adds an 8 bit value to A that is pointed to by the value of HL
                    case "(hl)":
                        result = regs.getA() + regs.getHL();
                        addValue = regs.getHL();
                        regs.setA(result);
                        break;
                    // add an 8 bit immediate value to A. The value that we add to A is the next
                    // byte in memory
                    case "d8":
                        int d8Val = 0; // we will have to get the value from somewhere
                        result = regs.getA() + d8Val;
                        addValue = d8Val;
                        regs.setA(result);
                        // regs.incPC();
                        break;
                }
            case "hl":
                switch (addRegister) {
                    case "bc":
                        result = regs.getHL() + regs.getBC();
                        regs.setHL(result);
                        break;
                    case "de":
                        result = regs.getHL() + regs.getDE();
                        regs.setHL(regs.getHL() + regs.getDE());
                        break;
                    case "hl":
                        result = regs.getHL() + regs.getHL();
                        regs.setHL(result);
                        break;
                    case "sp":
                        result = regs.getHL() + regs.getSP();
                        regs.setHL(result);
                }
                // add an 8 bit immediate value to sp
            case "sp":
                result = regs.getSP() + 0;
                regs.setSP(result);
                break;
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
        if (intoRegister.equals("a")) {
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
        } // if we aren't adding to A, we are adding to HL
        else {
            // check if we need to set the carry flag
            boolean carry = ((regs.getHL() & 0xFFFF) + (addValue & 0xFFFF) > 0xFFFF);
            regs.fByte.setC(carry);

            // Check if there is a carry from the lower 4 bits to the upper 4 bits
            boolean halfCarry = ((regs.getHL() & 0x0FFF) + (addValue & 0x0FFF)) > 0x0FFF;
            regs.fByte.setH(halfCarry);
        }
    }

    // right shift register
    // Right rotate without carry on A is currently implemented will need to add the
    // other registers when implementing
    // the extended opcodes
    public void RR() {
        int a = regs.getA();
        int carry = a & 0x01; // Get the least significant bit of A
        a = (a >> 1) | (regs.fByte.checkC() ? 0x80 : 0x00); // Right shift A by 1 and insert previous carry into the
                                                            // most significant bit
        regs.setA(a);

        // Set the carry flag to the least significant bit of the register being rotated
        regs.fByte.setC(carry == 1);
    }

    // jump if zero flag is not set the amount that is passed in e.g. r8
    public void JRNZ(int value) {
        System.out.println("jump" + value);
    };

    public void LDINCDECHL(String opcode) {
        // need to update to load the contents of the memory address pointed to by HL
        // not just the value of HL
        switch (opcode) {
            case "LD A, (HL+)":
                regs.setA(regs.getHL());
                regs.setHL(regs.getHL() + 1);
                break;
            case "LD (HL+), A":
                regs.setHL(regs.getA());
                regs.setHL(regs.getHL() + 1);
                break;
            case "LD A, (HL-)":
                regs.setA(regs.getHL());
                regs.setHL(regs.getHL() - 1);
                break;
            case "LD (HL-), A":
                regs.setHL(regs.getA());
                regs.setHL(regs.getHL() - 1);
                break;
        }

    }

    // decimal adjust register A
    // NOT WORKING CORRECTLY
    // INCOMPLETE
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
    }

    // jump if zero flag is set
    public void JRZ(int offset) {
        System.out.println("Jump not zero" + offset);
    }

    // set carry flag
    public void SCF() {
        regs.fByte.setC(true);
    }

    // jump if the carry flag is set
    public void JRC() {
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

    // add the value in the register to A then add one if the carry flag is set
    // it in the first register sets all flags depending on the result works for
    // INCOMPLETE
    public void ADC(String register) {
        int result = 0;
        int addValue = regs.getRegisterValue(register);
        int carry = 0;

        // set carry to 1 is the carry flag is set to true
        if(regs.fByte.checkC())
            carry = 1;

        if(!register.equals("d8")) {
            // set A to the result
            result = regs.getA() + regs.getRegisterValue(register) + carry;
            regs.setA(result);
        }
        else {
            // add d8 value to A
            regs.setA(0);
        }

        // set the flags
        if(result == 0)
            regs.fByte.setZ(true);
        regs.fByte.setN(false);

        // check for half carry
        if(((regs.getA() & 0x0F) + (addValue & 0x0F) + carry) > 0x0F)
            regs.fByte.setH(true);

        // check for carry
        if((regs.getA() + addValue + carry) > 0xFF)
            regs.fByte.setC(true);

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
            case "HL":// (HL) byte where hl points to
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
        return result;
    }

    // AND A with register passed in
    public void AND(String register) {
        System.out.println("");
        int result = 0;
        if(register.equals("d8")){
            System.out.println("AND wiht d8");
        }
        else {
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
                case "(hl)":
                    result = regs.getA() & mem.readByte(regs.getHL());
            }
        }
        if (result == 0)
            regs.fByte.setZ(true);
        regs.fByte.setN(false);
        regs.fByte.setH(true);
        regs.fByte.setC(false);
        regs.setA(result);
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
    }

    // XOR with a register passed in 
    public void XOR(String register) {
        if(register.equals("(HL)")){
            System.out.println("XOR with value pointed to by hl");
        }
        else if(register.equals("d8")){
            System.out.println("XOR with d8");
        }
        else {
            System.out.println(" XOR ");
            int result = regs.getA() ^ regs.getRegisterValue(register);
            if (result == 0)
                regs.fByte.setZ(true);
            regs.setA(result);

            // other flags are set to false
            regs.fByte.setC(false);
            regs.fByte.setH(false);
            regs.fByte.setZ(false);
        }
    }

    public void XOR(int A, int u8) {// 0xEE op
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
    }

    // compare contents of a register with A, sets flags, but does not modify either
    public void CP(int u8) {
        System.out.println("CP");
        int atemp = regs.getA();// need temp cuz sub method will change A
        SUB(u8);
        regs.setA(atemp);
    }

    public void CP(String register) {
        System.out.println("CP");
        int value = 0;
        int atemp = regs.getA();// need temp cuz sub method will change A
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
            case "HL": {// (HL) points to address in memory
                value = regs.getHL();
                value = mem.readByte(value);
            }
        }
        SUB(value);// SUB method will set flags
        regs.setA(atemp);
    }

    // pops value of top of stack and stores it into the register specified

    // needs memory access for stack pointer

    public void POP(String register) {
        System.out.println("");
        int sp = regs.getSP();
        switch (register) {
            case "AF": {
                regs.fByte.setFByte(mem.readByte(sp) & ~0xf);
                sp++;// lower F nibble stays 0
                regs.setA(mem.readByte(sp));
                sp++;
                // maybe need to set flags, dont know how though
            }
            case "BC": {
                regs.setC(mem.readByte(sp));
                sp++;
                regs.setB(mem.readByte(sp));
                sp++;
            }
            case "DE": {
                regs.setE(mem.readByte(sp));
                sp++;
                regs.setD(mem.readByte(sp));
                sp++;
            }
            case "HL": {
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
            case "AF": {
                upper = regs.getA();
                lower = regs.fByte.getFByte();
                // maybe need to set flags, dont know how though
            }
            case "BC": {
                upper = regs.getB();
                lower = regs.getC();
            }
            case "DE": {
                upper = regs.getD();
                lower = regs.getE();
            }
            case "HL": {
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
        //mem.writeByte(stackp, upper);// sets upper
        stackp--;
        //mem.writeByte(stackp, lower);
        regs.setSP(stackp);
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
    public void CALLZ(int location) {
    }

    // calls subroutine
    public void CALL(int location) {
    }

    // call a specific routine in memory located as address passed in e.g. 0x08 that
    // pushes the address of the next instruction
    // onto the stack and transfers program control to the specified routine
    public void RST(int address) {// address specific to opcode
        PUSH("nextPC");

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

    // return from interrupt
    public void RETI() {
        interruptmanager.setInterruptsEnabled(true);
        RET();
    }

    // subtracts contents of the register and the carry flag from A stores it in A
    // and updates the carry, zero, and half carry flags
    public void SBC(String register) {
        int carry = regs.fByte.checkC() ? 1 : 0;// short hand conditional if carry set, then 1
        int value = 0;
        int diff, result = 0;
        switch (register) {
            case "a":
                value = regs.getA();
                break;
            case "b":
                value = regs.getB();
                break;
            case "c":
                value = regs.getC();
                break;
            case "d":
                value = regs.getD();
                break;
            case "e":
                value = regs.getE();
                break;
            case "h":
                value = regs.getH();
                break;
            case "l":
                value = regs.getL();
                break;
            case "hl":
                value = mem.readByte(regs.getPC());// gets value in mem pointed to by pc
                break;
            case "(hl)":
                // value pointed to by hl
                value = 0;
                break;
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

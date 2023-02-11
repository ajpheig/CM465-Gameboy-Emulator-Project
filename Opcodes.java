import java.util.HashMap;
import java.util.Map;

public class Opcodes {

    public void executeOpcodes(byte[] romData) {
        Map<Integer, Runnable> opcodeHandlers = new HashMap<>();

        // this is a map where the opcodes are key and an opcode will call the corresponding runnable object
        // like this Runnable handler = opcodeHandlers.get(0x1);
        // which can be run with handler.run

        // one of two ways to call the method
        opcodeHandlers.put(0x0, this::nop);
        /*opcodeHandlers.put(0x1, () -> ldBCd16);
        opcodeHandlers.put(0x2, () -> LD_(BC),A);
        opcodeHandlers.put(0x3, () -> INC_BC);
        opcodeHandlers.put(0x4, () -> INC_B);
        opcodeHandlers.put(0x5, () -> DEC_B);
        opcodeHandlers.put(0x6, () -> LD_B,d8);
        // rotate bits right reg A
        opcodeHandlers.put(0x7, () -> RLCA);
        opcodeHandlers.put(0x8, () -> LD_(a16),SP);
        opcodeHandlers.put(0x9, () -> ADD_HL,BC);
        opcodeHandlers.put(0xa, () -> LD_A,(BC));
        opcodeHandlers.put(0xb, () -> DEC_BC);
        opcodeHandlers.put(0xc, () -> INC_C);
        opcodeHandlers.put(0xd, () -> DEC_C);
        opcodeHandlers.put(0xe, () -> LD_C,d8);
        opcodeHandlers.put(0xf, () -> RRCA);
        opcodeHandlers.put(0x10, () -> STOP);
        opcodeHandlers.put(0x11, () -> LD_DE,d16);
        opcodeHandlers.put(0x12, () -> LD_(DE),A);
        opcodeHandlers.put(0x13, () -> INC_DE);
        opcodeHandlers.put(0x14, () -> INC_D);
        opcodeHandlers.put(0x15, () -> DEC_D);
        opcodeHandlers.put(0x16, () -> LD_D,d8);
        // shift bits left reg A
        opcodeHandlers.put(0x17, () -> RLA);
        // jump to an offset by adding r8 to the current program counter to determing the address to jump to
        opcodeHandlers.put(0x18, () -> JR_r8);
        // add DE to HL and store result in HL
        opcodeHandlers.put(0x19, () -> ADD_HL,DE);
        opcodeHandlers.put(0x1a, () -> LD_A,(DE));
        opcodeHandlers.put(0x1b, () -> DEC_DE);
        opcodeHandlers.put(0x1c, () -> INC_E);
        opcodeHandlers.put(0x1d, () -> DEC_E);
        opcodeHandlers.put(0x1e, () -> LD_E,d8);
        // right shift reg A
        opcodeHandlers.put(0x1f, () -> RRA);
        // jump to new address if teh zero flag is not set
        opcodeHandlers.put(0x20, () -> JR_NZ,r8);
        opcodeHandlers.put(0x21, () -> LD_HL,d16);
        // increment HL after loading A into memory
        opcodeHandlers.put(0x22, () -> LD_(HL+),A);
        opcodeHandlers.put(0x23, () -> INC_HL);
        opcodeHandlers.put(0x24, () -> INC_H);
        opcodeHandlers.put(0x25, () -> DEC_H);
        opcodeHandlers.put(0x26, () -> LD_H,d8);
        // Decimal adjust accumulator. Adjusts a to the correct representation of a binary-coded decimal using the flags
        opcodeHandlers.put(0x27, () -> DAA);
        // jump to new address if zero flag is set
        opcodeHandlers.put(0x28, () -> JR_Z,r8);
        opcodeHandlers.put(0x29, () -> ADD_HL,HL);
        // load HL into A then increment it
        opcodeHandlers.put(0x2a, () -> LD_A,(HL+));
        opcodeHandlers.put(0x2b, () -> DEC_HL);
        opcodeHandlers.put(0x2c, () -> INC_L);
        opcodeHandlers.put(0x2d, () -> DEC_L);
        opcodeHandlers.put(0x2e, () -> LD_L,d8);
        opcodeHandlers.put(0x2f, () -> CPL);
        opcodeHandlers.put(0x30, () -> JR_NC,r8);
        opcodeHandlers.put(0x31, () -> LD_SP,d16);
        // load A into HL then DEC HL
        opcodeHandlers.put(0x32, () -> LD_(HL-),A);
        opcodeHandlers.put(0x33, () -> INC_SP);
        opcodeHandlers.put(0x34, () -> INC_(HL));
        opcodeHandlers.put(0x35, () -> DEC_(HL));
        opcodeHandlers.put(0x36, () -> LD_(HL),d8);
        // set carry flag to 1
        opcodeHandlers.put(0x37, () -> SCF);
        // perform relative jump if if the carry flag is set
        opcodeHandlers.put(0x38, () -> JR_C,r8);
        opcodeHandlers.put(0x39, () -> ADD_HL,SP);
        opcodeHandlers.put(0x3a, () -> LD_A,(HL-));
        opcodeHandlers.put(0x3b, () -> DEC_SP);
        opcodeHandlers.put(0x3c, () -> INC_A);
        opcodeHandlers.put(0x3d, () -> DEC_A);
        opcodeHandlers.put(0x3e, () -> LD_A,d8);
        // complement the carry flag
        opcodeHandlers.put(0x3f, () -> CCF);
        opcodeHandlers.put(0x40, () -> LD_B,B);
        opcodeHandlers.put(0x41, () -> LD_B,C);
        opcodeHandlers.put(0x42, () -> LD_B,D);
        opcodeHandlers.put(0x43, () -> LD_B,E);
        opcodeHandlers.put(0x44, () -> LD_B,H);
        opcodeHandlers.put(0x45, () -> LD_B,L);
        opcodeHandlers.put(0x46, () -> LD_B,(HL));
        opcodeHandlers.put(0x47, () -> LD_B,A);
        opcodeHandlers.put(0x48, () -> LD_C,B);
        opcodeHandlers.put(0x49, () -> LD_C,C);
        opcodeHandlers.put(0x4a, () -> LD_C,D);
        opcodeHandlers.put(0x4b, () -> LD_C,E);
        opcodeHandlers.put(0x4c, () -> LD_C,H);
        opcodeHandlers.put(0x4d, () -> LD_C,L);
        opcodeHandlers.put(0x4e, () -> LD_C,(HL));
        opcodeHandlers.put(0x4f, () -> LD_C,A);
        opcodeHandlers.put(0x50, () -> LD_D,B);
        opcodeHandlers.put(0x51, () -> LD_D,C);
        opcodeHandlers.put(0x52, () -> LD_D,D);
        opcodeHandlers.put(0x53, () -> LD_D,E);
        opcodeHandlers.put(0x54, () -> LD_D,H);
        opcodeHandlers.put(0x55, () -> LD_D,L);
        opcodeHandlers.put(0x56, () -> LD_D,(HL));
        opcodeHandlers.put(0x57, () -> LD_D,A);
        opcodeHandlers.put(0x58, () -> LD_E,B);
        opcodeHandlers.put(0x59, () -> LD_E,C);
        opcodeHandlers.put(0x5a, () -> LD_E,D);
        opcodeHandlers.put(0x5b, () -> LD_E,E);
        opcodeHandlers.put(0x5c, () -> LD_E,H);
        opcodeHandlers.put(0x5d, () -> LD_E,L);
        opcodeHandlers.put(0x5e, () -> LD_E,(HL));
        opcodeHandlers.put(0x5f, () -> LD_E,A);
        opcodeHandlers.put(0x60, () -> LD_H,B);
        opcodeHandlers.put(0x61, () -> LD_H,C);
        opcodeHandlers.put(0x62, () -> LD_H,D);
        opcodeHandlers.put(0x63, () -> LD_H,E);
        opcodeHandlers.put(0x64, () -> LD_H,H);
        opcodeHandlers.put(0x65, () -> LD_H,L);
        opcodeHandlers.put(0x66, () -> LD_H,(HL));
        opcodeHandlers.put(0x67, () -> LD_H,A);
        opcodeHandlers.put(0x68, () -> LD_L,B);
        opcodeHandlers.put(0x69, () -> LD_L,C);
        opcodeHandlers.put(0x6a, () -> LD_L,D);
        opcodeHandlers.put(0x6b, () -> LD_L,E);
        opcodeHandlers.put(0x6c, () -> LD_L,H);
        opcodeHandlers.put(0x6d, () -> LD_L,L);
        opcodeHandlers.put(0x6e, () -> LD_L,(HL));
        opcodeHandlers.put(0x6f, () -> LD_L,A);
        opcodeHandlers.put(0x70, () -> LD_(HL),B);
        opcodeHandlers.put(0x71, () -> LD_(HL),C);
        opcodeHandlers.put(0x72, () -> LD_(HL),D);
        opcodeHandlers.put(0x73, () -> LD_(HL),E);
        opcodeHandlers.put(0x74, () -> LD_(HL),H);
        opcodeHandlers.put(0x75, () -> LD_(HL),L);
        opcodeHandlers.put(0x76, () -> HALT);
        opcodeHandlers.put(0x77, () -> LD_(HL),A);
        opcodeHandlers.put(0x78, () -> LD_A,B);
        opcodeHandlers.put(0x79, () -> LD_A,C);
        opcodeHandlers.put(0x7a, () -> LD_A,D);
        opcodeHandlers.put(0x7b, () -> LD_A,E);
        opcodeHandlers.put(0x7c, () -> LD_A,H);
        opcodeHandlers.put(0x7d, () -> LD_A,L);
        opcodeHandlers.put(0x7e, () -> LD_A,(HL));
        opcodeHandlers.put(0x7f, () -> LD_A,A);
        opcodeHandlers.put(0x80, () -> ADD_A,B);
        opcodeHandlers.put(0x81, () -> ADD_A,C);
        opcodeHandlers.put(0x82, () -> ADD_A,D);
        opcodeHandlers.put(0x83, () -> ADD_A,E);
        opcodeHandlers.put(0x84, () -> ADD_A,H);
        opcodeHandlers.put(0x85, () -> ADD_A,L);
        opcodeHandlers.put(0x86, () -> ADD_A,(HL));
        opcodeHandlers.put(0x87, () -> ADD_A,A);
        opcodeHandlers.put(0x88, () -> ADC_A,B);
        opcodeHandlers.put(0x89, () -> ADC_A,C);
        opcodeHandlers.put(0x8a, () -> ADC_A,D);
        opcodeHandlers.put(0x8b, () -> ADC_A,E);
        // add contents of H into A and adds the value of the carry flag
        opcodeHandlers.put(0x8c, () -> ADC_A,H);
        opcodeHandlers.put(0x8d, () -> ADC_A,L);
        opcodeHandlers.put(0x8e, () -> ADC_A,(HL));
        opcodeHandlers.put(0x8f, () -> ADC_A,A);
        // subtract teh contents of B from contents of A and updates the carry and zero flags and half carry flag
        // and puts the value in A
        opcodeHandlers.put(0x90, () -> SUB_B);
        opcodeHandlers.put(0x91, () -> SUB_C);
        opcodeHandlers.put(0x92, () -> SUB_D);
        opcodeHandlers.put(0x93, () -> SUB_E);
        opcodeHandlers.put(0x94, () -> SUB_H);
        opcodeHandlers.put(0x95, () -> SUB_L);
        opcodeHandlers.put(0x96, () -> SUB_(HL));
        opcodeHandlers.put(0x97, () -> SUB_A);
        // subtracts contents of B and the carry flag from A stores it in A and updates the carry, zero, and half carry
        // flag
        opcodeHandlers.put(0x98, () -> SBC_B);
        opcodeHandlers.put(0x99, () -> SBC_C);
        opcodeHandlers.put(0x9a, () -> SBC_D);
        opcodeHandlers.put(0x9b, () -> SBC_E);
        opcodeHandlers.put(0x9c, () -> SBC_H);
        opcodeHandlers.put(0x9d, () -> SBC_L);
        opcodeHandlers.put(0x9e, () -> SBC_(HL));
        opcodeHandlers.put(0x9f, () -> SBC_A);
        // AND A and B
        opcodeHandlers.put(0xa0, () -> AND_B);
        opcodeHandlers.put(0xa1, () -> AND_C);
        opcodeHandlers.put(0xa2, () -> AND_D);
        opcodeHandlers.put(0xa3, () -> AND_E);
        opcodeHandlers.put(0xa4, () -> AND_H);
        opcodeHandlers.put(0xa5, () -> AND_L);
        opcodeHandlers.put(0xa6, () -> AND_(HL));
        opcodeHandlers.put(0xa7, () -> AND_A);
        // XOR B with A
        opcodeHandlers.put(0xa8, () -> XOR_B);
        opcodeHandlers.put(0xa9, () -> XOR_C);
        opcodeHandlers.put(0xaa, () -> XOR_D);
        opcodeHandlers.put(0xab, () -> XOR_E);
        opcodeHandlers.put(0xac, () -> XOR_H);
        opcodeHandlers.put(0xad, () -> XOR_L);
        opcodeHandlers.put(0xae, () -> XOR_(HL));
        opcodeHandlers.put(0xaf, () -> XOR_A);
        // or with A
        opcodeHandlers.put(0xb0, () -> OR_B);
        opcodeHandlers.put(0xb1, () -> OR_C);
        opcodeHandlers.put(0xb2, () -> OR_D);
        opcodeHandlers.put(0xb3, () -> OR_E);
        opcodeHandlers.put(0xb4, () -> OR_H);
        opcodeHandlers.put(0xb5, () -> OR_L);
        opcodeHandlers.put(0xb6, () -> OR_(HL));
        opcodeHandlers.put(0xb7, () -> OR_A);
        // compare A and B without modifying either. zero flag is set to 1 if if result is 0. Carry flag is set to 1 if
        // A is smaller than B abd 0 otherwise. Half carry flag is set to 1 if a carry occured from the lower nibble
        //Subtract flag is set to 1
        opcodeHandlers.put(0xb8, () -> CP_B);
        opcodeHandlers.put(0xb9, () -> CP_C);
        opcodeHandlers.put(0xba, () -> CP_D);
        opcodeHandlers.put(0xbb, () -> CP_E);
        opcodeHandlers.put(0xbc, () -> CP_H);
        opcodeHandlers.put(0xbd, () -> CP_L);
        opcodeHandlers.put(0xbe, () -> CP_(HL));
        opcodeHandlers.put(0xbf, () -> CP_A);
        // return to the address at the top of the stack
        opcodeHandlers.put(0xc0, () -> RET_NZ);
        // pop two bytes from the top of the stack and store them in BC
        opcodeHandlers.put(0xc1, () -> POP_BC);
        // jump if zero flag is not set
        opcodeHandlers.put(0xc2, () -> JP_NZ,a16);
        opcodeHandlers.put(0xc3, () -> JP_a16);
        // call a subroutine if zero flag is not set
        opcodeHandlers.put(0xc4, () -> CALL_NZ,a16);
        // push contents of BC onto the stack
        opcodeHandlers.put(0xc5, () -> PUSH_BC);
        opcodeHandlers.put(0xc6, () -> ADD_A,d8);
        // software reset. Moves the program counter to the start of the program by loading it with 0x00
        opcodeHandlers.put(0xc7, () -> RST_00H);
        // return from a subroutine by popping two bytes from the stack and loading them into the program counter. Only
        // executes if the zero flag is set
        opcodeHandlers.put(0xc8, () -> RET_Z);
        // returns from subroutine no matter what
        opcodeHandlers.put(0xc9, () -> RET);
        opcodeHandlers.put(0xca, () -> JP_Z,a16);
        // initiates a different set of opcodes that allows for more complex operations
        opcodeHandlers.put(0xcb, () -> new_CB());
        // call subroutine if zero flag is set
        opcodeHandlers.put(0xcc, () -> CALL_Z,a16);
        // call subroutine
        opcodeHandlers.put(0xcd, () -> CALL_a16);
        // ADD with carry on register A
        opcodeHandlers.put(0xce, () -> ADC_A,d8);
        // call a specific routine in memory located as address 0x08 that pushes the address of teh next instruction
        // onto the stack and transfers program control to the specified routine
        opcodeHandlers.put(0xcf, () -> RST_08H);
        // return from a subroutine if carry flag is not set
        opcodeHandlers.put(0xd0, () -> RET_NC);
        opcodeHandlers.put(0xd1, () -> POP_DE);
        // Jump if carry flag is not set
        opcodeHandlers.put(0xd2, () -> JP_NC,a16);
        // not a valid opcode
        opcodeHandlers.put(0xd3, () -> XXX);
        // call a subroutine if the carry flag is not set
        opcodeHandlers.put(0xd4, () -> CALL_NC,a16);
        opcodeHandlers.put(0xd5, () -> PUSH_DE);
        // subtract d8 from A
        opcodeHandlers.put(0xd6, () -> SUB_d8);
        // call routine located at 0x10
        opcodeHandlers.put(0xd7, () -> RST_10H);
        // returns control of the program after a call instruction if carry flag is set
        opcodeHandlers.put(0xd8, () -> RET_C);
        // return from interrupt
        opcodeHandlers.put(0xd9, () -> RETI);
        // jump if carry flag is set
        opcodeHandlers.put(0xda, () -> JP_C,a16);
        opcodeHandlers.put(0xdb, () -> XXX);
        // call subroutine at a16 is carry flag is set
        opcodeHandlers.put(0xdc, () -> CALL_C,a16);
        opcodeHandlers.put(0xdd, () -> XXX);
        // subtraction with carry then stored in register A. Zeroe, carry, half-carry, and negative flags are updated
        opcodeHandlers.put(0xde, () -> SBC_d8);
        // call subroutine at 0x18
        opcodeHandlers.put(0xdf, () -> RST_18H);*/
        // load contents of the memory address pointed to by a8 into the high0bytes of memory address $FF00 + a8 and
        // stores it into A
        /*opcodeHandlers.put(0xe0, () -> LDH_(a8),A);
        opcodeHandlers.put(0xe1, () -> POP_HL);
        opcodeHandlers.put(0xe2, () -> LD_(C),A);
        opcodeHandlers.put(0xe3, () -> XXX);
        opcodeHandlers.put(0xe4, () -> XXX);
        opcodeHandlers.put(0xe5, () -> PUSH_HL);
        // and d8 with A
        opcodeHandlers.put(0xe6, () -> AND_d8);
        // resets the program counter to address 0x0020
        opcodeHandlers.put(0xe7, () -> RST_20H);
        opcodeHandlers.put(0xe8, () -> ADD_SP,r8);
        // jumps to address stored in HL
        opcodeHandlers.put(0xe9, () -> JP_HL);
        opcodeHandlers.put(0xea, () -> LD_(a16),A);
        opcodeHandlers.put(0xeb, () -> XXX);
        opcodeHandlers.put(0xec, () -> XXX);
        opcodeHandlers.put(0xed, () -> XXX);
        // XOR d8 with A
        opcodeHandlers.put(0xee, () -> XOR_d8);
        // call to 0x28 saving the program counter to the stack so teh execution can resume after the routine is complete
        opcodeHandlers.put(0xef, () -> RST_28H);
        opcodeHandlers.put(0xf0, () -> LD_A,(a8));
        opcodeHandlers.put(0xf1, () -> POP_AF);
        opcodeHandlers.put(0xf2, () -> LD_A,(C));
        // disables interrupts
        opcodeHandlers.put(0xf3, () -> DI);
        opcodeHandlers.put(0xf4, () -> XXX);
        opcodeHandlers.put(0xf5, () -> PUSH_AF);
        // OR A and d8. I think it is inclusive
        opcodeHandlers.put(0xf6, () -> OR_d8);
        // call to 0x30
        opcodeHandlers.put(0xf7, () -> RST_30H);
        opcodeHandlers.put(0xf8, () -> LD_HL,SP+r8);
        opcodeHandlers.put(0xf9, () -> LD_SP,HL);
        opcodeHandlers.put(0xfa, () -> LD_A,(a16));
        // enables interrupts
        opcodeHandlers.put(0xfb, () -> EI);
        opcodeHandlers.put(0xfc, () -> XXX);
        opcodeHandlers.put(0xfd, () -> XXX);
        opcodeHandlers.put(0xfe, () -> CP_d8);
        // call to 0x28
        opcodeHandlers.put(0xff, () -> RST_38H);*/


        String[] operations = new String[256];
        operations[0x0] = "NOP";
        operations[0x1] = "LD BC,d16";
        operations[0x2] = "LD (BC),A";
        operations[0x3] = "INC BC";
        operations[0x4] = "INC B";
        operations[0x5] = "DEC B";
        operations[0x6] = "LD B,d8";
        operations[0x7] = "RLCA";
        operations[0x8] = "LD (a16),SP";
        operations[0x9] = "ADD HL,BC";
        operations[0xa] = "LD A,(BC)";
        operations[0xb] = "DEC BC";
        operations[0xc] = "INC C";
        operations[0xd] = "DEC C";
        operations[0xe] = "LD C,d8";
        operations[0xf] = "RRCA";
        operations[0x10] = "STOP";
        operations[0x11] = "LD DE,d16";
        operations[0x12] = "LD (DE),A";
        operations[0x13] = "INC DE";
        operations[0x14] = "INC D";
        operations[0x15] = "DEC D";
        operations[0x16] = "LD D,d8";
        operations[0x17] = "RLA";
        operations[0x18] = "JR r8";
        operations[0x19] = "ADD HL,DE";
        operations[0x1a] = "LD A,(DE)";
        operations[0x1b] = "DEC DE";
        operations[0x1c] = "INC E";
        operations[0x1d] = "DEC E";
        operations[0x1e] = "LD E,d8";
        operations[0x1f] = "RRA";
        operations[0x20] = "JR NZ,r8";
        operations[0x21] = "LD HL,d16";
        operations[0x22] = "LD (HL+),A";
        operations[0x23] = "INC HL";
        operations[0x24] = "INC H";
        operations[0x25] = "DEC H";
        operations[0x26] = "LD H,d8";
        operations[0x27] = "DAA";
        operations[0x28] = "JR Z,r8";
        operations[0x29] = "ADD HL,HL";
        operations[0x2a] = "LD A,(HL+)";
        operations[0x2b] = "DEC HL";
        operations[0x2c] = "INC L";
        operations[0x2d] = "DEC L";
        operations[0x2e] = "LD L,d8";
        operations[0x2f] = "CPL";
        operations[0x30] = "JR NC,r8";
        operations[0x31] = "LD SP,d16";
        operations[0x32] = "LD (HL-),A";
        operations[0x33] = "INC SP";
        operations[0x34] = "INC (HL)";
        operations[0x35] = "DEC (HL)";
        operations[0x36] = "LD (HL),d8";
        operations[0x37] = "SCF";
        operations[0x38] = "JR C(cond),r8";
        operations[0x39] = "ADD HL,SP";
        operations[0x3a] = "LD A,(HL-)";
        operations[0x3b] = "DEC SP";
        operations[0x3c] = "INC A";
        operations[0x3d] = "DEC A";
        operations[0x3e] = "LD A,d8";
        operations[0x3f] = "CCF";
        operations[0x40] = "LD B,B";
        operations[0x41] = "LD B,C";
        operations[0x42] = "LD B,D";
        operations[0x43] = "LD B,E";
        operations[0x44] = "LD B,H";
        operations[0x45] = "LD B,L";
        operations[0x46] = "LD B,(HL)";
        operations[0x47] = "LD B,A";
        operations[0x48] = "LD C,B";
        operations[0x49] = "LD C,C";
        operations[0x4a] = "LD C,D";
        operations[0x4b] = "LD C,E";
        operations[0x4c] = "LD C,H";
        operations[0x4d] = "LD C,L";
        operations[0x4e] = "LD C,(HL)";
        operations[0x4f] = "LD C,A";
        operations[0x50] = "LD D,B";
        operations[0x51] = "LD D,C";
        operations[0x52] = "LD D,D";
        operations[0x53] = "LD D,E";
        operations[0x54] = "LD D,H";
        operations[0x55] = "LD D,L";
        operations[0x56] = "LD D,(HL)";
        operations[0x57] = "LD D,A";
        operations[0x58] = "LD E,B";
        operations[0x59] = "LD E,C";
        operations[0x5a] = "LD E,D";
        operations[0x5b] = "LD E,E";
        operations[0x5c] = "LD E,H";
        operations[0x5d] = "LD E,L";
        operations[0x5e] = "LD E,(HL)";
        operations[0x5f] = "LD E,A";
        operations[0x60] = "LD H,B";
        operations[0x61] = "LD H,C";
        operations[0x62] = "LD H,D";
        operations[0x63] = "LD H,E";
        operations[0x64] = "LD H,H";
        operations[0x65] = "LD H,L";
        operations[0x66] = "LD H,(HL)";
        operations[0x67] = "LD H,A";
        operations[0x68] = "LD L,B";
        operations[0x69] = "LD L,C";
        operations[0x6a] = "LD L,D";
        operations[0x6b] = "LD L,E";
        operations[0x6c] = "LD L,H";
        operations[0x6d] = "LD L,L";
        operations[0x6e] = "LD L,(HL)";
        operations[0x6f] = "LD L,A";
        operations[0x70] = "LD (HL),B";
        operations[0x71] = "LD (HL),C";
        operations[0x72] = "LD (HL),D";
        operations[0x73] = "LD (HL),E";
        operations[0x74] = "LD (HL),H";
        operations[0x75] = "LD (HL),L";
        operations[0x76] = "HALT";
        operations[0x77] = "LD (HL),A";
        operations[0x78] = "LD A,B";
        operations[0x79] = "LD A,C";
        operations[0x7a] = "LD A,D";
        operations[0x7b] = "LD A,E";
        operations[0x7c] = "LD A,H";
        operations[0x7d] = "LD A,L";
        operations[0x7e] = "LD A,(HL)";
        operations[0x7f] = "LD A,A";
        operations[0x80] = "ADD A,B";
        operations[0x81] = "ADD A,C";
        operations[0x82] = "ADD A,D";
        operations[0x83] = "ADD A,E";
        operations[0x84] = "ADD A,H";
        operations[0x85] = "ADD A,L";
        operations[0x86] = "ADD A,(HL)";
        operations[0x87] = "ADD A,A";
        operations[0x88] = "ADC A,B";
        operations[0x89] = "ADC A,C";
        operations[0x8a] = "ADC A,D";
        operations[0x8b] = "ADC A,E";
        operations[0x8c] = "ADC A,H";
        operations[0x8d] = "ADC A,L";
        operations[0x8e] = "ADC A,(HL)";
        operations[0x8f] = "ADC A,A";
        operations[0x90] = "SUB B";
        operations[0x91] = "SUB C";
        operations[0x92] = "SUB D";
        operations[0x93] = "SUB E";
        operations[0x94] = "SUB H";
        operations[0x95] = "SUB L";
        operations[0x96] = "SUB (HL)";
        operations[0x97] = "SUB A";
        operations[0x98] = "SBC B";
        operations[0x99] = "SBC C";
        operations[0x9a] = "SBC D";
        operations[0x9b] = "SBC E";
        operations[0x9c] = "SBC H";
        operations[0x9d] = "SBC L";
        operations[0x9e] = "SBC (HL)";
        operations[0x9f] = "SBC A";
        operations[0xa0] = "AND B";
        operations[0xa1] = "AND C";
        operations[0xa2] = "AND D";
        operations[0xa3] = "AND E";
        operations[0xa4] = "AND H";
        operations[0xa5] = "AND L";
        operations[0xa6] = "AND (HL)";
        operations[0xa7] = "AND A";
        operations[0xa8] = "XOR B";
        operations[0xa9] = "XOR C";
        operations[0xaa] = "XOR D";
        operations[0xab] = "XOR E";
        operations[0xac] = "XOR H";
        operations[0xad] = "XOR L";
        operations[0xae] = "XOR (HL)";
        operations[0xaf] = "XOR A";
        operations[0xb0] = "OR B";
        operations[0xb1] = "OR C";
        operations[0xb2] = "OR D";
        operations[0xb3] = "OR E";
        operations[0xb4] = "OR H";
        operations[0xb5] = "OR L";
        operations[0xb6] = "OR (HL)";
        operations[0xb7] = "OR A";
        operations[0xb8] = "CP B";
        operations[0xb9] = "CP C";
        operations[0xba] = "CP D";
        operations[0xbb] = "CP E";
        operations[0xbc] = "CP H";
        operations[0xbd] = "CP L";
        operations[0xbe] = "CP (HL)";
        operations[0xbf] = "CP A";
        operations[0xc0] = "RET NZ";
        operations[0xc1] = "POP BC";
        operations[0xc2] = "JP NZ,a16";
        operations[0xc3] = "JP a16";
        operations[0xc4] = "CALL NZ,a16";
        operations[0xc5] = "PUSH BC";
        operations[0xc6] = "ADD A,d8";
        operations[0xc7] = "RST 00H";
        operations[0xc8] = "RET Z";
        operations[0xc9] = "RET";
        operations[0xca] = "JP Z,a16";
        operations[0xcb] = "new CB()?????";
        operations[0xcc] = "CALL Z,a16";
        operations[0xcd] = "CALL a16";
        operations[0xce] = "ADC A,d8";
        operations[0xcf] = "RST 08H";
        operations[0xd0] = "RET NC";
        operations[0xd1] = "POP DE";
        operations[0xd2] = "JP NC,a16";
        operations[0xd3] = "XXX";
        operations[0xd4] = "CALL NC,a16";
        operations[0xd5] = "PUSH DE";
        operations[0xd6] = "SUB d8";
        operations[0xd7] = "RST 10H";
        operations[0xd8] = "RET C(cond)";
        operations[0xd9] = "RETI";
        operations[0xda] = "JP C(cond),a16";
        operations[0xdb] = "XXX";
        operations[0xdc] = "CALL C(cond),a16";
        operations[0xdd] = "XXX";
        operations[0xde] = "SBC d8";
        operations[0xdf] = "RST 18H";
        operations[0xe0] = "LD (a8),A";
        operations[0xe1] = "POP HL";
        operations[0xe2] = "LD (C),A";
        operations[0xe3] = "XXX";
        operations[0xe4] = "XXX";
        operations[0xe5] = "PUSH HL";
        operations[0xe6] = "AND d8";
        operations[0xe7] = "RST 20H";
        operations[0xe8] = "ADD SP,r8";
        operations[0xe9] = "JP HL";
        operations[0xea] = "LD (a16),A";
        operations[0xeb] = "XXX";
        operations[0xec] = "XXX";
        operations[0xed] = "XXX";
        operations[0xee] = "XOR d8";
        operations[0xef] = "RST 28H";
        operations[0xf0] = "LD A,(a8)";
        operations[0xf1] = "POP AF";
        operations[0xf2] = "LD A,(C)";
        operations[0xf3] = "DI";
        operations[0xf4] = "XXX";
        operations[0xf5] = "PUSH AF";
        operations[0xf6] = "OR d8";
        operations[0xf7] = "RST 30H";
        operations[0xf8] = "LD HL,SP+r8";
        operations[0xf9] = "LD SP,HL";
        operations[0xfa] = "LD A,(a16)";
        operations[0xfb] = "EI";
        operations[0xfc] = "XXX";
        operations[0xfd] = "XXX";
        operations[0xfe] = "CP d8";
        operations[0xff] = "RST 38H";
        int i = 0;
        // System.out.println("print OPcodes");


        while (i < romData.length) {
            // fetch opcode
            int opcode = (romData[i] & 0xff) | ((romData[i + 1] & 0xff) << 8);
            //
            Runnable handler = opcodeHandlers.get(opcode & 0xff);
            if (handler != null) {
                handler.run();
            }
            // increment by two because we are reading pairs of bytes
            i += 2;
        }
    }

    public void nop(){
        System.out.println("nop");
    }

}


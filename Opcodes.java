import Memory.Memory;

import java.util.HashMap;
import java.util.Map;

public class Opcodes {
    Registers regs;
    byte[] romData;
    InterruptManager interruptmanager;
    CPU cpu;
    Memory mem;// Can change just place holding with small features just to make Opcode
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
        opcodeHandlers.put(0x7, () -> RLCA());
        opcodeHandlers.put(0x8, () -> LD(mem.readWord(regs.getPC() + 1), "sp", 3));
        opcodeHandlers.put(0x9, () -> ADD("hl", "bc"));// _HL,BC);
        opcodeHandlers.put(0xa, () -> LD("a", mem.readByte(regs.getBC()), 1));
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
        opcodeHandlers.put(0x17, () -> RLA());
        opcodeHandlers.put(0x18, () -> JR(mem.readWord(regs.getPC() + 1)));
        opcodeHandlers.put(0x19, () -> ADD("hl", "de"));// _HL,DE);
        opcodeHandlers.put(0x1a, () -> LD("a", mem.readByte(regs.getDE()), 1));// A,(DE));
        opcodeHandlers.put(0x1b, () -> DEC("de"));
        opcodeHandlers.put(0x1c, () -> INC("e"));
        opcodeHandlers.put(0x1d, () -> DEC("e"));
        opcodeHandlers.put(0x1e, () -> LDu("e", mem.readByte(regs.getPC() + 1), 2));// _E,d8);
        opcodeHandlers.put(0x1f, () -> RR()); // opcode is RRA
        opcodeHandlers.put(0x20, () -> JRNZ((byte) mem.readByte(regs.getPC() + 1))); // r8);
        opcodeHandlers.put(0x21, () -> LDu("hl", mem.readWord(regs.getPC() + 1), 3));
        opcodeHandlers.put(0x22, () -> LDINCDECHL("LD_(HL+),A)"));
        opcodeHandlers.put(0x23, () -> INC("hl"));
        opcodeHandlers.put(0x24, () -> INC("h"));
        opcodeHandlers.put(0x25, () -> DEC("h"));
        opcodeHandlers.put(0x26, () -> LDu("h", mem.readByte(regs.getPC() + 1), 2));// H,d8);
        opcodeHandlers.put(0x27, () -> DAA());
        opcodeHandlers.put(0x28, () -> JRZ(mem.readByte(regs.getPC() + 1)));// ,r8);
        opcodeHandlers.put(0x29, () -> ADD("hl", "hl")); // ADD_HL,HL);
        opcodeHandlers.put(0x2a, () -> LDINCDECHL("LD_A,(HL+)")); // LD_A,(HL+)
        opcodeHandlers.put(0x2b, () -> DEC("hl")); // decrement value of hl
        opcodeHandlers.put(0x2c, () -> INC("l"));
        opcodeHandlers.put(0x2d, () -> DEC("l"));
        opcodeHandlers.put(0x2e, () -> LDu("l", mem.readByte(regs.getPC() + 1), 2));
        opcodeHandlers.put(0x2f, () -> CPL());
        opcodeHandlers.put(0x30, () -> JRNC(mem.readByte(regs.getPC() + 1)));// ,r8);
        opcodeHandlers.put(0x31, () -> LDu("sp", mem.readWord(regs.getPC() + 1), 3));// SP,d16);
        opcodeHandlers.put(0x32, () -> LDINCDECHL("LD_(HL-),A)"));
        opcodeHandlers.put(0x33, () -> INC("sp"));
        opcodeHandlers.put(0x34, () -> INC("(hl)")); // (hl) means the memory value at hl
        opcodeHandlers.put(0x35, () -> DEC("(hl)"));
        opcodeHandlers.put(0x36, () -> LD(regs.getHL(), mem.readByte(regs.getPC() + 1), 2));// _(HL),d8);
        opcodeHandlers.put(0x37, () -> SCF());
        opcodeHandlers.put(0x3a, () -> LDINCDECHL("LD_A,(HL-)"));
        opcodeHandlers.put(0x3b, () -> DEC("sp"));
        opcodeHandlers.put(0x3c, () -> INC("a"));
        opcodeHandlers.put(0x3d, () -> DEC("a"));
        opcodeHandlers.put(0x3e, () -> LDu("a", mem.readByte(regs.getPC() + 1), 2));
        opcodeHandlers.put(0x3f, () -> CCF());
        opcodeHandlers.put(0x40, () -> LD("b", regs.getB(), 1));
        opcodeHandlers.put(0x41, () -> LD("b", regs.getC(), 1));
        opcodeHandlers.put(0x42, () -> LD("b", regs.getD(), 1));// LD_B,D);
        opcodeHandlers.put(0x43, () -> LD("b", regs.getE(), 1));// LD_B,E);
        opcodeHandlers.put(0x44, () -> LD("b", regs.getH(), 1));// LD_B,H);
        opcodeHandlers.put(0x45, () -> LD("b", regs.getL(), 1));// LD_B,L);
        opcodeHandlers.put(0x46, () -> LD("b", mem.readByte(regs.getHL()), 1));// LD_B,(HL));
        opcodeHandlers.put(0x47, () -> LD("b", regs.getA(), 1));
        opcodeHandlers.put(0x48, () -> LD("c", regs.getB(), 1));// LD_C,B);
        opcodeHandlers.put(0x49, () -> LD("c", regs.getC(), 1));
        opcodeHandlers.put(0x4a, () -> LD("c", regs.getD(), 1));
        opcodeHandlers.put(0x4b, () -> LD("c", regs.getE(), 1));
        opcodeHandlers.put(0x4c, () -> LD("c", regs.getH(), 1));
        opcodeHandlers.put(0x4d, () -> LD("c", regs.getL(), 1));
        opcodeHandlers.put(0x4e, () -> LD("c", mem.readByte(regs.getHL()), 1));
        opcodeHandlers.put(0x4f, () -> LD("c", regs.getA(), 1));
        opcodeHandlers.put(0x50, () -> LD("d", regs.getB(), 1));
        opcodeHandlers.put(0x51, () -> LD("d", regs.getC(), 1));
        opcodeHandlers.put(0x52, () -> LD("d", regs.getD(), 1));
        opcodeHandlers.put(0x53, () -> LD("d", regs.getE(), 1));
        opcodeHandlers.put(0x54, () -> LD("d", regs.getH(), 1));
        opcodeHandlers.put(0x55, () -> LD("d", regs.getL(), 1));
        opcodeHandlers.put(0x56, () -> LD("d", mem.readByte(regs.getHL()), 1));
        opcodeHandlers.put(0x57, () -> LD("d", regs.getA(), 1));
        opcodeHandlers.put(0x58, () -> LD("e", regs.getB(), 1));
        opcodeHandlers.put(0x59, () -> LD("e", regs.getC(), 1));
        opcodeHandlers.put(0x5a, () -> LD("e", regs.getD(), 1));
        opcodeHandlers.put(0x5b, () -> LD("e", regs.getE(), 1));
        opcodeHandlers.put(0x5c, () -> LD("e", regs.getH(), 1));
        opcodeHandlers.put(0x5d, () -> LD("e", regs.getL(), 1));
        opcodeHandlers.put(0x5e, () -> LD("e", mem.readByte(regs.getHL()), 1));
        opcodeHandlers.put(0x5f, () -> LD("e", regs.getA(), 1));
        opcodeHandlers.put(0x60, () -> LD("h", regs.getB(), 1));
        opcodeHandlers.put(0x61, () -> LD("h", regs.getC(), 1));
        opcodeHandlers.put(0x62, () -> LD("h", regs.getD(), 1));
        opcodeHandlers.put(0x63, () -> LD("h", regs.getE(), 1));
        opcodeHandlers.put(0x64, () -> LD("h", regs.getH(), 1));
        opcodeHandlers.put(0x65, () -> LD("h", regs.getL(), 1));
        opcodeHandlers.put(0x66, () -> LD("h", mem.readByte(regs.getHL()), 1));
        opcodeHandlers.put(0x67, () -> LD("h", regs.getA(), 1));
        opcodeHandlers.put(0x68, () -> LD("l", regs.getB(), 1));
        opcodeHandlers.put(0x69, () -> LD("l", regs.getC(), 1));
        opcodeHandlers.put(0x6a, () -> LD("l", regs.getD(), 1));
        opcodeHandlers.put(0x6b, () -> LD("l", regs.getE(), 1));
        opcodeHandlers.put(0x6c, () -> LD("l", regs.getH(), 1));
        opcodeHandlers.put(0x6d, () -> LD("l", regs.getL(), 1));
        opcodeHandlers.put(0x6e, () -> LD("l", mem.readByte(regs.getHL()), 1));
        opcodeHandlers.put(0x6f, () -> LD("l", regs.getA(), 1));
        opcodeHandlers.put(0x70, () -> LD(regs.getHL(), "b", 1));// _(HL),B);
        opcodeHandlers.put(0x71, () -> LD(regs.getHL(), "c", 1));// _(HL),C);
        opcodeHandlers.put(0x72, () -> LD(regs.getHL(), "d", 1));// _(HL),D);
        opcodeHandlers.put(0x73, () -> LD(regs.getHL(), "e", 1));// _(HL),E);
        opcodeHandlers.put(0x74, () -> LD(regs.getHL(), "h", 1));// _(HL),H);
        opcodeHandlers.put(0x75, () -> LD(regs.getHL(), "l", 1));// _(HL),L);
        opcodeHandlers.put(0x76, () -> HALT());
        opcodeHandlers.put(0x77, () -> LD(regs.getHL(), "a", 1));
        opcodeHandlers.put(0x78, () -> LD("a", regs.getB(), 1));
        opcodeHandlers.put(0x79, () -> LD("a", regs.getC(), 1));
        opcodeHandlers.put(0x7a, () -> LD("a", regs.getD(), 1));
        opcodeHandlers.put(0x7b, () -> LD("a", regs.getE(), 1));
        opcodeHandlers.put(0x7c, () -> LD("a", regs.getH(), 1));
        opcodeHandlers.put(0x7d, () -> LD("a", regs.getL(), 1));
        opcodeHandlers.put(0x7e, () -> LD("a", mem.readByte(regs.getHL()), 1));
        opcodeHandlers.put(0x7f, () -> LD("a", regs.getA(), 1));
        opcodeHandlers.put(0x80, () -> ADD("a", "b")); // ADD_A,B);
        opcodeHandlers.put(0x81, () -> ADD("a", "c")); // ADD_A,C);
        opcodeHandlers.put(0x82, () -> ADD("a", "d")); // ADD_A,D);
        opcodeHandlers.put(0x83, () -> ADD("a", "e")); // ADD_A,E);
        opcodeHandlers.put(0x84, () -> ADD("a", "h")); // ADD_A,H);
        opcodeHandlers.put(0x85, () -> ADD("a", "l")); // ADD_A,L);
        opcodeHandlers.put(0x86, () -> ADD("a", "(hl)")); // ADD_A,(HL));
        opcodeHandlers.put(0x87, () -> ADD("a", "a")); // ADD_A,A);
        opcodeHandlers.put(0x88, () -> ADC("b"));// _A, B);
        opcodeHandlers.put(0x89, () -> ADC("c"));// _A, C);
        opcodeHandlers.put(0x8a, () -> ADC("d"));// _A, D);
        opcodeHandlers.put(0x8b, () -> ADC("e"));// _A, E);
        opcodeHandlers.put(0x8c, () -> ADC("h"));// _A, H);
        opcodeHandlers.put(0x8d, () -> ADC("l"));// _A, L);
        opcodeHandlers.put(0x8e, () -> ADC(mem.readByte(regs.getHL())));// _A, (HL));
        opcodeHandlers.put(0x8f, () -> ADC("a"));// _A, A);
        opcodeHandlers.put(0x90, () -> SUB("b")); // SUB_B);
        opcodeHandlers.put(0x91, () -> SUB("c")); // SUB_C);
        opcodeHandlers.put(0x92, () -> SUB("d")); // SUB_D);
        opcodeHandlers.put(0x93, () -> SUB("e")); // SUB_E);
        opcodeHandlers.put(0x94, () -> SUB("h")); // SUB_H);
        opcodeHandlers.put(0x95, () -> SUB("l")); // SUB_L);
        opcodeHandlers.put(0x96, () -> SUB("(hl)")); // SUB_(HL));
        opcodeHandlers.put(0x97, () -> SUB("a")); // SUB_A);
        opcodeHandlers.put(0x98, () -> SBC("b")); // SBC_B);
        opcodeHandlers.put(0x99, () -> SBC("c")); // SBC_C);
        opcodeHandlers.put(0x9a, () -> SBC("d")); // SBC_D);
        opcodeHandlers.put(0x9b, () -> SBC("e")); // SBC_E);
        opcodeHandlers.put(0x9c, () -> SBC("h")); // SBC_H);
        opcodeHandlers.put(0x9d, () -> SBC("l")); // SBC_L);
        opcodeHandlers.put(0x9e, () -> SBC("(HL)")); // SBC_(HL));
        opcodeHandlers.put(0x9f, () -> SBC("a")); // SBC_A);
        opcodeHandlers.put(0xa0, () -> AND("b")); // AND_B);
        opcodeHandlers.put(0xa1, () -> AND("c")); // AND_C);
        opcodeHandlers.put(0xa2, () -> AND("d")); // AND_D);
        opcodeHandlers.put(0xa3, () -> AND("e")); // AND_E);
        opcodeHandlers.put(0xa4, () -> AND("h")); // AND_H);
        opcodeHandlers.put(0xa5, () -> AND("l")); // AND_L);
        opcodeHandlers.put(0xa6, () -> AND("(hl)")); // AND_(HL));
        opcodeHandlers.put(0xa7, () -> AND("a")); // AND_A);
        opcodeHandlers.put(0xa8, () -> XOR("b"));
        opcodeHandlers.put(0xa9, () -> XOR("c"));
        opcodeHandlers.put(0xaa, () -> XOR("d"));
        opcodeHandlers.put(0xab, () -> XOR("e"));
        opcodeHandlers.put(0xac, () -> XOR("h"));
        opcodeHandlers.put(0xad, () -> XOR("l"));
        opcodeHandlers.put(0xae, () -> XOR("hl"));
        opcodeHandlers.put(0xaf, () -> XOR("a"));
        opcodeHandlers.put(0xb0, () -> OR("b"));
        opcodeHandlers.put(0xb1, () -> OR("c"));
        opcodeHandlers.put(0xb2, () -> OR("d"));
        opcodeHandlers.put(0xb3, () -> OR("e"));
        opcodeHandlers.put(0xb4, () -> OR("h"));
        opcodeHandlers.put(0xb5, () -> OR("l"));
        opcodeHandlers.put(0xb6, () -> OR("hl"));
        opcodeHandlers.put(0xb7, () -> OR("a"));
        opcodeHandlers.put(0xb8, () -> CP("b"));
        opcodeHandlers.put(0xb9, () -> CP("c"));
        opcodeHandlers.put(0xba, () -> CP("d"));
        opcodeHandlers.put(0xbb, () -> CP("e"));
        opcodeHandlers.put(0xbc, () -> CP("h"));
        opcodeHandlers.put(0xbd, () -> CP("l"));
        opcodeHandlers.put(0xbe, () -> CP("hl"));
        opcodeHandlers.put(0xbf, () -> CP("a"));
        opcodeHandlers.put(0xc0, () -> RETNZ());
        opcodeHandlers.put(0xc1, () -> POP("bc"));
        opcodeHandlers.put(0xc2, () -> JPNZ(mem.readWord(regs.getPC() + 1)));// ,a16);
        opcodeHandlers.put(0xc3, () -> JP(mem.readWord(regs.getPC() + 1)));
        opcodeHandlers.put(0xc4, () -> CALLNZ(mem.readWord(regs.getPC() + 1), !regs.fByte.checkZ()));// ,a16);
        opcodeHandlers.put(0xc5, () -> PUSH("bc"));// _BC);
        opcodeHandlers.put(0xc6, () -> ADD("a", "d8"));// ADD_A,d8);
        opcodeHandlers.put(0xc7, () -> RST(00));
        opcodeHandlers.put(0xc8, () -> RETZ());
        opcodeHandlers.put(0xc9, () -> RET());
        opcodeHandlers.put(0xca, () -> JPZ(mem.readWord(regs.getPC() + 1)));// ,a16);
        // read the next byte in memory and use that as the opcode
        opcodeHandlers.put(0xcb, () -> NEW_CB());//Send to new table or catch before
        // operation.run()?
        opcodeHandlers.put(0xcc, () -> CALLZ(mem.readWord(regs.getPC() + 1), regs.fByte.checkZ()));// ,a16);
        opcodeHandlers.put(0xcd, () -> CALL(mem.readWord(regs.getPC() + 1)));// _a16);
        opcodeHandlers.put(0xce, () -> ADC(mem.readByte(regs.getPC() + 1)));// _A,d8);
        opcodeHandlers.put(0xcf, () -> RST(0x08));
        opcodeHandlers.put(0xd0, () -> RETNC());
        opcodeHandlers.put(0xd1, () -> POP("de"));
        opcodeHandlers.put(0xd2, () -> JPNC(mem.readWord(regs.getPC() + 1)));
        // opcodeHandlers.put(0xd3, () -> XXX);
        opcodeHandlers.put(0xd4, () -> CALLNC(mem.readWord(regs.getPC() + 1), !regs.fByte.checkC()));
        opcodeHandlers.put(0xd5, () -> PUSH("de"));
        opcodeHandlers.put(0xd6, () -> SUB(mem.readByte(regs.getPC() + 1)));
        opcodeHandlers.put(0xd7, () -> RST(0x10));
        opcodeHandlers.put(0xd8, () -> RETC());
        opcodeHandlers.put(0xd9, () -> RETI());
        opcodeHandlers.put(0xda, () -> JPC(mem.readWord(regs.getPC() + 1)));
        // opcodeHandlers.put(0xdb, () -> XXX);
        opcodeHandlers.put(0xdc, () -> CALLC(mem.readWord(regs.getPC() + 1), regs.fByte.checkC()));// ,a16);
        // opcodeHandlers.put(0xdd, () -> XXX);
        opcodeHandlers.put(0xde, () -> SBC(mem.readByte(regs.getPC() + 1)));// d8
        opcodeHandlers.put(0xdf, () -> RST(0x18));// RST 18
        opcodeHandlers.put(0xe0, () -> LD(0xFF00 + mem.readByte(regs.getPC() + 1), "a", 2));// _(a8),A);
        opcodeHandlers.put(0xe1, () -> POP("hl"));
        opcodeHandlers.put(0xe2, () -> LD(0xFF00 + regs.getC(), "a", 1));// _(C),A);
        // opcodeHandlers.put(0xe3, () -> XXX);
        // opcodeHandlers.put(0xe4, () -> XXX);
        opcodeHandlers.put(0xe5, () -> PUSH("hl"));// _HL);
        opcodeHandlers.put(0xe6, () -> AND(mem.readByte(regs.getPC() + 1)));// _d8);
        opcodeHandlers.put(0xe7, () -> RST(0x20));
        opcodeHandlers.put(0xe8, () -> ADD("sp", "i8"));
        opcodeHandlers.put(0xe9, () -> JP(regs.getHL()));
        opcodeHandlers.put(0xea, () -> LD(mem.readWord(regs.getPC() + 1), "a", 3));// _(a16),A);
        // opcodeHandlers.put(0xeb, () -> XXX);
        // opcodeHandlers.put(0xec, () -> XXX);
        // opcodeHandlers.put(0xed, () -> XXX);
        opcodeHandlers.put(0xee, () -> XOR(mem.readByte(regs.getPC() + 1)));// _d8);
        opcodeHandlers.put(0xef, () -> RST(0x28));
        opcodeHandlers.put(0xf0, () -> LDu("a", mem.readByte(0xFF00 + mem.readByte(regs.getPC() + 1)), 2));// A,(a8));
        opcodeHandlers.put(0xf1, () -> POP("af"));
        opcodeHandlers.put(0xf2, () -> LDu("a", mem.readByte(0xFF00 + regs.getC()), 1));
        opcodeHandlers.put(0xf3, () -> DI());
        opcodeHandlers.put(0xf4, () -> nop());
        opcodeHandlers.put(0xf5, () -> PUSH("af"));
        opcodeHandlers.put(0xf6, () -> OR(mem.readByte(regs.getPC() + 1)));
        opcodeHandlers.put(0xf7, () -> RST(0x30));
        opcodeHandlers.put(0xf8, () -> LDu("hl", regs.getSP() + (byte) mem.readByte(regs.getPC() + 1), 2));// _HL,SP+r8);
        opcodeHandlers.put(0xf9, () -> LD("sp", regs.getHL(), 1));// _SP,HL);
        opcodeHandlers.put(0xfa, () -> LDu("a", mem.readWord(regs.getPC() + 1), 3));// _A,(a16));
        opcodeHandlers.put(0xfb, () -> EI());
        opcodeHandlers.put(0xfc, () -> nop());
        opcodeHandlers.put(0xfd, () -> nop());
        opcodeHandlers.put(0xfe, () -> CP(mem.readByte(regs.getPC() + 1)));
        opcodeHandlers.put(0xff, () -> RST(0x38));//
    }

    // map for the extended opcodes
    public Map<Integer, Runnable> extendedOpcodeHandlers = new HashMap<>();
    {
        extendedOpcodeHandlers.put(0x0, () -> RLC("b"));
        extendedOpcodeHandlers.put(0x1, () -> RLC("c"));
        extendedOpcodeHandlers.put(0x2, () -> RLC("d"));
        extendedOpcodeHandlers.put(0x3, () -> RLC("e"));
        extendedOpcodeHandlers.put(0x4, () -> RLC("h"));
        extendedOpcodeHandlers.put(0x5, () -> RLC("l"));
        extendedOpcodeHandlers.put(0x6, () -> RLC("(hl)"));
        extendedOpcodeHandlers.put(0x7, () -> RLC("a"));
        extendedOpcodeHandlers.put(0x8, () -> RRC("b"));
        extendedOpcodeHandlers.put(0x9, () -> RRC("c"));
        extendedOpcodeHandlers.put(0xA, () -> RRC("d"));
        extendedOpcodeHandlers.put(0xB, () -> RRC("e"));
        extendedOpcodeHandlers.put(0xC, () -> RRC("h"));
        extendedOpcodeHandlers.put(0xD, () -> RRC("l"));
        extendedOpcodeHandlers.put(0xE, () -> RRC("hl"));
        extendedOpcodeHandlers.put(0xF, () -> RRC("a"));
        extendedOpcodeHandlers.put(0x10, () -> RL("b"));
        extendedOpcodeHandlers.put(0x11, () -> RL("c"));
        extendedOpcodeHandlers.put(0x12, () -> RL("d"));
        extendedOpcodeHandlers.put(0x13, () -> RL("e"));
        extendedOpcodeHandlers.put(0x14, () -> RL("h"));
        extendedOpcodeHandlers.put(0x15, () -> RL("l"));
        extendedOpcodeHandlers.put(0x16, () -> RL("(hl)"));
        extendedOpcodeHandlers.put(0x17, () -> RL("a"));
        extendedOpcodeHandlers.put(0x18, () -> RR("b"));
        extendedOpcodeHandlers.put(0x19, () -> RR("c"));
        extendedOpcodeHandlers.put(0x1A, () -> RR("d"));
        extendedOpcodeHandlers.put(0x1B, () -> RR("e"));
        extendedOpcodeHandlers.put(0x1C, () -> RR("d"));
        extendedOpcodeHandlers.put(0x1D, () -> RR("l"));
        extendedOpcodeHandlers.put(0x1E, () -> RR("hl"));
        extendedOpcodeHandlers.put(0x1F, () -> RR("a"));
        extendedOpcodeHandlers.put(0x20, () -> SLA("b"));
        extendedOpcodeHandlers.put(0x21, () -> SLA("c"));
        extendedOpcodeHandlers.put(0x22, () -> SLA("d"));
        extendedOpcodeHandlers.put(0x23, () -> SLA("e"));
        extendedOpcodeHandlers.put(0x24, () -> SLA("h"));
        extendedOpcodeHandlers.put(0x25, () -> SLA("l"));
        extendedOpcodeHandlers.put(0x26, () -> SLA("hl"));
        extendedOpcodeHandlers.put(0x27, () -> SLA("a"));
        //extendedOpcodeHandlers.put(0x28, () -> SRA_B());
        //extendedOpcodeHandlers.put(0x29, () -> SRA_C());
        //extendedOpcodeHandlers.put(0x2A, () -> SRA_D());
        //extendedOpcodeHandlers.put(0x2B, () -> SRA_E());
        //extendedOpcodeHandlers.put(0x2C, () -> SRA_H());
        //extendedOpcodeHandlers.put(0x2D, () -> SRA_L());
        //extendedOpcodeHandlers.put(0x2E, () -> SRA_mHL());
        //extendedOpcodeHandlers.put(0x2F, () -> SRA_A());
        //extendedOpcodeHandlers.put(0x30, () -> SWAP_B());
        //extendedOpcodeHandlers.put(0x31, () -> SWAP_C());
        //extendedOpcodeHandlers.put(0x32, () -> SWAP_D());
        //extendedOpcodeHandlers.put(0x33, () -> SWAP_E());
        //extendedOpcodeHandlers.put(0x34, () -> SWAP_H());
        //extendedOpcodeHandlers.put(0x35, () -> SWAP_L());
        //extendedOpcodeHandlers.put(0x36, () -> SWAP_mHL());
        //extendedOpcodeHandlers.put(0x37, () -> SWAP_A());
        //extendedOpcodeHandlers.put(0x38, () -> SRL_B());
        //extendedOpcodeHandlers.put(0x39, () -> SRL_C());
        //extendedOpcodeHandlers.put(0x3A, () -> SRL_D());
        //extendedOpcodeHandlers.put(0x3B, () -> SRL_E());
        //extendedOpcodeHandlers.put(0x3C, () -> SRL_H());
        //extendedOpcodeHandlers.put(0x3D, () -> SRL_L());
        //extendedOpcodeHandlers.put(0x3E, () -> SRL_mHL());
        //extendedOpcodeHandlers.put(0x3F, () -> SRL_A());
        //extendedOpcodeHandlers.put(0x40, () -> BIT_B(0));
        //extendedOpcodeHandlers.put(0x41, () -> BIT_C(0));
        //extendedOpcodeHandlers.put(0x42, () -> BIT_D(0));
        //extendedOpcodeHandlers.put(0x43, () -> BIT_E(0));
        //extendedOpcodeHandlers.put(0x44, () -> BIT_H(0));
        //extendedOpcodeHandlers.put(0x45, () -> BIT_L(0));
        //extendedOpcodeHandlers.put(0x46, () -> BIT_HL(0));
        //extendedOpcodeHandlers.put(0x47, () -> BIT_A(0));
        //extendedOpcodeHandlers.put(0x48, () -> BIT_B(1));
        //extendedOpcodeHandlers.put(0x49, () -> BIT_C(1));
        //extendedOpcodeHandlers.put(0x4A, () -> BIT_D(1));
        //extendedOpcodeHandlers.put(0x4B, () -> BIT_E(1));
        //extendedOpcodeHandlers.put(0x4C, () -> BIT_H(1));
        //extendedOpcodeHandlers.put(0x4D, () -> BIT_L(1));
        //extendedOpcodeHandlers.put(0x4E, () -> BIT_HL(1));
        //extendedOpcodeHandlers.put(0x4F, () -> BIT_A(1));
        //extendedOpcodeHandlers.put(0x50, () -> BIT_B(2));
        //extendedOpcodeHandlers.put(0x51, () -> BIT_C(2));
        //extendedOpcodeHandlers.put(0x52, () -> BIT_D(2));
        //extendedOpcodeHandlers.put(0x53, () -> BIT_E(2));
        //extendedOpcodeHandlers.put(0x54, () -> BIT_H(2));
        //extendedOpcodeHandlers.put(0x55, () -> BIT_L(2));
        //extendedOpcodeHandlers.put(0x56, () -> BIT_HL(2));
        //extendedOpcodeHandlers.put(0x57, () -> BIT_A(2));
        //extendedOpcodeHandlers.put(0x58, () -> BIT_B(3));
        //extendedOpcodeHandlers.put(0x59, () -> BIT_C(3));
        //extendedOpcodeHandlers.put(0x5A, () -> BIT_D(3));
        //extendedOpcodeHandlers.put(0x5B, () -> BIT_E(3));
        //extendedOpcodeHandlers.put(0x5C, () -> BIT_H(3));
        //extendedOpcodeHandlers.put(0x5D, () -> BIT_L(3));
        //extendedOpcodeHandlers.put(0x5E, () -> BIT_HL(3));
        //extendedOpcodeHandlers.put(0x5F, () -> BIT_A(3));
        //extendedOpcodeHandlers.put(0x60, () -> BIT(4, "b"));
        //extendedOpcodeHandlers.put(0x61, () -> BIT(4, "c"));
        //extendedOpcodeHandlers.put(0x62, () -> BIT(4, "d"));
        //extendedOpcodeHandlers.put(0x63, () -> BIT(4, "e"));
        //extendedOpcodeHandlers.put(0x64, () -> BIT(4, "h"));
        //extendedOpcodeHandlers.put(0x65, () -> BIT(4, "l"));
        //extendedOpcodeHandlers.put(0x66, () -> BIT_HL(4));
        //extendedOpcodeHandlers.put(0x67, () -> BIT(4, "a"));
        //extendedOpcodeHandlers.put(0x68, () -> BIT(5, "b"));
        //extendedOpcodeHandlers.put(0x69, () -> BIT(5, "c"));
        //extendedOpcodeHandlers.put(0x6A, () -> BIT(5, "d"));
        //extendedOpcodeHandlers.put(0x6B, () -> BIT(5, "e"));
        //extendedOpcodeHandlers.put(0x6C, () -> BIT(5, "h"));
        //extendedOpcodeHandlers.put(0x6D, () -> BIT(5, "l"));
        //extendedOpcodeHandlers.put(0x6E, () -> BIT_HL(5));
        //extendedOpcodeHandlers.put(0x6F, () -> BIT(5, "a"));
        //extendedOpcodeHandlers.put(0x70, () -> BIT(6, "b"));
        //extendedOpcodeHandlers.put(0x71, () -> BIT(6, "c"));
        //extendedOpcodeHandlers.put(0x72, () -> BIT(6, "d"));
        //extendedOpcodeHandlers.put(0x73, () -> BIT(6, "e"));
        //extendedOpcodeHandlers.put(0x74, () -> BIT(6, "h"));
        //extendedOpcodeHandlers.put(0x75, () -> BIT(6, "l"));
        //extendedOpcodeHandlers.put(0x76, () -> BIT_HL(6));
        //extendedOpcodeHandlers.put(0x77, () -> BIT(6, "a"));
        //extendedOpcodeHandlers.put(0x78, () -> BIT(7, "b"));
        //extendedOpcodeHandlers.put(0x79, () -> BIT(7, "c"));
        //extendedOpcodeHandlers.put(0x7A, () -> BIT(7, "d"));
        //extendedOpcodeHandlers.put(0x7B, () -> BIT(7, "e"));
        //extendedOpcodeHandlers.put(0x7C, () -> BIT(7, "h"));
        //extendedOpcodeHandlers.put(0x7D, () -> BIT(7, "l"));
        //extendedOpcodeHandlers.put(0x7E, () -> BIT_HL(7));
        //extendedOpcodeHandlers.put(0x7F, () -> BIT(7, "a"));
        //extendedOpcodeHandlers.put(0x80, () -> RES_B(0, "b"));
        //extendedOpcodeHandlers.put(0x81, () -> RES_B(0, "c"));
        //extendedOpcodeHandlers.put(0x82, () -> RES_B(0, "d"));
        //extendedOpcodeHandlers.put(0x83, () -> RES_B(0, "e"));
        //extendedOpcodeHandlers.put(0x84, () -> RES_B(0, "h"));
        //extendedOpcodeHandlers.put(0x85, () -> RES_B(0, "l"));
        //extendedOpcodeHandlers.put(0x86, () -> RES_uHL(0));
        //extendedOpcodeHandlers.put(0x87, () -> RES_B(0, "a"));
        //extendedOpcodeHandlers.put(0x88, () -> RES_B(1, "b"));
        //extendedOpcodeHandlers.put(0x89, () -> RES_B(1, "c"));
        //extendedOpcodeHandlers.put(0x8A, () -> RES_B(1, "d"));
        //extendedOpcodeHandlers.put(0x8B, () -> RES_B(1, "e"));
        //extendedOpcodeHandlers.put(0x8C, () -> RES_B(1, "h"));
        //extendedOpcodeHandlers.put(0x8D, () -> RES_B(1, "l"));
        //extendedOpcodeHandlers.put(0x8E, () -> RES_uHL(1));
        //extendedOpcodeHandlers.put(0x8F, () -> RES_B(1, "a"));
        //extendedOpcodeHandlers.put(0x90, () -> RES_B(2, "b"));
        //extendedOpcodeHandlers.put(0x91, () -> RES_B(2, "c"));
        //extendedOpcodeHandlers.put(0x92, () -> RES_B(2, "d"));
        //extendedOpcodeHandlers.put(0x93, () -> RES_B(2, "e"));
        //extendedOpcodeHandlers.put(0x94, () -> RES_B(2, "h"));
        //extendedOpcodeHandlers.put(0x95, () -> RES_B(2, "l"));
        //extendedOpcodeHandlers.put(0x96, () -> RES_uHL(2));
        //extendedOpcodeHandlers.put(0x97, () -> RES_B(2, "a"));
        //extendedOpcodeHandlers.put(0x98, () -> RES_B(3, "b"));
        //extendedOpcodeHandlers.put(0x99, () -> RES_B(3, "c"));
        //extendedOpcodeHandlers.put(0x9A, () -> RES_B(3, "d"));
        //extendedOpcodeHandlers.put(0x9B, () -> RES_B(3, "e"));
        //extendedOpcodeHandlers.put(0x9C, () -> RES_B(3, "h"));
        //extendedOpcodeHandlers.put(0x9D, () -> RES_B(3, "l"));
        //extendedOpcodeHandlers.put(0x9E, () -> RES_uHL(3));
        //extendedOpcodeHandlers.put(0x9F, () -> RES_B(3, "a"));
        //extendedOpcodeHandlers.put(0xA0, () -> RES_B(4, "b"));
        //extendedOpcodeHandlers.put(0xA1, () -> RES_B(4, "c"));
        //extendedOpcodeHandlers.put(0xA2, () -> RES_B(4, "d"));
        //extendedOpcodeHandlers.put(0xA3, () -> RES_B(4, "e"));
        //extendedOpcodeHandlers.put(0xA4, () -> RES_B(4, "h"));
        //extendedOpcodeHandlers.put(0xA5, () -> RES_B(4, "l"));
        //extendedOpcodeHandlers.put(0xA6, () -> RES_uHL(4));
        //extendedOpcodeHandlers.put(0xA6, () -> AND_A());
        //extendedOpcodeHandlers.put(0xA7, () -> RST(0x20));
        //extendedOpcodeHandlers.put(0xA8, () -> XOR_B());
        //extendedOpcodeHandlers.put(0xA9, () -> XOR_C());
        //extendedOpcodeHandlers.put(0xAA, () -> XOR_D());
        //extendedOpcodeHandlers.put(0xAB, () -> XOR_E());
        //extendedOpcodeHandlers.put(0xAC, () -> XOR_H());
        //extendedOpcodeHandlers.put(0xAD, () -> XOR_L());
        //extendedOpcodeHandlers.put(0xAE, () -> XOR_HL());
        //extendedOpcodeHandlers.put(0xAF, () -> XOR_A());
        //extendedOpcodeHandlers.put(0xB0, () -> OR_B());
        //extendedOpcodeHandlers.put(0xB1, () -> OR_C());
        //extendedOpcodeHandlers.put(0xB2, () -> OR_D());
        //extendedOpcodeHandlers.put(0xB3, () -> OR_E());
        //extendedOpcodeHandlers.put(0xB4, () -> OR_H());
        //extendedOpcodeHandlers.put(0xB5, () -> OR_L());
        //extendedOpcodeHandlers.put(0xB6, () -> OR_HL());
        //extendedOpcodeHandlers.put(0xB7, () -> CP_A());
        //extendedOpcodeHandlers.put(0xB8, () -> RET_C());
        //extendedOpcodeHandlers.put(0xB9, () -> RET_NC());
        //extendedOpcodeHandlers.put(0xBA, () -> RET_Z());
        //extendedOpcodeHandlers.put(0xBB, () -> RET_NZ());
        //extendedOpcodeHandlers.put(0xBC, () -> LD_A_u8());
        //extendedOpcodeHandlers.put(0xBD, () -> POP_AF());
        //extendedOpcodeHandlers.put(0xBE, () -> LD_A_HLI());
        //extendedOpcodeHandlers.put(0xBF, () -> CP_u8());
        //extendedOpcodeHandlers.put(0xC0, () -> RET_C());
        //extendedOpcodeHandlers.put(0xC1, () -> POP_BC());
        //extendedOpcodeHandlers.put(0xC2, () -> JP_C_u16());
        //extendedOpcodeHandlers.put(0xC3, () -> CB_PREFIX());
        //extendedOpcodeHandlers.put(0xC4, () -> CALL_C_u16());
        //extendedOpcodeHandlers.put(0xC5, () -> PUSH_BC());
        //extendedOpcodeHandlers.put(0xC6, () -> ADD_u8());
        //extendedOpcodeHandlers.put(0xC7, () -> RST(0x00));
        //extendedOpcodeHandlers.put(0xC8, () -> RET_C());
        //extendedOpcodeHandlers.put(0xC9, () -> RET());
        //extendedOpcodeHandlers.put(0xCA, () -> JP_C_u16());
        //extendedOpcodeHandlers.put(0xCB, () -> CB_PREFIX());
        //extendedOpcodeHandlers.put(0xCC, () -> CALL_C_u16());
        //extendedOpcodeHandlers.put(0xCD, () -> CALL_u16());
        //extendedOpcodeHandlers.put(0xCE, () -> ADC_u8());
        //extendedOpcodeHandlers.put(0xCF, () -> RST(0x08));
        //extendedOpcodeHandlers.put(0xD0, () -> RET_NC());
        //extendedOpcodeHandlers.put(0xD1, () -> POP_DE());
        //extendedOpcodeHandlers.put(0xD2, () -> JP_NC_u16());
        //extendedOpcodeHandlers.put(0xD3, () -> INVALID_OPCODE());
        //extendedOpcodeHandlers.put(0xD4, () -> CALL_NC_u16());
        //extendedOpcodeHandlers.put(0xD5, () -> PUSH_DE());
        //extendedOpcodeHandlers.put(0xD6, () -> SUB_u8());
        //extendedOpcodeHandlers.put(0xD7, () -> RST(0x10));
        //extendedOpcodeHandlers.put(0xD8, () -> RET_C());
        //extendedOpcodeHandlers.put(0xD9, () -> RETI());
        //extendedOpcodeHandlers.put(0xDA, () -> JP_C_nn());
        //extendedOpcodeHandlers.put(0xDB, () -> NOP());
        //extendedOpcodeHandlers.put(0xDC, () -> CALL_C_nn());
        //extendedOpcodeHandlers.put(0xDD, () -> NOP());
        //extendedOpcodeHandlers.put(0xDE, () -> SBC_A_n());
        //extendedOpcodeHandlers.put(0xDF, () -> RST_18H());
        //extendedOpcodeHandlers.put(0xE0, () -> LDH_n_A());
        //extendedOpcodeHandlers.put(0xE1, () -> POP_HL());
        //extendedOpcodeHandlers.put(0xE2, () -> LD_C_A());
        //extendedOpcodeHandlers.put(0xE3, () -> NOP());
        //extendedOpcodeHandlers.put(0xE4, () -> NOP());
        //extendedOpcodeHandlers.put(0xE5, () -> PUSH_HL());
        //extendedOpcodeHandlers.put(0xE6, () -> AND_n());
        //extendedOpcodeHandlers.put(0xE7, () -> RST_20H());
        //extendedOpcodeHandlers.put(0xE8, () -> ADD_SP_n());
        //extendedOpcodeHandlers.put(0xE9, () -> JP_HL());
        //extendedOpcodeHandlers.put(0xEA, () -> LD_nn_A());
        //extendedOpcodeHandlers.put(0xEB, () -> NOP());
        //extendedOpcodeHandlers.put(0xEC, () -> NOP());
        //extendedOpcodeHandlers.put(0xED, () -> NOP());
        //extendedOpcodeHandlers.put(0xEE, () -> XOR_n());
        //extendedOpcodeHandlers.put(0xEF, () -> RST_28H());
        //extendedOpcodeHandlers.put(0xF0, () -> LDH_A_n(mem.readByte(0xFF00 + mem.readByte(regs.getPC() + 1))));
        //extendedOpcodeHandlers.put(0xF1, () -> POP_AF());
        //extendedOpcodeHandlers.put(0xF2, () -> LD_A_n(mem.readByte(0xFF00 + regs.getC())));
        //extendedOpcodeHandlers.put(0xF3, () -> DI());
        //extendedOpcodeHandlers.put(0xF4, NOP());
        //extendedOpcodeHandlers.put(0xF5, () -> PUSH_AF());
        //extendedOpcodeHandlers.put(0xF6, () -> OR_n(mem.readByte(regs.getPC() + 1)));
        //extendedOpcodeHandlers.put(0xF7, () -> RST(0x30));
        //extendedOpcodeHandlers.put(0xF8, () -> LDHL_SP_n(mem.readByte(regs.getPC() + 1)));
        //extendedOpcodeHandlers.put(0xF9, () -> LD_SP_HL());
        //extendedOpcodeHandlers.put(0xFA, () -> LD_A_nn(mem.readWord(regs.getPC() + 1)));
        //extendedOpcodeHandlers.put(0xFB, () -> EI());
        //extendedOpcodeHandlers.put(0xFC, NOP());
        //extendedOpcodeHandlers.put(0xFD, NOP());
        //extendedOpcodeHandlers.put(0xFE, () -> CP_n(mem.readByte(regs.getPC() + 1)));
        //extendedOpcodeHandlers.put(0xFF, () -> RST(0x38));
    }

    // rotate content of the register left by 1 bit
    public void RL(String register){
        int regValue;
        if(register.equals("(hl)"))
            regValue = mem.readByte(regs.getHL());
        else
            regValue = regs.getRegisterValue(register);

        // get the most significant bit
        int msb = (regValue & 0x80) >> 7;

        // shift the value left by 1 bit then OR that with the carry flag then and that with FF to set any higher
        //bits to zero so it will fit in a register.
        regValue = ((regValue << 1) | (regs.fByte.checkC() ? 1 : 0)) & 0xFF;


        // set the result in the register
        if(register.equals("(hl"))
            mem.writeByte(regs.getHL(), regValue);
        else
            regs.setRegisterValue(register, regValue);

        // set flags
        regs.fByte.setZ(regValue == 0);
        regs.fByte.setN(false);
        regs.fByte.setH(false);
        regs.fByte.setC(msb == 1);
    }

    // left shift arithmetic. Leftmost bit is set to zero and the carry flag is set to the original value of that bit
    public void SLA(String register) {
        int regValue;
        if(register.equals("(hl)"))
            regValue = mem.readByte(regs.getHL());
        else
            regValue = regs.getRegisterValue(register);

        // Get the most significant bit
        int msb = (regValue & 0x80) >> 7;

        // Shift the value left by 1 bit, setting the least significant bit to 0
        regValue = (regValue << 1) & 0xFF;

        // Set the result in the register
        if(register.equals("(hl)"))
            mem.writeByte(regs.getHL(), regValue);
        else
            regs.setRegisterValue(register, regValue);

        // Set flags
        regs.fByte.setZ(regValue == 0);
        regs.fByte.setN(false);
        regs.fByte.setH(false);
        regs.fByte.setC(msb == 1);
    }

    // extended Right rotate
    public void RR(String register) {
        int regValue;
        if(register.equals("(hl)"))
            regValue = mem.readByte(regs.getHL());
        else
            regValue = regs.getRegisterValue(register);

        // Get the least significant bit of the register
        int lsb = regValue & 0x01;

        // Shift the register value right by 1 and insert the previous carry flag into the most significant bit
        regValue = (regValue >> 1) | (regs.fByte.checkC() ? 0x80 : 0x00);

        // Set the result back into the register
        if(register.equals("(hl)"))
            mem.writeByte(regs.getHL(), regValue);
        else
            regs.setRegisterValue(register, regValue);

        // Set the flags
        regs.fByte.setZ(regValue == 0);
        regs.fByte.setN(false);
        regs.fByte.setH(false);
        regs.fByte.setC(lsb == 1);
    }

    // Rotate left circular operation sets zero, subtract, half-carry, and carry
    public void RLC(String register){
        int regValue;
        if (register.equals("(hl)")) {
            regValue = mem.readByte(regs.getHL());
        } else {
            regValue = regs.getRegisterValue(register);
        }

        // get the most significant byte
        int msb = (regValue & 0x80) >> 7;

        // shift the value left by 1 bit and set the least significant byte to the msb
        regValue = ((regValue << 1) | msb) & 0xFF;

        if (register.equals("(hl)")) {
            mem.writeByte(regs.getHL(), regValue);
        } else {
            // set the result in the register
            regs.setRegisterValue(register, regValue);
        }

        // set flags
        regs.fByte.setZ(regValue == 0);
        regs.fByte.setN(false);
        regs.fByte.setH(false);
        regs.fByte.setC(msb == 1);
    }


    public void RRC(String register) {
        int regValue = regs.getRegisterValue(register);

        // get the least significant bit
        int lsb = regValue & 0x01;

        // shift the value right by 1 bit and set the most significant byte to the lsb
        regValue = ((regValue >> 1) | (lsb << 7)) & 0xFF;

        // set the result in the register
        regs.setRegisterValue(register, regValue);

        // set flags
        regs.fByte.setZ(regValue == 0);
        regs.fByte.setN(false);
        regs.fByte.setH(false);
        regs.fByte.setC(lsb != 0);
    }


    public void nop() {
        System.out.println("nop");
        regs.setPC(regs.getPC() + 1);
    }

    // run the extended opcode that corresponds to the next byte in memory read after the opcode CB is read
    public void NEW_CB(){
        // get the next byte
        int extendedOpcode = mem.readByte(regs.getPC() + 1);
        Runnable exOperation = extendedOpcodeHandlers.get(extendedOpcode);
        exOperation.run();
    }

    // load value into intoRegister
    public void LD(String register, int value, int length) {
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
    //
    //
    public void LD(int address, String register, int length) {// Load register value into address
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
                // this one sets flags, see opcode table 0x35
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
        if (register.length() < 2 || register.equals("(hl)")) {
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

    // jump to address location
    public void JP(int value) {
        System.out.println("jump" + value);
        regs.setPC(value);
    }

    // add the value of addRegister into intoRegister and store it in intoRegister
    public void ADD(String intoRegister, String addRegister) {
        int result = 0;
        int addValue = 0;
        int length = 0;

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
                        result = regs.getA() + mem.readByte(regs.getHL());
                        addValue = mem.readByte(regs.getHL());
                        regs.setA(result);
                        break;
                    // add an 8 bit immediate value to A. The value that we add to A is the next
                    // byte in memory
                    case "d8":
                        int d8Val = mem.readByte(regs.getPC() + 1); // got the value from mem
                        result = regs.getA() + d8Val;
                        addValue = d8Val;
                        regs.setA(result);
                        length++;
                        break;
                    case "i8":
                        int i8Val = (byte) mem.readByte(regs.getPC() + 1); // got the value from mem
                        result = regs.getA() + i8Val;
                        addValue = i8Val;
                        regs.setA(result);
                        length++;
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
                // what opcode?
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
        regs.setPC(regs.getPC() + length);
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
        regs.fByte.setC(carry != 0);
        regs.fByte.setZ(false);
        regs.fByte.setN(false);
        regs.fByte.setH(false);
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
        // System.out.println(regs.getA());
        regs.setPC(regs.getPC() + 1);
    }

    // one bit left rotation of A
    public void RLCA() {
        int a = regs.getA();
        int carry = (a >> 7) & 0x01; // Get the MSB of A and convert it to an integer
        a = (a << 1) | carry; // Left shift A by 1 and insert carry into the least significant bit
        regs.setA(a);
        regs.fByte.setC(carry == 1); // Set the carry flag to the value of the MSB of A
        regs.fByte.setZ(a == 0); // Set the zero flag if A is now zero
        regs.setPC(regs.getPC() + 1);
    }

    // https://forums.nesdev.org/viewtopic.php?f=20&t=15944 psuedoCode for DAA
    // decimal adjust register A
    // WORKING CORRECTLY
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
        regs.setPC(regs.getPC() + 1);
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
        int sp = regs.getSP();
        int lower = (mem.readByte(sp));
        sp++;
        int upper = (mem.readByte(sp));
        sp++;
        regs.setSP(sp);
        JP((upper << 8) | lower);
    }

    // call subroutine if zero flag is set
    public void CALLZ(int location, boolean flag) {
        if (flag)
            CALL(location);
        regs.setPC(regs.getPC() + 3);
    }

    // call a subroutine if zero flag is not set
    public void CALLNZ(int location, boolean flag) {
        if (flag)
            CALL(location);
        regs.setPC(regs.getPC() + 3);
    }

    /// call a subroutine if the carry flag is not set
    public void CALLNC(int location, boolean flag) {
        if (flag)
            CALL(location);
        regs.setPC(regs.getPC() + 3);
    }

    // call subroutine if carry flag is set
    public void CALLC(int location, boolean flag) {
        if (flag)
            CALL(location);
        regs.setPC(regs.getPC() + 3);
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
            RET();
        regs.setPC(regs.getPC() + 1);
    }

    // executes if the zero flag is set
    public void RETZ() {
        if (regs.fByte.checkZ() == true)
            RET();
        regs.setPC(regs.getPC() + 1);
    }

    // return from a subroutine if carry flag is not set
    public void RETNC() {
        if (!regs.fByte.checkC() == true)
            RET();
        regs.setPC(regs.getPC() + 1);
    }

    // return from a subroutine if carry flag is set
    public void RETC() {
        if (regs.fByte.checkC() == true)
            RET();
        regs.setPC(regs.getPC() + 1);
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
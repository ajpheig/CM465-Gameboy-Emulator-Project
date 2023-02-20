
/**
 * Registers Class
 * af, bc, de, and hl are different registers, but different halves can be
 * accesses separately
 * a is the accumulator, which stores data for logical functions
 * f stores flags
 * b,c,d,e,h, and l are all generic purpose registers
 * sp is the stack pointer, which points to the top of the stack, it may need to
 * increment or decrement
 * pc i the program counter, which points to the address of the next
 * instruction. only needs to be incremented
 * methods can set half-registers, get registers or half-registers, and perform
 * sp/pc functions.
 * Made By Adam Crawford
 */
public class Registers {
    private int a; // accumulator, stores operands for logical functions
    public Flags fByte = new Flags();; // flags

    private int b; // mem
    private int c; // mem

    private int d; // mem
    private int e; // mem

    private int h; // gen purpose
    private int l; // gen purpose

    private int sp; // stack pointer, points to top of stack

    private int pc; // program counter, points to address of next instruction

    public int getAF() {
        return a << 8 | fByte.getFByte();
    }

    public int getBC() {
        return b << 8 | c;
    }

    public int getDE() {
        return d << 8 | e;
    }

    public int getHL() {
        return h << 8 | l;
    }

    public int getSP() {
        return sp;
    }

    public int getPC() {
        return pc;
    }

    public int getA() {
        return a;
    }

    public Flags getF() {
        return fByte;
    }

    public int getB() {
        return b;
    }

    public int getC() {
        return c;
    }

    public int getD() {
        return d;
    }

    public int getE() {
        return e;
    }

    public int getH() {
        return h;
    }

    public int getL() {
        return l;
    }

    public void setA(int a) {
        this.a = a;
    }

    public void setB(int b) {
        this.b = b;
    }

    public void setC(int c) {
        this.c = c;
    }

    public void setD(int d) {
        this.d = d;
    }

    public void setE(int e) {
        this.e = e;
    }

    public void setH(int h) {
        this.h = h;
    }

    public void setL(int l) {
        this.l = l;
    }

    public void incrementPC() {
        pc = pc + 1;
    }

    public void incrementSP() {
        sp = sp + 1;
    }

    public void decrementSP() {
        sp = sp - 1;
    }

    public void setPC(int pc) {
        this.pc = pc;
    }

    public void setSP(int sp) {
        this.sp = sp;
    }

    public void setAF(int af) {
        a = af >> 8;
        fByte.setFByte(af & 0xff);
    }

    public void setBC(int bc) {
        b = bc >> 8;
        c = bc & 0xff;
    }

    public void setDE(int de) {
        d = de >> 8;
        e = de & 0xff;
    }

    public void setHL(int hl) {
        h = hl >> 8;
        l = hl & 0xff;
    }

}

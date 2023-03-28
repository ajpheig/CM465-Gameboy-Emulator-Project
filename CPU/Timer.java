package CPU;

import Memory.*;

public class Timer {
    private CPU cpu;
    private Memory mem;
    int ticks = 1;
    int divCount;
    int timerCounter = 0;

    public Timer(CPU cpu, Memory mem) {
        this.cpu = cpu;
        this.mem = mem;
    }
    public int getDiv() {
        return this.divCount;
    }

    public void handleTimer(int ticks) {
        divCount += (ticks / 4);// inc DIV register
        if (divCount >= 256) {
            divCount -= 256;
            mem.writeByte(0xff04, mem.readByte(0xff04) + 1);
        }
        // is timer enabled?
        if (((mem.readByte(0xff07) >> 2) & 0x1) == 1) {// grab the 2nd bit
            timerCounter += ticks;
            int freq = 4096;// base freq Hz
            if ((mem.readByte(0xff07) & 0b11) == 1) {
                freq = 262144;// 0x01 frequency
            } else if ((mem.readByte(0xff07) & 0b11) == 2)
                freq = 65536;// 0x10 frequency
            else if ((mem.readByte(0xff07) & 0b11) == 3)
                freq = 16834;// 0x11 frequency
            while (timerCounter >= (4194304 / freq)) {// while so that it can be ++ twice during one cycle
                // increase TIMA register
                // System.out.printf("| %d |", mem.readByte(0xff05));
                mem.writeByte(0xff05, mem.readByte(0xff05) + 1);
                // did TIMA overflow? ==0?
                if (mem.readByte(0xff05) == 0) {
                    mem.writeByte(0xff0f, mem.readByte(0xff0f) | 0b100);// set Timer interrupt in
                    // IF
                    // System.out.println("TIMA overflow, set int");
                    mem.writeByte(0xff05, mem.readByte(0xff06));// reset TIMA to value in TMA
                }
                timerCounter = 0;// -= (4194304 / freq);
            }
        }
    }
}

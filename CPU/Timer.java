package CPU;

import Memory.*;

public class Timer {
    private CPU cpu;
    private Memory mem;
    int ticks = 1;
    int divCount;
    int timerCounter = 0;
    int freq=4096;
    int tima =0;
    int div=0;
    boolean enabled=false;

    public Timer(CPU cpu, Memory mem) {
        this.cpu = cpu;
        this.mem = mem;
    }
    public int getDiv() {
        return this.div;
    }
    public void resetDiv() {
        this.div=0;
    }
    public int getTIMA() {
        return this.tima;
    }
    public void setTIMA(int i) {
        this.tima=i;
    }
    public void setTAC(int i) {
        System.out.println(i+" "+(i&4));
        if((i&4)==4)enabled=true;
        if((i&3)==0)this.freq=4096;//1024;
        if((i&3)==1)this.freq=262144;//16;
        if((i&3)==2)this.freq=65536;//64;
        if((i&3)==3)this.freq=16834;//256;
    }


    public void handleTimer(int ticks) {
        divCount += (ticks / 4);// inc DIV register
        if (divCount >= 256) {
            divCount -= 256;
            div++;
            //mem.writeByte(0xff04, mem.readByte(0xff04) + 1);
        }
        // is timer enabled?
        if (enabled) {// grab the 2nd bit
            timerCounter += ticks;
            //freq = 4096;// base freq Hz
            // if ((mem.readByte(0xff07) & 0b11) == 1) {
            // freq = 262144;// 0x01 frequency
            // } else if ((mem.readByte(0xff07) & 0b11) == 2)
            //freq = 65536;// 0x10 frequency
            //else if ((mem.readByte(0xff07) & 0b11) == 3)
            //freq = 16834;// 0x11 frequency
            while (timerCounter >= (4194304 / this.freq)) {// while so that it can be ++ twice during one cycle
                // increase TIMA register
                // System.out.printf("| %d |", mem.readByte(0xff05));
                //mem.writeByte(0xff05, mem.readByte(0xff05) + 1);
                tima++;
                // did TIMA overflow? ==0?
                if (tima >=256) {
                    mem.writeByte(0xff0f,  mem.readByte(0xff0f) |0b100);// set Timer interrupt in
                    // IF
                    //System.out.println("TIMA :"+tima);
                    tima=mem.readByte(0xff06);
                    //System.out.println("TIMA overflow, set int:"+tima);
                    //mem.writeByte(0xff05, mem.readByte(0xff06));// reset TIMA to value in TMA
                }
                timerCounter -= (4194304 / this.freq);
            }
        }
    }
}

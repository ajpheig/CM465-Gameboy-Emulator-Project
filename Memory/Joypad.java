package Memory;

public class Joypad {
int value =0xff;
    int buttons=1;
    int direction=1;
    int up=1;
    int right=1;
    int down=1;
    int left=1;
    int start=1;
    int select=1;
    int a=1;
    int b=1;

    public void setPad(int value) {
        this.value=value&0xff;
    }
    public void setPadBits(int value) {//0b10001 is right arrow
        this.value&=~(value);
        //System.out.println("setpad"+Integer.toHexString(value));
    }
    public void resetPadBits(int value) {//0b10001 is right arrow
        this.value|=(value);
    }
    public int readButtons() {
        return readPad()&0b1111;
    }
    public int getControlSelect() {
        if((value>>4&1)==0)return 1;
        if((value>>5&1)==0)return 2;
        return 0;
    }
    public void resetPad(int value1,int value2) {
        value=0xff;
    }
    public int readPad() {
        return value&0xff;
    }
}

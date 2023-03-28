package Memory;

public class Joypad {
int value =0xff;
    public int up=1;
    public int right=1;
    public int down=1;
    public int left=1;
    public int start=1;
    public int select=1;
    public int a=1;
    public int b=1;

    public void setPadBits(int set,int bit) {//0b10001 is right arrow
        if(set==5)
            switch(bit) {
                case 0:
                    a=0;
                    break;
                case 1:
                    b=0;
                    break;
                case 2:
                    select=0;
                    break;
                case 3:
                    start=0;
                    break;
            }
        else
            switch(bit) {
                case 0:
                    right=0;
                    break;
                case 1:
                    left=0;
                    break;
                case 2:
                    up=0;
                    break;
                case 3:
                    down=0;
                    break;
            }
    }
    public void resetActionBits(int bit) {

        switch(bit) {
            case 0:
                a=1;
                break;
            case 1:
                b=1;
                break;
            case 2:
                select=1;
                break;
            case 3:
                start=1;
                break;
        }
    }
    public void resetDirBits(int bit) {

        switch(bit) {
            case 0:
                right=1;
                break;
            case 1:
                left=1;
                break;
            case 2:
                up=1;
                break;
            case 3:
                down=1;
                break;
        }
    }
    public int readActionButtons() {
        return start<<3|select<<2|b<<1|a;//reads bottom bits
    }
    public int readDirButtons() {
        return down<<3|up<<2|left<<1|right;//reads bottom bits
    }
}

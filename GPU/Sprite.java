package GPU;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// class to hold information about sprites and their location
public class Sprite {
    private byte x;
    private byte y;
    private byte tileNumber;
    private byte flags;
    private static List<Sprite> spriteList = new ArrayList<>();

    public Sprite(byte y, byte x, byte tileNumber, byte flags) {
        this.x = x;
        this.y = y;
        this.tileNumber = tileNumber;
        this.flags = flags;
        spriteList.add(this);
//        System.out.println("SPRITE ADDED TO SPRITELISET INFO");
//        System.out.print("y ");
//        System.out.println(this.y & 0xFF);
//        System.out.print("x ");
//        System.out.println(this.x & 0xFF);
//        System.out.print("tile# ");
//        System.out.println(this.tileNumber & 0xFF);
//        System.out.print("flags ");
//        System.out.println(this.flags & 0xFF);
    }

    public byte getX() {
        return x;
    }

    public byte getY() {
        return y;
    }

    public byte getTileNumber() {
        return tileNumber;
    }

    public byte getFlags() {
        return flags;
    }

    public static List<Sprite> getAllSprites() {
        //System.out.println("get All sprites");
        if(spriteList.isEmpty())
            return new ArrayList<>();
        else
            return spriteList;
    }

    public static int getSpriteCount() {
        if(spriteList.isEmpty())
            return 0;
        else
            return spriteList.size();
    }

    // sort sprites from smallest to largest x cord, if there is a tie go off priority, if there is still
    // a tie the one that was added last to the oam table(biggest index in spriteList)
    public static void sortSprites() {
        Collections.sort(spriteList, new Comparator<Sprite>() {
            @Override
            public int compare(Sprite s1, Sprite s2) {
                // First compare by X-coordinate
                if (s1.getX() < s2.getX()) {
                    return -1;
                } else if (s1.getX() > s2.getX()) {
                    return 1;
                } else {
                    // If X-coordinates are equal, compare by priority bit
                    if ((s1.getFlags() & 0x80) == 0 && (s2.getFlags() & 0x80) != 0) {
                        return -1;
                    } else if ((s1.getFlags() & 0x80) != 0 && (s2.getFlags() & 0x80) == 0) {
                        return 1;
                    } else {
                        // If priorities are equal, the one added later wins
                        return spriteList.indexOf(s2) - spriteList.indexOf(s1);
                    }
                }
            }
        });
        // Remove the extra sprites if there are more than 10
        if (spriteList.size() > 10)
        {
         //   spriteList = spriteList.subList(0, 10);
        }
    }

    public static void clearSprites(){
        spriteList.clear();
    }
}
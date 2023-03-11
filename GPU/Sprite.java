package GPU;

import java.util.ArrayList;
import java.util.List;

// class to hold information about sprites and their location
public class Sprite {
    private int x;
    private int y;
    private int tileNumber;
    private boolean flipX;
    private boolean flipY;
    private int paletteNumber;
    private static List<Sprite> spriteList = new ArrayList<>();

    public Sprite(int x, int y, int tileNumber, boolean flipX, boolean flipY, int paletteNumber) {
        this.x = x;
        this.y = y;
        this.tileNumber = tileNumber;
        this.flipX = flipX;
        this.flipY = flipY;
        this.paletteNumber = paletteNumber;
        spriteList.add(this);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getTileNumber() {
        return tileNumber;
    }

    public boolean getFlipX() {
        return flipX;
    }

    public boolean getFlipY() {
        return flipY;
    }

    public int getPaletteNumber() {
        return paletteNumber;
    }

    public static List<Sprite> getAllSprites() {
        return spriteList;
    }
}

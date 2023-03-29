package GPU;
import Memory.*;
public class TileMap {
    private int[][] map;
    private int tileSetNum;
    private Memory mem;
    public TileMap(Memory mem, int startAddress,int tileSetNum) {
        this.map = new int[32][32];
        this.mem=mem;
        this.tileSetNum=tileSetNum;
        for (int i = 0; i < 32; i++) {
            for (int j = 0; j < 32; j++) {
                int tileNumberAddress = startAddress + (i * 32) + j;
                int tileNumber;
                if (tileSetNum == 0) {
                    tileNumber = mem.readByte(tileNumberAddress);
                }
                else {
                   tileNumber = (byte) mem.readByte(tileNumberAddress);
                }
                // if(i==9&&j==0xc) System.out.println(Integer.toHexString(tileNumber));
                this.map[i][j] = tileNumber&0xff;
            }
        }
    }
    public int getTile(int x, int y) {
         Tile[] ts= mem.getVram().getTileSet();
        int tileNum =-1;
        boolean b=mem.getLcdc().getBit(4);
        if(b||map[y % 32][x % 32]>127) tileNum= map[y % 32][x % 32];
        else {
            tileNum=map[y % 32][x % 32]+256;
        }
        //System.out.println(map[x % 32][y % 32]);
        if (tileNum == -1) {
            System.out.println("failed to find tile num: " + tileNum);
        }
        //if(y==9&&x==0xd) System.out.println("tile "+Integer.toHexString(map[y][x])
          //      +" x:"+x+" y:"+y+" tile:"+tileNum);
        return tileNum;
    }
}

package GPU;

public class Tile {
    private int[][] tileData;
    final static int Zero =0;
    final static int One =1;
    final static int Two =2;
    final static int Three =3;

    public Tile(){
    tileData=new int[8][8];
    }
    public void setVal(int x,int y,int val) {
        this.tileData[x][y]=val;
    }
    public int getVal(int x,int y) {
        return this.tileData[x][y];
    }

}

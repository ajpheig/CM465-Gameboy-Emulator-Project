import java.io.*;

//this class reads the Game Boy game rom file as binary data

public class ReadGameBoyRom {
    public static void main(String[] args){
        //File romFile = new File("/Users/brettkulp/Desktop/Capstone/Emulator/src/Tetris (World) (Rev A).gb");
        File romFile = new File("/Users/brettkulp/Desktop/Capstone/Emulator/src/dmg_boot.bin");
        byte[] romData = new byte[(int)romFile.length()];

        try{
            FileInputStream romStream = new FileInputStream(romFile);
            romStream.read(romData);
            romStream.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        //here is where we do something with the ROM data currently just printing it out in here
        int i = 0;
        for (byte b : romData) {
            if(i  % 10 == 0 && i != 0){
                System.out.println();
            }
            System.out.print(String.format("%02X ", b & 0xFF));
            i++;
        }
    }
}

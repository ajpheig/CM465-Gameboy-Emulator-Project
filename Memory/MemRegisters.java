package Memory;
/**
 * Write a description of class MemRegisters here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
import Memory.*;
import java.util.*;
public class MemRegisters
{
    public byte value;
    public int location;
    public MemRegisters()
    {
        this.value=0;
    }
    public byte getByte()
    {
        return value;
    }
    public void setByte(byte value) 
    {
        this.value = value;
    }
    public int getLocation()
    {
        return location;
    }
    public void setLocation()
    {
        this.location = location;
    }
    public boolean getBit(int bit)
    {
        return ((value >> bit) & 0x01) == 1;
    }
    public void setBit(int bit, boolean set)
    {
        byte mask = (byte)(1 << bit);
        if(set)
            value |= mask;
        else
            value &= mask;
    }
    public static byte setBits(byte value, boolean bit, int start, int end)
    {
        int bitmask = (1 << (end-start+1))-1;
        bitmask <<=start;
        byte newValue;
        if(bit)
            newValue = (byte)1;
        else
            newValue = (byte)0;
        value |= (newValue << start) & bitmask;
        return value;
    }
    /*
     * Here are a list of Memory Registers
     * IE Register - Controls which interrupts are enabled, an 8-bit register at 0xFFFF
     *      Bit 1 refers to V-Blank Interrupt enable, occurs when LCD Controller enters or leaves V-Blank state
     *      Bit 2 refers to LCD STAT interrupt enable, occurs when LCD Controllers enters a specific mode
     *      Bit 3 refers to Timer Interrupt Enable, occurs when the timer overflows
     *      Bit 4 refers to Serial Interrupt enable, occurs when data is ready to be sent/recieved over serial port
     *      Bit 5 refers to the Joypad Interrupt Enable, occurs when a button on the joypad is pressed
     *      Bits 6-8 are not used
     * IF Register - Requests interrupts from different sources in the gameboy by enabling a corresponding bit. Located at 0xFF0F
     *      Bit 1 refers to the V-Blank Interrupt Request (INT 40h)
     *      Bit 2 refers to the LCD STAT Interrupt Request (INT 48h)
     *      Bit 3 refers to the Timer Interrupt Request (INT 50h)
     *      Bit 4 refers to the Serial Interrupt Request (INT 58h)
     *      Bit 5 refers to the Joypad Interrupt Request (INT 60H)
     *      Bits 6-8 are not used.
     * Sound Registers - controls the four GB Sound Channels (1 and 2 are squarewave, 3 is waveform output, 4 is noise). Located between 0xFF10 and 0xFF3f
     * Each Channel has 4-5 registers
     *      C1: NR11, Sweep Register. NR12, Sound Length/Wave Pattern Duty. NR13, Frequency Lo. NR14, Frequency Hi/Control
     *      C2: NR21, Sound Length/Wave Pattern Duty. NR22, Envelope. NR23, Freq Lo. NR24, Freq Hi/Control
     *      C3: NR30, Sound On/Off. NR31, Sound Length. NR32, Select Output Level. NR33, Freq Lo. NR34, Freq Hi/Control
     *      C4: NR41, Sound Length. NR42, Envelope. NR43, Polynomial Counter. NR44, Control
     * LCD Registers - LCD Registers control the graphics system and are located between 0xFF40 and 0xFF4B. There are 4 types.
     *      LCDC (LCD Control) Register: This 8-bit register controls the display and the behavior of the graphics system
     *          Bit 1: BG Display (Background) Enable
     *          Bit 2: OBJ (Sprite) Display Enable
     *          Bit 3: OBJ (Sprite) Size
     *          Bit 4: BG Tile Map Display Select
     *          Bit 5: BG & Window Tile Data Select
     *          Bit 6: Window Display Enable
     *          Bit 7: WIndow Tile Map Display Select
     *          Bit 8: LCD Display Enable
     *      STAT (LCD Status): This register provides information about the current LCD state and controls behavior of LCD interrupt
     *          Bit 1 and 2: Mode Flag
     *          Bit 3: Coincidence Flag
     *          Bit 4: Mode 0 H-Blank Interrupt Enable
     *          Bit 5: Mode 1 V-Blank Interrupt Enable
     *          Bit 6: Mode 2 OAM interrupt Enable
     *          Bit 7: LYC=LY Coincidence Interrupt Enable
     *          Bit 8 is unused
     *          
     *     Background Registers
     *          SCY (Scroll Y): Specifies Y-Coord of Background Scroll
     *          SCX (Scroll X): Specifies X-Coord of Background Scroll
     *          BGPD (Background Pallete Data): COntains color values for GB's Background palette
     *          BGP (Background Palette): Specifies which colors are used for Background Palette
     *     Window Registers
     *          WY (Window Y): Specifies Y Coord of Window
     *          WX (Window X): Specifies X Coord of Window
     *     Object (Sprite) Registers
     *          OAM (Object Attribute Memory): Contains information about the sprites that are currently on display
     *          OBJPD (Object Palette Data): This register contains the color values for the GB's Object Palette
     *          OBP0 (Object Palette 0): Specifies which colors are used for the first object palette
     *          OBP1 (Object Palette 1): Specifies which colors are used for the second object palette
     * Timer registers - The timer registers control the Gameboy's timer system, located form 0xFF04 ot 0xFF07
     *      DIV (Divider Register): FF04, 8 bit read only that is incremented at a rate of 16384 Hz, generating a clock signal for the timer system
     *      TIMA (Timer Counter): FF05, 8 bit counter incremeented at a rate determined by the value in the TAC register. When it overflows at 0xFF, generates and interrupt
     *      TMA (Timer Modulo): FF06, 8 bit register that sets the starting value of the TIMA counter, setting TIMA to its value upon an overflow
     *      TAC (Timer Control): FF07, 8 bit Register that controls the behavior of the timer system
     *          Bit 1-2: Input clock select
     *              00: 4096 Hz
     *              01: 262144 Hz
     *              10: 65536 Hz
     *              01: 16384
     *          Bit 3: Timer stop
     *          Bit 4-8: unused
     * Joypad Register - Detects a control input on the joypad for the game to interact with, located at 0xFF00
     *      Bit 6: indicates whether the register reflects the face buttons (0) or doesn't (1)
     *      Bit 5: indicates whether the register reflects the joypad (0) or doesn't (1)
     *      Bit 4: Maps to Down or Start (0 pressed, 1 not)
     *      Bit 3: Maps to Up or Select ( pressed, 1 not)
     *      Bit 2: Maps to Left or B
     *      Bit 1: Maps to Right or A
     * There are serial i/o registers, but we might not implement them since link cable emulation is not extremely important
       */
    
}

# CM465-Gameboy-Emulator-Project
 
Group project for CM465 Capstone
Developed by: Alex Pheigaru, Adam Crawford, Brett Kulp
 
WUBOY is an emulator for the original GameBoy that provides an accurate Gameboy Playing experience.

INSTALL
----------------------------------------------------------
Java SE Development Kit 11.0.1 (JDK 11.0.1)
A Java IDE

CONTENTS
----------------------------------------------------------
NOTICE: Official Nintendo Boot Rom not included for legal reasons 
Bootrom is not required for operation

Readme.txt	
#organized by packages	
CPU 
CPU.java
Flags.java
InterruptManager.java
Opcodes.java
OpcodeTestUnit.java
Registers.java
RegisterTestUnit.java
Timer.java
Gameboy
GameBoyRom.java
GBFrame.java
GBScreen.java
ReadGBFC.java
GPU
Display.java
PPU.java
Sprite.java
Tile.java
TileMap.java
Memory
DebugPane.java
Joypad.java
Memory.java
MemRegister.java
Ram.java

Lib  (Jar Files for testing) 
Bsh-2.0b4.jar
Hamcrest-core-1.3.jar
Jcommander.jar
Junit-4.13.2.jar
testng-6.0.1.jar


RUNNING THE EMULATOR
----------------------------------------------------------

Download and Install
If you do not have a Java IDE, download and install one. Popular IDEs are NetBeans, BlueJ, VSCode, IntelliJ.

Open the project and run ReadGBFC from the gameboy package
This will launch the program and a GUI will appear

PLAYING GAMES ON THE EMULATOR
----------------------------------------------------------
Download ROMs
ROMs are what the files are called but they will have a .gb file extension. You can source these from Gameboy ROM dumper tools and a genuine GB cartridge. Recommended that you keep these files inside one document for ease of use.

Choosing game
When the GUI appears you can click the upper left menu button “file” then a drop down will show a “load ROM” button. Click this and a file chooser will pop up and you can locate your ROM file and open it.

Play the game
After opening a file, the program will immediately launch the game and you can play. 

CONTROLS
----------------------------------------------------------
Dpad up  → up arrow 
Dpad down → down arrow
Dpad left → left arrow
Dpad right → right arrow

A button → X
B button → Z

Start → enter
Select → backspace

DEBUGGER
----------------------------------------------------------

A game must be running for the debugger to open. You can open it by clicking the debug menu item then “show debugger”. This will open a secondary screen with the background map and the sprite table. There is also a print out of the current register values that are updated each cycle. This will bring the game to a halt but the main purpose of this utility is to debug the PPU so that is not important. Another feature is the “step through mode” that can be selected below the last button. This will pause the emulator where you can step the CPU with the Enter key. To unpause, press the Spacebar. Exiting out of the debugger will allow the game to run at normal speeds again.

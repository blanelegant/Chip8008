package com.blanelegant.chip8008;

import javax.swing.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) throws Exception {
        // write your code here

        // initialize CHIP-8 emulation core
        Chip8 myChip8 = new Chip8();

        myChip8.initialize();

        UI.launch(args); // launch main UI

        // main emulation loop
        while(true) {
            myChip8.tick();

            // If the draw flag is set, update the screen
            if(myChip8.drawFlag) {
                UI.updateScreen();
            }

            // Store key press state (Press and Release)
//            myChip8.setKeys();
        }
    }
}

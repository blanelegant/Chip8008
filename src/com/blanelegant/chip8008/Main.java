package com.blanelegant.chip8008;

import javax.swing.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {
        // write your code here

        // initialize CHIP-8 emulation core
        Chip8 myChip8 = new Chip8();

        myChip8.initialize();

        JFileChooser chooser = new JFileChooser();
//        FileNameExtensionFilter filter = new FileNameExtensionFilter(
//                "JPG & GIF Images", "jpg", "gif");
//        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            System.out.println("You chose to open this file: " +
                    chooser.getSelectedFile().getName());

            myChip8.load(
                    FileSystems.getDefault().getPath(
                    chooser.getSelectedFile().getAbsolutePath()));
        }

        // main emulation loop
        while(true) {
            myChip8.tick();

            // If the draw flag is set, update the screen
            if(myChip8.drawFlag)
                drawGraphics();

            // Store key press state (Press and Release)
            myChip8.setKeys();
        }
    }
}

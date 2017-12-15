package com.blanelegant.chip8008;

import javax.swing.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {
        // write your code here

        // initialize CHIP-8 emulation core
        Chip8 chip8 = new Chip8();

        chip8.initialize();

        JFileChooser chooser = new JFileChooser();
//        FileNameExtensionFilter filter = new FileNameExtensionFilter(
//                "JPG & GIF Images", "jpg", "gif");
//        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            System.out.println("You chose to open this file: " +
                    chooser.getSelectedFile().getName());

            chip8.load(
                    FileSystems.getDefault().getPath(
                    chooser.getSelectedFile().getAbsolutePath()));
        }

        // main emulation loop
        while(true) {
            chip8.tick();
        }
    }
}

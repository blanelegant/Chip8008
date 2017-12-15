package com.blanelegant.chip8008;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This is the main instruction interpreter for the chip8008 emulator.
 */

public class Chip8 {

    // set up basic registers
    // char has length of 16 bits
    char current_opcode;

    char[] memory;
    char[] register;

    char index;
    char program_counter;

    // timer registers
    char delay_timer;
    char sound_timer;

    // screen array
    char[] graphics;

    // stack stuff
    char[] stack;
    char stack_pointer;

    char[] keypad;

    boolean drawFlag;

    public Chip8() {

    }

    /**
     * This method is used to (re)set-up the initial state of the machine.
     */
    void initialize() {
        current_opcode = 0; // Reset current opcode

        memory = new char[4096]; // instantiate 4KB memory
        register = new char[16]; // instantiate registers

        index = 0; // zero out index pointer
        program_counter = 0x200; // Program counter starts at 0x200

        graphics = new char[64 * 32]; // 64 columns, 32 rows

        stack = new char[16];
        stack_pointer = 0; // clear stack pointer

        keypad = new char[16];

        // Clear display
        // Clear stack
        // Clear registers V0-VF
        // Clear memory

        // Load fontset, should start at address 0x50 == 80
        for(int i = 0; i < 80; ++i)
            memory[i] = chip8_fontset[i];

        // Reset timers
    }

    /**
     * This method will be called for every clock cycle of the CPU we want to emulate.
     */
    void tick() {

    }

    /**
     * This method will be used to load the memory with a ROM file on disk.
     */
    void load(Path file) {
        byte[] fileArray;
        
        try {
            fileArray = Files.readAllBytes(file);

            // iterate and read the bytes and transfer to memory
            for (int i = 0; i < fileArray.length; i++) {
                memory[0x200 + i] = (char) (0 | fileArray[i]);
            }

        } catch (IOException e) {
    
        }
    
    }
}

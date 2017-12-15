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
        // fetch next opcode
        current_opcode = (char) ((memory[program_counter] << 8) | memory[program_counter + 1]);

        // Decode Opcode

        // possible initial nibble: 0, 1, 2, 3,4, 5, 6, 7, 8, 9, A, B, C, D, E, F
        switch (current_opcode & 0xF000) {
            
            case 0x0000:

                switch (current_opcode & 0x000F) {

                    case 0x0000: // Clears the screen.
                        break;

                    case 0x000E: // Returns from a subroutine.
                        break;
                }

                break;
                
            case 0x1000: // 1NNN: Jumps to address NNN.
                break;
            
            case 0x2000: // 2NNN: Calls subroutine at NNN.
                break;
                    
            case 0x3000: // 3XNN: Skips the next instruction if VX = NN.
                break;
                
            case 0x4000: // 4XNN: Skips the next instruction if VX != NN.
                break;

            case 0x5000: // 5xNN: Skips the next instruction if VX = VY.
                break;

            case 0x6000: // 6XNN: Sets VX to NN.
                break;

            case 0x7000: // 7XNN: Adds NN to VX. (Carry flag is not changed)
                break;

            case 0x8000: // possible ending nibbles: 0, 1, 2, 3, 4, 5, 6 , 7, E

                switch (current_opcode & 0x000F) {

                    case 0x0000: // 8XY0: Sets VX to the value of VY.
                        break;
                        
                    case 0x0001: // 8XY1: Sets VX to VX or VY. (Bitwise OR operation)
                        break;
                        
                    case 0x0002: // 8XY2: Sets VX to VX and VY. (Bitwise AND operation)
                        break;
                        
                    case 0x0003: // 8XY3: Sets VX to VX xor VY.
                        break;
                        
                    case 0x0004: // 8XY4: Adds VY to VX. VF is set to 1 when there's a carry, and to 0 when there isn't.
                        break;

                    case 0x0005: // 8XY5: VY is subtracted from VX. VF is set to 0 when there's a borrow, and 1 when there isn't.
                        break;

                    case 0x0006: // 8XY6: Shifts VY right by one and copies the result to VX. VF is set to the value of the least significant bit of VY before the shift.[2]
                        break;

                    case 0x0007: // 8XY7: Sets VX to VY minus VX. VF is set to 0 when there's a borrow, and 1 when there isn't.
                        break;

                    case 0x000E: // 8XYE: Shifts VY < 1, copies to VX. Sets VF to most sig bit of VY before shift.
                        break;
            }
                break;

            case 0x9000: // 9XY0: Skips the next instruction if VX doesn't equal VY. (Usually the next instruction is a jump to skip a code block)
                break;

            case 0xA000: // ANNN: Sets I to the address NNN.
                break;

            case 0xB000: // BNNN: Jumps to the address NNN plus V0.
                break;

            case 0xC000: // CXNN: Sets VX to the result of & operation on a random number.
                break;

            case 0xD000: // DXYN:Draws a sprite at coorinate (VX, VY) with a width of 8 pix, and a height of N pix.
                break;

            case 0xE000:

                // Possible ending nibbles: E, 1.
                switch(current_opcode & 0x000F) {

                    case 0x00E: // EX9E: Skips the next instruction if the key stored in VX is pressed.
                        break;

                    case 0x001: // EXA1: Skips the next instruction if the key stored in VX isn't pressed.
                        break;
            }
                break;

            case 0xF000

                // possible ending nibbles: 07, 0A,
                switch(current_opcode & 0x00FF) {
                    
                    case 0x0007: // FX07: Sets VX to the value of the delay timer.
                        break;

                    case 0x000A: // FX0A: A key press is awaited, then stored in VX.
                        break;
                        
                    case 0x0015: // FX15: Sets the delay timer to VX.
                        break;
                    
                    case 0x0018: // FX18: Sets the sound timer to VX.
                        break;
                    
                    case 0x001E: // FX1E: Adds VX to I.
                        break;
                        
                    case 0x0029: // FX29: Sets I to the location f the sprite for the character in VX.
                        break;
                        
                    case 0x0033: // FX33: Stores the binary-coded decimal representation of VX, with the most significant of three digits at the address in I, the middle digit at I plus 1, and the least significant digit at I plus 2
                        break;

                    case 0x0055: // FX55: Stores V0 VX in memory starting at address I. Is increased by 1 for each value written.
                        break;

                    case 0x0065: // FX65: Fills V0 through VX with values from memory starting at address I. Is increased by 1 for eaach value written.
                        break;

                    default:
                        throw new Exception("Invalid opcode!");
                        break;
                }
                break;
        }
        // Execute Opcode

        // Update timers
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

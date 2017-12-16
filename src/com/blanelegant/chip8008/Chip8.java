package com.blanelegant.chip8008;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

/**
 * This is the main instruction interpreter for the chip8008 emulator.
 */

public class Chip8 {

    // set up basic registers
    // char has length of 16 bits
    char current_instruction;

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
        current_instruction = 0; // Reset current opcode

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

        // Reset timers
    }

    /**
     * This method will be called for every clock cycle of the CPU we want to emulate.
     */
    void tick() throws UnknownOpcodeException {
        // fetch next opcode
        current_instruction = (char) ((memory[program_counter] << 8) | memory[program_counter + 1]);

        // Decode Opcode

        // possible initial nibble: 0, 1, 2, 3,4, 5, 6, 7, 8, 9, A, B, C, D, E, F
        switch (current_instruction & 0xF000) {
            
            case 0x0000:

                switch (current_instruction & 0x000F) {

                    case 0x0000: // 00E0: Clears the screen.
                        op00E0();
                        break;

                    case 0x000E: // 00EE: Returns from a subroutine.
                        op00EE();
                        break;
                }

                break;
                
            case 0x1000: // 1NNN: Jumps to address NNN.
                op1NNN();
                break;
            
            case 0x2000: // 2NNN: Calls subroutine at NNN.
                op2NNN();
                break;

            // 3XNN: Skips the next instruction if VX = NN.
            case 0x3000:
                op3XNN();
                break;
                
            case 0x4000: // 4XNN: Skips the next instruction if VX != NN.
                op4XNN();
                break;

            case 0x5000: // 5XY0: Skips the next instruction if VX = VY.
                op5XY0();
                break;

            case 0x6000: // 6XNN: Sets VX to NN.
                op6XNN();
                break;

            case 0x7000: // 7XNN: Adds NN to VX. (Carry flag is not changed)
                op7XNN();
                break;

            case 0x8000: // possible ending nibbles: 0, 1, 2, 3, 4, 5, 6 , 7, E

                switch (current_instruction & 0x000F) {

                    case 0x0000: // 8XY0: Sets VX to the value of VY.
                        op8XY0();
                        break;
                        
                    case 0x0001: // 8XY1: Sets VX to VX or VY. (Bitwise OR operation)
                        op8XY1();
                        break;
                        
                    case 0x0002: // 8XY2: Sets VX to VX and VY. (Bitwise AND operation)
                        op8XY2();
                        break;
                        
                    case 0x0003: // 8XY3: Sets VX to VX xor VY.
                        op8XY3();
                        break;
                        
                    case 0x0004: // 8XY4: Adds VY to VX. VF is set to 1 when there's a carry, and to 0 when there isn't.
                        op8XY4();
                        break;

                    case 0x0005: // 8XY5: VY is subtracted from VX. VF is set to 0 when there's a borrow, and 1 when there isn't.
                        op8XY5();
                        break;

                    case 0x0006: // 8XY6: Shifts VY right by one and copies the result to VX. VF is set to the value of the least significant bit of VY before the shift.[2]
                        op8XY6();
                        break;

                    case 0x0007: // 8XY7: Sets VX to VY minus VX. VF is set to 0 when there's a borrow, and 1 when there isn't.
                        op8XY7();
                        break;

                    case 0x000E: // 8XYE: Shifts VY < 1, copies to VX. Sets VF to most sig bit of VY before shift.
                        op8XYE();
                        break;
            }
                break;

            case 0x9000: // 9XY0: Skips the next instruction if VX doesn't equal VY. (Usually the next instruction is a jump to skip a code block)
                op9XY0();
                break;

            case 0xA000: // ANNN: Sets I to the address.
                opANNN();
                break;

            case 0xB000: // BNNN: Jumps to the address NNN plus V0.
                opBNNN();
                break;

            case 0xC000: // CXNN: Sets VX to the result of & operation on a random number.
                opCXNN();
                break;

            case 0xD000: // DXYN:Draws a sprite at coordinate (VX, VY) with a width of 8 pix, and a height of N pix.
                opDXYN();
                break;

            case 0xE000:

                // Possible ending nibbles: E, 1.
                switch(current_instruction & 0x000F) {

                    case 0x00E: // EX9E: Skips the next instruction if the key stored in VX is pressed.
                        opEX9E();
                        break;

                    case 0x001: // EXA1: Skips the next instruction if the key stored in VX isn't pressed.
                        opEXA1();
                        break;
            }
                break;

            case 0xF000:

                // possible ending nibbles: 07, 0A,
                switch(current_instruction & 0x00FF) {
                    
                    case 0x0007: // FX07: Sets VX to the value of the delay timer.
                        opFX07();
                        break;

                    case 0x000A: // FX0A: A key press is awaited, then stored in VX.
                        opFX0A();
                        break;
                        
                    case 0x0015: // FX15: Sets the delay timer to VX.
                        opFX15();
                        break;
                    
                    case 0x0018: // FX18: Sets the sound timer to VX.
                        opFX18();
                        break;
                    
                    case 0x001E: // FX1E: Adds VX to I.
                        opFX1E();
                        break;
                        
                    case 0x0029: // FX29: Sets I to the location f the sprite for the character in VX.
                        opFX29();
                        break;
                        
                    case 0x0033: // FX33: Stores the binary-coded decimal representation of VX, with the most significant of three digits at the address in I, the middle digit at I plus 1, and the least significant digit at I plus 2
                        opFX33();
                        break;

                    case 0x0055: // FX55: Stores V0 VX in memory starting at address I. Is increased by 1 for each value written.
                        opFX55();
                        break;

                    case 0x0065: // FX65: Fills V0 through VX with values from memory starting at address I. Is increased by 1 for eaach value written.
                        opFX65();
                        break;

                    default:
                        throw new UnknownOpcodeException();
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

    /**
     * Clears the screen.
     */
    private void op00E0() {
        // TODO: graphics code to clear the screen goes here
    }

    /**
     * 00EE: Returns from a subroutine.
     */
    private void op00EE() {

    }

    /***
     * // 1NNN: Jumps to address NNN.
     */
    private void op1NNN() {
        program_counter = (char)(current_instruction & 0x0FFF);
    }

    /***
     * Calls subroutine at NNN.
     */
    private void op2NNN() {
        // put current pc on the stack
        stack[stack_pointer] = (char) (program_counter + 1);
        stack_pointer++;
        program_counter = (char) ((current_instruction & 0x0FFF));
    }

    /***
     * 3XNN
     * Skips the next instruction if VX equals NN.
     * (Usually the next instruction is a jump to skip a code block)
     */
    private void op3XNN() {
        char immediate = (char)(current_instruction & 0x00FF);
        
        if ((register[(current_instruction & 0x0F00) >>> 8]) == immediate)
            program_counter++;
    }

    private void op4XNN() {
        
    }

    private void op5XY0() {
        
    }

    private void op6XNN() {
        
    }

    private void op7XNN() {
        
    }

    /***
     * Sets VX to the value of VY.
     */
    private void op8XY0() {
        char x = (char) ((current_instruction & 0x0F00) >>> 8);
        char y = (char) ((current_instruction & 0x00F0) >>> 4);

        register[x] = register[y];
    }

    /***
     * Sets VX to VX or VY. (Bitwise OR operation)
     */
    private void op8XY1() {
        char x = (char) ((current_instruction & 0x0F00) >>> 8);
        char y = (char) ((current_instruction & 0x00F0) >>> 4);

        register[x] = (char) (register[x] | register[y]);
    }

    /***
     * Sets VX to VX and VY. (Bitwise AND operation)
     */
    private void op8XY2() {
        char x = (char) ((current_instruction & 0x0F00) >>> 8);
        char y = (char) ((current_instruction & 0x00F0) >>> 4);

        register[x] = (char) (register[x] & register[y]);
    }

    /**
     * Sets VX to VX xor VY. (Bitwise XOR operation)
     */
    private void op8XY3() {
        char x = (char) ((current_instruction & 0x0F00) >>> 8);
        char y = (char) ((current_instruction & 0x00F0) >>> 4);

        register[x] = (char) (register[x] ^ register[y]);

    }

    /**
     * Adds VY to VX.
     * VF is set to 1 when there's a carry, and to 0 when there isn't.
     */
    private void op8XY4() {
        char x = (char) ((current_instruction & 0x0F00) >>> 8);
        char y = (char) ((current_instruction & 0x00F0) >>> 4);

        char sum = (char) (register[x] + register[y]); // sum

        // set carry register
        if ((sum & 0xFF00) != 0) {
            register[0xF] = 0x1;
        } else {
            register[0xF] = 0x0;
        }

        register[x] = (char) (sum & 0x00FF); // Removes any overflow.
    }

    /**
     *VY is subtracted from VX. VF is set to 0 when there's a borrow, and 1 when there isn't.
     */
    private void op8XY5() {
        char x = (char) ((current_instruction & 0x0F00) >>> 8);
        char y = (char) ((current_instruction & 0x00F0) >>> 4);

        char difference = (char) (register[x] - register[y]); // Difference

        // Set borrow register.
        if ((difference & 0xFF00) != 0) {
            register[0xF] = 1;
        } else {
            register[0xF] = 0;
        }
    }
    /**
    * Shifts VY right by one and copies the result to VX. VF is set to the value of the least significant bit of VY
     * before the shift.
     */

    private void op8XY6() {
        char x = (char) ((current_instruction & 0x0F00) >>> 8);
        char y = (char) ((current_instruction & 0x00F0) >>> 4);

        register[0xF] = (char) (register[y] & 0x0001); // Set flag.

        register[x] = (char) (register[y] >>> 1); // Right shift y set to x.
    }

    /**
     * Sets VX to VY minus VX. VF is set to 0 when there's a borrow, and 1 when there isn't.
     */
    private void op8XY7() {
        char x = (char) ((current_instruction & 0x0F00) >>> 8);
        char y = (char) ((current_instruction & 0x00F0) >>> 4);

        register[x] = (char) (register[y] - register[x]);

        // Implement borrow flag.
    }

    /**
     *Shifts VY left by one and copies the result to VX. VF is set to the value of the most significant bit of VY before
     *  the shift.
     */
    private void op8XYE() {
        char x = (char) ((current_instruction & 0x0F00) >>> 8);
        char y = (char) ((current_instruction & 0x00F0) >>> 4);

        register[0xF] = (char) (register[y] & 0x0080); // Set flag.

        register[x] = (char) (register[y] << 1); // Left shift by one.
    }

    /**
     *Skips the next instruction if VX doesn't equal VY.
     */
    private void op9XY0() {
        char x = (char) ((current_instruction & 0x0F00) >>> 8);
        char y = (char) ((current_instruction & 0x00F0) >>> 4);

        if (register[x] != register[y]) {

            program_counter++;
        }
    }

    /**
     *Sets I to the address NNN.
     */
    private void opANNN() {
        index = (char) (current_instruction & 0x0FFF);
    }

    /**
     * Jumps to the address NNN plus V0.
     */
    private void opBNNN() {
        program_counter = (char) (register[0x0] + (current_instruction & 0x0FFF));
    }

    /**
     *Sets VX to the result of a bitwise and operation on a random number (Typically: 0 to 255) and NN.
     */
    private void opCXNN() {
        char x = (char) ((current_instruction & 0x0F00) >>> 8);
        char immediate = (char) (current_instruction & 0x00FF);

        Random random = new Random();

        register[x] = (char) (random.nextInt(255) & immediate);
    }

    /**
     * Draws a sprite at coordinate (VX, VY) that has a width of 8 pixels and a height of N pixels. Each row of 8 pixels
     * is read as bit-coded starting from memory location I; I value doesn’t change after the execution of this
     * instruction. As described above, VF is set to 1 if any screen pixels are flipped from set to unset when the
     * sprite is drawn, and to 0 if that doesn’t happen
     */
    private void opDXYN() {
        char x = (char) ((current_instruction & 0x0F00) >>> 8);
        char y = (char) ((current_instruction & 0x00F0) >>> 4);
        char immediate = (char) (current_instruction & 0x000F);

        // TODO: implement graphics code that draws pixels here
//        graphics(register[x], register[y], immediate);

        // TODO: Set register[F] to 1 if pixels are flipped from set to unset, 0 if not.
    }

    /**
     *Skips the next instruction if the key stored in VX isn't pressed.
     */
    private void opEX9E() {
        // Come back to when UI.
    }

    /**
     * Skips the next instruction if the key stored in VX isn't pressed.
     * (Usually the next instruction is a jump to skip a code block)
     */
    private void opEXA1() {
        char x = (char) ((current_instruction & 0x0F00) >>> 8);
    }

    /**
     * Sets VX to the value of the delay timer.
     */
    private void opFX07() {
        char x = (char) ((current_instruction & 0x0F00) >>> 8);
    }

    /**
     * 	A key press is awaited, and then stored in VX.
     * 	(Blocking Operation. All instruction halted until next key event)
     */
    private void opFX0A() {
        char x = (char) ((current_instruction & 0x0F00) >>> 8);
    }

    /**
     * Sets the delay timer to VX.
     */
    private void opFX15() {
        char x = (char) ((current_instruction & 0x0F00) >>> 8);

        delay_timer = register[x];
    }

    /**
     * Sets the sound timer to VX.
     */
    private void opFX18() {
        char x = (char) ((current_instruction & 0x0F00) >>> 8);

        sound_timer = register[x];
    }

    /**
     * Adds VX to I.
     */
    private void opFX1E() {
        char x = (char) ((current_instruction & 0x0F00) >>> 8);
        char sum = (char) ((register[x] + index) & 0x00FF);

        index = (char) (sum & 0x00FF); // chop off first 8 bits
    }

    /**
     * 	Sets I to the location of the sprite for the character in VX.
     * 	Characters 0-F (in hexadecimal) are represented by a 4x5 font.
     */
    private void opFX29() {
        char x = (char) ((current_instruction & 0x0F00) >>> 8);

        // TODO: implement pointers to location of letters
    }

    /**
     * Stores the binary-coded decimal representation of VX, with the most
     * significant of three digits at the address in I, the middle digit at
     * I plus 1, and the least significant digit at I plus 2. (In other words,
     * take the decimal representation of VX, place the hundreds digit in memory
     * at location in I, the tens digit at location I+1, and the ones digit at
     * location I+2.)
     */
    private void opFX33() {
        // TODO: implement this monstrous beast
    }

    /**
     *Stores V0 to VX (including VX) in memory starting at address I. I is increased by 1 for each value written.
     */
    private void opFX55() {
        char x = (char) ((current_instruction & 0x0F00) >>> 8);

        for (int i = 0; i <= x; i++) {
            memory[index] = register[i]; // copy register to memory location
            index++; // increment index pointer
        }
    }

    /**
     * Fills V0 to VX (including VX) with values from memory starting at
     * address I. I is increased by 1 for each value written.
     */
    private void opFX65() {
        char x = (char) ((current_instruction & 0x0F00) >>> 8);

        for (int i = 0; i <= x; i++) {
            register[i] = memory[index]; // copy value at memory location to register
            index++;
        }
    }

}

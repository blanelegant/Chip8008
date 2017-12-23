package com.blanelegant.chip8008;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.PixelWriter;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class Main extends Application {

    // global constants
    static final int SCALE_FACTOR = 10; // amount to scale the native 64x32 display by

    static GraphicsContext gc; // needs to be instance member so we can access in methods other than main
    static Chip8 myChip8;

    static boolean run;

    /**
     * Entry point into the application.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // write your code here

        // initialize CHIP-8 emulation core
        myChip8 = new Chip8();

        myChip8.initialize();

        launch(args); // launch main application UI

        // main emulation loop
        while(run) {
            myChip8.tick();

            // If the draw flag is set, update the screen
            if(myChip8.drawFlag) {
                updateScreen();
            }

            // Store key press state (Press and Release)
//            myChip8.setKeys();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Chip 8008 Emulator");

        /* UI set up */
//        Label label = new Label("Hello World, JavaFX !");

        // instantiate UI components
        Button fileOpenButton = new Button("Open ROM");
        Button pauseButton = new Button("Start"); // default text
        final Canvas screen = new Canvas(64 * SCALE_FACTOR, 32 * SCALE_FACTOR); // 10x
        gc = screen.getGraphicsContext2D(); // needed to access the canvas
        HBox toolbar = new HBox(fileOpenButton, pauseButton); // add toolbar with button

        PixelWriter pixelWriter = gc.getPixelWriter(); // will be used to write the individual pixels

        // set up main layout container
        BorderPane borderPane = new BorderPane();

        // add UI components to container
        borderPane.setTop(toolbar);
        borderPane.setCenter(screen);

        // open button listener
        fileOpenButton.setOnMouseClicked(event -> {

            JFileChooser chooser = new JFileChooser(".");
            chooser.setMultiSelectionEnabled(false);
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            // file extension filter
            FileFilter filter = new FileNameExtensionFilter("CHIP-8 ROM", "ch8", "c8", "rom");
            chooser.addChoosableFileFilter(filter);
            chooser.setFileFilter(filter);

            int ret = chooser.showOpenDialog(null);
            if(ret == JFileChooser.APPROVE_OPTION) {
                myChip8.load(chooser.getSelectedFile().toPath());
            }

        });

        // open button listener
        pauseButton.setOnMouseClicked(event -> {
            run = (run) ? false : true;
            pauseButton.setText((run) ? "Pause" : "Run");
        });

        // set background color
//        screen.setStyle("-fx-background-color: black");

        // final steps, create window and show
        Scene scene = new Scene(borderPane, (64 * SCALE_FACTOR), (32 * SCALE_FACTOR) + 32);
        primaryStage.setScene(scene);
        primaryStage.show(); // show window
    }

    /**
     * This method should read the chip-8 graphics buffer and write the canvas's pixels
     */
    public static void updateScreen() {

        // update row
        for (int row = 0; row < 64; row++) { // iterate through rows
            // update column
            for (int column = 0; column < 32; column++) { // iterate through columns
                if (myChip8.graphics[(row * 64) + column] == 0x01) { // if pixel == 1
                    drawPixel(row, column, Color.GRAY); // color the pixel
                } else {
                    drawPixel(row, column, Color.BLACK);
                }
            }
        }
    }

    /**
     * This method should draw a single CHIP-8 pixel onto the canvas.
     * @param x the x-coordinate of the pixel to draw
     * @param y the y-coordinate of the pixel to draw
     */
    private static void drawPixel(int x, int y, Color color) {
        for (int row = 0; row < SCALE_FACTOR; row++) { // update row / vertical lines
            for (int column = 0; column < SCALE_FACTOR; column++) { // update column / horizontal lines
                gc.getPixelWriter().setColor((x * SCALE_FACTOR) + column, (y * SCALE_FACTOR) + row, color);
            }
        }
    }
}

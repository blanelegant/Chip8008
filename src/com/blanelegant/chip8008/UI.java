package com.blanelegant.chip8008;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.PixelWriter;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class UI extends Application{

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Chip 8008 Emulator");

        /* UI set up */
//        Label label = new Label("Hello World, JavaFX !");

        // instantiate UI components
        Button fileOpenButton = new Button("Open ROM");
        final Canvas screen = new Canvas(640, 320); // 10x
        GraphicsContext gc = screen.getGraphicsContext2D();
        HBox toolbar = new HBox(fileOpenButton); // add toolbar with button

        PixelWriter pixelWriter = gc.getPixelWriter();

        // set up main layout container
        BorderPane borderPane = new BorderPane();

        // add UI components to container
        borderPane.setTop(fileOpenButton);
        borderPane.setCenter(screen);

        // final steps, create window and show
        Scene scene = new Scene(borderPane, 400, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void updateScreen() {
        for (int pixel = 0; pixel < (64 * 32); pixel++) {

        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package physicsdemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import simulation.Box;
import simulation.Simulation;

public class PhysicsDemo extends Application implements simulation.Constants{

    @Override
    public void start(Stage primaryStage) {
        GamePane root = new GamePane();
        Simulation sim = new Simulation(300, 250, 2, 2);
        root.setShapes(sim.setUpShapes());
        
        Scene scene = new Scene(root, 300, 250);
        root.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case DOWN:
                    sim.moveInner(0, 3);
                    break;
                case UP:
                    sim.moveInner(0, -3);
                    break;
                case LEFT:
                    sim.moveInner(-3, 0);
                    break;
                case RIGHT:
                    sim.moveInner(3, 0);
                    break;
            }
        });
        root.requestFocus(); 

        primaryStage.setTitle("Game Physics");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest((event)->System.exit(0));
        primaryStage.show();

        // This is the main animation thread
        new Thread(() -> {
            while (true) {
                sim.evolve(1.0);
                Platform.runLater(()->sim.updateShapes());
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ex) {

                }
            }
        }).start();
    }
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    class handleClient implements Runnable {

        private Socket socket;
        private Box paddle;

        public handleClient(Socket socket, Box paddle) {
            this.socket = socket;
            this.paddle = paddle;
        }

        public void run() {
            BufferedReader in = null;
            PrintWriter out = null;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream());
                while (true) {
                    int request = Integer.parseInt(in.readLine());
                    switch (request) {
                        case (GET_INFO):
                            break;
                        case (GET_COLOR):
                            break;
                        case (WAIT):
                            break;
                        case (MOVE):
                            break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

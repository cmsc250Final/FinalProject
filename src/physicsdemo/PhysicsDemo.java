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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import simulation.Simulation;

public class PhysicsDemo extends Application implements simulation.Constants{
    
    private Simulation sim;
    private ReentrantLock lock;
    private Condition readyToStart;
    private int numClients = 0;
    
    
    public synchronized void waitForClients() {
            while(numClients<2) {
                try{Thread.sleep(20);}catch(Exception e){}
            }
        }
    @Override
    public void start(Stage primaryStage) {
        lock = new ReentrantLock();
        readyToStart = lock.newCondition();
        GamePane root = new GamePane();
        sim = new Simulation(300, 250, 1, 1);
        root.setShapes(sim.setUpShapes());
        
        Scene scene = new Scene(root, 300, 250);
        root.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case DOWN:
                    sim.moveInner(0, 3,BLUE);
                    break;
                case UP:
                    sim.moveInner(0, -3,BLUE);
                    break;
                case LEFT:
                    sim.moveInner(-3, 0,BLUE);
                    break;
                case RIGHT:
                    sim.moveInner(3, 0,BLUE);
                    break;
                case S:
                    sim.moveInner(0, 3,RED);
                    break;
                case W:
                    sim.moveInner(0, -3,RED);
                    break;
                case A:
                    sim.moveInner(-3, 0,RED);
                    break;
                case D:
                    sim.moveInner(3, 0,RED);
                    break;
            }
        });
        root.requestFocus(); 

        primaryStage.setTitle("Game Physics");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest((event)->System.exit(0));
        primaryStage.show();
        try{ 
            ServerSocket serversocket = new ServerSocket(3200);
            Socket socket = serversocket.accept();
            new Thread(new handleClient(socket,BLUE)).start();
            numClients++;
            Socket socket2 = serversocket.accept();
            new Thread(new handleClient(socket2,RED)).start();
            numClients++;
            System.out.println(socket);
            System.out.println(socket2);
            new Thread(new randomMover(BLUE)).start();
            new Thread(new randomMover(RED)).start();
        } catch (IOException e) {
                e.printStackTrace();;
        } finally {
        }
        
        // This is the main animation thread
        new Thread(() -> {
            while (true) {
                sim.evolve(1.0);
                Platform.runLater(()->sim.updateShapes());
                try {
                    Thread.sleep(20);
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
    private class handleClient implements Runnable {

        private Socket hcsocket;
        private int hccolor;

        public handleClient(Socket newsocket, int newcolor) {
            hcsocket = newsocket;
            hccolor = newcolor;
            System.out.println("Thread "+ hccolor);
        }

        public void run() {
            BufferedReader in = null;
            PrintWriter out = null;
            try {
                in = new BufferedReader(new InputStreamReader(hcsocket.getInputStream()));
                out = new PrintWriter(hcsocket.getOutputStream());
                while (true) {
                    int request = Integer.parseInt(in.readLine());   
                    System.out.println(request);
                    switch (request) {
                        case (GET_INFO):
                            for(int i:sim.sendChangingValues()) {
                                out.println(i);
                            }
                            break;
                        case (GET_COLOR):
                            out.println(hccolor);
                            break;
                        case (WAIT):
                            waitForClients();
                            out.println(START);
                            break;
                        case (MOVE):
                            int direction = Integer.parseInt(in.readLine());
                            switch (direction) {
                                case DOWN:
                                    sim.moveInner(0, 3, hccolor);
                                    break;
                                case UP:
                                    sim.moveInner(0, -3, hccolor);
                                    break;
                                case LEFT:
                                    sim.moveInner(-3, 0, hccolor);
                                    break;
                                case RIGHT:
                                    sim.moveInner(3, 0, hccolor);
                                    break;
                            }
                            break;
                    } out.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }
        }
    }
    
    private class randomMover implements Runnable {
        private int colorToMove;
        private randomMover(int c) {
            colorToMove = c;
        }
        public void run() {
            while(true) {
            int score = sim.getScore(colorToMove);
            int rand = (int) (Math.random()*20)+1;
            if(score-4>=rand) {
                int distance = (int) (Math.random()*5)+1;
                int direction = (int) (Math.random()*4);
                if(direction==0)
                    sim.moveInner(0,distance,colorToMove);
                else if(direction==1)
                    sim.moveInner(0,-distance,colorToMove);
                else if(direction==2)
                    sim.moveInner(distance,0,colorToMove);
                else if(direction==3)
                    sim.moveInner(-distance, 0, colorToMove);
            }
            try {
                Thread.sleep(100);
            } catch(InterruptedException e) {
                
            }
        }
        }
    }

}

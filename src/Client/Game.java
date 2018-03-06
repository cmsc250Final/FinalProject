package Client;

import java.util.concurrent.locks.Lock;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import physicsdemo.GamePane;
import simulation.Ball;
import simulation.Box;
import static simulation.Constants.START;
import simulation.Goal;

public class Game extends Application implements simulation.Constants {

    private Box outer;
    private Ball ball;
    private Box redPaddle;
    private Box bluePaddle;
    private Lock lock;
    private Goal blueGoal;
    private Goal redGoal;
    private int redScore;
    private int blueScore;
    ChatGateway client = new ChatGateway();

    public static final int MOVE = 12;
    public static final int LEFT = 5;
    public static final int RIGHT = 6;
    public static final int UP = 7;
    public static final int DOWN = 8;
    public static final int GET_COLOR = 9;
    public static final int GET_INFO = 4; //[blue paddle],[red paddle],[ball],blueScore,redScore
    public static final int RED = 2;
    public static final int BLUE = 1;
    public static final int WAIT = 10;
    public static final int START = 11;

    @Override
    public void start(Stage primaryStage) {
        GamePane root = new GamePane();
        Scene scene = new Scene(root, 300, 250);
        root.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case DOWN:
                    client.moveDown();
                    break;
                case UP:
                    client.moveUp();
                    break;
                case LEFT:
                    client.moveLeft();
                    break;
                case RIGHT:
                    client.moveRight();
                    break;
            }
        });

        root.requestFocus();

        primaryStage.setTitle("Game Physics");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest((event) -> System.exit(0));
        primaryStage.show();

        new Thread(new TranscriptCheck(client, root)).start();

    }

    public static void main(String[] args) {
        launch(args);
    }

}

class TranscriptCheck implements Runnable, simulation.Constants {

    private ChatGateway client;
    private Rectangle outer;
    private Circle ball;
    private Rectangle redPaddle;
    private Rectangle bluePaddle;
    private GamePane root;

    public TranscriptCheck(ChatGateway gateway, GamePane root) {
        this.client = gateway;
        ball = new Circle(0, 0, 4);
        ball.setFill(Color.RED);
        outer = new Rectangle(0, 0, 300, 250);
        outer.setFill(Color.WHITE);
        outer.setStroke(Color.BLACK);
        redPaddle = new Rectangle(0, 0, 40, 20);
        redPaddle.setFill(Color.RED);
        redPaddle.setStroke(Color.BLACK);
        bluePaddle = new Rectangle(0, 0, 40, 20);
        bluePaddle.setFill(Color.WHITE);
        bluePaddle.setStroke(Color.BLUE);
        this.root = root;
    }

    /**
     * Run a thread
     */
    public void run() {
        while (true) {
            if (client.sendWait() == START) {
                String info[] = client.getInfo();
                int redPaddleX = Integer.parseInt(info[0]);
                int redPadleY = Integer.parseInt(info[1]);
                int bluePaddleX = Integer.parseInt(info[2]);
                int bluePaddleY = Integer.parseInt(info[3]);
                int ballX = Integer.parseInt(info[4]);
                int ballY = Integer.parseInt(info[5]);
                String redScore = info[6];
                String blueScore = info[7];
                Platform.runLater(()-> {
                ball.setCenterX(ballX);
                ball.setCenterY(ballY);
                redPaddle.setX(redPaddleX);
                redPaddle.setY(redPadleY);
                bluePaddle.setX(bluePaddleX);
                bluePaddle.setY(bluePaddleY);
                root.getChildren().clear();
                root.getChildren().add(ball);
                root.getChildren().add(redPaddle);
                root.getChildren().add(bluePaddle);
                });


            } else {
                try {
                    Thread.sleep(2);
                } catch (InterruptedException ex) {
                }
            }
        }
    }
}

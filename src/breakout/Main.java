package breakout;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.util.Duration;


/**
 * An introductory JavaFX program to run a game of breakout.
 *
 * @author Thomas Owens
 * Starter code taken from the first lab code, written by Robert C. Duvall
 */
public class Main extends Application {
    public static final String TITLE = "Alien Breakout";
    public static final int HEIGHT = 600;
    public static final int WIDTH = 400;
    public static final int FRAMES_PER_SECOND = 60;
    public static final int MILLISECOND_DELAY = 1000 / FRAMES_PER_SECOND;
    public static final double SECOND_DELAY = 1.0 / FRAMES_PER_SECOND;
    public static final Paint BACKGROUND = new Color(0.1, 0.1, 0.1, 1);
    public static final String BALL_IMAGE = "ball.gif";
    public static final int BALL_SPEED = 100;
    public static final int PADDLE_SIZE = 50;
    public static final int PADDLE_SPEED = 5;
    public static final Paint PADDLE_COLOR = Color.WHITE;

    // some things needed to remember during game
    private Scene myScene;
    private ImageView myBall;
    private Rectangle myPaddle;
    private double[] ballDirection = new double[]{0.6, 0.8};


    /**
     * Initialize what will be displayed and how it will be updated.
     */
    @Override
    public void start (Stage stage) {
        // attach scene to the stage and display it
        myScene = setupGame(WIDTH, HEIGHT, BACKGROUND);
        stage.setScene(myScene);
        stage.setTitle(TITLE);
        stage.show();
        // attach "game loop" to timeline to play it (basically just calling step() method repeatedly forever)
        KeyFrame frame = new KeyFrame(Duration.millis(MILLISECOND_DELAY), e -> step(SECOND_DELAY));
        Timeline animation = new Timeline();
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.getKeyFrames().add(frame);
        animation.play();
    }

    // Create the game's "scene": what shapes will be in the game and their starting properties
    private Scene setupGame (int width, int height, Paint background) {
        // create one top level collection to organize the things in the scene
        Group root = new Group();
        // make some shapes and set their properties
        Image image = new Image(this.getClass().getClassLoader().getResourceAsStream(BALL_IMAGE));
        myBall = new ImageView(image);
        // x and y represent the top left corner, so center it in window
        myBall.setX(width / 2. - myBall.getBoundsInLocal().getWidth() / 2);
        myBall.setY(height / 2. - myBall.getBoundsInLocal().getHeight() / 2);

        myPaddle = new Rectangle(width / 2.0 - PADDLE_SIZE / 2.0, height - 50, PADDLE_SIZE, 10);
        myPaddle.setFill(PADDLE_COLOR);

        // order added to the group is the order in which they are drawn
        root.getChildren().add(myBall);
        root.getChildren().add(myPaddle);
        // create a place to see the shapes
        Scene scene = new Scene(root, width, height, background);
        // respond to input
        scene.setOnKeyPressed(e -> handleKeyInput(e.getCode()));
        scene.setOnMouseClicked(e -> handleMouseInput(e.getX(), e.getY()));
        return scene;
    }

    // Change properties of shapes in small ways to animate them over time
    // Note, there are more sophisticated ways to animate shapes, but these simple ways work fine to start
    private void step (double elapsedTime) {
        // update "actors" attributes
        moveBall(elapsedTime);

        // check for collision between ball and paddle
        if (myPaddle.getBoundsInParent().intersects(myBall.getBoundsInParent())) {
            System.out.println(myBall.getLayoutBounds().getMaxY() + " " + myPaddle.getLayoutBounds().getMinY());
            if (Math.round(myBall.getLayoutBounds().getMaxY()) <= myPaddle.getLayoutBounds().getMinY()) {
                // the ball is above the paddle
                ballDirection[1] *= -1;
            }
            if (Math.round(myBall.getLayoutBounds().getCenterY()) > myPaddle.getLayoutBounds().getMinY() &&
                Math.round(myBall.getLayoutBounds().getCenterY()) < myPaddle.getLayoutBounds().getMaxY()) {
                // the ball is on the side of the paddle
                ballDirection[0] *= -1;
            }
            else {
                // the ball is on the corner
                ballDirection[0] *= -1;
                ballDirection[1] *= -1;
            }
        }


        // with shapes, can check precisely
        // NEW Java 10 syntax that simplifies things (but watch out it can make code harder to understand)
        // var intersection = Shape.intersect(myMover, myGrower);
        // Shape intersection = Shape.intersect(myMover, myGrower);
    }

    private void moveBall(double elapsedTime) {
        myBall.setX(myBall.getX() + BALL_SPEED * elapsedTime * ballDirection[0]);
        myBall.setY(myBall.getY() + BALL_SPEED * elapsedTime * ballDirection[1]);

        if (myBall.getX() <= 0 || myBall.getX() >= WIDTH - myBall.getLayoutBounds().getWidth()) {
            ballDirection[0] *= -1;
        }
        if (myBall.getY() <= 0) {
            ballDirection[1] *= -1;
        }

        if (myBall.getY() >= HEIGHT - myBall.getLayoutBounds().getHeight()) {
            //System.out.println("Lose a life.");
            // TODO: lose a life
        }
    }

    // What to do each time a key is pressed
    private void handleKeyInput (KeyCode code) {
        if (code == KeyCode.RIGHT) {
            myPaddle.setX(myPaddle.getX() + PADDLE_SPEED);
        }
        else if (code == KeyCode.LEFT) {
            myPaddle.setX(myPaddle.getX() - PADDLE_SPEED);
        }
//        else if (code == KeyCode.UP) {
//            myMover.setY(myMover.getY() - MOVER_SPEED);
//        }
//        else if (code == KeyCode.DOWN) {
//            myMover.setY(myMover.getY() + MOVER_SPEED);
//        }
        // NEW Java 12 syntax that some prefer (but watch out for the many special cases!)
        //   https://blog.jetbrains.com/idea/2019/02/java-12-and-intellij-idea/
        // Note, must set Project Language Level to "13 Preview" under File -> Project Structure
        // switch (code) {
        //     case RIGHT -> myMover.setX(myMover.getX() + MOVER_SPEED);
        //     case LEFT -> myMover.setX(myMover.getX() - MOVER_SPEED);
        //     case UP -> myMover.setY(myMover.getY() - MOVER_SPEED);
        //     case DOWN -> myMover.setY(myMover.getY() + MOVER_SPEED);
        // }
    }

    // What to do each time a key is pressed
    private void handleMouseInput (double x, double y) {
//        if (myGrower.contains(x, y)) {
//            myGrower.setScaleX(myGrower.getScaleX() * GROWER_RATE);
//            myGrower.setScaleY(myGrower.getScaleY() * GROWER_RATE);
//        }
    }

    /**
     * Start the program.
     */
    public static void main (String[] args) {
        launch(args);
    }
}
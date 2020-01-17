package breakout;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;


/**
 * An introductory JavaFX program to run a game of breakout.
 *
 * @author Thomas Owens
 * Starter code taken from the first lab code, written by Robert C. Duvall
 */
public class Main extends Application {
    public static final String TITLE = "Alien Breakout";
    public static final int INFO_HEIGHT = 40;
    public static final int INFO_WIDTH = 20;
    public static final Paint INFO_COLOR = Color.rgb(0, 150, 255);
    public static final int HEIGHT = 600;
    public static final int WIDTH = 6 * 70 + 2 * INFO_WIDTH;
    public static final int FRAMES_PER_SECOND = 60;
    public static final int MILLISECOND_DELAY = 1000 / FRAMES_PER_SECOND;
    public static final double SECOND_DELAY = 1.0 / FRAMES_PER_SECOND;
    public static final Paint BACKGROUND = new Color(0.1, 0.1, 0.1, 1);
    public static final String BALL_IMAGE = "ball.gif";
    public static final int BALL_SPEED = 150;
    public static final int PADDLE_SIZE = 80;
    public static final int PADDLE_SPEED = 200;
    public static final Paint PADDLE_COLOR = Color.WHITE;
    public static final double BOUNCE_FACTOR = 0.03;
    public static final double MAX_BALL_SPEED = 400;
    public static final int BRICK_LAYERS = 10;

    // some things needed to remember during game
    private Scene myScene;
    private ImageView myBall;
    private Rectangle myPaddle;
//    private Group myLives;
    private double[] ballDirection;
    private int paddleVelocity;
    private int[] paddleInput;
    private int livesRemaining;
    private int myScore;

    private Group bricks;
    private Group root;
    private Group powerups;
    private Text livesDialog;
    private Text scoreDialog;


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
        root = new Group();

        Group infoBar = new Group();
        Rectangle infoTop = new Rectangle(0, 0, WIDTH, INFO_HEIGHT);
        Rectangle infoLeft = new Rectangle(0, 0, INFO_WIDTH, HEIGHT);
        Rectangle infoRight = new Rectangle(WIDTH-INFO_WIDTH, 0, INFO_WIDTH, HEIGHT);
        infoTop.setFill(INFO_COLOR);
        infoLeft.setFill(INFO_COLOR);
        infoRight.setFill(INFO_COLOR);

        infoBar.getChildren().add(infoTop);
        infoBar.getChildren().add(infoLeft);
        infoBar.getChildren().add(infoRight);

        livesRemaining = 3;
        livesDialog = new Text();
        livesDialog.setText("Lives Remaining: " + livesRemaining);
        livesDialog.setFont(Font.font("impact", 20));
        livesDialog.setX(INFO_WIDTH);
        livesDialog.setY((INFO_HEIGHT + livesDialog.getFont().getSize()) / 2.0);

        myScore = 0;
        scoreDialog = new Text();
        scoreDialog.setText("Score: " + myScore);
        scoreDialog.setFont(Font.font("impact", 20));
        scoreDialog.setX(5/8.0 * WIDTH);
        scoreDialog.setY((INFO_HEIGHT + livesDialog.getFont().getSize()) / 2.0);

        Image image = new Image(this.getClass().getClassLoader().getResourceAsStream(BALL_IMAGE));
        myBall = new ImageView(image);
        myBall.setX(width / 2. - myBall.getBoundsInLocal().getWidth() / 2);
        myBall.setY(height / 2. - myBall.getBoundsInLocal().getHeight() / 2);
        ballDirection = new double[]{0, 1};

        myPaddle = new Rectangle(width / 2.0 - PADDLE_SIZE / 2.0, height - 50, PADDLE_SIZE, 10);
        myPaddle.setFill(PADDLE_COLOR);
        paddleVelocity = 0;
        paddleInput = new int[]{0, 0};

        bricks = new Group();
        Brick layoutBrick = new Brick("brick1.gif", 0, 0, 1, 0, 0);

        for (int yOff = 1; yOff <= BRICK_LAYERS; yOff++) {
            for (int xOff = 0; xOff * layoutBrick.getBoundsInParent().getWidth() < WIDTH - 2*INFO_WIDTH; xOff++) {
                Brick brick = new Brick("brick" + (yOff % 4 + 1) + ".gif",
                        Math.floor(xOff * layoutBrick.getBoundsInParent().getWidth()) + INFO_WIDTH,
                        yOff * (layoutBrick.getBoundsInParent().getHeight()+1) + INFO_HEIGHT, 1, 10, 0);
                bricks.getChildren().add(brick);
            }
        }

        powerups = new Group();

        root.getChildren().add(myBall);
        root.getChildren().add(myPaddle);
        root.getChildren().add(bricks);
        root.getChildren().add(infoBar);
        root.getChildren().add(livesDialog);
        root.getChildren().add(scoreDialog);
        root.getChildren().add(powerups);
        //root.getChildren().add(myLives);

        Scene scene = new Scene(root, width, height, background);

        scene.setOnKeyPressed(e -> handleKeyPress(e.getCode()));
        scene.setOnKeyReleased(e -> handleKeyRelease(e.getCode()));
        return scene;
    }

    // Change properties of shapes in small ways to animate them over time
    // Note, there are more sophisticated ways to animate shapes, but these simple ways work fine to start
    private void step (double elapsedTime) {
        // update "actors" attributes
        moveBall(elapsedTime);
        movePaddle(elapsedTime);
        checkPaddleCollisions();
        checkBrickCollisions();

        // with shapes, can check precisely
        // NEW Java 10 syntax that simplifies things (but watch out it can make code harder to understand)
        // var intersection = Shape.intersect(myMover, myGrower);
        // Shape intersection = Shape.intersect(myMover, myGrower);
    }

    private void checkPaddleCollisions() {
        // check for collision between ball and paddle
        if (myPaddle.getBoundsInParent().intersects(myBall.getBoundsInParent())) {
            if (Math.floor(myBall.getBoundsInParent().getMaxY()) <= myPaddle.getBoundsInParent().getMinY()) {
                // the ball is above the paddle
                ballDirection[0] += ((myBall.getBoundsInParent().getCenterX() - myPaddle.getBoundsInParent().getCenterX()) *
                        BOUNCE_FACTOR);
                double newSpeed = Math.sqrt(ballDirection[0] * ballDirection[0] + ballDirection[1] * ballDirection[1]);
                if (newSpeed > MAX_BALL_SPEED) {
                    ballDirection[0] *= MAX_BALL_SPEED / newSpeed;
                }
                ballDirection[1] *= -1.01;
            }
            // TODO: Test this, it might be bugged.
            else if (Math.floor(myBall.getBoundsInParent().getMaxY()) > myPaddle.getBoundsInParent().getMinY() &&
                    Math.floor(myBall.getBoundsInParent().getMinY()) < myPaddle.getBoundsInParent().getMaxY()) {
                // the ball is on the side of the paddle
                ballDirection[0] *= -1;
                ballDirection[0] += paddleVelocity * ((float)PADDLE_SPEED / BALL_SPEED);
            }
        }
    }

    private void checkBrickCollisions() {
        boolean bounceY = false, bounceX = false;
        for (Node n : bricks.getChildren()) {
            Brick brick = (Brick) n;
            if (brick.getBoundsInParent().intersects(myBall.getBoundsInParent())) {
//                System.out.println(myBall.getBoundsInParent().getMinY() + " " + myBall.getBoundsInParent().getMaxY());
//                System.out.println(brick.getBoundsInParent().getMinY() + " " + brick.getBoundsInParent().getMaxY());
//                System.out.println();
                if (Math.ceil(myBall.getBoundsInParent().getCenterY()) <= brick.getBoundsInParent().getMinY() ||
                        Math.floor(myBall.getBoundsInParent().getCenterY()) >= brick.getBoundsInParent().getMaxY()) {
                    bounceY = true;
                } else {
                    bounceX = true;
                }

                brick.breakBrick();
                myScore += brick.getScore();
                scoreDialog.setText("Score: " + myScore);
            }
        }

        if (bounceX) {
            ballDirection[0] *= -1;
        }
        if (bounceY) {
            ballDirection[1] *= -1;
        }
    }

    private void moveBall(double elapsedTime) {
        myBall.setX(myBall.getX() + BALL_SPEED * elapsedTime * ballDirection[0]);
        myBall.setY(myBall.getY() + BALL_SPEED * elapsedTime * ballDirection[1]);

        if (myBall.getX() <= INFO_WIDTH || myBall.getX() >= WIDTH - INFO_WIDTH - myBall.getBoundsInParent().getWidth()) {
            ballDirection[0] *= -1;
        }
        if (myBall.getY() <= INFO_HEIGHT) {
            ballDirection[1] *= -1;
        }

        if (myBall.getY() >= HEIGHT - myBall.getBoundsInParent().getHeight()) {
            loseALife();
        }
    }

    private void movePaddle(double elapsedTime) {
        if (!myPaddle.getBoundsInParent().intersects(myBall.getBoundsInParent())) {
            if (paddleInput[0] == paddleInput[1]) {
                paddleVelocity = 0;
            } else if (paddleInput[0] == 1) {
                paddleVelocity = -1;
            } else if (paddleInput[1] == 1) {
                paddleVelocity = 1;
            }
            double newX = myPaddle.getX() + PADDLE_SPEED * elapsedTime * paddleVelocity;
            if (-1 + INFO_WIDTH <= newX && newX <= WIDTH - myPaddle.getWidth() - INFO_WIDTH) {
                myPaddle.setX(newX);
            }
        }
    }

    private void loseALife() {
        livesRemaining -= 1;
        livesDialog.setText("Lives Remaining: " + livesRemaining);

        myPaddle.setX((WIDTH - myPaddle.getLayoutBounds().getWidth()) / 2.0);
        myBall.setX((WIDTH - myBall.getLayoutBounds().getWidth()) / 2.0);
        myBall.setY((HEIGHT - myBall.getLayoutBounds().getHeight()) / 2.0);
        ballDirection[0] = 0;
        ballDirection[1] = 1;
    }

    // What to do each time a key is pressed
    private void handleKeyPress (KeyCode code) {
        if (code == KeyCode.RIGHT) {
            paddleInput[1] = 1;
        }
        else if (code == KeyCode.LEFT) {
            paddleInput[0] = 1;
        }

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

    private void handleKeyRelease (KeyCode code) {
        if (code == KeyCode.RIGHT) {
            paddleInput[1] = 0;
        }
        else if (code == KeyCode.LEFT) {
            paddleInput[0] = 0;
        }
    }

    /**
     * Start the program.
     */
    public static void main (String[] args) {
        launch(args);
    }

}

// TODO next: add power-ups (2 hours), add splash screen and enforce lives (2 hours), add levels (2 hours)
// TODO low priority: create new bricks (2 hours), add movement (way too long)
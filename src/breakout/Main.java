package breakout;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Arrays;


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
    public static final int PADDLE_SIZE = 80;
    public static final int PADDLE_SPEED = 200;
    public static final Paint PADDLE_COLOR = Color.WHITE;
    public static final double BOUNCE_FACTOR = 0.03;
    public static final int BRICK_LAYERS = 10;
    private static final double BALL_Y_SPEEDUP = 0.03;

    // some things needed to remember during game
    private Scene myScene;
    private Rectangle myPaddle;
    private int paddleVelocity;
    private int[] paddleInput;
    private int livesRemaining;
    private int myScore;

    private Group balls;
    private Group bricks;
    private Group powerups;
    private Group lasers;
    private Group explosions;
    private Group infoBar;
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
        Group root = new Group();

        initializeInfoBar();
        initializeLivesDialog();
        initializeScoreDialog();
        initializeBall(width, height);
        initializePaddle(width, height);
        initializeBricks();

        powerups = new Group();
        lasers = new Group();
        explosions = new Group();

        root.getChildren().add(balls);
        root.getChildren().add(lasers);
        root.getChildren().add(explosions);
        root.getChildren().add(myPaddle);
        root.getChildren().add(bricks);
        root.getChildren().add(infoBar);
        root.getChildren().add(livesDialog);
        root.getChildren().add(scoreDialog);
        root.getChildren().add(powerups);

        Scene scene = new Scene(root, width, height, background);

        scene.setOnKeyPressed(e -> handleKeyPress(e.getCode()));
        scene.setOnKeyReleased(e -> handleKeyRelease(e.getCode()));
        return scene;
    }

    private void initializeInfoBar() {
        infoBar = new Group();
        Rectangle infoTop = new Rectangle(0, 0, WIDTH, INFO_HEIGHT);
        Rectangle infoLeft = new Rectangle(0, 0, INFO_WIDTH, HEIGHT);
        Rectangle infoRight = new Rectangle(WIDTH-INFO_WIDTH, 0, INFO_WIDTH, HEIGHT);
        infoTop.setFill(INFO_COLOR);
        infoLeft.setFill(INFO_COLOR);
        infoRight.setFill(INFO_COLOR);

        infoBar.getChildren().add(infoTop);
        infoBar.getChildren().add(infoLeft);
        infoBar.getChildren().add(infoRight);
    }

    private void initializeLivesDialog() {
        livesRemaining = 3;
        livesDialog = new Text();
        livesDialog.setText("Lives Remaining: " + livesRemaining);
        livesDialog.setFont(Font.font("impact", 20));
        livesDialog.setX(INFO_WIDTH);
        livesDialog.setY((INFO_HEIGHT + livesDialog.getFont().getSize()) / 2.0);
    }

    private void initializeScoreDialog() {
        myScore = 0;
        scoreDialog = new Text();
        scoreDialog.setText("Score: " + myScore);
        scoreDialog.setFont(Font.font("impact", 20));
        scoreDialog.setX(5/8.0 * WIDTH);
        scoreDialog.setY((INFO_HEIGHT + livesDialog.getFont().getSize()) / 2.0);
    }

    private void initializeBall(int width, int height) {
        balls = new Group();
        Ball ball = new Ball(width / 2.0, height / 2.0, 0);
        balls.getChildren().add(ball);
    }

    private void initializePaddle(int width, int height) {
        myPaddle = new Rectangle(width / 2.0 - PADDLE_SIZE / 2.0, height - 50, PADDLE_SIZE, 10);
        myPaddle.setFill(PADDLE_COLOR);
        paddleVelocity = 0;
        paddleInput = new int[]{0, 0};
    }

    private void initializeBricks() {
        bricks = new Group();
        Brick layoutBrick = new Brick("brick1.gif", 0, 0, 1, 0, 0);

        for (int yOff = 1; yOff <= BRICK_LAYERS; yOff++) {
            for (int xOff = 0; xOff * layoutBrick.getBoundsInParent().getWidth() < WIDTH - 2*INFO_WIDTH; xOff++) {
                int drop = 0;
                double rand = Math.random();
                if (0.6 < rand && rand < 0.75) {
                    drop = 1;
                } else if (0.75 < rand && rand < 0.92) {
                    drop = 2;
                } else if (0.92 < rand) {
                    drop = 3;
                }
                Brick brick = new Brick("brick" + (yOff % 4 + 1) + ".gif",
                        Math.floor(xOff * layoutBrick.getBoundsInParent().getWidth()) + INFO_WIDTH,
                        yOff * (layoutBrick.getBoundsInParent().getHeight()+1) + INFO_HEIGHT,
                        1, 10, drop);
                bricks.getChildren().add(brick);
            }
        }
    }

    // Change properties of shapes in small ways to animate them over time
    // Note, there are more sophisticated ways to animate shapes, but these simple ways work fine to start
    private void step (double elapsedTime) {
        // update "actors" attributes
        moveBalls(elapsedTime);
        movePaddle(elapsedTime);
        movePowerUps(elapsedTime);
        checkPaddleCollisions();
        checkBrickCollisions();

        // with shapes, can check precisely
        // NEW Java 10 syntax that simplifies things (but watch out it can make code harder to understand)
        // var intersection = Shape.intersect(myMover, myGrower);
        // Shape intersection = Shape.intersect(myMover, myGrower);
    }

    private void checkPaddleCollisions() {
        // check for collision between ball and paddle
        for (Node n : balls.getChildren()) {
            Ball ball = (Ball) n;
            if (myPaddle.getBoundsInParent().intersects(ball.getBoundsInParent())) {
                if (Math.floor(ball.getBoundsInParent().getMaxY()) <= myPaddle.getBoundsInParent().getMinY()) {
                    // the ball is above the paddle
                    ball.addVelocity((ball.getBoundsInParent().getCenterX() - myPaddle.getBoundsInParent().getCenterX()) *
                            BOUNCE_FACTOR, 0);
                    ball.multiplyVelocity(1, -1 * (1 + BALL_Y_SPEEDUP));
                }
                else if (Math.floor(ball.getBoundsInParent().getMaxY()) > myPaddle.getBoundsInParent().getMinY() &&
                        Math.floor(ball.getBoundsInParent().getMinY()) < myPaddle.getBoundsInParent().getMaxY()) {
                    // the ball is on the side of the paddle
                    ball.multiplyVelocity(-1, 1);
                    ball.addVelocity(paddleVelocity * ((float) PADDLE_SPEED / Ball.BALL_SPEED), 0);
                }
            }
        }

        ArrayList<Node> toBeRemoved = new ArrayList<>();
        // check for collision between powerups and paddle
        for (Node n : powerups.getChildren()) {
            PowerUp pu = (PowerUp) n;
            if (myPaddle.getLayoutBounds().intersects(pu.getLayoutBounds())) {
                toBeRemoved.add(n);
                activatePowerUp(pu.getType());
            }
        }

        for (Node n : toBeRemoved) {
            powerups.getChildren().remove(n);
        }
    }

    private void checkBrickCollisions() {
        for (Node b : balls.getChildren()) {
            Ball ball = (Ball) b;
            boolean bounceY = false, bounceX = false;
            for (Node n : bricks.getChildren()) {
                Brick brick = (Brick) n;
                if (brick.getBoundsInParent().intersects(ball.getBoundsInParent())) {
                    if (Math.ceil(ball.getBoundsInParent().getCenterY()) <= brick.getBoundsInParent().getMinY() ||
                            Math.floor(ball.getBoundsInParent().getCenterY()) >= brick.getBoundsInParent().getMaxY()) {
                        bounceY = true;
                    } else {
                        bounceX = true;
                    }

                    handleBrickHit(brick);
                }
            }

            if (bounceX) {
                ball.multiplyVelocity(-1, 1);
            }
            if (bounceY) {
                ball.multiplyVelocity(1, -1);
            }
        }
    }

    private void handleBrickHit(Brick brick) {
        brick.damageBrick();
        if (brick.isBroken()) {
            if (brick.getDrop() > 0) {
                powerups.getChildren().add(new PowerUp(brick.getDrop(),
                        brick.getBoundsInParent().getCenterX(),
                        brick.getBoundsInParent().getCenterY()));
            }
            brick.breakBrick();
        }
        myScore += brick.getScore();
        scoreDialog.setText("Score: " + myScore);
    }

    private void movePaddle(double elapsedTime) {
        boolean hittingBall = false;
        for (Node n : balls.getChildren()) {
            if (myPaddle.getBoundsInParent().intersects(n.getBoundsInParent())) {
                hittingBall = true;
            }
        }
        if (!hittingBall) {
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

    private void movePowerUps(double elapsedTime) {
        for (Node n : powerups.getChildren()) {
            PowerUp pu = (PowerUp) n;
            pu.move(elapsedTime);
        }

        for (Node n : lasers.getChildren()) {
            Rectangle laser = (Rectangle) n;
            laser.setY(laser.getY() - PowerUp.LASER_SPEED * elapsedTime);
        }

        ArrayList<Node> toBeRemoved = new ArrayList<>();
        for (Node n : explosions.getChildren()) {
            Circle explosion = (Circle) n;
            explosion.setOpacity(explosion.getOpacity() - 0.02);
            if (explosion.getOpacity() <= 0) {
                toBeRemoved.add(explosion);
            }
        }

        for (Node n : toBeRemoved) {
            explosions.getChildren().remove(n);
        }

        checkLaserHits();
    }

    private void moveBalls(double elapsedTime) {
        ArrayList<Node> toBeRemoved = new ArrayList<>();
        for (Node n : balls.getChildren()) {
            Ball ball = (Ball) n;
            ball.move(elapsedTime, WIDTH, INFO_WIDTH, INFO_HEIGHT);
            if (ball.getY() > HEIGHT) {
                toBeRemoved.add(ball);
            }
        }

        for (Node n : toBeRemoved) {
            balls.getChildren().remove(n);
        }

        if (balls.getChildren().size() == 0) {
            loseALife();
        }
    }

    private void activatePowerUp(int type) {
        switch (type) {
            case 1:
                activateLaserPower();
                break;
            case 2:
                activateExplosionPower();
                break;
            case 3:
                activateExtraBallPower();
                break;
        }
    }

    private void activateExtraBallPower() {
        Ball newBall = new Ball(myPaddle.getX() + myPaddle.getWidth() / 2.0,
                myPaddle.getY() - 20, balls.getChildren().size());
        balls.getChildren().add(newBall);
    }

    private void activateExplosionPower() {
        for (int i = 0; i < PowerUp.NUMBER_OF_EXPLOSIONS; i++) {
            double[] explosionPosition = new double[]{Math.random() * WIDTH, Math.random()*(HEIGHT / 2.0)};
            Circle explosion = new Circle(explosionPosition[0], explosionPosition[1], PowerUp.EXPLOSION_RADIUS);
            explosion.setFill(Color.ORANGERED);
            explosions.getChildren().add(explosion);
            for (Node n : bricks.getChildren()) {
                Brick brick = (Brick) n;
                if (brick.getBoundsInParent().intersects(explosion.getBoundsInParent())) {
                    // TODO: Refactor brick breaking/powerup spawning code
                    brick.damageBrick();
                    if (brick.isBroken()) {
                        brick.breakBrick();
                    }
                }
            }
        }
    }

    private void activateLaserPower() {
        for (int i = 0; i < PowerUp.NUMBER_OF_LASERS; i++) {
            double laserX = myPaddle.getX() + i * myPaddle.getWidth() / 3.0;
            Rectangle laser = new Rectangle(laserX, myPaddle.getY() - PowerUp.LASER_HEIGHT + Math.random() * 10,
                    5, PowerUp.LASER_HEIGHT);
            laser.setFill(Color.RED);
            lasers.getChildren().add(laser);
        }
    }

    private void checkLaserHits() {
        ArrayList<Node> toBeRemoved = new ArrayList<>();
        for (Node laser : lasers.getChildren()) {
            for (Node n : bricks.getChildren()) {
                Brick brick = (Brick) n;
                if (laser.getBoundsInParent().intersects(brick.getBoundsInParent())) {
                    brick.damageBrick();
                    if (brick.isBroken()) {
                        brick.breakBrick();
                    }
                    toBeRemoved.add(laser);
                }
            }
        }

        for (Node laser : toBeRemoved) {
            lasers.getChildren().remove(laser);
        }

    }

    private void loseALife() {
        livesRemaining -= 1;
        livesDialog.setText("Lives Remaining: " + livesRemaining);

        myPaddle.setX((WIDTH - myPaddle.getLayoutBounds().getWidth()) / 2.0);
        Ball newBall = new Ball(WIDTH / 2.0, HEIGHT / 2.0, 0);
        balls.getChildren().add(newBall);
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

/*
 TODO next: add cheat keys, add splash screen and enforce lives (2 hours), add levels (2 hours)
 TODO low priority: create new bricks (2 hours), add movement (way too long)
Known bugs:
* ball can get stuck on the edge of the screen in weird edge case
* bouncing on the side of a brick occasionally causes the ball to bounce in the y direction
 */
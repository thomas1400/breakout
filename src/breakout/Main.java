package breakout;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.FileNotFoundException;
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
    public static final int HEIGHT = 600;
    public static final int WIDTH = 396 + 2 * INFO_WIDTH;
    public static final int FRAMES_PER_SECOND = 60;
    public static final int MILLISECOND_DELAY = 1000 / FRAMES_PER_SECOND;
    public static final double SECOND_DELAY = 1.0 / FRAMES_PER_SECOND;
    public static final int PADDLE_SIZE = 100;
    public static final int PADDLE_SPEED = 300;
    public static final double BOUNCE_FACTOR = 0.025;
    private static final double BALL_Y_SPEEDUP = 0.03;

    public static final Paint BACKGROUND = Color.BLACK;
    public static final Paint TEXT_COLOR = Color.WHITE;
    public static final Paint PADDLE_COLOR = Color.WHITE;
    public static final Paint INFO_COLOR = Color.BLUE;


    // some things needed to remember during game
    private Scene myScene;
    private Rectangle myPaddle;
    private int paddleVelocity;
    private int[] paddleInput;
    private int livesRemaining;
    private int myScore;
    private boolean controllingBall;
    private int myLevel;

    private Group balls;
    private Group bricks;
    private Group powerUps;
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
        myScene = setupGame();
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
    private Scene setupGame() {
        Group root = new Group();

        initializeInfoBar();
        initializeLivesDialog();
        initializeScoreDialog();
        initializeBall(WIDTH, HEIGHT);
        initializePaddle(WIDTH, HEIGHT);
        myLevel = 1;
        initializeBricks();

        powerUps = new Group();
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
        root.getChildren().add(powerUps);

        Scene scene = new Scene(root, WIDTH, HEIGHT, BACKGROUND);

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
        livesDialog.setFill(TEXT_COLOR);
        livesDialog.setX(INFO_WIDTH);
        livesDialog.setY((INFO_HEIGHT + livesDialog.getFont().getSize()) / 2.0);
    }

    private void initializeScoreDialog() {
        myScore = 0;
        scoreDialog = new Text();
        scoreDialog.setText("Score: " + myScore);
        scoreDialog.setFont(Font.font("impact", 20));
        scoreDialog.setFill(TEXT_COLOR);
        scoreDialog.setX(5/8.0 * WIDTH);
        scoreDialog.setY((INFO_HEIGHT + livesDialog.getFont().getSize()) / 2.0);
    }

    private void initializeBall(int width, int height) {
        balls = new Group();
        Ball ball = new Ball(width / 2.0, 3 * height / 4.0, 0);
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

        for (Brick brick : LevelInterpreter.interpretLevelFromFile("resources/level" + myLevel + "layout.txt")) {
            bricks.getChildren().add(brick);
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
        for (Node n : powerUps.getChildren()) {
            PowerUp pu = (PowerUp) n;
            if (myPaddle.getLayoutBounds().intersects(pu.getLayoutBounds())) {
                toBeRemoved.add(n);
                activatePowerUp(pu.getType());
            }
        }

        for (Node n : toBeRemoved) {
            powerUps.getChildren().remove(n);
        }
    }

    private void checkBrickCollisions() {
        for (Node b : balls.getChildren()) {
            Ball ball = (Ball) b;
            boolean bounceY = false, bounceX = false;
            for (Node n : bricks.getChildren()) {
                Brick brick = (Brick) n;
                if (brick.getBoundsInParent().intersects(ball.getBoundsInParent())) {
                    if (Math.floor(ball.getMaxX()) <= brick.getCenterX() &&
                            (ball.getCenterX() - brick.getMinX() + brick.getMinY()) <= ball.getCenterY() &&
                            ball.getCenterY() <= -1 * (ball.getCenterX() - brick.getMinX()) + brick.getMaxY()) {
                        bounceX = true;
                        System.out.println("left");

                    }
                    else if (Math.ceil(ball.getMinX()) >= brick.getCenterX() &&
                            -1 * (ball.getCenterX() - brick.getMaxX()) + brick.getMinY() <= ball.getCenterY() &&
                            ball.getCenterY() <= (ball.getCenterX() - brick.getMaxX()) + brick.getMaxY()) {
                        bounceX = true;
                        System.out.println("right");

                    }
                    else if (Math.floor(ball.getMaxY()) <= brick.getCenterY() &&
                            (ball.getCenterY() - brick.getMinY() + brick.getMinX()) <= ball.getCenterX() &&
                            ball.getCenterX() <= -1 * (ball.getCenterY() - brick.getMinY()) + brick.getMaxX()) {
                        bounceY = true;
                        System.out.println("top");

                    } else if (Math.ceil(ball.getMinY()) >= brick.getCenterY() &&
                            -1 * (ball.getCenterY() - brick.getMaxY()) + brick.getMinX() <= ball.getCenterX() &&
                            ball.getCenterX() <= (ball.getCenterY() - brick.getMaxY()) + brick.getMaxX()) {
                        bounceY = true;
                        System.out.println("bottom");

                    } else {
                        System.out.println("no bounce");
                    }


//                    if (ball.getBoundsInParent().getCenterX() >= brick.getBoundsInParent().getMinX() &&
//                            ball.getBoundsInParent().getCenterX() <= brick.getBoundsInParent().getMaxX()) {
//                        bounceY = true;
//                    } else if (ball.getBoundsInParent().getCenterY() >= brick.getBoundsInParent().getMinY() &&
//                            ball.getBoundsInParent().getCenterY() <= brick.getBoundsInParent().getMaxY()) {
//                        bounceX = true;
//                    } else {
//                        bounceX = true;
//                        bounceY = true;
//                        System.out.println("corner");
//                    }

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
                powerUps.getChildren().add(new PowerUp(brick.getDrop(),
                        brick.getBoundsInParent().getCenterX(),
                        brick.getBoundsInParent().getCenterY()));
            }
            brick.breakBrick();
            updateScore(brick);
        }
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
        for (Node n : powerUps.getChildren()) {
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

    private void setBallMovement(String direction) {
        for (Node n : balls.getChildren()) {
            Ball ball = (Ball) n;
            if (direction.equals("right")) {
                ball.multiplyVelocity(0, 0);
                ball.addVelocity(1, 0);
            }
            if (direction.equals("left")) {
                ball.multiplyVelocity(0, 0);
                ball.addVelocity(-1, 0);
            }
            if (direction.equals("up")) {
                ball.multiplyVelocity(0, 0);
                ball.addVelocity(0, -1);
            }
            if (direction.equals("down")) {
                ball.multiplyVelocity(0, 0);
                ball.addVelocity(0, 1);
            }
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
                        updateScore(brick);
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
                        updateScore(brick);
                    }
                    toBeRemoved.add(laser);
                }
            }
        }

        for (Node laser : toBeRemoved) {
            lasers.getChildren().remove(laser);
        }

    }

    private void updateScore(Brick brick) {
        myScore += brick.getScore();
        scoreDialog.setText("Score: " + myScore);
    }

    private void loseALife() {
        livesRemaining -= 1;
        livesDialog.setText("Lives Remaining: " + livesRemaining);

        resetBallAndPaddle();
    }

    private void resetBallAndPaddle() {
        balls.getChildren().clear();
        myPaddle.setX((WIDTH - myPaddle.getLayoutBounds().getWidth()) / 2.0);
        Ball newBall = new Ball(WIDTH / 2.0, 3 * HEIGHT / 4.0, 0);
        balls.getChildren().add(newBall);
    }

    private void breakRandomBrick() {
        ArrayList<Brick> unbroken = new ArrayList<>();
        for (Node n : bricks.getChildren()) {
            Brick brick = (Brick) n;
            if (!brick.isBroken()) {
                unbroken.add(brick);
            }
        }
        if (unbroken.size() > 0) {
            int indexToBreak = (int) (Math.random() * unbroken.size());
            unbroken.get(indexToBreak).breakBrick();
            myScore += unbroken.get(indexToBreak).getScore();
        }
    }

    private void changeBallSize(double scale) {
        for (Node n : balls.getChildren()) {
            n.setScaleX(scale * n.getScaleX());
            n.setScaleY(scale * n.getScaleY());
            if (n.getScaleX() > 3) {
                n.setScaleX(3);
                n.setScaleY(3);
            } else if (n.getScaleX() < 1) {
                n.setScaleX(1);
                n.setScaleY(1);
            }
        }
    }

    // What to do each time a key is pressed
    private void handleKeyPress (KeyCode code) {
        if (code == KeyCode.RIGHT) {
            if (controllingBall) {
                setBallMovement("right");
            } else {
                paddleInput[1] = 1;
            }
        }
        else if (code == KeyCode.LEFT) {
            if (controllingBall) {
                setBallMovement("left");
            } else {
                paddleInput[0] = 1;
            }
        }
        else if (code == KeyCode.UP) {
            if (controllingBall) {
                setBallMovement("up");
            }
        }
        else if (code == KeyCode.DOWN) {
            if (controllingBall) {
                setBallMovement("down");
            }
        }

        // CHEAT KEYS
        if (code == KeyCode.L) {
            livesRemaining += 1;
            livesDialog.setText("Lives Remaining: " + livesRemaining);
        }
        if (code == KeyCode.R) {
            resetBallAndPaddle();
        }
        if (code == KeyCode.B) {
            controllingBall = !controllingBall;
        }
        if (code == KeyCode.S) {
            breakRandomBrick();
        }
        if (code == KeyCode.COMMA) {
            changeBallSize(0.75);
        }
        if (code == KeyCode.PERIOD) {
            changeBallSize(1.5);
        }

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
 TODO next: add splash screen and enforce lives (2 hours), add levels (2 hours) (add level switch cheat key)
 TODO low priority: create new bricks (2 hours), add movement (way too long)
Known bugs:
* ball can get stuck on the edge of the screen in weird edge case
* bouncing on the side of a brick occasionally causes the ball to bounce in the y direction
* scaling the ball can cause issues when checking intersection with paddle
 */
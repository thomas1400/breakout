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
import javafx.scene.text.TextAlignment;
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
    public static final double BALL_Y_SPEEDUP = 0.03;

    public static final Paint BACKGROUND = Color.BLACK;
    public static final Paint TEXT_COLOR = Color.WHITE;
    public static final Paint PADDLE_COLOR = Color.WHITE;
    public static final Paint INFO_COLOR = Color.BLUE;
    public static final int MAX_LEVEL = 6;


    private Rectangle myPaddle;
    private int paddleVelocity;
    private int[] paddleInput;
    private int livesRemaining;
    private int myScore;
    private boolean controllingBall;
    private int myLevel;
    private int stepsSinceSpawn;
    private boolean paused;
    private boolean gameOver;

    private Group startMenu;
    private Group balls;
    private Group bricks;
    private Group powerUps;
    private Group lasers;
    private Group explosions;
    private Group infoBar;
    private Text livesDialog;
    private Text scoreDialog;
    private Group loseDialog;
    private Group winDialog;

    /**
     * Initialize what will be displayed and how it will be updated.
     */
    @Override
    public void start (Stage stage) {
        // attach scene to the stage and display it
        // some things needed to remember during game
        Scene myScene = setupGame();
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
        gameOver = false;

        initializeStartMenu();
        initializeInfoBar();
        initializeLivesDialog();
        initializeScoreDialog();
        initializeBall(WIDTH, HEIGHT);
        initializePaddle(WIDTH, HEIGHT);
        myLevel = 1;
        initializeBricks();

        winDialog = new Group();
        loseDialog = new Group();

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
        root.getChildren().add(startMenu);
        root.getChildren().add(winDialog);
        root.getChildren().add(loseDialog);

        Scene scene = new Scene(root, WIDTH, HEIGHT, BACKGROUND);

        scene.setOnKeyPressed(e -> handleKeyPress(e.getCode()));
        scene.setOnKeyReleased(e -> handleKeyRelease(e.getCode()));
        return scene;
    }

    private void initializeStartMenu() {
        paused = true;
        startMenu = new Group();

        Rectangle background = new Rectangle(0, 0, WIDTH, HEIGHT);
        background.setFill(Color.BLACK);
        startMenu.getChildren().add(background);

        Text title = new Text();
        title.setText("Alien Invasion");
        title.setFont(Font.font("impact", 50));
        title.setFill(TEXT_COLOR);
        title.setX(WIDTH / 2.0 - title.getLayoutBounds().getWidth() / 2.0);
        title.setY(175);
        startMenu.getChildren().add(title);

        Text startButton = new Text();
        startButton.setText("PRESS SPACE TO START");
        startButton.setFont(Font.font("impact", 20));
        startButton.setFill(TEXT_COLOR);
        startButton.setX(WIDTH / 2.0 - startButton.getLayoutBounds().getWidth() / 2.0);
        startButton.setY(250);
        startMenu.getChildren().add(startButton);

        Text instructions = new Text();
        instructions.setText("Instructions:\nMake your way through 6 levels of alien invaders\nto save the planet!\n\n" +
                "Use the LEFT and RIGHT arrow keys to move the paddle.\nKeep the ball from falling " +
                "and destroy all of the aliens to win!\n\nDestroy special orange aliens for extra points,\n" +
                "and destroy purple aliens to earn power-ups.");
        instructions.setFont(Font.font("impact", 15));
        instructions.setFill(TEXT_COLOR);
        instructions.setTextAlignment(TextAlignment.CENTER);
        instructions.setX(WIDTH / 2.0 - instructions.getLayoutBounds().getWidth() / 2.0);
        instructions.setY(375);
        startMenu.getChildren().add(instructions);
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
        stepsSinceSpawn = 0;
        loadLevel(myLevel);
    }

    // Change properties of shapes in small ways to animate them over time
    // Note, there are more sophisticated ways to animate shapes, but these simple ways work fine to start
    private void step (double elapsedTime) {
        if (!paused) {
            moveBalls(elapsedTime);
            movePaddle(elapsedTime);
            movePowerUps(elapsedTime);
            checkPaddleCollisions();
            checkBrickCollisions();
            checkMothershipSpawn();

            if (checkLevelEnd()) {
                loadNextLevel();
            }
            checkGameOver();
        }
    }

    private void checkGameOver() {
        if (livesRemaining == -1) {
            showLoseDialog();
            paused = true;
            gameOver = true;
        }
        if (myLevel == MAX_LEVEL + 1) {
            showWinDialog();
            paused = true;
            gameOver = true;
        }
    }

    private void showWinDialog() {
        Rectangle background = new Rectangle(0, 0, WIDTH, HEIGHT);
        background.setFill(Color.BLACK);
        winDialog.getChildren().add(background);

        Text winText = new Text();
        winText.setText("YOU WIN!");
        winText.setFont(Font.font("impact", 40));
        winText.setFill(TEXT_COLOR);
        winText.setTextAlignment(TextAlignment.CENTER);
        winText.setX(WIDTH / 2.0 - winText.getLayoutBounds().getWidth() / 2.0);
        winText.setY(275);
        winDialog.getChildren().add(winText);

        Text winSubText = new Text();
        winSubText.setText("Score: " + myScore + "\n\nPress space to play again.");
        winSubText.setFont(Font.font("impact", 20));
        winSubText.setFill(TEXT_COLOR);
        winSubText.setTextAlignment(TextAlignment.CENTER);
        winSubText.setX(WIDTH / 2.0 - winSubText.getLayoutBounds().getWidth() / 2.0);
        winSubText.setY(350);
        winDialog.getChildren().add(winSubText);
    }

    private void showLoseDialog() {
        Rectangle background = new Rectangle(0, 0, WIDTH, HEIGHT);
        background.setFill(Color.BLACK);
        loseDialog.getChildren().add(background);

        Text loseText = new Text();
        loseText.setText("YOU LOSE!");
        loseText.setFont(Font.font("impact", 40));
        loseText.setFill(TEXT_COLOR);
        loseText.setTextAlignment(TextAlignment.CENTER);
        loseText.setX(WIDTH / 2.0 - loseText.getLayoutBounds().getWidth() / 2.0);
        loseText.setY(275);
        loseDialog.getChildren().add(loseText);

        Text loseSubText = new Text();
        loseSubText.setText("Score: " + myScore + "\n\nPress space to play again.");
        loseSubText.setFont(Font.font("impact", 20));
        loseSubText.setFill(TEXT_COLOR);
        loseSubText.setTextAlignment(TextAlignment.CENTER);
        loseSubText.setX(WIDTH / 2.0 - loseSubText.getLayoutBounds().getWidth() / 2.0);
        loseSubText.setY(350);
        loseDialog.getChildren().add(loseSubText);
    }

    private void loadNextLevel() {
        myLevel += 1;
        loadLevel(myLevel);
    }

    private void loadLevel(int level) {
        bricks.getChildren().clear();
        bricks.getChildren().addAll(
                LevelInterpreter.interpretLevelFromFile("resources/level" + level + "layout.txt")
        );
        resetBallAndPaddle();
    }

    private boolean checkLevelEnd() {
        for (Node n : bricks.getChildren()) {
            if (!((Brick) n).isBroken()) {
                return false;
            }
        }
        return true;
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
            }
        }

        for (Node n : toBeRemoved) {
            powerUps.getChildren().remove(n);
            activatePowerUp(((PowerUp) n).getType());
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
                    }
                    else if (Math.ceil(ball.getMinX()) >= brick.getCenterX() &&
                            -1 * (ball.getCenterX() - brick.getMaxX()) + brick.getMinY() <= ball.getCenterY() &&
                            ball.getCenterY() <= (ball.getCenterX() - brick.getMaxX()) + brick.getMaxY()) {
                        bounceX = true;
                    }
                    else if (Math.floor(ball.getMaxY()) <= brick.getCenterY() &&
                            (ball.getCenterY() - brick.getMinY() + brick.getMinX()) <= ball.getCenterX() &&
                            ball.getCenterX() <= -1 * (ball.getCenterY() - brick.getMinY()) + brick.getMaxX()) {
                        bounceY = true;
                    } else if (Math.ceil(ball.getMinY()) >= brick.getCenterY() &&
                            -1 * (ball.getCenterY() - brick.getMaxY()) + brick.getMinX() <= ball.getCenterX() &&
                            ball.getCenterX() <= (ball.getCenterY() - brick.getMaxY()) + brick.getMaxX()) {
                        bounceY = true;
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

    private void checkMothershipSpawn() {
        if (stepsSinceSpawn < 300) {
            stepsSinceSpawn += 1;
            return;
        } else {
            stepsSinceSpawn = 0;
        }
        Brick newBrick = null;
        for (Node n : bricks.getChildren()) {
            if (((Brick) n).isMothership() && !((Brick) n).isBroken()) {
                newBrick = new Brick("alienbrick1.gif",
                        Math.random() * (WIDTH - 2*INFO_WIDTH - 36) + INFO_WIDTH,
                        Math.random() * (HEIGHT / 2.0) + INFO_HEIGHT,
                        1, 10, 0);

            }
        }
        if (newBrick != null) {
            boolean validNewBrick = true;
            for (Node b : bricks.getChildren()) {
                if (b.getBoundsInParent().intersects(newBrick.getBoundsInParent())) {
                    validNewBrick = false;
                }
            }
            if (validNewBrick) {
                bricks.getChildren().add(newBrick);
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
            double[] explosionPosition = new double[]{
                    Math.random() * (WIDTH - 2*INFO_WIDTH) + INFO_WIDTH,
                    Math.random()*(HEIGHT / 2.0) + INFO_HEIGHT};
            Circle explosion = new Circle(explosionPosition[0], explosionPosition[1], PowerUp.EXPLOSION_RADIUS);
            explosion.setFill(Color.ORANGERED);
            explosions.getChildren().add(explosion);
            for (Node n : bricks.getChildren()) {
                Brick brick = (Brick) n;
                if (brick.getBoundsInParent().intersects(explosion.getBoundsInParent())) {
                    handleBrickHit(brick);
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
                        handleBrickHit(brick);
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
            updateScore(unbroken.get(indexToBreak));
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

    private void resetGame() {
        gameOver = false;
        paused = false;
        myScore = 0;
        scoreDialog.setText("Score: 0");
        myLevel = 1;
        livesRemaining = 3;
        livesDialog.setText("Lives Remaining: 3");

        winDialog.getChildren().clear();
        loseDialog.getChildren().clear();

        resetBallAndPaddle();
        loadLevel(1);

        System.out.println(paused);
    }

    private void skipToLevel(int level) {
        myLevel = level;
        loadLevel(level);
        resetBallAndPaddle();
    }

    private void handleKeyPress (KeyCode code) {
        if (code == KeyCode.SPACE) {
            if (paused) {
                paused = false;
            }
            startMenu.getChildren().clear();
            if (gameOver) {
                resetGame();
            }
        }

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
        if (code == KeyCode.DIGIT1) {
            skipToLevel(1);
        }
        if (code == KeyCode.DIGIT2) {
            skipToLevel(2);
        }
        if (code == KeyCode.DIGIT3) {
            skipToLevel(3);
        }
        if (code == KeyCode.DIGIT4) {
            skipToLevel(4);
        }
        if (code == KeyCode.DIGIT5) {
            skipToLevel(5);
        }
        if (code == KeyCode.DIGIT6) {
            skipToLevel(6);
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
Known bugs:
* ball can get stuck on the edge of the screen in weird edge case
* scaling the ball can cause issues when checking intersection with paddle
* directly controlling the ball's direction with cheat key 'b' can cause it to stop colliding with the paddle
 */
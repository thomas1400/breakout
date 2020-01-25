package breakout;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * A Ball in the game of Breakout. Used in Main of this package to represent the game's ball(s).
 * The constructor does not clean input; negative x or y coordinates will break the class' logic.
 *
 * Initialize the first ball in a game using number = 0, following balls should be initialized using number > 0.
 *
 * @author Thomas Owens
 */
public class Ball extends Group {
    private static final String BALL_IMAGE = "ball.gif";
    private ImageView image;
    private double[] direction;
    public static final int BALL_SPEED = 150;
    public static final double MAX_BALL_SPEED = 400;

    /**
     * Creates a new Ball (the 'number'-th ball) at coordinates (x, y).
     */
    public Ball(double x, double y, int number) {
        super();
        initializeImage();
        this.image.setX(x - this.image.getLayoutBounds().getWidth() / 2.0);
        this.image.setY(y - this.image.getLayoutBounds().getHeight() / 2.0);

        this.direction = new double[]{0, 0};
        if (number == 0) {
            addVelocity(0 + 0.02 * Math.random(), 1);
        } else {
            addVelocity(Math.random(), -1);
        }
    }

    private void initializeImage() {
        Image image = new Image(this.getClass().getClassLoader().getResourceAsStream(BALL_IMAGE));
        this.image = new ImageView(image);
        this.getChildren().add(this.image);
    }

    /**
     * Moves the ball according to its current velocity and the time elapsed
     * @param elapsedTime is the time since the last update
     * @param WIDTH is the width of the screen
     * @param INFO_WIDTH is the width of the info bars
     * @param INFO_HEIGHT is the height of the info bars
     */
    public void move(double elapsedTime, int WIDTH, int INFO_WIDTH, int INFO_HEIGHT) {
        image.setX(image.getX() + BALL_SPEED * elapsedTime * direction[0]);
        image.setY(image.getY() + BALL_SPEED * elapsedTime * direction[1]);


        if (image.getX() <= INFO_WIDTH || image.getX() >= WIDTH - INFO_WIDTH - image.getBoundsInParent().getWidth()) {
            direction[0] *= -1;
        }
        if (image.getY() <= INFO_HEIGHT) {
            direction[1] *= -1;
        }
    }

    /**
     * Adds to this ball's velocity
     * @param xv is the component to add in the x direction
     * @param yv is the component to add in the y direction
     */
    public void addVelocity(double xv, double yv) {
        direction[0] += xv;
        direction[1] += yv;

        double newSpeed = Math.sqrt(direction[0] * direction[0] + direction[1] * direction[1]);
        if (newSpeed > MAX_BALL_SPEED) {
            direction[0] *= MAX_BALL_SPEED / newSpeed;
        }
    }

    /**
     * Returns this ball's y coordinate.
     * @return y
     */
    public double getY() {
        return this.image.getY();
    }

    /**
     * Returns this ball's center x coordinate.
     * @return centerx
     */
    public double getCenterX() {
        return this.getBoundsInParent().getCenterX();
    }

    /**
     * Returns this ball's center y coordinate.
     * @return centery
     */
    public double getCenterY() {
        return this.getBoundsInParent().getCenterY();
    }

    /**
     * Returns this ball's minimum x coordinate.
     * @return minx
     */
    public double getMinX() {
        return this.getBoundsInParent().getMinX();
    }

    /**
     * Returns this ball's maximum x coordinate.
     * @return maxx
     */
    public double getMaxX() {
        return this.getBoundsInParent().getMaxX();
    }

    /**
     * Returns this ball's minimum y coordinate.
     * @return miny
     */
    public double getMinY() {
        return this.getBoundsInParent().getMinY();
    }

    /**
     * Returns this ball's maximum y coordinate.
     * @return maxy
     */
    public double getMaxY() {
        return this.getBoundsInParent().getMaxY();
    }

    /**
     * Multiplies this ball's velocity by given values
     * @param xv the component to multiply in the x direction
     * @param yv the component to multiply in the y direction
     */
    public void multiplyVelocity(double xv, double yv) {
        direction[0] *= xv;
        direction[1] *= yv;

        double newSpeed = Math.sqrt(direction[0] * direction[0] + direction[1] * direction[1]);
        if (newSpeed > MAX_BALL_SPEED) {
            direction[0] *= MAX_BALL_SPEED / newSpeed;
        }
    }
}

package breakout;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Ball extends Group {
    private static final String BALL_IMAGE = "ball.gif";
    private ImageView image;
    private double[] direction;
    public static final int BALL_SPEED = 150;
    public static final double MAX_BALL_SPEED = 400;

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

    public void addVelocity(double xv, double yv) {
        direction[0] += xv;
        direction[1] += yv;

        double newSpeed = Math.sqrt(direction[0] * direction[0] + direction[1] * direction[1]);
        if (newSpeed > MAX_BALL_SPEED) {
            direction[0] *= MAX_BALL_SPEED / newSpeed;
        }
    }

    public double getY() {
        return this.image.getY();
    }

    public double getCenterX() {
        return this.getBoundsInParent().getCenterX();
    }

    public double getCenterY() {
        return this.getBoundsInParent().getCenterY();
    }

    public double getMinX() {
        return this.getBoundsInParent().getMinX();
    }

    public double getMaxX() {
        return this.getBoundsInParent().getMaxX();
    }

    public double getMinY() {
        return this.getBoundsInParent().getMinY();
    }

    public double getMaxY() {
        return this.getBoundsInParent().getMaxY();
    }

    public void multiplyVelocity(double xv, double yv) {
        direction[0] *= xv;
        direction[1] *= yv;

        double newSpeed = Math.sqrt(direction[0] * direction[0] + direction[1] * direction[1]);
        if (newSpeed > MAX_BALL_SPEED) {
            direction[0] *= MAX_BALL_SPEED / newSpeed;
        }
    }
}

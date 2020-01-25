package breakout;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Holds a PowerUp for the Breakout game initialized in Main.
 * Keeps track of image and type, and handles movement.
 *
 * Should be initialized with a type defined in Main, with positive x and y coordinates.
 *
 * @author Thomas Owens
 */
public class PowerUp extends Group {
    public static final int POWER_UP_SPEED = 100;
    public static final int NUMBER_OF_EXPLOSIONS = 3;
    public static final int EXPLOSION_RADIUS = 10;
    public static final int NUMBER_OF_LASERS = 3;
    public static final int LASER_HEIGHT = 50;
    public static final int LASER_SPEED = 150;

    private int type; // 1 for rockets, 2 for explosion, 3 for extra ball
    private ImageView image;

    /**
     * Creates a new PowerUp
     * @param type this power-up's type
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public PowerUp(int type, double x, double y) {
        super();
        this.type = type;
        retrieveImage(type);
        image.setX(x);
        image.setY(y);
    }

    private void retrieveImage(int type) {
        String path;
        switch (type) {
            case 1:
                path = "laserpower.gif";
                break;
            case 2:
                path = "sizepower.gif";
                break;
            case 3:
                path = "extraballpower.gif";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }

        Image image = new Image(this.getClass().getClassLoader().getResourceAsStream(path));
        this.image = new ImageView(image);
        this.getChildren().add(this.image);
    }

    /**
     * Update this power-up's position according to a predetermined downward speed.
     * @param elapsedTime the time since the last update
     */
    public void move(double elapsedTime) {
        image.setY(image.getY() + POWER_UP_SPEED * elapsedTime);
    }

    /**
     * Get this power-up's type.
     * @return type
     */
    public int getType() {
        return type;
    }

}

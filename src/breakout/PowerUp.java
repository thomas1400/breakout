package breakout;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PowerUp extends Group {
    public static final int POWER_UP_SPEED = 100;
    public static final int NUMBER_OF_EXPLOSIONS = 3;
    public static final int EXPLOSION_RADIUS = 10;
    public static final int NUMBER_OF_LASERS = 3;
    public static final int LASER_HEIGHT = 50;
    public static final int LASER_SPEED = 150;

    private int type; // 1 for rockets, 2 for explosion, 3 for extra ball
    private ImageView image;

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

    public void move(double elapsedTime) {
        image.setY(image.getY() + POWER_UP_SPEED * elapsedTime);
    }

    public int getType() {
        return type;
    }

}

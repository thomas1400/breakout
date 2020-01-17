package breakout;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PowerUp extends Group {
    public static final int POWER_UP_SPEED = 100;

    private int type; // 1 for rockets, 2 for explosion, 3 for extra ball
    private ImageView image;

    public PowerUp(int type, int x, int y) {
        retrieveImage(type);
        image.setX(x);
        image.setY(y);
    }

    public void updatePosition(double elapsedTime) {
        image.setY(image.getY() + POWER_UP_SPEED * elapsedTime);
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
    }

}

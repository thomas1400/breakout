package breakout;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Brick {
    private ImageView image;
    private int durability;
    private String drop;


    public Brick(String imagePath, int x, int y, int durability, String drop) {
        Image image = new Image(this.getClass().getClassLoader().getResourceAsStream(imagePath));
        this.image = new ImageView(image);
        this.image.setX(x);
        this.image.setY(y);
        this.durability = durability;
        this.drop = drop;
    }

    public ImageView getImage() {
        return image;
    }
}

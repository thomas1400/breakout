package breakout;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Brick extends Group {
    private ImageView image;
    private int durability;
    private int drop;
    private int score;

    public Brick(String imagePath, double x, double y, int durability, int score, int drop) {
        super();
        Image image = new Image(this.getClass().getClassLoader().getResourceAsStream(imagePath));
        this.image = new ImageView(image);
        this.getChildren().add(this.image);

        this.image.setX(x);
        this.image.setY(y);
        this.durability = durability;
        this.score = score;
        this.drop = drop;
    }

    public void breakBrick() {
        image.setX(-1 * image.getBoundsInParent().getWidth() - 10);
    }

    public ImageView getImage() {
        return image;
    }

    public int getScore() {
        return score;
    }

}

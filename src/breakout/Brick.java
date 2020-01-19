package breakout;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Brick extends Group {
    private ImageView image;
    private String imagePath;
    private int durability;
    private int drop;
    private int score;

    public Brick(String imagePath, double x, double y, int durability, int score, int drop) {
        super();
        this.imagePath = imagePath;
        Image image = new Image(this.getClass().getClassLoader().getResourceAsStream(imagePath));
        this.image = new ImageView(image);
        this.getChildren().add(this.image);

        this.image.setX(x);
        this.image.setY(y);
        this.durability = durability;
        this.score = score;
        this.drop = drop;
    }

    public void damageBrick() {
        durability -= 1;
        if (imagePath.equals("alienbrick4.gif")) {
            setNewImage("alienbrick4damaged.gif");

        } else if (imagePath.equals("alienbrick4damaged.gif")) {
            setNewImage("alienbrick4twicedamaged.gif");
        }
    }

    private void setNewImage(String imagePath) {
        ImageView newImage = new ImageView(new Image(this.getClass().getClassLoader().getResourceAsStream(imagePath)));
        this.imagePath = imagePath;
        newImage.setX(image.getX());
        newImage.setY(image.getY());
        this.getChildren().remove(image);
        this.getChildren().add(newImage);
        this.image = newImage;
    }

    public ImageView getImage() {
        return image;
    }

    public int getScore() {
        return score;
    }

    public boolean isBroken() {
        return durability <= 0;
    }

    public int getDrop() {
        return drop;
    }

    public void breakBrick() {
        image.setX(-1 * image.getBoundsInParent().getWidth() - 10);
        durability = 0;
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

    @Override
    public String toString() {
        //"className imagePath x y durability score drop", tab separated with newline at end
        return String.format("Brick\t%s\t%f\t%f\t%d\t%d\t%d\n", this.imagePath, this.image.getX(), this.image.getY(),
                this.durability, this.score, this.drop);
    }
}

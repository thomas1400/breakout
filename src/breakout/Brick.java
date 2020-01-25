package breakout;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Holds a Brick for the Breakout game initialized in Main.
 * Keeps track of Image, position, durability, drop, and score.
 *
 * Should be initialized with all positive arguments; negative arguments will break the class.
 *
 * @author Thomas Owens
 */
public class Brick extends Group {
    private ImageView image;
    private String imagePath;
    private int durability;
    private int drop;
    private int score;

    /**
     * Creates a new Brick
     * @param imagePath the path to this Brick's image
     * @param x the x coordinate
     * @param y the y coordinate
     * @param durability this brick's durability
     * @param score this brick's score upon breaking
     * @param drop this brick's drop upon breaking
     */
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

    /**
     * Decrement this brick's durability.
     */
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

    /**
     * Get this brick's image.
     * @return image
     */
    public ImageView getImage() {
        return image;
    }

    /**
     * Get this brick's score upon breaking.
     * @return score
     */
    public int getScore() {
        return score;
    }

    /**
     * Check if this brick is broken.
     * @return true if broken
     */
    public boolean isBroken() {
        return durability <= 0;
    }

    /**
     * Get this brick's power-up drop, if any
     * @return 0 or power-up type
     */
    public int getDrop() {
        return drop;
    }

    /**
     * Move this brick off-screen to 'break' it.
     */
    public void breakBrick() {
        image.setX(-1 * image.getBoundsInParent().getWidth() - 10);
        durability = 0;
    }

    /**
     * Get this brick's center x
     * @return centerx
     */
    public double getCenterX() {
        return this.getBoundsInParent().getCenterX();
    }

    /**
     * Get this brick's center y
     * @return centery
     */
    public double getCenterY() {
        return this.getBoundsInParent().getCenterY();
    }

    /**
     * Get this brick's minimum x
     * @return minx
     */
    public double getMinX() {
        return this.getBoundsInParent().getMinX();
    }

    /**
     * Get this brick's maximum x
     * @return maxx
     */
    public double getMaxX() {
        return this.getBoundsInParent().getMaxX();
    }

    /**
     * Get this brick's minimum y
     * @return miny
     */
    public double getMinY() {
        return this.getBoundsInParent().getMinY();
    }

    /**
     * Get this brick's maximum y
     * @return maxy
     */
    public double getMaxY() {
        return this.getBoundsInParent().getMaxY();
    }

    /**
     * Check if this brick is a "mothership"
     * @return true if mothership
     */
    public boolean isMothership() {
        return imagePath.equals("alienmothership.gif");
    }

    /**
     * Return a String representation of this brick.
     * @return this Brick as String
     */
    @Override
    public String toString() {
        //"className imagePath x y durability score drop", tab separated with newline at end
        return String.format("Brick\t%s\t%f\t%f\t%d\t%d\t%d\n", this.imagePath, this.image.getX(), this.image.getY(),
                this.durability, this.score, this.drop);
    }
}

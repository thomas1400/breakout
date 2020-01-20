package breakout;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class LevelInterpreter {
    public static final int INFO_HEIGHT = 40;
    public static final int INFO_WIDTH = 20;
    public static final int WIDTH = 396 + 2 * INFO_WIDTH;

    public static final int BRICK_WIDTH = 36;
    public static final int BRICK_HEIGHT = 24;

    public static ArrayList<Brick> interpretLevelFromFile(String filepath) {
        try {
            File file = new File(filepath);
            Scanner s = new Scanner(file);

            ArrayList<Brick> bricks = new ArrayList<>();

            int y = INFO_HEIGHT;
            while (s.hasNextLine()) {
                String line = s.nextLine();
                String[] lineSplit = line.replaceAll("\n", "").split(" ");
                int x = INFO_WIDTH;
                for (String character : lineSplit) {
                    Brick brick = interpretCharacter(character, x, y);
                    if (brick != null) {
                        bricks.add(brick);
                    }
                    x += BRICK_WIDTH + 4;
                }

                y += BRICK_HEIGHT;
            }

            return bricks;

        } catch (FileNotFoundException e) {
            return buildRandomLevel();
        }
    }

    private static Brick interpretCharacter(String character, double x, double y) {
        if (character.equals("1")) {
            return new Brick("alienbrick1.gif", x, y, 1, 10, 0);
        } else if (character.equals("2")) {
            return new Brick("alienbrick2.gif", x, y, 1, 10, 0);
        } else if (character.equals("3")) {
            return new Brick("alienbrick3.gif", x, y, 1, 50, ((int)(Math.random()*3)) + 1);
        } else if (character.equals("4")) {
            return new Brick("alienbrick4.gif", x, y, 3, 100, 0);
        } else if (character.equals("5")) {
            return new Brick("alienbrick5.gif", x, y, 1, 200, 0);
        } else if (character.equals("P")) {
            return new Brick("alienplanet.gif", x, y, 15, 300, 0);
        } else if (character.equals("M")) {
            return new Brick("alienmothership.gif", x, y, 5, 500, 0);
        } else {
            return null;
        }
    }


    private static ArrayList<Brick> buildRandomLevel() {
        ArrayList<Brick> bricks = new ArrayList<>();
        final int BRICK_LAYERS = 15;
        Brick layoutBrick = new Brick("alienbrick1.gif", 0, 0, 1, 0, 0);
        String[] brickTypes = new String[]{"1", "2", "3", "4", "5"};

        for (int yOff = 1; yOff <= BRICK_LAYERS; yOff++) {
            for (int xOff = 0; xOff * (layoutBrick.getBoundsInParent().getWidth() + 4) < WIDTH - 2*INFO_WIDTH; xOff++) {
                if (Math.random() > 0.5) {
                    Brick brick = interpretCharacter(brickTypes[(int)(Math.random() * brickTypes.length)],
                            Math.floor(xOff * (layoutBrick.getBoundsInParent().getWidth() + 4)) + INFO_WIDTH,
                            yOff * layoutBrick.getBoundsInParent().getHeight() + INFO_HEIGHT);

                    bricks.add(brick);
                }
            }
        }

        return bricks;
    }
}

package GameLogic;

import DataManagement.FileHandler;

import java.awt.Point;
import java.awt.image.BufferedImage;

public class Floor {
    private static int[] floor;
    private static void initializeFloor () {
        BufferedImage image = FileHandler.get().readImage("floor.png");
        floor = new int[image.getWidth()];
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                if (image.getRGB(i,j) == (0xFF000000)) {
                    floor[i] = j;
                    break;
                }
            }
        }
        System.out.println();
    }
    public static int getDistanceToFloor (Point pos) {
        if (floor == null)
            initializeFloor();

        return floor[pos.x] - pos.y-16;
    }
}

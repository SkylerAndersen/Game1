package GameLogic;

import Graphics.ScreenLayoutManager;

public class Background {
    public static final int SAND_WORLD = 0, MIDDLE_WORLD = 1, FOREST_WORLD = 2;
    private int x, y;

    public void setX (int x) {
        this.x = x;
    }

    public void setY (int y) {
        this.y = y;
    }

    public int getX () {
        return x;
    }

    public int getY () {
        return y;
    }

    public int getLayer () {
        return ScreenLayoutManager.BACKGROUND;
    }
}

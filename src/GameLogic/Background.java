package GameLogic;

import Graphics.Image;
import Graphics.ScreenLayoutManager;

import java.awt.*;

public class Background {
    private final Image backgroundImage;
    private int x, y;
    private Dimension size;

    public Background (Image backgroundImage) {
        this.backgroundImage = backgroundImage;
        size = new Dimension(backgroundImage.getSource().getWidth(),backgroundImage.getSource().getHeight());
    }

    public Image getImage () {
        return backgroundImage;
    }

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

    public Dimension getSize() {
        return size;
    }
}

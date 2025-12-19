package Graphics;

import javax.swing.*;
import java.awt.*;


public class Canvas extends JPanel {
    private final ScreenLayoutManager layoutManager;

    public Canvas (Dimension size) {
        layoutManager = new ScreenLayoutManager(this);
        setLayout(layoutManager);
        layoutManager.setBackgroundSize(size);
    }

    public void draw (GraphicsObject imToDraw, int x, int y, int layer) {
        // gather info for constraints
        Integer[] constraints = new Integer[3];
        constraints[0] = layer;
        constraints[1] = x;
        constraints[2] = y;

        add(imToDraw,constraints);
    }

    public void refresh () {
        repaint();
        revalidate();
    }

    public void setZoomFactor (double zoomFactor) {
        layoutManager.setZoomFactor(zoomFactor);
    }

    public double getZoomFactor () {
        return layoutManager.getZoomFactor();
    }
}

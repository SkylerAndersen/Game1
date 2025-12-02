package Graphics;

import DataManagement.FileHandler;

import javax.swing.*;
import java.awt.*;


public class Canvas {
    public static final int SAND_WORLD = 0, MIDDLE_WORLD = 1, FOREST_WORLD = 2;
    private final FileHandler fileHandler = FileHandler.get();
    private final JPanel applicationScreen;
    private ScreenLayoutManager layoutManager;
    private Runnable drawCleanup;

    public Canvas (Dimension size) {
        this.applicationScreen = new JPanel();
        layoutManager = new ScreenLayoutManager(applicationScreen);
        applicationScreen.setLayout(layoutManager);
        layoutManager.setBackgroundSize(size);
    }

    public void redraw () {
        applicationScreen.repaint();
        applicationScreen.revalidate();
    }

    public void runDrawCleanup () {
        if (drawCleanup == null)
            return;

        drawCleanup.run();
        drawCleanup = null;
    }

    public void setDrawCleanup (Runnable drawCleanup) {
        this.drawCleanup = drawCleanup;
    }

    public void draw (Image imToDraw, int x, int y, boolean flipped, int layer) {
        // gather info for constraints
        Integer[] constraints = new Integer[3];
        constraints[0] = layer;
        constraints[1] = x;
        constraints[2] = y;

        // wrap in swing object and draw to screen
        if (flipped)
            imToDraw.setReversed();
        else // ensure the wrapper wraps the current source
            imToDraw.wrap();
        applicationScreen.add(imToDraw.getWrapper(),constraints);
    }

    public void setZoomFactor (double zoomFactor) {
        layoutManager.setZoomFactor(zoomFactor);
    }

    public double getZoomFactor () {
        return layoutManager.getZoomFactor();
    }

    public Dimension getBackgroundScreenSize () {
        return ImageUtilities.getBackground().getSize();
    }

    public JPanel getApplicationScreen () {
        return applicationScreen;
    }
}

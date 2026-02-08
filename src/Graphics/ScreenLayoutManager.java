package Graphics;

import javax.swing.*;
import java.awt.*;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.lang.reflect.Array;
import java.util.HashMap;

public class ScreenLayoutManager implements LayoutManager, LayoutManager2 {
    public static final Integer BACKGROUND = 0, CHARACTER = 1, ITEM = 2;
    private Container parent;
    @SuppressWarnings("unchecked")
    private final HashMap<Component,BufferedImage>[] graphicsElements =
            (HashMap<Component, BufferedImage>[]) Array.newInstance(HashMap.class,3);
    private HashMap<Component,int[]> coordinates;
    private Dimension backgroundSize;
    private JLabel imageContainer;
    private double zoomFactor;

    public ScreenLayoutManager(Container parent) {
        this.parent = parent;
        layer = new HashMap<>();
        coordinates = new HashMap<>();
        graphicsElements[ScreenLayoutManager.BACKGROUND] = new HashMap<>();
        graphicsElements[ScreenLayoutManager.CHARACTER] = new HashMap<>();
        graphicsElements[ScreenLayoutManager.ITEM] = new HashMap<>();
        imageContainer = new JLabel();
        backgroundSize = parent.getSize();
        parent.add("Container",imageContainer);
        zoomFactor = 1.0;
    }

    /**
     * Not Implemented in this LayoutManager.
     * */
    @Override
    public void addLayoutComponent(String name, Component comp) {}

    @Override
    public void removeLayoutComponent(Component comp) {
        if (!(comp instanceof JLabel) || ((JLabel) comp).getIcon() == null)
            return;

        coordinates.remove(comp);
        graphicsElements[ScreenLayoutManager.BACKGROUND].remove(comp);
        graphicsElements[ScreenLayoutManager.CHARACTER].remove(comp);
        graphicsElements[ScreenLayoutManager.ITEM].remove(comp);
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        boolean sizeInitialized = parent.getSize().width == 0 || parent.getSize().height == 0;
        return sizeInitialized ? parent.getSize() : parent.getPreferredSize();
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        return parent.getMinimumSize();
    }
    private void layoutContainerHelper (BufferedImage canvas, int layer) {
        for (Component imageComponent : graphicsElements[layer].keySet()) {
            int x = coordinates.get(imageComponent)[0];
            int y = coordinates.get(imageComponent)[1];
            BufferedImage image = graphicsElements[layer].get(imageComponent);
            int currWidth = image.getWidth();
            int currHeight = image.getHeight();
            for (int i = 0; i < currWidth && i + x < backgroundSize.width; i++) {
                for (int j = 0; j < currHeight && j + y < backgroundSize.height; j++) {
                    if (image.getRGB(i,j) >> 24 == 0)
                        continue;
                    boolean outOfBoundsX = i+x >= backgroundSize.width || i+x < 0;
                    boolean outOfBoundsY = j+y >= backgroundSize.height || j+y < 0;
                    if (!outOfBoundsX && !outOfBoundsY)
                        canvas.setRGB(i+x,j+y,image.getRGB(i,j));
                }
            }
        }
        canvas.flush();
    }

    @Override
    public void layoutContainer(Container parent) {
        int width = parent.getSize().width;
        int height = parent.getSize().height;
        double scaleFactor = (double)height / backgroundSize.height;
        scaleFactor *= zoomFactor;
//        System.out.printf("Height: %d, bgHeight: %d\n",height,bgHeight);

        // generate image to draw
        BufferedImage canvas = new BufferedImage(backgroundSize.width,backgroundSize.height,
                BufferedImage.TYPE_INT_ARGB);
        layoutContainerHelper(canvas,ScreenLayoutManager.BACKGROUND);
        layoutContainerHelper(canvas,ScreenLayoutManager.CHARACTER);
        layoutContainerHelper(canvas, ScreenLayoutManager.ITEM);

        // scale image
        int scaledWidth = (int)(backgroundSize.width * scaleFactor);
        int scaledHeight = (int)(backgroundSize.height * scaleFactor);
//        System.out.println(""+bgWidth+" "+bgHeight+" "+scaleFactor+" ");
        ImageIcon toDraw = new ImageIcon(canvas.getScaledInstance(scaledWidth,
                scaledHeight,Image.SCALE_FAST));

        // draw image
//        System.out.printf("Scaled Width: %d, Scaled Height: %d\n",scaledWidth,scaledHeight);
        int labelX = (width - scaledWidth) / 2;
        int labelY = 0;
        imageContainer.setBounds(labelX,labelY,scaledWidth,scaledHeight);
        imageContainer.setIcon(toDraw);
//        System.out.printf("LabelX: %d, LabelY: %d\n",labelX,labelY);
    }

    @Override
    public void addLayoutComponent(Component comp, Object constraints) {
        if (!(constraints instanceof Integer[] layerXY) || layerXY.length != 3)
            throw new RuntimeException("Invalid Constraints Parameter.");

        if (!(comp instanceof JLabel label) || label.getIcon() == null)
            return;

        // get the image properties
        int[] XY = {layerXY[1],layerXY[2]};
        if (!coordinates.containsKey(comp))
            coordinates.put(comp,XY);

        // get the image itself
        BufferedImage graphicsElement = (BufferedImage) ((ImageIcon) label.getIcon()).getImage();
        graphicsElements[layerXY[0]].put(comp,graphicsElement);
    }

    public void setBackgroundSize (Dimension newSize) {
        backgroundSize = newSize;
    }

    @Override
    public Dimension maximumLayoutSize(Container target) {
        return target.getMaximumSize();
    }

    @Override
    public float getLayoutAlignmentX(Container target) {
        return target.getAlignmentX();
    }

    @Override
    public float getLayoutAlignmentY(Container target) {
        return target.getAlignmentY();
    }

    /**
     * Not Implemented in this LayoutManager.
     * */
    @Override
    public void invalidateLayout(Container target) {
    }

    public void setZoomFactor (double zoomFactor) {
        this.zoomFactor = zoomFactor;
    }

    public double getZoomFactor () {
        return zoomFactor;
    }
}

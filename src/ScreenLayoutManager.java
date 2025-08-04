import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;

public class ScreenLayoutManager implements LayoutManager, LayoutManager2 {
    public static final Integer BACKGROUND = 0, CHARACTER = 1, ITEM = 2;
    private Container parent;
    private HashMap<Component,BufferedImage> graphicsElements;
    private HashMap<Component,Integer> layer;
    private HashMap<Component,int[]> coordinates;
    private int bgWidth, bgHeight;
    private JLabel imageContainer;
    private boolean hasExported;
    private final InputManager inputManager = InputManager.get();

    public ScreenLayoutManager(Container parent) {
        this.parent = parent;
        layer = new HashMap<>();
        coordinates = new HashMap<>();
        graphicsElements = new HashMap<>();
        imageContainer = new JLabel();
        bgWidth = 0;
        bgHeight = 0;
        parent.add("Container",imageContainer);
        hasExported = false;
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
        layer.remove(comp);
        coordinates.remove(comp);
        graphicsElements.remove(comp);
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

    @Override
    public void layoutContainer(Container parent) {
        int width = parent.getSize().width;
        int height = parent.getSize().height;
        double scaleFactor = (double)height / bgHeight;
//        System.out.printf("Height: %d, bgHeight: %d\n",height,bgHeight);

        // generate image to draw
        BufferedImage canvas = new BufferedImage(bgWidth,bgHeight,BufferedImage.TYPE_INT_ARGB);
        for (Component imageComponent : graphicsElements.keySet()) {
            if (layer.get(imageComponent).equals(ScreenLayoutManager.BACKGROUND)) {
                int x = coordinates.get(imageComponent)[0];
                int y = coordinates.get(imageComponent)[1];
                BufferedImage image = graphicsElements.get(imageComponent);
                int currWidth = image.getWidth();
                int currHeight = image.getHeight();
                for (int i = 0; i < currWidth && i + x < bgWidth; i++) {
                    for (int j = 0; j < currHeight && j + x < bgHeight; j++) {
                        if (image.getRGB(i,j) >> 24 == 0)
                            continue;
                        canvas.setRGB(i+x,j+y,image.getRGB(i,j));
                    }
                }
            }
            canvas.flush();
        }
        for (Component imageComponent : graphicsElements.keySet()) {
            if (layer.get(imageComponent).equals(ScreenLayoutManager.CHARACTER)) {
                int x = coordinates.get(imageComponent)[0];
                int y = coordinates.get(imageComponent)[1];
                BufferedImage image = graphicsElements.get(imageComponent);
                int currWidth = image.getWidth();
                int currHeight = image.getHeight();
                System.out.println("bgWidth: "+bgWidth+", bgHeight: "+bgHeight);
                for (int i = 0; i < currWidth && i + x < bgWidth; i++) {
                    for (int j = 0; j < currHeight && j + y < bgHeight; j++) {
                        if (image.getRGB(i,j) >> 24 == 0)
                            continue;
                        canvas.setRGB(i+x,j+y,image.getRGB(i,j));
                    }
                }
            }
            canvas.flush();
        }
        for (Component imageComponent : graphicsElements.keySet()) {
            if (layer.get(imageComponent).equals(ScreenLayoutManager.ITEM)) {
                int x = coordinates.get(imageComponent)[0];
                int y = coordinates.get(imageComponent)[1];
                BufferedImage image = graphicsElements.get(imageComponent);
                int currWidth = image.getWidth();
                int currHeight = image.getHeight();
                for (int i = 0; i < currWidth && i + x < bgWidth; i++) {
                    for (int j = 0; j < currHeight && j + x < bgHeight; j++) {
                        if (image.getRGB(i,j) >> 24 == 0)
                            continue;
                        canvas.setRGB(i+x,j+y,image.getRGB(i,j));
                    }
                }
            }
            canvas.flush();
        }

        // scale image
        int scaledWidth = (int)(bgWidth * scaleFactor);
        int scaledHeight = (int)(bgHeight * scaleFactor);
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
        if (!(constraints instanceof Integer[]))
            throw new RuntimeException("Invalid Constrains Parameter.");

        Integer[] layerXY = (Integer[])constraints;
        if (layerXY.length != 3)
            throw new RuntimeException("Invalid Constrains Parameter.");

        if (!(comp instanceof JLabel) || ((JLabel) comp).getIcon() == null)
            return;

        // get the image properties
        if (!layer.containsKey(comp))
            layer.put(comp,layerXY[0]);
        int[] XY = {layerXY[1],layerXY[2]};
        if (!coordinates.containsKey(comp))
            coordinates.put(comp,XY);

        // get the image itself
        java.awt.Image image = ((ImageIcon) ((JLabel) comp).getIcon()).getImage();
        BufferedImage graphicsElement = new BufferedImage(image.getWidth(null),
                image.getHeight(null),BufferedImage.TYPE_INT_ARGB);
        graphicsElement.getGraphics().drawImage(image,0,0,null);
        if (graphicsElements.containsKey(comp))
            graphicsElements.remove(comp);
        graphicsElements.put(comp,graphicsElement);

        // get the width and height of the background
        if (graphicsElement.getWidth() > bgWidth)
            bgWidth = graphicsElement.getWidth();
        if (graphicsElement.getHeight() > bgHeight)
            bgHeight = graphicsElement.getHeight();
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
}

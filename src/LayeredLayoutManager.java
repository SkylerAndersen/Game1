import java.awt.*;
import java.util.HashMap;

public class LayeredLayoutManager implements LayoutManager, LayoutManager2 {
    public static final Integer BACKGROUND = 0, CHARACTER = 1, ITEM = 2;
    private Container parent;
    private HashMap<Component,Integer> layer;
    private HashMap<Component,int[]> coordinates;

    public LayeredLayoutManager (Container parent) {
        this.parent = parent;
        layer = new HashMap<>();
        coordinates = new HashMap<>();
    }

    /**
     * Not Implemented in this LayoutManager.
     * */
    @Override
    public void addLayoutComponent(String name, Component comp) {}

    @Override
    public void removeLayoutComponent(Component comp) {
        layer.remove(comp);
        coordinates.remove(comp);
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
        for (Component component : parent.getComponents()) {
            int x = coordinates.get(component)[0];
            int y = coordinates.get(component)[1];
            int width = component.getWidth();
            int height = component.getHeight();
            component.setBounds(x,y,width,height);
        }
        for (Component component : parent.getComponents()) {
            if (!layer.get(component).equals(BACKGROUND))
                continue;
            component.repaint();
        }
        for (Component component : parent.getComponents()) {
            if (!layer.get(component).equals(CHARACTER))
                continue;
            component.repaint();
        }
        for (Component component : parent.getComponents()) {
            if (!layer.get(component).equals(ITEM))
                continue;
            component.repaint();
        }
    }

    @Override
    public void addLayoutComponent(Component comp, Object constraints) {
        if (!(constraints instanceof Integer[]))
            throw new RuntimeException("Invalid Constrains Parameter.");

        Integer[] layerXY = (Integer[])constraints;
        if (layerXY.length != 3)
            throw new RuntimeException("Invalid Constrains Parameter.");

        layer.put(comp,layerXY[0]);
        int[] XY = {layerXY[1],layerXY[2]};
        coordinates.put(comp,XY);
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

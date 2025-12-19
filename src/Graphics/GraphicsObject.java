package Graphics;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class GraphicsObject extends JLabel {
    private static int numGraphicsObjects;
    private final int id;
    private int currentState;
    private boolean flipped;
    private final HashMap<Integer,Image> states;

    public GraphicsObject () {
        id = numGraphicsObjects++;
        flipped = false;
        states = new HashMap<>();
        setIcon(new ImageIcon());
        currentState = -1;
    }

    public int addState (Image state) {
        if (states.containsKey(state.getId()))
            return -1;

        states.put(state.getId(),state);
        return state.getId();
    }

    public void setState (int id) {
        if (!states.containsKey(id) || currentState == id)
            return;

        currentState = id;

        Image stateImage = states.get(currentState);
        BufferedImage stateSource = flipped ? stateImage.getReverseSource() : stateImage.getSource();
        ((ImageIcon) getIcon()).setImage(stateSource);
    }

    public void setFlipped (boolean flipped) {
        this.flipped = flipped;
    }

    public int getId () {
        return id;
    }

    public Dimension getSize () {
        if (!states.containsKey(currentState))
            return new Dimension(0,0);

        return states.get(currentState).getSize();
    }
}

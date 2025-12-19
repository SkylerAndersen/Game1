package Graphics;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class GraphicsObject extends JLabel {
    public static final int EQUALS = 0, REVERSE = -1, DIFFERS = 1;
    private static int numGraphicsObjects;
    private final int id;
    private Image image;

    public GraphicsObject () {
        id = numGraphicsObjects;
        numGraphicsObjects++;
    }

    public void setImage (Image image) {
        this.image = image;
        setIcon(new ImageIcon(image.getSource()));

        // create flipped counterpart
        BufferedImage reverse = new BufferedImage(image.getSource().getWidth(),
                image.getSource().getHeight(),BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i <= reverse.getWidth()/2; i++) {
            for (int j = 0; j < reverse.getHeight(); j++) {
                reverse.setRGB(i,j,image.getSource().getRGB(image.getSource().getWidth()-1-i,j));
                reverse.setRGB(image.getSource().getWidth()-1-i,j,image.getSource().getRGB(i,j));
            }
        }

        image.setFlippedImage(new Image(reverse));
        image.getFlippedImage().setFlippedImage(image);

    }

    public void flip () {
        image = image.getFlippedImage();
        setIcon(new ImageIcon(image.getSource()));
    }

    public int compare (GraphicsObject other) {
        if (other.image.getId() == image.getId())
            return EQUALS;
        if (other.image.getFlippedImage().getId() == image.getId())
            return REVERSE;
        return DIFFERS;
    }

    public Image getImage () {
        return image;
    }

    public int getId () {
        return id;
    }
}

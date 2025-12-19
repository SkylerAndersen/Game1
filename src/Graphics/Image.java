package Graphics;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Image {
    public static int count = 0;
    private final int id;
    private final BufferedImage source, sourceReverse;
    private final Dimension size;

    public Image (java.awt.Image source) {
        this.id = count++;

        BufferedImage imageToWrap;
        if (!(source instanceof BufferedImage)) {
            imageToWrap = new BufferedImage(source.getWidth(null),
                    source.getHeight(null),BufferedImage.TYPE_INT_ARGB);
            imageToWrap.getGraphics().drawImage(source,0,0,null);
            imageToWrap.flush();
        } else {
            imageToWrap = (BufferedImage) source;
        }

        BufferedImage reverse = new BufferedImage(imageToWrap.getWidth(),
                imageToWrap.getHeight(),BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i <= reverse.getWidth()/2; i++) {
            for (int j = 0; j < reverse.getHeight(); j++) {
                reverse.setRGB(i,j,imageToWrap.getRGB(imageToWrap.getWidth()-1-i,j));
                reverse.setRGB(imageToWrap.getWidth()-1-i,j,imageToWrap.getRGB(i,j));
            }
        }

        this.source = imageToWrap;
        sourceReverse = reverse;
        size = new Dimension(this.source.getWidth(),this.source.getHeight());
    }

    public int getId () {
        return id;
    }

    public BufferedImage getSource () {
        return source;
    }

    public BufferedImage getReverseSource () {
        return sourceReverse;
    }

    public Dimension getSize () {
        return size;
    }
}

package Graphics;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class Image {
    public static int count = 0;
    private final int id;
    private BufferedImage bufferedImage;
    private BufferedImage reversedImage;
    private JLabel wrapper;

    public Image (java.awt.Image imageToWrap) {
        this.id = count++;
        wrapper = new JLabel();
        changeSource(imageToWrap);
    }

    public int getId () {
        return id;
    }

    public void changeSourceWithoutWrapping (java.awt.Image imageToWrap) {
        if (!(imageToWrap instanceof BufferedImage)) {
            BufferedImage temp = new BufferedImage(imageToWrap.getWidth(null),
                    imageToWrap.getHeight(null),BufferedImage.TYPE_INT_ARGB);
            temp.getGraphics().drawImage(imageToWrap,0,0,null);
            temp.flush();
            imageToWrap = temp;
        }
        this.bufferedImage = (BufferedImage) imageToWrap;
        reversedImage = null;
    }

    public void changeSource (java.awt.Image imageToWrap) {
        if (!(imageToWrap instanceof BufferedImage)) {
            BufferedImage temp = new BufferedImage(imageToWrap.getWidth(null),
                    imageToWrap.getHeight(null),BufferedImage.TYPE_INT_ARGB);
            temp.getGraphics().drawImage(imageToWrap,0,0,null);
            temp.flush();
            imageToWrap = temp;
        }
        this.bufferedImage = (BufferedImage) imageToWrap;
        wrap();
        reversedImage = null;
    }

    public void wrap () {
        wrap(bufferedImage);
    }

    private void wrap (BufferedImage bufferedImage) {
        if (wrapper.getIcon() == null) {
            wrapper.setIcon(new ImageIcon(bufferedImage));
            wrapper.setSize(bufferedImage.getWidth(),bufferedImage.getHeight());
        } else
            ((ImageIcon) wrapper.getIcon()).setImage(bufferedImage);
    }

    private BufferedImage flipImage (BufferedImage image) {
        BufferedImage reverse = new BufferedImage(image.getWidth(),
                image.getHeight(),BufferedImage.TYPE_INT_ARGB);

        for (int i = 0; i <= reverse.getWidth()/2; i++) {
            for (int j = 0; j < reverse.getHeight(); j++) {
                reverse.setRGB(i,j,image.getRGB(image.getWidth()-1-i,j));
                reverse.setRGB(image.getWidth()-1-i,j,image.getRGB(i,j));
            }
        }

        return reverse;
    }

    public void setReversed () {
        if (reversedImage == null)
            reversedImage = flipImage(bufferedImage);
        wrap(reversedImage);
    }

    public BufferedImage getSource () {
        return bufferedImage;
    }

    public JLabel getWrapper () {
        return wrapper;
    }
}

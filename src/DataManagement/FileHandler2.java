package DataManagement;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;

public class FileHandler2 {
    private String projectPath;
    private String imagePath;
    private NonvolatileStream stream;
    private NonvolatileHashMap hashMap;
    public FileHandler2 (String projectPath) {
        this.projectPath = projectPath;
        File hashMapFile = new File(projectPath + "addressing.bin");
        File streamFile = new File(projectPath + "data.bin");
        stream = new NonvolatileStream(streamFile.toPath().toString());
        hashMap = new NonvolatileHashMap(hashMapFile.toPath().toString());
        imagePath = "/resources/";
    }

    public boolean retrieveBoolean (String name) {
        if (!hashMap.hasKey(name))
            return false;

        int location = hashMap.retrieve(name);
        return (boolean) stream.retrieve(location);
    }

    public void save (String name, boolean data) {
        int location = stream.add(data);
        hashMap.add(name,location);
    }

    public int retrieveInt (String name) {
        if (!hashMap.hasKey(name))
            return -1;

        int location = hashMap.retrieve(name);
        return (int) stream.retrieve(location);
    }

    public void save (String name, int data) {
        int location = stream.add(data);
        hashMap.add(name,location);
    }

    public String retrieveString (String name) {
        if (!hashMap.hasKey(name))
            return "";

        int location = hashMap.retrieve(name);
        return (String) stream.retrieve(location);
    }

    public void save (String name, String data) {
        int location = stream.add(data);
        hashMap.add(name,location);
    }

    public boolean[] retrieveBooleanArray (String name) {
        if (!hashMap.hasKey(name))
            return new boolean[0];

        int location = hashMap.retrieve(name);
        return (boolean[]) stream.retrieve(location);
    }

    public void save (String name, boolean[] data) {
        int location = stream.add(data);
        hashMap.add(name,location);
    }

    public int[] retrieveIntArray (String name) {
        if (!hashMap.hasKey(name))
            return new int[0];

        int location = hashMap.retrieve(name);
        return (int[]) stream.retrieve(location);
    }

    public void save (String name, int[] data) {
        int location = stream.add(data);
        hashMap.add(name,location);
    }

    public String[] retrieveStringArray (String name) {
        if (!hashMap.hasKey(name))
            return new String[0];

        int location = hashMap.retrieve(name);
        return (String[]) stream.retrieve(location);
    }

    public void save (String name, String[] data) {
        int location = stream.add(data);
        hashMap.add(name,location);
    }

    public ImageIcon retrieveImage (String name) {
        if (imagePath == null)
            return new ImageIcon();

        ImageIcon output = new ImageIcon();
        try {
            InputStream imageStream = Class.forName("Main").getResourceAsStream(imagePath+name+".png");
            BufferedImage image = ImageIO.read(imageStream);
            output = new ImageIcon(image);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Faild at path: "+imagePath+name+".png");
        }
        return output;
    }

    public void save (String name, ImageIcon image) {
        if (imagePath == null)
            return;

        // get a rendered image from our ImageIcon
        java.awt.Image imageToWrite = image.getImage();
        // if the ImageIcon's Image is not already a BufferedImage, i.e. not a RenderedImage
        if (!(imageToWrite instanceof BufferedImage)) {
            BufferedImage bufferedImage = new BufferedImage(imageToWrite.getWidth(null),
                    imageToWrite.getHeight(null),BufferedImage.TYPE_INT_ARGB);
            bufferedImage.getGraphics().drawImage(imageToWrite,0,0,null);
            imageToWrite = bufferedImage;
        }

        // write image to file
        try {
            ImageIO.write((BufferedImage) imageToWrite, "png",
                    new File(imagePath+name+".png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package DataManagement;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Objects;

public class FileHandler {
    private String devResourcePath, devSaveStatePath, packageResourcePath, packageSaveStatePath,
            resourcePath, saveStatePath;
    private boolean packageMode, onWindows, onMac;
    private static FileHandler fileHandler;
    private NonvolatileStream stream;
    private NonvolatileHashMap hashMap;

    private FileHandler () {
        packageMode = false;
        onWindows = false;
        onMac = false;
        setupImportantPaths();
        setPackageMode(System.getProperty("java.class.path").contains(".jar"));
    }

    public byte[] readAudio (String fileName) {
        byte[] output = null;
        try {
            AudioInputStream audioInputStream;
            if (packageMode) {
                InputStream inputStream = Objects.requireNonNull(Class.
                        forName("Main").getResourceAsStream(resourcePath + fileName));
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                audioInputStream = AudioSystem.getAudioInputStream(bufferedInputStream);
            } else {
                audioInputStream = AudioSystem.getAudioInputStream(new File(resourcePath+fileName));
            }
            output = audioInputStream.readAllBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Audio at: "+resourcePath+fileName);
        return output;
    }

    public BufferedImage readImage (String fileName) {
        BufferedImage output = null;
        try {
            if (packageMode) {
                InputStream imageStream = Class.forName("Main").
                        getResourceAsStream(resourcePath+fileName);
                output = ImageIO.read(imageStream);
            } else {
                File imageFile = new File(resourcePath+fileName);
                output = ImageIO.read(imageFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Image at: "+resourcePath+fileName);
        return output;
    }

    public boolean readBoolean (String name) {
        if (!hashMap.hasKey(name))
            return false;

        int location = hashMap.retrieve(name);
        return (boolean) stream.retrieve(location);
    }

    public int readInt (String name) {
        if (!hashMap.hasKey(name))
            return -1;

        int location = hashMap.retrieve(name);
        return (int) stream.retrieve(location);
    }

    public String readString (String name) {
        if (!hashMap.hasKey(name))
            return "";

        int location = hashMap.retrieve(name);
        return (String) stream.retrieve(location);
    }

    public boolean[] readBooleanArray (String name) {
        if (!hashMap.hasKey(name))
            return new boolean[0];

        int location = hashMap.retrieve(name);
        return (boolean[]) stream.retrieve(location);
    }

    public int[] readIntArray (String name) {
        if (!hashMap.hasKey(name))
            return new int[0];

        int location = hashMap.retrieve(name);
        return (int[]) stream.retrieve(location);
    }

    public String[] readStringArray (String name) {
        if (!hashMap.hasKey(name))
            return new String[0];

        int location = hashMap.retrieve(name);
        return (String[]) stream.retrieve(location);
    }

    public void write (String name, boolean data) {
        int location = stream.add(data);
        hashMap.add(name,location);
    }

    public void write (String name, int data) {
        int location = stream.add(data);
        hashMap.add(name,location);
    }

    public void write (String name, String data) {
        int location = stream.add(data);
        hashMap.add(name,location);
    }

    public void write (String name, boolean[] data) {
        int location = stream.add(data);
        hashMap.add(name,location);
    }

    public void write (String name, int[] data) {
        int location = stream.add(data);
        hashMap.add(name,location);
    }

    public void write (String name, String[] data) {
        int location = stream.add(data);
        hashMap.add(name,location);
    }

    public static FileHandler get () {
        if (fileHandler == null)
            fileHandler = new FileHandler();
        return fileHandler;
    }

    public void setPackageMode (boolean packageMode) {
        this.packageMode = packageMode;
        resourcePath = packageMode ? packageResourcePath : devResourcePath;
        saveStatePath = packageMode ? packageSaveStatePath : devSaveStatePath;
        stream = new NonvolatileStream(saveStatePath+"stream.bin");
        hashMap = new NonvolatileHashMap(saveStatePath+"hash-map.bin");
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public String getSaveStatePath () {
        return saveStatePath;
    }

    private void setupImportantPaths () {
        onWindows = System.getProperty("os.name").toLowerCase().contains("win");
        onMac = System.getProperty("os.name").toLowerCase().contains("mac");
        if (onWindows) {
            devResourcePath = System.getProperty("user.dir") + "\\src\\development-resources\\";
            devSaveStatePath = devResourcePath;
            packageResourcePath = "/resources/";
            packageSaveStatePath = System.getProperty("user.home").substring(0,3)+"\\Questify";
            new File(packageSaveStatePath).mkdir();
            packageSaveStatePath += "\\resources\\";
            new File(packageSaveStatePath.substring(0,packageSaveStatePath.length()-1)).mkdir();
        } else if (onMac) {
            devResourcePath = System.getProperty("user.dir") + "/src/development-resources/";
            devSaveStatePath = devResourcePath;
            packageResourcePath = "/resources/";
            packageSaveStatePath = "/Applications/Game1.app/Contents/Resources/";
        } else {
            devResourcePath = System.getProperty("user.dir") + "/src/development-resources/";
            devSaveStatePath = devResourcePath;
            packageResourcePath = "/resources/";
            packageSaveStatePath = System.getProperty("user.home")+"/Questify";
            new File(packageSaveStatePath).mkdir();
            packageSaveStatePath += "/resources/";
            new File(packageSaveStatePath.substring(0,packageSaveStatePath.length()-1)).mkdir();
        }
        resourcePath = devResourcePath;
        saveStatePath = devSaveStatePath;
    }
}

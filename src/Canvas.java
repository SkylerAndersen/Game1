import DataManagement.FileHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class Canvas {
    public static final int SAND_WORLD = 0;
    public static final int MIDDLE_WORLD = 1;
    public static final int FOREST_WORLD = 2;
    public static final int BASE_POSE = 3;
    public static final int WALK_POSE = 4;
    public static final int ATTACK_POSE = 5;
    public static final int MAIN_CHARACTER = 6;
    public static final int ALT_CHARACTER = 7;
    private final FileHandler fileHandler = FileHandler.get();
    private final InputManager inputManager = InputManager.get();
    private JPanel applicationScreen;
    private long previousCycleTime;
    private Image background;
    private Image character, walkPose, attackPose, altCharacter, altWalkPose, altAttackPose;
    private JLabel previousWorldImage;
    private JLabel previousCharacterImage;

    public Canvas (JPanel applicationScreen) {
        this.applicationScreen = applicationScreen;
        previousCycleTime = System.currentTimeMillis();
        applicationScreen.setLayout(new LayeredLayoutManager(applicationScreen));
    }
    public void loadWorld (int worldNum) {
        String name = worldNum == 0 ? "sand-world.png" : worldNum == 1 ? "middle-world.png" :
                "forest-world.png";
        background = fileHandler.readImage(name);
        drawWorldFromCoordinates(0,0);
    }

    public void loadCharacter (int characterNum) {
        character = fileHandler.readImage("base-pose.png");
        walkPose = fileHandler.readImage("walk-pose.png");
        attackPose = fileHandler.readImage("attack-pose.png");
    }

    public void refreshCycle () {
        Thread render = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    drawWorldFromCoordinates(0,0);
                    drawCharacterFromCoordinates(MAIN_CHARACTER,BASE_POSE,0,0,true);
                    applicationScreen.repaint();
                    applicationScreen.revalidate();

                    // compute fps
                    long time = System.currentTimeMillis();
                    double fps = (int) (100000 / (time - previousCycleTime+0.000001))/100.0;
                    previousCycleTime = time;
//                    System.out.printf("FPS is approximately %.2f\n", fps);
                }
            }
        });
        render.start();
    }

    public void drawWorldFromCoordinates (int x, int y) {
        if (previousWorldImage == null)
            previousWorldImage = new JLabel();

        int width = (int)applicationScreen.getParent().getSize().getWidth();
        int height = (int)applicationScreen.getParent().getSize().getHeight();
        width = width == 0 ? 1000 : width;
        height = height == 0 ? 800 : height;
        Image image = background.getScaledInstance(width,height, Image.SCALE_FAST);
        Integer[] constraints = new Integer[3];
        constraints[0] = 0;
        constraints[1] = x;
        constraints[2] = y;
        previousWorldImage.setIcon(new ImageIcon(image));
        previousWorldImage.setSize(new Dimension(width,height));
        applicationScreen.add(previousWorldImage,constraints);
    }

    public void drawCharacterFromCoordinates (int character, int pose, int x, int y) {
        drawCharacterFromCoordinates(character,pose,x,y,false);
    }

    public void drawCharacterFromCoordinates (int character, int pose, int x, int y, boolean flipped) {
        if (previousCharacterImage == null)
            previousCharacterImage = new JLabel();

        // select image
        Image drawing = (character == MAIN_CHARACTER) ? switch (pose) {
            case ATTACK_POSE -> attackPose;
            case WALK_POSE -> walkPose;
            default -> this.character;
        } : switch (pose) {
            case ATTACK_POSE -> altAttackPose;
            case WALK_POSE -> altWalkPose;
            default -> altCharacter;
        };

        // flip
        if (flipped) {
            BufferedImage image = new BufferedImage(drawing.getWidth(null),
                    drawing.getHeight(null),BufferedImage.TYPE_INT_ARGB);
            image.getGraphics().drawImage(drawing,0,0,null);
            image.flush();

            BufferedImage copy = new BufferedImage(drawing.getWidth(null),
                    drawing.getHeight(null),BufferedImage.TYPE_INT_ARGB);
            for (int i = 0; i < copy.getWidth()/2; i++) {
                for (int j = 0; j < copy.getHeight(); j++) {
                    copy.setRGB(i,j,image.getRGB(image.getWidth()-1-i,j));
                    copy.setRGB(copy.getWidth()-1-i,j,image.getRGB(i,j));
                }
            }

            drawing = copy;
        }


        // draw
        int width = drawing.getWidth(null);
        int height = drawing.getHeight(null);
        Integer[] constraints = new Integer[3];
        constraints[0] = 1;
        constraints[1] = x;
        constraints[2] = y;
        previousCharacterImage.setIcon(new ImageIcon(drawing));
        previousCharacterImage.setSize(new Dimension(width,height));
        applicationScreen.add(previousCharacterImage,constraints);
    }
}

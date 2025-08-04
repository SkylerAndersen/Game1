import DataManagement.FileHandler;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

public class Canvas {
    public static final int SAND_WORLD = 0, MIDDLE_WORLD = 1, FOREST_WORLD = 2, BASE_POSE = 3, WALK_POSE = 4,
            ATTACK_POSE = 5, MAIN_CHARACTER = 6, ALT_CHARACTER = 7;
    private final FileHandler fileHandler = FileHandler.get();
    private final InputManager inputManager = InputManager.get();
    private JPanel applicationScreen;
    private long previousCycleTime;
    private Image background;
    private Image character, walkPose, attackPose, altCharacter, altWalkPose, altAttackPose;
    private JLabel wrapper, wrapper2;
    private int characterX, characterY;

    public Canvas (JPanel applicationScreen) {
        this.applicationScreen = applicationScreen;
        previousCycleTime = System.currentTimeMillis();
        wrapper = new JLabel();
        wrapper2 = new JLabel();
        applicationScreen.setLayout(new ScreenLayoutManager(applicationScreen));
//        applicationScreen.setLayout(null);
    }

    public void moveCharacter (int x, int y, int deltaTime) {
        boolean validX = characterX+x >= 0 && characterX+x+character.getWidth(null) <
                background.getWidth(null);
        boolean validY = characterY+y >= 0 && characterY+y+character.getHeight(null) <
                background.getHeight(null);
        if (validX) {
            characterX += x*deltaTime/20;
        }
        if (validY) {
            characterY += y*deltaTime/20;
        }
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
        drawCharacterFromCoordinates(characterNum,Canvas.BASE_POSE,0,0,true);
    }

    public void refreshCycle () {
        Thread render = new Thread(new Runnable() {
            @Override
            public void run() {
                long time = System.currentTimeMillis();
                long total = 0;
                int deltaTime = (int)(time - previousCycleTime);
                while (true) {
                    if (inputManager.queryKeyPress('w')) {
                        moveCharacter(0,-1,deltaTime);
                    }
                    if (inputManager.queryKeyPress('a')) {
                        moveCharacter(-1,0,deltaTime);
                    }
                    if (inputManager.queryKeyPress('s')) {
                        moveCharacter(0,1,deltaTime);
                    }
                    if (inputManager.queryKeyPress('d')) {
                        moveCharacter(1,0,deltaTime);
                    }
                    drawWorldFromCoordinates(0,0);
                    drawCharacterFromCoordinates(MAIN_CHARACTER,BASE_POSE,characterX,characterY,true);
                    applicationScreen.repaint();
                    applicationScreen.revalidate();

                    // compute fps
                    double fps = (int) (100000 / (time - previousCycleTime+0.000001))/100.0;
//                    previousCycleTime = time;
//                    System.out.printf("FPS is approximately %.2f\n", fps);
                }
            }
        });
        render.start();
    }

    public void drawWorldFromCoordinates (int x, int y) {
        Integer[] constraints = new Integer[3];
        constraints[0] = ScreenLayoutManager.BACKGROUND;
        constraints[1] = x;
        constraints[2] = y;
        if (wrapper.getIcon() == null) {
            wrapper.setIcon(new ImageIcon(background));
            wrapper.setSize(new Dimension(background.getWidth(null),
                    background.getHeight(null)));
        } else
            ((ImageIcon) wrapper.getIcon()).setImage(background);
//        applicationScreen.remove(wrapper);
        applicationScreen.add(wrapper,constraints);
    }

    public void drawCharacterFromCoordinates (int character, int pose, int x, int y) {
        drawCharacterFromCoordinates(character,pose,x,y,false);
    }

    public void drawCharacterFromCoordinates (int character, int pose, int x, int y, boolean flipped) {
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
        constraints[0] = ScreenLayoutManager.CHARACTER;
        constraints[1] = x;
        constraints[2] = y;
        if (wrapper2.getIcon() == null) {
            wrapper2.setIcon(new ImageIcon(drawing));
            wrapper2.setSize(width,height);
        } else
            ((ImageIcon) wrapper2.getIcon()).setImage(drawing);
//        applicationScreen.remove(wrapper2);
        applicationScreen.add(wrapper2,constraints);
    }
}

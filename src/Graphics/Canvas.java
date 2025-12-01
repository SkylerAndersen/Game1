package Graphics;

import DataManagement.FileHandler;
import GameLogic.Character;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;


public class Canvas {
    public static final int SAND_WORLD = 0, MIDDLE_WORLD = 1, FOREST_WORLD = 2;
    private final FileHandler fileHandler = FileHandler.get();
    private final JPanel applicationScreen;
    private Image background;
    private Image character, walkPose, attackPose, altCharacter, altWalkPose, altAttackPose;
    private ScreenLayoutManager layoutManager;

    public Canvas () {
        this.applicationScreen = new JPanel();
        layoutManager = new ScreenLayoutManager(applicationScreen);
        applicationScreen.setLayout(layoutManager);
    }

    public void redraw () {
        applicationScreen.repaint();
        applicationScreen.revalidate();
    }

    public void loadWorld (int worldNum) {
        String name = worldNum == 0 ? "sand-world.png" : worldNum == 1 ? "middle-world.png" :
                "forest-world.png";
        background = new Image(fileHandler.readImage(name));
        drawWorldFromCoordinates(0,0);
    }

    public void loadCharacter (int characterNum) {
        character = new Image(fileHandler.readImage("base-pose.png"));
        walkPose = new Image(fileHandler.readImage("walk-pose.png"));
        attackPose = new Image(fileHandler.readImage("attack-pose.png"));
        altCharacter = new Image(fileHandler.readImage("base-pose.png"));
        altWalkPose = new Image(fileHandler.readImage("walk-pose.png"));
        altAttackPose = new Image(fileHandler.readImage("attack-pose.png"));
        drawCharacterFromCoordinates(characterNum, Character.BASE_POSE,0,0,false);
    }

    public void drawWorldFromCoordinates (int x, int y) {
        drawFromCoordinates(background,x,y,false,ScreenLayoutManager.BACKGROUND);
    }

    public void drawCharacterFromCoordinates (int character, int pose, int x, int y, boolean flipped) {
        // select image for state
        Image state = (character == Character.MAIN_CHARACTER) ? switch (pose) {
            case Character.ATTACK_POSE -> attackPose;
            case Character.WALK_POSE -> walkPose;
            default -> this.character;
        } : switch (pose) {
            case Character.ATTACK_POSE -> altAttackPose;
            case Character.WALK_POSE -> altWalkPose;
            default -> altCharacter;
        };

        // temporarily swap the image for the state with the Base image
        Image drawing = (character == Character.MAIN_CHARACTER) ? this.character : altCharacter;
        if (state != drawing) {
            BufferedImage drawingSource = drawing.getSource();
            drawing.changeSource(state.getSource());
            drawFromCoordinates(drawing,x,y,flipped,ScreenLayoutManager.CHARACTER);
            // silently change source back
            drawing.changeSourceWithoutWrapping(drawingSource);
        } else {
            // draw otherwise
            drawFromCoordinates(drawing,x,y,flipped,ScreenLayoutManager.CHARACTER);
        }
    }

    public void drawFromCoordinates (Image imToDraw, int x, int y, boolean flipped, int layer) {
        // gather info for constraints
        Integer[] constraints = new Integer[3];
        constraints[0] = layer;
        constraints[1] = x;
        constraints[2] = y;

        // wrap in swing object and draw to screen
        if (flipped)
            imToDraw.setReversed();
        else // ensure the wrapper wraps the current source
            imToDraw.wrap();
        applicationScreen.add(imToDraw.getWrapper(),constraints);
    }

    public void setZoomFactor (double zoomFactor) {
        layoutManager.setZoomFactor(zoomFactor);
    }

    public double getZoomFactor () {
        return layoutManager.getZoomFactor();
    }

    public Dimension getCharacterScreenSize () {
        return new Dimension(character.getSource().getWidth(),character.getSource().getHeight());
    }

    public Dimension getBackgroundScreenSize () {
        return new Dimension(background.getSource().getWidth(),background.getSource().getHeight());
    }

    public JPanel getApplicationScreen () {
        return applicationScreen;
    }
}

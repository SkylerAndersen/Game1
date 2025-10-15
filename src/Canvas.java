import DataManagement.FileHandler;

import javax.swing.*;

public class Canvas {
    public static final int SAND_WORLD = 0, MIDDLE_WORLD = 1, FOREST_WORLD = 2;
    private final FileHandler fileHandler = FileHandler.get();
    private final InputManager inputManager = InputManager.get();
    private JPanel applicationScreen;
    private long previousCycleTime;
    private Image background;
    private Image character, walkPose, attackPose, altCharacter, altWalkPose, altAttackPose;
    private JLabel wrapper, wrapper2;
    private int characterX, characterY;
    private final Game gameLogic = Game.get();
    private ScreenLayoutManager layoutManager;
    private int worldOffsetX, worldOffsetY;

    public Canvas (JPanel applicationScreen) {
        this.applicationScreen = applicationScreen;
        previousCycleTime = System.currentTimeMillis();
        wrapper = new JLabel();
        wrapper2 = new JLabel();
        layoutManager = new ScreenLayoutManager(applicationScreen);
        applicationScreen.setLayout(layoutManager);
        worldOffsetX = worldOffsetY = 0;
    }

    public void moveCharacter (int x, int y) {
        boolean validX = x >= 0 && x+character.getSource().getWidth() <
                background.getSource().getWidth();
        boolean validY = y >= 0 && y+character.getSource().getHeight() <
                background.getSource().getHeight();
        if (validX) {
            characterX = x;
        }
        if (validY) {
            characterY = y;
        }
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
        drawCharacterFromCoordinates(characterNum,Character.BASE_POSE,0,0,true);
    }

    public void refreshCycle () {
        Thread render = new Thread(new Runnable() {
            @Override
            public void run() {
                long time;
                int deltaTime;

                while (true) {
                    time = System.currentTimeMillis();
                    deltaTime = (int)(time - previousCycleTime);
                    previousCycleTime = time;

                    gameLogic.update(deltaTime,Canvas.this);

                    drawWorldFromCoordinates(worldOffsetX,worldOffsetY);
                    drawCharacterFromCoordinates(Character.MAIN_CHARACTER,Character.BASE_POSE,
                            characterX+worldOffsetX,characterY+worldOffsetY,true);
                    applicationScreen.repaint();
                    applicationScreen.revalidate();

                    // compute fps
                    double fps = (100000 / (deltaTime+0.000001))/100.0;
//                    if (fps < 500)
//                        System.out.printf("\rFPS is approximately %.2f", fps);
                }
            }
        });
        render.start();
    }

    public void drawWorldFromCoordinates (int x, int y) {
        drawFromCoordinates(background,x,y,false,ScreenLayoutManager.BACKGROUND);
    }

    public void drawCharacterFromCoordinates (int character, int pose, int x, int y) {
        drawCharacterFromCoordinates(character,pose,x,y,false);
    }

    public void drawCharacterFromCoordinates (int character, int pose, int x, int y, boolean flipped) {
        // select image
        Image drawing = (character == Character.MAIN_CHARACTER) ? switch (pose) {
            case Character.ATTACK_POSE -> attackPose;
            case Character.WALK_POSE -> walkPose;
            default -> this.character;
        } : switch (pose) {
            case Character.ATTACK_POSE -> altAttackPose;
            case Character.WALK_POSE -> altWalkPose;
            default -> altCharacter;
        };

        // draw
        drawFromCoordinates(drawing,x,y,flipped,ScreenLayoutManager.CHARACTER);
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
        applicationScreen.add(imToDraw.getWrapper(),constraints);
    }

    public void setZoomFactor (double zoomFactor) {
        layoutManager.setZoomFactor(zoomFactor);
    }

    public double getZoomFactor () {
        return layoutManager.getZoomFactor();
    }

    public void setWorldOffsetX (int offset) {
        worldOffsetX = offset;
    }

    public int getWorldOffsetX () {
        return worldOffsetX;
    }

    public void setWorldOffsetY (int offset) {
        worldOffsetY = offset;
    }

    public int getWorldOffsetY () {
        return worldOffsetY;
    }
}

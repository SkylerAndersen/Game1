package Graphics;

import DataManagement.FileHandler;
import GameLogic.Background;
import GameLogic.Character;

import java.awt.Dimension;


public class ImageUtilities {
    private static GraphicsObject background, character, walkPose, attackPose, altCharacter, altWalkPose,
            altAttackPose;
    private static final FileHandler fileHandler = FileHandler.get();
    private static Dimension characterScreenSize;
    private static Dimension backgroundScreenSize;
    public static void loadCharacters() {
        character = new GraphicsObject();
        walkPose = new GraphicsObject();
        attackPose = new GraphicsObject();
        altCharacter = new GraphicsObject();
        altWalkPose = new GraphicsObject();
        altAttackPose = new GraphicsObject();

        character.setImage(new Image(fileHandler.readImage("base-pose.png")));
        walkPose.setImage(new Image(fileHandler.readImage("walk-pose.png")));
        attackPose.setImage(new Image(fileHandler.readImage("attack-pose.png")));
        altCharacter.setImage(new Image(fileHandler.readImage("base-pose.png")));
        altWalkPose.setImage(new Image(fileHandler.readImage("walk-pose.png")));
        altAttackPose.setImage(new Image(fileHandler.readImage("attack-pose.png")));

        int characterWidth = character.getImage().getSource().getWidth();
        int characterHeight = character.getImage().getSource().getHeight();
        characterScreenSize = new Dimension(characterWidth,characterHeight);
    }

    public static void loadBackground (int worldNum) {
        String name = worldNum == 0 ? "sand-world.png" : worldNum == 1 ? "middle-world.png" :
                "forest-world.png";
        background = new GraphicsObject();
        background.setImage(new Image(fileHandler.readImage(name)));

        int backgroundWidth = background.getImage().getSource().getWidth();
        int backgroundHeight = background.getImage().getSource().getHeight();
        backgroundScreenSize = new Dimension(backgroundWidth,backgroundHeight);
    }

    public static GraphicsObject getBackground () {
        if (background == null)
            loadBackground(Background.SAND_WORLD);

        return background;
    }

    public static GraphicsObject getCharacter (int character, int pose) {
        if (ImageUtilities.character == null)
            loadCharacters();

        return (character == Character.MAIN_CHARACTER) ? switch (pose) {
            case Character.ATTACK_POSE -> attackPose;
            case Character.WALK_POSE -> walkPose;
            default -> ImageUtilities.character;
        } : switch (pose) {
            case Character.ATTACK_POSE -> altAttackPose;
            case Character.WALK_POSE -> altWalkPose;
            default -> altCharacter;
        };
    }

    public static Dimension getCharacterScreenSize () {
        return characterScreenSize;
    }

    public static Dimension getBackgroundScreenSize () {
        return backgroundScreenSize;
    }
}

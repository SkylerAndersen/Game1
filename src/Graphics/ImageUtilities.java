package Graphics;

import DataManagement.FileHandler;
import GameLogic.Background;
import GameLogic.Character;

import java.awt.Dimension;


public class ImageUtilities {
    private static Image background;
    private static Image character, walkPose, attackPose, altCharacter, altWalkPose, altAttackPose;
    private static final FileHandler fileHandler = FileHandler.get();
    private static Dimension characterScreenSize;
    private static Dimension backgroundScreenSize;
    public static void loadCharacters() {
        character = new Image(fileHandler.readImage("base-pose.png"));
        walkPose = new Image(fileHandler.readImage("walk-pose.png"));
        attackPose = new Image(fileHandler.readImage("attack-pose.png"));
        altCharacter = new Image(fileHandler.readImage("base-pose.png"));
        altWalkPose = new Image(fileHandler.readImage("walk-pose.png"));
        altAttackPose = new Image(fileHandler.readImage("attack-pose.png"));
        characterScreenSize = new Dimension(character.getSource().getWidth(),character.getSource().getHeight());
    }

    public static void loadBackground (int worldNum) {
        String name = worldNum == 0 ? "sand-world.png" : worldNum == 1 ? "middle-world.png" :
                "forest-world.png";
        background = new Image(fileHandler.readImage(name));
        backgroundScreenSize = new Dimension(background.getSource().getWidth(),background.getSource().getHeight());
    }

    public static Image getBackground () {
        if (background == null)
            loadBackground(Background.SAND_WORLD);

        return background;
    }

    public static Image getCharacter (int character, int pose) {
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

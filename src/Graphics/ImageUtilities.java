package Graphics;

import DataManagement.FileHandler;
import GameLogic.Background;
import GameLogic.Character;

import java.awt.Dimension;


public class ImageUtilities {
    private static final FileHandler fileHandler = FileHandler.get();
    private static int characterId, walkId, attackId, altCharacterId, altWalkId, altAttackId;
    private static GraphicsObject character, background;
    private static Dimension characterScreenSize;
    private static Dimension backgroundScreenSize;
    public static void loadCharacters() {
        character = new GraphicsObject();

        characterId = character.addState(new Image(fileHandler.readImage("base-pose.png")));
        walkId = character.addState(new Image(fileHandler.readImage("walk-pose.png")));
        attackId = character.addState(new Image(fileHandler.readImage("attack-pose.png")));
        altCharacterId = character.addState(new Image(fileHandler.readImage("base-pose.png")));
        altWalkId = character.addState(new Image(fileHandler.readImage("walk-pose.png")));
        altAttackId = character.addState(new Image(fileHandler.readImage("attack-pose.png")));

        character.setState(characterId);
        characterScreenSize = character.getSize();
    }

    public static void loadBackground (int worldNum) {
        String name = worldNum == 0 ? "sand-world.png" : worldNum == 1 ? "middle-world.png" :
                "forest-world.png";
        background = new GraphicsObject();
        int stateId = background.addState(new Image(fileHandler.readImage(name)));
        background.setState(stateId);

        backgroundScreenSize = background.getSize();
    }

    public static GraphicsObject getBackground () {
        if (background == null)
            loadBackground(Background.SAND_WORLD);

        return background;
    }

    public static GraphicsObject getCharacter (int character, int pose) {
        if (ImageUtilities.character == null)
            loadCharacters();

        int stateId = (character == Character.MAIN_CHARACTER) ? switch (pose) {
            case Character.ATTACK_POSE -> attackId;
            case Character.WALK_POSE -> walkId;
            default -> characterId;
        } : switch (pose) {
            case Character.ATTACK_POSE -> altAttackId;
            case Character.WALK_POSE -> altWalkId;
            default -> altCharacterId;
        };

        ImageUtilities.character.setState(stateId);
        return ImageUtilities.character;
    }

    public static Dimension getCharacterScreenSize () {
        return characterScreenSize;
    }

    public static Dimension getBackgroundScreenSize () {
        return backgroundScreenSize;
    }
}

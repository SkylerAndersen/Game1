package Graphics;

import DataManagement.FileHandler;
import GameLogic.Character;

import java.awt.Dimension;
import java.util.ArrayList;


public class ImageUtilities {
    private static final FileHandler fileHandler = FileHandler.get();
    private static int characterId, walkId, attackId, altCharacterId, altWalkId, altAttackId;
    private static int slimeIdle1, slimeIdle2;
    private static GraphicsObject character, background;
    private static ArrayList<GraphicsObject> slimes;
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

    public static void loadSlime (int id) {
        if (slimes == null)
            slimes = new ArrayList<>();

        Image state1 = new Image(fileHandler.readImage("slime-idle-1.png"));
        Image state2 = new Image(fileHandler.readImage("slime-idle-2.png"));
        slimeIdle1 = state1.getId();
        slimeIdle2 = state2.getId();
        for (int i = 0; i <= id; i++) {
            GraphicsObject currSlime = new GraphicsObject();
            slimes.add(i,currSlime);
            currSlime.addState(state1);
            currSlime.addState(state2);
            currSlime.setState(slimeIdle1);
        }
    }

    public static void loadBackground (int worldNum) {
        String name = worldNum == 0 ? "sand-world.png" : worldNum == 1 ? "middle-world.png" :
                worldNum == 2 ? "forest-world.png" : "map.png";
        background = new GraphicsObject();
        int stateId = background.addState(new Image(fileHandler.readImage(name)));
        background.setState(stateId);

        backgroundScreenSize = background.getSize();
    }

    public static GraphicsObject getBackground () {
        if (background == null)
            loadBackground(-1);

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

    public static GraphicsObject getSlime (int id) {
        if (slimes == null || slimes.size() <= id || slimes.get(id) == null)
            loadSlime(id);

        return slimes.get(id);
    }

    public static Dimension getCharacterScreenSize () {
        return characterScreenSize;
    }

    public static Dimension getBackgroundScreenSize () {
        return backgroundScreenSize;
    }
}

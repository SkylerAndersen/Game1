package GameLogic;

import ApplicationManagement.AsynchronousDispatch;
import ApplicationManagement.InputManager;
import Graphics.Canvas;
import Sound.AudioManager;

import java.awt.*;

public class Game {
    private static Game singleton;
    private static final AudioManager audioManager = AudioManager.get();
    private final InputManager inputManager = InputManager.get();
    private Character mainCharacter;
    private Canvas mainCanvas;
    private AsynchronousDispatch eventQueue;

    private Game () {
        mainCanvas = new Canvas();
        mainCharacter = new Character();
        eventQueue = new AsynchronousDispatch();
        setPoseBase();
        createDevCameraControls();
    }

    public void setCharacterPose (int characterPose) {
        if (characterPose < 3 || characterPose > 7)
            return;

        GameRecords.characterPose = characterPose;
    }

    public void moveCharacter (int x, int y) {
        if (mainCanvas == null)
            return;

        Dimension characterSize = mainCanvas.getCharacterScreenSize();
        Dimension backgroundSize = mainCanvas.getBackgroundScreenSize();
        boolean validX = x >= 0 && x+characterSize.width < backgroundSize.width;
        boolean validY = y >= 0 && y+characterSize.height < backgroundSize.height;

        if (validX)
            GameRecords.characterX = x;
        if (validY)
            GameRecords.characterY = y;
    }

    public void setFlippedCharacter (boolean flippedCharacter) {
        GameRecords.flippedCharacter = flippedCharacter;
    }

    public void setPoseWalk () {
        if (!mainCharacter.isWalking())
            return;

        long time = System.currentTimeMillis();
        if (time-mainCharacter.getLastWalked() < 500)
            return;
        mainCharacter.setLastWalked(time);

        mainCharacter.setPose(Character.WALK_POSE);
//        System.out.println("set to walk");
        eventQueue.schedule(250,this::setPoseBase);
        eventQueue.schedule(500, this::setPoseWalk);
    }

    public void setPoseBase () {
        long time = System.currentTimeMillis();
        if (time-mainCharacter.getLastStood() < 500)
            return;
        mainCharacter.setLastStood(time);
        mainCharacter.setPose(Character.BASE_POSE);
//        System.out.println("set to base");
    }

    public void update (int timeDeltaTime) {
        handleMovement(timeDeltaTime);
        handleCharacterState();

        mainCanvas.drawWorldFromCoordinates(GameRecords.worldOffsetX,GameRecords.worldOffsetY);
        mainCanvas.drawCharacterFromCoordinates(Character.MAIN_CHARACTER,GameRecords.characterPose,
                GameRecords.characterX+GameRecords.worldOffsetX,GameRecords.characterY+
                        GameRecords.worldOffsetY, GameRecords.flippedCharacter);
        mainCanvas.redraw();
    }

    public void handleCharacterState () {
        setCharacterPose(mainCharacter.getPose());
        setFlippedCharacter(mainCharacter.getFlipped());
    }

    public void handleMovement (int timeDeltaTime) {
        boolean pressedW = inputManager.queryKeyPress('w');
        boolean pressedA = inputManager.queryKeyPress('a');
        boolean pressedS = inputManager.queryKeyPress('s');
        boolean pressedD = inputManager.queryKeyPress('d');
        boolean anyPressed = pressedW || pressedA || pressedS || pressedD;

        if (pressedW)
            mainCharacter.move(0,-1,timeDeltaTime);
        if (pressedA)
            mainCharacter.move(-1,0,timeDeltaTime);
        if (pressedS)
            mainCharacter.move(0,1,timeDeltaTime);
        if (pressedD)
            mainCharacter.move(1,0,timeDeltaTime);


        // animation state
        if (!anyPressed) {
            mainCharacter.setIsWalking(false);
            return;
        }

        // movement
        Point pos = mainCharacter.getPos();
        moveCharacter(pos.x,pos.y);

        // animation state
        if (!mainCharacter.isWalking()) {
            mainCharacter.setIsWalking(true);
            setPoseWalk();
        }

        // flipped state
        if (pressedD) {
            mainCharacter.setFlipped(false);
        } else if (pressedA) {
            mainCharacter.setFlipped(true);
        }
    }

    public void createDevCameraControls() {
        inputManager.addCallback('+', () -> {mainCanvas.setZoomFactor(mainCanvas.getZoomFactor()+0.1);});
        inputManager.addCallback('-', () -> {mainCanvas.setZoomFactor(mainCanvas.getZoomFactor()-0.1);});
        inputManager.addCallback('h', () -> {GameRecords.worldOffsetX += 10;});
        inputManager.addCallback('j', () -> {GameRecords.worldOffsetY -= 10;});
        inputManager.addCallback('k', () -> {GameRecords.worldOffsetY += 10;});
        inputManager.addCallback('l', () -> {GameRecords.worldOffsetX -= 10;});
    }

    public static Game get () {
        if (singleton == null)
            singleton = new Game();

        return singleton;
    }

    public Canvas getMainCanvas () {
        return mainCanvas;
    }
}

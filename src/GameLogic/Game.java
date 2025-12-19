package GameLogic;

import ApplicationManagement.AsynchronousDispatch;
import ApplicationManagement.InputManager;
import Graphics.Canvas;
import Graphics.ScreenLayoutManager;
import Graphics.ImageUtilities;
import Sound.AudioManager;
import Graphics.GraphicsObject;

import java.awt.*;

public class Game {
    private static Game singleton;
    private static final AudioManager audioManager = AudioManager.get();
    private final InputManager inputManager = InputManager.get();
    private Character mainCharacter;
    private Background background;
    private Canvas mainCanvas;
    private AsynchronousDispatch eventQueue;

    private Game () {
        mainCanvas = new Canvas(new Dimension(640,360));
        mainCharacter = new Character();
        background = new Background();
        mainCharacter.setPos(new Point(320,220));
        eventQueue = new AsynchronousDispatch();
        setPoseBase();
        createDevCameraControls();
        inputManager.addCallback('a',() -> {mainCharacter.setFlipped(true);});
        inputManager.addCallback('d',() -> {mainCharacter.setFlipped(false);});
        audioManager.play();
    }

    public void update (int timeDeltaTime) {
        handleMovement(timeDeltaTime);
        handleGraphics();
    }

    private void handleGraphics () {
        GraphicsObject backgroundImage = ImageUtilities.getBackground();
        mainCanvas.draw(backgroundImage,background.getX(),background.getY(),background.getLayer());

        GraphicsObject character = ImageUtilities.getCharacter(Character.MAIN_CHARACTER, mainCharacter.getPose());
        character.setFlipped(mainCharacter.getFlipped());
        mainCanvas.draw(character,mainCharacter.getPos().x+background.getX(),
                mainCharacter.getPos().y+background.getY(),
                ScreenLayoutManager.CHARACTER);

        mainCanvas.refresh();
    }

    private void handleMovement (int timeDeltaTime) {
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

        // animation state
        if (!mainCharacter.isWalking()) {
            mainCharacter.setIsWalking(true);
            setPoseWalk();
        }
    }

    private void createDevCameraControls() {
        inputManager.addCallback('+', () -> {mainCanvas.setZoomFactor(mainCanvas.getZoomFactor()+0.1);});
        inputManager.addCallback('-', () -> {mainCanvas.setZoomFactor(mainCanvas.getZoomFactor()-0.1);});
        inputManager.addCallback('h', () -> {background.setX(background.getX()+10);});
        inputManager.addCallback('j', () -> {background.setY(background.getY()-10);});
        inputManager.addCallback('k', () -> {background.setY(background.getY()+10);});
        inputManager.addCallback('l', () -> {background.setX(background.getX()-10);});
    }

    private void setPoseWalk () {
        if (!mainCharacter.isWalking())
            return;

        long time = System.currentTimeMillis();
        if (time-mainCharacter.getLastWalked() < 500)
            return;
        mainCharacter.setLastWalked(time);

        mainCharacter.setPose(Character.WALK_POSE);
        eventQueue.schedule(250,this::setPoseBase);
        eventQueue.schedule(500, this::setPoseWalk);
    }

    private void setPoseBase () {
        long time = System.currentTimeMillis();
        if (time-mainCharacter.getLastStood() < 500)
            return;
        mainCharacter.setLastStood(time);
        mainCharacter.setPose(Character.BASE_POSE);
    }

    public Canvas getMainCanvas () {
        return mainCanvas;
    }

    public static Game get () {
        if (singleton == null)
            singleton = new Game();

        return singleton;
    }
}

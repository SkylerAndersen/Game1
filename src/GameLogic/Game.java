package GameLogic;

import ApplicationManagement.AsynchronousDispatch;
import ApplicationManagement.InputManager;
import DataManagement.Pair;
import Graphics.Canvas;
import Graphics.ScreenLayoutManager;
import Graphics.ImageUtilities;
import Sound.AudioManager;
import Graphics.Image;

import java.awt.*;

public class Game {
    private static Game singleton;
    private static final AudioManager audioManager = AudioManager.get();
    private final InputManager inputManager = InputManager.get();
    private Character mainCharacter;
    private Canvas mainCanvas;
    private AsynchronousDispatch eventQueue;

    private Game () {
        mainCanvas = new Canvas(new Dimension(640,360));
        mainCharacter = new Character();
        mainCharacter.setPos(new Point(320,220));
        eventQueue = new AsynchronousDispatch();
        setPoseBase();
        createDevCameraControls();
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
        handleGraphics();
    }

    public void handleGraphics () {
        // draw background
        Background bg = ImageUtilities.getBackground();
        mainCanvas.draw(bg.getImage(),bg.getX(),bg.getY(),false,bg.getLayer());

        // draw character
        Pair<Image,Runnable> imageRunnablePair = mainCharacter.getImageToDrawAndCleanup(Character.MAIN_CHARACTER,
                mainCharacter.getPose());
        mainCanvas.setDrawCleanup(imageRunnablePair.getVal2());
        mainCanvas.draw(imageRunnablePair.getVal1(),mainCharacter.getPos().x+bg.getX(),
                mainCharacter.getPos().y+bg.getY(), mainCharacter.getFlipped(), ScreenLayoutManager.CHARACTER);
        mainCanvas.runDrawCleanup();

        // refresh canvas
        mainCanvas.redraw();
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
        inputManager.addCallback('h', () -> {Background bg = ImageUtilities.getBackground();
            bg.setX(bg.getX()+10);});
        inputManager.addCallback('j', () -> {Background bg = ImageUtilities.getBackground();
            bg.setY(bg.getY()-10);});
        inputManager.addCallback('k', () -> {Background bg = ImageUtilities.getBackground();
            bg.setY(bg.getY()+10);});
        inputManager.addCallback('l', () -> {Background bg = ImageUtilities.getBackground();
            bg.setX(bg.getX()-10);});
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

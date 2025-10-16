import java.awt.Point;

public class Game {
    private static Game singleton;
    private InputManager inputManager;
    private Character mainCharacter;
    private AsynchronousDispatch eventQueue;
    private boolean holdingPlus, holdingMinus, holdingH, holdingJ, holdingK, holdingL;

    private Game () {
        this.inputManager = InputManager.get();
        holdingPlus = holdingMinus = holdingH = holdingJ = holdingK = holdingL = false;
        mainCharacter = new Character();
        eventQueue = new AsynchronousDispatch();
        setPoseBase();
    }

    public void setPoseWalk () {
        if (!mainCharacter.isWalking())
            return;

        long time = System.currentTimeMillis();
        if (time-mainCharacter.getLastWalked() < 500)
            return;
        mainCharacter.setLastWalked(time);

        mainCharacter.setPose(Character.WALK_POSE);
        System.out.println("set to walk");
        eventQueue.schedule(250,this::setPoseBase);
        eventQueue.schedule(500, this::setPoseWalk);
    }

    public void setPoseBase () {
        long time = System.currentTimeMillis();
        if (time-mainCharacter.getLastStood() < 500)
            return;
        mainCharacter.setLastStood(time);
        mainCharacter.setPose(Character.BASE_POSE);
        System.out.println("set to base");
    }

    public void update (int timeDeltaTime, Canvas screen) {
        handleMovement(timeDeltaTime,screen);
        handleCharacterState(screen);
        processDevCameraMovements(screen);
    }

    public void handleCharacterState (Canvas screen) {
        screen.setCharacterPose(mainCharacter.getPose());
    }

    public void handleMovement (int timeDeltaTime, Canvas screen) {
        boolean pressedW = inputManager.queryKeyPress('w');
        boolean pressedA = inputManager.queryKeyPress('a');
        boolean pressedS = inputManager.queryKeyPress('s');
        boolean pressedD = inputManager.queryKeyPress('d');
        boolean anyPressed = pressedW || pressedA || pressedS || pressedD;
        if (pressedW) {
            mainCharacter.move(0,-1,timeDeltaTime);
        }
        if (pressedA) {
            mainCharacter.move(-1,0,timeDeltaTime);
        }
        if (pressedS) {
            mainCharacter.move(0,1,timeDeltaTime);
        }
        if (pressedD) {
            mainCharacter.move(1,0,timeDeltaTime);
        }

        if (!anyPressed) {
            mainCharacter.setIsWalking(false);
            return;
        }
        Point pos = mainCharacter.getPos();
        screen.moveCharacter(pos.x,pos.y);
        if (!mainCharacter.isWalking()) {
            mainCharacter.setIsWalking(true);
            setPoseWalk();
        }
    }

    public void processDevCameraMovements (Canvas screen) {
        if (inputManager.queryKeyPress('+') && !holdingPlus) {
            screen.setZoomFactor(screen.getZoomFactor()+0.1);
        }
        if (inputManager.queryKeyPress('-') && !holdingMinus) {
            screen.setZoomFactor(screen.getZoomFactor()-0.1);
        }
        if (inputManager.queryKeyPress('h') && !holdingH) {
            screen.setWorldOffsetX(screen.getWorldOffsetX()+10);
        }
        if (inputManager.queryKeyPress('j') && !holdingJ) {
            screen.setWorldOffsetY(screen.getWorldOffsetY()-10);
        }
        if (inputManager.queryKeyPress('k') && !holdingK) {
            screen.setWorldOffsetY(screen.getWorldOffsetY()+10);
        }
        if (inputManager.queryKeyPress('l') && !holdingL) {
            screen.setWorldOffsetX(screen.getWorldOffsetX()-10);
        }
        if (inputManager.queryKeyPress('+') != holdingPlus) {
            holdingPlus = !holdingPlus;
        }
        if (inputManager.queryKeyPress('-') != holdingMinus) {
            holdingMinus = !holdingMinus;
        }
        if (inputManager.queryKeyPress('h') != holdingH) {
            holdingH = !holdingH;
        }
        if (inputManager.queryKeyPress('j') != holdingJ) {
            holdingJ = !holdingJ;
        }
        if (inputManager.queryKeyPress('k') != holdingK) {
            holdingK = !holdingK;
        }
        if (inputManager.queryKeyPress('l') != holdingL) {
            holdingL = !holdingL;
        }
    }

    public static Game get () {
        if (singleton == null)
            singleton = new Game();

        return singleton;
    }
}

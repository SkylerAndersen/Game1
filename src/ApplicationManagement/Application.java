package ApplicationManagement;

import DataManagement.FileHandler;
import GameLogic.Character;
import Graphics.Canvas;
import GameLogic.Game;
import javax.swing.*;
import java.awt.*;

public class Application {
    private static Application singleton;
    private final FileHandler fileHandler = FileHandler.get();
    private final InputManager inputManager = InputManager.get();
    private final Game game = Game.get();

    public void start () {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(1000,800));
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        frame.setVisible(true);

        Canvas mainCanvas = game.getMainCanvas();
        frame.add(mainCanvas.getApplicationScreen(),BorderLayout.CENTER);
        mainCanvas.loadWorld(Canvas.SAND_WORLD);
        mainCanvas.loadCharacter(Character.MAIN_CHARACTER);
        refreshCycle();

        inputManager.consider(frame);
    }

    public void refreshCycle () {
        Thread loop = new Thread(this::gameLoop,"game-loop");
        loop.start();
    }

    public void gameLoop () {
        long time;
        int deltaTime;
        long previousCycleTime = 0;

        while (true) {
            time = System.currentTimeMillis();
            deltaTime = (int)(time - previousCycleTime);
            previousCycleTime = time;

            game.update(deltaTime);

            // compute fps
            double fps = (100000 / (deltaTime+0.000001))/100.0;
//                    if (fps < 500)
//                        System.out.printf("\rFPS is approximately %.2f", fps);
        }
    }

    public static Application get () {
        if (singleton == null)
            singleton = new Application();

        return singleton;
    }

    public FileHandler getFileHandler () {
        return fileHandler;
    }
}

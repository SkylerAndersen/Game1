import javax.swing.*;
import java.awt.*;

public class WindowManager {
    private static WindowManager singleton;
    private JFrame frame;
    private InputManager inputManager;
    private WindowManager () {
        inputManager = InputManager.get();
    }

    public void openWindow () {
        frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(1000,800));
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        frame.setVisible(true);

        JPanel animationView = new JPanel();
        frame.add(animationView,BorderLayout.CENTER);
        Canvas canvas = new Canvas(animationView);
        canvas.loadWorld(Canvas.SAND_WORLD);
        canvas.loadCharacter(Canvas.MAIN_CHARACTER);
        canvas.refreshCycle();

        inputManager.consider(frame);
    }

    public static WindowManager get () {
        if (singleton == null)
            singleton = new WindowManager();
        return singleton;
    }
}

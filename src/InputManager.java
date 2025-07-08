import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;

public class InputManager {
    private static InputManager singleton;
    private KeyListener keyListener;
    private Set<java.lang.Character> keysPressed;
    private InputManager () {
        keysPressed = new HashSet<>();
        keyListener = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                keysPressed.add(e.getKeyChar());
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (keysPressed.contains(e.getKeyChar()))
                    keysPressed.remove(e.getKeyChar());
            }
        };
    }

    public static InputManager get () {
        if (singleton == null)
            singleton = new InputManager();
        return singleton;
    }

    public void consider (JFrame application) {
        application.addKeyListener(keyListener);
    }

    public boolean queryKeyPress (char key) {
        return keysPressed.contains(key);
    }
}

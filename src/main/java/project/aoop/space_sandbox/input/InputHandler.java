package project.aoop.space_sandbox.input;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class InputHandler {
    private final Set<KeyCode> held = new HashSet<>();

    public void attach(Scene scene) {
        scene.setOnKeyPressed(e  -> held.add(e.getCode()));
        scene.setOnKeyReleased(e -> held.remove(e.getCode()));
    }

    private boolean is(KeyCode k) { return held.contains(k); }

    public boolean thrustForward() { return is(KeyCode.W) || is(KeyCode.UP); }
    public boolean rotateLeft()    { return is(KeyCode.A) || is(KeyCode.LEFT); }
    public boolean rotateRight()   { return is(KeyCode.D) || is(KeyCode.RIGHT); }
    public boolean brake()         { return is(KeyCode.SPACE); }
    public boolean shoot()         { return is(KeyCode.SPACE); }
    public boolean zoomIn()        { return is(KeyCode.EQUALS); }
    public boolean zoomOut()       { return is(KeyCode.MINUS); }
}
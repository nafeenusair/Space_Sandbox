package project.aoop.space_sandbox.render;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import org.springframework.stereotype.Component;
import project.aoop.space_sandbox.camera.GameCamera;
import project.aoop.space_sandbox.entity.Ship;

@Component
public class GameRenderer {

    private Canvas canvas;
    private GraphicsContext gc;

    private final GameCamera camera;
    private final StarfieldRenderer starfieldRenderer;
    private final ShipRenderer shipRenderer;

    public GameRenderer(GameCamera camera, StarfieldRenderer starfieldRenderer,
                        ShipRenderer shipRenderer) {
        this.camera = camera;
        this.starfieldRenderer = starfieldRenderer;
        this.shipRenderer = shipRenderer;
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
    }

    public void render(Ship ship, boolean thrusting) {
        if (gc == null) return;
        double w = canvas.getWidth(), h = canvas.getHeight();

        starfieldRenderer.render(gc, camera, w, h);
        shipRenderer.render(gc, camera, ship, thrusting);
    }
}
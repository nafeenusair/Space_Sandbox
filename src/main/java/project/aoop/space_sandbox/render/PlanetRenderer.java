package project.aoop.space_sandbox.render;

import jakarta.annotation.PostConstruct;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.springframework.stereotype.Component;
import project.aoop.space_sandbox.camera.GameCamera;
import project.aoop.space_sandbox.entity.Planet;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PlanetRenderer {

    // One texture per planet name — loaded once at startup
    private final Map<String, Image> textures = new HashMap<>();
    private final GameCamera camera;

    public PlanetRenderer(GameCamera camera) {
        this.camera = camera;
    }

    // Draw all orbit rings
    public void drawOrbitRings(GraphicsContext gc, List<Planet> planets) {
        gc.setStroke(Color.color(1, 1, 1, 0.06));
        gc.setLineWidth(1);
        for (Planet planet : planets) {
            double a  = planet.getSemiMajorAxis() * camera.getZoom();
            double b  = planet.getSemiMinorAxis() * camera.getZoom();
            double cx = camera.toScreenX(0);
            double cy = camera.toScreenY(0);
            gc.strokeOval(cx - a, cy - b, a * 2, b * 2);
        }
    }

}
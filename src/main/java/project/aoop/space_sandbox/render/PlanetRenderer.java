package project.aoop.space_sandbox.render;

import jakarta.annotation.PostConstruct;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.springframework.stereotype.Component;
import project.aoop.space_sandbox.camera.GameCamera;
import project.aoop.space_sandbox.entity.Planet;
import project.aoop.space_sandbox.entity.Star;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PlanetRenderer {
    private final Map<String, Image> textures = new HashMap<>();

    @PostConstruct
    public void init() {
        String[] names = {
                "Sun", "Mercury", "Venus", "Earth",
                "Mars", "Jupiter", "Saturn", "Uranus", "Neptune"
        };

        for (String name : names) {
            String path = "/sprites/planet_stars/" + name.toLowerCase() + ".jpg";
            InputStream is = getClass().getResourceAsStream(path);
            if (is != null) {
                textures.put(name, new Image(is));
            }
        }
    }

    public void render(GraphicsContext gc, GameCamera camera,
                       Star star, List<Planet> planets) {
        drawOrbits(gc, camera, planets);
        drawSun(gc, camera, star);
        drawPlanets(gc, camera, planets);
    }

    private void drawOrbits(GraphicsContext gc, GameCamera camera, List<Planet> planets) {
        gc.setStroke(Color.color(1, 1, 1, 0.06));
        gc.setLineWidth(1);

        for (Planet p : planets) {
            double a  = p.getSemiMajorAxis() * camera.getZoom();
            double b  = p.getSemiMinorAxis() * camera.getZoom();
            double cx = camera.toScreenX(0);
            double cy = camera.toScreenY(0);
            gc.strokeOval(cx - a, cy - b, a * 2, b * 2);
        }
    }

    private void drawSun(GraphicsContext gc, GameCamera camera, Star star) {
        double sx = camera.toScreenX(star.getX());
        double sy = camera.toScreenY(star.getY());
        double r  = star.getRadius() * camera.getZoom();

        javafx.scene.paint.RadialGradient glow = new javafx.scene.paint.RadialGradient(
                0, 0, sx, sy, r * 2.5, false,
                javafx.scene.paint.CycleMethod.NO_CYCLE,
                new javafx.scene.paint.Stop(0, Color.color(1, 0.9, 0.3, 0.4)),
                new javafx.scene.paint.Stop(1, Color.TRANSPARENT)
        );
        gc.setFill(glow);
        gc.fillOval(sx - r*2.5, sy - r*2.5, r*5, r*5);

        Image sunTex = textures.get("Sun");
        if (sunTex != null) {
            gc.save();
            gc.beginPath();
            gc.arc(sx, sy, r, r, 0, 360);
            gc.clip();
            gc.drawImage(sunTex, sx - r, sy - r, r * 2, r * 2);
            gc.restore();
        } else {
            gc.setFill(Color.web("#ffe066"));
            gc.fillOval(sx - r, sy - r, r * 2, r * 2);
        }
    }

    private void drawPlanets(GraphicsContext gc, GameCamera camera, List<Planet> planets) {
        for (Planet p : planets) {
            double sx = camera.toScreenX(p.getX());
            double sy = camera.toScreenY(p.getY());

            if (sx < -200 || sx > 2000 || sy < -200 || sy > 1000) continue;

            double r = Math.max(p.getRadius() * camera.getZoom(), 3);

            Image tex = textures.get(p.getName());
            if (tex != null) {
                gc.save();
                gc.beginPath();
                gc.arc(sx, sy, r, r, 0, 360);
                gc.clip();
                gc.drawImage(tex, sx - r, sy - r, r * 2, r * 2);
                gc.restore();
            } else {
                gc.setFill(p.getColor());
                gc.fillOval(sx - r, sy - r, r * 2, r * 2);
            }

            gc.setStroke(Color.color(1, 1, 1, 0.15));
            gc.setLineWidth(1);
            gc.strokeOval(sx - r, sy - r, r * 2, r * 2);

            if (camera.getZoom() > 8) {
                gc.setFill(Color.color(1, 1, 1, 0.75));
                gc.setFont(javafx.scene.text.Font.font(11));
                gc.fillText(p.getName(), sx + r + 4, sy + 4);
            }
        }
    }
}
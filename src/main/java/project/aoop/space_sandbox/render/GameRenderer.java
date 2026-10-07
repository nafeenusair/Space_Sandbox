package project.aoop.space_sandbox.render;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.springframework.stereotype.Component;
import project.aoop.space_sandbox.camera.GameCamera;
import project.aoop.space_sandbox.entity.CelestialBody;
import project.aoop.space_sandbox.entity.Planet;
import project.aoop.space_sandbox.entity.Ship;
import project.aoop.space_sandbox.entity.Star;
import project.aoop.space_sandbox.service.LevelService;
import project.aoop.space_sandbox.service.NavigationService;

import java.util.List;

@Component
public class GameRenderer {

    private Canvas canvas;
    private GraphicsContext gc;

    private final GameCamera               camera;
    private final StarfieldRenderer        starfieldRenderer;
    private final ShipRenderer             shipRenderer;
    private final PlanetRenderer           planetRenderer;
    private final MinimapRenderer          minimapRenderer;
    private final HUDRenderer              hudRenderer;
    private final SolarSystemMap           solarSystemMap;
    private final ProceduralPlanetRenderer proceduralRenderer;
    private final NavigationService        navigationService;
    private final Star                     star;
    private final LevelService             levelService;
    private final List<Planet>             planets;

    public GameRenderer(GameCamera camera, StarfieldRenderer starfieldRenderer,
                        ShipRenderer shipRenderer, PlanetRenderer planetRenderer,
                        MinimapRenderer minimapRenderer, HUDRenderer hudRenderer,
                        SolarSystemMap solarSystemMap,
                        ProceduralPlanetRenderer proceduralRenderer,
                        NavigationService navigationService,
                        LevelService levelService,
                        Star star, List<Planet> planets) {
        this.camera             = camera;
        this.starfieldRenderer  = starfieldRenderer;
        this.shipRenderer       = shipRenderer;
        this.planetRenderer     = planetRenderer;
        this.minimapRenderer    = minimapRenderer;
        this.hudRenderer        = hudRenderer;
        this.solarSystemMap     = solarSystemMap;
        this.proceduralRenderer = proceduralRenderer;
        this.navigationService  = navigationService;
        this.star               = star;
        this.planets            = planets;
        this.levelService       = levelService;
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
        this.gc     = canvas.getGraphicsContext2D();
    }

    public void render(Ship ship, boolean thrusting) {
        if (gc == null) return;
        double w = canvas.getWidth(), h = canvas.getHeight();

        // World layers
        starfieldRenderer.render(gc, camera, w, h);
        planetRenderer.drawOrbitRings(gc, planets);
        drawGravityZone(star, Color.web("#ff6600"), ship);
        planets.forEach(p -> drawGravityZone(p, Color.web("#aaaaaa"), ship));
        proceduralRenderer.drawSun(gc, star);
        planets.forEach(p -> proceduralRenderer.drawPlanet(gc, p));
        shipRenderer.render(gc, camera, ship, thrusting);

        // HUD layers
        minimapRenderer.render(gc, w, h, ship, star, planets);
        hudRenderer.render(gc, ship, planets);

        // Map overlay — drawn last, on top of everything
        solarSystemMap.render(gc, w, h, ship, star, planets, navigationService);
    }

    // Called by JavaFXLauncher on mouse click
    public void handleClick(double x, double y) {
        if (canvas == null) return;
        double w = canvas.getWidth(), h = canvas.getHeight();

        if (solarSystemMap.isOpen()) {
            // Map is open — handle planet selection
            solarSystemMap.handleClick(x, y, w, h, planets, navigationService);
        } else if (minimapRenderer.isClickOnMinimap(x, y, w, h)) {
            // Click on minimap — open the full map
            solarSystemMap.open();
        }
    }

    private void drawGravityZone(CelestialBody body, Color baseColor, Ship ship) {
        // Don't draw zone for mastered or locked planets
        if (!(body instanceof Star)) {
            if (levelService.isMastered(body.getName())) return;
            if (levelService.isLocked(body.getName()))   return;
        }

        double sx = camera.toScreenX(body.getX());
        double sy = camera.toScreenY(body.getY());
        double r  = body.getGravityRadius() * camera.getZoom();

        double dx      = ship.getX() - body.getX();
        double dy      = ship.getY() - body.getY();
        boolean inside = Math.sqrt(dx*dx + dy*dy) < body.getGravityRadius();

        Color ring = inside
                ? Color.color(1, 0.2, 0.2, 0.6)
                : Color.color(baseColor.getRed(), baseColor.getGreen(),
                baseColor.getBlue(), 0.10);

        gc.setStroke(ring);
        gc.setLineWidth(inside ? 1.5 : 1.0);
        gc.setLineDashes(inside ? 0 : 6);
        gc.strokeOval(sx - r, sy - r, r*2, r*2);
        gc.setLineDashes(0);
    }
}
package project.aoop.space_sandbox.engine;

import javafx.animation.AnimationTimer;
import org.springframework.stereotype.Component;
import project.aoop.space_sandbox.camera.GameCamera;
import project.aoop.space_sandbox.entity.*;
import project.aoop.space_sandbox.input.InputHandler;
import project.aoop.space_sandbox.physics.OrbitalMechanics;
import project.aoop.space_sandbox.render.GameRenderer;
import project.aoop.space_sandbox.render.SolarSystemMap;
import project.aoop.space_sandbox.service.LevelService;
import project.aoop.space_sandbox.service.ShipService;

import java.util.ArrayList;
import java.util.List;

@Component
public class GameLoop extends AnimationTimer {
    private boolean escHeld = false;
    private long lastTime = 0;

    private final GameState        gameState;
    private final ShipService      shipService;
    private final InputHandler     inputHandler;
    private final GameRenderer     renderer;
    private final GameCamera       camera;
    private final Star             star;
    private final List<Planet>     planets;
    private final OrbitalMechanics orbitalMechanics;
    private final LevelService     levelService;
    private final SolarSystemMap solarSystemMap;

    // Add to constructor params + assignment
    private boolean levelUpHeld = false;

    public GameLoop(GameState gameState, ShipService shipService,
                    InputHandler inputHandler, GameRenderer renderer,
                    GameCamera camera, Star star, List<Planet> planets,
                    OrbitalMechanics orbitalMechanics, LevelService levelService, SolarSystemMap solarSystemMap) {
        this.gameState        = gameState;
        this.shipService      = shipService;
        this.inputHandler     = inputHandler;
        this.renderer         = renderer;
        this.camera           = camera;
        this.star             = star;
        this.planets          = planets;
        this.orbitalMechanics = orbitalMechanics;
        this.levelService     = levelService;
        this.solarSystemMap = solarSystemMap;
    }

    @Override
    public void handle(long now) {
        if (lastTime == 0) { lastTime = now; return; }
        double dt = Math.min((now - lastTime) / 1_000_000_000.0, 0.05);
        lastTime = now;

        if (!gameState.isRunning()) return;

        // 1. Input
        if (inputHandler.thrustForward()) shipService.thrustForward(dt);
        if (inputHandler.rotateLeft())    shipService.rotateLeft(dt);
        if (inputHandler.rotateRight())   shipService.rotateRight(dt);
        if (inputHandler.brake())         shipService.brake(dt);
        if (inputHandler.levelUp()) {
            if (!levelUpHeld) {
                levelService.levelUp();
                levelUpHeld = true;
            }
        } else {
            levelUpHeld = false;
        }
        if (inputHandler.closeMap()) {
            if (!escHeld) { solarSystemMap.close(); escHeld = true; }
        } else { escHeld = false; }

        // 2. Move planets
        for (Planet p : planets) {
            if (p instanceof Moon m) {
                m.updateAroundEarth(dt);  // Moon orbits Earth
            } else {
                orbitalMechanics.update(p, dt); // everything else orbits Sun
            }
        }

        // 3. Physics — gravity from Sun and accessible planets only
        List<CelestialBody> bodies = new ArrayList<>();
        bodies.add(star);
        bodies.addAll(planets);
        shipService.update(bodies, dt);

        Ship ship = shipService.getShip();

        // 4. Sun: only danger in the game — crash if touching surface
        double sunDist = dist(ship, star.getX(), star.getY());
        if (sunDist <= star.getRadius()) {
            ship.crash();
            gameState.setState(GameState.State.PAUSED);
            System.out.println("☀ DESTROYED — vaporized by the Sun");
            return;
        }

        // 6. Camera + render
        camera.follow(ship.getX(), ship.getY(), dt);
        renderer.render(ship, inputHandler.thrustForward());
    }

    private double dist(Ship ship, double tx, double ty) {
        double dx = ship.getX() - tx;
        double dy = ship.getY() - ty;
        return Math.sqrt(dx * dx + dy * dy);
    }
}
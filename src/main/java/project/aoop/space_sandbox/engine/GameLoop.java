package project.aoop.space_sandbox.engine;

import javafx.animation.AnimationTimer;
import org.springframework.stereotype.Component;
import project.aoop.space_sandbox.camera.GameCamera;
import project.aoop.space_sandbox.entity.Ship;
import project.aoop.space_sandbox.input.InputHandler;
import project.aoop.space_sandbox.render.GameRenderer;
import project.aoop.space_sandbox.service.ShipService;

@Component
public class GameLoop extends AnimationTimer {

    private long lastTime = 0;

    private final GameState         gameState;
    private final ShipService       shipService;
    private final InputHandler      inputHandler;
    private final GameRenderer      renderer;
    private final GameCamera        camera;

    public GameLoop(GameState gameState, ShipService shipService,
                    InputHandler inputHandler,
                    GameRenderer renderer, GameCamera camera) {
        this.gameState    = gameState;
        this.shipService  = shipService;
        this.inputHandler = inputHandler;
        this.renderer     = renderer;
        this.camera       = camera;
    }

    @Override
    public void handle(long now) {
        if (lastTime == 0) { lastTime = now; return; }
        double dt = Math.min((now - lastTime) / 1_000_000_000.0, 0.05);
        lastTime = now;

        if (!gameState.isRunning()) return;

        boolean thrusting = inputHandler.thrustForward();
        if (thrusting) shipService.thrustForward(dt);
        if (inputHandler.brake())         shipService.brake(dt);
        if (inputHandler.rotateLeft())    shipService.rotateLeft(dt);
        if (inputHandler.rotateRight())   shipService.rotateRight(dt);

        shipService.update(dt);
        Ship ship = shipService.getShip();
        camera.follow(ship.getX(), ship.getY(), dt);
        renderer.render(ship, thrusting);
    }
}
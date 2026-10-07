package project.aoop.space_sandbox.service;

import org.springframework.stereotype.Service;
import project.aoop.space_sandbox.entity.CelestialBody;
import project.aoop.space_sandbox.entity.Ship;
import project.aoop.space_sandbox.physics.GravitySystem;

import java.util.List;

@Service
public class ShipService {
    private final Ship ship;
    private final GravitySystem gravitySystem;

    private static final double CRASH_SPEED = 30.0;

    public ShipService(Ship ship, GravitySystem gravitySystem) {
        this.ship = ship;
        this.gravitySystem = gravitySystem;
    }

    public void thrustForward(double deltaTime) { ship.thrust(deltaTime); }
    public void brake(double deltaTime) { ship.brake(deltaTime); }
    public void rotateLeft(double deltaTime) {
        ship.rotateLeft(deltaTime);
    }
    public void rotateRight(double deltaTime) {
        ship.rotateRight(deltaTime);
    }

    public void update(double deltaTime) {
        ship.update(deltaTime);
    }

    public void update(List<CelestialBody> bodies, double deltaTime) {
        gravitySystem.applyGravity(ship, bodies, deltaTime);
        ship.update(deltaTime);
    }

    public boolean isCrashSpeed() { return ship.getSpeed() > CRASH_SPEED; }
    public void crash() { ship.setHealth(0); }
    public Ship getShip() { return ship; }
}

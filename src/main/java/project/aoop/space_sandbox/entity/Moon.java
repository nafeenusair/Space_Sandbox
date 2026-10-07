package project.aoop.space_sandbox.entity;

import javafx.scene.paint.Color;
import org.springframework.stereotype.Component;
import project.aoop.space_sandbox.config.PhysicsConfig;

@Component
public class Moon extends Planet {

    private final Earth earth;

    public Moon(Earth earth) {
        super("Moon",
                PhysicsConfig.MOON_MASS,
                PhysicsConfig.MOON_RADIUS,
                PhysicsConfig.MOON_ORBIT_RADIUS, // orbit radius around Earth
                PhysicsConfig.MOON_ORBIT_RADIUS,
                PhysicsConfig.MOON_PERIOD,
                Color.web("#cccccc"));
        this.earth        = earth;
        this.gravityRadius = PhysicsConfig.MOON_GRAVITY_RADIUS;
        setOrbitalAngle(PhysicsConfig.MOON_START_ANGLE);
        updateAroundEarth(0);
    }

    // Each frame — recalculate position relative to Earth
    public void updateAroundEarth(double deltaTime) {
        double speed = (2 * Math.PI) / getOrbitalPeriod();
        double angle = getOrbitalAngle() + speed * deltaTime;
        setOrbitalAngle(angle);

        // Always relative to Earth's current position
        setX(earth.getX() + getSemiMajorAxis() * Math.cos(angle));
        setY(earth.getY() + getSemiMinorAxis() * Math.sin(angle));
    }

    public Earth getEarth() { return earth; }
}
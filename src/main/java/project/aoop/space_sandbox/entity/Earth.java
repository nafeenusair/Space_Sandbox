package project.aoop.space_sandbox.entity;

import javafx.scene.paint.Color;
import project.aoop.space_sandbox.config.PhysicsConfig;

public class Earth extends Planet {

    public Earth() {
        super("Earth",
                PhysicsConfig.EARTH_MASS,
                PhysicsConfig.EARTH_RADIUS,
                PhysicsConfig.EARTH_SEMI_MAJOR,
                PhysicsConfig.EARTH_SEMI_MINOR,
                PhysicsConfig.EARTH_PERIOD,
                Color.web("#4fa3e0"));
        this.gravityRadius = PhysicsConfig.EARTH_GRAVITY_RADIUS;
        setOrbitalAngle(PhysicsConfig.EARTH_START_ANGLE);
    }
}
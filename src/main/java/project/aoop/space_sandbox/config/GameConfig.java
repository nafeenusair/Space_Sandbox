package project.aoop.space_sandbox.config;

import javafx.scene.paint.Color;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import project.aoop.space_sandbox.entity.Earth;
import project.aoop.space_sandbox.entity.Planet;
import project.aoop.space_sandbox.physics.OrbitalMechanics;

@Configuration
public class GameConfig {
    @Bean public Earth earth(OrbitalMechanics om) {
        Earth earth = new Earth();
        earth.setOrbitalAngle(PhysicsConfig.EARTH_START_ANGLE);
        earth.setGravityRadius(PhysicsConfig.EARTH_GRAVITY_RADIUS);
        om.update(earth, 0);
        return earth;
    }

    @Bean public Planet mercury(OrbitalMechanics om) {
        return make(om, "Mercury", PhysicsConfig.MERCURY_MASS, PhysicsConfig.MERCURY_RADIUS,
                PhysicsConfig.MERCURY_SEMI_MAJOR, PhysicsConfig.MERCURY_SEMI_MINOR,
                PhysicsConfig.MERCURY_PERIOD, PhysicsConfig.MERCURY_GRAVITY_RADIUS,
                PhysicsConfig.MERCURY_START_ANGLE, Color.GRAY);
    }

    @Bean public Planet venus(OrbitalMechanics om) {
        return make(om, "Venus", PhysicsConfig.VENUS_MASS, PhysicsConfig.VENUS_RADIUS,
                PhysicsConfig.VENUS_SEMI_MAJOR, PhysicsConfig.VENUS_SEMI_MINOR,
                PhysicsConfig.VENUS_PERIOD, PhysicsConfig.VENUS_GRAVITY_RADIUS,
                PhysicsConfig.VENUS_START_ANGLE, Color.web("#e8cda0"));
    }

    @Bean public Planet mars(OrbitalMechanics om) {
        return make(om, "Mars", PhysicsConfig.MARS_MASS, PhysicsConfig.MARS_RADIUS,
                PhysicsConfig.MARS_SEMI_MAJOR, PhysicsConfig.MARS_SEMI_MINOR,
                PhysicsConfig.MARS_PERIOD, PhysicsConfig.MARS_GRAVITY_RADIUS,
                PhysicsConfig.MARS_START_ANGLE, Color.web("#c1440e"));
    }

    @Bean public Planet jupiter(OrbitalMechanics om) {
        return make(om, "Jupiter", PhysicsConfig.JUPITER_MASS, PhysicsConfig.JUPITER_RADIUS,
                PhysicsConfig.JUPITER_SEMI_MAJOR, PhysicsConfig.JUPITER_SEMI_MINOR,
                PhysicsConfig.JUPITER_PERIOD, PhysicsConfig.JUPITER_GRAVITY_RADIUS,
                PhysicsConfig.JUPITER_START_ANGLE, Color.web("#c88b3a"));
    }

    @Bean public Planet saturn(OrbitalMechanics om) {
        return make(om, "Saturn", PhysicsConfig.SATURN_MASS, PhysicsConfig.SATURN_RADIUS,
                PhysicsConfig.SATURN_SEMI_MAJOR, PhysicsConfig.SATURN_SEMI_MINOR,
                PhysicsConfig.SATURN_PERIOD, PhysicsConfig.SATURN_GRAVITY_RADIUS,
                PhysicsConfig.SATURN_START_ANGLE, Color.web("#e4d191"));
    }

    @Bean public Planet uranus(OrbitalMechanics om) {
        return make(om, "Uranus", PhysicsConfig.URANUS_MASS, PhysicsConfig.URANUS_RADIUS,
                PhysicsConfig.URANUS_SEMI_MAJOR, PhysicsConfig.URANUS_SEMI_MINOR,
                PhysicsConfig.URANUS_PERIOD, PhysicsConfig.URANUS_GRAVITY_RADIUS,
                PhysicsConfig.URANUS_START_ANGLE, Color.web("#7de8e8"));
    }

    @Bean public Planet neptune(OrbitalMechanics om) {
        return make(om, "Neptune", PhysicsConfig.NEPTUNE_MASS, PhysicsConfig.NEPTUNE_RADIUS,
                PhysicsConfig.NEPTUNE_SEMI_MAJOR, PhysicsConfig.NEPTUNE_SEMI_MINOR,
                PhysicsConfig.NEPTUNE_PERIOD, PhysicsConfig.NEPTUNE_GRAVITY_RADIUS,
                PhysicsConfig.NEPTUNE_START_ANGLE, Color.web("#3f54ba"));
    }

    private Planet make(OrbitalMechanics om, String name, double mass, double radius,
                        double a, double b, double period, double gravRadius,
                        double angle, Color color) {
        Planet p = new Planet(name, mass, radius, a, b, period, color);
        p.setOrbitalAngle(angle);
        p.setGravityRadius(gravRadius);
        om.update(p, 0);
        return p;
    }
}
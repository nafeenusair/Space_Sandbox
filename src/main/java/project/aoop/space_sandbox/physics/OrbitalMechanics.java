package project.aoop.space_sandbox.physics;

import org.springframework.stereotype.Component;
import project.aoop.space_sandbox.entity.Planet;

@Component
public class OrbitalMechanics {

    // Moves planet forward along its ellipse each frame
    public void update(Planet planet, double deltaTime) {
        double angularSpeed = (2 * Math.PI) / planet.getOrbitalPeriod();
        double angle = planet.getOrbitalAngle() + angularSpeed * deltaTime;
        planet.setOrbitalAngle(angle);

        // Ellipse formula — x = a*cos, y = b*sin
        planet.setX(planet.getSemiMajorAxis() * Math.cos(angle));
        planet.setY(planet.getSemiMinorAxis() * Math.sin(angle));
    }
}
package project.aoop.space_sandbox.physics;

import org.springframework.stereotype.Component;
import project.aoop.space_sandbox.entity.CelestialBody;
import project.aoop.space_sandbox.entity.Ship;

import java.util.List;

@Component
public class GravitySystem {
    private static final double G   = 0.0001;
    private static final double MIN_DIST = 1.0;
    private static final double STAR_INFLUENCE_RADIUS = 10.0;
    private static final double PLANET_INFLUENCE_RADIUS = 4.0;

    public void applyGravity(Ship ship, List<CelestialBody> bodies, double deltaTime) {
        double ax = 0, ay = 0;

        for (CelestialBody body : bodies) {
            double dx = body.getX() - ship.getX();
            double dy = body.getY() - ship.getY();
            double distSq = dx * dx + dy * dy;
            double dist   = Math.sqrt(distSq);

            double influenceRadius = body.getName().equals("Sun")
                    ? STAR_INFLUENCE_RADIUS
                    : PLANET_INFLUENCE_RADIUS;
            if (dist < MIN_DIST || dist > influenceRadius) continue;

            double force = G * body.getMass() / distSq;
            ax += (dx / dist) * force;
            ay += (dy / dist) * force;
        }

        ship.setVx(ship.getVx() + ax * deltaTime);
        ship.setVy(ship.getVy() + ay * deltaTime);
    }
}
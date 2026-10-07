package project.aoop.space_sandbox.physics;

import org.springframework.stereotype.Component;
import project.aoop.space_sandbox.config.PhysicsConfig;
import project.aoop.space_sandbox.entity.CelestialBody;
import project.aoop.space_sandbox.entity.Ship;
import project.aoop.space_sandbox.entity.Star;
import project.aoop.space_sandbox.service.LevelService;

import java.util.List;

@Component
public class GravitySystem {

    private final LevelService levelService;

    public GravitySystem(LevelService levelService) {
        this.levelService = levelService;
    }

    public void applyGravity(Ship ship, List<CelestialBody> bodies, double dt) {
        CelestialBody dominant = null;
        double closestDist     = Double.MAX_VALUE;

        for (CelestialBody body : bodies) {
            double dx   = body.getX() - ship.getX();
            double dy   = body.getY() - ship.getY();
            double dist = Math.sqrt(dx*dx + dy*dy);

            if (dist > body.getGravityRadius()) continue;
            if (dist < body.getRadius())        continue;

            if (body instanceof Star) {
                // Sun always applies gravity — no level check
                if (dist < closestDist) {
                    closestDist = dist;
                    dominant    = body;
                }
            } else {
                // Planets: only apply gravity if at exactly current level
                // Mastered (below level) = fly past freely
                // Locked (above level)   = fly past freely
                if (!levelService.isGravityActive(body.getName())) continue;

                if (dist < closestDist) {
                    closestDist = dist;
                    dominant    = body;
                }
            }
        }

        if (dominant == null) return;

        double dx = dominant.getX() - ship.getX();
        double dy = dominant.getY() - ship.getY();

        if (dominant instanceof Star) {
            applyRawGravity(ship, dx, dy, closestDist, dominant.getMass(), dt);
        } else {
            applyOrbitAssist(ship, dx, dy, closestDist,
                    dominant.getMass(), dominant.getGravityRadius(), dt);
        }
    }

    private void applyRawGravity(Ship ship, double dx, double dy,
                                 double dist, double mass, double dt) {
        double force = PhysicsConfig.G * mass / (dist * dist);
        ship.setVx(ship.getVx() + (dx / dist) * force * dt);
        ship.setVy(ship.getVy() + (dy / dist) * force * dt);
    }

    private void applyOrbitAssist(Ship ship, double dx, double dy,
                                  double dist, double mass,
                                  double zoneRadius, double dt) {
        double rx = -dx / dist, ry = -dy / dist;
        double tx = -ry, ty = rx;

        double radial     = ship.getVx() * rx + ship.getVy() * ry;
        double tangential = ship.getVx() * tx + ship.getVy() * ty;

        double dir           = tangential >= 0 ? 1.0 : -1.0;
        double absTangential = Math.abs(tangential);
        double orbitalSpeed  = Math.sqrt(PhysicsConfig.G * mass / dist);

        double blendRate        = Math.min(PhysicsConfig.ORBIT_ASSIST_RATE * dt, 1.0);
        double newAbsTangential = absTangential + (orbitalSpeed - absTangential) * blendRate;
        double newRadial        = radial * (1.0 - PhysicsConfig.RADIAL_DAMPEN_RATE * dt);

        // Edge retention — stop ship drifting out of zone
        double edgeThreshold = zoneRadius * 0.75;
        if (dist > edgeThreshold) {
            double edgeFraction = (dist - edgeThreshold) / (zoneRadius - edgeThreshold);
            double pullStrength = edgeFraction * PhysicsConfig.G * mass / (dist * dist) * 0.5;
            newRadial -= pullStrength * dt;
        }

        ship.setVx(newRadial * rx + dir * newAbsTangential * tx);
        ship.setVy(newRadial * ry + dir * newAbsTangential * ty);
    }
}
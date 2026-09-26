package project.aoop.space_sandbox.entity;
import org.springframework.stereotype.Component;

@Component
public class Ship {
    private double x = 0, y = 0;
    private double vx = 0, vy = 0;
    private double angle = 0;
    private double radius = 0.3;

    private int engineTier = 1;
    private int health = 100;

    private static final double BASE_THRUST = 2000.0;
    private static final double ROTATE_SPEED = 1.2;

    public void thrust(double deltaTime) {
        double power = BASE_THRUST * engineTier;
        vx += Math.cos(angle) * power * deltaTime;
        vy += Math.sin(angle) * power * deltaTime;
    }

    public void brake(double deltaTime) {
        double braking = Math.max(0.0, 1.0 - 8.0 * deltaTime);
        vx *= braking;
        vy *= braking;
    }

    public void rotateLeft(double deltaTime) {
        angle -= ROTATE_SPEED * deltaTime;
    }

    public void rotateRight(double deltaTime) {
        angle += ROTATE_SPEED * deltaTime;
    }

    public void update(double deltaTime) {
        x += vx * deltaTime;
        y += vy * deltaTime;
    }

    public void crash() { health = 0; }

    public double getSpeed() {
        return Math.sqrt(vx * vx + vy * vy);
    }

    //getters
    public double getX() { return x; }
    public double getY() { return y; }
    public double getVx() { return vx; }
    public double getVy() { return vy; }
    public double getAngle() { return angle; }
    public double getRadius() { return radius; }
    public int getEngineTier() { return engineTier; }
    public int getHealth() { return health; }

    //setters
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setVx(double vx) { this.vx = vx; }
    public void setVy(double vy) { this.vy = vy; }
    public void setEngineTier(int engineTier) { this.engineTier = engineTier; }
    public void setHealth(int health) { this.health = health; }
}

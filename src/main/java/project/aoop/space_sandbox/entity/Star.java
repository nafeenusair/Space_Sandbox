package project.aoop.space_sandbox.entity;

import org.springframework.stereotype.Component;

@Component
public class Star extends CelestialBody {

    public Star() {
        super("Sun", 1000, 1.5);
        this.x = 0;
        this.y = 0;
        this.gravityRadius = 4.5; // ship feels Sun's pull within 3.5 units
    }
}
package project.aoop.space_sandbox.entity;

import org.springframework.stereotype.Component;

@Component
public class Star extends CelestialBody {

    public Star() {
        super("Sun", 100_000, 5.0);
        this.x = 0;
        this.y = 0;
    }
}
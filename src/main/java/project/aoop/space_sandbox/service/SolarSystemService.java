package project.aoop.space_sandbox.service;

import jakarta.annotation.PostConstruct;
import javafx.scene.paint.Color;
import org.springframework.stereotype.Service;
import project.aoop.space_sandbox.entity.CelestialBody;
import project.aoop.space_sandbox.entity.Planet;
import project.aoop.space_sandbox.entity.Star;
import project.aoop.space_sandbox.physics.OrbitalMechanics;

import java.util.ArrayList;
import java.util.List;

@Service
public class SolarSystemService {

    private final Star star;
    private final OrbitalMechanics orbitalMechanics;
    private final List<Planet> planets = new ArrayList<>();

    public SolarSystemService(Star star, OrbitalMechanics orbitalMechanics) {
        this.star             = star;
        this.orbitalMechanics = orbitalMechanics;
    }

    // Runs once after Spring creates this bean
    // Game distance scale: Moon=1 unit, Earth≈10, Mars=15, Jupiter=40, Neptune=130
    // Orbital periods are in game seconds (6x time compression applied)
    @PostConstruct
    public void init() {
        //         name        mass       radius  semiMajor  semiMinor  period(gameSec)    color
        planets.add(new Planet("Mercury", 3.3e23,  0.3,   4.0,   3.91,  1_267_200,  Color.GRAY));
        planets.add(new Planet("Venus",   4.87e24, 0.6,   7.0,   7.00,  3_240_000,  Color.web("#e8cda0")));
        planets.add(new Planet("Earth",   5.97e24, 0.65, 10.0,   9.99,  5_256_000,  Color.web("#4fa3e0")));
        planets.add(new Planet("Mars",    6.39e23, 0.45, 15.0,  14.93,  9_892_800,  Color.web("#c1440e")));
        planets.add(new Planet("Jupiter", 1.9e27,  1.5,  40.0,  39.95, 62_395_200,  Color.web("#c88b3a")));
        planets.add(new Planet("Saturn",  5.68e26, 1.3,  70.0,  69.89, 154_929_600, Color.web("#e4d191")));
        planets.add(new Planet("Uranus",  8.68e25, 0.9, 100.0,  99.89, 441_892_800, Color.web("#7de8e8")));
        planets.add(new Planet("Neptune", 1.02e26, 0.85,130.0, 129.99, 866_736_000, Color.web("#3f54ba")));

        // Spread planets out at different starting angles so they don't all line up
        double[] startAngles = {0.8, 2.1, 4.5, 1.2, 3.8, 0.5, 2.9, 5.1};
        for (int i = 0; i < planets.size(); i++) {
            planets.get(i).setOrbitalAngle(startAngles[i]);
            orbitalMechanics.update(planets.get(i), 0); // set initial position
        }
    }

    // Called every frame by GameLoop
    public void update(double deltaTime) {
        for (Planet planet : planets) {
            orbitalMechanics.update(planet, deltaTime);
        }
    }

    // Returns Sun + all planets — used by GravitySystem
    public List<CelestialBody> getAllBodies() {
        List<CelestialBody> all = new ArrayList<>();
        all.add(star);
        all.addAll(planets);
        return all;
    }

    public Star         getStar()    { return star; }
    public List<Planet> getPlanets() { return planets; }
}
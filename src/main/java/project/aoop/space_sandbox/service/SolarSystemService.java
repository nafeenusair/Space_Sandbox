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

    @PostConstruct
    public void init() {
        //              name       mass      radius  semiMajor  semiMinor  period(sec)         color
        planets.add(new Planet("Mercury",   500,    0.3,    4.0,    3.91,    600,   Color.GRAY));
        planets.add(new Planet("Venus",    4_000,   0.6,    7.0,    7.00,   1_200,  Color.web("#e8cda0")));
        planets.add(new Planet("Earth",    5_000,   0.65,  10.0,    9.99,   2_000,  Color.web("#4fa3e0")));
        planets.add(new Planet("Mars",     1_000,   0.45,  15.0,   14.93,   3_800,  Color.web("#c1440e")));
        planets.add(new Planet("Jupiter", 50_000,   1.5,   40.0,   39.95,  12_000,  Color.web("#c88b3a")));
        planets.add(new Planet("Saturn",  30_000,   1.3,   70.0,   69.89,  24_000,  Color.web("#e4d191")));
        planets.add(new Planet("Uranus",  10_000,   0.9,  100.0,   99.89,  48_000,  Color.web("#7de8e8")));
        planets.add(new Planet("Neptune", 11_000,   0.85, 130.0,  129.99,  96_000,  Color.web("#3f54ba")));

        double[] startAngles = {0.8, 2.1, 4.5, 1.2, 3.8, 0.5, 2.9, 5.1};
        for (int i = 0; i < planets.size(); i++) {
            planets.get(i).setOrbitalAngle(startAngles[i]);
            orbitalMechanics.update(planets.get(i), 0);
        }
    }

    public void update(double deltaTime) {
        for (Planet planet : planets) {
            orbitalMechanics.update(planet, deltaTime);
        }
    }

    public List<CelestialBody> getAllBodies() {
        List<CelestialBody> all = new ArrayList<>();
        all.add(star);
        all.addAll(planets);
        return all;
    }

    public Star         getStar()    { return star; }
    public List<Planet> getPlanets() { return planets; }
}
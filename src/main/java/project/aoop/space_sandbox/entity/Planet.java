package project.aoop.space_sandbox.entity;

import javafx.scene.paint.Color;


public class Planet extends CelestialBody {

    private final double semiMajorAxis;
    private final double semiMinorAxis;
    private final double orbitalPeriod;
    private final Color  color;
    private double orbitalAngle = 0;

    public Planet(String name, double mass, double radius,
                  double semiMajorAxis, double semiMinorAxis,
                  double orbitalPeriod, Color color) {
        super(name, mass, radius);
        this.semiMajorAxis = semiMajorAxis;
        this.semiMinorAxis = semiMinorAxis;
        this.orbitalPeriod = orbitalPeriod;
        this.color         = color;
    }

    public double getSemiMajorAxis() { return semiMajorAxis; }
    public double getSemiMinorAxis() { return semiMinorAxis; }
    public double getOrbitalPeriod() { return orbitalPeriod; }
    public Color  getColor()         { return color; }
    public double getOrbitalAngle()  { return orbitalAngle; }
    public void   setOrbitalAngle(double a) { this.orbitalAngle = a; }
}
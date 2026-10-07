package project.aoop.space_sandbox.service;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class LevelService {

    private int level = 1;
    private final Set<String> visitedPlanets = new HashSet<>();

    // Which level each planet belongs to
    public int getPlanetLevel(String name) {
        return switch (name) {
            case "Earth", "Moon" -> 1;
            case "Mars"          -> 2;
            case "Venus"         -> 3;
            case "Mercury"       -> 4;
            case "Jupiter"       -> 5;
            case "Saturn"        -> 6;
            case "Uranus"        -> 7;
            case "Neptune"       -> 8;
            default              -> 0;
        };
    }

    // Planets you can land on (current level and below)
    public List<String> getAccessiblePlanets() {
        List<String> list = new ArrayList<>(Arrays.asList("Earth", "Moon"));
        if (level >= 2) list.add("Mars");
        if (level >= 3) list.add("Venus");
        if (level >= 4) list.add("Mercury");
        if (level >= 5) list.add("Jupiter");
        if (level >= 6) list.add("Saturn");
        if (level >= 7) list.add("Uranus");
        if (level >= 8) list.add("Neptune");
        return list;
    }

    // Gravity only works for planets at exactly your current level
    // Mastered planets (below level) = no gravity
    // Locked planets (above level) = no gravity
    public boolean isGravityActive(String name) {
        return getPlanetLevel(name) == level;
    }

    // Can still land on mastered planets (for resources later)
    public boolean isAccessible(String name) {
        return getPlanetLevel(name) <= level;
    }

    // Planet is below your level — you've mastered it
    public boolean isMastered(String name) {
        return getPlanetLevel(name) < level;
    }

    // Planet is above your level — locked
    public boolean isLocked(String name) {
        return getPlanetLevel(name) > level;
    }

    public String getTargetPlanet() {
        return switch (level) {
            case 1 -> visitedPlanets.contains("Earth") ? "Moon" : "Earth";
            case 2 -> "Mars";
            case 3 -> "Venus";
            case 4 -> "Mercury";
            case 5 -> "Jupiter";
            case 6 -> "Saturn";
            case 7 -> "Uranus";
            case 8 -> "Neptune";
            default -> "Neptune";
        };
    }

    public void visitPlanet(String name) {
        if (visitedPlanets.contains(name)) return;
        visitedPlanets.add(name);
        System.out.println("✓ Visited: " + name);
    }

    public void levelUp() {
        if (level < 8) {
            level++;
            System.out.println("★ LEVEL " + level
                    + " — " + getTargetPlanet() + " now active");
        }
    }

    public boolean hasLaser()    { return level >= 5; }
    public int     getLevel()    { return level; }
    public Set<String> getVisited() { return visitedPlanets; }
}
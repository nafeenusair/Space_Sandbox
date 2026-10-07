package project.aoop.space_sandbox.service;

import org.springframework.stereotype.Service;

@Service
public class NavigationService {

    private String customTarget = null;
    private final LevelService levelService;

    public NavigationService(LevelService levelService) {
        this.levelService = levelService;
    }

    // Returns custom target if set, otherwise uses level progression
    public String getTarget() {
        return customTarget != null ? customTarget : levelService.getTargetPlanet();
    }

    public void setTarget(String name) { this.customTarget = name; }
    public void clearTarget()          { this.customTarget = null; }
}
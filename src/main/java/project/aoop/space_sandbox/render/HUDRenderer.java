package project.aoop.space_sandbox.render;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import org.springframework.stereotype.Component;
import project.aoop.space_sandbox.camera.GameCamera;
import project.aoop.space_sandbox.entity.Planet;
import project.aoop.space_sandbox.entity.Ship;
import project.aoop.space_sandbox.service.LevelService;
import project.aoop.space_sandbox.service.NavigationService;

import java.util.List;

@Component
public class HUDRenderer {

    private final GameCamera       camera;
    private final LevelService     levelService;
    private final NavigationService navigationService;

    public HUDRenderer(GameCamera camera, LevelService levelService,
                       NavigationService navigationService) {
        this.camera            = camera;
        this.levelService      = levelService;
        this.navigationService = navigationService;
    }

    public void render(GraphicsContext gc, Ship ship, List<Planet> planets) {
        drawLevelPanel(gc);
        drawTargetArrow(gc, ship, planets);
        drawSpeedBar(gc, ship);
    }

    private void drawLevelPanel(GraphicsContext gc) {
        double h = levelService.hasLaser() ? 95 : 78;

        gc.setFill(Color.color(0, 0, 0, 0.65));
        gc.fillRoundRect(12, 12, 200, h, 8, 8);
        gc.setStroke(Color.color(1, 1, 1, 0.08));
        gc.setLineWidth(1);
        gc.strokeRoundRect(12, 12, 200, h, 8, 8);

        gc.setFont(Font.font("Courier New", 10));
        gc.setFill(Color.color(1, 1, 1, 0.3));
        gc.fillText("── SHIP LEVEL ──", 22, 30);

        gc.setFont(Font.font("Courier New", FontWeight.BOLD, 20));
        gc.setFill(Color.web("#4dffc8"));
        gc.fillText("LEVEL " + levelService.getLevel(), 22, 54);

        gc.setFont(Font.font("Courier New", 10));
        gc.setFill(Color.color(1, 1, 1, 0.4));
        gc.fillText(String.join("  ", levelService.getAccessiblePlanets()), 22, 70);

        if (levelService.hasLaser()) {
            gc.setFill(Color.web("#ff4444"));
            gc.fillText("⚡ LASER UNLOCKED", 22, 86);
        }
    }

    private void drawTargetArrow(GraphicsContext gc, Ship ship, List<Planet> planets) {
        // Use NavigationService — respects both custom and progression targets
        String targetName = navigationService.getTarget();

        Planet target = planets.stream()
                .filter(p -> p.getName().equals(targetName))
                .findFirst().orElse(null);
        if (target == null) return;

        double cx    = camera.getScreenW() / 2;
        double cy    = camera.getScreenH() / 2;
        double dx    = target.getX() - ship.getX();
        double dy    = target.getY() - ship.getY();
        double dist  = Math.sqrt(dx*dx + dy*dy);
        double angle = Math.atan2(dy, dx);

        double arrowDist = 90;
        double ax = cx + Math.cos(angle) * arrowDist;
        double ay = cy + Math.sin(angle) * arrowDist;

        boolean offScreen = dist * camera.getZoom() > camera.getScreenW() * 0.4;

        if (offScreen) {
            gc.save();
            gc.translate(ax, ay);
            gc.rotate(Math.toDegrees(angle));
            gc.setFill(Color.color(0.3, 1, 0.8, 0.9));
            gc.fillPolygon(new double[]{14, -6, -6}, new double[]{0, -5, 5}, 3);
            gc.setStroke(Color.color(0.3, 1, 0.8, 0.4));
            gc.setLineWidth(2);
            gc.strokeLine(-6, 0, -16, 0);
            gc.restore();

            gc.setFill(Color.color(0.3, 1, 0.8, 0.9));
            gc.setFont(Font.font("Courier New", FontWeight.BOLD, 12));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText("→ " + target.getName(),
                    ax + Math.cos(angle)*24, ay + Math.sin(angle)*24 - 5);
            gc.setFill(Color.color(1, 1, 1, 0.5));
            gc.setFont(Font.font("Courier New", 10));
            gc.fillText((int) dist + " u",
                    ax + Math.cos(angle)*24, ay + Math.sin(angle)*24 + 9);
            gc.setTextAlign(TextAlignment.LEFT);

        } else {
            // Planet visible — draw pulsing ring around it
            double sx = camera.toScreenX(target.getX());
            double sy = camera.toScreenY(target.getY());
            gc.setStroke(Color.color(0.3, 1, 0.8, 0.5));
            gc.setLineWidth(1.5);
            double pr = target.getRadius() * camera.getZoom() + 12;
            gc.strokeOval(sx - pr, sy - pr, pr*2, pr*2);
        }
    }

    private void drawSpeedBar(GraphicsContext gc, Ship ship) {
        double cx     = camera.getScreenW() / 2;
        double bottom = camera.getScreenH() - 14;

        gc.setFill(Color.color(0, 0, 0, 0.5));
        gc.fillRoundRect(cx - 80, bottom - 28, 160, 28, 6, 6);
        gc.setFill(Color.color(1, 1, 1, 0.55));
        gc.setFont(Font.font("Courier New", 12));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(String.format("SPD  %.2f  u/s", ship.getSpeed()), cx, bottom - 10);
        gc.setTextAlign(TextAlignment.LEFT);
    }
}
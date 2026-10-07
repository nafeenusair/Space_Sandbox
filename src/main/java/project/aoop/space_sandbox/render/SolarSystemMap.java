package project.aoop.space_sandbox.render;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import org.springframework.stereotype.Component;
import project.aoop.space_sandbox.entity.Planet;
import project.aoop.space_sandbox.entity.Ship;
import project.aoop.space_sandbox.entity.Star;
import project.aoop.space_sandbox.service.LevelService;
import project.aoop.space_sandbox.service.NavigationService;

import java.util.List;

@Component
public class SolarSystemMap {

    private boolean open = false;

    private final LevelService             levelService;
    private final ProceduralPlanetRenderer proceduralRenderer;

    public SolarSystemMap(LevelService levelService,
                          ProceduralPlanetRenderer proceduralRenderer) {
        this.levelService       = levelService;
        this.proceduralRenderer = proceduralRenderer;
    }

    public void open()      { open = true; }
    public void close()     { open = false; }
    public void toggle()    { open = !open; }
    public boolean isOpen() { return open; }

    public void render(GraphicsContext gc, double w, double h, Ship ship,
                       Star star, List<Planet> planets, NavigationService nav) {
        if (!open) return;

        // Dark overlay
        gc.setFill(Color.color(0, 0, 0.05, 0.92));
        gc.fillRect(0, 0, w, h);
        gc.setStroke(Color.color(1, 1, 1, 0.08));
        gc.setLineWidth(1);
        gc.strokeRect(20, 20, w-40, h-40);

        // Title
        gc.setFill(Color.color(1, 1, 1, 0.4));
        gc.setFont(Font.font("Courier New", 11));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("SOLAR SYSTEM  ·  click planet to track  ·  ESC to close", w/2, 38);

        double cx = w / 2;
        double cy = h / 2 + 10;

        // Max usable radius in pixels from center
        double maxR = Math.min(w, h) * 0.42;

        // Max world distance (Neptune)
        double maxDist = planets.stream()
                .mapToDouble(Planet::getSemiMajorAxis)
                .max().orElse(6500);

        // Log scale: world dist → screen pixels from center
        // Inner planets spread far apart, outer ones compressed
        java.util.function.DoubleUnaryOperator logScale = (worldDist) -> {
            if (worldDist <= 0) return 0;
            return (Math.log(worldDist) / Math.log(maxDist)) * maxR;
        };

        // Convert world (x,y) → map screen position
        java.util.function.BiFunction<Double, Double, double[]> toMap = (wx, wy) -> {
            double dist = Math.sqrt(wx*wx + wy*wy);
            if (dist < 0.001) return new double[]{cx, cy};
            double scaled = logScale.applyAsDouble(dist);
            return new double[]{cx + (wx/dist)*scaled, cy + (wy/dist)*scaled};
        };

        // Orbit rings — log scaled circles
        gc.setLineWidth(0.5);
        for (Planet p : planets) {
            double r = logScale.applyAsDouble(p.getSemiMajorAxis());
            gc.setStroke(Color.color(1, 1, 1, 0.07));
            gc.strokeOval(cx - r, cy - r, r*2, r*2);
        }

        // Sun
        gc.setFill(Color.color(1, 0.9, 0.3, 0.3));
        gc.fillOval(cx - 12, cy - 12, 24, 24);
        gc.setFill(Color.web("#ffe066"));
        gc.fillOval(cx - 6, cy - 6, 12, 12);

        // Planets
        String target = nav.getTarget();
        for (Planet p : planets) {
            double[] mp = toMap.apply(p.getX(), p.getY());
            double px   = mp[0];
            double py   = mp[1];

            // Planet display size — bigger for map, capped so outer planets don't dominate
            double pr = Math.min(Math.max(p.getRadius() * 0.25, 8), 22);

            boolean accessible = levelService.isAccessible(p.getName());
            boolean isTarget   = p.getName().equals(target);

            // Dim locked planets
            gc.setGlobalAlpha(accessible ? 1.0 : 0.3);
            proceduralRenderer.drawAt(gc, p.getName(), px, py, pr, p.getColor());
            gc.setGlobalAlpha(1.0);

            // Target ring — glowing
            if (isTarget) {
                gc.setStroke(Color.color(0.3, 1, 0.8, 0.9));
                gc.setLineWidth(2);
                gc.strokeOval(px - pr - 6, py - pr - 6, (pr+6)*2, (pr+6)*2);
            }

            // Distance label under planet name
            double realDist = Math.sqrt(
                    Math.pow(p.getX() - ship.getX(), 2) +
                            Math.pow(p.getY() - ship.getY(), 2)
            );

            gc.setFill(accessible ? Color.color(1, 1, 1, 0.85) : Color.color(1, 1, 1, 0.3));
            gc.setFont(Font.font("Courier New",
                    accessible ? FontWeight.BOLD : FontWeight.NORMAL, 10));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText(p.getName(), px, py + pr + 14);

            gc.setFill(Color.color(1, 1, 1, 0.35));
            gc.setFont(Font.font("Courier New", 9));
            gc.fillText((int) realDist + " u", px, py + pr + 25);

            if (!accessible) {
                gc.setFill(Color.color(1, 0.4, 0.4, 0.6));
                gc.fillText("LOCKED", px, py + pr + 36);
            }
        }

        // Ship marker
        double rawSx = cx + ship.getX() / maxDist * maxR;
        double rawSy = cy + ship.getY() / maxDist * maxR;
        double[] sm  = toMap.apply(ship.getX(), ship.getY());
        double sx    = Math.max(30, Math.min(w-30, sm[0]));
        double sy    = Math.max(50, Math.min(h-30, sm[1]));

        gc.save();
        gc.translate(sx, sy);
        gc.rotate(Math.toDegrees(ship.getAngle()));
        gc.setFill(Color.web("#4dffc8"));
        gc.fillPolygon(new double[]{8, -5, -5}, new double[]{0, -5, 5}, 3);
        gc.restore();

        gc.setFill(Color.color(0.3, 1, 0.8, 0.7));
        gc.setFont(Font.font("Courier New", FontWeight.BOLD, 10));
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("YOU", sx + 10, sy + 4);

        // Tracking label bottom right
        gc.setFill(Color.color(0.3, 1, 0.8, 0.5));
        gc.setFont(Font.font("Courier New", 10));
        gc.setTextAlign(TextAlignment.RIGHT);
        gc.fillText("TRACKING → " + target, w - 30, h - 30);
        gc.setTextAlign(TextAlignment.LEFT);
    }

    // Returns true if a planet was selected
    public boolean handleClick(double mouseX, double mouseY, double w, double h,
                               List<Planet> planets, NavigationService nav) {
        if (!open) return false;

        double cx      = w / 2;
        double cy      = h / 2 + 10;
        double maxR    = Math.min(w, h) * 0.42;
        double maxDist = planets.stream()
                .mapToDouble(Planet::getSemiMajorAxis)
                .max().orElse(6500);

        Planet closest  = null;
        double closestD = Double.MAX_VALUE;

        for (Planet p : planets) {
            double dist   = Math.sqrt(p.getX()*p.getX() + p.getY()*p.getY());
            double scaled = dist < 0.001 ? 0
                    : (Math.log(dist) / Math.log(maxDist)) * maxR;
            double px     = cx + (p.getX() / (dist < 0.001 ? 1 : dist)) * scaled;
            double py     = cy + (p.getY() / (dist < 0.001 ? 1 : dist)) * scaled;
            double hitR   = Math.max(Math.max(p.getRadius() * 0.25, 8), 18);
            double d      = Math.sqrt((mouseX-px)*(mouseX-px) + (mouseY-py)*(mouseY-py));

            if (d < hitR && d < closestD) {
                closestD = d;
                closest  = p;
            }
        }

        if (closest != null) {
            nav.setTarget(closest.getName());
            System.out.println("📍 Tracking: " + closest.getName());
        }

        close();
        return closest != null;
    }
}
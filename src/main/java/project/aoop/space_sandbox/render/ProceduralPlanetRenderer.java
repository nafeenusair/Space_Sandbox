package project.aoop.space_sandbox.render;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import org.springframework.stereotype.Component;
import project.aoop.space_sandbox.camera.GameCamera;
import project.aoop.space_sandbox.entity.Planet;
import project.aoop.space_sandbox.entity.Star;

@Component
public class ProceduralPlanetRenderer {

    private final GameCamera camera;

    public ProceduralPlanetRenderer(GameCamera camera) {
        this.camera = camera;
    }

    // ── Used by game view (converts world → screen first) ──
    public void drawPlanet(GraphicsContext gc, Planet planet) {
        double sx = camera.toScreenX(planet.getX());
        double sy = camera.toScreenY(planet.getY());
        double r  = Math.max(planet.getRadius() * camera.getZoom(), 4);

        if (sx < -r*3 || sx > camera.getScreenW() + r*3) return;
        if (sy < -r*3 || sy > camera.getScreenH() + r*3) return;

        drawAt(gc, planet.getName(), sx, sy, r, planet.getColor());

        // Label when zoomed in enough
        if (camera.getZoom() > 0.05) {
            gc.setFill(Color.color(1, 1, 1, 0.7));
            gc.setFont(javafx.scene.text.Font.font("Courier New", 11));
            gc.fillText(planet.getName(), sx + r + 5, sy + 4);
        }
    }

    // ── Used by game view for Sun ──
    public void drawSun(GraphicsContext gc, Star star) {
        double sx = camera.toScreenX(star.getX());
        double sy = camera.toScreenY(star.getY());
        double r  = star.getRadius() * camera.getZoom();

        // Corona
        for (int i = 4; i >= 1; i--) {
            gc.setFill(Color.color(1, 0.7, 0.1, 0.04 * i));
            gc.fillOval(sx - r*i*1.5, sy - r*i*1.5, r*i*3, r*i*3);
        }

        // Surface gradient
        RadialGradient grad = new RadialGradient(
                0, 0, sx - r*0.2, sy - r*0.2, r * 1.2, false, CycleMethod.NO_CYCLE,
                new Stop(0,   Color.web("#fff5a0")),
                new Stop(0.5, Color.web("#ffcc00")),
                new Stop(1,   Color.web("#ff6600"))
        );
        gc.setFill(grad);
        gc.fillOval(sx - r, sy - r, r*2, r*2);
    }

    // ── Used by BOTH game view (via drawPlanet) and map overlay ──
    // Takes explicit screen coordinates — no camera involved
    public void drawAt(GraphicsContext gc, String name,
                       double cx, double cy, double r, Color color) {
        // Saturn rings go BEHIND the sphere
        if (name.equals("Saturn")) drawSaturnRings(gc, cx, cy, r);

        // Base sphere with lighting
        drawSphere(gc, cx, cy, r, color);

        // Details only visible when planet is big enough to see them
        if (r > 6) {
            switch (name) {
                case "Earth"   -> drawEarth(gc, cx, cy, r);
                case "Moon"    -> drawMoon(gc, cx, cy, r);
                case "Mars"    -> drawMars(gc, cx, cy, r);
                case "Venus"   -> drawVenus(gc, cx, cy, r);
                case "Mercury" -> drawMercury(gc, cx, cy, r);
                case "Jupiter" -> drawJupiter(gc, cx, cy, r);
                case "Saturn"  -> drawSaturnBands(gc, cx, cy, r);
                case "Uranus"  -> drawUranus(gc, cx, cy, r);
                case "Neptune" -> drawNeptune(gc, cx, cy, r);
            }
        }

        // Atmosphere glow for Earth and Venus
        if (r > 8 && (name.equals("Earth") || name.equals("Venus"))) {
            Color atm = name.equals("Earth")
                    ? Color.color(0.4, 0.7, 1.0, 0.12)
                    : Color.color(1.0, 0.9, 0.6, 0.12);
            gc.setFill(atm);
            gc.fillOval(cx - r*1.18, cy - r*1.18, r*2.36, r*2.36);
        }
    }

    // ── Base sphere — 3D lighting via offset radial gradient ──────────────────

    private void drawSphere(GraphicsContext gc, double cx, double cy,
                            double r, Color base) {
        // Dark limb
        gc.setFill(base.darker().darker());
        gc.fillOval(cx - r, cy - r, r*2, r*2);

        // Lit surface (light from top-left)
        RadialGradient lit = new RadialGradient(
                0, 0, cx - r*0.28, cy - r*0.28, r * 1.1, false, CycleMethod.NO_CYCLE,
                new Stop(0.00, base.brighter().brighter()),
                new Stop(0.45, base.brighter()),
                new Stop(0.75, base),
                new Stop(1.00, base.darker().darker().darker())
        );
        gc.setFill(lit);
        gc.fillOval(cx - r, cy - r, r*2, r*2);
    }

    // ── Planet surface details ─────────────────────────────────────────────────

    private void drawEarth(GraphicsContext gc, double cx, double cy, double r) {
        gc.save();
        gc.beginPath(); gc.arc(cx, cy, r, r, 0, 360); gc.clip();

        // Continents
        gc.setFill(Color.web("#2d7a2d"));
        gc.fillOval(cx + r*0.05, cy - r*0.05, r*0.35, r*0.55); // Africa
        gc.fillOval(cx - r*0.45, cy - r*0.10, r*0.28, r*0.50); // Americas
        gc.fillOval(cx - r*0.10, cy - r*0.45, r*0.55, r*0.30); // Eurasia

        // Ice caps
        gc.setFill(Color.color(1, 1, 1, 0.8));
        gc.fillOval(cx - r*0.40, cy - r,       r*0.80, r*0.30);
        gc.fillOval(cx - r*0.35, cy + r*0.75,  r*0.70, r*0.28);

        // Clouds
        gc.setFill(Color.color(1, 1, 1, 0.2));
        gc.fillOval(cx - r*0.6,  cy - r*0.2, r*0.5,  r*0.14);
        gc.fillOval(cx + r*0.1,  cy + r*0.2, r*0.55, r*0.12);

        gc.restore();
    }

    private void drawMoon(GraphicsContext gc, double cx, double cy, double r) {
        gc.save();
        gc.beginPath(); gc.arc(cx, cy, r, r, 0, 360); gc.clip();

        // Maria (dark seas)
        gc.setFill(Color.color(0.28, 0.28, 0.32, 0.6));
        gc.fillOval(cx - r*0.15, cy - r*0.25, r*0.40, r*0.35);
        gc.fillOval(cx + r*0.10, cy + r*0.10, r*0.30, r*0.25);

        // Craters
        gc.setStroke(Color.color(0.2, 0.2, 0.2, 0.5));
        gc.setLineWidth(r * 0.04);
        gc.strokeOval(cx + r*0.25, cy - r*0.35, r*0.20, r*0.20);
        gc.strokeOval(cx - r*0.40, cy - r*0.10, r*0.15, r*0.15);
        gc.strokeOval(cx + r*0.10, cy + r*0.30, r*0.12, r*0.12);

        gc.restore();
    }

    private void drawMars(GraphicsContext gc, double cx, double cy, double r) {
        gc.save();
        gc.beginPath(); gc.arc(cx, cy, r, r, 0, 360); gc.clip();

        // Dark volcanic patches
        gc.setFill(Color.color(0.4, 0.15, 0.05, 0.4));
        gc.fillOval(cx - r*0.20, cy - r*0.10, r*0.40, r*0.35);
        gc.fillOval(cx + r*0.15, cy + r*0.10, r*0.35, r*0.25);

        // Polar ice cap
        gc.setFill(Color.color(1, 1, 1, 0.75));
        gc.fillOval(cx - r*0.30, cy - r,       r*0.60, r*0.28);

        // Valles Marineris
        gc.setStroke(Color.color(0.3, 0.1, 0.05, 0.5));
        gc.setLineWidth(r * 0.06);
        gc.strokeLine(cx - r*0.4, cy + r*0.1, cx + r*0.3, cy + r*0.05);

        gc.restore();
    }

    private void drawVenus(GraphicsContext gc, double cx, double cy, double r) {
        gc.save();
        gc.beginPath(); gc.arc(cx, cy, r, r, 0, 360); gc.clip();

        gc.setFill(Color.color(1, 0.95, 0.7, 0.22));
        for (int i = -3; i <= 3; i++) {
            gc.fillOval(cx - r, cy + i * r*0.28 - r*0.1, r*2, r*0.2);
        }

        gc.restore();
    }

    private void drawMercury(GraphicsContext gc, double cx, double cy, double r) {
        gc.save();
        gc.beginPath(); gc.arc(cx, cy, r, r, 0, 360); gc.clip();

        double[][] craters = {
                {0.3, -0.3, 0.22}, {-0.35, 0.1, 0.18}, {0.1, 0.35, 0.15},
                {-0.15, -0.4, 0.13}, {0.4, 0.25, 0.16}, {-0.1, 0.1, 0.10}
        };
        for (double[] c : craters) {
            double cx2 = cx + c[0]*r, cy2 = cy + c[1]*r, cr = c[2]*r;
            gc.setFill(Color.color(0.3, 0.3, 0.3, 0.3));
            gc.fillOval(cx2 - cr, cy2 - cr, cr*2, cr*2);
            gc.setStroke(Color.color(0.2, 0.2, 0.2, 0.5));
            gc.setLineWidth(cr * 0.15);
            gc.strokeOval(cx2 - cr, cy2 - cr, cr*2, cr*2);
        }

        gc.restore();
    }

    private void drawJupiter(GraphicsContext gc, double cx, double cy, double r) {
        gc.save();
        gc.beginPath(); gc.arc(cx, cy, r, r, 0, 360); gc.clip();

        // Horizontal bands
        Color[] bands = {
                Color.web("#c88b3a"), Color.web("#e8c070"), Color.web("#aa6020"),
                Color.web("#d4a050"), Color.web("#bb7030"), Color.web("#e0b060"),
        };
        double bh = r * 2.0 / bands.length;
        for (int i = 0; i < bands.length; i++) {
            gc.setFill(Color.color(bands[i].getRed(), bands[i].getGreen(),
                    bands[i].getBlue(), 0.5));
            gc.fillRect(cx - r, cy - r + i * bh, r*2, bh);
        }

        // Great Red Spot
        gc.setFill(Color.color(0.8, 0.3, 0.2, 0.65));
        gc.fillOval(cx + r*0.1, cy + r*0.15, r*0.35, r*0.22);

        gc.restore();
    }

    private void drawSaturnRings(GraphicsContext gc, double cx, double cy, double r) {
        // Outer ring
        gc.setStroke(Color.color(0.9, 0.85, 0.65, 0.45));
        gc.setLineWidth(r * 0.22);
        gc.strokeOval(cx - r*2.0, cy - r*0.44, r*4.0, r*0.88);

        // Inner ring
        gc.setStroke(Color.color(0.7, 0.65, 0.45, 0.35));
        gc.setLineWidth(r * 0.12);
        gc.strokeOval(cx - r*1.45, cy - r*0.32, r*2.9, r*0.64);
    }

    private void drawSaturnBands(GraphicsContext gc, double cx, double cy, double r) {
        gc.save();
        gc.beginPath(); gc.arc(cx, cy, r, r, 0, 360); gc.clip();

        Color[] bands = {Color.web("#e4d191"), Color.web("#c8b870"), Color.web("#d4c880")};
        double bh = r * 2.0 / bands.length;
        for (int i = 0; i < bands.length; i++) {
            gc.setFill(Color.color(bands[i].getRed(), bands[i].getGreen(),
                    bands[i].getBlue(), 0.4));
            gc.fillRect(cx - r, cy - r + i * bh, r*2, bh);
        }

        gc.restore();
    }

    private void drawUranus(GraphicsContext gc, double cx, double cy, double r) {
        gc.save();
        gc.beginPath(); gc.arc(cx, cy, r, r, 0, 360); gc.clip();

        gc.setFill(Color.color(0.5, 0.9, 0.9, 0.15));
        gc.fillOval(cx - r, cy - r*0.20, r*2, r*0.40);
        gc.fillOval(cx - r, cy + r*0.25, r*2, r*0.30);

        gc.restore();
    }

    private void drawNeptune(GraphicsContext gc, double cx, double cy, double r) {
        gc.save();
        gc.beginPath(); gc.arc(cx, cy, r, r, 0, 360); gc.clip();

        // Cloud streaks
        gc.setFill(Color.color(0.7, 0.85, 1.0, 0.25));
        gc.fillOval(cx - r*0.6, cy - r*0.15, r*0.9, r*0.22);
        gc.fillOval(cx + r*0.1, cy + r*0.25, r*0.6, r*0.18);

        // Dark spot
        gc.setFill(Color.color(0.1, 0.2, 0.5, 0.4));
        gc.fillOval(cx - r*0.2, cy - r*0.05, r*0.35, r*0.28);

        gc.restore();
    }
}
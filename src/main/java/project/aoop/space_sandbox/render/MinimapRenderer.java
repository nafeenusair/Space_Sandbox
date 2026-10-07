package project.aoop.space_sandbox.render;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.springframework.stereotype.Component;
import project.aoop.space_sandbox.entity.Planet;
import project.aoop.space_sandbox.entity.Ship;
import project.aoop.space_sandbox.entity.Star;

import java.util.List;

@Component
public class MinimapRenderer {

    private static final double SIZE       = 180;
    private static final double PADDING    = 14;
    private static final double MAX_RADIUS = SIZE / 2 - 8; // usable radius in pixels
    private static final double MAX_DIST   = 6500;          // Neptune's orbit

    public void render(GraphicsContext gc, double canvasW, double canvasH,
                       Ship ship, Star star, List<Planet> planets) {

        double mx  = canvasW - SIZE - PADDING;
        double my  = PADDING;
        double mcx = mx + SIZE / 2;
        double mcy = my + SIZE / 2;

        // Background
        gc.setFill(Color.color(0, 0, 0.05, 0.80));
        gc.fillRoundRect(mx, my, SIZE, SIZE, 10, 10);
        gc.setStroke(Color.color(1, 1, 1, 0.12));
        gc.setLineWidth(1);
        gc.strokeRoundRect(mx, my, SIZE, SIZE, 10, 10);

        // Clip to minimap box
        gc.save();
        gc.beginPath();
        gc.rect(mx, my, SIZE, SIZE);
        gc.clip();

        // Orbit rings — also log scaled
        gc.setLineWidth(0.5);
        for (Planet p : planets) {
            double logR = logScale(p.getSemiMajorAxis());
            gc.setStroke(Color.color(1, 1, 1, 0.06));
            gc.strokeOval(mcx - logR, mcy - logR, logR*2, logR*2);
        }

        // Sun — always at center
        gc.setFill(Color.color(1, 0.9, 0.3, 0.3));
        gc.fillOval(mcx - 6, mcy - 6, 12, 12);
        gc.setFill(Color.web("#ffe066"));
        gc.fillOval(mcx - 3, mcy - 3, 6, 6);

        // Planets — log scaled position, bigger dots, labels
        for (Planet p : planets) {
            double[] mp = worldToMinimap(p.getX(), p.getY(), mcx, mcy);

            // Skip if outside bounds
            if (mp[0] < mx || mp[0] > mx+SIZE || mp[1] < my || mp[1] > my+SIZE) continue;

            // Dot size relative to planet radius — min 4px so it's always visible
            double dotR = Math.max(4, Math.min(8, p.getRadius() * 0.15));

            gc.setFill(p.getColor());
            gc.fillOval(mp[0] - dotR, mp[1] - dotR, dotR*2, dotR*2);

            // Edge highlight
            gc.setStroke(Color.color(1, 1, 1, 0.3));
            gc.setLineWidth(0.5);
            gc.strokeOval(mp[0] - dotR, mp[1] - dotR, dotR*2, dotR*2);

            // Label — always visible on minimap
            gc.setFill(Color.color(1, 1, 1, 0.55));
            gc.setFont(Font.font("Courier New", 8));
            gc.setTextAlign(TextAlignment.LEFT);
            gc.fillText(p.getName(), mp[0] + dotR + 2, mp[1] + 3);
        }

        // Ship marker
        double[] sm      = worldToMinimap(ship.getX(), ship.getY(), mcx, mcy);
        double rawSx     = sm[0];
        double rawSy     = sm[1];
        double sx        = Math.max(mx + 5, Math.min(mx + SIZE - 5, rawSx));
        double sy        = Math.max(my + 5, Math.min(my + SIZE - 5, rawSy));
        boolean offEdge  = rawSx < mx || rawSx > mx+SIZE || rawSy < my || rawSy > my+SIZE;

        gc.save();
        gc.translate(sx, sy);
        gc.rotate(Math.toDegrees(ship.getAngle()));
        gc.setFill(Color.web("#4dffc8"));
        gc.fillPolygon(new double[]{6, -4, -4}, new double[]{0, -3, 3}, 3);
        gc.restore();

        // Ring indicator when ship is at edge
        if (offEdge) {
            gc.setStroke(Color.web("#4dffc8"));
            gc.setLineWidth(1.5);
            gc.strokeOval(sx - 5, sy - 5, 10, 10);
        }

        gc.restore(); // end clip

        // Labels below minimap
        gc.setFill(Color.color(1, 1, 1, 0.25));
        gc.setFont(Font.font("Courier New", 9));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("MINIMAP  ·  click to expand", mcx, my + SIZE + 13);

        double speed = ship.getSpeed();
        gc.setFill(Color.color(1, 1, 1, 0.45));
        gc.fillText(String.format("SPD %.2f", speed), mcx, my + SIZE + 25);
        gc.setTextAlign(TextAlignment.LEFT);
    }

    // Log scale: converts world distance to minimap pixels
    // Inner planets spread out, outer planets compressed
    private double logScale(double worldDist) {
        if (worldDist <= 0) return 0;
        return (Math.log(worldDist) / Math.log(MAX_DIST)) * MAX_RADIUS;
    }

    // Converts any world (x, y) to minimap screen position using log scale
    private double[] worldToMinimap(double wx, double wy, double mcx, double mcy) {
        double dist = Math.sqrt(wx*wx + wy*wy);
        if (dist < 0.001) return new double[]{mcx, mcy};

        // Direction preserved, distance log-scaled
        double scaledDist = logScale(dist);
        double mx = mcx + (wx / dist) * scaledDist;
        double my = mcy + (wy / dist) * scaledDist;
        return new double[]{mx, my};
    }

    public boolean isClickOnMinimap(double mouseX, double mouseY,
                                    double canvasW, double canvasH) {
        double mx = canvasW - SIZE - PADDING;
        double my = PADDING;
        return mouseX >= mx && mouseX <= mx+SIZE && mouseY >= my && mouseY <= my+SIZE;
    }
}
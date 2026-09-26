package project.aoop.space_sandbox.render;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.springframework.stereotype.Component;
import project.aoop.space_sandbox.camera.GameCamera;

@Component
public class StarfieldRenderer {
    private final double[][] stars = new double[250][2];

    public StarfieldRenderer() {
        for (int i = 0; i < stars.length; i++) {
            stars[i][0] = Math.random();
            stars[i][1] = Math.random();
        }
    }

    public void render(GraphicsContext gc, GameCamera camera, double w, double h) {
        // Black background first
        gc.setFill(Color.web("#000008"));
        gc.fillRect(0, 0, w, h);

        double[] alphas = {0.35, 0.60, 0.90};
        double[] sizes  = {0.8,  1.2,  1.8 };
        double[] speeds = {0.02, 0.05, 0.10};
        int[]    counts = {120,  80,   50  };
        int offset = 0;
        for (int layer = 0; layer < 3; layer++) {
            gc.setFill(Color.color(1, 1, 1, alphas[layer]));
            for (int i = offset; i < offset + counts[layer]; i++) {
                double sx = mod(stars[i][0] * w - camera.getX() * speeds[layer], w);
                double sy = mod(stars[i][1] * h - camera.getY() * speeds[layer], h);
                gc.fillOval(sx, sy, sizes[layer], sizes[layer]);
            }
            offset += counts[layer];
        }
    }

    private double mod(double v, double range) {
        return ((v % range) + range) % range;
    }
}
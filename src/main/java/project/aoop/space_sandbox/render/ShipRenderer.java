package project.aoop.space_sandbox.render;

import jakarta.annotation.PostConstruct;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.springframework.stereotype.Component;
import project.aoop.space_sandbox.camera.GameCamera;
import project.aoop.space_sandbox.entity.Ship;

import java.io.InputStream;

@Component
public class ShipRenderer {
    private Image shipImage;

    @PostConstruct
    public void init() {
        InputStream is = getClass().getResourceAsStream("/sprites/ship.png");
        if (is != null) shipImage = new Image(is);
    }

    public void render(GraphicsContext gc, GameCamera camera, Ship ship, boolean thrusting) {
        double cx = camera.getScreenW() / 2;
        double cy = camera.getScreenH() / 2;

        gc.save();
        gc.translate(cx, cy);

        gc.rotate(Math.toDegrees(ship.getAngle()) - 90);

        if (thrusting) {
            gc.setFill(Color.ORANGE);
            drawFlame(gc, -13, -25, 8, 15);
            drawFlame(gc, 13, -25, 8, 15);
        }

        if (shipImage != null && !shipImage.isError()) {
            double w = 30, h = 36;  // was 50x60
            gc.drawImage(shipImage, -w / 2, -h / 2, w, h);
        } else {
            gc.setFill(Color.web("#aabbcc"));
            gc.beginPath();
            gc.moveTo(12, 0); gc.lineTo(-8, -6);  // smaller triangle too
            gc.lineTo(-5, 0); gc.lineTo(-8,  6);
            gc.closePath();
            gc.fill();
        }

        gc.restore();
    }

    private void drawFlame(GraphicsContext gc, double x, double y, double width, double length) {
        double flicker = Math.random() * 5;

        gc.setFill(Color.ORANGE);
        gc.beginPath();
        gc.moveTo(x - width / 2, y);
        gc.lineTo(x, y - length - flicker);
        gc.lineTo(x + width / 2, y);
        gc.closePath();
        gc.fill();

        gc.setFill(Color.YELLOW);
        gc.beginPath();
        gc.moveTo(x - width / 4, y);
        gc.lineTo(x, y - length * 0.65 - flicker);
        gc.lineTo(x + width / 4, y);
        gc.closePath();
        gc.fill();
    }
}
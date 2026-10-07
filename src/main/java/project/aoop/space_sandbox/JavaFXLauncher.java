package project.aoop.space_sandbox;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import project.aoop.space_sandbox.camera.GameCamera;
import project.aoop.space_sandbox.engine.GameLoop;
import project.aoop.space_sandbox.engine.GameState;
import project.aoop.space_sandbox.input.InputHandler;
import project.aoop.space_sandbox.render.GameRenderer;

public class JavaFXLauncher extends Application {

    private ConfigurableApplicationContext springContext;

    // Runs before window opens — boots Spring so all beans are ready
    @Override
    public void init() {
        springContext = new SpringApplicationBuilder(SpaceSandboxApplication.class).run();
    }

    // Creates and shows the window
    @Override
    public void start(Stage stage) {
        double W = 1280, H = 720;

        // Canvas is the drawing surface
        Canvas canvas = new Canvas(W, H);

        // Pull beans from Spring context
        GameCamera camera = springContext.getBean(GameCamera.class);
        camera.setScreenSize(W, H);

        GameRenderer renderer = springContext.getBean(GameRenderer.class);
        renderer.setCanvas(canvas);

        InputHandler inputHandler = springContext.getBean(InputHandler.class);
        GameLoop gameLoop         = springContext.getBean(GameLoop.class);

        GameState gameState = springContext.getBean(GameState.class);
        gameState.setState(GameState.State.RUNNING);

        // Scene holds canvas and listens for keyboard
        Scene scene = new Scene(new Pane(canvas), W, H);
        inputHandler.attach(scene);

        // Mouse click — minimap expand or planet selection on map
        canvas.setOnMouseClicked(e -> renderer.handleClick(e.getX(), e.getY()));

        // Scroll wheel — zoom in/out
        scene.setOnScroll(e -> {
            if (e.getDeltaY() > 0) camera.adjustZoom(1.1);
            else                   camera.adjustZoom(0.9);
        });

        stage.setTitle("Space Sandbox");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        gameLoop.start(); // begin AnimationTimer — game is now running
    }

    // Runs when window is closed — clean shutdown
    @Override
    public void stop() {
        springContext.close();
        Platform.exit();
    }
}
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

    @Override
    public void init() {
        springContext = new SpringApplicationBuilder(SpaceSandboxApplication.class).run();
    }

    @Override
    public void start(Stage stage) {
        double W = 1280, H = 720;
        Canvas canvas = new Canvas(W, H);
        canvas.setFocusTraversable(true);

        GameCamera camera = springContext.getBean(GameCamera.class);
        camera.setScreenSize(W, H);

        GameRenderer renderer = springContext.getBean(GameRenderer.class);
        renderer.setCanvas(canvas);

        InputHandler inputHandler = springContext.getBean(InputHandler.class);
        GameLoop gameLoop = springContext.getBean(GameLoop.class);

        GameState gameState = springContext.getBean(GameState.class);
        gameState.setState(GameState.State.RUNNING);

        Scene scene = new Scene(new Pane(canvas), W, H);
        inputHandler.attach(scene);
        stage.setScene(scene);
        stage.show();
        canvas.requestFocus();

        gameLoop.start();
    }

    @Override
    public void stop() {
        springContext.close();
        Platform.exit();
    }
}

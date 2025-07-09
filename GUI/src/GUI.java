import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.util.ArrayList;

public class GUI extends Application {
    private Group root;
    private ObservableList<Node> rootList;
    private Scene scene;
    private int idCounter = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Server clientHandler = new Server(this);
        new Thread(clientHandler).start();

        root = new Group();
        this.rootList = root.getChildren();
        scene = new Scene(root, 1000, 1000);
        scene.setFill(Color.WHITESMOKE);
        primaryStage.setTitle("Target Detection Simulation");
        primaryStage.setScene(scene);

        Line line;
        for (int i = 0; i <= 50; i++) {
            line = new Line(0, i * 20, 1000, i * 20);
            line.setStroke(Color.LIGHTSLATEGRAY);
            rootList.add(line);
            line = new Line(i * 20, 0, i * 20, 1000);
            line.setStroke(Color.LIGHTSLATEGRAY);
            rootList.add(line);
        }
        primaryStage.show();
    }

    public String addEnemy(int x, int y) {
        Circle enemy = new Circle(x * 20 + 10, y * 20 + 10,8);
        String id = "enemy-" + idCounter;
        idCounter++;
        enemy.setId(id);
        enemy.setFill(Color.RED);
        Platform.runLater(() -> {
            rootList.add(enemy);
        });
        return id;
    }
    public void moveEntity(String entityID, int x, int y) {
        Platform.runLater(() -> {
            Circle entity = (Circle) scene.lookup("#" + entityID);
            entity.setCenterX(x * 20 + 10);
            entity.setCenterY(y * 20 + 10);
        });
    }

    public void removeEntity(String entityID) {
        Platform.runLater(() -> {
            Circle entity = (Circle) scene.lookup("#" + entityID);
            rootList.remove(entity);
        });
    }
}

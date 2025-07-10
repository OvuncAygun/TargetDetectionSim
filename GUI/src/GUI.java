import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class GUI extends Application {
    private Stage primaryStage;
    private Group backgroundLayer;
    private Group gridLayer;
    private Group entityLayer;
    private Group root;
    private Scene scene;
    private int width;
    private int height;
    private int gridXCount;
    private int gridYCount;
    private int gridXSize;
    private int gridYSize;
    private final AtomicInteger idCounter = new AtomicInteger(0);
    private final Map<String, GUITile> tileMap = new HashMap<>();
    private final Map<String, GUIEntity> entityMap = new HashMap<>();
    private final Map<String, GUIMark> markMap = new HashMap<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        Server server = new Server(this);
        new Thread(server).start();
    }

    public void initialize(int width, int height, int gridXCount, int gridYCount) {
        this.width = width;
        this.height = height;
        this.gridXCount = gridXCount;
        this.gridYCount = gridYCount;
        this.gridXSize = width / gridXCount;
        this.gridYSize = height / gridYCount;
        backgroundLayer = new Group();
        gridLayer = new Group();
        entityLayer = new Group();
        root = new Group(backgroundLayer, gridLayer, entityLayer);
        scene = new Scene(root, width, height);
        scene.setFill(Color.BLACK);
        primaryStage.setTitle("Target Detection Simulation");
        primaryStage.setScene(scene);

        Rectangle rectangle;
        for (int i = 0; i < gridXCount; i++) {
            for (int j = 0; j < gridYCount; j++) {
                rectangle = new Rectangle(i * gridXSize, j * gridYSize, gridXSize, gridYSize);
                String id = "grid-" + i + "-" + j;
                rectangle.setId(id);
                rectangle.setFill(Color.WHITESMOKE);
                rectangle.setStroke(Color.LIGHTSLATEGRAY);
                rectangle.setStrokeWidth(1);
                backgroundLayer.getChildren().add(rectangle);
                tileMap.put(id, new GUITile(id, rectangle));
            }
        }
        primaryStage.show();
    }

    public String addEnemy(int x, int y) {
        Circle enemy = new Circle((x * gridXSize) + ((double) gridXSize / 2), (y * gridYSize) + ((double) gridYSize / 2),8);
        String id = "enemy-" + idCounter.getAndIncrement();
        enemy.setId(id);
        enemy.setFill(Color.RED);
        Platform.runLater(() -> {
            entityLayer.getChildren().add(enemy);
        });
        entityMap.put(id, new GUIEntity(id, enemy));
        return id;
    }
    public String addObserver(int x, int y) {
        Circle observer = new Circle((x * gridXSize) + ((double) gridXSize / 2), (y * gridYSize) + ((double) gridYSize / 2),8);
        String id = "observer-" + idCounter.getAndIncrement();
        observer.setId(id);
        observer.setFill(Color.BLUE);
        Platform.runLater(() -> {
            entityLayer.getChildren().add(observer);
        });
        entityMap.put(id, new GUIEntity(id, observer));
        return id;
    }
    public void moveEntity(String entityID, int x, int y) {
        Circle entity = entityMap.get(entityID).node;
        Platform.runLater(() -> {
            entity.setCenterX((x * gridXSize) + ((double) gridXSize /2));
            entity.setCenterY((y * gridYSize) + ((double) gridYSize /2));
        });
    }

    public void removeEntity(String entityID) {
        Circle entity = entityMap.get(entityID).node;
        Platform.runLater(() -> {
            entityLayer.getChildren().remove(entity);
        });
        entityMap.remove(entityID);
    }

    public String markEntity(String entityID, int x, int y) {
        Rectangle mark = new Rectangle(x * gridXSize, y * gridYSize, gridXSize, gridYSize);
        String id = entityID + idCounter.getAndIncrement();
        mark.setId(id);
        mark.setStroke(Color.BLUE);
        mark.setStrokeWidth(2);
        mark.setFill(Color.TRANSPARENT);
        Platform.runLater(() -> {
            entityLayer.getChildren().add(mark);
        });
        markMap.put(id, new GUIMark(id, mark));
        return id;
    }

    public void removeMark(String markID) {
        Rectangle mark = markMap.get(markID).node;
        Platform.runLater(() -> {
            entityLayer.getChildren().remove(mark);
        });
        markMap.remove(markID);
    }
}

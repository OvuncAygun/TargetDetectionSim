import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class GUI extends Application {
    private Stage primaryStage;
    private final Group root = new Group();
    private Scene scene;
    private final GridPane gridPane = new GridPane();
    private int width;
    private int height;
    private int gridXCount;
    private int gridYCount;
    private int gridXSize;
    private int gridYSize;
    private final AtomicInteger idCounter = new AtomicInteger(0);
    private GUITile[][] grid;
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
        gridXSize = width / gridXCount;
        gridYSize = height / gridYCount;
        grid = new GUITile[gridXCount][gridYCount];
        scene = new Scene(root, width, height);
        scene.setFill(Color.WHITE);
        primaryStage.setTitle("Target Detection Simulation");
        primaryStage.setScene(scene);

        for (int i = 0; i < gridXCount; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(100.0 / gridXCount);
            column.setHgrow(Priority.ALWAYS);
            gridPane.getColumnConstraints().add(column);
        }

        for (int i = 0; i < gridYCount; i++) {
            RowConstraints row = new RowConstraints();
            row.setPercentHeight(100.0 / gridYCount);
            row.setVgrow(Priority.ALWAYS);
            gridPane.getRowConstraints().add(row);
        }

        gridPane.prefWidthProperty().bind(scene.widthProperty());
        gridPane.prefHeightProperty().bind(scene.heightProperty());
        root.getChildren().add(gridPane);

        for (int i = 0; i < gridXCount; i++) {
            for (int j = 0; j < gridYCount; j++) {
                StackPane stackPane = new StackPane();
                gridPane.add(stackPane, i, j);
                String id = "grid-" + i + "-" + j;
                Rectangle rectangle = new Rectangle();
                rectangle.setStrokeType(StrokeType.INSIDE);
                rectangle.setFill(Color.WHITESMOKE);
                rectangle.setStroke(Color.LIGHTSLATEGRAY);
                rectangle.setManaged(false);
                rectangle.widthProperty().bind(stackPane.widthProperty());
                rectangle.heightProperty().bind(stackPane.heightProperty());
                grid[i][j] = new GUITile(id, stackPane, rectangle);
            }
        }

        primaryStage.show();

    }

    public String addEnemy(int x, int y) {
        Circle enemy = new Circle();
        String id = "enemy-" + idCounter.getAndIncrement();
        enemy.setId(id);
        enemy.setFill(Color.RED);
        enemy.setManaged(false);
        GUIEntity guiEntity = new GUIEntity(id, enemy, x, y, 0, grid[x][y].entityLayer);
        entityMap.put(id, guiEntity);
        Platform.runLater(() -> {
            guiEntity.node.radiusProperty().bind(Bindings.createDoubleBinding(
                    () -> (Math.min(grid[x][y].stackPane.getWidth(), grid[x][y].stackPane.getHeight()) * 0.4),
                    grid[x][y].stackPane.widthProperty(),
                    grid[x][y].stackPane.heightProperty()));
            guiEntity.group.getChildren().add(guiEntity.node);
        });
        return id;
    }
    public String addObserver(int x, int y, int scanRange) {
        Circle observer = new Circle();
        String id = "observer-" + idCounter.getAndIncrement();
        observer.setId(id);
        observer.setFill(Color.BLUE);
        observer.setManaged(false);
        GUIEntity guiEntity = new GUIEntity(id, observer, x, y, scanRange, grid[x][y].entityLayer);
        entityMap.put(id, guiEntity);
        Platform.runLater(() -> {
            guiEntity.node.radiusProperty().bind(Bindings.createDoubleBinding(
                    () -> (Math.min(grid[x][y].stackPane.getWidth(), grid[x][y].stackPane.getHeight()) * 0.4),
                    grid[x][y].stackPane.widthProperty(),
                    grid[x][y].stackPane.heightProperty()));
            guiEntity.group.getChildren().add(observer);
        });
        return id;
    }
    public void moveEntity(String entityID, int x, int y) {
        GUIEntity guiEntity = entityMap.get(entityID);
        Platform.runLater(() -> {
            guiEntity.group.getChildren().remove(guiEntity.node);
            guiEntity.node.radiusProperty().unbind();
        });
        guiEntity.group = grid[x][y].entityLayer;
        Platform.runLater(() -> {
            guiEntity.node.radiusProperty().bind(Bindings.createDoubleBinding(
                    () -> (Math.min(grid[x][y].stackPane.getWidth(), grid[x][y].stackPane.getHeight()) * 0.4),
                    grid[x][y].stackPane.widthProperty(),
                    grid[x][y].stackPane.heightProperty()));
            guiEntity.group.getChildren().add(guiEntity.node);
        });
        guiEntity.x = x;
        guiEntity.y = y;
    }

    public void removeEntity(String entityID) {
        GUIEntity entity = entityMap.get(entityID);
        Platform.runLater(() -> {
            entity.group.getChildren().remove(entity.node);
            entity.node.radiusProperty().unbind();
        });
        entityMap.remove(entityID);
        for(int i = 0; i < gridXCount; i++) {
            for (int j = 0; j < gridYCount; j++) {
                GUITile tile = grid[i][j];
                tile.observerSet.remove(entityID);
                tile.checkObserverSet();
            }
        }
    }

    public String markEntity(String entityID, int x, int y) {
        Rectangle mark = new Rectangle();
        String id = entityID + "-" + idCounter.getAndIncrement();
        mark.setId(id);
        mark.setStroke(Color.BLUE);
        mark.setStrokeType(StrokeType.INSIDE);
        mark.setStrokeWidth(2);
        mark.setFill(Color.TRANSPARENT);
        mark.setManaged(false);
        GUIMark guiMark = new GUIMark(id, mark, x, y, grid[x][y].markLayer);
        markMap.put(id, guiMark);
        Platform.runLater(() -> {
            guiMark.node.widthProperty().bind(grid[x][y].stackPane.widthProperty());
            guiMark.node.heightProperty().bind(grid[x][y].stackPane.heightProperty());
            guiMark.group.getChildren().add(guiMark.node);
        });
        return id;
    }

    public void removeMark(String markID) {
        GUIMark guiMark = markMap.get(markID);
        Platform.runLater(() -> {
            guiMark.group.getChildren().remove(guiMark.node);
            guiMark.node.widthProperty().unbind();
            guiMark.node.heightProperty().unbind();
        });
        markMap.remove(markID);
    }

    public void drawRange(String entityID) {
        GUIEntity entity = entityMap.get(entityID);
        for(int i = 0; i < gridXCount; i++) {
            for (int j = 0; j < gridYCount; j++) {
                GUITile tile = grid[i][j];
                tile.observerSet.remove(entityID);
            }
        }
        for(int i = entity.scanRange; i >= -entity.scanRange; i--){
            if(entity.x + i >= 0 && entity.x + i < gridXCount) {
                for(int j = entity.scanRange - Math.abs(i); j >= Math.abs(i) - entity.scanRange; j--){
                    if(entity.y + j >= 0 && entity.y + j < gridYCount) {
                        GUITile tile = grid[entity.x + i][entity.y + j];
                        tile.observerSet.add(entityID);
                    }
                }
            }
        }
        for(int i = 0; i < gridXCount; i++) {
            for (int j = 0; j < gridYCount; j++) {
                GUITile tile = grid[i][j];
                tile.checkObserverSet();
            }
        }
    }
}

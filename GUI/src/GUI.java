import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextBoundsType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class GUI extends Application {
    public boolean running = true;
    private Server server;
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
        server = new Server(this);
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
                grid[i][j] = new GUITile(id, stackPane, rectangle, i, j);
            }
        }

        primaryStage.show();

        EventHandler<WindowEvent> onCloseRequestHandler = new EventHandler<>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                try {
                    server.stop();
                    running = false;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        primaryStage.setOnCloseRequest(onCloseRequestHandler);
    }

    public String addEnemy(int x, int y) {
        GUITile guiTile = grid[x][y];
        Circle enemy = new Circle();
        String id = "enemy-" + idCounter.getAndIncrement();
        enemy.setId(id);
        enemy.setFill(Color.RED);
        enemy.setManaged(false);
        GUIEntity guiEntity = new GUIEntity(id, enemy, x, y, 0, guiTile.entityLayer);
        entityMap.put(id, guiEntity);
        Platform.runLater(() -> {
            guiEntity.node.radiusProperty().bind(Bindings.createDoubleBinding(
                    () -> (Math.min(guiTile.stackPane.getWidth(), guiTile.stackPane.getHeight()) * 0.4),
                    guiTile.stackPane.widthProperty(),
                    guiTile.stackPane.heightProperty()));;
            guiEntity.node.centerXProperty().bind(guiTile.stackPane.widthProperty().multiply(0.5));
            guiEntity.node.centerYProperty().bind(guiTile.stackPane.heightProperty().multiply(0.5));
            guiEntity.group.getChildren().add(guiEntity.node);
        });
        return id;
    }
    public String addObserver(int x, int y, int scanRange) {
        GUITile guiTile = grid[x][y];
        Circle observer = new Circle();
        String id = "observer-" + idCounter.getAndIncrement();
        observer.setId(id);
        observer.setFill(Color.BLUE);
        observer.setManaged(false);
        GUIEntity guiEntity = new GUIEntity(id, observer, x, y, scanRange, guiTile.entityLayer);
        entityMap.put(id, guiEntity);
        Platform.runLater(() -> {
            guiEntity.node.radiusProperty().bind(Bindings.createDoubleBinding(
                    () -> (Math.min(guiTile.stackPane.getWidth(), guiTile.stackPane.getHeight()) * 0.4),
                    guiTile.stackPane.widthProperty(),
                    guiTile.stackPane.heightProperty()));;
            guiEntity.node.centerXProperty().bind(guiTile.stackPane.widthProperty().multiply(0.5));
            guiEntity.node.centerYProperty().bind(guiTile.stackPane.heightProperty().multiply(0.5));
            guiEntity.group.getChildren().add(observer);
        });
        return id;
    }
    public void moveEntity(String entityID, int x, int y) {
        GUITile guiTile = grid[x][y];
        GUIEntity guiEntity = entityMap.get(entityID);
        Platform.runLater(() -> {
            guiEntity.group.getChildren().remove(guiEntity.node);
            guiEntity.node.radiusProperty().unbind();
            guiEntity.node.centerXProperty().unbind();
            guiEntity.node.centerYProperty().unbind();
            guiEntity.group = guiTile.entityLayer;
        });
        Platform.runLater(() -> {
            guiEntity.node.radiusProperty().bind(Bindings.createDoubleBinding(
                    () -> (Math.min(guiTile.stackPane.getWidth(), guiTile.stackPane.getHeight()) * 0.4),
                    guiTile.stackPane.widthProperty(),
                    guiTile.stackPane.heightProperty()));
            guiEntity.node.centerXProperty().bind(guiTile.stackPane.widthProperty().multiply(0.5));
            guiEntity.node.centerYProperty().bind(guiTile.stackPane.heightProperty().multiply(0.5));
            guiEntity.group.getChildren().add(guiEntity.node);
        });
        guiEntity.x = x;
        guiEntity.y = y;
    }

    public void removeEntity(String entityID) {
        GUIEntity guiEntity = entityMap.get(entityID);
        Platform.runLater(() -> {
            guiEntity.group.getChildren().remove(guiEntity.node);
            guiEntity.node.radiusProperty().unbind();
            guiEntity.node.centerXProperty().unbind();
            guiEntity.node.centerYProperty().unbind();
        });
        entityMap.remove(entityID);
        Platform.runLater(() -> {
            for(int i = 0; i < gridXCount; i++) {
                for (int j = 0; j < gridYCount; j++) {
                    GUITile guiTile = grid[i][j];
                    guiTile.observerSet.remove(entityID);
                    if (guiTile.observerSet.isEmpty() && guiTile.node.getFill() == Color.LIGHTBLUE) {
                        guiTile.node.setFill(Color.WHITESMOKE);
                    }
                }
            }
            for (int i = 0; i < guiEntity.markList.size(); i++) {
                GUIMark guiMark = guiEntity.markList.get(i);
                guiMark.group.getChildren().remove(guiMark.node);
                guiMark.node.widthProperty().unbind();
                guiMark.node.heightProperty().unbind();
                guiMark.group.getChildren().remove(guiMark.text);
                guiMark.text.wrappingWidthProperty().unbind();
                guiMark.text.yProperty().unbind();
                markMap.remove(guiMark.id);
            }
        });
    }

    public String markEntity(String markerID, String entityID, int x, int y) {
        GUITile guiTile = grid[x][y];
        String id = markerID + "-" + idCounter.getAndIncrement();
        Rectangle mark = new Rectangle();
        mark.setId(id);
        mark.setStrokeType(StrokeType.INSIDE);
        mark.setFill(Color.TRANSPARENT);
        mark.setStroke(Color.BLUE);
        mark.setStrokeWidth(1.5);
        mark.setManaged(false);
        Text text = new Text(entityID);
        text.setStyle("-fx-font-weight: bold");
        text.setFill(Color.WHITE);
        text.setTextAlignment(TextAlignment.CENTER);
        text.setTextOrigin(VPos.CENTER);
        text.setManaged(false);
        text.setBoundsType(TextBoundsType.LOGICAL_VERTICAL_CENTER);
        GUIMark guiMark = new GUIMark(id, mark, text, x, y, markerID, guiTile.markLayer);
        markMap.put(id, guiMark);
        GUIEntity guiEntity = entityMap.get(markerID);
        guiEntity.markList.add(guiMark);
        Platform.runLater(() -> {
            guiMark.node.widthProperty().bind(guiTile.stackPane.widthProperty());
            guiMark.node.heightProperty().bind(guiTile.stackPane.heightProperty());
            guiMark.group.getChildren().add(guiMark.node);
            guiMark.text.wrappingWidthProperty().bind(guiTile.stackPane.widthProperty());
            guiMark.text.yProperty().bind(guiTile.stackPane.heightProperty().divide(2));
            guiMark.group.getChildren().add(guiMark.text);
        });
        return id;
    }

    public void removeMark(String markID) {
        GUIMark guiMark = markMap.get(markID);
        Platform.runLater(() -> {
            guiMark.group.getChildren().remove(guiMark.node);
            guiMark.node.widthProperty().unbind();
            guiMark.node.heightProperty().unbind();
            guiMark.group.getChildren().remove(guiMark.text);
            guiMark.text.wrappingWidthProperty().unbind();
            guiMark.text.yProperty().unbind();
        });
        markMap.remove(markID);
    }

    public void drawRange(String entityID) {
        GUIEntity entity = entityMap.get(entityID);
        for(int i = 0; i < gridXCount; i++) {
            for (int j = 0; j < gridYCount; j++) {
                GUITile guiTile = grid[i][j];
                guiTile.observerSet.remove(entityID);
            }
        }
        for(int i = entity.scanRange; i >= -entity.scanRange; i--){
            if(entity.x + i >= 0 && entity.x + i < gridXCount) {
                int jValue = (int) Math.round(Math.sqrt((Math.pow(entity.scanRange + 0.5, 2) - Math.pow(i, 2))) - 0.5);
                for(int j = jValue;
                    j >= -jValue;
                    j--){
                    if(entity.y + j >= 0 && entity.y + j < gridYCount) {
                        GUITile guiTile = grid[entity.x + i][entity.y + j];
                        guiTile.observerSet.add(entityID);
                    }
                }
            }
        }
        Platform.runLater(() -> {
            for(int i = 0; i < gridXCount; i++) {
                for (int j = 0; j < gridYCount; j++) {
                    GUITile guiTile = grid[i][j];
                    if (guiTile.observerSet.isEmpty() && guiTile.node.getFill() == Color.LIGHTBLUE) {
                        guiTile.node.setFill(Color.WHITESMOKE);
                    }
                    else if (!guiTile.observerSet.isEmpty() && guiTile.node.getFill() == Color.WHITESMOKE) {
                        guiTile.node.setFill(Color.LIGHTBLUE);
                    }
                }
            }
        });
    }
}

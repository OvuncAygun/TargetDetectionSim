import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class GUI extends Application {
    private Stage primaryStage;
    private Group root;
    private ObservableList<Node> rootList;
    private Scene scene;
    private int idCounter = 0;
    private int width;
    private int height;
    private int gridXCount;
    private int gridYCount;
    private int gridXSize;
    private int gridYSize;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        Server clientHandler = new Server(this);
        new Thread(clientHandler).start();
    }

    public void initialize(int width, int height, int gridXCount, int gridYCount) {
        this.width = width;
        this.height = height;
        this.gridXCount = gridXCount;
        this.gridYCount = gridYCount;
        this.gridXSize = width / gridXCount;
        this.gridYSize = width / gridYCount;
        root = new Group();
        this.rootList = root.getChildren();
        scene = new Scene(root, width, height);
        scene.setFill(Color.WHITESMOKE);
        primaryStage.setTitle("Target Detection Simulation");
        primaryStage.setScene(scene);

        Line line;
        for (int i = 0; i <= gridXCount; i++) {
            line = new Line(0, i * gridXSize, width, i * gridXSize);
            line.setStroke(Color.LIGHTSLATEGRAY);
            rootList.add(line);
        }
        for (int i = 0; i <= gridYCount; i++) {
            line = new Line(i * gridYSize, 0, i * gridYSize, height);
            line.setStroke(Color.LIGHTSLATEGRAY);
            rootList.add(line);
        }
        primaryStage.show();
    }

    public String addEnemy(int x, int y) {
        Circle enemy = new Circle((x * gridXSize) + ((double) gridXSize / 2), (y * gridYSize) + ((double) gridYSize / 2),8);
        String id = "enemy-" + idCounter;
        idCounter++;
        enemy.setId(id);
        enemy.setFill(Color.RED);
        Platform.runLater(() -> {
            rootList.add(enemy);
        });
        return id;
    }
    public String addObserver(int x, int y) {
        Circle observer = new Circle((x * gridXSize) + ((double) gridXSize / 2), (y * gridYSize) + ((double) gridYSize / 2),8);
        String id = "observer-" + idCounter;
        idCounter++;
        observer.setId(id);
        observer.setFill(Color.BLUE);
        Platform.runLater(() -> {
            rootList.add(observer);
        });
        return id;
    }
    public void moveEntity(String entityID, int x, int y) {
        Platform.runLater(() -> {
            Circle entity = (Circle) scene.lookup("#" + entityID);
            entity.setCenterX((x * gridXSize) + ((double) gridXSize /2));
            entity.setCenterY((y * gridYSize) + ((double) gridYSize /2));
        });
    }

    public void removeEntity(String entityID) {
        Platform.runLater(() -> {
            Circle entity = (Circle) scene.lookup("#" + entityID);
            rootList.remove(entity);
        });
    }

    public String markEntity(String entityID, int x, int y) {
        Rectangle mark = new Rectangle(x * gridXSize, y * gridYSize, gridXSize, gridYSize);
        String id = entityID + idCounter;
        idCounter++;
        mark.setId(id);
        mark.setStroke(Color.BLUE);
        mark.setStrokeWidth(2);
        mark.setFill(Color.TRANSPARENT);
        Platform.runLater(() -> {
            rootList.add(mark);
        });
        return id;
    }

    public void removeMark(String markID) {
        Platform.runLater(() -> {
            Rectangle mark = (Rectangle) scene.lookup("#" + markID);
            rootList.remove(mark);
        });
    }


}

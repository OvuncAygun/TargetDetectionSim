import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.util.ArrayList;

public class GUI extends Application {
    private final ArrayList<int[]> enemyLocations = new ArrayList<>();
    private ObservableList<Node> rootList;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Server clientHandler = new Server(this);
        new Thread(clientHandler).start();

        Group root = new Group();
        this.rootList = root.getChildren();
        Scene scene = new Scene(root, 1000, 1000);
        primaryStage.setTitle("Target Detection Simulation");
        primaryStage.setScene(scene);

        for (int i = 0; i <= 50; i++) {
            rootList.add(new Line(0, 20 * i, 1000, 20 * i));
            rootList.add(new Line(20 * i, 0, 20 * i, 1000));
        }
        primaryStage.show();
    }

    public int addEnemy(int x, int y) {
        enemyLocations.add(new int[] {x, y});
        int enemyID = enemyLocations.size() - 1;
        return enemyID;
    }
}

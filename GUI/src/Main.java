import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Group root = new Group();
        ObservableList<Node> rootList = root.getChildren();
        Scene scene = new Scene(root, 1000, 1000);
        primaryStage.setTitle("Target Detection Simulation");
        primaryStage.setScene(scene);

        for(int i = 0; i <= 50; i++) {
            Line line = new Line();
            line.setStartX(0);
            line.setEndX(1000);
            line.setStartY(20*i);
            line.setEndY(20*i);
            rootList.add(line);
            line = new Line();
            line.setStartX(20*i);
            line.setEndX(20*i);
            line.setStartY(0);
            line.setEndY(1000);
            rootList.add(line);
        }
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        Group root = new Group();
        ObservableList<Node> rootList = root.getChildren();
        Scene scene = new Scene(root, 1000, 1000);
        primaryStage.setTitle("Target Detection Simulation");
        primaryStage.setScene(scene);

        for(int i = 0; i <= 50; i++) {
            rootList.add(new Line(0, 20*i, 1000, 20*i));
            rootList.add(new Line(20*i, 0, 20*i, 1000));
        }
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.HashSet;
import java.util.Set;

public class GUITile {
    public String id;
    public StackPane stackPane;
    public Rectangle node;
    public int x;
    public int y;
    public final Set<String> observerSet = new HashSet<>();
    public Group entityLayer = new Group();
    public Group markLayer = new Group();

    public GUITile(String id, StackPane stackPane, Rectangle node, int x, int y) {
        this.id = id;
        this.stackPane = stackPane;
        this.node = node;
        this.x = x;
        this.y = y;
        node.setId("rectangle(%d,%d)".formatted(x, y));
        entityLayer.setId("entityLayer[%d,%d]".formatted(x, y));
        markLayer.setId("markLayer[%d,%d]".formatted(x, y));
        entityLayer.setManaged(false);
        markLayer.setManaged(false);
        stackPane.getChildren().addAll(node, entityLayer, markLayer);
    }

    public void checkObserverSet() {
        if (observerSet.isEmpty()) {
            Platform.runLater(() -> {
                node.setFill(Color.WHITESMOKE);
            });
        }
        else {
            Platform.runLater(() -> {
                node.setFill(Color.LIGHTBLUE);
            });
        }
    }

}

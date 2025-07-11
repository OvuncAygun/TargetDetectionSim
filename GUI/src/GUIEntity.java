import javafx.scene.Group;
import javafx.scene.shape.Circle;

import java.util.ArrayList;

public class GUIEntity {
    public String id;
    public Circle node;
    public int x;
    public int y;
    public final int scanRange;
    public Group group;
    public ArrayList<GUIMark> markList = new ArrayList<>();

    public GUIEntity(String id, Circle node, int x, int y, int scanRange, Group group) {
        this.id = id;
        this.node = node;
        this.x = x;
        this.y = y;
        this.scanRange = scanRange;
        this.group = group;
    }
}

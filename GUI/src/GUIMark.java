import javafx.scene.Group;
import javafx.scene.shape.Rectangle;

public class GUIMark {
    public String id;
    public Rectangle node;
    public int x;
    public int y;
    public Group group;

    public GUIMark(String id, Rectangle node, int x, int y, Group group) {
        this.id = id;
        this.node = node;
        this.x = x;
        this.y = y;
        this.group = group;
    }
}

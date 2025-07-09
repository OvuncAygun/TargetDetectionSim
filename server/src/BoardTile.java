import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class BoardTile {
    public int x;
    public int y;
    public boolean traversable;
    public Set<Entity> tileEntities = new HashSet<>();

    public BoardTile(int x, int y, boolean traversable) {
        this.x = x;
        this.y = y;
        this.traversable = traversable;
    }
}

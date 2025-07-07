import java.util.ArrayList;

public class BoardTile {
    public int x;
    public int y;
    public boolean traversable;
    public ArrayList<Entity> tileEntities = new ArrayList<>();

    public BoardTile(int x, int y, boolean traversable) {
        this.x = x;
        this.y = y;
        this.traversable = traversable;
    }
}

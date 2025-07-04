import java.util.ArrayList;

public class BoardTile {
    public int[] tileCoordinates = new int[2];
    public ArrayList<Entity> tileEntities = new ArrayList<>();

    public BoardTile(int x, int y) {
        this.tileCoordinates[0] = x;
        this.tileCoordinates[1] = y;
    }
}

import java.util.ArrayList;

public class BoardTile {
    public int x;
    public int y;
    public boolean discovered = false;
    public boolean passable;

    public BoardTile(int x, int y, boolean passable) {
        this.x = x;
        this.y = y;
        this.passable = passable;
    }


}

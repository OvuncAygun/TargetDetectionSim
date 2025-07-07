public class BoardTile {
    public int x;
    public int y;
    public boolean discovered = false;
    public boolean traversable;

    public BoardTile(int x, int y) {
        this.x = x;
        this.y = y;
    }


}

public class Board {
    public int xSize;
    public int ySize;
    public BoardTile[][] boardTiles;

    public Board(int xSize, int ySize) {
        this.xSize = xSize;
        this.ySize = ySize;
        boardTiles = new BoardTile[xSize][ySize];
        for(int i = 0; i < xSize; i++) {
            for(int j = 0; j < ySize; j++) {
                boardTiles[i][j] = new BoardTile(i, j, true);
            }
        }
    }

    public BoardTile getBoardTile(int x, int y) {
        return boardTiles[x][y];
    }
}

import java.util.ArrayList;

public class Board {
    public int xSize;
    public int ySize;
    public BoardTile[][] boardTiles;
    public ArrayList<BoardTile> discoveredTiles = new ArrayList<>();
    public ArrayList<DiscoveredEntity> discoveredEntities = new ArrayList<>();
    public int identificationCounter = 0;

    public Board(int xSize, int ySize) {
        this.xSize = xSize;
        this.ySize = ySize;
        boardTiles = new BoardTile[xSize][ySize];
        for(int i = 0; i < xSize; i++) {
            for(int j = 0; j < ySize; j++) {
                boardTiles[i][j] = new BoardTile(i, j);
            }
        }
    }

    public BoardTile getBoardTile(int x, int y) {
        return boardTiles[x][y];
    }

    public void discoverBoardTile(int x, int y, boolean traversable) {
        BoardTile boardTile = boardTiles[x][y];
        boardTile.discovered = true;
        boardTile.traversable = traversable;
        discoveredTiles.add(boardTile);
    }


}

public class Board {
    public int[] boardSize = new int[2];
    public BoardTile[][] boardTiles;

    public Board(int boardSizeX, int boardSizeY) {
        this.boardSize[0] = boardSizeX;
        this.boardSize[1] = boardSizeY;
        boardTiles = new BoardTile[boardSize[0]][boardSize[1]];
        for(int i = 0; i < boardSize[0]; i++) {
            for(int j = 0; j < boardSize[1]; j++) {
                boardTiles[i][j] = new BoardTile(i, j, true);
            }
        }
    }
}

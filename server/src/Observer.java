import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class Observer implements Entity{
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;
    private final Board board;
    private final GUI gui;
    private final String guiID;
    public String name;
    private int x;
    private int y;
    private final Set<String> markIDSet = new HashSet<>();

    public Observer(DataInputStream inputStream, DataOutputStream outputStream, Board board, GUI gui) throws IOException {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.board = board;
        this.gui = gui;
        this.name = inputStream.readUTF();
        this.x = inputStream.readInt();
        this.y = inputStream.readInt();
        while (!board.getBoardTile(x, y).traversable){
            outputStream.writeBoolean(false);
            this.x = inputStream.readInt();
            this.y = inputStream.readInt();
        }
        outputStream.writeBoolean(true);
        int scanRange = inputStream.readInt();
        board.getBoardTile(x, y).tileEntities.add(this);
        this.guiID = gui.addObserver(x, y, scanRange);
        gui.drawRangeCircular(guiID);
    }

    public void move() throws IOException {
        board.getBoardTile(x, y).tileEntities.remove(this);
        x = inputStream.readInt();
        y = inputStream.readInt();
        board.getBoardTile(x, y).tileEntities.add(this);
        gui.moveEntity(guiID, x, y);
        gui.drawRangeCircular(guiID);
    }

    public void discover() throws IOException {
        int size = inputStream.readInt();
        StringBuilder data = new StringBuilder();
        for(int i = size; i >= -size; i--){
            if(x + i >= 0 && x + i < board.xSize) {
                for(int j = size - Math.abs(i); j >= Math.abs(i) - size; j--){
                    if(y + j >= 0 && y + j < board.ySize) {
                        BoardTile boardTile = board.getBoardTile(x + i,y + j);
                        data.append(boardTile.traversable ? 1 : 0);
                    }
                }
            }
        }
        outputStream.writeUTF(data.toString());
    }

    public void scan() throws IOException {
        clearMarks();
        int scanRange = inputStream.readInt();
        StringBuilder data = new StringBuilder();
        for(int i = scanRange; i >= -scanRange; i--){
            if(x + i >= 0 && x + i < board.xSize) {
                for(int j = scanRange - Math.abs(i); j >= Math.abs(i) - scanRange; j--){
                    if(y + j >= 0 && y + j < board.ySize) {
                        BoardTile boardTile = board.getBoardTile(x + i,y + j);
                        data.append(boardTile.tileEntities.isEmpty() ? 0 : 1);
                    }
                }
            }
        }
        outputStream.writeUTF(data.toString());
    }

    public void scanCircular() throws IOException {
        clearMarks();
        int scanRange = inputStream.readInt();
        StringBuilder data = new StringBuilder();
        for(int i = scanRange; i >= -scanRange; i--){
            if(x + i >= 0 && x + i < board.xSize) {
                int jValue = (int) Math.round(Math.sqrt((Math.pow(scanRange + 0.5, 2) - Math.pow(i, 2))) - 0.5);
                for(int j = jValue;
                    j >= -jValue;
                    j--){
                    if(y + j >= 0 && y + j < board.ySize) {
                        BoardTile boardTile = board.getBoardTile(x + i,y + j);
                        data.append(boardTile.tileEntities.isEmpty() ? 0 : 1);
                    }
                }
            }
        }
        outputStream.writeUTF(data.toString());
    }

    public void mark() throws IOException {
        int x = inputStream.readInt();
        int y = inputStream.readInt();
        markIDSet.add(gui.markEntity(guiID, x, y));
    }

    public void clearMarks() {
        for(String markID : markIDSet) {
            gui.removeMark(markID);
        }
        markIDSet.clear();
    }

    public void remove() {
        board.getBoardTile(x, y).tileEntities.remove(this);
        gui.removeEntity(guiID);
    }
}

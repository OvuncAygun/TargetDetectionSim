import javax.imageio.IIOException;
import java.io.*;

public class Enemy implements Entity{
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;
    public String name;
    private int x;
    private int y;
    public Board board;

    public Enemy(DataInputStream inputStream, DataOutputStream outputStream, Board board) throws IOException {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.board = board;
        this.name = inputStream.readUTF();
        this.x = inputStream.readInt();
        this.y = inputStream.readInt();
        outputStream.writeBoolean(true);
    }

    public void move() throws IOException {
        x = inputStream.readInt();
        y = inputStream.readInt();
    }

    public void discover() throws IOException {
        int size = inputStream.readInt();
        for(int i = size; i >= -size; i--){
            for(int j = size - Math.abs(i); j >= Math.abs(i) - size; j--){
                BoardTile boardTile = board.getBoardTile(x + i,y + j);
                outputStream.writeBoolean(boardTile.traversable);
            }
        }
    }
}

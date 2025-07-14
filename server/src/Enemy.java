import java.io.*;
import java.nio.ByteBuffer;

public class Enemy implements Entity{
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;
    private final Board board;
    private final GUI gui;
    private final String guiID;
    public String name;
    private int x;
    private int y;

    public Enemy(DataInputStream inputStream, DataOutputStream outputStream, Board board, GUI gui) throws IOException {
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
        board.getBoardTile(x, y).tileEntities.add(this);
        this.guiID = gui.addEnemy(x, y);
    }

    public void move() throws IOException {
        board.getBoardTile(x, y).tileEntities.remove(this);
        x = inputStream.readInt();
        y = inputStream.readInt();
        board.getBoardTile(x, y).tileEntities.add(this);
        gui.moveEntity(guiID, x, y);
    }

    public void discover() throws IOException {
        int coordinateCount = inputStream.readInt();
        ByteBuffer inputByteBuffer = ByteBuffer.wrap(inputStream.readNBytes(coordinateCount * (2 * Integer.BYTES)));
        ByteBuffer outputByteBuffer = ByteBuffer.allocate(coordinateCount * (2 * Integer.BYTES + 1));
        while (inputByteBuffer.hasRemaining()) {
            int x = inputByteBuffer.getInt();
            int y = inputByteBuffer.getInt();
            BoardTile boardTile = board.getBoardTile(x,y);
            outputByteBuffer.putInt(x);
            outputByteBuffer.putInt(y);
            outputByteBuffer.put(boardTile.traversable ? (byte) 1 : (byte) 0);
        }
        outputStream.write(outputByteBuffer.array());
    }

    public void scan() throws IOException {
        System.out.println("Enemy does not have \"scan\" method");
    }

    public void mark() throws IOException {
        System.out.println("Enemy does not have \"mark\" method");
    }

    public void remove() {
        board.getBoardTile(x, y).tileEntities.remove(this);
        gui.removeEntity(guiID);
    }
}

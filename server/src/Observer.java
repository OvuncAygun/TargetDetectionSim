import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

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
        outputStream.writeUTF(this.guiID);
        gui.drawRange(guiID);
    }

    public void move() throws IOException {
        board.getBoardTile(x, y).tileEntities.remove(this);
        x = inputStream.readInt();
        y = inputStream.readInt();
        board.getBoardTile(x, y).tileEntities.add(this);
        gui.moveEntity(guiID, x, y);
        gui.drawRange(guiID);
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
        clearMarks();
        int coordinateCount = inputStream.readInt();
        ByteBuffer inputByteBuffer = ByteBuffer.wrap(inputStream.readNBytes(coordinateCount * (2 * Integer.BYTES)));
        ByteBuffer outputByteBuffer = ByteBuffer.allocate(coordinateCount * (2 * Integer.BYTES + 1));
        while (inputByteBuffer.hasRemaining()) {
            int x = inputByteBuffer.getInt();
            int y = inputByteBuffer.getInt();
            BoardTile boardTile = board.getBoardTile(x,y);
            outputByteBuffer.putInt(x);
            outputByteBuffer.putInt(y);
            outputByteBuffer.put(boardTile.tileEntities.isEmpty() ? (byte) 0 : (byte) 1);
        }
        outputStream.write(outputByteBuffer.array());
    }

    public void mark() throws IOException {
        String entityID = inputStream.readUTF();
        int x = inputStream.readInt();
        int y = inputStream.readInt();
        markIDSet.add(gui.markEntity(guiID, entityID, x, y));
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

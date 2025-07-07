import javax.xml.stream.FactoryConfigurationError;
import java.io.*;
import java.util.Random;

public class NormalEnemy implements Enemy {
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;
    private final Board board;
    private int x;
    private int y;
    private final Random random = new Random(System.currentTimeMillis());
    private final int movement = 2;

    public NormalEnemy(DataInputStream inputStream, DataOutputStream outputStream, Board board) throws IOException {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.board = board;
        outputStream.writeUTF("normalEnemy");
        outputStream.writeUTF("enemyName");
        do {
            x = random.nextInt(0, 100);
            y = random.nextInt(0, 100);
            outputStream.writeInt(x);
            outputStream.writeInt(y);
        }
        while (!inputStream.readBoolean());
    }

    public void move() throws IOException {
        this.discover(movement);
        outputStream.writeUTF("MOVE");
        int x = random.nextInt(0, 5) - 2;
        int y = random.nextInt(0, 5 - (2 * Math.abs(x))) - 2 + Math.abs(x);
        outputStream.writeInt(x);
        outputStream.writeInt(y);
    }

    public void discover(int size) throws IOException {
        outputStream.writeUTF("DISCOVER");
        outputStream.writeInt(size);
        for(int i = size; i >= -size; i--){
            for(int j = size - Math.abs(i); j >= Math.abs(i) - size; i--){
                boolean traversable = inputStream.readBoolean();
                board.discoverBoardTile(x + i, y + j, traversable);
            }
        }
    }

}

import javax.xml.stream.FactoryConfigurationError;
import java.io.*;
import java.util.Random;

public class NormalEnemy implements Enemy {
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;
    private final Random random = new Random(System.currentTimeMillis());

    public NormalEnemy(DataInputStream inputStream, DataOutputStream outputStream) throws IOException {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        outputStream.writeUTF("normalEnemy");
        outputStream.writeUTF("enemyName");
        do {
            outputStream.writeInt(random.nextInt(0, 100));
            outputStream.writeInt(random.nextInt(0, 100));
        }
        while (!inputStream.readBoolean());
    }

    public void move() throws IOException {
        outputStream.writeUTF("MOVE");
        int x = random.nextInt(0, 5) - 2;
        int y = random.nextInt(0, 5 - (2 * Math.abs(x))) - 2 + Math.abs(x);
        outputStream.writeInt(x);
        outputStream.writeInt(y);
    }

    public void discover(int size) throws IOException {
        outputStream.writeUTF("DISCOVER");
        outputStream.writeInt(size);
    }

}

import java.io.*;
import java.util.Random;

public class Enemy1 implements Enemy {
    private int[] coordinates = new int[2];
    private final DataOutputStream outputStream;
    private final Random random = new Random(System.currentTimeMillis());

    public Enemy1(int x, int y, DataOutputStream outputStream) throws IOException {
        this.coordinates[0] = x;
        this.coordinates[1] = y;
        this.outputStream = outputStream;
        outputStream.writeUTF("enemy1");
        outputStream.writeUTF("enemyName");
    }

    public void move() throws IOException {
        outputStream.writeUTF("MOVE");
        int x = random.nextInt(0, 3);
        int y = 2 - x;
        outputStream.writeInt(x);
        outputStream.writeInt(y);
    }

}

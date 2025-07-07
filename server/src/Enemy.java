import javax.imageio.IIOException;
import java.io.*;

public class Enemy implements Entity{
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;
    public String name;
    private int x;
    private int y;

    public Enemy(DataInputStream inputStream, DataOutputStream outputStream) throws IOException {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.name = inputStream.readUTF();
        this.x = inputStream.readInt();
        this.y = inputStream.readInt();
        outputStream.writeBoolean(true);
    }

    public void move() throws IOException {
        int x = inputStream.readInt();
        int y = inputStream.readInt();
        System.out.printf("Moved [%d, %d]\n", x, y);
    }

    public void discover(int size) throws IIOException {

    }
}

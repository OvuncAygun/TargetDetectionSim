import java.io.*;

public class Enemy implements Entity{
    private final DataInputStream inputStream;
    public String name;

    public Enemy(DataInputStream inputStream) throws IOException {
        this.inputStream = inputStream;
        this.name = inputStream.readUTF();
    }

    public void move() throws IOException {
        int x = inputStream.readInt();
        int y = inputStream.readInt();
        System.out.printf("Moved [%d, %d]\n", x, y);
    }
}

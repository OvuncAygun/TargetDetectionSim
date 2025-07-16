import java.io.IOException;

public interface Observer {
    void move() throws IOException;

    boolean findPath(int x, int y);

    void discover(int size) throws IOException;

    void scan() throws IOException;
}

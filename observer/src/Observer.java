import java.io.IOException;

public interface Observer {
    void move() throws IOException;

    void discover(int size) throws IOException;

    void scan() throws IOException;

    void markEntities() throws IOException;

    boolean findPath(int x, int y);
}

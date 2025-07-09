import java.io.*;

public interface Entity {
    void move() throws IOException;

    void discover() throws IOException;

    void scan() throws IOException;

    void mark() throws IOException;

    void remove();
}

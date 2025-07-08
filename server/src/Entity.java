import java.io.*;

public interface Entity {
    int[] coordinates = new int[2];

    void move() throws IOException;

    void discover() throws IOException;
}

import java.io.IOException;

public interface Entity {
    int[] coordinates = new int[2];

    void move() throws IOException;
}

import java.io.IOException;

public interface Enemy {
    int[] coordinates = new int[2];

    void move() throws IOException;

    void discover(int size) throws IOException;
}

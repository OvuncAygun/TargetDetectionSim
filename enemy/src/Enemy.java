import java.io.IOException;

public interface Enemy {
    int[] coordinates = new int[2];

    public void move() throws IOException;
}

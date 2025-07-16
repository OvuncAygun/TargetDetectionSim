import java.util.Deque;
import java.util.ArrayDeque;

public class DiscoveredEntity {
    public int x;
    public int y;
    public boolean identified = false;
    public int lostCounter = 0;
    public String id = "";
    public int prevX = -1;
    public int prevY = -1;
    public Deque<int[]> coordinateQueue = new ArrayDeque<>();

    public DiscoveredEntity (int x, int y) {
        this.x = x;
        this.y = y;
        coordinateQueue.addLast(new int[] {x, y});
    }

    public void updateCoordinate(int x, int y) {
        if(x != prevX && y != prevY) {
            prevX = this.x;
            prevY = this.y;
        }
        this.x = x;
        this.y = y;
        coordinateQueue.addLast(new int [] {x, y});
        while (coordinateQueue.size() > 3) {
            coordinateQueue.removeFirst();
        }

    }

}

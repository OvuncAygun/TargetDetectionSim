import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

public class NormalObserver implements Observer {
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;
    private final Board board;
    private int x;
    private int y;
    private int targetX;
    private int targetY;
    private final Random random = new Random(System.currentTimeMillis());
    private final static int visionRange = 10;
    private final static int scanRange = 20;
    private final Deque<int[]> path = new ArrayDeque<>();

    public NormalObserver(DataInputStream inputStream, DataOutputStream outputStream, Board board) throws IOException {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.board = board;
        outputStream.writeUTF("observer");
        outputStream.writeUTF("observerName");
        do {
            x = random.nextInt(0, board.xSize);
            y = random.nextInt(0, board.ySize);
            outputStream.writeInt(x);
            outputStream.writeInt(y);
        }
        while (!inputStream.readBoolean());
        outputStream.writeInt(scanRange);
    }

    public void move() throws IOException {
        this.discover(visionRange);
        outputStream.writeUTF("MOVE");
        if(path.isEmpty()) {
            do {
                int[] target = board.discoveredTiles.get(random.nextInt(0, board.discoveredTiles.size()));
                targetX = target[0];
                targetY = target[1];
            }
            while (!findPath(targetX, targetY));
        }
        int[] movement = path.removeFirst();
        x = movement[0];
        y = movement[1];
        outputStream.writeInt(x);
        outputStream.writeInt(y);


    }

    public void discover(int size) throws IOException {
        outputStream.writeUTF("DISCOVER");
        outputStream.writeInt(size);
        String data = inputStream.readUTF();
        int index = 0;
        for(int i = size; i >= -size; i--){
            if(x + i >= 0 && x + i < board.xSize) {
                for(int j = size - Math.abs(i); j >= Math.abs(i) - size; j--){
                    if(y + j >= 0 && y + j < board.ySize) {
                        boolean traversable = data.charAt(index) == '1';
                        index++;
                        board.discoverBoardTile(x + i, y + j, traversable);
                    }
                }
            }
        }
    }

    public void scan() throws IOException {
        outputStream.writeUTF("SCAN");
        outputStream.writeInt(scanRange);
        String data = inputStream.readUTF();
        board.discoveredEntities = new ArrayList<>();
        int index = 0;
        for(int i = scanRange; i >= -scanRange; i--){
            if(x + i >= 0 && x + i < board.xSize) {
                for(int j = scanRange - Math.abs(i); j >= Math.abs(i) - scanRange; j--){
                    if(y + j >= 0 && y + j < board.ySize) {
                        if(data.charAt(index) == '1') {
                            board.discoveredEntities.add(new int[] {x + i, y + j});
                        }
                        index++;
                    }
                }
            }
        }
        markEntities();
    }

    public void markEntities() throws IOException {
        for (int[] entity : board.discoveredEntities) {
            outputStream.writeUTF("MARK");
            outputStream.writeInt(entity[0]);
            outputStream.writeInt(entity[1]);
        }
    }

    public boolean findPath(int targetX, int targetY) {
        Queue<Point> queue = new ArrayDeque<>();
        Set<Point> visited = new HashSet<>();
        Map<Point, Point> pathMap = new HashMap<>();
        Point currentPoint = new Point(x, y);
        queue.add(currentPoint);
        visited.add(currentPoint);
        while(!queue.isEmpty()) {
            currentPoint = queue.remove();
            Point point = new Point(currentPoint.x + 1, currentPoint.y);
            if(currentPoint.x + 1 < board.xSize && board.getBoardTile(currentPoint.x + 1, currentPoint.y).traversable && !visited.contains(point)) {
                queue.add(point);
                visited.add(point);
                pathMap.put(point, currentPoint);
            }
            point = new Point(currentPoint.x, currentPoint.y + 1);
            if(currentPoint.y + 1 < board.ySize && board.getBoardTile(currentPoint.x, currentPoint.y + 1).traversable && !visited.contains(point)) {
                queue.add(point);
                visited.add(point);
                pathMap.put(point, currentPoint);
            }
            point = new Point(currentPoint.x - 1, currentPoint.y);
            if(currentPoint.x - 1 >= 0 && board.getBoardTile(currentPoint.x - 1, currentPoint.y).traversable && !visited.contains(point)) {
                queue.add(point);
                visited.add(point);
                pathMap.put(point, currentPoint);
            }
            point = new Point(currentPoint.x, currentPoint.y - 1);
            if(currentPoint.y - 1 >= 0 && board.getBoardTile(currentPoint.x, currentPoint.y - 1).traversable && !visited.contains(point)) {
                queue.add(point);
                visited.add(point);
                pathMap.put(point, currentPoint);
            }
        }
        Point pathPoint = new Point(targetX, targetY);
        while(pathMap.containsKey(pathPoint)) {
            path.addFirst(new int[] {pathPoint.x, pathPoint.y});
            pathPoint = pathMap.get(pathPoint);
        }
        return pathMap.containsKey(new Point(targetX, targetY));
    }
}
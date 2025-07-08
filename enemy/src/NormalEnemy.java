import java.awt.*;
import java.io.*;
import java.util.*;

public class NormalEnemy implements Enemy {
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;
    private final Board board;
    private int x;
    private int y;
    private int targetX;
    private int targetY;
    private final Random random = new Random(System.currentTimeMillis());
    private final int movement = 2;

    public NormalEnemy(DataInputStream inputStream, DataOutputStream outputStream, Board board) throws IOException {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.board = board;
        outputStream.writeUTF("normalEnemy");
        outputStream.writeUTF("enemyName");
        do {
            x = random.nextInt(0, 100);
            y = random.nextInt(0, 100);
            outputStream.writeInt(x);
            outputStream.writeInt(y);
        }
        while (!inputStream.readBoolean());
    }

    public void move() throws IOException {
        this.discover(movement);
        outputStream.writeUTF("MOVE");
        int targetX;
        int targetY;
        do {
            int[] target = board.discoveredTiles.get(random.nextInt(0, board.discoveredTiles.size()));
            targetX = target[0];
            targetY = target[1];
        }
        while (!findPath(targetX, targetY));

        x = targetX;
        y = targetY;
        outputStream.writeInt(x);
        outputStream.writeInt(y);


    }

    public void discover(int size) throws IOException {
        outputStream.writeUTF("DISCOVER");
        outputStream.writeInt(size);
        for(int i = size; i >= -size; i--){
            for(int j = size - Math.abs(i); j >= Math.abs(i) - size; j--){
                boolean traversable = inputStream.readBoolean();
                board.discoverBoardTile(x + i, y + j, traversable);
            }
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
            System.out.printf("%d, %d\n", pathPoint.x, pathPoint.y);
            pathPoint = pathMap.get(pathPoint);
        }
        System.out.printf("%d, %d\n\n\n", pathPoint.x, pathPoint.y);

        return pathMap.containsKey(new Point(targetX, targetY));
    }
}
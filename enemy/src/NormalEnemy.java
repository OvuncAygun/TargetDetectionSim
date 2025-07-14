import java.awt.*;
import java.io.*;
import java.nio.ByteBuffer;
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
    private final static int visionRange = 10;
    private final Deque<int[]> path = new ArrayDeque<>();

    public NormalEnemy(DataInputStream inputStream, DataOutputStream outputStream, Board board) throws IOException {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.board = board;
        outputStream.writeUTF("enemy");
        outputStream.writeUTF("enemyName");
        do {
            x = random.nextInt(0, board.xSize);
            y = random.nextInt(0, board.ySize);
            outputStream.writeInt(x);
            outputStream.writeInt(y);
        }
        while (!inputStream.readBoolean());
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

    private int findCircleCoordinates(ArrayList<Integer> coordinateArray, int radius) {
        int coordinateCount = 0;
        for(int i = radius; i >= -radius; i--){
            if(x + i >= 0 && x + i < board.xSize) {
                int jValue = (int) Math.round(Math.sqrt((Math.pow(radius + 0.5, 2) - Math.pow(i, 2))) - 0.5);
                for(int j = jValue;
                    j >= -jValue;
                    j--){
                    if(y + j >= 0 && y + j < board.ySize) {
                        coordinateArray.add(x + i);
                        coordinateArray.add(y + j);
                        coordinateCount++;
                    }
                }
            }
        }
        return coordinateCount;
    }

    public void discover(int size) throws IOException {
        outputStream.writeUTF("DISCOVER");
        ArrayList<Integer> coordinateArray = new ArrayList<>();
        int coordinateCount = findCircleCoordinates(coordinateArray, size);
        ByteBuffer outputByteBuffer = ByteBuffer.allocate(coordinateCount * (2 * Integer.BYTES));
        for (int coordinate : coordinateArray) {
            outputByteBuffer.putInt(coordinate);
        }
        outputStream.writeInt(coordinateCount);
        outputStream.write(outputByteBuffer.array());
        ByteBuffer inputByteBuffer = ByteBuffer.wrap(inputStream.readNBytes(coordinateCount * (2 * Integer.BYTES + 1)));
        while (inputByteBuffer.hasRemaining()) {
            int x = inputByteBuffer.getInt();
            int y = inputByteBuffer.getInt();
            boolean traversable = inputByteBuffer.get() == (byte) 1;
            board.discoverBoardTile(x, y, traversable);
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
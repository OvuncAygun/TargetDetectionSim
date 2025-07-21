import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

public class NormalObserver implements Observer {
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;
    private final Board board;
    private final ExecutorService targetingService;
    private final String id;
    private int x;
    private int y;
    private final Random random = new Random(System.currentTimeMillis());
    private final static int visionRange = 20;
    private final static int scanRange = 10;
    private final Deque<int[]> path = new ArrayDeque<>();

    public NormalObserver(DataInputStream inputStream, DataOutputStream outputStream, Board board,
                          ExecutorService targetingService) throws IOException {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.board = board;
        this.targetingService = targetingService;
        outputStream.writeUTF("observer");
        do {
            x = random.nextInt(0, board.xSize);
            y = random.nextInt(0, board.ySize);
            outputStream.writeInt(x);
            outputStream.writeInt(y);
        }
        while (!inputStream.readBoolean());
        outputStream.writeInt(scanRange);
        this.id = inputStream.readUTF().substring("observer-".length());
    }

    public void move() throws IOException {
        this.discover(visionRange);
        outputStream.writeUTF("MOVE");
        if(path.isEmpty()) {
            int targetX;
            int targetY;
            do {
                BoardTile target = board.discoveredTiles.get(random.nextInt(0, board.discoveredTiles.size()));
                targetX = target.x;
                targetY = target.y;
            }
            while (!findPath(targetX, targetY));
        }
        int[] movement = path.removeFirst();
        x = movement[0];
        y = movement[1];
        outputStream.writeInt(x);
        outputStream.writeInt(y);
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

    public void scan() throws IOException{
        outputStream.writeUTF("SCAN");
        ArrayList<Integer> coordinateArray = new ArrayList<Integer>();
        int coordinateCount = findCircleCoordinates(coordinateArray, scanRange);
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
            boolean entityExists = inputByteBuffer.get() == (byte) 1;
            if (entityExists) {
                board.discoveredEntities.add(new DiscoveredEntity(x, y));
            }
        }
        identify();
        markEntities();
    }

    private void markEntities() throws IOException {
        for (DiscoveredEntity entity : board.discoveredEntities) {
            if (entity.identified) {
                if (entity.lostCounter == 0) {
                    outputStream.writeUTF("MARK");
                    outputStream.writeUTF(entity.id);
                    outputStream.writeInt(entity.x);
                    outputStream.writeInt(entity.y);
                    targetEntity(entity);
                }
                else {
                    outputStream.writeUTF("MARK");
                    outputStream.writeUTF(entity.id + "?");
                    outputStream.writeInt(entity.x);
                    outputStream.writeInt(entity.y);
                }
            }
            else {
                outputStream.writeUTF("MARK");
                outputStream.writeUTF("?");
                outputStream.writeInt(entity.x);
                outputStream.writeInt(entity.y);
            }
        }
    }

    private void targetEntity(DiscoveredEntity entity) {

    }

    private void identify() {
        Iterator<DiscoveredEntity> entityIterator = board.discoveredEntities.iterator();
        HashMap<DiscoveredEntity, EntityProbabilityMap> entityProbabilityMapMap = new HashMap<>();
        while (entityIterator.hasNext()) {
            DiscoveredEntity entity = entityIterator.next();
            if (entity.identified) {
                EntityProbabilityMap entityProbabilityMap = new EntityProbabilityMap();
                entityProbabilityMapMap.put(entity, entityProbabilityMap);
                for (DiscoveredEntity unidentifiedEntity : board.discoveredEntities) {
                    if (!unidentifiedEntity.identified && !unidentifiedEntity.removeMark) {
                        for (int i = -2; i <= 2; i++) {
                            for (int j = Math.abs(i) - 2; j <= 2 - Math.abs(i); j++) {
                                if(entity.x == unidentifiedEntity.x + i && entity.y == unidentifiedEntity.y + j) {
                                    entityProbabilityMap.hashMap.put(unidentifiedEntity,
                                            entity.getProbability(i, j));
                                }
                            }
                        }
                    }
                }
                entityProbabilityMap.calculateHighestProbability();
            }
        }
        EntityProbabilityArray entityProbabilityArray =
                new EntityProbabilityArray(board, id, entityProbabilityMapMap);

        entityProbabilityArray.normalize();

        entityProbabilityArray.findLines();

        entityProbabilityArray.matchEntities();

        entityIterator = board.discoveredEntities.iterator();
        while (entityIterator.hasNext()) {
            DiscoveredEntity entity = entityIterator.next();
            if(entity.removeMark) {
                entityIterator.remove();
            }
        }
    }
}
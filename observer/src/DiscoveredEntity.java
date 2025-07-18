import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;

public class DiscoveredEntity {
    public int x;
    public int y;
    public boolean identified = false;
    public boolean removeMark = false;
    public int lostCounter = 0;
    public int updateCounter = 0;
    public int moveCounter = 0;
    public int lastMovementCounter = 0;
    public String id = "";
    public List<int[]> coordinateQueue = new LinkedList<>();
    public int xProbability;
    public int yProbability;
    public HashMap<Point, Integer> probabilityMap = new HashMap<>();
    public DiscoveredEntity matchedEntity = null;

    public DiscoveredEntity (int x, int y) {
        this.x = x;
        this.y = y;
        coordinateQueue.addLast(new int[] {x, y});
    }

    public void updateCoordinate(int x, int y) {
        lostCounter = 0;
        if (this.x != x || this.y != y) {
            moveCounter++;
            lastMovementCounter = 0;
            this.x = x;
            this.y = y;
            coordinateQueue.addLast(new int [] {x, y});
            while (coordinateQueue.size() > 3) {
                coordinateQueue.removeFirst();
            }
            int dx1;
            int dy1;
            int dx2;
            int dy2;
            switch (coordinateQueue.size()) {
                case 1:
                    xProbability = 0;
                    yProbability = 0;
                    break;
                case 2:
                    dx1 = coordinateQueue.get(1)[0] - coordinateQueue.get(0)[0];
                    dy1 = coordinateQueue.get(1)[1] - coordinateQueue.get(0)[1];
                    xProbability = 2 * (dx1);
                    yProbability = 2 * (dy1);
                    break;
                case 3:
                    dx1 = coordinateQueue.get(1)[0] - coordinateQueue.get(0)[0];
                    dy1 = coordinateQueue.get(1)[1] - coordinateQueue.get(0)[1];
                    dx2 = coordinateQueue.get(2)[0] - coordinateQueue.get(1)[0];
                    dy2 = coordinateQueue.get(2)[1] - coordinateQueue.get(1)[1];
                    xProbability = (dx2) + (dx1);
                    yProbability = (dy2) + (dy1);
                    if (dx1 * dx2 < 0 || dy1 * dy2 < 0) {
                        xProbability = 2 * (dx2);
                        yProbability = 2 * (dy2);
                    }
                    break;
            }
        }
        else {
            lastMovementCounter++;
        }
        updateCounter++;
    }

    public int getProbability(int xMovement, int yMovement) {
        int predictedMovementTiming = 0;
        if (moveCounter != 0) {
            predictedMovementTiming = Math.round((float) updateCounter / moveCounter);
        }
        return switch (Math.abs(xMovement) + Math.abs(yMovement)) {
            case 0 -> {
                if (moveCounter == 0) {
                    yield 10;
                }
                else {
                    int scaled = Math.max(0,
                            Math.min(predictedMovementTiming, predictedMovementTiming - lastMovementCounter));
                    yield 10 - Math.round((float) 10 * scaled / predictedMovementTiming);
                }

            }
            case 1 -> x * xProbability + y * yProbability;
            case 2 -> x * xProbability + y * yProbability - 100;
            default -> -1000;
        };
    }

    public void match(DiscoveredEntity matchedEntity) {
        this.matchedEntity = matchedEntity;
        updateCoordinate(matchedEntity.x, matchedEntity.y);
        matchedEntity.removeMark = true;
    }


}

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;

public class DiscoveredEntity {
    public int x;
    public int y;
    public boolean identified = false;
    public boolean removeMark = false;
    public int lostCounter = 0;
    public String id = "";
    public List<int[]> coordinateQueue = new LinkedList<>();
    public int xProbability;
    public int yProbability;
    public HashMap<String, Integer> probabilityMap= new HashMap<>();
    public HashMap<DiscoveredEntity, Integer> entityProbabilityMap = new HashMap<>();

    public DiscoveredEntity (int x, int y) {
        this.x = x;
        this.y = y;
        coordinateQueue.addLast(new int[] {x, y});
        probabilityMap.put("left", 0);
        probabilityMap.put("right", 0);
        probabilityMap.put("up", 0);
        probabilityMap.put("down", 0);
        probabilityMap.put("stay", 10);
    }

    public void updateCoordinate(int x, int y) {
        lostCounter = 0;
        if (this.x != x || this.y != y) {
            this.x = x;
            this.y = y;
            coordinateQueue.addLast(new int [] {x, y});
            while (coordinateQueue.size() > 3) {
                coordinateQueue.removeFirst();
            }
            switch (coordinateQueue.size()) {
                case 1:
                    xProbability = 0;
                    yProbability = 0;
                    break;
                case 2:
                    xProbability = 2 * (coordinateQueue.get(1)[0] - coordinateQueue.get(0)[0]);
                    yProbability = 2 * (coordinateQueue.get(1)[1] - coordinateQueue.get(0)[1]);
                    break;
                case 3:
                    xProbability = (coordinateQueue.get(2)[0] - coordinateQueue.get(1)[0]) +
                            (coordinateQueue.get(1)[0] - coordinateQueue.get(0)[0]);
                    yProbability = (coordinateQueue.get(2)[1] - coordinateQueue.get(1)[1]) +
                            (coordinateQueue.get(1)[1] - coordinateQueue.get(0)[1]);
                    break;
            }
            probabilityMap.put("left", -xProbability);
            probabilityMap.put("right", xProbability);
            probabilityMap.put("up", -yProbability);
            probabilityMap.put("down", yProbability);
            probabilityMap.put("stay", 10);

            System.out.println(id + ":" + probabilityMap);
        }
    }
}

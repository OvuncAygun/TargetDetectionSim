import com.sun.jdi.IntegerValue;

import javax.swing.text.html.parser.Entity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EntityProbabilityArray {
    private final Board board;
    private final String id;
    private ArrayList<DiscoveredEntity> entityList;
    public ArrayList<DiscoveredEntity> identifiedEntities = new ArrayList<>();
    public ArrayList<DiscoveredEntity> unidentifiedEntities = new ArrayList<>();
    public int[][] array;
    public int arraySize;
    private static final int IMPOSSIBLE_PROBABILITY = -1000;

    public EntityProbabilityArray(Board board, String id,
                                  HashMap<DiscoveredEntity, EntityProbabilityMap> EntityProbabilityMapMap) {
        this.board = board;
        this.id = id;
        this.entityList = board.discoveredEntities;
        for (DiscoveredEntity entity : entityList) {
            if (entity.identified) {
                identifiedEntities.add(entity);
            }
            else {
                unidentifiedEntities.add(entity);
            }
        }
        arraySize = Math.max(identifiedEntities.size(), unidentifiedEntities.size());
        array = new int[arraySize][arraySize];
        for (int[] row : array) {
            Arrays.fill(row, IMPOSSIBLE_PROBABILITY);
        }
        for (Map.Entry<DiscoveredEntity, EntityProbabilityMap> probabilityMapEntry : EntityProbabilityMapMap.entrySet()) {
            DiscoveredEntity identifiedEntity = probabilityMapEntry.getKey();
            EntityProbabilityMap entityProbabilityMap = probabilityMapEntry.getValue();
            for (Map.Entry<DiscoveredEntity, Integer> probabilityEntry : entityProbabilityMap.hashMap.entrySet()) {
                DiscoveredEntity unidentifiedEntity = probabilityEntry.getKey();
                int probability = probabilityEntry.getValue();
                array[identifiedEntities.indexOf(identifiedEntity)]
                        [unidentifiedEntities.indexOf(unidentifiedEntity)] = probability;
            }
        }
    }

    public void normalize() {
        for(int row = 0; row < arraySize; row++) {
            int maximum = Integer.MIN_VALUE;
            for (int col = 0; col < arraySize; col++) {
                maximum = Math.max(maximum, array[row][col]);
            }
            for (int col = 0; col < arraySize; col++) {
                array[row][col] -= maximum;
            }
        }
        for(int col = 0; col < arraySize; col++) {
            int maximum = Integer.MIN_VALUE;
            for (int row = 0; row < arraySize; row++) {
                maximum = Math.max(maximum, array[row][col]);
            }
            for (int row = 0; row < arraySize; row++) {
                array[row][col] -= maximum;
            }
        }
    }

    public void findLines() {
        int lineCount = 0;

        ArrayList<Integer> markedRows;
        ArrayList<Integer> markedCols;

        while (true) {
            markedRows = new ArrayList<>();
            markedCols = new ArrayList<>();
            boolean[][] zeroStarred = new boolean[arraySize][arraySize];
            for (boolean[] row : zeroStarred) {
                Arrays.fill(row, false);
            }
            boolean[] rowMarked = new boolean[arraySize];
            boolean[] colMarked = new boolean[arraySize];
            Arrays.fill(colMarked, false);
            for (int row = 0; row < arraySize; row++) {
                for (int col = 0; col < arraySize; col++) {
                    if (array[row][col] == 0 && !colMarked[col]) {
                        zeroStarred[row][col] = true;
                        colMarked[col] = true;
                        break;
                    }
                }
            }
            Arrays.fill(rowMarked, false);
            Arrays.fill(colMarked, false);
            for (int row = 0; row < arraySize; row++) {
                boolean rowHasStar = false;
                for (int col = 0; col < arraySize; col++) {
                    if (zeroStarred[row][col]) {
                        rowHasStar = true;
                    }
                }
                if (!rowHasStar) {
                    rowMarked[row] = true;
                }
            }
            boolean newMarks = true;
            while (newMarks) {
                newMarks = false;
                for (int row = 0; row < arraySize; row++) {
                    for (int col = 0; col < arraySize; col++) {
                        if (rowMarked[row] && array[row][col] == 0 && !colMarked[col]) {
                            colMarked[col] = true;
                            newMarks = true;
                        }
                    }
                }
                for (int row = 0; row < arraySize; row++) {
                    for (int col = 0; col < arraySize; col++) {
                        if (colMarked[col] && zeroStarred[row][col] && !rowMarked[row]) {
                            rowMarked[row] = true;
                            newMarks = true;
                            break;
                        }
                    }
                }
            }
            for (int i = 0; i < arraySize; i++) {
                if (!rowMarked[i]) {
                    markedRows.add(i);
                }
                if (colMarked[i]) {
                    markedCols.add(i);
                }
            }
            lineCount = markedRows.size() + markedCols.size();
            if (lineCount != arraySize) {
                int maximum = Integer.MIN_VALUE;
                for (int row = 0; row < arraySize; row++) {
                    if (rowMarked[row]) {
                        for (int col = 0; col < arraySize; col++) {
                            if (!colMarked[col]) {
                                maximum = Math.max(maximum, array[row][col]);
                            }
                        }
                    }
                }
                for (int row = 0; row < arraySize; row++) {
                    if (rowMarked[row]) {
                        for (int col = 0; col < arraySize; col++) {
                            if (!colMarked[col]) {
                                array[row][col] -= maximum;
                            }
                        }
                    }
                }
            }
            else {
                break;
            }
        }
    }

    public void matchEntities() {
        boolean[] rowAssigned = new boolean[arraySize];
        boolean[] colAssigned = new boolean[arraySize];
        Arrays.fill(rowAssigned, false);
        Arrays.fill(colAssigned, false);
        for (int row = 0; row < identifiedEntities.size(); row++) {
            if (!rowAssigned[row]) {
                for (int col = 0; col < unidentifiedEntities.size(); col++) {
                    if (array[row][col] == 0 && !colAssigned[col]) {
                        DiscoveredEntity rowEntity = identifiedEntities.get(row);
                        DiscoveredEntity colEntity = unidentifiedEntities.get(col);
                        rowEntity.match(colEntity);
                        rowAssigned[row] = true;
                        colAssigned[col] = true;
                    }
                }
            }
        }
        for (int row = 0; row < identifiedEntities.size(); row++) {
            if (!rowAssigned[row]) {
                DiscoveredEntity rowEntity = identifiedEntities.get(row);
                if (rowEntity.lostCounter > 10) {
                    rowEntity.removeMark = true;
                }
                else {
                    rowEntity.lostCounter++;
                }
            }
        }

        for (int col = 0; col < unidentifiedEntities.size(); col++) {
            if (!colAssigned[col]) {
                DiscoveredEntity colEntity = unidentifiedEntities.get(col);
                colEntity.id = "%s-%d".formatted(id, board.identificationCounter);
                board.identificationCounter++;
                colEntity.identified = true;
            }
        }
    }
}

import com.sun.jdi.IntegerValue;

import javax.swing.text.html.parser.Entity;
import java.awt.*;
import java.util.*;

public class EntityProbabilityArray {
    private final Board board;
    private final String id;
    private ArrayList<DiscoveredEntity> entityList;
    public ArrayList<DiscoveredEntity> identifiedEntities = new ArrayList<>();
    public ArrayList<DiscoveredEntity> unidentifiedEntities = new ArrayList<>();
    public int[][] array;
    public int arraySize;
    public int rowCount;
    public int colCount;
    public ArrayList<int[]> starredZeros;
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

        rowCount = identifiedEntities.size();
        colCount = unidentifiedEntities.size();

        arraySize = Math.max(identifiedEntities.size(), unidentifiedEntities.size());

        ArrayList<ArrayList<Integer>> arrayList = new ArrayList<>();

        for (int row = 0; row < rowCount; row++) {
            ArrayList<Integer> arrayListRow = new ArrayList<>();
            for (int col = 0; col < colCount; col++) {
                arrayListRow.add(IMPOSSIBLE_PROBABILITY);
            }
            arrayList.add(arrayListRow);
        }

        for (Map.Entry<DiscoveredEntity, EntityProbabilityMap> probabilityMapEntry : EntityProbabilityMapMap.entrySet()) {
            DiscoveredEntity identifiedEntity = probabilityMapEntry.getKey();
            EntityProbabilityMap entityProbabilityMap = probabilityMapEntry.getValue();
            for (Map.Entry<DiscoveredEntity, Integer> probabilityEntry : entityProbabilityMap.hashMap.entrySet()) {
                DiscoveredEntity unidentifiedEntity = probabilityEntry.getKey();
                int probability = probabilityEntry.getValue();
                arrayList.get(identifiedEntities.indexOf(identifiedEntity))
                        .set(unidentifiedEntities.indexOf(unidentifiedEntity), probability);
            }
        }

        for (int row = 0; row < identifiedEntities.size(); row++) {
            boolean impossible = true;
            for (int col = 0; col < unidentifiedEntities.size(); col++) {
                if (arrayList.get(row).get(col) != IMPOSSIBLE_PROBABILITY) {
                    impossible = false;
                    break;
                }
            }
            if (impossible) {
                identifiedEntities.get(row).matchable = false;
                identifiedEntities.remove(row);
                arrayList.remove(row);
                row--;
            }
        }

        for (int col = 0; col < unidentifiedEntities.size(); col++) {
            boolean impossible = true;
            for (int row = 0; row < identifiedEntities.size(); row++) {
                if (arrayList.get(row).get(col) != IMPOSSIBLE_PROBABILITY) {
                    impossible = false;
                    break;
                }
            }
            if (impossible) {
                unidentifiedEntities.get(col).matchable = false;
                unidentifiedEntities.remove(col);
                for (ArrayList<Integer> arrayListRow : arrayList) {
                    arrayListRow.remove(col);
                }
                col--;
            }
        }

        rowCount = identifiedEntities.size();
        colCount = unidentifiedEntities.size();

        arraySize = Math.max(identifiedEntities.size(), unidentifiedEntities.size());

        array = new int[rowCount][colCount];
        for (int row = 0; row < rowCount; row++) {
            Arrays.fill(array[row], IMPOSSIBLE_PROBABILITY);
        }
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < colCount; col++) {
                array[row][col] = arrayList.get(row).get(col);
            }
        }
    }

    public void normalize() {

        // Hungarian Algorithm Step 1
        for(int row = 0; row < rowCount; row++) {
            int maximum = Integer.MIN_VALUE;
            for (int col = 0; col < colCount; col++) {
                maximum = Math.max(maximum, array[row][col]);
            }
            for (int col = 0; col < colCount; col++) {
                array[row][col] -= maximum;
            }
        }

        // Hungarian Algorithm Step 2
        for(int col = 0; col < colCount; col++) {
            int maximum = Integer.MIN_VALUE;
            for (int row = 0; row < rowCount; row++) {
                maximum = Math.max(maximum, array[row][col]);
            }
            for (int row = 0; row < rowCount; row++) {
                array[row][col] -= maximum;
            }
        }
    }

    public void findLines() {
        int lineCount = 0;

        boolean[][] zeroStarred = new boolean[rowCount][colCount];
        boolean[][] zeroPrimed = new boolean[rowCount][colCount];
        boolean[] rowMarked = new boolean[rowCount];
        boolean[] colMarked = new boolean[colCount];

        for (boolean[] row : zeroStarred) {
            Arrays.fill(row, false);
        }
        for (boolean[] row : zeroPrimed) {
            Arrays.fill(row, false);
        }
        Arrays.fill(rowMarked, false);
        Arrays.fill(colMarked, false);

        boolean[] rowUsed = new boolean[rowCount];
        boolean[] colUsed = new boolean[colCount];
        Arrays.fill(rowUsed, false);
        Arrays.fill(colUsed, false);

        // Hungarian Algorithm Step 3
        for (int row = 0; row < rowCount; row++) {
            if (!rowUsed[row]) {
                for (int col = 0; col < colCount; col++) {
                    if (!colUsed[col] && array[row][col] == 0) {
                        zeroStarred[row][col] = true;
                        rowUsed[row] = true;
                        colUsed[col] = true;
                        break;
                    }
                }
            }
        }

        while (true) {
            starredZeros = new ArrayList<>();

            // Hungarian Algorithm Step 4
            for (int col = 0; col < colCount; col++) {
                boolean colHasStar = false;
                for (int row = 0; row < rowCount; row++) {
                    if (zeroStarred[row][col]) {
                        colHasStar = true;
                        break;
                    }
                }
                if (colHasStar) {
                    colMarked[col] = true;
                }
            }

            // Hungarian Algorithm Step 4 Loop
            boolean nonMarkedZero = true;
            loop:
            while (nonMarkedZero) {
                nonMarkedZero = false;
                for (int row = 0; row < rowCount; row++) {
                    for (int col = 0; col < colCount; col++) {
                        if (array[row][col] == 0 && !colMarked[col] && !rowMarked[row]) {
                            nonMarkedZero = true;
                            zeroPrimed[row][col] = true;
                            boolean rowHasStar = false;
                            int starredCol;
                            for (starredCol = 0; starredCol < colCount; starredCol++) {
                                if (zeroStarred[row][starredCol]) {
                                    rowHasStar = true;
                                    break;
                                }
                            }
                            if (rowHasStar) {
                                rowMarked[row] = true;
                                colMarked[starredCol] = false;
                            }
                            else {
                                boolean loop2 = true;
                                Deque<int[]> path = new ArrayDeque<>();
                                int subStepRow = row;
                                int subStepCol = col;
                                path.add(new int[] {subStepRow, subStepCol});
                                while (true) {
                                    // Hungarian Algorithm Step 4.1
                                    boolean starredZeroInCol = false;
                                    for (subStepRow = 0; subStepRow < rowCount; subStepRow++) {
                                        if (zeroStarred[subStepRow][subStepCol]) {
                                            path.add(new int[] {subStepRow, subStepCol});
                                            starredZeroInCol = true;
                                            break;
                                        }
                                    }
                                    if (!starredZeroInCol) {
                                        break;
                                    }

                                    // Hungarian Algorithm Step 4.2
                                    for (subStepCol = 0; subStepCol < colCount; subStepCol++) {
                                        if (zeroPrimed[subStepRow][subStepCol]) {
                                            path.add(new int[] {subStepRow, subStepCol});
                                            break;
                                        }
                                    }
                                }
                                for (int[] zero : path) {
                                    int zeroRow = zero[0];
                                    int zeroCol = zero[1];
                                    if (zeroPrimed[zeroRow][zeroCol]) {
                                        zeroStarred[zeroRow][zeroCol] = true;
                                    }
                                    else if (zeroStarred[zeroRow][zeroCol]) {
                                        zeroStarred[zeroRow][zeroCol] = false;
                                    }
                                }

                                for (boolean[] zeroPrimedRow : zeroPrimed) {
                                    Arrays.fill(zeroPrimedRow, false);
                                }
                                Arrays.fill(rowMarked, false);
                                Arrays.fill(colMarked, false);
                            }
                            continue loop;
                        }
                    }
                }
            }

            for (int row = 0; row < rowCount; row++) {
                for (int col = 0; col < colCount; col++) {
                    if (zeroStarred[row][col]) {
                        starredZeros.add(new int[] {row, col});
                    }
                }
            }
            // Hungarian Algorithm Step 5
            if (starredZeros.size() == Math.min(rowCount, colCount)) {
                break;
            }
            else {
                int maximum = Integer.MIN_VALUE;
                for (int row = 0; row < rowCount; row++) {
                    for (int col = 0; col < colCount; col++) {
                        if (!rowMarked[row] && !colMarked[col]) {
                            maximum = Math.max(maximum, array[row][col]);
                        }
                    }
                }
                for (int row = 0; row < rowCount; row++) {
                    for (int col = 0; col < colCount; col++) {
                        if (!rowMarked[row] && !colMarked[col]) {
                            array[row][col] -= maximum;
                        }
                    }
                }
            }
        }

        for (int[] row : array) {
            System.out.println(Arrays.toString(row));
        }
        System.out.println();
        for (boolean[] row : zeroStarred) {
            System.out.println(Arrays.toString(row));
        }
        System.out.println();
    }

    public void matchEntities() {
        boolean[] rowAssigned = new boolean[arraySize];
        boolean[] colAssigned = new boolean[arraySize];
        Arrays.fill(rowAssigned, false);
        Arrays.fill(colAssigned, false);
        for (int[] starredZero : starredZeros) {
            int starRow = starredZero[0];
            int starCol = starredZero[1];
            DiscoveredEntity rowEntity = identifiedEntities.get(starRow);
            DiscoveredEntity colEntity = unidentifiedEntities.get(starCol);
            rowEntity.match(colEntity);
            rowAssigned[starRow] = true;
            colAssigned[starCol] = true;
        }

        for (int row = 0; row < rowCount; row++) {
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

        for (int col = 0; col < colCount; col++) {
            if (!colAssigned[col]) {
                DiscoveredEntity colEntity = unidentifiedEntities.get(col);
                colEntity.id = "%s-%d".formatted(id, board.identificationCounter);
                board.identificationCounter++;
                colEntity.identified = true;
            }
        }

        for (DiscoveredEntity entity : entityList) {
            if (!entity.matchable) {
                if (entity.identified) {
                    if (entity.lostCounter > 10) {
                        entity.removeMark = true;
                    } else {
                        entity.lostCounter++;
                    }
                } else {
                    entity.id = "%s-%d".formatted(id, board.identificationCounter);
                    board.identificationCounter++;
                    entity.identified = true;
                }
                entity.matchable = true;
            }
        }
    }
}

import java.util.HashMap;
import java.util.Map;

public class EntityProbabilityMap {
    public HashMap<DiscoveredEntity, Integer> hashMap = new HashMap<>();
    public int highestProbability;
    public DiscoveredEntity highestProbabilityEntity;

    public void calculateHighestProbability() {
        highestProbability = Integer.MIN_VALUE;
        for (Map.Entry<DiscoveredEntity, Integer> entry : hashMap.entrySet()) {
            if (entry.getValue() > highestProbability && !entry.getKey().removeMark) {
                highestProbability = entry.getValue();
                highestProbabilityEntity = entry.getKey();
            }
        }
    }
}

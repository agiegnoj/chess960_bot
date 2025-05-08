package ai;

import java.util.LinkedHashMap;
import java.util.Map;

public class FixedSizeHashMap<Long, Integer> extends LinkedHashMap <Long, Integer>{
    
    final int maxSize;
       
    public FixedSizeHashMap(int maxSize) {
        super(maxSize + 1, 0.75f, true);
        this.maxSize = maxSize;
    }
      
    @Override
    protected boolean removeEldestEntry(Map.Entry<Long, Integer> eldest) {
        return size() > maxSize;
    } 
}

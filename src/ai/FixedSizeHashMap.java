package ai;

import java.util.LinkedHashMap;
import java.util.Map;

public class FixedSizeHashMap<Board, Integer> extends LinkedHashMap <Board, Integer>{
    
    final int maxSize;
       
    public FixedSizeHashMap(int maxSize) {
        super(maxSize + 1, 0.75f, true);
        this.maxSize = maxSize;
    }
      
    @Override
    protected boolean removeEldestEntry(Map.Entry<Board, Integer> eldest) {
        return size() > maxSize;
    } 
}

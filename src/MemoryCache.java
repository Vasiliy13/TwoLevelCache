import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by Vasiliy on 29.03.2017.
 */

public class MemoryCache<KeyType, ValueType> implements Cache<KeyType,ValueType>, CallFrequency<KeyType> {
    private HashMap<KeyType,ValueType> hashMap;
    private TreeMap<KeyType, Integer> frequencyMap;


    public MemoryCache(){
        hashMap = new HashMap<KeyType, ValueType>();
        frequencyMap = new TreeMap<KeyType, Integer>();
    }

    @Override
    public void cache(KeyType key, ValueType value) {
        frequencyMap.put(key,1);
        hashMap.put(key, value);
    }


    @Override
    public ValueType getObject(KeyType key) {
        if(hashMap.containsKey(key)){
            int frequency = frequencyMap.get(key);
            frequencyMap.put(key,++frequency);
            return hashMap.get(key);
        }


        return null;
    }


    @Override
    public void deleteObject(KeyType key) {
        if(hashMap.containsKey(key)){
            hashMap.remove(key);
            frequencyMap.remove(key);
        }
    }


    @Override
    public void clearCache() {
        hashMap.clear();
        frequencyMap.clear();
    }


    @Override
    public ValueType removeObject(KeyType key) {
        if(hashMap.containsKey(key)){
            ValueType result = this.getObject(key);
            this.deleteObject(key);
            return result;
        }
        return null;
    }


    @Override
    public boolean containsKey(KeyType key) {
        return hashMap.containsKey(key);
    }


    @Override
    public int getSize() {
        return hashMap.size();
    }


    @Override
    public Set<KeyType> getMostFrequentlyUsedKeys() {
        TreeMap<KeyType,Integer> sorted = new TreeMap(new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                if((Integer)o1 < (Integer)o2) {
                    return 1;
                } else if(o1 == o2) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        sorted.putAll(frequencyMap);
        return sorted.keySet();
    }


    @Override
    public int getFrequencyOfCallingObject(KeyType key) {
        if(hashMap.containsKey(key)){
            return frequencyMap.get(key);
        }
        return 0;
    }
}
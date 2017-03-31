import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Vasiliy on 30.03.2017.
 */


public class TwoLevelCache<KeyType,ValueType extends Serializable>
        implements Cache<KeyType, ValueType>, CallFrequency<KeyType>
{
    private MemoryCache<KeyType,ValueType> memCache;
    private FileSystemCache<KeyType,ValueType> fileCache;
    private int maxMemorySize;
    private int RecacheCount;
    private int RecacheMax;

    public TwoLevelCache(int maxMemory, int RecacheC)
    {
        maxMemorySize = maxMemory;
        RecacheMax = RecacheC;
        memCache = new MemoryCache<>();
        fileCache = new FileSystemCache<>();
        RecacheCount = 0;
    }


    @Override
    public void cache(KeyType key, ValueType value) throws Exception {
        if (memCache.getSize() > maxMemorySize) {
            this.recache();
        }
        memCache.cache(key,value);
    }


    @Override
    public ValueType getObject(KeyType key) throws Exception {
        if(memCache.containsKey(key)){
            RecacheCount++;
            if(RecacheCount >= RecacheMax){
                this.recache();
                RecacheCount = 0;
            }
            return memCache.getObject(key);
        }
        if(fileCache.containsKey(key)){
            RecacheCount++;
            if(RecacheCount > RecacheMax){
                this.recache();
                RecacheCount = 0;
            }
            return  fileCache.getObject(key);
        }
        return null;
    }


    @Override
    public void deleteObject(KeyType key) {
        if(memCache.containsKey(key)){
            memCache.deleteObject(key);
        }
        if(fileCache.containsKey(key)){
            fileCache.deleteObject(key);
        }
    }


    @Override
    public void clearCache() {
        fileCache.clearCache();
        memCache.clearCache();
    }


    @Override
    public ValueType removeObject(KeyType key) throws Exception {
        if(memCache.containsKey(key)){
            return memCache.removeObject(key);
        }
        if(fileCache.containsKey(key)){
            return  fileCache.removeObject(key);
        }
        return null;
    }


    @Override
    public boolean containsKey(KeyType key) {
        if(memCache.containsKey(key)){
            return true;
        }
        if(fileCache.containsKey(key)){
            return  true;
        }
        return false;
    }


    @Override
    public int getSize() {
        return memCache.getSize() + fileCache.getSize();
    }


    @Override
    public Set<KeyType> getMostFrequentlyUsedKeys() {
        TreeSet<KeyType> set = new TreeSet<KeyType>(memCache.getMostFrequentlyUsedKeys());
        set.addAll(fileCache.getMostFrequentlyUsedKeys());
        return set;
    }


    @Override
    public int getFrequencyOfCallingObject(KeyType key) {
        if(memCache.containsKey(key))
        {
            return memCache.getFrequencyOfCallingObject(key);
        }
        if(fileCache.containsKey(key))
        {
            return fileCache.getFrequencyOfCallingObject(key);
        }
        return 0;
    }

    private void recache() throws Exception {
        TreeSet<KeyType> memKeySet = new TreeSet<>(memCache.getMostFrequentlyUsedKeys());
        int averageFreq = 0;

        for(KeyType key: memKeySet)
        {
            averageFreq += memCache.getFrequencyOfCallingObject(key);
        }
        averageFreq /= memKeySet.size();


        for(KeyType key: memKeySet)
        {
            if(memCache.getFrequencyOfCallingObject(key) <= averageFreq){
                fileCache.cache(key, memCache.removeObject(key));
            }
        }

        TreeSet<KeyType> fileKeySet = new TreeSet<>(fileCache.getMostFrequentlyUsedKeys());
        for(KeyType key : fileKeySet)
        {
            try{
                if(fileCache.getFrequencyOfCallingObject(key) > averageFreq)
                {
                    memCache.cache(key,fileCache.removeObject(key));
                }
            }
            catch (Exception e)
            {
                fileCache.deleteObject(key);
            }
        }
    }
}
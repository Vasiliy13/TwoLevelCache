import java.io.*;
import java.util.*;

/**
 * Created by Vasiliy on 29.03.2017.
 */

public class FileSystemCache<KeyType, ValueType extends Serializable>
        implements Cache<KeyType,ValueType>, CallFrequency<KeyType> {


    private HashMap<KeyType, String> hashMap;
    private TreeMap<KeyType, Integer> frequencyMap;


    public FileSystemCache() {
        hashMap = new HashMap<>();
        frequencyMap = new TreeMap<>();


        File tempFolder = new File("temp\\");
        if(!tempFolder.exists()){
            tempFolder.mkdirs();
        }
    }

    @Override
    public void cache(KeyType key, ValueType value) throws Exception {
        String pathToObject;
        pathToObject = this.getFileName();

        frequencyMap.put(key,1);
        hashMap.put(key, pathToObject);


        FileOutputStream fileStream = new FileOutputStream(pathToObject);
        ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);

        objectStream.writeObject(value);
        objectStream.flush();
        objectStream.close();
        fileStream.flush();
        fileStream.close();
    }


    @Override
    public ValueType getObject(KeyType key) throws Exception {
        if(hashMap.containsKey(key)){
            String pathToObject = hashMap.get(key);
            try
            {
                FileInputStream fileStream = new FileInputStream(pathToObject);
                ObjectInputStream objectStream = new ObjectInputStream(fileStream);


                ValueType deserializedObject =  (ValueType) objectStream.readObject();

                int frequency = frequencyMap.remove(key);
                frequencyMap.put(key, ++frequency);


                fileStream.close();
                objectStream.close();

                return deserializedObject;
            }
            catch (Exception e)
            {
                return null;
            }
        }
        return null;
    }


    @Override
    public void deleteObject(KeyType key) {
        if(hashMap.containsKey(key))
        {
            File deletingFile = new File(hashMap.remove(key));
            frequencyMap.remove(key);
            deletingFile.delete();
        }
    }


    @Override
    public void clearCache() {
        for(KeyType key : hashMap.keySet()){
            File deletingFile = new File(hashMap.get(key));
            deletingFile.delete();
        }

        hashMap.clear();
        frequencyMap.clear();
    }


    @Override
    public ValueType removeObject(KeyType key) throws Exception {
        if(hashMap.containsKey(key)) {
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
        return  0;
    }

    private String getFileName() {
        return "temp\\" + UUID.randomUUID().toString() + ".temp";
    }
}


/**
 * Created by Vasiliy on 29.03.2017.
 */

// Main interface for Cache
interface Cache<KeyType, ValueType> {

    void cache(KeyType key, ValueType value) throws Exception;

    ValueType getObject(KeyType key) throws Exception;

    void  deleteObject(KeyType key);

    void clearCache();

    ValueType removeObject(KeyType key) throws Exception;

    boolean containsKey(KeyType key);

    int getSize();
}

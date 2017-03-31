import java.util.Set;

/**
 * Created by Vasiliy on 29.03.2017.
 */
public interface CallFrequency<KeyType> {

    Set<KeyType> getMostFrequentlyUsedKeys();

    int getFrequencyOfCallingObject(KeyType key);
}

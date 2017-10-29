package utils.data;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collector;
import java.util.stream.Stream;

/**
 * ListUtils - contains functionality which can be applied to lists
 */

public class ListUtils {

    public static <T, K> Stream<Entry<K, List<T>>> groupToStream(List<T> l, Collector<T, ?, Map<K, List<T>>> c) {
        return l.stream().collect(c).entrySet().stream();
    }
}
package utils.data;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * MapUtils contains functionality which can be applied to maps
 */

public class MapUtils {

    public static <K, V> Map<K, V> descSort(Map<K, V> m, Comparator<Entry<K, V>> c) {
        return m.entrySet().stream()
                .sorted(c.reversed())
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    public static <K, V extends Comparable<V>> Map<K, Integer> zipWithRankByValue(Map<K, V> m) {
        Map<K, V> s = descSort(m, Entry.comparingByValue());

        List<Entry<K, V>> l = new ArrayList<>(s.entrySet());
        Map<K, Integer> b = new LinkedHashMap<>();
        int i = 0;
        int r = 0;
        for (Entry<K, V> e : l) {
            if (i == 0 || e.getValue().compareTo(l.get(i - 1).getValue()) < 0)
                r += 1;
            b.put(e.getKey(), r);
            i += 1;
        }

        return b;
    }
}

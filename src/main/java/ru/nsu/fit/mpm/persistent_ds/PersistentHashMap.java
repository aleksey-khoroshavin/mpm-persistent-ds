package ru.nsu.fit.mpm.persistent_ds;

import javafx.util.Pair;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class PersistentHashMap<K, V> {
    private PersistentArray<LinkedList<Pair<K, V>>> table;

    public PersistentHashMap() {
        this.table = new PersistentArray<>(4);
        for (int i = 0; i < table.maxSize; i++) {
            table.add(new LinkedList<Pair<K, V>>());
        }
    }

    public V put(K key, V value) {
        int index = calculateIndex(key.hashCode());
        for (Pair<K, V> pair : table.get(index)) {
            if (pair.getKey().equals(key)) {
                return null;
            }
        }
        table.get(index).add(new Pair<>(key, value));
        return value;
    }

    public V get(K key) {
        int index = calculateIndex(key.hashCode());
        for (Pair<K, V> pair : table.get(index)) {
            if (pair.getKey().equals(key)) {
                return pair.getValue();
            }
        }
        return null;
    }

    public Set<K> keySet() {
        Set<K> setKey = new HashSet<>();
        for (LinkedList<Pair<K, V>> pairs : table) {
            for (Pair<K, V> pair : pairs) {
                setKey.add(pair.getKey());
            }
        }
        return setKey;
    }

    public List<V> values() {
        List<V> values = new LinkedList<>();
        for (LinkedList<Pair<K, V>> pairs : table) {
            for (Pair<K, V> pair : pairs) {
                values.add(pair.getValue());
            }
        }
        return values;
    }

    private int calculateIndex(int hashcode) {
        return hashcode & (table.maxSize - 1);
    }
}

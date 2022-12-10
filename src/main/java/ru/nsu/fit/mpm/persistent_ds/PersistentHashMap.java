package ru.nsu.fit.mpm.persistent_ds;

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class PersistentHashMap<K, V> extends AbstractMap<K, V> implements UndoRedo {

    private PersistentArray<LinkedList<Pair<K, V>>> table;

    public PersistentHashMap() {
        this.table = new PersistentArray<>(16);
        for (int i = 0; i < table.maxSize; i++) {
            table.add(new LinkedList<>());
        }
    }

    @Override
    public V put(K key, V value) {
        int index = calculateIndex(key.hashCode());

        for (int i = 0; i < table.get(index).size(); i++) {
            Pair<K, V> pair = table.get(index).get(i);
            if (pair.getKey().equals(key)) {
                pair.setValue(value);
                return value;
            }
        }

        table.get(index).add(new Pair<>(key, value));
        return value;
    }

    @Override
    public V remove(Object key) {
        for (LinkedList<Pair<K, V>> pairs : table) {
            for (int j = 0; j < pairs.size(); j++) {
                if (pairs.get(j).key.equals(key)) {
                    V value = pairs.get(j).getValue();
                    pairs.remove(j);
                    return value;
                }
            }
        }
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        for (LinkedList<Pair<K, V>> pairs : table) {
            pairs.clear();
        }
    }

    @Override
    public V get(Object key) {
        int index = calculateIndex(key.hashCode());
        for (Pair<K, V> pair : table.get(index)) {
            if (pair.getKey().equals(key)) {
                return pair.getValue();
            }
        }
        return null;
    }

    @Override
    public Set<K> keySet() {
        Set<K> setKey = new HashSet<>();
        for (LinkedList<Pair<K, V>> pairs : table) {
            for (Pair<K, V> pair : pairs) {
                setKey.add(pair.getKey());
            }
        }
        return setKey;
    }

    @Override
    public List<V> values() {
        List<V> values = new LinkedList<>();
        for (LinkedList<Pair<K, V>> pairs : table) {
            for (Pair<K, V> pair : pairs) {
                values.add(pair.getValue());
            }
        }
        return values;
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> es = new HashSet<>();
        for (LinkedList<Pair<K, V>> pairs : table) {
            es.addAll(pairs);
        }
        return es;
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "[]";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        for (Map.Entry<K, V> entry : this.entrySet()) {
            stringBuilder.append(entry);
            stringBuilder.append(" ");
        }
        stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(" "));
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    private int calculateIndex(int hashcode) {
        return hashcode & (table.maxSize - 1);
    }

    @Override
    public void undo() {
    }

    @Override
    public void redo() {
    }

    static class Pair<K, V> implements Map.Entry<K, V> {
        private K key;
        private V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }


        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public String toString() {
            return key + "=" + value;
        }

        @Override
        public int hashCode() {
            return key.hashCode() * 13 + (value == null ? 0 : value.hashCode());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o instanceof Pair) {
                Pair pair = (Pair) o;
                if (!Objects.equals(key, pair.key)) return false;
                return Objects.equals(value, pair.value);
            }
            return false;
        }

        @Override
        public V setValue(V value) {
            return this.value = value;
        }
    }
}
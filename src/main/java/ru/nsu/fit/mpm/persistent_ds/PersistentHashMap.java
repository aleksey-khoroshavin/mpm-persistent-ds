package ru.nsu.fit.mpm.persistent_ds;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;

public class PersistentHashMap<K, V> extends AbstractMap<K, V> implements UndoRedo {

    private ArrayList<PersistentLinkedList<Pair<K, V>>> table;
    private final int tableMaxSize = 16;
    private Stack<Integer> redo = new Stack<>();
    private Stack<Integer> undo = new Stack<>();

    public PersistentHashMap() {
        this.table = new ArrayList<>(30);
        for (int i = 0; i < tableMaxSize; i++) {
            table.add(new PersistentLinkedList<>());
        }
    }

    @Override
    public V put(K key, V value) {
        int index = calculateIndex(key.hashCode());

        for (int i = 0; i < table.get(index).size(); i++) {
            Pair<K, V> pair = table.get(index).get(i);
            if (pair.getKey().equals(key)) {
                table.get(index).get(i).setValue(value);
                return value;
            }
        }

        table.get(index).add(new Pair<>(key, value));
        undo.push(index);
        redo.clear();
        return value;
    }

    @Override
    public V remove(Object key) {
        for (PersistentLinkedList<Pair<K, V>> pairs : table) {
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
        for (PersistentLinkedList<Pair<K, V>> pairs : table) {
            pairs.clear();
        }
    }

    @Override
    public V get(Object key) {
        int index = calculateIndex(key.hashCode());
        PersistentLinkedList<Pair<K, V>> get = table.get(index);
        for (Pair<K, V> pair : get) {
            if (pair.getKey().equals(key)) {
                return pair.getValue();
            }
        }
        return null;
    }

    @Override
    public Set<K> keySet() {
        Set<K> setKey = new HashSet<>();
        for (PersistentLinkedList<Pair<K, V>> pairs : table) {
            for (Pair<K, V> pair : pairs) {
                setKey.add(pair.getKey());
            }
        }
        return setKey;
    }

    @Override
    public List<V> values() {
        List<V> values = new LinkedList<>();
        for (PersistentLinkedList<Pair<K, V>> pairs : table) {
            for (Pair<K, V> pair : pairs) {
                values.add(pair.getValue());
            }
        }
        return values;
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> es = new HashSet<>();
        for (PersistentLinkedList<Pair<K, V>> pairs : table) {
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
        return hashcode & (tableMaxSize - 1);
    }

    @Override
    public void undo() {
        table.get(undo.peek()).undo();
        redo.push(undo.pop());
    }

    @Override
    public void redo() {
        table.get(redo.peek()).redo();
        undo.push(redo.pop());
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
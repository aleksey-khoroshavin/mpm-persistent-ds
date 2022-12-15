package ru.nsu.fit.mpm.persistent_ds;

import java.util.*;

public class PersistentHashMap<K, V> extends AbstractMap<K, V> implements UndoRedo {

    private final ArrayList<PersistentLinkedList<Pair<K, V>>> table;
    private final int tableMaxSize = 16;
    private final Stack<Integer> redo = new Stack<>();
    private final Stack<Integer> undo = new Stack<>();
    private PersistentHashMap<?, PersistentHashMap<?, ?>> parent;
    private int countInsertedHM = 0;
    private final Stack<PersistentHashMap<?, ?>> insertedUndo = new Stack<>();
    private final Stack<PersistentHashMap<?, ?>> insertedRedo = new Stack<>();

    public PersistentHashMap() {
        this.table = new ArrayList<>(30);
        for (int i = 0; i < tableMaxSize; i++) {
            table.add(new PersistentLinkedList<>());
        }
    }

    public PersistentHashMap(PersistentHashMap<K, V> other) {
        this.table = new ArrayList<>(30);
        for (int i = 0; i < tableMaxSize; i++) {
            table.add(new PersistentLinkedList<>(other.table.get(i)));
        }
        this.undo.addAll(other.undo);
        this.redo.addAll(other.redo);
    }

    @Override
    public V put(K key, V value) {
        int index = calculateIndex(key.hashCode());

        for (int i = 0; i < table.get(index).size(); i++) {
            Pair<K, V> pair = table.get(index).get(i);
            if (pair.getKey().equals(key)) {
                table.get(index).set(i, new Pair<>(key, value));
                return value;
            }
        }

        table.get(index).add(new Pair<>(key, value));
        undo.push(index);
        redo.clear();
        tryParentUndo(value);
        return value;
    }

    @Override
    public V remove(Object key) {
        int index = calculateIndex(key.hashCode());
        for (int i = 0; i < table.get(index).size(); i++) {
            Pair<K, V> pair = table.get(index).get(i);
            if (pair.getKey().equals(key)) {
                V value = pair.getValue();
                table.get(index).remove(i);
                undo.push(index);
                redo.clear();
                tryParentUndo((V) this);
                return value;
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

    public PersistentHashMap<K, V> conj(K key, V value) {
        PersistentHashMap<K, V> result = new PersistentHashMap<>(this);
        result.put(key, value);
        return result;
    }

    private int calculateIndex(int hashcode) {
        return hashcode & (tableMaxSize - 1);
    }

    @Override
    public void undo() {
        if (!insertedUndo.empty()) {
            if (insertedUndo.peek().isEmpty()) {
                insertedRedo.push(insertedUndo.pop()); //?
                standardUndo();
            } else {
                PersistentHashMap persistentHashMap = insertedUndo.pop();
                persistentHashMap.undo();
                insertedRedo.push(persistentHashMap);
            }
        } else {
            standardUndo();
        }
    }

    @Override
    public void redo() {
        if (!insertedRedo.empty()) {
            if (insertedRedo.peek().isEmpty()) {
                if (insertedRedo.peek().parent.size() == countInsertedHM) {
                    standardInsertedRedo();
                } else {
                    insertedUndo.push(insertedRedo.pop());
                    standardRedo();
                }
            } else {
                standardInsertedRedo();
            }
        } else {
            standardRedo();
        }
    }

    private void standardInsertedRedo() {
        PersistentHashMap persistentHashMap = insertedRedo.pop();
        persistentHashMap.redo();
        insertedUndo.push(persistentHashMap);
    }

    private void standardRedo() {
        if (!redo.empty()) {
            table.get(redo.peek()).redo();
            undo.push(redo.pop());
        }
    }

    private void standardUndo() {
        if (!undo.empty()) {
            table.get(undo.peek()).undo();
            redo.push(undo.pop());
        }
    }

    private void tryParentUndo(V value) {
        if (value instanceof PersistentHashMap) {
            countInsertedHM++;
            ((PersistentHashMap) value).parent = this;
            insertedUndo.push((PersistentHashMap) value);
            redo.clear();
            insertedRedo.clear();
        }

        if (parent != null) {
            parent.insertedUndo.push(this);
        }
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
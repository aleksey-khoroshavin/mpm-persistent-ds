package ru.nsu.fit.mpm.persistent_ds.map;

import ru.nsu.fit.mpm.persistent_ds.collection.UndoRedoCollection;
import ru.nsu.fit.mpm.persistent_ds.list.PersistentLinkedList;

import java.util.*;

/**
 * Персистентный ассоциативный массив на осове Hash-таблицы
 *
 * @param <K> тип ключей
 * @param <V> тип значений
 */
public class PersistentHashMap<K, V> extends AbstractMap<K, V> implements UndoRedoCollection {
    private static final int TABLE_MAX_SIZE = 16;

    private final ArrayList<PersistentLinkedList<Pair<K, V>>> table;
    private final Stack<Integer> redo = new Stack<>();
    private final Stack<Integer> undo = new Stack<>();
    private final Stack<PersistentHashMap<?, ?>> insertedUndo = new Stack<>();
    private final Stack<PersistentHashMap<?, ?>> insertedRedo = new Stack<>();

    private PersistentHashMap<?, PersistentHashMap<?, ?>> parent;
    private int countInsertedMaps = 0;

    public PersistentHashMap() {
        this.table = new ArrayList<>(30);
        for (int i = 0; i < TABLE_MAX_SIZE; i++) {
            table.add(new PersistentLinkedList<>());
        }
    }

    public PersistentHashMap(PersistentHashMap<K, V> other) {
        this.table = new ArrayList<>(30);
        for (int i = 0; i < TABLE_MAX_SIZE; i++) {
            table.add(new PersistentLinkedList<>(other.table.get(i)));
        }
        this.undo.addAll(other.undo);
        this.redo.addAll(other.redo);
    }

    @Override
    public void undo() {
        if (!insertedUndo.empty()) {
            if (insertedUndo.peek().isEmpty()) {
                insertedRedo.push(insertedUndo.pop());
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
                if (insertedRedo.peek().parent.size() == countInsertedMaps) {
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

    private void standardUndo() {
        if (!undo.empty()) {
            table.get(undo.peek()).undo();
            redo.push(undo.pop());
        }
    }

    private void standardRedo() {
        if (!redo.empty()) {
            table.get(redo.peek()).redo();
            undo.push(redo.pop());
        }
    }

    private void standardInsertedRedo() {
        PersistentHashMap persistentHashMap = insertedRedo.pop();
        persistentHashMap.redo();
        insertedUndo.push(persistentHashMap);
    }

    private void tryParentUndo(V value) {
        if (value instanceof PersistentHashMap) {
            countInsertedMaps++;
            ((PersistentHashMap) value).parent = this;
            insertedUndo.push((PersistentHashMap) value);
            redo.clear();
            insertedRedo.clear();
        }

        if (parent != null) {
            parent.insertedUndo.push(this);
        }
    }

    /**
     * Связывает указанное значение с указанным ключом в этом ассоциативном массиве.
     * Если ассоциативный массив ранее содержал сопоставление для ключа, старое значение заменяется указанным значением.
     *
     * @param key   ключ, с которым должно быть связано указанное значение
     * @param value значение, которое будет связано с указанным ключом
     * @return предыдущее значение, связанное с ключом, или null, если не было сопоставления для ключа
     * (возврат null также может указывать на то, что ассоциативный массив ранее связывал null с ключом)
     */
    @Override
    public V put(K key, V value) {
        V result = get(key);

        int index = calculateIndex(key.hashCode());
        for (int i = 0; i < table.get(index).size(); i++) {
            Pair<K, V> pair = table.get(index).get(i);
            if (pair.getKey().equals(key)) {
                table.get(index).set(i, new Pair<>(key, value));
                undo.push(index);
                redo.clear();
                tryParentUndo(value);

                return result;
            }
        }

        table.get(index).add(new Pair<>(key, value));
        undo.push(index);
        redo.clear();
        tryParentUndo(value);

        return result;
    }

    /**
     * Копирует все сопоставления с указанного ассоциативого массива в этот ассоциативный массив.
     * <p>
     * Эффект от этого вызова эквивалентен эффекту вызова put(k, v) на этой карте один раз
     * для каждого отображения ключа k на значение v в указанной карте.
     * Эта реализация выполняет итерацию по коллекции entrySet() указанного ассоциативного массива
     * и вызывает операцию put этого ассоциативного массива один раз для каждой записи, возвращаемой итерацией.
     * </p>
     *
     * @param m сопоставления, которые будут храниться в этом ассоциативном массиве
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Возвращает копию ассоциативного массива, в которой связывает указанное значение с указанным ключом.
     *
     * @param key   ключ, с которым должно быть связано указанное значение
     * @param value значение, которое будет связано с указанным ключом
     * @return измененная копия ассоциативного массива
     */
    public PersistentHashMap<K, V> conj(K key, V value) {
        PersistentHashMap<K, V> result = new PersistentHashMap<>(this);
        result.put(key, value);
        return result;
    }

    /**
     * Удаляет сопоставление для ключа из этого ассоциативного массива, если оно присутствует.
     * Возвращает значение, с которым эта карта ранее связала ключ, или null, если карта не содержала сопоставления для ключа.
     * <p>
     * Так как этот ассоциативный массив допускает хранение значений null,
     * то возвращаемое значение null не обязательно означает, что ассоциативнный массив не содержит сопоставления для ключа,
     * возможно, что ассоциативнный массив явно сопоставил ключ с нулевым значением.
     * <p>
     * Ассоциативный массив не будет содержать сопоставления для указанного ключа после возврата вызова.
     *
     * @param key ключ, сопоставление которого должно быть удалено из ассоциативного массива
     * @return предыдущее значение, связанное с ключом, или null, если не было сопоставления для указаннного ключа
     */
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

    /**
     * Удаляет все сопоставления из этого ассоциативного массива.
     * Ассоциативный массив будет пустым после возврата этого вызова.
     */
    @Override
    public void clear() {
        for (PersistentLinkedList<Pair<K, V>> pairs : table) {
            pairs.clear();
        }
    }

    /**
     * Возвращает значение, которому сопоставлен указанный ключ, или null, если этот ассоциативный массив не содержит сопоставления для ключа.
     * <p>
     * Возвращаемое значение null не обязательно означает, что ассоциативный массив не содержит сопоставления для ключа;
     * также возможно, что ассоциативный массив явно сопоставляет ключ со значением null.
     * Чтобы различать эти два случая, можно использовать операцию containsKey.
     *
     * @param key ключ, ассоциированное значение которого должно быть возвращено
     * @return значение, которому сопоставлен указанный ключ, или null, если этот ассоциативный массив не содержит сопоставления для ключа
     */
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

    /**
     * Возвращает множество набора ключей, содержащихся в этом ассоциативном массиве.
     *
     * @return множество ключей, содержащихся в этом ассоциативном массиве
     */
    @Override
    public Set<K> keySet() {
        Set<K> keySet = new HashSet<>();
        for (PersistentLinkedList<Pair<K, V>> pairs : table) {
            for (Pair<K, V> pair : pairs) {
                keySet.add(pair.getKey());
            }
        }
        return keySet;
    }

    /**
     * Возвращает множество сопоставлений, содержащихся в этом ассоциативном массиве.
     *
     * @return набор сопоставлений, содержащихся в этом ассоциативном массиве
     */
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> entrySet = new HashSet<>();
        for (PersistentLinkedList<Pair<K, V>> pairs : table) {
            entrySet.addAll(pairs);
        }
        return entrySet;
    }

    /**
     * Возвращает список значений, содержащихся в этом ассоциативном массиве.
     *
     * @return список значений, содержащихся в этом ассоциативном массиве
     */
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

    /**
     * Сравнивает указанный объект с этим ассоциативным массивом на равенство.
     * Возвращает true, если данный объект также является ассоциативным массивом
     * и оба массива представляют одни и те же сопоставления.
     * <p>
     * Этот метод сначала проверяет, является ли указанный объект этим ассоциативным массивом;
     * если это так, он возвращает true.
     * Затем он проверяет, является ли указанный объект ассоциативным массивом, размер которого идентичен размеру этого ассоциативного массива;
     * если нет, он возвращает ложь.
     * Если это так, он выполняет итерацию по коллекции entrySet этого ассоциативного массива и проверяет,
     * содержит ли указанный ассоциативный массив каждое сопоставление, которое содержит этот ассоциативный массив.
     * Если указанный ассоциативный массив не содержит такого сопоставления, возвращается false.
     * Если итерация завершается, возвращается true.
     *
     * @param o объект для сравнения на равенство с этим ассоциативным массивом
     * @return true, если указанный объект равен этому ассоциативному массиву
     */
    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    /**
     * Возвращает значение хэш-кода для этого ассоциативного массива.
     * <p>
     * Хэш-код ассоциативного массива определяется как сумма хэш-кодов каждой записи в представлении entrySet() ассоциативного массива.
     * Этот метод перебирает entrySet(), вызывая hashCode() для каждого элемента (записи) в наборе и суммируя результаты.
     *
     * @return значение хэш-кода для этого ассоциативного массива
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * Возвращает строковое представление этого ассоциативного массива.
     * <p>
     * Строковое представление состоит из списка сопоставлений ключ-значение в порядке,
     * возвращаемом итератором представления entrySet ассоциативного массива, заключенного в фигурные скобки ("{}").
     * Смежные сопоставления разделяются символами ", " (запятая и пробел).
     * Каждое сопоставление "ключ-значение" отображается как ключ, за которым следует знак равенства ("="),
     * за которым следует связанное значение. Ключи и значения преобразуются в строки с помощью String.valueOf(Object).
     *
     * @return строковое представление этого ассоциативного массива
     */
    @Override
    public String toString() {
        if (isEmpty()) {
            return "{}";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");
        for (Map.Entry<K, V> entry : this.entrySet()) {
            stringBuilder.append(entry);
            stringBuilder.append(", ");
        }
        stringBuilder.delete(stringBuilder.lastIndexOf(", "), stringBuilder.lastIndexOf(", ") + 2);
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    private int calculateIndex(int hashcode) {
        return hashcode & (TABLE_MAX_SIZE - 1);
    }

    /**
     * Запись ассоциативного массива (пара ключ-значение).
     */
    static class Pair<K, V> implements Map.Entry<K, V> {
        private final K key;
        private V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        /**
         * Возвращает ключ, соответствующий этой записи.
         *
         * @return ключ, соответствующий этой записи
         */
        @Override
        public K getKey() {
            return key;
        }

        /**
         * Возвращает значение, соответствующее этой записи.
         *
         * @return значение, соответствующее этой записи
         */
        @Override
        public V getValue() {
            return value;
        }

        /**
         * Заменяет значение, соответствующее этой записи, на указанное значение.
         *
         * @param value новое значение, которое будет сохранено в этой записи
         * @return старое значение, соответствующее записи
         */
        @Override
        public V setValue(V value) {
            return this.value = value;
        }

        /**
         * Возвращает строковое представление этой записи ассоциативного массива.
         * Эта реализация возвращает строковое представление ключа этой записи, за которым следует символ равенства ("="),
         * за которым следует строковое представление значения этой записи.
         *
         * @return строковое представление этой записи ассоциативного массива
         */
        @Override
        public String toString() {
            return key + "=" + value;
        }

        /**
         * Возвращает значение хэш-кода для этой записи карты.
         *
         * @return значение хэш-кода для этой записи карты
         */
        @Override
        public int hashCode() {
            return key.hashCode() * 13 + (value == null ? 0 : value.hashCode());
        }

        /**
         * Сравнивает указанный объект с этой записью на равенство.
         * Возвращает true, если данный объект также является записью ассоциативного массива и две записи представляют одно и то же отображение.
         *
         * @param o объект для сравнения на равенство с этой записью ассоциативного массива
         * @return true, если указанный объект равен этой записи ассоциативного массива
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o instanceof Pair) {
                Pair<?, ?> pair = (Pair<?, ?>) o;
                if (!Objects.equals(key, pair.key)) {
                    return false;
                }
                return Objects.equals(value, pair.value);
            }
            return false;
        }
    }
}
package ru.nsu.fit.mpm.persistent_ds;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersistentHashMapTest {
    PersistentHashMap<String, Integer> persistentHashMap = new PersistentHashMap<>();

    private void addABC() {
        persistentHashMap.put("A", 1);
        persistentHashMap.put("B", 2);
        persistentHashMap.put("C", 3);
    }

    @Test
    void testPersistentHashMapPutAndGet() {
        addABC();
        assertEquals(Integer.valueOf(1), persistentHashMap.get("A"));
        assertEquals(Integer.valueOf(2), persistentHashMap.get("B"));
        assertEquals(Integer.valueOf(3), persistentHashMap.get("C"));
    }

    @Test
    void testPersistentHashMapValues() {
        addABC();
        assertEquals("[1, 2, 3]", persistentHashMap.values().toString());
    }

    @Test
    void testPersistentHashMapKeySet() {
        addABC();

        HashSet<String> hs = new HashSet<>();
        hs.add("A");
        hs.add("B");
        hs.add("C");

        assertEquals(hs, persistentHashMap.keySet());
    }

    @Test
    void testPersistentHashMapForEach() {
        addABC();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        for (Map.Entry<String, Integer> entry : persistentHashMap.entrySet()) {
            stringBuilder.append(entry);
            stringBuilder.append(" ");
        }
        stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(" "));
        stringBuilder.append("]");
        assertTrue(stringBuilder.toString().contains("A=1"));
        assertTrue(stringBuilder.toString().contains("B=2"));
        assertTrue(stringBuilder.toString().contains("C=3"));
    }

    @Test
    void testPersistentHashMapUndoRedo() {
        addABC();

        persistentHashMap.undo();
        assertEquals(Integer.valueOf(1), persistentHashMap.get("A"));
        assertEquals(Integer.valueOf(2), persistentHashMap.get("B"));
        assertFalse(persistentHashMap.containsKey("C"));

        persistentHashMap.undo();
        assertEquals(Integer.valueOf(1), persistentHashMap.get("A"));
        assertFalse(persistentHashMap.containsKey("B"));
        assertFalse(persistentHashMap.containsKey("C"));

        persistentHashMap.redo();
        assertEquals(Integer.valueOf(1), persistentHashMap.get("A"));
        assertEquals(Integer.valueOf(2), persistentHashMap.get("B"));
        assertFalse(persistentHashMap.containsKey("C"));

        persistentHashMap.redo();
        assertEquals(Integer.valueOf(1), persistentHashMap.get("A"));
        assertEquals(Integer.valueOf(2), persistentHashMap.get("B"));
        assertEquals(Integer.valueOf(3), persistentHashMap.get("C"));

        persistentHashMap.undo();
        persistentHashMap.undo();
        persistentHashMap.undo();
        assertEquals(0, persistentHashMap.size());

        persistentHashMap.put("Alone", 1);
        assertEquals(Integer.valueOf(1), persistentHashMap.get("Alone"));
    }

    @Test
    void testPersistentHashMapContainsKey() {
        addABC();

        assertEquals(3, persistentHashMap.size());

        assertTrue(persistentHashMap.containsKey("A"));
        assertTrue(persistentHashMap.containsKey("B"));
        assertTrue(persistentHashMap.containsKey("C"));

        assertFalse(persistentHashMap.containsKey("D"));
        assertFalse(persistentHashMap.containsKey("E"));
        assertFalse(persistentHashMap.containsKey("F"));
    }

    @Test
    void testPersistentHashMapContainsValue() {
        addABC();

        assertEquals(3, persistentHashMap.size());

        assertTrue(persistentHashMap.containsValue(1));
        assertTrue(persistentHashMap.containsValue(2));
        assertTrue(persistentHashMap.containsValue(3));

        assertFalse(persistentHashMap.containsValue(4));
        assertFalse(persistentHashMap.containsValue(5));
        assertFalse(persistentHashMap.containsValue(6));
    }

    @Test
    void testPersistentHashMapAPIForEach() {
        addABC();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        persistentHashMap.forEach((k, v) -> stringBuilder.append(k).append(":").append(v).append(" "));
        stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(" "));
        stringBuilder.append("]");

        assertTrue(stringBuilder.toString().contains("A:1"));
        assertTrue(stringBuilder.toString().contains("B:2"));
        assertTrue(stringBuilder.toString().contains("C:3"));
    }

    @Test
    void testPersistentHashMapClear() {
        addABC();
        assertEquals(3, persistentHashMap.size());
        persistentHashMap.clear();
        assertEquals(0, persistentHashMap.size());
    }

    @Test
    void testPersistentHashMapRemove() {
        addABC();

        assertEquals(3, persistentHashMap.size());
        assertEquals(Integer.valueOf(1), persistentHashMap.get("A"));
        assertEquals(Integer.valueOf(2), persistentHashMap.get("B"));
        assertEquals(Integer.valueOf(3), persistentHashMap.get("C"));

        persistentHashMap.remove("A");
        assertFalse(persistentHashMap.containsKey("A"));
        assertEquals(2, persistentHashMap.size());

        persistentHashMap.remove("C");
        assertFalse(persistentHashMap.containsKey("C"));
        assertEquals(1, persistentHashMap.size());
    }

    @Test
    void testPersistentHashMapModifyAndUndoRedo() {
        persistentHashMap.put("key", 12);
        assertEquals(1, persistentHashMap.size());
        assertEquals(Integer.valueOf(12), persistentHashMap.get("key"));

        persistentHashMap.put("key", 1000);
        assertEquals(1, persistentHashMap.size());
        assertEquals(Integer.valueOf(1000), persistentHashMap.get("key"));

        persistentHashMap.undo();
        assertEquals(1, persistentHashMap.size());
        assertEquals(Integer.valueOf(12), persistentHashMap.get("key"));
    }

    @Test
    void testPersistentHashMapToString() {
        addABC();
        assertTrue(persistentHashMap.toString().contains("A=1"));
        assertTrue(persistentHashMap.toString().contains("B=2"));
        assertTrue(persistentHashMap.toString().contains("C=3"));
        assertEquals(13, persistentHashMap.toString().length());
    }

    @Test
    void testPersistentHashMapCascade() {
        PersistentHashMap<String, Integer> version1 = new PersistentHashMap<>();
        version1.put("key1", 1);
        PersistentHashMap<String, Integer> version2 = version1.conj("key2", 2);

        assertEquals(Integer.valueOf(1), version1.get("key1"));
        assertFalse(version1.containsKey("key2"));
        assertEquals(1, version1.size());

        assertEquals(Integer.valueOf(1), version2.get("key1"));
        assertEquals(Integer.valueOf(2), version2.get("key2"));
        assertEquals(2, version2.size());

        PersistentHashMap<String, Integer> version3 = version2.conj("key1", 999);

        assertEquals(Integer.valueOf(1), version1.get("key1"));
        assertFalse(version1.containsKey("key2"));
        assertEquals(1, version1.size());

        assertEquals(Integer.valueOf(1), version2.get("key1"));
        assertEquals(Integer.valueOf(2), version2.get("key2"));
        assertEquals(2, version2.size());

        assertEquals(Integer.valueOf(999), version3.get("key1"));
        assertEquals(Integer.valueOf(2), version3.get("key2"));
        assertEquals(2, version3.size());

        version3.put("key3", 3);
        version3.put("key4", 4);
        assertEquals(4, version3.size());

        version3.remove("key3");
        assertFalse(version3.containsKey("key3"));
        assertEquals(3, version3.size());
    }
}
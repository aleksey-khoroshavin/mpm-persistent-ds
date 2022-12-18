package ru.nsu.fit.mpm.persistent_ds.map;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PersistentHashMapTest {
    PersistentHashMap<String, Integer> persistentHashMap = new PersistentHashMap<>();

    private void fillWithABCData() {
        persistentHashMap.put("A", 1);
        persistentHashMap.put("B", 2);
        persistentHashMap.put("C", 3);
    }

    @Test
    void testPersistentHashMapPutAndGet() {
        fillWithABCData();
        assertEquals(Integer.valueOf(1), persistentHashMap.get("A"));
        assertEquals(Integer.valueOf(2), persistentHashMap.get("B"));
        assertEquals(Integer.valueOf(3), persistentHashMap.get("C"));
    }

    @Test
    void testPersistentHashMapValues() {
        fillWithABCData();
        assertEquals("[1, 2, 3]", persistentHashMap.values().toString());
    }

    @Test
    void testPersistentHashMapKeySet() {
        fillWithABCData();

        HashSet<String> hs = new HashSet<>();
        hs.add("A");
        hs.add("B");
        hs.add("C");

        assertEquals(hs, persistentHashMap.keySet());
    }

    @Test
    void testPersistentHashMapForEach() {
        fillWithABCData();
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
        fillWithABCData();

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
        fillWithABCData();

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
        fillWithABCData();

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
        fillWithABCData();
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
        fillWithABCData();
        assertEquals(3, persistentHashMap.size());
        persistentHashMap.clear();
    }

    @Test
    void testPersistentHashMapRemove() {
        fillWithABCData();

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
        persistentHashMap.put("Test_str_1", 12);
        assertEquals(1, persistentHashMap.size());
        assertEquals(Integer.valueOf(12), persistentHashMap.get("Test_str_1"));

        persistentHashMap.put("Test_str_1", 1000);
        assertEquals(1, persistentHashMap.size());
        assertEquals(Integer.valueOf(1000), persistentHashMap.get("Test_str_1"));

        persistentHashMap.undo();
        assertEquals(1, persistentHashMap.size());
        assertEquals(Integer.valueOf(12), persistentHashMap.get("Test_str_1"));
    }

    @Test
    void testPersistentHashMapToString() {
        fillWithABCData();
        assertTrue(persistentHashMap.toString().contains("A=1"));
        assertTrue(persistentHashMap.toString().contains("B=2"));
        assertTrue(persistentHashMap.toString().contains("C=3"));
        assertEquals(15, persistentHashMap.toString().length());
    }

    @Test
    void testPersistentHashMapCascade() {
        PersistentHashMap<String, Integer> v1 = new PersistentHashMap<>();
        v1.put("Test_str_1", 1);
        PersistentHashMap<String, Integer> v2 = v1.conj("Test_str_2", 2);

        assertEquals(Integer.valueOf(1), v1.get("Test_str_1"));
        assertFalse(v1.containsKey("Test_str_2"));
        assertEquals(1, v1.size());

        assertEquals(Integer.valueOf(1), v2.get("Test_str_1"));
        assertEquals(Integer.valueOf(2), v2.get("Test_str_2"));
        assertEquals(2, v2.size());

        //assoc
        PersistentHashMap<String, Integer> v3 = v2.conj("Test_str_1", 999);

        assertEquals(Integer.valueOf(1), v1.get("Test_str_1"));
        assertFalse(v1.containsKey("Test_str_2"));
        assertEquals(1, v1.size());

        assertEquals(Integer.valueOf(1), v2.get("Test_str_1"));
        assertEquals(Integer.valueOf(2), v2.get("Test_str_2"));
        assertEquals(2, v2.size());

        assertEquals(Integer.valueOf(999), v3.get("Test_str_1"));
        assertEquals(Integer.valueOf(2), v3.get("Test_str_2"));
        assertEquals(2, v3.size());

        v3.put("Test_str_3", 3);
        v3.put("Test_str_4", 4);
        assertEquals(4, v3.size());

        v3.remove("Test_str_3");
        assertFalse(v3.containsKey("Test_str_3"));
        assertEquals(3, v3.size());
    }
}
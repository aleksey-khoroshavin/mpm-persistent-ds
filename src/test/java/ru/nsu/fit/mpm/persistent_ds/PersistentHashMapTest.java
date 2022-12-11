package ru.nsu.fit.mpm.persistent_ds;

import org.junit.jupiter.api.Test;

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
        assertEquals(1, persistentHashMap.get("A").intValue());
        assertEquals(2, persistentHashMap.get("B").intValue());
        assertEquals(3, persistentHashMap.get("C").intValue());
    }

    @Test
    void testPersistentHashMapValues() {
        addABC();
        assertEquals("[1, 2, 3]", persistentHashMap.values().toString());
    }

    @Test
    void testPersistentHashMapKeySet() {
        addABC();
        assertTrue(persistentHashMap.keySet().toString().contains("A"));
        assertTrue(persistentHashMap.keySet().toString().contains("B"));
        assertTrue(persistentHashMap.keySet().toString().contains("C"));
        assertFalse(persistentHashMap.keySet().toString().contains("D"));
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
    void testPersistentHashMapContainsKey() {
        addABC();

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

        assertTrue(persistentHashMap.containsValue(1));
        assertTrue(persistentHashMap.containsValue(2));
        assertTrue(persistentHashMap.containsValue(3));

        assertFalse(persistentHashMap.containsValue(4));
        assertFalse(persistentHashMap.containsValue(5));
        assertFalse(persistentHashMap.containsValue(6));
    }

    @Test
    void testPersistentHashMapAPI() {
        addABC();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        persistentHashMap.forEach((k, v) -> stringBuilder.append(k).append(":").append(v).append(" "));
        stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(" "));
        stringBuilder.append("]");
        assertEquals("[C:3 B:2 A:1]", stringBuilder.toString());
    }
}
package ru.nsu.fit.mpm.persistent_ds;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PersistentHashMapTest {
    PersistentHashMap<String, Integer> persistentHashMap = new PersistentHashMap<>();

    @AfterEach
    public void clear() {
        persistentHashMap = new PersistentHashMap<>();
    }

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
    }

}

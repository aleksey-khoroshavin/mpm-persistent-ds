package ru.nsu.fit.mpm.persistent_ds;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PersistentLinkedListTest {
    PersistentLinkedList<Integer> persistentLinkedList;

    private void init(int fillSize) {
        persistentLinkedList = new PersistentLinkedList<>(100);
        fill(fillSize);
    }

    private void init(int fillSize, int maxSize) {
        persistentLinkedList = new PersistentLinkedList<>(maxSize);
        fill(fillSize);
    }

    private void init(int fillSize, int depth, int bitPerNode) {
        persistentLinkedList = new PersistentLinkedList<>(depth, bitPerNode);
        fill(fillSize);
    }

    private void fill(int size) {
        for (int i = 0; i < size; i++)
            persistentLinkedList.add(i);
    }

    @Test
    public void add() {
        init(0);

        persistentLinkedList.add(3);
        assertEquals(1, persistentLinkedList.getUniqueLeafsSize());
        assertEquals(2, persistentLinkedList.getVersionCount());
        assertEquals(0, persistentLinkedList.getCurrentHead().first);
        assertEquals(0, persistentLinkedList.getCurrentHead().last);
        assertEquals(1, persistentLinkedList.size());

        persistentLinkedList.add(4);
        assertEquals(2, persistentLinkedList.getUniqueLeafsSize());
        assertEquals(3, persistentLinkedList.getVersionCount());
        assertEquals(0, persistentLinkedList.getCurrentHead().first);
        assertEquals(1, persistentLinkedList.getCurrentHead().last);
        assertEquals(2, persistentLinkedList.size());

        persistentLinkedList.add(6);
        assertEquals(3, persistentLinkedList.getUniqueLeafsSize());
        assertEquals(4, persistentLinkedList.getVersionCount());
        assertEquals(0, persistentLinkedList.getCurrentHead().first);
        assertEquals(2, persistentLinkedList.getCurrentHead().last);
        assertEquals(3, persistentLinkedList.size());

        persistentLinkedList.add(9);
        assertEquals(4, persistentLinkedList.getUniqueLeafsSize());
        assertEquals(5, persistentLinkedList.getVersionCount());
        assertEquals(0, persistentLinkedList.getCurrentHead().first);
        assertEquals(3, persistentLinkedList.getCurrentHead().last);
        assertEquals(4, persistentLinkedList.size());

        persistentLinkedList.undo();
        assertEquals(4, persistentLinkedList.getUniqueLeafsSize());
        assertEquals(5, persistentLinkedList.getVersionCount());
        assertEquals(0, persistentLinkedList.getCurrentHead().first);
        assertEquals(2, persistentLinkedList.getCurrentHead().last);
        assertEquals(3, persistentLinkedList.size());

    }
}

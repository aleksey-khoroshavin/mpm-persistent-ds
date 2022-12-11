package ru.nsu.fit.mpm.persistent_ds;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    private <E> String valuesToString(PersistentLinkedList<E> list) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            stringBuilder.append(list.get(i));
        }

        return stringBuilder.toString();
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
        assertEquals("3", valuesToString(persistentLinkedList));

        persistentLinkedList.add(4);
        assertEquals(2, persistentLinkedList.getUniqueLeafsSize());
        assertEquals(3, persistentLinkedList.getVersionCount());
        assertEquals(0, persistentLinkedList.getCurrentHead().first);
        assertEquals(1, persistentLinkedList.getCurrentHead().last);
        assertEquals(2, persistentLinkedList.size());
        assertEquals("34", valuesToString(persistentLinkedList));

        persistentLinkedList.add(6);
        assertEquals(3, persistentLinkedList.getUniqueLeafsSize());
        assertEquals(4, persistentLinkedList.getVersionCount());
        assertEquals(0, persistentLinkedList.getCurrentHead().first);
        assertEquals(2, persistentLinkedList.getCurrentHead().last);
        assertEquals(3, persistentLinkedList.size());
        assertEquals("346", valuesToString(persistentLinkedList));

        persistentLinkedList.add(9);
        assertEquals(4, persistentLinkedList.getUniqueLeafsSize());
        assertEquals(5, persistentLinkedList.getVersionCount());
        assertEquals(0, persistentLinkedList.getCurrentHead().first);
        assertEquals(3, persistentLinkedList.getCurrentHead().last);
        assertEquals(4, persistentLinkedList.size());
        assertEquals("3469", valuesToString(persistentLinkedList));

        persistentLinkedList.undo();
        assertEquals(4, persistentLinkedList.getUniqueLeafsSize());
        assertEquals(5, persistentLinkedList.getVersionCount());
        assertEquals(0, persistentLinkedList.getCurrentHead().first);
        assertEquals(2, persistentLinkedList.getCurrentHead().last);
        assertEquals(3, persistentLinkedList.size());
        assertEquals("346", valuesToString(persistentLinkedList));
    }

    @Test
    public void insertMemCopy() {
        init(0);
        persistentLinkedList.add(3);
        persistentLinkedList.add(4);
        persistentLinkedList.add(6);
        persistentLinkedList.add(0);
        persistentLinkedList.add(7);
        assertEquals(5, persistentLinkedList.getUniqueLeafsSize());
        assertEquals(6, persistentLinkedList.getVersionCount());
        assertEquals(0, persistentLinkedList.getCurrentHead().first);
        assertEquals(4, persistentLinkedList.getCurrentHead().last);
        assertEquals(5, persistentLinkedList.size());
        assertEquals("34607", valuesToString(persistentLinkedList));

        persistentLinkedList.add(3, 9);
        assertEquals(6, persistentLinkedList.getUniqueLeafsSize());
        assertEquals(7, persistentLinkedList.getVersionCount());
        assertEquals(0, persistentLinkedList.getCurrentHead().first);
        assertEquals(4, persistentLinkedList.getCurrentHead().last);
        assertEquals(6, persistentLinkedList.size());
        assertEquals("346907", valuesToString(persistentLinkedList));
    }

    @Test
    public void insert() {
        init(0);
        persistentLinkedList.add(3);
        persistentLinkedList.add(4);
        persistentLinkedList.add(6);
        assertEquals(3, persistentLinkedList.getUniqueLeafsSize());
        assertEquals(4, persistentLinkedList.getVersionCount());
        assertEquals(0, persistentLinkedList.getCurrentHead().first);
        assertEquals(2, persistentLinkedList.getCurrentHead().last);
        assertEquals(3, persistentLinkedList.size());
        assertEquals("346", valuesToString(persistentLinkedList));

        persistentLinkedList.add(1, 9);
        assertEquals(4, persistentLinkedList.getUniqueLeafsSize());
        assertEquals(5, persistentLinkedList.getVersionCount());
        assertEquals(0, persistentLinkedList.getCurrentHead().first);
        assertEquals(2, persistentLinkedList.getCurrentHead().last);
        assertEquals(4, persistentLinkedList.size());
        assertEquals("3946", valuesToString(persistentLinkedList));

        persistentLinkedList.add(1, 7);
        assertEquals(5, persistentLinkedList.getUniqueLeafsSize());
        assertEquals(6, persistentLinkedList.getVersionCount());
        assertEquals(0, persistentLinkedList.getCurrentHead().first);
        assertEquals(2, persistentLinkedList.getCurrentHead().last);
        assertEquals(5, persistentLinkedList.size());
        assertEquals("37946", valuesToString(persistentLinkedList));

        persistentLinkedList.add(8);
        assertEquals(6, persistentLinkedList.getUniqueLeafsSize());
        assertEquals(7, persistentLinkedList.getVersionCount());
        assertEquals(0, persistentLinkedList.getCurrentHead().first);
        assertEquals(5, persistentLinkedList.getCurrentHead().last);
        assertEquals(6, persistentLinkedList.size());
        assertEquals("379468", valuesToString(persistentLinkedList));
    }

    @Test
    public void insertIntoBeginAndEnd() {
        init(0, 3, 1);
        persistentLinkedList.add(1);
        persistentLinkedList.add(2);
        persistentLinkedList.add(3);
        assertEquals(4, persistentLinkedList.getUniqueLeafsSize());
        assertEquals(4, persistentLinkedList.getVersionCount());
        assertEquals(0, persistentLinkedList.getCurrentHead().first);
        assertEquals(2, persistentLinkedList.getCurrentHead().last);
        assertEquals(3, persistentLinkedList.size());
        assertEquals("123", valuesToString(persistentLinkedList));

        persistentLinkedList.add(0, 4);
        assertEquals(6, persistentLinkedList.getUniqueLeafsSize());
        assertEquals(5, persistentLinkedList.getVersionCount());
        assertEquals(3, persistentLinkedList.getCurrentHead().first);
        assertEquals(2, persistentLinkedList.getCurrentHead().last);
        assertEquals(4, persistentLinkedList.size());
        assertEquals("4123", valuesToString(persistentLinkedList));

        persistentLinkedList.add(0, 5);
        assertEquals(8, persistentLinkedList.getUniqueLeafsSize());
        assertEquals(6, persistentLinkedList.getVersionCount());
        assertEquals(4, persistentLinkedList.getCurrentHead().first);
        assertEquals(2, persistentLinkedList.getCurrentHead().last);
        assertEquals(5, persistentLinkedList.size());
        assertEquals("54123", valuesToString(persistentLinkedList));

        assertThrows(IndexOutOfBoundsException.class, () -> persistentLinkedList.add(5, 6));
    }
}
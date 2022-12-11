package ru.nsu.fit.mpm.persistent_ds;

import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

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
        for (int i = 0; i < size; i++) {
            persistentLinkedList.add(i);
        }
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
        assertEquals("[3]", persistentLinkedList.toString());

        persistentLinkedList.add(4);
        assertEquals(2, persistentLinkedList.getUniqueLeafsSize());
        assertEquals(3, persistentLinkedList.getVersionCount());
        assertEquals(0, persistentLinkedList.getCurrentHead().first);
        assertEquals(1, persistentLinkedList.getCurrentHead().last);
        assertEquals(2, persistentLinkedList.size());
        assertEquals("[3, 4]", persistentLinkedList.toString());

        persistentLinkedList.add(6);
        assertEquals(3, persistentLinkedList.getUniqueLeafsSize());
        assertEquals(4, persistentLinkedList.getVersionCount());
        assertEquals(0, persistentLinkedList.getCurrentHead().first);
        assertEquals(2, persistentLinkedList.getCurrentHead().last);
        assertEquals(3, persistentLinkedList.size());
        assertEquals("[3, 4, 6]", persistentLinkedList.toString());

        persistentLinkedList.add(9);
        assertEquals(4, persistentLinkedList.getUniqueLeafsSize());
        assertEquals(5, persistentLinkedList.getVersionCount());
        assertEquals(0, persistentLinkedList.getCurrentHead().first);
        assertEquals(3, persistentLinkedList.getCurrentHead().last);
        assertEquals(4, persistentLinkedList.size());
        assertEquals("[3, 4, 6, 9]", persistentLinkedList.toString());

        persistentLinkedList.undo();
        assertEquals(4, persistentLinkedList.getUniqueLeafsSize());
        assertEquals(5, persistentLinkedList.getVersionCount());
        assertEquals(0, persistentLinkedList.getCurrentHead().first);
        assertEquals(2, persistentLinkedList.getCurrentHead().last);
        assertEquals(3, persistentLinkedList.size());
        assertEquals("[3, 4, 6]", persistentLinkedList.toString());
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
        assertEquals("[3, 4, 6, 0, 7]", persistentLinkedList.toString());

        persistentLinkedList.add(3, 9);
        assertEquals(6, persistentLinkedList.getUniqueLeafsSize());
        assertEquals(7, persistentLinkedList.getVersionCount());
        assertEquals(0, persistentLinkedList.getCurrentHead().first);
        assertEquals(4, persistentLinkedList.getCurrentHead().last);
        assertEquals(6, persistentLinkedList.size());
        assertEquals("[3, 4, 6, 9, 0, 7]", persistentLinkedList.toString());
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
        assertEquals("[3, 4, 6]", persistentLinkedList.toString());

        persistentLinkedList.add(1, 9);
        assertEquals(4, persistentLinkedList.getUniqueLeafsSize());
        assertEquals(5, persistentLinkedList.getVersionCount());
        assertEquals(0, persistentLinkedList.getCurrentHead().first);
        assertEquals(2, persistentLinkedList.getCurrentHead().last);
        assertEquals(4, persistentLinkedList.size());
        assertEquals("[3, 9, 4, 6]", persistentLinkedList.toString());

        persistentLinkedList.add(1, 7);
        assertEquals(5, persistentLinkedList.getUniqueLeafsSize());
        assertEquals(6, persistentLinkedList.getVersionCount());
        assertEquals(0, persistentLinkedList.getCurrentHead().first);
        assertEquals(2, persistentLinkedList.getCurrentHead().last);
        assertEquals(5, persistentLinkedList.size());
        assertEquals("[3, 7, 9, 4, 6]", persistentLinkedList.toString());

        persistentLinkedList.add(8);
        assertEquals(6, persistentLinkedList.getUniqueLeafsSize());
        assertEquals(7, persistentLinkedList.getVersionCount());
        assertEquals(0, persistentLinkedList.getCurrentHead().first);
        assertEquals(5, persistentLinkedList.getCurrentHead().last);
        assertEquals(6, persistentLinkedList.size());
        assertEquals("[3, 7, 9, 4, 6, 8]", persistentLinkedList.toString());
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
        assertEquals("[1, 2, 3]", persistentLinkedList.toString());

        persistentLinkedList.add(0, 4);
        assertEquals(6, persistentLinkedList.getUniqueLeafsSize());
        assertEquals(5, persistentLinkedList.getVersionCount());
        assertEquals(3, persistentLinkedList.getCurrentHead().first);
        assertEquals(2, persistentLinkedList.getCurrentHead().last);
        assertEquals(4, persistentLinkedList.size());
        assertEquals("[4, 1, 2, 3]", persistentLinkedList.toString());

        persistentLinkedList.add(0, 5);
        assertEquals(8, persistentLinkedList.getUniqueLeafsSize());
        assertEquals(6, persistentLinkedList.getVersionCount());
        assertEquals(4, persistentLinkedList.getCurrentHead().first);
        assertEquals(2, persistentLinkedList.getCurrentHead().last);
        assertEquals(5, persistentLinkedList.size());
        assertEquals("[5, 4, 1, 2, 3]", persistentLinkedList.toString());

        assertThrows(IndexOutOfBoundsException.class, () -> persistentLinkedList.add(5, 6));
    }

    @Test
    public void testPersistentLinkedListIterator() {
        init(3);
        Iterator<Integer> i = persistentLinkedList.iterator();
        assertTrue(i.hasNext());
        assertEquals(Integer.valueOf(0), i.next());
        assertEquals(Integer.valueOf(1), i.next());
        assertEquals(Integer.valueOf(2), i.next());
        assertFalse(i.hasNext());
        persistentLinkedList.add(3);
        assertFalse(i.hasNext());

        i = persistentLinkedList.iterator();
        assertTrue(i.hasNext());
        assertEquals(Integer.valueOf(0), i.next());
        assertEquals(Integer.valueOf(1), i.next());
        assertEquals(Integer.valueOf(2), i.next());
        assertEquals(Integer.valueOf(3), i.next());
        assertFalse(i.hasNext());

        persistentLinkedList = new PersistentLinkedList<>(3, 1);
        persistentLinkedList.add(3);
        persistentLinkedList.add(4);
        persistentLinkedList.remove(0);
        assertEquals("[4]", persistentLinkedList.toString());
        i = persistentLinkedList.iterator();
        assertTrue(i.hasNext());
        assertEquals(Integer.valueOf(4), i.next());
        assertFalse(i.hasNext());

        persistentLinkedList = new PersistentLinkedList<>();
        i = persistentLinkedList.iterator();
        assertFalse(i.hasNext());
    }

    @Test
    public void testPersistentLinkedListRemove() {
        init(3);
        assertEquals("[0, 1, 2]", persistentLinkedList.toString());
        persistentLinkedList.remove(0);
        assertEquals("[1, 2]", persistentLinkedList.toString());
        persistentLinkedList.remove(0);
        assertEquals("[2]", persistentLinkedList.toString());
        assertEquals(1, persistentLinkedList.size());
        persistentLinkedList.add(3);
        persistentLinkedList.add(4);
        persistentLinkedList.add(5);
        assertEquals("[2, 3, 4, 5]", persistentLinkedList.toString());
        persistentLinkedList.remove(2);
        assertEquals("[2, 3, 5]", persistentLinkedList.toString());
        assertEquals(3, persistentLinkedList.size());
        assertThrows(IndexOutOfBoundsException.class, () -> persistentLinkedList.set(999, 10));
        assertThrows(IndexOutOfBoundsException.class, () -> persistentLinkedList.set(-1, 10));
    }

    @Test
    public void testPersistentLinkedListSet() {
        init(3);
        assertEquals("[0, 1, 2]", persistentLinkedList.toString());
        persistentLinkedList.set(1, -1);
        assertEquals("[0, -1, 2]", persistentLinkedList.toString());
        persistentLinkedList.set(2, -2);
        assertEquals("[0, -1, -2]", persistentLinkedList.toString());

        persistentLinkedList.undo();
        persistentLinkedList.undo();
        assertEquals("[0, 1, 2]", persistentLinkedList.toString());

        assertThrows(IndexOutOfBoundsException.class, () -> persistentLinkedList.set(999, 10));
        assertThrows(IndexOutOfBoundsException.class, () -> persistentLinkedList.set(-1, 10));
    }

    @Test
    public void testPersistentLinkedListRemoveLastElement() {
        init(2);
        assertEquals("[0, 1]", persistentLinkedList.toString());
        persistentLinkedList.remove(1);
        assertEquals("[0]", persistentLinkedList.toString());
        persistentLinkedList.add(2);
        assertEquals("[0, 2]", persistentLinkedList.toString());
    }

    @Test
    public void testPersistentLinkedListRemoveMiddleElement() {
        init(3);
        assertEquals("[0, 1, 2]", persistentLinkedList.toString());
        persistentLinkedList.remove(1);
        assertEquals("[0, 2]", persistentLinkedList.toString());
        persistentLinkedList.set(1, 9);
        assertEquals("[0, 9]", persistentLinkedList.toString());
        persistentLinkedList.undo();
        assertEquals("[0, 2]", persistentLinkedList.toString());
    }

    @Test
    public void testPersistentLinkedListUndoRedo() {
        init(3);
        assertEquals("[0, 1, 2]", persistentLinkedList.toString());

        persistentLinkedList.add(3);
        assertEquals("[0, 1, 2, 3]", persistentLinkedList.toString());
        persistentLinkedList.undo();
        assertEquals("[0, 1, 2]", persistentLinkedList.toString());
        persistentLinkedList.redo();
        assertEquals("[0, 1, 2, 3]", persistentLinkedList.toString());

        persistentLinkedList.set(1, -1);
        assertEquals("[0, -1, 2, 3]", persistentLinkedList.toString());
        persistentLinkedList.undo();
        assertEquals("[0, 1, 2, 3]", persistentLinkedList.toString());
        persistentLinkedList.redo();
        assertEquals("[0, -1, 2, 3]", persistentLinkedList.toString());

        persistentLinkedList.remove(2);
        assertEquals("[0, -1, 3]", persistentLinkedList.toString());
        persistentLinkedList.undo();
        assertEquals("[0, -1, 2, 3]", persistentLinkedList.toString());
        persistentLinkedList.redo();
        assertEquals("[0, -1, 3]", persistentLinkedList.toString());
    }

    @Test
    public void testPersistentLinkedListRemoveAllElementsAndUndoRedo() {
        init(3);
        assertEquals("[0, 1, 2]", persistentLinkedList.toString());
        persistentLinkedList.remove(0);
        persistentLinkedList.remove(0);
        persistentLinkedList.remove(0);
        assertEquals("[]", persistentLinkedList.toString());

        persistentLinkedList.undo();
        persistentLinkedList.undo();
        persistentLinkedList.undo();

        assertEquals("[0, 1, 2]", persistentLinkedList.toString());
        persistentLinkedList.remove(0);
        persistentLinkedList.remove(0);
        persistentLinkedList.remove(0);
        assertEquals("[]", persistentLinkedList.toString());

        persistentLinkedList.add(-1);
        persistentLinkedList.add(-2);
        persistentLinkedList.add(-3);
        assertEquals("[-1, -2, -3]", persistentLinkedList.toString());
        persistentLinkedList.remove(0);
        persistentLinkedList.remove(0);
        persistentLinkedList.remove(0);

        persistentLinkedList.undo();
        persistentLinkedList.undo();
        persistentLinkedList.undo();
        persistentLinkedList.redo();
        persistentLinkedList.redo();
        persistentLinkedList.redo();

        assertEquals("[]", persistentLinkedList.toString());
    }

    @Test
    public void testPersistentLinkedListForEach() {
        init(3);
        StringBuilder stringBuilder = new StringBuilder();
        for (Integer i : persistentLinkedList) {
            stringBuilder.append(i);
        }

        assertEquals("012", stringBuilder.toString());

        PersistentLinkedList<PersistentHashMap.Pair<String, Integer>> persistentLinkedList2 = new PersistentLinkedList<>();
        persistentLinkedList2.add(new PersistentHashMap.Pair<>("Str1", 1));
        persistentLinkedList2.add(new PersistentHashMap.Pair<>("Str2", 2));
        persistentLinkedList2.add(new PersistentHashMap.Pair<>("Str3", 3));

        stringBuilder = new StringBuilder();
        for (PersistentHashMap.Pair<String, Integer> pair : persistentLinkedList2) {
            stringBuilder.append("[");
            stringBuilder.append(pair.getKey());
            stringBuilder.append(" ");
            stringBuilder.append(pair.getValue());
            stringBuilder.append("]");
        }

        assertEquals("[Str1 1][Str2 2][Str3 3]", stringBuilder.toString());
    }

    @Test
    public void testPersistentLinkedListClear() {
        init(3);
        assertEquals("[0, 1, 2]", persistentLinkedList.toString());
        assertEquals(3, persistentLinkedList.size());

        persistentLinkedList.clear();
        assertEquals("[]", persistentLinkedList.toString());
        assertEquals(0, persistentLinkedList.size());

        persistentLinkedList.undo();
        assertEquals("[0, 1, 2]", persistentLinkedList.toString());
        assertEquals(3, persistentLinkedList.size());

        persistentLinkedList.redo();
        assertEquals("[]", persistentLinkedList.toString());
        assertEquals(0, persistentLinkedList.size());

        persistentLinkedList.add(1);
        persistentLinkedList.add(2);
        persistentLinkedList.add(3);
        assertEquals("[1, 2, 3]", persistentLinkedList.toString());
        assertEquals(3, persistentLinkedList.size());
    }
}
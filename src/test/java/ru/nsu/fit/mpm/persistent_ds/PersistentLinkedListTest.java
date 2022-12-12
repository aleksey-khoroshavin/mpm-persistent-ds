package ru.nsu.fit.mpm.persistent_ds;

import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersistentLinkedListTest {
    PersistentLinkedList<Integer> persistentLinkedList;

    private void init(int fillSize) {
        persistentLinkedList = new PersistentLinkedList<>(100);
        fill(fillSize);
    }

    private void init(int fillSize, int depth, int bit_na_pu) {
        persistentLinkedList = new PersistentLinkedList<>(depth, bit_na_pu);
        fill(fillSize);
    }

    private void fill(int size) {
        for (int i = 0; i < size; i++)
            persistentLinkedList.add(i);
    }

    @Test
    void add() {
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
    void insertMemCopy() {
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
    void insert() {
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
    void insertIntoBeginAndEnd() {
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
    void testPersistentLinkedListIterator() {
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
    void testPersistentLinkedListRemove() {
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
    void testPersistentLinkedListSet() {
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
    void testPersistentLinkedListRemoveLastElement() {
        init(2);
        assertEquals("[0, 1]", persistentLinkedList.toString());
        persistentLinkedList.remove(1);
        assertEquals("[0]", persistentLinkedList.toString());
        persistentLinkedList.add(2);
        assertEquals("[0, 2]", persistentLinkedList.toString());
    }

    @Test
    void testPersistentLinkedListRemoveMiddleElement() {
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
    void testPersistentLinkedListUndoRedo() {
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
    void testPersistentLinkedListRemoveAllElementsAndUndoRedo() {
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
    void testPersistentLinkedListForEach() {
        init(3);
        StringBuilder stringBuilder = new StringBuilder();
        for (Integer i : persistentLinkedList) {
            stringBuilder.append(i);
        }

        assertEquals("012", stringBuilder.toString());

        PersistentLinkedList<PersistentHashMap.Pair<String, Integer>> pl2 = new PersistentLinkedList<>();
        pl2.add(new PersistentHashMap.Pair<>("Test_str_1", 1));
        pl2.add(new PersistentHashMap.Pair<>("Test_str_2", 2));
        pl2.add(new PersistentHashMap.Pair<>("Test_str_3", 3));

        stringBuilder = new StringBuilder();
        for (PersistentHashMap.Pair<String, Integer> pair : pl2) {
            stringBuilder.append("[");
            stringBuilder.append(pair.getKey());
            stringBuilder.append(" ");
            stringBuilder.append(pair.getValue());
            stringBuilder.append("]");
        }

        assertEquals("[Test_str_1 1][Test_str_2 2][Test_str_3 3]", stringBuilder.toString());
    }

    @Test
    void testPersistentLinkedListClear() {
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

    @Test
    void testPersistentLinkedListCascade() {
        PersistentLinkedList<String> v1 = new PersistentLinkedList<>();
        v1.add("Test_str_1");

        PersistentLinkedList<String> v2 = v1.conj("Test_str_2");

        assertEquals("[Test_str_1]", v1.toString());
        assertEquals("[Test_str_1, Test_str_2]", v2.toString());

        PersistentLinkedList<String> v3 = v2.assoc(0, "Test_str_3");

        assertEquals("[Test_str_1]", v1.toString());
        assertEquals("[Test_str_1, Test_str_2]", v2.toString());
        assertEquals("[Test_str_3, Test_str_2]", v3.toString());

        v3.add("3");
        v3.add("4");
        assertEquals("[Test_str_3, Test_str_2, 3, 4]", v3.toString());

        v3.remove(2);
        assertEquals("[Test_str_3, Test_str_2, 4]", v3.toString());
    }

    @Test
    void testPersistentLinkedListInsertedUndoRedo() {
        PersistentLinkedList<PersistentLinkedList<String>> parent = new PersistentLinkedList<>();
        PersistentLinkedList<String> child1 = new PersistentLinkedList<>();
        PersistentLinkedList<String> child2 = new PersistentLinkedList<>();
        PersistentLinkedList<String> child3 = new PersistentLinkedList<>();
        parent.add(child1);
        parent.add(child2);
        parent.add(child3);

        parent.get(0).add("1");
        parent.get(0).add("2");
        parent.get(0).add("3");

        parent.get(1).add("11");
        parent.get(1).add("22");
        parent.get(1).add("33");

        parent.get(2).add("111");
        parent.get(2).add("222");
        parent.get(2).add("333");

        assertEquals("[[1, 2, 3], [11, 22, 33], [111, 222, 333]]", parent.toString());
        parent.undo();
    }

    @Test
    void testPersistentLinkedListMemoryReuse() {
        PersistentLinkedList<Integer> linkedList = new PersistentLinkedList<>(4, 1);

        linkedList.add(3);
        linkedList.add(4);
        linkedList.add(5);
        assertEquals("[3, 4, 5]", linkedList.toString());
        assertEquals(3, linkedList.getCurrentHead().sizeTree);

        linkedList.remove(1);
        assertEquals("[3, 5]", linkedList.toString());
        assertEquals(3, linkedList.getCurrentHead().sizeTree);

        linkedList.add(6);
        assertEquals("[3, 5, 6]", linkedList.toString());
        assertEquals(3, linkedList.getCurrentHead().sizeTree);

        linkedList.undo();
        assertEquals("[3, 5]", linkedList.toString());
        assertEquals(3, linkedList.getCurrentHead().sizeTree);

        linkedList.add(7);
        assertEquals("[3, 5, 7]", linkedList.toString());
        assertEquals(3, linkedList.getCurrentHead().sizeTree);

        linkedList.add(8);
        assertEquals("[3, 5, 7, 8]", linkedList.toString());
        assertEquals(4, linkedList.getCurrentHead().sizeTree);
    }
}
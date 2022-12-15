package ru.nsu.fit.mpm.persistent_ds.array;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersistentArrayTest {
    PersistentArray<String> persistentArray;

    private void addABC() {
        persistentArray = new PersistentArray<>(32);
        persistentArray.add("A");
        persistentArray.add("B");
        persistentArray.add("C");
    }

    private void addABC(int depth, int bitPerNode) {
        persistentArray = new PersistentArray<>(depth, bitPerNode);
        persistentArray.add("A");
        persistentArray.add("B");
        persistentArray.add("C");
    }

    @Test
    void testPersistentArrayAddAndGet() {
        addABC();
        assertEquals("A", persistentArray.get(0));
        assertEquals("B", persistentArray.get(1));
        assertEquals("C", persistentArray.get(2));
    }

    @Test
    void testPersistentArrayToArray() {
        addABC();
        assertEquals("[A, B, C]", Arrays.toString(persistentArray.toArray()));
    }

    @Test
    void testPersistentArraySize() {
        persistentArray = new PersistentArray<>(32);
        assertEquals(persistentArray.size(), 0);
        persistentArray.add("A");
        persistentArray.add("B");
        persistentArray.add("C");
        assertEquals(persistentArray.size(), 3);
    }

    @Test
    void testPersistentAdd() {
        persistentArray = new PersistentArray<>(1, 1);
        assertEquals(2, persistentArray.maxSize);

        assertTrue(persistentArray.add("A"));
        assertTrue(persistentArray.add("B"));
    }

    @Test
    void testPersistentArrayIsEmpty() {
        persistentArray = new PersistentArray<>(32);
        assertTrue(persistentArray.isEmpty());
        persistentArray.add("A");
        assertFalse(persistentArray.isEmpty());
    }

    @Test
    void testPersistentArrayUndoRedo() {
        addABC();
        assertEquals(4, persistentArray.getVersionCount());
        persistentArray.undo();
        assertEquals(4, persistentArray.getVersionCount());
        persistentArray.undo();
        assertEquals(4, persistentArray.getVersionCount());
        assertEquals("[A]", persistentArray.toString());

        persistentArray.redo();
        assertEquals(4, persistentArray.getVersionCount());
        assertEquals("[A, B]", persistentArray.toString());

        persistentArray.undo();
        assertEquals(4, persistentArray.getVersionCount());
        persistentArray.undo();
        assertEquals(4, persistentArray.getVersionCount());
        assertEquals("[]", persistentArray.toString());

        persistentArray.redo();
        assertEquals(4, persistentArray.getVersionCount());
        persistentArray.redo();
        assertEquals(4, persistentArray.getVersionCount());
        persistentArray.redo();
        assertEquals(4, persistentArray.getVersionCount());
        assertEquals("[A, B, C]", persistentArray.toString());
    }

    @Test
    void testPersistentArrayInsertedUndoRedo() {
        PersistentArray<PersistentArray<String>> persistentArrayrent = new PersistentArray<>();
        PersistentArray<String> child1 = new PersistentArray<>();
        PersistentArray<String> child2 = new PersistentArray<>();
        PersistentArray<String> child3 = new PersistentArray<>();
        persistentArrayrent.add(child1);
        persistentArrayrent.add(child2);
        persistentArrayrent.add(child3);

        persistentArrayrent.get(0).add("1");
        persistentArrayrent.get(0).add("2");
        persistentArrayrent.get(0).add("3");

        persistentArrayrent.get(1).add("11");
        persistentArrayrent.get(1).add("22");
        persistentArrayrent.get(1).add("33");

        persistentArrayrent.get(2).add("111");
        persistentArrayrent.get(2).add("222");
        persistentArrayrent.get(2).add("333");

        assertEquals("[[1, 2, 3], [11, 22, 33], [111, 222, 333]]", persistentArrayrent.toString());
        persistentArrayrent.undo();
        assertEquals("[[1, 2, 3], [11, 22, 33], [111, 222]]", persistentArrayrent.toString());

        PersistentArray<String> child4 = new PersistentArray<>();
        persistentArrayrent.add(1, child4);
        child4.add("Test_str_1");
        assertEquals("[[1, 2, 3], [Test_str_1], [11, 22, 33], [111, 222]]", persistentArrayrent.toString());
        persistentArrayrent.undo();
        assertEquals("[[1, 2, 3], [], [11, 22, 33], [111, 222]]", persistentArrayrent.toString());

        persistentArrayrent.get(0).set(0, "Test_str_2");
        persistentArrayrent.get(0).set(1, "Test_str_3");
        assertEquals("[[Test_str_2, Test_str_3, 3], [], [11, 22, 33], [111, 222]]", persistentArrayrent.toString());
        persistentArrayrent.undo();
        assertEquals("[[Test_str_2, 2, 3], [], [11, 22, 33], [111, 222]]", persistentArrayrent.toString());
    }

    @Test
    void testPersistentArrayIterator() {
        addABC();
        Iterator<String> i = persistentArray.iterator();
        assertEquals("A", i.next());
        assertEquals("B", i.next());
        assertEquals("C", i.next());
        assertFalse(i.hasNext());
    }

    @Test
    void testPersistentArrayForEach() {
        addABC();
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : persistentArray) {
            stringBuilder.append(s);
        }
        assertEquals("ABC", stringBuilder.toString());

        stringBuilder = new StringBuilder();
        persistentArray.forEach(stringBuilder::append);
        assertEquals("ABC", stringBuilder.toString());
    }

    @Test
    void testPersistentArrayPop() {
        addABC();
        assertEquals("C", persistentArray.pop());
        assertEquals("B", persistentArray.pop());
        persistentArray.undo();
        persistentArray.undo();
        assertEquals("C", persistentArray.pop());
    }

    @Test
    void testPersistentArraySet() {
        addABC();
        assertEquals("[A, B, C]", persistentArray.toString());
        assertEquals("A", persistentArray.set(0, "Q"));
        assertEquals("B", persistentArray.set(1, "W"));
        assertEquals("[Q, W, C]", persistentArray.toString());
        persistentArray.undo();
        persistentArray.undo();
        assertEquals("[A, B, C]", persistentArray.toString());
    }

    @Test
    void testPersistentArrayCascade() {
        PersistentArray<String> persistentArray_0 = new PersistentArray<>(32);
        persistentArray_0.add("A");

        PersistentArray<String> persistentArray_1 = persistentArray_0.conj("B");

        assertEquals("[A]", persistentArray_0.toString());
        assertEquals("[A, B]", persistentArray_1.toString());

        PersistentArray<String> persistentArray_2 = persistentArray_1.assoc(0, "C");

        assertEquals("[C, B]", persistentArray_2.toString());
    }

    @Test
    void testPersistentArrayStream() {
        PersistentArray<Integer> persistentArray = new PersistentArray<>();
        persistentArray.add(4);
        persistentArray.add(5);
        persistentArray.add(6);
        persistentArray.add(7);

        assertEquals("[12, 14]", Arrays.toString(
                persistentArray.stream().map(i -> i * 2).filter(x -> x > 10).toArray()));

        persistentArray.undo();

        assertEquals("[12]", Arrays.toString(
                persistentArray.stream().map(i -> i * 2).filter(x -> x > 10).toArray()));
    }

    @Test
    void testPersistentArrayConstructor() {
        PersistentArray<String> persistentArray_0 = new PersistentArray<>();
        assertEquals(1073741824, persistentArray_0.maxSize);
        assertEquals(6, persistentArray_0.depth);
        assertEquals(32, persistentArray_0.width);

        PersistentArray<String> persistentArray_1 = new PersistentArray<>(27);
        assertEquals(32, persistentArray_1.maxSize);
        assertEquals(1, persistentArray_1.depth);
        assertEquals(32, persistentArray_1.width);

        PersistentArray<String> persistentArray_2 = new PersistentArray<>(32);
        assertEquals(32, persistentArray_2.maxSize);
        assertEquals(1, persistentArray_2.depth);
        assertEquals(32, persistentArray_2.width);

        PersistentArray<String> persistentArray_3 = new PersistentArray<>(33);
        assertEquals(1024, persistentArray_3.maxSize);
        assertEquals(2, persistentArray_3.depth);
        assertEquals(32, persistentArray_3.width);

        PersistentArray<String> persistentArray_4 = new PersistentArray<>(3, 1);
        assertEquals(8, persistentArray_4.maxSize);
        assertEquals(3, persistentArray_4.depth);
        assertEquals(2, persistentArray_4.width);
    }

    @Test
    void testPersistentArrayAddInTheMiddle() {
        persistentArray = new PersistentArray<>(3, 1);
        persistentArray.add("3");
        persistentArray.add("7");
        persistentArray.add("6");
        persistentArray.add("9");
        persistentArray.add("1");
        assertEquals("[3, 7, 6, 9, 1]", persistentArray.toString());
        persistentArray.add(3, "8");
        assertEquals("[3, 7, 6, 8, 9, 1]", persistentArray.toString());
        assertThrows(IndexOutOfBoundsException.class, () -> persistentArray.add(-1, "8"));
        assertThrows(IndexOutOfBoundsException.class, () -> persistentArray.add(6, "8"));
        assertThrows(IndexOutOfBoundsException.class, () -> persistentArray.add(9999, "8"));
    }

    @Test
    void testPersistentArrayToString() {
        addABC();
        assertEquals("[A, B, C]", persistentArray.toString());
    }

    @Test
    void testPersistentArrayRemove() {
        addABC(3, 1);

        assertThrows(IndexOutOfBoundsException.class, () -> persistentArray.remove(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> persistentArray.remove(3));
        assertThrows(IndexOutOfBoundsException.class, () -> persistentArray.remove(999));

        assertEquals("B", persistentArray.remove(1));
        assertEquals("[A, C]", persistentArray.toString());

        assertEquals("C", persistentArray.remove(1));
        assertEquals("[A]", persistentArray.toString());

        assertEquals("A", persistentArray.remove(0));
        assertEquals("[]", persistentArray.toString());
        assertThrows(IndexOutOfBoundsException.class, () -> persistentArray.remove(0));
    }

    @Test
    void testPersistentArrayClear() {
        addABC();
        persistentArray.clear();
        assertEquals("[]", persistentArray.toString());
        persistentArray.undo();
        assertEquals("[A, B, C]", persistentArray.toString());
    }

    @Test
    void testPersistentArrayPop2() {
        addABC(3, 1);
        assertEquals("C", persistentArray.pop());
        assertEquals("B", persistentArray.pop());
        assertEquals("A", persistentArray.pop());
    }
}
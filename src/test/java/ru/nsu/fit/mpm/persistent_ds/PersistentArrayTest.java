package ru.nsu.fit.mpm.persistent_ds;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PersistentArrayTest {
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
    public void testPersistentArrayAddAndGet() {
        addABC();
        assertEquals("A", persistentArray.get(0));
        assertEquals("B", persistentArray.get(1));
        assertEquals("C", persistentArray.get(2));
    }

    @Test
    public void testPersistentArrayToArray() {
        addABC();
        String[] strings = new String[persistentArray.size()];
        persistentArray.toArray(strings);
        assertEquals("[A, B, C]", Arrays.toString(strings));
    }

    @Test
    public void testPersistentArraySize() {
        persistentArray = new PersistentArray<>(32);
        assertEquals(persistentArray.size(), 0);
        persistentArray.add("A");
        persistentArray.add("B");
        persistentArray.add("C");
        assertEquals(persistentArray.size(), 3);
    }

    @Test
    public void testPersistentAdd() {
        persistentArray = new PersistentArray<>(1, 1);
        assertEquals(2, persistentArray.maxSize);

        assertTrue(persistentArray.add("A"));
        assertTrue(persistentArray.add("B"));
        assertFalse(persistentArray.add("C"));
    }

    @Test
    public void testPersistentArrayIsEmpty() {
        persistentArray = new PersistentArray<>(32);
        assertTrue(persistentArray.isEmpty());
        persistentArray.add("A");
        assertFalse(persistentArray.isEmpty());
    }

    @Test
    public void testPersistentArrayUndoRedo() {
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
    public void testPersistentArrayInsertedUndoRedo() {
        PersistentArray<PersistentArray<String>> persistentArrays = new PersistentArray<>();
        PersistentArray<String> child1 = new PersistentArray<>();
        PersistentArray<String> child2 = new PersistentArray<>();
        PersistentArray<String> child3 = new PersistentArray<>();
        persistentArrays.add(child1);
        persistentArrays.add(child2);
        persistentArrays.add(child3);

        persistentArrays.get(0).add("1");
        persistentArrays.get(0).add("2");
        persistentArrays.get(0).add("3");

        persistentArrays.get(1).add("11");
        persistentArrays.get(1).add("22");
        persistentArrays.get(1).add("33");

        persistentArrays.get(2).add("111");
        persistentArrays.get(2).add("222");
        persistentArrays.get(2).add("333");

        assertEquals("[[1, 2, 3], [11, 22, 33], [111, 222, 333]]", persistentArrays.toString());
        persistentArrays.undo();
        assertEquals("[[1, 2, 3], [11, 22, 33], [111, 222]]", persistentArrays.toString());

        PersistentArray<String> child4 = new PersistentArray<>();
        persistentArrays.add(1, child4);
        child4.add("test_string_1");
        assertEquals("[[1, 2, 3], [test_string_1], [11, 22, 33], [111, 222]]", persistentArrays.toString());
        persistentArrays.undo();
        assertEquals("[[1, 2, 3], [], [11, 22, 33], [111, 222]]", persistentArrays.toString());

        persistentArrays.get(0).set(0, "test_string_2");
        persistentArrays.get(0).set(1, "test_string_3");
        assertEquals("[[test_string_2, test_string_3, 3], [], [11, 22, 33], [111, 222]]", persistentArrays.toString());
        persistentArrays.undo();
        assertEquals("[[test_string_2, 2, 3], [], [11, 22, 33], [111, 222]]", persistentArrays.toString());
    }

    @Test
    public void testPersistentArrayIterator() {
        addABC();
        Iterator<String> i = persistentArray.iterator();
        assertEquals("A", i.next());
        assertEquals("B", i.next());
        assertEquals("C", i.next());
        assertFalse(i.hasNext());
    }

    @Test
    public void testPersistentArrayForEach() {
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
    public void testPersistentArrayPop() {
        addABC();
        assertEquals("C", persistentArray.pop());
        assertEquals("B", persistentArray.pop());
        persistentArray.undo();
        persistentArray.undo();
        assertEquals("C", persistentArray.pop());
    }

    @Test
    public void testPersistentArraySet() {
        addABC();
        assertEquals("[A, B, C]", persistentArray.toString());
        persistentArray.set(0, "Q");
        persistentArray.set(1, "W");
        assertEquals("[Q, W, C]", persistentArray.toString());
        persistentArray.undo();
        persistentArray.undo();
        assertEquals("[A, B, C]", persistentArray.toString());
    }

    @Test
    public void testPersistentArrayCascade() {
        persistentArray = new PersistentArray<>(32);
        persistentArray.add("A");

        PersistentArray<String> v2 = persistentArray.conj("B");

        assertEquals("[A]", persistentArray.toString());
        assertEquals("[A, B]", v2.toString());

        PersistentArray<String> v3 = v2.assoc(0, "C");

        assertEquals("[C, B]", v3.toString());
    }

    @Test
    public void testPersistentArrayStream() {
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
    public void testPersistentArrayConstructor() {
        PersistentArray<String> persistentArray0 = new PersistentArray<>();
        assertEquals(1073741824, persistentArray0.maxSize);
        assertEquals(6, persistentArray0.depth);
        assertEquals(32, persistentArray0.width);

        PersistentArray<String> persistentArray1 = new PersistentArray<>(27);
        assertEquals(32, persistentArray1.maxSize);
        assertEquals(1, persistentArray1.depth);
        assertEquals(32, persistentArray1.width);

        PersistentArray<String> persistentArray2 = new PersistentArray<>(32);
        assertEquals(32, persistentArray2.maxSize);
        assertEquals(1, persistentArray2.depth);
        assertEquals(32, persistentArray2.width);

        PersistentArray<String> persistentArray3 = new PersistentArray<>(33);
        assertEquals(1024, persistentArray3.maxSize);
        assertEquals(2, persistentArray3.depth);
        assertEquals(32, persistentArray3.width);

        PersistentArray<String> persistentArray4 = new PersistentArray<>(3, 1);
        assertEquals(8, persistentArray4.maxSize);
        assertEquals(3, persistentArray4.depth);
        assertEquals(2, persistentArray4.width);
    }

    @Test
    public void testPersistentArrayAddInTheMiddle() {
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
    public void testPersistentArrayToString() {
        addABC();
        assertEquals("[A, B, C]", persistentArray.toString());
    }

    @Test
    public void testPersistentArrayRemove() {
        addABC(3, 1);

        assertEquals(3, persistentArray.calcUniqueLeafs());
        assertThrows(IndexOutOfBoundsException.class, () -> persistentArray.remove(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> persistentArray.remove(3));
        assertThrows(IndexOutOfBoundsException.class, () -> persistentArray.remove(999));

        assertEquals("B", persistentArray.remove(1));
        assertEquals("[A, C]", persistentArray.toString());
        assertEquals(4, persistentArray.calcUniqueLeafs());

        assertEquals("C", persistentArray.remove(1));
        assertEquals("[A]", persistentArray.toString());
        assertEquals(5, persistentArray.calcUniqueLeafs());

        assertEquals("A", persistentArray.remove(0));
        assertEquals("[]", persistentArray.toString());
        assertEquals(5, persistentArray.calcUniqueLeafs());
        assertThrows(IndexOutOfBoundsException.class, () -> persistentArray.remove(0));
    }

    @Test
    public void testPersistentArrayClear() {
        addABC();
        persistentArray.clear();
        assertEquals("[]", persistentArray.toString());
        persistentArray.undo();
        assertEquals("[A, B, C]", persistentArray.toString());
    }

    @Test
    public void testPersistentArrayUniqueLeafs() {
        persistentArray = new PersistentArray<>(3, 1);
        assertEquals(0, persistentArray.calcUniqueLeafs());
        persistentArray.add("A");
        assertEquals(1, persistentArray.calcUniqueLeafs());
        persistentArray.add("B");
        assertEquals(2, persistentArray.calcUniqueLeafs());
    }
}
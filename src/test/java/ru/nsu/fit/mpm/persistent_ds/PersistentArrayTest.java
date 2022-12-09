package ru.nsu.fit.mpm.persistent_ds;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

public class PersistentArrayTest {

    PersistentArray<String> persistentArray = new PersistentArray<>(32);

    private void addABC() {
        persistentArray.add("A");
        persistentArray.add("B");
        persistentArray.add("C");
    }

    private <E> String valuesToString(PersistentArray<E> array) {
        StringBuilder stringBuilder = new StringBuilder();
        for (E e : array) {
            stringBuilder.append(e.toString());
        }
        return stringBuilder.toString();
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
        assertEquals(persistentArray.size(), 0);
        addABC();
        assertEquals(persistentArray.size(), 3);
    }

    @Test
    public void testPersistentArrayIsEmpty() {
        assertTrue(persistentArray.isEmpty());
        persistentArray.add("A");
        assertFalse(persistentArray.isEmpty());
    }

    @Test
    public void testPersistentArrayUndoRedo() {
        addABC();
        persistentArray.undo();
        persistentArray.undo();
        assertEquals("A", valuesToString(persistentArray));

        persistentArray.redo();
        assertEquals("AB", valuesToString(persistentArray));

        persistentArray.undo();
        persistentArray.undo();
        assertEquals("", valuesToString(persistentArray));

        persistentArray.redo();
        persistentArray.redo();
        persistentArray.redo();
        assertEquals("ABC", valuesToString(persistentArray));
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
        assertEquals("ABC", valuesToString(persistentArray));
        persistentArray.set(0, "Q");
        persistentArray.set(1, "W");
        persistentArray.set(2, "E");
        assertEquals("QWE", valuesToString(persistentArray));
        persistentArray.undo();
        persistentArray.undo();
        persistentArray.undo();
        assertEquals("ABC", valuesToString(persistentArray));
    }

    @Test
    public void testPersistentArrayCascade() {
        persistentArray.add("A");

        PersistentArray<String> v2 = persistentArray.conj("B");

        assertEquals("A", valuesToString(persistentArray));
        assertEquals("AB", valuesToString(v2));

        PersistentArray<String> v3 = v2.assoc(0, "C");

        assertEquals("CB", valuesToString(v3));
    }

    @Test
    public void testPersistentArrayStream() {
        PersistentArray<Integer> persistentArray = new PersistentArray<>();
        persistentArray.add(4);
        persistentArray.add(5);
        persistentArray.add(6);
        persistentArray.add(7);

        assertEquals("[12, 14]", Arrays.toString(persistentArray.stream().map(i -> i * 2).filter(x -> x > 10).toArray()));

        persistentArray.undo();

        assertEquals("[12]", Arrays.toString(persistentArray.stream().map(i -> i * 2).filter(x -> x > 10).toArray()));
    }
}

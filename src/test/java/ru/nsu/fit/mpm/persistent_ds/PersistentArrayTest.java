package ru.nsu.fit.mpm.persistent_ds;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

public class PersistentArrayTest {

    PersistentArray<String> stringPersistentArray = new PersistentArray<>(32);

    @AfterEach
    public void clear() {
        stringPersistentArray.clear();
    }

    private void addABC() {
        stringPersistentArray.add("A");
        stringPersistentArray.add("B");
        stringPersistentArray.add("C");
    }

    private String valuesToString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String string : stringPersistentArray) {
            stringBuilder.append(string);
        }
        return stringBuilder.toString();
    }

    @Test
    public void testPersistentArrayAddAndGet() {
        System.out.println("Add");
        addABC();
        assertEquals("A", stringPersistentArray.get(0));
        assertEquals("B", stringPersistentArray.get(1));
        assertEquals("C", stringPersistentArray.get(2));
    }

    @Test
    public void testPersistentArrayToArray() {
        System.out.println("To array" + stringPersistentArray.size());
        addABC();
        String[] strings = new String[stringPersistentArray.size()];
        stringPersistentArray.toArray(strings);
        assertEquals("[A, B, C]", Arrays.toString(strings));
    }

    @Test
    public void testPersistentArraySize() {
        assertEquals(stringPersistentArray.size(), 0);
        addABC();
        assertEquals(stringPersistentArray.size(), 3);
    }

    @Test
    public void testPersistentArrayIsEmpty() {
        assertTrue(stringPersistentArray.isEmpty());
        stringPersistentArray.add("A");
        assertFalse(stringPersistentArray.isEmpty());
    }

    @Test
    public void testPersistentArrayUndoRedo() {
        addABC();
        stringPersistentArray.undo();
        stringPersistentArray.undo();
        assertEquals(valuesToString(), "A");

        stringPersistentArray.redo();
        assertEquals(valuesToString(), "AB");

        stringPersistentArray.undo();
        stringPersistentArray.undo();
        assertEquals(valuesToString(), "");

        stringPersistentArray.redo();
        stringPersistentArray.redo();
        stringPersistentArray.redo();
        assertEquals(valuesToString(), "ABC");
    }

    @Test
    public void testPersistentArrayIterator() {
        addABC();
        Iterator<String> i = stringPersistentArray.iterator();
        System.out.println(i.next());
        System.out.println(i.next());
        System.out.println(i.next());
    }

    @Test
    public void testPersistentArrayForEach() {
        addABC();
        assertEquals("ABC", valuesToString());
    }

    @Test
    public void testPersistentArrayPop() {
        addABC();
        System.out.println(stringPersistentArray.pop());
    }


}

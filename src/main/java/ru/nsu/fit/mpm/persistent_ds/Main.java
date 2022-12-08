package ru.nsu.fit.mpm.persistent_ds;

import java.util.Arrays;
import java.util.Iterator;

public class Main {
    public static void main(String[] args) {
        PersistentArray<Integer> persistentArray = new PersistentArray<>(100);
        System.out.println("Max count: " + persistentArray.maxSize);

        testUndoRedo(persistentArray);
        testIterator(persistentArray);
        testPop(persistentArray);
        testAPI(persistentArray);
    }

    private static void testAPI(PersistentArray<Integer> persistentArray) {
        System.out.println("testAPI");
        clearAndFill(persistentArray, 5);
        persistentArray.add(8);
        printArray(persistentArray);
        System.out.println(Arrays.toString(
                persistentArray.stream().map(i -> i * 2).filter(x -> x > 10).toArray()));
        persistentArray.undo();

        System.out.println(Arrays.toString(
                persistentArray.stream().map(i -> i * 2).filter(x -> x > 10).toArray()));

        for (Integer integer : persistentArray) {
            System.out.print(integer + " ");
        }
    }

    private static void testPop(PersistentArray<Integer> persistentArray) {
        System.out.println("testPop");
        clearAndFill(persistentArray, 5);
        System.out.println("pop=" + persistentArray.pop());
        printArray(persistentArray);
        System.out.println("pop=" + persistentArray.pop());
        printArray(persistentArray);
        persistentArray.undo();
        persistentArray.undo();
        printArray(persistentArray);
        persistentArray.redo();
        persistentArray.redo();
        printArray(persistentArray);
    }

    private static void testUndoRedo(PersistentArray<Integer> persistentArray) {
        System.out.println("testUndoRedo");
        clearAndFill(persistentArray, 5);
        printArray(persistentArray);
        persistentArray.undo();
        persistentArray.undo();
        printArray(persistentArray);
        persistentArray.add(999);
        printArray(persistentArray);
        persistentArray.redo();
        printArray(persistentArray);
        System.out.println("undo() undo() redo() redo()");
        persistentArray.undo();
        persistentArray.undo();
        printArray(persistentArray);
        persistentArray.redo();
        persistentArray.redo();
        printArray(persistentArray);
    }

    private static void testIterator(PersistentArray<Integer> persistentArray) {
        System.out.println("testIterator");
        clearAndFill(persistentArray, 5);
        printArray(persistentArray);
        Iterator<Integer> i = persistentArray.iterator();
        System.out.println(i.next());
        System.out.println(i.next());
        System.out.println(i.hasNext());
    }

    private static void clearAndFill(PersistentArray<Integer> persistentArray, int count) {
        persistentArray.clear();
        for (int i = 0; i < count; i++) {
            persistentArray.add((count - i) + 2);
        }
        printArray(persistentArray);
    }

    private static void printArray(PersistentArray<Integer> array) {
        System.out.print("size: " + array.size() + "   ");
        for (Integer integer : array) {
            System.out.print(integer + " ");
        }
        System.out.println();
    }
}
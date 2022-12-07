package ru.nsu.fit.mpm.persistent_ds;

import java.util.Iterator;

public class Main {
    public static void main(String[] args) {
        PersistentArray<Integer> persistentArray = new PersistentArray<>(5);
        System.out.println("Max count: " + persistentArray.maxSize);

        int count = 7;
        for (int i = 0; i < count; i++) {
            persistentArray.add(i);
        }

        testUndoRedo(persistentArray);
        testClear(persistentArray);
        testIterator(persistentArray);
        testPop(persistentArray);
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

    private static void testClear(PersistentArray<Integer> persistentArray) {
        System.out.println("testClear");
        persistentArray.clear();
        printArray(persistentArray);
    }

    private static void testIterator(PersistentArray<Integer> persistentArray) {
        System.out.println("testIterator");
        clearAndFill(persistentArray, 5);
        persistentArray.add(7);
        persistentArray.add(3);
        persistentArray.add(9);
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

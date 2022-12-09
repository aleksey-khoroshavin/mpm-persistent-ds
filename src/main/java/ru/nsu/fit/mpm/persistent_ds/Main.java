package ru.nsu.fit.mpm.persistent_ds;

import java.util.Arrays;
import java.util.Iterator;

public class Main {

    public static void main(String[] args) {

        testUndoRedo();
        testIterator();
        testPop();
        testAPI();
        testAdd();
        testUniqueLeafs();
        testString();
        testIntAsString();
        testAllInOne();
    }

    private static void testAllInOne() {
        System.out.println("\n" + "testAllInOne");
        PersistentArray<Integer> persistentArray = new PersistentArray<>(100);
        System.out.println("Max count: " + persistentArray.maxSize);
        persistentArray.add(1);
        persistentArray.add(2);
        persistentArray.add(3);
        persistentArray.add(4);
        printArray(persistentArray);
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

    private static void testIntAsString() {
        System.out.println("\n" + "testIntAsString");
        PersistentArray<Integer> persistentArray = new PersistentArray<>(20);
        persistentArray.add(1);
        persistentArray.add(2);
        persistentArray.add(3);
        Iterator<Integer> i = persistentArray.iterator();
        System.out.println(i.next());
        System.out.println(i.next());
        System.out.println(i.next());
    }

    private static void testString() {
        System.out.println("\n" + "testString");
        PersistentArray<String> persistentArray = new PersistentArray<>(20);
        persistentArray.add("A");
        persistentArray.add("B");
        persistentArray.add("C");
        Iterator<String> i = persistentArray.iterator();
        System.out.println(i.next());
        System.out.println(i.next());
        System.out.println(i.next());
    }

    private static void testUniqueLeafs() {
        PersistentArray<Integer> persistentArray = testBegin("testUniqueLists", 4, 3);
        persistentArray.add(4);
        printArray(persistentArray);
        persistentArray.add(5);
        printArray(persistentArray);
    }

    private static PersistentArray<Integer> testBegin(String section, int fillSize, int depth) {
        System.out.println("\n" + section);
        PersistentArray<Integer> persistentArray = new PersistentArray<>(depth, false);
        System.out.println("Max count: " + persistentArray.maxSize);
        fill(persistentArray, fillSize);
        printArray(persistentArray);
        return persistentArray;
    }

    private static PersistentArray<Integer> testBegin(String section, int fillSize) {
        System.out.println("\n" + section);
        PersistentArray<Integer> persistentArray = new PersistentArray<>(100);
        System.out.println("Max count: " + persistentArray.maxSize);
        fill(persistentArray, fillSize);
        printArray(persistentArray);
        return persistentArray;
    }

    private static PersistentArray<Integer> testBegin(String section) {
        System.out.println("\n" + section);
        PersistentArray<Integer> persistentArray = new PersistentArray<>(100);
        System.out.println("Max count: " + persistentArray.maxSize);
        return persistentArray;
    }

    private static void testAdd() {
        PersistentArray<Integer> persistentArray = testBegin("testAdd", 4);
        persistentArray.add(3, 999);
        printArray(persistentArray);
        persistentArray.undo();
        printArray(persistentArray);
    }

    private static void testAPI() {
        PersistentArray<Integer> persistentArray = testBegin("testAPI");
        persistentArray.add(7);
        persistentArray.add(6);
        persistentArray.add(5);
        persistentArray.add(4);
        persistentArray.add(3);
        persistentArray.add(8);
        printArray(persistentArray);
        System.out.println(Arrays.toString(persistentArray.stream().map(i -> i * 2).filter(x -> x > 10).toArray()));
        persistentArray.undo();
        System.out.println(Arrays.toString(persistentArray.stream().map(i -> i * 2).filter(x -> x > 10).toArray()));
        for (Integer integer : persistentArray) {
            System.out.print(integer + " ");
        }
        System.out.println();
    }

    private static void testPop() {
        PersistentArray<Integer> persistentArray = testBegin("testPop", 5);
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

    private static void testUndoRedo() {
        PersistentArray<Integer> persistentArray = testBegin("testUndoRedo", 5);
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

    private static void testIterator() {
        PersistentArray<Integer> persistentArray = testBegin("testIterator", 5);
        Iterator<Integer> i = persistentArray.iterator();
        System.out.println(i.next());
        System.out.println(i.next());
        System.out.println(i.hasNext());
    }

    private static void fill(PersistentArray<Integer> persistentArray, int count) {
        for (int i = 0; i < count; i++) {
            persistentArray.add(i);
        }
    }

    private static void printArray(PersistentArray<Integer> array) {
        System.out.println(array);
    }
}
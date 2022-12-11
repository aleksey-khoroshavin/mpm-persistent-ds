package ru.nsu.fit.mpm.persistent_ds;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        hashMapPresentation();
    }

    private static void hashMapPresentation() {
        PersistentHashMap<String, Integer> persistentHashMap = new PersistentHashMap<>();
        persistentHashMap.put("key_1", 10);
        persistentHashMap.put("key_2", 11);
        System.out.println("2 elem\t\t\t" + persistentHashMap.toString());
        persistentHashMap.undo();
        System.out.println("undo\t\t\t" + persistentHashMap.toString());
        persistentHashMap.redo();
        System.out.println("redo\t\t\t" + persistentHashMap.toString());

        System.out.println();
        persistentHashMap.put("key_3", 12);
        System.out.println("add key_3\t\t" + persistentHashMap.toString());
        persistentHashMap.put("key_3", 1000);
        System.out.println("modify key_3\t" + persistentHashMap.toString());
        persistentHashMap.undo();
        System.out.println("undo\t\t\t" + persistentHashMap.toString());

        System.out.println();
        persistentHashMap.put("key_4", -99);
        System.out.println("add key_4\t\t" + persistentHashMap.toString());
        persistentHashMap.remove("key_4");
        System.out.println("remove key_4\t\t" + persistentHashMap.toString());
        persistentHashMap.undo();
        System.out.println("undo\t\t\t" + persistentHashMap.toString());
    }

    private static void listPresentation() {
        System.out.println("\n\nlist");
        PersistentLinkedList<Integer> persistentLinkedList = new PersistentLinkedList<>(3, 1);

        persistentLinkedList.add(3);
        persistentLinkedList.add(4);
        persistentLinkedList.add(6);
        persistentLinkedList.add(0);
        persistentLinkedList.add(7);
        System.out.println(Arrays.toString(persistentLinkedList.toArray()) + " fill");
        System.out.println(persistentLinkedList.drawGraph());

        persistentLinkedList.add(3, 9);
        System.out.println(Arrays.toString(persistentLinkedList.toArray()) + " add(3,9)");
        System.out.println(persistentLinkedList.drawGraph());

        persistentLinkedList.add(0, 1);
        System.out.println(Arrays.toString(persistentLinkedList.toArray()) + " add(0,1)");
        System.out.println(persistentLinkedList.drawGraph());

        persistentLinkedList.undo();
        System.out.println(Arrays.toString(persistentLinkedList.toArray()) + " undo");
        System.out.println(persistentLinkedList.drawGraph());
    }

    private static void arrayPresentation() {
        simple();
        arrayStrings();
        nesting();
    }

    private static void nesting() {
        System.out.println("\n" + "nesting");
        PersistentArray<PersistentArray<Integer>> persistentArray = new PersistentArray<>(3, 1);

        PersistentArray<Integer> persistentArray1 = new PersistentArray<>(3, 1);
        persistentArray1.add(1);
        persistentArray1.add(2);

        PersistentArray<Integer> persistentArray2 = new PersistentArray<>(3, 1);
        persistentArray2.add(3);
        persistentArray2.add(4);

        persistentArray.add(persistentArray1);
        persistentArray.add(persistentArray2);

        System.out.println(persistentArray.getCurrentHead().root.drawGraph());
        persistentArray1.set(1, 9);
        System.out.println(persistentArray.getCurrentHead().root.drawGraph());
        persistentArray.undo();
        System.out.println(persistentArray.getCurrentHead().root.drawGraph());
    }

    private static void arrayStrings() {
        System.out.println("\n" + "strings");
        PersistentArray<String> version1 = new PersistentArray<>(3, 1);
        System.out.println("maxSize = " + version1.maxSize);
        version1.add("Str1");
        PersistentArray<String> version2 = version1.conj("Str2");

        System.out.println(version1);
        System.out.println(version2);

        PersistentArray<String> version3 = version2.assoc(0, "Str3");

        System.out.println();
        System.out.println(version1);
        System.out.println(version2);
        System.out.println(version3);

        version3.add("3");
        version3.add("4");
        System.out.println(version3.drawGraph());
        version3.remove(2);
        System.out.println(version3.drawGraph());
    }

    private static void simple() {
        PersistentArray<String> persistentArray = new PersistentArray<>(28);
        System.out.println("maxSize = " + persistentArray.maxSize);

        persistentArray.add("1");
        persistentArray.add("2");
        System.out.println(persistentArray);
        System.out.println("pop=" + persistentArray.pop());
        System.out.println(persistentArray);
        persistentArray.undo();
        System.out.println(persistentArray);
    }
}
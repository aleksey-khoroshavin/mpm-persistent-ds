package ru.nsu.fit.mpm.persistent_ds;

public class Main {
    public static void main(String[] args) {
        listPresentation();
    }

    private static void listPresentation() {
        System.out.println("\n" + "list");
        PersistentLinkedList<Integer> persistentLinkedList = new PersistentLinkedList<>(3, 1);

        persistentLinkedList.add(3);
        System.out.println(persistentLinkedList.drawGraph());

        persistentLinkedList.add(4);
        System.out.println(persistentLinkedList.drawGraph());

        persistentLinkedList.add(6);
        System.out.println(persistentLinkedList.drawGraph());

        persistentLinkedList.add(9);
        System.out.println(persistentLinkedList.drawGraph());

        persistentLinkedList.undo();
        System.out.println(persistentLinkedList.drawGraph());
    }

    private static void arrayPresentation() {
        System.out.println("\n" + "array");
        PersistentArray<String> persistentArray = new PersistentArray<>(28);
        System.out.println("maxSize = " + persistentArray.maxSize);

        persistentArray.add("1");
        persistentArray.add("2");
        System.out.println(persistentArray);
        System.out.println("pop=" + persistentArray.pop());
        System.out.println(persistentArray);
        persistentArray.undo();
        System.out.println(persistentArray);

        PersistentArray<String> version1 = new PersistentArray<>(3, 1);
        System.out.println("maxSize = " + version1.maxSize);
        version1.add("Str1");
        PersistentArray<String> version2 = version1.conj("Str2");

        System.out.println(version1);
        System.out.println(version2);

        PersistentArray<String> version3 = version2.assoc(0, "Str3");

        System.out.println(version1);
        System.out.println(version2);
        System.out.println(version3);

        version3.add("3");
        version3.add("4");
        System.out.println(version3.drawGraph());
        version3.remove(2);
        System.out.println(version3.drawGraph());
    }
}
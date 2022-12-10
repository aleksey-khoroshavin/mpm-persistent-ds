package ru.nsu.fit.mpm.persistent_ds;

public class Main {
    public static void main(String[] args) {
        arrayPresentation();
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

        PersistentArray<String> v1 = new PersistentArray<>(3, 1);
        System.out.println("maxSize = " + v1.maxSize);
        v1.add("Str1");
        PersistentArray<String> v2 = v1.conj("Str2");

        System.out.println(v1);
        System.out.println(v2);

        PersistentArray<String> v3 = v2.assoc(0, "Str3");

        System.out.println(v1);
        System.out.println(v2);
        System.out.println(v3);

        v3.add("3");
        v3.add("4");
        System.out.println(v3.drawGraph());
        v3.remove(2);
        System.out.println(v3.drawGraph());
    }
}
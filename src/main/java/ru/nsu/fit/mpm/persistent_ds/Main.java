package ru.nsu.fit.mpm.persistent_ds;

public class Main {
    public static void main(String[] args) {
        arrayPresentation();
    }

    private static void arrayPresentation() {
        System.out.println("\n" + "array");
        PersistentArray<String> version1 = new PersistentArray<>(30);

        version1.add("1");
        version1.add("2");
        System.out.println(version1);
        System.out.println("pop=" + version1.pop());
        System.out.println(version1);
        version1.undo();

        version1.clear();
        version1.add("Str1");
        PersistentArray<String> version2 = version1.conj("Str2");

        System.out.println(version1);
        System.out.println(version2);

        PersistentArray<String> version3 = version2.assoc(0, "Str3");

        System.out.println(version1);
        System.out.println(version2);
        System.out.println(version3);

    }
}
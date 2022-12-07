package ru.nsu.fit.mpm.persistent_ds;

public class Main {
    public static void main(String[] args) {
        PersistentArray<Integer> pa = new PersistentArray<>();

        int count = 2;
        for (int i = 0; i < count; i++) {
            pa.add(i);
        }

        printArray(pa);
    }

    private static void printArray(PersistentArray<Integer> array) {
        System.out.print("size: " + array.size() + "   ");
        for (int i = 0; i < array.size(); i++) {
            System.out.print(array.get(i) + " ");
        }
        System.out.println();
    }
}

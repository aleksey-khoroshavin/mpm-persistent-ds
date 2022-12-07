package ru.nsu.fit.mpm.persistent_ds;

public class Main {
    public static void main(String[] args) {
        PersistentArray<Integer> pa = new PersistentArray<>();
        System.out.println("Max count: " + pa.maxSize());

        int count = 7;
        for (int i = 0; i < count; i++) {
            pa.add(i);
        }
        printArray(pa);
        pa.undo();
        pa.undo();
        printArray(pa);
        pa.add(999);
        printArray(pa);
        pa.redo();
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

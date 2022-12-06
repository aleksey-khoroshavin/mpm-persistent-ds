package ru.nsu.fit.mpm.persistent_ds;

public class Main {
    public static void main(String[] args) {

        PersistentArray<Integer> persistentArray = new PersistentArray<>();

        for (int i = 0; i < 22; i++) {
            persistentArray.add2(i);
        }

        printArray(persistentArray);
    }

    private static void printArray(PersistentArray<Integer> array) {
        System.out.print("size: " + array.size() + "   ");
        for (int i = 0; i < array.size(); i++) {
            System.out.print(array.get(i) + " ");
        }
    }
}

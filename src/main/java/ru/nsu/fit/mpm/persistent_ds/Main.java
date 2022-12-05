package ru.nsu.fit.mpm.persistent_ds;

public class Main {
    public static void main(String[] args) {

        PersistentList<Integer> persistentList = new PersistentList<>();

        for (int i = 0; i < 15; i++) {
            persistentList.add(i);
        }

        for (int i = 0; i < persistentList.size(); i++) {
            System.out.println(persistentList.get(i));
        }
    }
}

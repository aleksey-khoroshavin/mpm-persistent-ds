package ru.nsu.fit.mpm.persistent_ds;

public class Main {
    public static void main(String[] args) {
        System.out.println("PersistentArray:");
        PersistentArray<Integer> persistentArray = new PersistentArray<>();

        System.out.println(persistentArray.root.getChildren().get(0).getChildren().get(0).data);
        for (int i = 0; i < 18; i++) {
            persistentArray.add(i);
        }
        System.out.println(persistentArray.root.getChildren().get(0).getChildren().get(0).data);
        System.out.println(persistentArray.root.getChildren().get(0).getChildren().get(1).data);
        System.out.println(persistentArray.root.getChildren().get(1).getChildren().get(0).data);

        System.out.println(persistentArray.get(0));
        System.out.println(persistentArray.get(1));
        System.out.println(persistentArray.get(2));

        System.out.println("\nPersistentList:");

        PersistentList<Integer> persistentList = new PersistentList<>();

        for (int i = 0; i < 15; i++) {
            persistentList.add(i);
        }

        for (int i = 0; i < persistentList.size(); i++) {
            System.out.println(persistentList.get(i));
        }
    }
}

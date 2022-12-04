package ru.nsu.fit.mpm.persistent_ds;

public class Main {
    public static void main(String[] args) {
        PersistentArray<Integer> pa = new PersistentArray<>();
        System.out.println(pa.root.getChildren().get(0).getChildren().get(0).data);
        pa.add(1);
        pa.add(2);
        System.out.println(pa.root.getChildren().get(0).getChildren().get(0).data);
    }
}

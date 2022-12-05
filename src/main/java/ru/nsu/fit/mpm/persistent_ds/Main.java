package ru.nsu.fit.mpm.persistent_ds;

public class Main {
    public static void main(String[] args) {
        PersistentArray<Integer> pa = new PersistentArray<>();

        System.out.println(pa.root.getChildren().get(0).getChildren().get(0).data);
        for (int i = 0; i < 18; i++) {
            pa.add(i);
        }
        System.out.println(pa.root.getChildren().get(0).getChildren().get(0).data);
        System.out.println(pa.root.getChildren().get(0).getChildren().get(1).data);
        System.out.println(pa.root.getChildren().get(0).getChildren().get(2).data);
        System.out.println(pa.root.getChildren().get(0).getChildren().get(3).data);
        System.out.println(pa.root.getChildren().get(1).getChildren().get(0).data);

        System.out.println(pa.get(0));
        System.out.println(pa.get(1));
    }
}

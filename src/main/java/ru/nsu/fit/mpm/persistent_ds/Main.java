package ru.nsu.fit.mpm.persistent_ds;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        PersistentArray<Integer> pa = new PersistentArray<>();
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        pa.add(list);
        PersistentArray<Integer> pa2 = pa.append(4);
    }
}

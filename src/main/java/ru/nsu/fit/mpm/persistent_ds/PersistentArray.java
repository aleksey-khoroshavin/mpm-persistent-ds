package ru.nsu.fit.mpm.persistent_ds;
import java.util.List;

public class PersistentArray<E> {
    Head<E> head;

    public PersistentArray() {
        head = new Head<>();
    }

    public void add(List<E> list) {

    }

    public PersistentArray<E> append(E element) {
        return new PersistentArray<E>();
    }
}

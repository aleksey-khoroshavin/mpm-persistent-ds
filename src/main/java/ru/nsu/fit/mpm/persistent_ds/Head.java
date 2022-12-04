package ru.nsu.fit.mpm.persistent_ds;

public class Head<E> {
    private int count = 0;
    private Tree<E> tree;

    public Head() {
        tree = new Tree<>();
    }
}

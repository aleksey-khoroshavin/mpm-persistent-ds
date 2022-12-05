package ru.nsu.fit.mpm.persistent_ds;

public class Head<E> {
    public Node<E> root;
    public int count = 0;

    public Head() {
        this.root = new Node<>();
        root.parent = null;
        this.count = 0;
    }

    public Head(Head<E> prevHead) {
        this.root = prevHead.root;
        this.count = prevHead.count;
    }
}

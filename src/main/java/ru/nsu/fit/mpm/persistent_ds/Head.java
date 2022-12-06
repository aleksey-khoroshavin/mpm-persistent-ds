package ru.nsu.fit.mpm.persistent_ds;

public class Head<E> {
    public Node<E> root;
    public int size = 0;

    public Head() {
        this.root = new Node<>();
        this.size = 0;
    }

    public Head(Head<E> prevHead, Integer sizeDelta) {
        this.root = new Node<>(prevHead.root);
        this.size = prevHead.size + sizeDelta;
    }
}

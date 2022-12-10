package ru.nsu.fit.mpm.persistent_ds;

public class Head<E> {
    public Node<E> root;
    public int size;

    public Head() {
        this.root = new Node<>();
        this.size = 0;
    }

    public Head(Head<E> prevHead, Integer sizeDelta) {
        this.root = new Node<>(prevHead.root);
        this.size = prevHead.size + sizeDelta;
    }

    public Head(Head<E> other, Integer newSize, Integer maxIndex) {
        this.root = new Node<>(other.root, maxIndex);
        this.size = newSize;
    }
}

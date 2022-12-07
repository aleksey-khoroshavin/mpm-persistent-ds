package ru.nsu.fit.mpm.persistent_ds;

import ru.nsu.fit.mpm.persistent_ds.nodes.Node;

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
}

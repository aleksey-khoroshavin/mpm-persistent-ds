package ru.nsu.fit.mpm.persistent_ds;

import ru.nsu.fit.mpm.persistent_ds.nodes.AbstractNode;

public class Head<E> {
    public AbstractNode<E> root;
    public int size;

    public Head() {
        this.root = new AbstractNode<>();
        this.size = 0;
    }

    public Head(Head<E> prevHead, Integer sizeDelta) {
        this.root = new AbstractNode<>(prevHead.root);
        this.size = prevHead.size + sizeDelta;
    }
}

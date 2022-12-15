package ru.nsu.fit.mpm.persistent_ds.util.head;

import ru.nsu.fit.mpm.persistent_ds.util.node.Node;

public class HeadArray<E> {
    private final Node<E> root;
    private int size = 0;

    public HeadArray() {
        this.root = new Node<>();
    }

    public HeadArray(HeadArray<E> other) {
        this.root = new Node<>(other.root);
        this.size = other.size;
    }

    public HeadArray(HeadArray<E> other, Integer sizeDelta) {
        this.root = new Node<>(other.root);
        this.size = other.size + sizeDelta;
    }

    public HeadArray(HeadArray<E> other, Integer newSize, Integer maxIndex) {
        this.root = new Node<>(other.root, maxIndex);
        this.size = newSize;
    }

    public Node<E> getRoot() {
        return root;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return String.format("%09x %d", root.hashCode(), size);
    }
}
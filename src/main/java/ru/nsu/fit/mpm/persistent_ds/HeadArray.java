package ru.nsu.fit.mpm.persistent_ds;

public class HeadArray<E> {
    public Node<E> root;
    public int size = 0;

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

    @Override
    public String toString() {
        return String.format("%09x %d", root.hashCode(), size);
    }

    public void copyTree(HeadList<E> other) {
    }
}
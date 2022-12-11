package ru.nsu.fit.mpm.persistent_ds;

public class CopyResult<E, H> {
    public Node<E> leaf;
    public int leafInnerIndex;
    public H head;

    public CopyResult(Node<E> leaf, int leafInnerIndex, H head) {
        this.leaf = leaf;
        this.leafInnerIndex = leafInnerIndex;
        this.head = head;
    }

    @Override
    public String toString() {
        return leaf.toString() + "; " + leafInnerIndex + "; " + head.toString();
    }
}

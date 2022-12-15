package ru.nsu.fit.mpm.persistent_ds.util.copy_path;

import ru.nsu.fit.mpm.persistent_ds.util.node.Node;

public class CopyResult<E, H> {
    private final Node<E> leaf;
    private final int leafInnerIndex;
    private final H head;

    public CopyResult(Node<E> leaf, int leafInnerIndex, H head) {
        this.leaf = leaf;
        this.leafInnerIndex = leafInnerIndex;
        this.head = head;
    }

    public Node<E> getLeaf() {
        return leaf;
    }

    public int getLeafInnerIndex() {
        return leafInnerIndex;
    }

    public H getHead() {
        return head;
    }

    @Override
    public String toString() {
        return leaf.toString() + "; " + leafInnerIndex + "; " + head.toString();
    }
}

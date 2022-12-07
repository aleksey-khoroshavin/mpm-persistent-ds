package ru.nsu.fit.mpm.persistent_ds;

import ru.nsu.fit.mpm.persistent_ds.nodes.AbstractNode;

import java.util.List;

public abstract class AbstractPersistentCollection<E> implements UndoRedo, List<E> {
    public int depth = 3;
    public int bitPerLevel = AbstractNode.bitPerNode * depth;
    public int mask = (int) Math.pow(2, AbstractNode.bitPerNode) - 1;

    public int maxSize() {
        return (int) Math.pow(2, bitPerLevel);
    }
}

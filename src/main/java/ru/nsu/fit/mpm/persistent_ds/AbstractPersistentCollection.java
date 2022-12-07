package ru.nsu.fit.mpm.persistent_ds;

import java.util.List;

public abstract class AbstractPersistentCollection<E> implements UndoRedo, List<E> {
    public final int depth;
    public final int bitPerLevel;
    public final int mask;
    public final int maxSize;

    public AbstractPersistentCollection(int depth) {
        this.depth = depth;
        bitPerLevel = Node.bitPerNode * depth;
        mask = (int) Math.pow(2, Node.bitPerNode) - 1;
        maxSize = (int) Math.pow(2, bitPerLevel);
    }
}
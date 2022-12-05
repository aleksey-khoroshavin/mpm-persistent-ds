package ru.nsu.fit.mpm.persistent_ds;

import java.util.List;

public abstract class AbstractPersistentCollection<E> implements UndoRedo, List<E> {
    public int depth = 3;
    public int bitPerLevel = Node.bitPerNode * depth;
    public int mask = (int) Math.pow(2, Node.bitPerNode) - 1;

    private int maxCount() {
        return (int) Math.pow(2, bitPerLevel);
    }
}

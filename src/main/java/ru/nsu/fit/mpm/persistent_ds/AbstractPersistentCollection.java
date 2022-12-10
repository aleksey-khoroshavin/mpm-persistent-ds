package ru.nsu.fit.mpm.persistent_ds;

import java.util.List;

public abstract class AbstractPersistentCollection<E> implements UndoRedo, List<E> {
    public final int depth;
    public final int bitPerLevel;
    public final int mask;
    public final int maxSize;
    public final int bitPerNode;
    public final int width;

    public AbstractPersistentCollection(int depth, int bitPerNode) {
        this.depth = depth;
        this.bitPerNode = bitPerNode;
        bitPerLevel = bitPerNode * depth;
        mask = (int) Math.pow(2, bitPerNode) - 1;
        maxSize = (int) Math.pow(2, bitPerLevel);
        width = (int) Math.pow(2, bitPerNode);
    }

    public static double log(int N, int newBase) {
        return (Math.log(N) / Math.log(newBase));
    }
}
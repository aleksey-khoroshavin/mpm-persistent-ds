package ru.nsu.fit.mpm.persistent_ds;

public abstract class AbstractPersistentCollection<E> implements UndoRedo {
    public final int depth;
    public final int bitPerLevel;
    public final int mask;
    public final int maxSize;
    public final int bitPerNode;
    public final int width;

    public AbstractPersistentCollection() {
        this(6, 5);
    }

    public AbstractPersistentCollection(int maxSize) {
        this((int) Math.ceil(log(maxSize, (int) Math.pow(2, 5))), 5);
    }

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
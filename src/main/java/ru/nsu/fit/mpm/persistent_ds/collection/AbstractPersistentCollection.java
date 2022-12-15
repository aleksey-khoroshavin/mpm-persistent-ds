package ru.nsu.fit.mpm.persistent_ds.collection;

/**
 * Описывает класс абстрактной персистентной коллекции с поддержкой механизма undo-redo
 */
public abstract class AbstractPersistentCollection implements UndoRedoCollection {
    public final int depth;
    public final int bitPerLevel;
    public final int mask;
    public final int maxSize;
    public final int bitPerNode;
    public final int width;

    protected AbstractPersistentCollection(int depth, int bitPerNode) {
        this.depth = depth;
        this.bitPerNode = bitPerNode;

        bitPerLevel = bitPerNode * depth;
        mask = (int) Math.pow(2, bitPerNode) - 1;
        maxSize = (int) Math.pow(2, bitPerLevel);

        width = (int) Math.pow(2, bitPerNode);
    }

    protected static double log(int n, int newBase) {
        return (Math.log(n) / Math.log(newBase));
    }
}
package ru.nsu.fit.mpm.persistent_ds.collection;

/**
 * Описывает класс абстрактной персистентной коллекции с поддержкой механизма undo-redo
 */
public abstract class AbstractPersistentCollection implements UndoRedoCollection {
    public final int depth;
    public final int mask;
    public final int maxSize;
    public final int bitPerEdge;
    public final int width;

    protected AbstractPersistentCollection(int depth, int bitPerEdge) {
        this.depth = depth;
        this.bitPerEdge = bitPerEdge;

        mask = (int) Math.pow(2, bitPerEdge) - 1;
        maxSize = (int) Math.pow(2, bitPerEdge * depth);

        width = (int) Math.pow(2, bitPerEdge);
    }

    protected static double log(int n, int newBase) {
        return (Math.log(n) / Math.log(newBase));
    }
}
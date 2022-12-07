package ru.nsu.fit.mpm.persistent_ds.nodes;

public abstract class AbstractNode {
    public static int bitPerNode = 1;
    public static int width;

    static {
        width = (int) Math.pow(2, bitPerNode);
    }
}

package ru.nsu.fit.mpm.persistent_ds;

import java.util.ArrayDeque;

public class HeadList<E> extends HeadArray<E> {
    public int first = -1;
    public int last = -1;
    public int sizeTree = 0;
    public ArrayDeque<Integer> deadList;

    public HeadList() {
        super();
    }

    public void copyOther(HeadList<E> other) {
        this.first = other.first;
        this.last = other.last;
        this.sizeTree = other.sizeTree;
        this.deadList = other.deadList;
    }

    public HeadList(HeadList<E> other) {
        super(other);
        copyOther(other);
    }

    public HeadList(HeadList<E> other, Integer sizeDelta) {
        super(other, sizeDelta);
        copyOther(other);
    }

    public HeadList(HeadList<E> other, Integer newSize, Integer maxIndex) {
        super(other, newSize, maxIndex);
        copyOther(other);
    }

    @Override
    public String toString() {
        return String.format("%09x s:%d; S:%d; F:%d; L:%d; D:%09x%s",
                this.hashCode(),
                size,
                sizeTree,
                first,
                last,
                deadList != null ? deadList.hashCode() : 0,
                deadList != null ? deadList.toString() : "[]"
        );
    }
}
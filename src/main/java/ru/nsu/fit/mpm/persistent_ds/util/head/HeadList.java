package ru.nsu.fit.mpm.persistent_ds.util.head;

import java.util.Deque;

public class HeadList<E> extends HeadArray<E> {
    private int first = -1;
    private int last = -1;
    private int sizeTree = 0;
    private Deque<Integer> deadList;

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

    public int getFirst() {
        return first;
    }

    public void setFirst(int first) {
        this.first = first;
    }

    public int getLast() {
        return last;
    }

    public void setLast(int last) {
        this.last = last;
    }

    public int getSizeTree() {
        return sizeTree;
    }

    public void setSizeTree(int sizeTree) {
        this.sizeTree = sizeTree;
    }

    public Deque<Integer> getDeadList() {
        return deadList;
    }

    public void setDeadList(Deque<Integer> deadList) {
        this.deadList = deadList;
    }

    @Override
    public String toString() {
        return String.format("%09x s:%d; S:%d; F:%d; L:%d; D:%09x%s",
                this.hashCode(),
                getSize(),
                sizeTree,
                first,
                last,
                deadList != null ? deadList.hashCode() : 0,
                deadList != null ? deadList.toString() : "[]"
        );
    }
}
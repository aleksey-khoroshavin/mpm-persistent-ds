package ru.nsu.fit.mpm.persistent_ds;

import java.util.LinkedList;
import java.util.Stack;

public abstract class AbstractPersistentCollection<E> {
    public final int depth;
    public final int bitPerLevel;
    public final int mask;
    public final int maxSize;
    public final int bitPerNode;
    public final int width;

    protected final Stack<Head<E>> undo = new Stack<>();
    protected final Stack<Head<E>> redo = new Stack<>();

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

        Head<E> head = new Head<>();
        undo.push(head);
        redo.clear();
    }

    public void undo() {
        if (!undo.empty()) {
            redo.push(undo.pop());
        }
    }

    public void redo() {
        if (!redo.empty()) {
            undo.push(redo.pop());
        }
    }

    public static double log(int N, int newBase) {
        return (Math.log(N) / Math.log(newBase));
    }

    protected Node<E> getLeaf(Head<E> head, int index) {
        if (index >= head.size)
            throw new IndexOutOfBoundsException();

        int level = bitPerLevel - bitPerNode;
        Node<E> node = head.root;

        while (level > 0) {
            int tempIndex = (index >> level) & mask;
            node = node.child.get(tempIndex);
            level -= bitPerNode;
        }

        return node;
    }

    public int calcUniqueLeafs() {
        LinkedList<Node<E>> list = new LinkedList<>();
        calcUniqueLeafs(list, undo);
        calcUniqueLeafs(list, redo);

        return list.size();
    }

    private void calcUniqueLeafs(LinkedList<Node<E>> list, Stack<Head<E>> undo1) {
        for (Head<E> head : undo1) {
            for (int i = 0; i < head.size; i++) {
                Node<E> leaf = getLeaf(head, i);
                if (!list.contains(leaf))
                    list.add(leaf);
            }
        }

    }

    public int size(Head<E> head) {
        return head.size;
    }

    protected Head<E> getCurrentHead() {
        return this.undo.peek();
    }
}
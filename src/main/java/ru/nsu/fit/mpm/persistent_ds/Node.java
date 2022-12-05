package ru.nsu.fit.mpm.persistent_ds;

import java.util.ArrayList;
import java.util.List;

public class Node<E> {

    public static int bitPerNode = 1;
    public static int width;
    public List<E> data;
    public List<Node<E>> children = new ArrayList<>();
    public Node<E> parent;

    static {
        width = (int) Math.pow(2, bitPerNode);
    }

    public void createChildren() {
        Node<E> node = new Node<E>();
        node.parent = this;
        if (children.size() < width) {
            children.add(node);
        }
    }

    public List<Node<E>> getChildren() {
        return children;
    }
}

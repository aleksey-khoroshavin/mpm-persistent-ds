package ru.nsu.fit.mpm.persistent_ds;

import java.util.ArrayList;
import java.util.List;

public class Node<E> {

    public static int bitPerNode = 1;
    public static int width;
    public List<E> data;
    //    public List<Node<E>> children = new ArrayList<>();
//    public Node<E> parent;
    public List<Node<E>> child = new ArrayList<>();

    static {
        width = (int) Math.pow(2, bitPerNode);
    }

    public Node() {

    }

    public Node(Node<E> prevRoot) {
        //TODO check
        if (prevRoot.child != null) {
            child.addAll(prevRoot.child);
        }

        if (prevRoot.data != null) {
            data = new ArrayList<>();
            data.addAll(prevRoot.data);
        }
    }

    public void createChildren() {
        Node<E> node = new Node<E>();
        if (child.size() < width) {
            child.add(node);
        }
    }

    public List<Node<E>> getChild() {
        return child;
    }
}

package ru.nsu.fit.mpm.persistent_ds;

import java.util.ArrayList;
import java.util.List;

public class Node<E> {
    public static int bitPerNode = 5;
    public static int width = (int) Math.pow(2, bitPerNode);

    public List<Node<E>> child;
    public List<E> value;

    public Node() {
    }

    public Node(Node<E> other) {
        if (other.child != null) {
            child = new ArrayList<>();
            child.addAll(other.child);
        }
        if (other.value != null) {
            value = new ArrayList<>();
            value.addAll(other.value);
        }
    }

    public boolean isEmpty() {
        if ((child == null) && (value == null))
            return true;
        boolean result = true;
        if ((child != null) && (!child.isEmpty()))
            result = false;
        if ((value != null) && (!value.isEmpty()))
            result = false;
        return result;
    }
}

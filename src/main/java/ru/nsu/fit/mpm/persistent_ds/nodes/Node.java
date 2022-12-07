package ru.nsu.fit.mpm.persistent_ds.nodes;

import java.util.ArrayList;
import java.util.List;

public class Node<E> extends AbstractNode {
    public List<Node<E>> child;

    public Node() {
    }

    /// Копирование содержимого при копировании пути
    public Node(Node<E> other) {
        if (other.child != null) {
            child = new ArrayList<>();
            child.addAll(other.child);
        }

        if (other.data != null) {
            data = new ArrayList<>();
            data.addAll(other.data);
        }
    }

    public List<Node<E>> getChild() {
        return child;
    }
}
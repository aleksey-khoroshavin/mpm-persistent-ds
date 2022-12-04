package ru.nsu.fit.mpm.persistent_ds;
import java.util.List;

public class Node<E> {

    private int width = 4;
    private E[] data;
    private Node<E> parent;
    private List<Node<E>> children;

    public E[] getData() {
        return data;
    }

    public void setData(E[] data) {
        this.data = data;
    }

    public Node<E> getParent() {
        return parent;
    }

    public void setParent(Node<E> parent) {
        this.parent = parent;
    }

    public List<Node<E>> getChildren() {
        return children;
    }

    public void setChildren(List<Node<E>> children) {
        this.children = children;
    }
}

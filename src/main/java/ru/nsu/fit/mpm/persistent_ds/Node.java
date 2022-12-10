package ru.nsu.fit.mpm.persistent_ds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Node<E> {

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

    public Node(Node<E> other, int maxIndex) {
        if (other.child != null) {
            child = new ArrayList<>();
            for (int i = 0; i <= maxIndex; i++) {
                child.add(other.child.get(i));
            }
        }

        if (other.value != null) {
            value = new ArrayList<>();
            for (int i = 0; i <= maxIndex; i++) {
                value.add(other.value.get(i));
            }
        }
    }

    @Override
    public String toString() {
        String childs = child == null ? "[null]" : Arrays.toString(child.toArray());
        String values = value == null ? "[null]" : Arrays.toString(value.toArray());
        return String.format("%09x %s %s", hashCode(), childs, values);
    }

    public boolean isEmpty() {
        if ((child == null) && (value == null)) {
            return true;
        }

        boolean result = true;

        if ((child != null) && (!child.isEmpty())) {
            result = false;
        }

        if ((value != null) && (!value.isEmpty())) {
            result = false;
        }

        return result;
    }

    private String drawTab(int count) {
        String s = "";
        for (int i = 0; i < count; i++) {
            s += "  ";
        }
        return s;
    }

    private String drawGraph(Node<E> node, int level) {
        String hash = String.format("%09x", node.hashCode()) + " ";
        StringBuilder result = new StringBuilder();
        if (node.child == null) {
            if (node.value == null)
                return drawTab(level) + hash + "\n";
            else
                return drawTab(level) + hash + node.value.toString() + "\n";
        } else {
            result
                    .append(drawTab(level))
                    .append(hash)
                    .append("\n");

            for (Node<E> n : node.child) {
                result.append(drawGraph(n, level + 1));
            }
        }
        return result.toString();
    }

    public String drawGraph() {
        return drawGraph(this, 0);
    }
}
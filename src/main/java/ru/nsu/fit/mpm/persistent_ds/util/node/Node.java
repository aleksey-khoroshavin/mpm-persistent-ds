package ru.nsu.fit.mpm.persistent_ds.util.node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Узел B-дерева для персистентной коллекции
 */
public class Node<E> {
    private List<Node<E>> child;
    private List<E> value;

    public Node() {
    }

    public Node(Node<E> other) {
        if (other != null) {
            if (other.child != null) {
                child = new ArrayList<>();
                child.addAll(other.child);
            }

            if (other.value != null) {
                value = new ArrayList<>();
                value.addAll(other.value);
            }
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

    /**
     * Возвращает список потомков этого узла.
     *
     * @return список потомков этого узла
     */
    public List<Node<E>> getChild() {
        return child;
    }

    /**
     * Устанавливает список потомков этому узлу.
     *
     * @param child список потомков
     */
    public void setChild(List<Node<E>> child) {
        this.child = child;
    }

    /**
     * Возвращает список значений этого узла.
     *
     * @return список значений этого узла.
     */
    public List<E> getValue() {
        return value;
    }

    /**
     * Устанавливает список значений этому узлу.
     *
     * @param value список значений
     */
    public void setValue(List<E> value) {
        this.value = value;
    }

    /**
     * Возвращает true, если узел не имеет потомков и не содержит значений.
     *
     * @return true, если узел не имеет потомков и не содержит значений
     */
    public boolean isEmpty() {
        if ((child == null) && (value == null)) {
            return true;
        }

        if ((value != null) && (!value.isEmpty())) {
            return false;
        }

        return (child == null) || (child.isEmpty());
    }

    /**
     * Возвращает строковое представление содержимого узла.
     *
     * @return строковое представление содержимого узла
     */
    @Override
    public String toString() {
        String childNodes = child == null ? "[child null]" : Arrays.toString(child.toArray());
        String values = value == null ? "[value null]" : Arrays.toString(value.toArray());
        return String.format("%09x %s %s", hashCode(), childNodes, values);
    }

    private String drawTab(int count) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < count; i++) {
            s.append("  ");
        }
        return s.toString();
    }

    private String drawGraph(Node<E> node, int level) {
        String hash = String.format("%09x", node.hashCode()) + " ";
        StringBuilder result = new StringBuilder();
        if (node.child == null) {
            if (node.value == null) {
                return drawTab(level) + hash + "\n";
            } else {
                return drawTab(level) + hash + node.value.toString() + "\n";
            }
        } else {
            result.append(drawTab(level)).append(hash).append("\n");

            for (Node<E> n : node.child) {
                if (n != null) {
                    result.append(drawGraph(n, level + 1));
                }
            }
        }
        return result.toString();
    }

    public String drawGraph() {
        return drawGraph(this, 0);
    }
}
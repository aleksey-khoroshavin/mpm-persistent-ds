package ru.nsu.fit.mpm.persistent_ds;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;

public class PersistentArray<E> extends AbstractPersistentCollection<E> {

    public Head<E> head;
    public Stack<Head<E>> undo = new Stack<>();
    public Stack<Head<E>> redo = new Stack<>();

    public PersistentArray() {
        head = new Head<>();
        undo.push(head);
        createBranch(head.root, depth);
    }

    @Override
    public void undo() {
        if (!undo.empty()) {
            redo.push(undo.pop());
        }
    }

    @Override
    public void redo() {
        if (!redo.empty()) {
            undo.push(redo.pop());
        }
    }

    @Override
    public boolean add(E element) {
        int level = bitPerLevel - Node.bitPerNode;
        Node<E> currentNode = head.root;

        while (level > 0) {
            int index = (head.count >> level) & mask;
            if (currentNode.children.size() - 1 != index) {
                currentNode.createChildren();
            }
            System.out.println(index + " " + currentNode.children.size());
            currentNode = currentNode.children.get(index);
            level -= Node.bitPerNode;
        }

        int index = head.count & mask;

        if (currentNode.data == null) {
            currentNode.data = new ArrayList<>();
        }

        currentNode.data.add(index, element);
        head.count++;

        Head<E> newHead = new Head<>(head);
        undo.push(newHead);
        while (!redo.empty()) {
            redo.pop();
        }

        return true;
    }

    @Override
    public E get(int index) {
        int level = bitPerLevel - Node.bitPerNode;
        Node<E> node = head.root;

        while (level > 0) {
            int tempIndex = (index >> level) & mask;
            node = node.children.get(tempIndex);
            level -= Node.bitPerNode;
        }

        return node.data.get(index & mask);
    }

    @Override
    public int size() {
        return head.count;
    }

    @Override
    public boolean isEmpty() {
        return head.count <= 0;
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public Iterator<E> iterator() {
        return null;
    }

    @Override
    public Object[] toArray() {
        Object[] objects = new Object[head.count];
        for (int i = 0; i < objects.length; i++) {
            objects[i] = this.get(i);
        }
        return objects;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] toArray(T[] a) {
        for (int i = 0; i < a.length; i++) {
            a[i] = (T) this.get(i);
        }
        return a;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return false;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public E set(int index, E element) {
        return null;
    }

    @Override
    public void add(int index, E element) {

    }

    @Override
    public E remove(int index) {
        return null;
    }

    @Override
    public int indexOf(Object o) {
        return 0;
    }

    @Override
    public int lastIndexOf(Object o) {
        return 0;
    }

    @Override
    public ListIterator<E> listIterator() {
        return null;
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return null;
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return null;
    }

    public void createBranch(Node<E> node, int depth) {
        node.createChildren();
        if (depth > 0) {
            createBranch(node.getChildren().get(0), --depth);
        }
    }
}

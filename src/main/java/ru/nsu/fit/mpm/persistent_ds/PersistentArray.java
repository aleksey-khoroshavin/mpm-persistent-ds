package ru.nsu.fit.mpm.persistent_ds;

import ru.nsu.fit.mpm.persistent_ds.nodes.AbstractNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;

public class PersistentArray<E> extends AbstractPersistentCollection<E> {

    private Stack<Head<E>> undo = new Stack<>();
    private Stack<Head<E>> redo = new Stack<>();

    public PersistentArray() {
        Head<E> head = new Head<>();
        undo.push(head);
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
    public boolean add(E newElement) {
        if (getCurrentHead().size == maxSize()) {
            return false;
        }

        Head<E> newHead = new Head<>(getCurrentHead(), +1);
        undo.push(newHead);
        redo.clear();
        AbstractNode<E> currentNode = newHead.root;
        int level = AbstractNode.bitPerNode * (depth - 1);
        System.out.print(newElement + "   ");
        while (level > 0) {
            int index = ((newHead.size - 1) >> level) & mask;
            System.out.print(index);
            AbstractNode<E> tmp, newNode;
            if (currentNode.child == null) {
                currentNode.child = new LinkedList<>();
                newNode = new AbstractNode<>();
                currentNode.child.add(newNode);
            } else {
                if (index == currentNode.child.size()) {
                    newNode = new AbstractNode<>();
                    currentNode.child.add(newNode);
                } else {
                    tmp = currentNode.child.get(index);
                    newNode = new AbstractNode<>(tmp);
                    currentNode.child.set(index, newNode);
                }
            }

            currentNode = newNode;
            level -= AbstractNode.bitPerNode;
        }
        if (currentNode.value == null)
            currentNode.value = new ArrayList<>();
        currentNode.value.add(newElement);
        System.out.println();
        return true;
    }

    @Override
    public E get(int index) {
        int level = bitPerLevel - AbstractNode.bitPerNode;
        AbstractNode<E> node = getCurrentHead().root;
        while (level > 0) {
            int tempIndex = (index >> level) & mask;
            node = node.child.get(tempIndex);
            level -= AbstractNode.bitPerNode;
        }
        return node.value.get(index & mask);
    }

    private Head<E> getCurrentHead() {
        return this.undo.peek();
    }

    @Override
    public int size() {
        return getCurrentHead().size;
    }

    @Override
    public boolean isEmpty() {
        return getCurrentHead().size <= 0;
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
        Object[] objects = new Object[getCurrentHead().size];
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
}
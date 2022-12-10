package ru.nsu.fit.mpm.persistent_ds;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class PersistentLinkedList<E> extends AbstractPersistentCollection<PersistentLinkedListElement<E>> implements List<E> {


    public PersistentLinkedList() {
        super();
    }

    public PersistentLinkedList(int maxSize) {
        super(maxSize);
    }

    public PersistentLinkedList(int depth, int bit_na_pu) {
        super(depth, bit_na_pu);
    }

    public PersistentLinkedList(PersistentLinkedList<E> other) {
        super(other.depth, other.bitPerNode);
        this.undo.addAll(other.undo);
        this.redo.addAll(other.redo);
    }

    @Override
    public int size() {
        return size(getCurrentHead());
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
        return new Object[0];
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return null;
    }

    @Override
    public boolean add(E e) {
        Head<PersistentLinkedListElement<E>> newHead = new Head<>(getCurrentHead(), 0);
        undo.push(newHead);
        redo.clear();

        return add(newHead, e);
    }

    private boolean add(Head<PersistentLinkedListElement<E>> head, E newElement) {
        if (head.size + 1 >= maxSize) {
            return false;
        }

        head.size += 1;

        Node<PersistentLinkedListElement<E>> currentNode = head.root;
        int level = bitPerNode * (depth - 1);

        while (level > 0) {
            int index = ((head.size - 1) >> level) & mask;
            Node<PersistentLinkedListElement<E>> tmp, newNode;

            if (currentNode.child == null) {
                currentNode.child = new LinkedList<>();
                newNode = new Node<>();
                currentNode.child.add(newNode);
            } else {
                if (index == currentNode.child.size()) {
                    newNode = new Node<>();
                    currentNode.child.add(newNode);
                } else {
                    tmp = currentNode.child.get(index);
                    newNode = new Node<>(tmp);
                    currentNode.child.set(index, newNode);
                }
            }

            currentNode = newNode;
            level -= bitPerNode;
        }

        if (currentNode.value == null) {
            currentNode.value = new ArrayList<>();
        }

        return true;
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
    public E get(int index) {
        return null;
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

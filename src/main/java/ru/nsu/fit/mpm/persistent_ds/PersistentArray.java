package ru.nsu.fit.mpm.persistent_ds;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Stack;

public class PersistentArray<E> extends AbstractPersistentCollection<E> {
    private Stack<Head<E>> undo = new Stack<>();
    private Stack<Head<E>> redo = new Stack<>();

    public PersistentArray() {
        super(6);
        Head<E> head = new Head<>();
        undo.push(head);
    }

    public PersistentArray(int maxSize) {
        super((int) Math.ceil(log(maxSize, (int) Math.pow(2, Node.bitPerNode))));
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

    public E pop() throws NoSuchElementException {
        if (getCurrentHead().size == 0) {
            throw new NoSuchElementException("Array is empty");
        }
        Head<E> newHead = new Head<>(getCurrentHead(), -1);
        undo.push(newHead);
        redo.clear();
        LinkedList<Pair<Node<E>, Integer>> path = new LinkedList<>();
        path.add(new Pair<>(newHead.root, 0));
        int level = Node.bitPerNode * (depth - 1);
        while (level > 0) {
            int index = (newHead.size >> level) & mask;
            Node<E> tmp, newNode;
            tmp = path.getLast().getKey().child.get(index);
            newNode = new Node<>(tmp);
            path.getLast().getKey().child.set(index, newNode);
            path.add(new Pair<>(newNode, index));
            level -= Node.bitPerNode;
        }
        int index = newHead.size & mask;
        E result = path.getLast().getKey().value.remove(index);
        for (int i = path.size() - 1; i >= 1; i--) {
            Pair<Node<E>, Integer> elem = path.get(i);
            if (elem.getKey().isEmpty()) {
                path.get(i - 1).getKey().child.set(elem.getValue(), null);
            } else
                break;
        }
        return result;
    }

    public boolean assoc(int index, E value) {
        if (getCurrentHead().size == maxSize) {
            return false;
        }

        Head<E> oldHead = getCurrentHead();
        Node<E> copedNode = copyNode(oldHead, index, +1);

        copedNode.value.add(index, value);
        if (copedNode.value.size() > Node.width) {
            copedNode.value.remove(copedNode.value.size() - 1);
            for (int i = index + 1; i < oldHead.size; i++) {
                conj(get(oldHead, i));
            }
        }

        return true;
    }

    private Node<E> copyNode(Head<E> head, int insertIndex, int sizeDelta) {
        if (getCurrentHead().size == maxSize) {
            throw new IllegalStateException("array is full");
        }

        Head<E> newHead = new Head<>(head, sizeDelta);
        undo.push(newHead);
        redo.clear();
        Node<E> currentNode = newHead.root;
        int level = Node.bitPerNode * (depth - 1);

        while (level > 0) {
            int index = (insertIndex >> level) & mask;
            Node<E> tmp, newNode;

            tmp = currentNode.child.get(index);
            newNode = new Node<>(tmp);
            currentNode.child.set(index, newNode);

            currentNode = newNode;
            level -= Node.bitPerNode;
        }

        return currentNode;
    }

    public boolean conj(E newElement) {
        if (getCurrentHead().size == maxSize) {
            return false;
        }
        Head<E> newHead = new Head<>(getCurrentHead(), +1);
        undo.push(newHead);
        redo.clear();
        Node<E> currentNode = newHead.root;
        int level = Node.bitPerNode * (depth - 1);
        while (level > 0) {
            int index = ((newHead.size - 1) >> level) & mask;
            Node<E> tmp, newNode;
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
            level -= Node.bitPerNode;
        }
        if (currentNode.value == null)
            currentNode.value = new ArrayList<>();
        currentNode.value.add(newElement);
        return true;
    }

    @Override
    public boolean add(E newElement) {
        return conj(newElement);
    }

    private Node<E> getNode(Node<E> root, int index) {
        int level = bitPerLevel - Node.bitPerNode;
        Node<E> node = root;

        while (level > 0) {
            int tempIndex = (index >> level) & mask;
            node = node.child.get(tempIndex);
            level -= Node.bitPerNode;
        }

        return node;
    }

    private E get(Head<E> head, int index) {
        return getNode(head.root, index).value.get(index & mask);
    }

    @Override
    public E get(int index) {
        return get(getCurrentHead(), index);
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
        return new PersistentArrayIterator<E>();
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
        undo.clear();
        redo.clear();
        Head<E> head = new Head<>();
        undo.push(head);
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

    public class PersistentArrayIterator<E> implements java.util.Iterator<E> {
        int index = 0;

        @Override
        public boolean hasNext() {
            return index < size();
        }

        @Override
        public E next() {
            return (E) get(index++);
        }

        @Override
        public void remove() {
        }
    }
}
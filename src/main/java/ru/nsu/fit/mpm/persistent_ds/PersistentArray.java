package ru.nsu.fit.mpm.persistent_ds;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Stack;

public class PersistentArray<E> extends AbstractPersistentCollection<E> implements List<E> {

    public PersistentArray() {
        super();
    }

    public PersistentArray(int maxSize) {
        super(maxSize);
    }

    public PersistentArray(int depth, int bitPerNode) {
        super(depth, bitPerNode);
    }

    public PersistentArray(PersistentArray<E> other) {
        super(other.depth, other.bitPerNode);
        this.undo.addAll(other.undo);
        this.redo.addAll(other.redo);
    }

    public int getVersionCount() {
        return undo.size() + redo.size();
    }

    public int calcUniqueLeafs() {
        LinkedList<Node<E>> list = new LinkedList<>();
        calcUniqueLeafs(list, undo);
        calcUniqueLeafs(list, redo);

        return list.size();
    }

    private void calcUniqueLeafs(LinkedList<Node<E>> list, Stack<Head<E>> undo1) {
        for (Head<E> head : undo1) {
            for (int i = 0; i < head.size; i++) {
                Node<E> leaf = getLeaf(head, i);
                if (!list.contains(leaf))
                    list.add(leaf);
            }
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
        int level = bitPerNode * (depth - 1);
        while (level > 0) {
            int index = (newHead.size >> level) & mask;
            Node<E> tmp, newNode;
            tmp = path.getLast().getKey().child.get(index);
            newNode = new Node<>(tmp);
            path.getLast().getKey().child.set(index, newNode);
            path.add(new Pair<>(newNode, index));
            level -= bitPerNode;
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


    private String toString(Head<E> head) {
        return "size: " + size(head) + "; unique leafs: "
                + calcUniqueLeafs() + "; array: " + Arrays.toString(toArray(head));
    }

    public String drawGraph() {
        return getCurrentHead().root.drawGraph();
    }

    @Override
    public String toString() {
        return toString(getCurrentHead());
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public E remove(int index) {
        E result = get(index);

        if (index >= getCurrentHead().size || index < 0) {
            throw new IndexOutOfBoundsException();
        }

        Head<E> oldHead = getCurrentHead();
        Head<E> newHead;

        if (index == 0) {
            newHead = new Head<>();
            undo.push(newHead);
            redo.clear();
        } else {
            Pair<Node<E>, Integer> copedNodeP = copyLeafInsert(oldHead, index);
            newHead = getCurrentHead();
            int ind = copedNodeP.getValue();
            copedNodeP.getKey().value.remove(ind);
            newHead.size += -1;
        }

        for (int i = index + 1; i < oldHead.size; i++) {
            add(newHead, get(oldHead, i));
        }

        return result;
    }

    @Override
    public void add(int index, E value) {
        if (index >= getCurrentHead().size || index < 0) {
            throw new IndexOutOfBoundsException();
        }

        Head<E> oldHead = getCurrentHead();

        Pair<Node<E>, Integer> copedNodeP = copyLeafInsert(oldHead, index);
        Head<E> newHead = getCurrentHead();
        int leafIndex = copedNodeP.getValue();
        Node<E> copedNode = copedNodeP.getKey();
        copedNode.value.set(leafIndex, value);

        for (int i = index; i < oldHead.size; i++) {
            add(newHead, get(oldHead, i));
        }
    }

    private Pair<Node<E>, Integer> copyLeafInsert(Head<E> oldHead, int index) {
        if (getCurrentHead().size == maxSize) {
            throw new IllegalStateException("array is full");
        }

        int level = bitPerNode * (depth - 1);
        Head<E> newHead = new Head<>(oldHead, index + 1, (index >> level) & mask);

        undo.push(newHead);
        redo.clear();
        Node<E> currentNode = newHead.root;

        while (level > 0) {
            int widthIndex = (index >> level) & mask;
            int widthIndexNext = (index >> (level - bitPerNode)) & mask;
            Node<E> tmp, newNode;
            tmp = currentNode.child.get(widthIndex);
            newNode = new Node<>(tmp, widthIndexNext);
            currentNode.child.set(widthIndex, newNode);
            currentNode = newNode;
            level -= bitPerNode;
        }

        return new Pair<>(currentNode, index & mask);
    }

    private Pair<Node<E>, Integer> copyLeaf(Head<E> head, int index) {
        if (getCurrentHead().size == maxSize) {
            throw new IllegalStateException("array is full");
        }

        Head<E> newHead = new Head<>(head, 0);
        undo.push(newHead);
        redo.clear();
        Node<E> currentNode = newHead.root;
        int level = bitPerNode * (depth - 1);
        while (level > 0) {
            int widthIndex = (index >> level) & mask;
            Node<E> tmp, newNode;
            tmp = currentNode.child.get(widthIndex);
            newNode = new Node<>(tmp);
            currentNode.child.set(widthIndex, newNode);
            currentNode = newNode;
            level -= bitPerNode;
        }
        return new Pair<>(currentNode, index & mask);
    }

    public PersistentArray<E> conj(E newElement) {
        PersistentArray<E> result = new PersistentArray<>(this);
        result.add(newElement);
        return result;
    }

    @Override
    public boolean add(E newElement) {
        if (getCurrentHead().size == maxSize) {
            return false;
        }
        Head<E> newHead = new Head<>(getCurrentHead(), 0);
        undo.push(newHead);
        redo.clear();

        return add(newHead, newElement);
    }

    private boolean add(Head<E> head, E newElement) {
        if (head.size == maxSize) {
            return false;
        }
        head.size += 1;
        Node<E> currentNode = head.root;
        int level = bitPerNode * (depth - 1);
        while (level > 0) {
            int index = ((head.size - 1) >> level) & mask;
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
            level -= bitPerNode;
        }

        if (currentNode.value == null) {
            currentNode.value = new ArrayList<>();
        }

        currentNode.value.add(newElement);
        return true;
    }

    private E get(Head<E> head, int index) {
        if (!((index < head.size) && (index >= 0))) {
            throw new IndexOutOfBoundsException();
        }
        return getLeaf(head, index).value.get(index & mask);
    }

    @Override
    public E get(int index) {
        return get(getCurrentHead(), index);
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
        return new PersistentArrayIterator<>();
    }

    private Object[] toArray(Head<E> head) {
        Object[] objects = new Object[head.size];
        for (int i = 0; i < objects.length; i++) {
            objects[i] = this.get(head, i);
        }
        return objects;
    }

    @Override
    public Object[] toArray() {
        return toArray(getCurrentHead());
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
        Head<E> head = new Head<>();
        undo.push(head);
        redo.clear();
    }

    public PersistentArray<E> assoc(int index, E element) {
        PersistentArray<E> result = new PersistentArray<>(this);
        result.set(index, element);
        return result;
    }

    @Override
    public E set(int index, E element) {
        Pair<Node<E>, Integer> pair = copyLeaf(getCurrentHead(), index);
        pair.getKey().value.set(pair.getValue(), element);
        return get(index);
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
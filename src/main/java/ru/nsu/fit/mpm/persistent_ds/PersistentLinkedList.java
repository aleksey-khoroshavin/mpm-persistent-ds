package ru.nsu.fit.mpm.persistent_ds;

import javafx.util.Pair;

import java.util.*;

public class PersistentLinkedList<E> extends AbstractPersistentCollection<PersistentLinkedListElement<E>> implements List<E> {

    protected final Stack<HeadList<PersistentLinkedListElement<E>>> redo = new Stack<>();
    protected final Stack<HeadList<PersistentLinkedListElement<E>>> undo = new Stack<>();

    public PersistentLinkedList() {
        this(6, 5);
    }

    public PersistentLinkedList(int maxSize) {
        this((int) Math.ceil(log(maxSize, (int) Math.pow(2, 5))), 5);
    }

    public PersistentLinkedList(int depth, int bitPerNode) {
        super(depth, bitPerNode);
        HeadList<PersistentLinkedListElement<E>> head = new HeadList<>();
        undo.push(head);
        redo.clear();
    }

    public PersistentLinkedList(PersistentLinkedList<E> other) {
        super(other.depth, other.bitPerNode);
        this.undo.addAll(other.undo);
        this.redo.addAll(other.redo);
    }

    public void undo() {
        if (!undo.empty()) {
            redo.push(undo.pop());
        }
    }

    public void redo() {
        if (!redo.empty()) {
            undo.push(redo.pop());
        }
    }

    public int getUniqueLeafsSize() {
        LinkedList<Node<PersistentLinkedListElement<E>>> list = new LinkedList<>();
        getUniqueLeafsSize(list, undo);
        getUniqueLeafsSize(list, redo);

        return list.size();
    }

    private void getUniqueLeafsSize(LinkedList<Node<PersistentLinkedListElement<E>>> list, Stack<HeadList<PersistentLinkedListElement<E>>> undo1) {
        for (HeadList<PersistentLinkedListElement<E>> head : undo1) {
            for (int i = 0; i < head.size; i++) {
                Node<PersistentLinkedListElement<E>> leaf = getLeaf(head, i).getKey();
                if (!list.contains(leaf))
                    list.add(leaf);
            }
        }

    }

    protected HeadList<PersistentLinkedListElement<E>> getCurrentHead() {
        return this.undo.peek();
    }

    public int size(HeadList<PersistentLinkedListElement<E>> head) {
        return head.size;
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

    private Object[] toArray(HeadList<PersistentLinkedListElement<E>> head) {
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

    @Override
    public <T> T[] toArray(T[] a) {
        return null;
    }

    public boolean isFull() {
        return isFull(getCurrentHead());
    }

    public boolean isFull(HeadList<PersistentLinkedListElement<E>> head) {
        return head.sizeTree >= maxSize;
    }


    @Override
    public boolean add(E newValue) {
        if (isFull()) {
            return false;
        }

        PersistentLinkedListElement<E> element;

        HeadList<PersistentLinkedListElement<E>> prevHead = getCurrentHead();
        HeadList<PersistentLinkedListElement<E>> head;

        if (getCurrentHead().size == 0) {
            head = new HeadList<>(prevHead);
            undo.push(head);
            redo.clear();
            element = new PersistentLinkedListElement<>(newValue, -1, -1);
            head.first = head.sizeTree;
        } else {
            element = new PersistentLinkedListElement<>(newValue, prevHead.last, -1);
            Pair<Node<PersistentLinkedListElement<E>>, Integer> pair = copyLeaf(prevHead, prevHead.last);
            head = getCurrentHead();
            PersistentLinkedListElement<E> prev = new PersistentLinkedListElement<>(pair.getKey().value.get(pair.getValue()));
            prev.next = head.sizeTree;
            pair.getKey().value.set(pair.getValue(), prev);
        }
        head.last = head.sizeTree;

        add2(head).value.add(element);
        return true;
    }

    protected Node<PersistentLinkedListElement<E>> add2(HeadList<PersistentLinkedListElement<E>> head) {
        if (isFull(head)) {
            throw new IndexOutOfBoundsException("collection is full");
        }

        head.size += 1;
        head.sizeTree += 1;

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

        return currentNode;
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

    private E get(HeadList<PersistentLinkedListElement<E>> head, int index) {
        if (!((index < head.size) && (index >= 0))) {
            throw new IndexOutOfBoundsException();
        }
        return getLeaf(head, index).getKey().value.get(index & mask).value;
    }

    protected Pair<Node<PersistentLinkedListElement<E>>, Integer> getLeaf(HeadList<PersistentLinkedListElement<E>> head, int index) {
        if (index >= head.size) {
            throw new IndexOutOfBoundsException();
        }

        int level = bitPerLevel - bitPerNode;
        Node<PersistentLinkedListElement<E>> node = head.root;

        while (level > 0) {
            int tempIndex = (index >> level) & mask;
            node = node.child.get(tempIndex);
            level -= bitPerNode;
        }

        return new Pair<>(node, index & mask);
    }

    private Pair<Node<PersistentLinkedListElement<E>>, Integer> copyLeaf(HeadList<PersistentLinkedListElement<E>> head, int index) {
        if (isFull()) {
            throw new IllegalStateException("array is full");
        }

        HeadList<PersistentLinkedListElement<E>> newHead = new HeadList<>(head, 0);
        undo.push(newHead);
        redo.clear();
        Node<PersistentLinkedListElement<E>> currentNode = newHead.root;
        int level = bitPerNode * (depth - 1);

        while (level > 0) {
            int widthIndex = (index >> level) & mask;
            Node<PersistentLinkedListElement<E>> tmp, newNode;

            tmp = currentNode.child.get(widthIndex);
            newNode = new Node<>(tmp);
            currentNode.child.set(widthIndex, newNode);

            currentNode = newNode;
            level -= bitPerNode;
        }

        return new Pair<>(currentNode, index & mask);
    }

    public int getVersionCount() {
        return undo.size() + redo.size();
    }

    public String drawGraph() {
        return "unique:" + getUniqueLeafsSize() + "; ver:" + getVersionCount() + "\n"
                + getCurrentHead() + "\n" + getCurrentHead().root.drawGraph();
    }

    @Override
    public E get(int index) {
        return get(getCurrentHead(), index);
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
package ru.nsu.fit.mpm.persistent_ds;

import java.util.*;

public class PersistentArray<E> implements List<E> {

    public static int depth = 3;
    public static int bitPerLevel = Node.bitPerNode * depth;
    public static int mask = (int) Math.pow(2, Node.bitPerNode) - 1;
    public Node<E> root;
    private int count = 0;

    public PersistentArray() {
        root = new Node<>();
        root.parent = null;
        createBranch(root, depth);
    }

    @Override
    public boolean add(E element) {
        int level = bitPerLevel - Node.bitPerNode;
        Node<E> node = root;

        while (level > 0) {
            int index = (count >> level) & mask;
            if (node.children.size() - 1 != index) {
                node.createChildren();
            }
            System.out.println(index + " " + node.children.size());
            node = node.children.get(index);
            level -= Node.bitPerNode;
        }
        System.out.println("Вышли");

        int index = count & mask;
        if (node.data == null) {
            node.data = new ArrayList<>();
        }
        node.data.add(index, element);
        count++;
        return true;
    }

    @Override
    public E get(int index) {
        int level = bitPerLevel - Node.bitPerNode;
        Node<E> node = root;

        while (level > 0) {
            int tempIndex = (index >> level) & mask;
            node = node.children.get(tempIndex);
            level -= Node.bitPerNode;
        }

        return node.data.get(index & mask);
    }

    @Override
    public int size() {
        return count;
    }

    @Override
    public boolean isEmpty() {
        return count <= 0;
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
        Object[] objects = new Object[count];
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

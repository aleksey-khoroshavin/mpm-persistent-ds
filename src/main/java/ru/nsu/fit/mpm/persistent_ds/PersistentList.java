package ru.nsu.fit.mpm.persistent_ds;

import java.util.*;

public class PersistentList<E> implements List<E> {

    public static int depth = 2;
    public static int bitPerLevel = Node.bitPerNode * depth;
    public static int mask = (int) Math.pow(2, Node.bitPerNode) - 1;

    public Node<LinkedData<E>> root;
    private int count = 0;

    public LinkedData<E> first;
    public LinkedData<E> last;

    public PersistentList() {
        root = new Node<>();
        root.parent = null;
        createBranch(root, depth);
    }

    private LinkedData<E> addFirst(E e) {
        LinkedData<E> oldFirst = first;
        LinkedData<E> newFirst = new LinkedData<E>(e, oldFirst, null);
        first = newFirst;
        if (oldFirst == null) {
            last = newFirst;
        } else {
            oldFirst.prev = newFirst;
        }
        return newFirst;
    }

    private LinkedData<E> addLast(E e) {
        LinkedData<E> oldLast = last;
        LinkedData<E> newLast = new LinkedData<E>(e, null, oldLast);
        last = newLast;
        if (oldLast == null) {
            first = newLast;
        } else {
            oldLast.next = newLast;
        }
        return newLast;
    }

    @Override
    public boolean add(E element) {
        int level = bitPerLevel - Node.bitPerNode;
        Node<LinkedData<E>> node = root;

        while (level > 0) {
            int index = (count >> level) & mask;
            if (node.children.size() - 1 != index) {
                node.createChildren();
            }
            node = node.children.get(index);
            level -= Node.bitPerNode;
        }

        int index = count & mask;

        if (node.data == null) {
            node.data = new ArrayList<>();
        }

        node.data.add(index, addLast(element));
        count++;
        return true;
    }


    @Override
    public E get(int index) {
        if (index > count) {
            throw new IndexOutOfBoundsException();
        } else if (index == 0) {
            return first.data;
        } else if (index == count) {
            return last.data;
        } else if ((count / 2) > index) {
            LinkedData<E> currentLinkedData = first;
            for (int i = 0; i < index; i++) {
                currentLinkedData = currentLinkedData.getNext();
            }
            return currentLinkedData.data;
        } else {
            LinkedData<E> currentLinkedData = last;
            for (int i = count - 1; i > index; i--) {
                currentLinkedData = currentLinkedData.getPrev();
            }
            return currentLinkedData.data;
        }
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

    public void createBranch(Node<LinkedData<E>> node, int depth) {
        node.createChildren();
        if (depth > 0) {
            createBranch(node.getChildren().get(0), --depth);
        }
    }

    private static class LinkedData<E> {
        public E data;
        public LinkedData<E> next;
        public LinkedData<E> prev;

        public LinkedData(E data, LinkedData<E> next, LinkedData<E> prev) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }

        public LinkedData<E> getNext() {
            return next;
        }

        public LinkedData<E> getPrev() {
            return prev;
        }
    }
}

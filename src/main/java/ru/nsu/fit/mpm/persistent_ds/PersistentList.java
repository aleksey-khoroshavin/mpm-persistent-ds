package ru.nsu.fit.mpm.persistent_ds;

import java.util.*;

public class PersistentList<E> extends AbstractPersistentCollection<E> {
    public Head<LinkedData<E>> head;
    public Stack<Head<LinkedData<E>>> undo = new Stack<>();
    public Stack<Head<LinkedData<E>>> redo = new Stack<>();
    public LinkedData<E> first;
    public LinkedData<E> last;

    public PersistentList(int depth) {
        super(depth);
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
        return true;
    }

    @Override
    public E get(int index) {
        if (index > head.size) {
            throw new IndexOutOfBoundsException();
        } else if (index == 0) {
            return first.data;
        } else if (index == head.size) {
            return last.data;
        } else if ((head.size / 2) > index) {
            LinkedData<E> currentLinkedData = first;
            for (int i = 0; i < index; i++) {
                currentLinkedData = currentLinkedData.getNext();
            }
            return currentLinkedData.data;
        } else {
            LinkedData<E> currentLinkedData = last;
            for (int i = head.size - 1; i > index; i--) {
                currentLinkedData = currentLinkedData.getPrev();
            }
            return currentLinkedData.data;
        }
    }

    @Override
    public int size() {
        return head.size;
    }

    @Override
    public boolean isEmpty() {
        return head.size <= 0;
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
        Object[] objects = new Object[head.size];
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
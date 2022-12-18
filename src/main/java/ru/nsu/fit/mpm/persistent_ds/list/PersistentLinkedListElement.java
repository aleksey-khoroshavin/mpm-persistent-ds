package ru.nsu.fit.mpm.persistent_ds.list;

/**
 * Элемент персистентного двусвязного списка
 */
public class PersistentLinkedListElement<E> {
    private int next;
    private int prev;
    private E value;

    public PersistentLinkedListElement(E value, int prev, int next) {
        this.next = next;
        this.prev = prev;
        this.value = value;
    }

    public PersistentLinkedListElement(PersistentLinkedListElement<E> other) {
        this.next = other.next;
        this.prev = other.prev;
        this.value = other.value;
    }

    public int getNext() {
        return next;
    }

    public void setNext(int next) {
        this.next = next;
    }

    public int getPrev() {
        return prev;
    }

    public void setPrev(int prev) {
        this.prev = prev;
    }

    public E getValue() {
        return value;
    }

    public void setValue(E value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("%09x", hashCode()) + "{P" + prev + ", " + value + ", N" + next + "}";
    }
}

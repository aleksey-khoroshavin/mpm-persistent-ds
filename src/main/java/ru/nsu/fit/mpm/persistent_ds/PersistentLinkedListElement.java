package ru.nsu.fit.mpm.persistent_ds;

public class PersistentLinkedListElement<E> {
    int next = -1;
    int prev = -1;
    E value;

    public PersistentLinkedListElement(E value) {
        this.value = value;
    }

    public PersistentLinkedListElement(E value, int prev, int next) {
        this.next = next;
        this.prev = prev;
        this.value = value;
    }

    public PersistentLinkedListElement(int next) {
        this.next = next;
    }

    public PersistentLinkedListElement(PersistentLinkedListElement<E> other) {
        this.next = other.next;
        this.prev = other.prev;
        this.value = other.value;
    }

    @Override
    public String toString() {
        return String.format("%09x", hashCode()) + "{P" + prev + ", " + value + ", N" + next + "}";
    }
}

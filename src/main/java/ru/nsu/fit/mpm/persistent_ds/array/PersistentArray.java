package ru.nsu.fit.mpm.persistent_ds.array;

import javafx.util.Pair;
import ru.nsu.fit.mpm.persistent_ds.collection.AbstractPersistentCollection;
import ru.nsu.fit.mpm.persistent_ds.util.head.HeadArray;
import ru.nsu.fit.mpm.persistent_ds.util.node.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Stack;

/**
 * Персистентный массив
 *
 * @param <E>
 */
public class PersistentArray<E> extends AbstractPersistentCollection implements List<E> {

    private static final String EMPTY_ARRAY_MESSAGE = "Array is empty";
    private static final String FULL_ARRAY_MESSAGE = "Array is full";
    private static final String INVALID_INDEX_MESSAGE = "Invalid index";

    private PersistentArray<PersistentArray<?>> parent;
    private final Stack<PersistentArray<?>> insertedUndo = new Stack<>();
    private final Stack<PersistentArray<?>> insertedRedo = new Stack<>();
    protected final Stack<HeadArray<E>> redo = new Stack<>();
    protected final Stack<HeadArray<E>> undo = new Stack<>();

    public PersistentArray() {
        this(6, 5);
    }

    public PersistentArray(int maxSize) {
        this((int) Math.ceil(log(maxSize, (int) Math.pow(2, 5))), 5);
    }

    public PersistentArray(int depth, int bitPerNode) {
        super(depth, bitPerNode);
        HeadArray<E> head = new HeadArray<>();
        undo.push(head);
        redo.clear();
    }

    public PersistentArray(PersistentArray<E> other) {
        super(other.depth, other.bitPerNode);
        this.undo.addAll(other.undo);
        this.redo.addAll(other.redo);
    }

    @Override
    public void undo() {
        if (!insertedUndo.empty()) {
            insertedUndo.peek().undo();
            insertedRedo.push(insertedUndo.pop());
        } else {
            if (!undo.empty()) {
                redo.push(undo.pop());
            }
        }
    }

    @Override
    public void redo() {
        if (!insertedRedo.empty()) {
            insertedRedo.peek().redo();
            insertedUndo.push(insertedRedo.pop());
        } else {
            if (!redo.empty()) {
                undo.push(redo.pop());
            }
        }
    }

    private void tryParentUndo(E value) {
        if (value instanceof PersistentArray) {
            ((PersistentArray) value).parent = this;
        }
        if (parent != null) {
            parent.insertedUndo.push(this);
        }
    }

    /**
     * Возвращает количество элементов в массиве.
     *
     * @return количество элементов в массиве
     */
    @Override
    public int size() {
        return size(getCurrentHead());
    }

    private int size(HeadArray<E> head) {
        return head.getSize();
    }

    protected HeadArray<E> getCurrentHead() {
        return this.undo.peek();
    }

    private void checkIndex(int index) {
        checkIndex(getCurrentHead(), index);
    }

    private void checkIndex(HeadArray<E> head, int index) {
        if ((index < 0) || (index >= head.getSize())) {
            throw new IndexOutOfBoundsException(INVALID_INDEX_MESSAGE);
        }
    }

    /**
     * Возвращает true, если список полон (size == maxSize).
     *
     * @return true, если список полон
     */
    public boolean isFull() {
        return isFull(getCurrentHead());
    }

    private boolean isFull(HeadArray<E> head) {
        return head.getSize() >= maxSize;
    }

    /**
     * Возвращает true, если массив не содержит элементов.
     *
     * @return true, если массив не содержит элементов
     */
    @Override
    public boolean isEmpty() {
        return getCurrentHead().getSize() <= 0;
    }

    /**
     * Возвращает количество версий массива.
     *
     * @return количество версий массива
     */
    public int getVersionCount() {
        return undo.size() + redo.size();
    }

    /**
     * Заменяет элемент в указанной позиции этого массива указанным элементом.
     *
     * @param index   индекс замняемого элемента
     * @param element элемент, который будет сохранен в указанной позиции
     * @return заменяемый элемент
     */
    @Override
    public E set(int index, E element) {
        checkIndex(index);

        E result = get(index);

        Pair<Node<E>, Integer> copedNodeP = copyLeafToChange(getCurrentHead(), index);
        int leafIndex = copedNodeP.getValue();
        Node<E> copedNode = copedNodeP.getKey();
        copedNode.getValue().set(leafIndex, element);

        tryParentUndo(element);

        return result;
    }

    /**
     * Возвращает копию массива, в которой заменяет элемент в указанной позиции указанным элементом.
     *
     * @param index   индекс замняемого элемента
     * @param element элемент, который будет сохранен в указанной позиции
     * @return измененная копия массива
     */
    public PersistentArray<E> assoc(int index, E element) {
        PersistentArray<E> result = new PersistentArray<>(this);
        result.set(index, element);
        return result;
    }

    /**
     * Добавление нового элмента в конец массива.
     *
     * @param element добавляемый элемент
     * @return true если массив изменился в результате вызова
     */
    @Override
    public boolean add(E element) {
        if (isFull()) {
            throw new IllegalStateException(FULL_ARRAY_MESSAGE);
        }

        HeadArray<E> newHead = new HeadArray<>(getCurrentHead(), 0);
        undo.push(newHead);
        redo.clear();
        tryParentUndo(element);

        return add(newHead, element);
    }

    /**
     * Возвращает копию массива, в конец которой добавлен указанный элемент.
     *
     * @param element добавляемый элемент
     * @return измененная копия массива
     */
    public PersistentArray<E> conj(E element) {
        PersistentArray<E> result = new PersistentArray<>(this);
        result.add(element);
        return result;
    }

    /**
     * Добавление нового элмента по идексу.
     * <p>
     * Вставляет указанный элемент в указанную позицию в этом массиве (дополнительная операция).
     * Сдвигает элемент, находящийся в данный момент в этой позиции (если есть),
     * и любые последующие элементы вправо (добавляет единицу к их индексам).
     * </p>
     *
     * @param index   индекс, по которому указанный элемент должен быть вставлен
     * @param element элемент, который нужно вставить
     */
    @Override
    public void add(int index, E element) {
        checkIndex(index);
        if (isFull()) {
            throw new IllegalStateException(FULL_ARRAY_MESSAGE);
        }

        HeadArray<E> oldHead = getCurrentHead();

        Pair<Node<E>, Integer> copedNodeP = copyLeafToMove(oldHead, index);
        int leafIndex = copedNodeP.getValue();
        Node<E> copedNode = copedNodeP.getKey();
        copedNode.getValue().set(leafIndex, element);

        HeadArray<E> newHead = getCurrentHead();
        for (int i = index; i < oldHead.getSize(); i++) {
            add(newHead, get(oldHead, i));
        }
        tryParentUndo(element);
    }

    private boolean add(HeadArray<E> head, E newElement) {
        add(head).getValue().add(newElement);

        return true;
    }

    private Node<E> add(HeadArray<E> head) {
        if (isFull(head)) {
            throw new IllegalStateException(FULL_ARRAY_MESSAGE);
        }

        head.setSize(head.getSize() + 1);
        Node<E> currentNode = head.getRoot();
        for (int level = bitPerNode * (depth - 1); level > 0; level -= bitPerNode) {
            int widthIndex = ((head.getSize() - 1) >> level) & mask;
            Node<E> tmp;
            Node<E> newNode;

            if (currentNode.getChild() == null) {
                currentNode.setChild(new LinkedList<>());
                newNode = new Node<>();
                currentNode.getChild().add(newNode);
            } else {
                if (widthIndex == currentNode.getChild().size()) {
                    newNode = new Node<>();
                    currentNode.getChild().add(newNode);
                } else {
                    tmp = currentNode.getChild().get(widthIndex);
                    newNode = new Node<>(tmp);
                    currentNode.getChild().set(widthIndex, newNode);
                }
            }
            currentNode = newNode;
        }

        if (currentNode.getValue() == null) {
            currentNode.setValue(new ArrayList<>());
        }
        return currentNode;
    }

    /**
     * Удаляет последний элемент массива.
     *
     * @return последний элемент массива
     */
    public E pop() {
        if (isEmpty()) {
            throw new NoSuchElementException(EMPTY_ARRAY_MESSAGE);
        }

        HeadArray<E> newHead = new HeadArray<>(getCurrentHead(), -1);
        undo.push(newHead);
        redo.clear();
        LinkedList<Pair<Node<E>, Integer>> path = new LinkedList<>();
        path.add(new Pair<>(newHead.getRoot(), 0));
        for (int level = bitPerNode * (depth - 1); level > 0; level -= bitPerNode) {
            int index = (newHead.getSize() >> level) & mask;
            Node<E> tmp;
            Node<E> newNode;
            tmp = path.getLast().getKey().getChild().get(index);
            newNode = new Node<>(tmp);
            path.getLast().getKey().getChild().set(index, newNode);
            path.add(new Pair<>(newNode, index));
        }

        int index = newHead.getSize() & mask;
        E result = path.getLast().getKey().getValue().remove(index);

        for (int i = path.size() - 1; i >= 1; i--) {
            Pair<Node<E>, Integer> elem = path.get(i);
            if (elem.getKey().isEmpty()) {
                path.get(i - 1).getKey().getChild().remove((int) elem.getValue());
            } else {
                break;
            }
        }

        return result;
    }

    /**
     * Удаляет элемент по указанному индексу.
     * <p>
     * Удаляет элемент в указанной позиции в этом массиве.
     * Сдвигает любые последующие элементы влево (вычитает единицу из их индексов).
     * Возвращает элемент, который был удален из массива.
     * </p>
     *
     * @param index индекс удаляемого элемента
     * @return удаленный элемент
     */
    @Override
    public E remove(int index) {
        checkIndex(index);

        E result = get(index);

        HeadArray<E> oldHead = getCurrentHead();
        HeadArray<E> newHead;

        if (index == 0) {
            newHead = new HeadArray<>();
            undo.push(newHead);
            redo.clear();
        } else {
            Pair<Node<E>, Integer> copedNodeP = copyLeafToMove(oldHead, index);
            int leafIndex = copedNodeP.getValue();
            Node<E> copedNode = copedNodeP.getKey();
            copedNode.getValue().remove(leafIndex);

            newHead = getCurrentHead();
            newHead.setSize(newHead.getSize() - 1);
        }

        for (int i = index + 1; i < oldHead.getSize(); i++) {
            add(newHead, get(oldHead, i));
        }

        return result;
    }

    /**
     * Удаляет все элементы из этого массива.
     * Массив будет пуст после возврата этого вызова.
     */
    @Override
    public void clear() {
        HeadArray<E> head = new HeadArray<>();
        undo.push(head);
        redo.clear();
    }

    private Pair<Node<E>, Integer> copyLeafToChange(HeadArray<E> head, int index) {
        HeadArray<E> newHead = new HeadArray<>(head, 0);
        undo.push(newHead);
        redo.clear();

        Node<E> currentNode = newHead.getRoot();
        for (int level = bitPerNode * (depth - 1); level > 0; level -= bitPerNode) {
            int widthIndex = (index >> level) & mask;
            Node<E> tmp;
            Node<E> newNode;
            tmp = currentNode.getChild().get(widthIndex);
            newNode = new Node<>(tmp);
            currentNode.getChild().set(widthIndex, newNode);
            currentNode = newNode;
        }

        return new Pair<>(currentNode, index & mask);
    }

    private Pair<Node<E>, Integer> copyLeafToMove(HeadArray<E> oldHead, int index) {
        int level = bitPerNode * (depth - 1);
        HeadArray<E> newHead = new HeadArray<>(oldHead, index + 1, (index >> level) & mask);
        undo.push(newHead);
        redo.clear();
        Node<E> currentNode = newHead.getRoot();
        for (; level > 0; level -= bitPerNode) {
            int widthIndex = (index >> level) & mask;
            int widthIndexNext = (index >> (level - bitPerNode)) & mask;
            Node<E> tmp;
            Node<E> newNode;
            tmp = currentNode.getChild().get(widthIndex);
            newNode = new Node<>(tmp, widthIndexNext);
            currentNode.getChild().set(widthIndex, newNode);
            currentNode = newNode;
        }

        return new Pair<>(currentNode, index & mask);
    }

    /**
     * Возвращает элемент в указанной позиции в массиве.
     *
     * @param index индекс возвращаемого элемента
     * @return элемент в указанной позиции в массиве
     */
    @Override
    public E get(int index) {
        return get(getCurrentHead(), index);
    }

    private E get(HeadArray<E> head, int index) {
        checkIndex(head, index);
        return getLeaf(head, index).getValue().get(index & mask);
    }

    private Node<E> getLeaf(HeadArray<E> head, int index) {
        checkIndex(head, index);

        Node<E> node = head.getRoot();
        for (int level = bitPerNode * (depth - 1); level > 0; level -= bitPerNode) {
            int widthIndex = (index >> level) & mask;
            node = node.getChild().get(widthIndex);
        }

        return node;
    }

    public String drawGraph() {
        return getCurrentHead().getRoot().drawGraph();
    }

    /**
     * Возвращает строковое представление содержимого массива.
     * Строковое представление состоит из списка элементов массива, заключенного в квадратные скобки («[]»).
     * Смежные элементы разделяются символами «, » (запятая с последующим пробелом).
     *
     * @return строковое представление массива
     */
    @Override
    public String toString() {
        return toString(getCurrentHead());
    }

    private String toString(HeadArray<E> head) {
        return Arrays.toString(toArray(head));
    }

    /**
     * Возвращает массив, содержащий все элементы этого массива в правильной последовательности (от первого до последнего элемента).
     * <p>
     * Возвращенный массив будет "безопасным" в том смысле, что этот массив не поддерживает никаких ссылок на него.
     * (Другими словами, этот метод должен выделять новый массив, даже если эта коллекция поддерживается массивом).
     * Таким образом, вызывающий объект может изменять возвращаемый массив.
     * </p>
     *
     * @return массив, содержащий все элементы этого массива в правильной последовательности
     */
    @Override
    public Object[] toArray() {
        return toArray(getCurrentHead());
    }

    private Object[] toArray(HeadArray<E> head) {
        Object[] objects = new Object[head.getSize()];
        for (int i = 0; i < objects.length; i++) {
            objects[i] = this.get(head, i);
        }
        return objects;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return null;
    }

    @Override
    public boolean contains(Object o) {
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
    public boolean remove(Object o) {
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

    @Override
    public Iterator<E> iterator() {
        return new PersistentArrayIterator<>();
    }

    public class PersistentArrayIterator<T> implements java.util.Iterator<T> {
        int index = 0;

        /**
         * Возвращает true, если итерация содержит больше элементов.
         *
         * @return true, если итерация имеет больше элементов
         */
        @Override
        public boolean hasNext() {
            return index < size();
        }

        /**
         * Возвращает следующий элемент в итерации.
         *
         * @return следующий элемент в итерации
         */
        @Override
        @SuppressWarnings("unchecked")
        public T next() {
            return (T) get(index++);
        }

        @Override
        public void remove() {
        }
    }
}
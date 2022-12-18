package ru.nsu.fit.mpm.persistent_ds.list;

import javafx.util.Pair;
import ru.nsu.fit.mpm.persistent_ds.collection.AbstractPersistentCollection;
import ru.nsu.fit.mpm.persistent_ds.util.head.HeadList;
import ru.nsu.fit.mpm.persistent_ds.util.node.Node;

import java.util.*;

/**
 * Персистентный двусвязный список
 */
public class PersistentLinkedList<E> extends AbstractPersistentCollection implements List<E> {

    private static final String FULL_LIST_MESSAGE = "List is full";
    private static final String INVALID_INDEX_MESSAGE = "Invalid index";

    private PersistentLinkedList<PersistentLinkedList<?>> parent;
    private final Stack<PersistentLinkedList<?>> insertedUndo = new Stack<>();
    private final Stack<PersistentLinkedList<?>> insertedRedo = new Stack<>();
    protected final Stack<HeadList<PersistentLinkedListElement<E>>> redo = new Stack<>();
    protected final Stack<HeadList<PersistentLinkedListElement<E>>> undo = new Stack<>();

    public PersistentLinkedList() {
        this(6, 5);
    }

    public PersistentLinkedList(int maxSize) {
        this((int) Math.ceil(log(maxSize, (int) Math.pow(2, 5))), 5);
    }

    public PersistentLinkedList(int depth, int bitPerEdge) {
        super(depth, bitPerEdge);
        HeadList<PersistentLinkedListElement<E>> head = new HeadList<>();
        undo.push(head);
        redo.clear();
    }

    public PersistentLinkedList(PersistentLinkedList<E> other) {
        super(other.depth, other.bitPerEdge);
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
        if (value instanceof PersistentLinkedList) {
            ((PersistentLinkedList) value).parent = this;
        }

        if (parent != null) {
            parent.insertedUndo.push(this);
        }
    }

    /**
     * Возвращает количество элементов в списке.
     *
     * @return количество элементов в списке
     */
    @Override
    public int size() {
        return size(getCurrentHead());
    }

    public int size(HeadList<PersistentLinkedListElement<E>> head) {
        return head.getSize();
    }

    public HeadList<PersistentLinkedListElement<E>> getCurrentHead() {
        return this.undo.peek();
    }

    private void checkListIndex(int index) {
        checkListIndex(index, getCurrentHead());
    }

    private void checkListIndex(int index, HeadList<PersistentLinkedListElement<E>> head) {
        if ((index < 0) || (index >= head.getSize())) {
            throw new IndexOutOfBoundsException(INVALID_INDEX_MESSAGE);
        }
    }

    private void checkTreeIndex(int index, HeadList<PersistentLinkedListElement<E>> head) {
        if ((index < 0) || (index >= head.getSizeTree())) {
            throw new IndexOutOfBoundsException(INVALID_INDEX_MESSAGE + ": " + index);
        }
    }

    /**
     * Возвращает true, если список полон (size == maxSize).
     *
     * @return true, если список полон
     */
    public boolean isFull() {
        return isFull(getCurrentHead(), 0);
    }

    private boolean isFull(int extra) {
        return isFull(getCurrentHead(), extra);
    }

    private boolean isFull(HeadList<PersistentLinkedListElement<E>> head) {
        return isFull(head, 0);
    }

    private boolean isFull(HeadList<PersistentLinkedListElement<E>> head, int extra) {
        return head.getSizeTree() + extra >= maxSize;
    }

    /**
     * Возвращает true, если список не содержит элементов.
     *
     * @return true, если список не содержит элементов
     */
    @Override
    public boolean isEmpty() {
        return getCurrentHead().getSize() <= 0;
    }

    /**
     * Возвращает количество версий списка.
     *
     * @return количество версий списка
     */
    public int getVersionCount() {
        return undo.size() + redo.size();
    }

    /**
     * Заменяет элемент в указанной позиции этого списка указанным элементом.
     *
     * @param index   индекс замняемого элемента
     * @param element элемент, который будет сохранен в указанной позиции
     * @return заменяемый элемент
     */
    @Override
    public E set(int index, E element) {
        return set(getCurrentHead(), index, element);
    }

    private E set(HeadList<PersistentLinkedListElement<E>> head, int index, E element) {
        checkListIndex(index, head);

        E result = get(index);

        HeadList<PersistentLinkedListElement<E>> newHead = new HeadList<>(head, 0);
        Pair<Node<PersistentLinkedListElement<E>>, Integer> copedNodeP = copyLeaf(newHead, getTreeIndex(head, index));
        int leafIndex = copedNodeP.getValue();
        Node<PersistentLinkedListElement<E>> copedNode = copedNodeP.getKey();

        PersistentLinkedListElement<E> newNode = new PersistentLinkedListElement<>(copedNode.getValue().get(leafIndex));
        newNode.setValue(element);
        copedNode.getValue().set(leafIndex, newNode);

        undo.push(newHead);
        redo.clear();
        tryParentUndo(element);

        return result;
    }

    /**
     * Возвращает копию списка, в которой заменяет элемент в указанной позиции указанным элементом.
     *
     * @param index   индекс замняемого элемента
     * @param element элемент, который будет сохранен в указанной позиции
     * @return измененная копия списка
     */
    public PersistentLinkedList<E> assoc(int index, E element) {
        PersistentLinkedList<E> result = new PersistentLinkedList<>(this);
        result.set(index, element);
        return result;
    }

    /**
     * Добавление нового элмента в конец списка.
     *
     * @param element добавляемый элемент
     * @return true если список изменился в результате вызова
     */
    @Override
    public boolean add(E element) {
        if (isFull()) {
            return false;
        }

        PersistentLinkedListElement<E> listElement;
        HeadList<PersistentLinkedListElement<E>> prevHead = getCurrentHead();
        HeadList<PersistentLinkedListElement<E>> newHead;
        Pair<Integer, Boolean> next;

        if (getCurrentHead().getSize() == 0) {
            newHead = new HeadList<>();
            newHead.setFirst(0);
            newHead.setLast(0);
            listElement = new PersistentLinkedListElement<>(element, -1, -1);
            findLeafForNewElement(newHead).getValue().add(listElement);
        } else {
            listElement = new PersistentLinkedListElement<>(element, prevHead.getLast(), -1);

            newHead = new HeadList<>(prevHead, 0);
            Pair<Node<PersistentLinkedListElement<E>>, Integer> copedNodeP = copyLeaf(newHead, prevHead.getLast());
            int leafIndex = copedNodeP.getValue();
            Node<PersistentLinkedListElement<E>> copedNode = copedNodeP.getKey();

            PersistentLinkedListElement<E> last = new PersistentLinkedListElement<>(copedNode.getValue().get(leafIndex));
            copedNode.getValue().set(leafIndex, last);

            next = getNextIndex(newHead);
            if (Boolean.FALSE.equals(next.getValue())) {
                last.setNext(newHead.getSizeTree());
                newHead.setLast(newHead.getSizeTree());
            } else {
                last.setNext(next.getKey());
                PersistentLinkedListElement<E> oldElement = new PersistentLinkedListElement<>(
                        getValueFromLeaf(newHead, next.getKey()));
                Pair<Node<PersistentLinkedListElement<E>>, Integer> oldLeaf = getLeaf(newHead, next.getKey());
                oldLeaf.getKey().getValue().set(oldLeaf.getValue(), oldElement);

                oldElement.setValue(element);
                oldElement.setNext(-1);
                oldElement.setPrev(prevHead.getLast());
                newHead.setLast(last.getNext());
                newHead.setSize(newHead.getSize() + 1);
            }

            if (Boolean.FALSE.equals(next.getValue())) {
                findLeafForNewElement(newHead).getValue().add(listElement);
            }
        }

        undo.push(newHead);
        redo.clear();

        return true;
    }

    /**
     * Добавление нового элмента по идексу.
     * <p>
     * Вставляет указанный элемент в указанную позицию в этом списке (дополнительная операция).
     * Сдвигает элемент, находящийся в данный момент в этой позиции (если есть),
     * и любые последующие элементы вправо (добавляет единицу к их индексам).
     * </p>
     *
     * @param index   индекс, по которому указанный элемент должен быть вставлен
     * @param element элемент, который нужно вставить
     */
    @Override
    public void add(int index, E element) {
        if (isFull()) {
            throw new IllegalStateException(FULL_LIST_MESSAGE);
        }
        checkListIndex(index);

        HeadList<PersistentLinkedListElement<E>> prevHead = getCurrentHead();
        HeadList<PersistentLinkedListElement<E>> newHead = null;

        int indexBefore = -1;
        PersistentLinkedListElement<E> beforeElement;

        int indexAfter = -1;
        PersistentLinkedListElement<E> afterElement;

        int freeIndex = prevHead.getSizeTree();

        if (prevHead.getSize() == 0) {
            newHead = new HeadList<>(prevHead);
        } else {
            if (index != 0) {
                indexBefore = getTreeIndex(prevHead, index - 1);

                newHead = new HeadList<>(prevHead, 0);
                Pair<Node<PersistentLinkedListElement<E>>, Integer> copedNodeP = copyLeaf(newHead, indexBefore);
                int leafIndex = copedNodeP.getValue();
                Node<PersistentLinkedListElement<E>> copedNode = copedNodeP.getKey();

                beforeElement = new PersistentLinkedListElement<>(copedNode.getValue().get(leafIndex));
                beforeElement.setNext(freeIndex);
                copedNode.getValue().set(leafIndex, beforeElement);
            }

            if (index != prevHead.getSize() - 1) {
                indexAfter = getTreeIndex(prevHead, index);
                HeadList<PersistentLinkedListElement<E>> prevHead2 = newHead != null ? newHead : prevHead;

                newHead = new HeadList<>(prevHead2, 0);
                Pair<Node<PersistentLinkedListElement<E>>, Integer> copedNodeP = copyLeaf(newHead, indexAfter);
                int leafIndex = copedNodeP.getValue();
                Node<PersistentLinkedListElement<E>> copedNode = copedNodeP.getKey();

                afterElement = new PersistentLinkedListElement<>(copedNode.getValue().get(leafIndex));
                afterElement.setPrev(freeIndex);
                copedNode.getValue().set(leafIndex, afterElement);
            }
        }

        undo.push(newHead);
        redo.clear();
        tryParentUndo(element);

        PersistentLinkedListElement<E> listElement = new PersistentLinkedListElement<>(element, indexBefore, indexAfter);

        if (indexBefore == -1 && newHead != null) {
            newHead.setFirst(freeIndex);
        }

        if (indexAfter == -1 && newHead != null) {
            newHead.setLast(freeIndex);
        }

        findLeafForNewElement(newHead).getValue().add(listElement);
    }

    /**
     * Возвращает копию списка, в конец которой добавлен указанный элемент.
     *
     * @param element добавляемый элемент
     * @return измененная копия списка
     */
    public PersistentLinkedList<E> conj(E element) {
        PersistentLinkedList<E> result = new PersistentLinkedList<>(this);
        result.add(element);
        return result;
    }

    protected Node<PersistentLinkedListElement<E>> findLeafForNewElement(HeadList<PersistentLinkedListElement<E>> head) {
        if (isFull(head)) {
            throw new IllegalStateException(FULL_LIST_MESSAGE);
        }

        head.setSize(head.getSize() + 1);
        head.setSizeTree(head.getSizeTree() + 1);

        Node<PersistentLinkedListElement<E>> currentNode = head.getRoot();
        for (int level = bitPerEdge * (depth - 1); level > 0; level -= bitPerEdge) {
            int widthIndex = ((head.getSizeTree() - 1) >> level) & mask;

            Node<PersistentLinkedListElement<E>> tmp;
            Node<PersistentLinkedListElement<E>> newNode;
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
     * Удаляет элемент по указанному индексу.
     * <p>
     * Удаляет элемент в указанной позиции в этом списке.
     * Сдвигает любые последующие элементы влево (вычитает единицу из их индексов).
     * Возвращает элемент, который был удален из списка.
     * </p>
     *
     * @param index индекс удаляемого элемента
     * @return удаленный элемент
     */
    @Override
    public E remove(int index) {
        return remove(getCurrentHead(), index);
    }

    private E remove(HeadList<PersistentLinkedListElement<E>> prevHead, int index) {
        if (isFull(2)) {
            throw new IllegalStateException(FULL_LIST_MESSAGE);
        }

        HeadList<PersistentLinkedListElement<E>> newHead;
        checkListIndex(index, prevHead);
        E result = get(index);

        if (prevHead.getSize() == 1) {
            undo.push(new HeadList<>());
            redo.clear();
            return result;
        }

        int treeIndex = getTreeIndex(prevHead, index);
        PersistentLinkedListElement<E> mid = getLeaf(prevHead, treeIndex).getKey().getValue().get(treeIndex & mask);

        if (mid.getPrev() == -1) {
            int nextIndex = index + 1;
            int treeNextIndex = getTreeIndex(nextIndex);

            newHead = new HeadList<>(prevHead, 0);
            copyLeaf(newHead, nextIndex);

            PersistentLinkedListElement<E> nextPersistentLinkedListElement = getPersistentLinkedListElement(newHead, nextIndex);

            PersistentLinkedListElement<E> newNextPersistentLinkedListElement = new PersistentLinkedListElement<>(nextPersistentLinkedListElement);
            newNextPersistentLinkedListElement.setPrev(-1);

            Pair<Node<PersistentLinkedListElement<E>>, Integer> leafNext = getLeaf(newHead, treeNextIndex);
            leafNext.getKey().getValue().set(treeNextIndex & mask, newNextPersistentLinkedListElement);

            newHead.setFirst(treeNextIndex);

            finishRemove(newHead);
            return result;
        }

        if (mid.getNext() == -1) {
            int prevIndex = index - 1;
            int treePrevIndex = getTreeIndex(prevIndex);

            newHead = new HeadList<>(prevHead, 0);
            copyLeaf(newHead, prevIndex);

            PersistentLinkedListElement<E> prevPersistentLinkedListElement = getPersistentLinkedListElement(
                    newHead,
                    prevIndex);

            PersistentLinkedListElement<E> newPrevPersistentLinkedListElement = new PersistentLinkedListElement<>(
                    prevPersistentLinkedListElement);
            newPrevPersistentLinkedListElement.setNext(-1);

            Pair<Node<PersistentLinkedListElement<E>>, Integer> leafPrev = getLeaf(newHead, treePrevIndex);
            leafPrev.getKey().getValue().set(treePrevIndex & mask, newPrevPersistentLinkedListElement);

            newHead.setLast(treePrevIndex);

            finishRemove(newHead);
            return result;
        }

        int nextIndex = index + 1;
        int treeNextIndex = getTreeIndex(nextIndex);

        newHead = new HeadList<>(prevHead, 0);
        copyLeaf(newHead, nextIndex);

        PersistentLinkedListElement<E> nextPersistentLinkedListElement = getPersistentLinkedListElement(
                newHead,
                nextIndex);

        PersistentLinkedListElement<E> newNextPersistentLinkedListElement = new PersistentLinkedListElement<>(
                nextPersistentLinkedListElement);

        newNextPersistentLinkedListElement.setPrev(mid.getPrev());

        Pair<Node<PersistentLinkedListElement<E>>, Integer> leafNext = getLeaf(newHead, treeNextIndex);
        leafNext.getKey().getValue().set(treeNextIndex & mask, newNextPersistentLinkedListElement);

        int prevIndex = index - 1;
        int treePrevIndex = getTreeIndex(prevIndex);

        newHead = new HeadList<>(newHead, 0);
        copyLeaf(newHead, prevIndex);

        PersistentLinkedListElement<E> prevPersistentLinkedListElement = getPersistentLinkedListElement(newHead, prevIndex);

        PersistentLinkedListElement<E> newPrevPersistentLinkedListElement = new PersistentLinkedListElement<>(
                prevPersistentLinkedListElement);

        newPrevPersistentLinkedListElement.setNext(mid.getNext());

        Pair<Node<PersistentLinkedListElement<E>>, Integer> leafPrev = getLeaf(newHead, treePrevIndex);
        leafPrev.getKey().getValue().set(treePrevIndex & mask, newPrevPersistentLinkedListElement);

        if (newHead.getDeadList() == null) {
            newHead.setDeadList(new ArrayDeque<>());
        } else {
            newHead.setDeadList(new ArrayDeque<>(newHead.getDeadList()));
        }

        newHead.getDeadList().push(treeIndex);

        finishRemove(newHead);
        return result;
    }

    private void finishRemove(HeadList<PersistentLinkedListElement<E>> newHead) {
        newHead.setSize(newHead.getSize() - 1);
        undo.push(newHead);
        redo.clear();
    }

    /**
     * Удаляет все элементы из этого списка.
     * Список будет пуст после возврата этого вызова.
     */
    @Override
    public void clear() {
        HeadList<PersistentLinkedListElement<E>> head = new HeadList<>();
        undo.push(head);
        redo.clear();
    }

    private int getTreeIndex(int listIndex) {
        return getTreeIndex(getCurrentHead(), listIndex);
    }

    private int getTreeIndex(HeadList<PersistentLinkedListElement<E>> head, int listIndex) {
        checkListIndex(listIndex, head);

        if (head.getSize() == 0) {
            return -1;
        }

        int result = head.getFirst();
        PersistentLinkedListElement<E> current;

        for (int i = 0; i < listIndex; i++) {
            Pair<Node<PersistentLinkedListElement<E>>, Integer> pair = getLeaf(head, result);
            current = pair.getKey().getValue().get(pair.getValue());
            result = current.getNext();
        }

        return result;
    }

    /**
     * Возвращает элемент в указанной позиции в списке.
     *
     * @param index индекс возвращаемого элемента
     * @return элемент в указанной позиции в списке
     */
    @Override
    public E get(int index) {
        return get(getCurrentHead(), index);
    }

    private E get(HeadList<PersistentLinkedListElement<E>> head, int index) {
        if (index == 0) {
            return getValueFromLeaf(head, head.getFirst()).getValue();
        } else if (index == head.getSize() - 1) {
            return getValueFromLeaf(head, head.getLast()).getValue();
        } else {
            return getPersistentLinkedListElement(head, index).getValue();
        }
    }

    private PersistentLinkedListElement<E> getPersistentLinkedListElement(
            HeadList<PersistentLinkedListElement<E>> head, int index) {
        checkListIndex(index);
        int treeIndex = getTreeIndex(index);
        if (treeIndex == -1) {
            throw new IndexOutOfBoundsException("getTreeIndex == -1");
        }

        return getLeaf(head, treeIndex).getKey().getValue().get(treeIndex & mask);
    }

    private PersistentLinkedListElement<E> getValueFromLeaf(HeadList<PersistentLinkedListElement<E>> head, int index) {
        return getLeaf(head, index).getKey().getValue().get(index & mask);
    }

    private Pair<Node<PersistentLinkedListElement<E>>, Integer> getLeaf(
            HeadList<PersistentLinkedListElement<E>> head, int index) {
        checkTreeIndex(index, head);

        Node<PersistentLinkedListElement<E>> node = head.getRoot();
        for (int level = bitPerEdge * (depth - 1); level > 0; level -= bitPerEdge) {
            int widthIndex = (index >> level) & mask;
            node = node.getChild().get(widthIndex);
        }

        return new Pair<>(node, index & mask);
    }

    private Pair<Integer, Boolean> getNextIndex(HeadList<PersistentLinkedListElement<E>> head) {
        if (head.getDeadList() == null) {
            return new Pair<>(head.getSizeTree(), false);
        }

        if (head.getDeadList().size() == 0) {
            return new Pair<>(head.getSizeTree(), false);
        }

        head.setDeadList(new ArrayDeque<>(head.getDeadList()));
        return new Pair<>(head.getDeadList().pop(), true);
    }

    private Pair<Node<PersistentLinkedListElement<E>>, Integer> copyLeaf(HeadList<PersistentLinkedListElement<E>> newHead, int index) {
        Node<PersistentLinkedListElement<E>> currentNode = newHead.getRoot();
        for (int level = bitPerEdge * (depth - 1); level > 0; level -= bitPerEdge) {
            int widthIndex = (index >> level) & mask;
            Node<PersistentLinkedListElement<E>> tmp;
            Node<PersistentLinkedListElement<E>> newNode;
            tmp = currentNode.getChild().get(widthIndex);
            newNode = new Node<>(tmp);
            currentNode.getChild().set(widthIndex, newNode);
            currentNode = newNode;
        }

        return new Pair<>(currentNode, index & mask);
    }

    public int getUniqueLeafsSize() {
        LinkedList<Node<PersistentLinkedListElement<E>>> list = new LinkedList<>();
        getUniqueLeafsSize(list, undo);
        getUniqueLeafsSize(list, redo);

        return list.size();
    }

    private void getUniqueLeafsSize(LinkedList<Node<PersistentLinkedListElement<E>>> list,
                                    Stack<HeadList<PersistentLinkedListElement<E>>> undoVersions) {
        for (HeadList<PersistentLinkedListElement<E>> head : undoVersions) {
            for (int i = 0; i < head.getSize(); i++) {
                Node<PersistentLinkedListElement<E>> leaf = getLeaf(head, i).getKey();
                if (!list.contains(leaf)) {
                    list.add(leaf);
                }
            }
        }
    }

    /**
     * Возвращает строковое представление содержимого списка.
     * <p>
     * Строковое представление состоит из списка элементов этого персистентного списка, заключенного в квадратные скобки («[]»).
     * Смежные элементы разделяются символами «, » (запятая с последующим пробелом).
     *
     * @return строковое представление списка
     */
    @Override
    public String toString() {
        return toString(getCurrentHead());
    }

    private String toString(HeadList<PersistentLinkedListElement<E>> head) {
        if (head.getSize() == 0) {
            return "[]";
        } else {
            return Arrays.toString(toArray(head));
        }
    }

    /**
     * Возвращает массив, содержащий все элементы этого персистентного списка в правильной последовательности (от первого до последнего элемента).
     *
     * @return массив, содержащий все элементы этого персистентного списка в правильной последовательности
     */
    @Override
    public Object[] toArray() {
        return toArray(getCurrentHead());
    }

    private Object[] toArray(HeadList<PersistentLinkedListElement<E>> head) {
        Object[] objects = new Object[head.getSize()];
        Iterator<E> iterator = iterator(head);
        for (int i = 0; i < objects.length; i++) {
            objects[i] = iterator.next();
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
        return Collections.emptyList();
    }

    @Override
    public Iterator<E> iterator() {
        return new PersistentListIterator<>();
    }

    public Iterator<E> iterator(HeadList<PersistentLinkedListElement<E>> head) {
        return new PersistentListIterator<>(head);
    }

    /**
     * Итератор над персистентным списком.
     */
    public class PersistentListIterator<T> implements java.util.Iterator<T> {
        HeadList<PersistentLinkedListElement<E>> head;
        PersistentLinkedListElement<E> current;
        int i = 0;

        public PersistentListIterator(HeadList<PersistentLinkedListElement<E>> head) {
            this.head = head;
            if (head.getSize() == 0) {
                return;
            }

            Pair<Node<PersistentLinkedListElement<E>>, Integer> tmp = getLeaf(head, head.getFirst());
            current = tmp.getKey().getValue().get(tmp.getValue());
        }

        public PersistentListIterator() {
            this(getCurrentHead());
        }

        /**
         * Возвращает true, если итерация содержит больше элементов.
         *
         * @return true, если итерация имеет больше элементов
         */
        @Override
        public boolean hasNext() {
            return head.getSize() > i;
        }

        /**
         * Возвращает следующий элемент в итерации.
         *
         * @return следующий элемент в итерации
         */
        @Override
        public T next() {
            T result = (T) current.getValue();
            i++;
            if (!hasNext()) {
                return result;
            }
            Pair<Node<PersistentLinkedListElement<E>>, Integer> tmp = getLeaf(head, current.getNext());
            current = tmp.getKey().getValue().get(tmp.getValue());
            return result;
        }

        @Override
        public void remove() {
        }
    }
}
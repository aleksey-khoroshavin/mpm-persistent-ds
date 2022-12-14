package ru.nsu.fit.mpm.persistent_ds;

import javafx.util.Pair;

import java.util.*;

public class PersistentLinkedList<E> extends AbstractPersistentCollection<PersistentLinkedListElement<E>> implements List<E> {

    private PersistentLinkedList<PersistentLinkedList<?>> parent;
    private Stack<PersistentLinkedList<?>> insertedUndo = new Stack<>();
    private Stack<PersistentLinkedList<?>> insertedRedo = new Stack<>();
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
        if (!insertedUndo.empty()) {
            insertedUndo.peek().undo();
            insertedRedo.push(insertedUndo.pop());
        } else {
            if (!undo.empty()) {
                redo.push(undo.pop());
            }
        }
    }

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
                if (!list.contains(leaf)) {
                    list.add(leaf);
                }
            }
        }

    }

    private void tryParentUndo(E value) {
        if (value instanceof PersistentLinkedList) {
            ((PersistentLinkedList) value).parent = this;
        }

        if (parent != null) {
            parent.onEvent(this);
        }
    }

    private void onEvent(PersistentLinkedList<?> persistentLinkedList) {
        insertedUndo.push(persistentLinkedList);
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
        return new PersistentListIterator<>();
    }

    public Iterator<E> iterator(HeadList<PersistentLinkedListElement<E>> head) {
        return new PersistentListIterator<>(head);
    }

    @Override
    public String toString() {
        return toString(getCurrentHead());
    }

    private String toString(HeadList<PersistentLinkedListElement<E>> head) {
        if (head.size == 0) {
            return "[]";
        } else {
            return Arrays.toString(toArray(head));
        }
    }

    @Override
    public Object[] toArray() {
        return toArray(getCurrentHead());
    }

    private Object[] toArray(HeadList<PersistentLinkedListElement<E>> head) {
        Object[] objects = new Object[head.size];
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

    public void checkListIndex(int index) {
        checkListIndex(index, getCurrentHead());
    }

    public void checkListIndex(int index, HeadList<PersistentLinkedListElement<E>> head) {
        if (!((index >= 0) && (index < head.size))) {
            throw new IndexOutOfBoundsException("Invalid index: " + index);
        }
    }

    public void checkTreeIndex(int index, HeadList<PersistentLinkedListElement<E>> head) {
        if (!((index >= 0) && (index < head.sizeTree))) {
            throw new IndexOutOfBoundsException("Invalid index: " + index);
        }
    }

    public boolean isFull() {
        return isFull(getCurrentHead(), 0);
    }

    public boolean isFull(int extra) {
        return isFull(getCurrentHead(), extra);
    }

    public boolean isFull(HeadList<PersistentLinkedListElement<E>> head) {
        return isFull(head, 0);
    }

    public boolean isFull(HeadList<PersistentLinkedListElement<E>> head, int extra) {
        return head.sizeTree + extra >= maxSize;
    }

    private Pair<Integer, Boolean> getNextIndex(HeadList<PersistentLinkedListElement<E>> head) {
        if (head.deadList == null) {
            return new Pair<>(head.sizeTree, false);
        }

        if (head.deadList.size() == 0) {
            return new Pair<>(head.sizeTree, false);
        }

        head.deadList = new ArrayDeque<>(head.deadList);
        return new Pair<>(head.deadList.pop(), true);
    }

    @Override
    public boolean add(E newValue) {
        if (isFull()) {
            return false;
        }

        PersistentLinkedListElement<E> element;
        HeadList<PersistentLinkedListElement<E>> prevHead = getCurrentHead();
        HeadList<PersistentLinkedListElement<E>> newHead;
        Pair<Integer, Boolean> next;

        if (getCurrentHead().size == 0) {
            newHead = new HeadList<>();
            element = new PersistentLinkedListElement<>(newValue, -1, -1);
            newHead.first = 0;
            newHead.last = 0;

            findLeafForNewElement(newHead).value.add(element);
        } else {
            element = new PersistentLinkedListElement<>(newValue, prevHead.last, -1);
            CopyResult<PersistentLinkedListElement<E>, HeadList<PersistentLinkedListElement<E>>> tmp
                    = copyLeaf(prevHead, prevHead.last);
            newHead = tmp.head;
            next = getNextIndex(newHead);
            PersistentLinkedListElement<E> last = new PersistentLinkedListElement<>(tmp.leaf.value.get(tmp.leafInnerIndex));
            tmp.leaf.value.set(tmp.leafInnerIndex, last);

            if (!next.getValue()) {
                last.next = newHead.sizeTree;
                newHead.last = newHead.sizeTree;
            } else {
                last.next = next.getKey();
                PersistentLinkedListElement<E> oldOne = new PersistentLinkedListElement<>(getValueFromLeaf(newHead, next.getKey()));

                Pair<Node<PersistentLinkedListElement<E>>, Integer> oldLeaf = getLeaf(newHead, next.getKey());
                oldLeaf.getKey().value.set(oldLeaf.getValue(), oldOne);

                oldOne.value = newValue;
                oldOne.next = -1;
                oldOne.prev = prevHead.last;
                newHead.last = last.next;
                newHead.size++;
            }

            if (!next.getValue()) {
                findLeafForNewElement(newHead).value.add(element);
            }
        }

        undo.push(newHead);
        redo.clear();

        return true;
    }

    @Override
    public void add(int index, E value) {
        if (isFull()) {
            throw new IllegalStateException("array is full");
        }

        HeadList<PersistentLinkedListElement<E>> prevHead = getCurrentHead();
        HeadList<PersistentLinkedListElement<E>> newHead = null;

        checkListIndex(index, prevHead);

        int indexBefore = -1;
        PersistentLinkedListElement<E> beforeE = null;

        int indexAfter = -1;
        PersistentLinkedListElement<E> afterE = null;

        int freeIndex = prevHead.sizeTree;

        if (prevHead.size == 0) {
            newHead = new HeadList<>(prevHead);
        } else {
            if (index != 0) {
                indexBefore = getTreeIndex(index - 1);
                CopyResult<PersistentLinkedListElement<E>, HeadList<PersistentLinkedListElement<E>>> before = copyLeaf(prevHead, indexBefore);
                beforeE = new PersistentLinkedListElement<>(before.leaf.value.get(before.leafInnerIndex));
                beforeE.next = freeIndex;
                before.leaf.value.set(before.leafInnerIndex, beforeE);
                newHead = before.head;
            }

            if (index != prevHead.size - 1) {
                indexAfter = getTreeIndex(index);
                HeadList<PersistentLinkedListElement<E>> prevHead2 = newHead != null ? newHead : prevHead;
                CopyResult<PersistentLinkedListElement<E>, HeadList<PersistentLinkedListElement<E>>> after = copyLeaf(prevHead2, indexAfter);
                afterE = new PersistentLinkedListElement<>(after.leaf.value.get(after.leafInnerIndex));
                afterE.prev = freeIndex;
                after.leaf.value.set(after.leafInnerIndex, afterE);
                newHead = after.head;
            }
        }

        undo.push(newHead);
        redo.clear();
        tryParentUndo(value);

        PersistentLinkedListElement<E> element = new PersistentLinkedListElement<>(value, indexBefore, indexAfter);

        if (indexBefore == -1) {
            newHead.first = freeIndex;
        }

        if (indexAfter == -1) {
            newHead.last = freeIndex;
        }

        findLeafForNewElement(newHead).value.add(element);
    }

    private int getTreeIndex(int listIndex) {
        return getTreeIndex(getCurrentHead(), listIndex);
    }

    private int getTreeIndex(HeadList<PersistentLinkedListElement<E>> head, int listIndex) {
        checkListIndex(listIndex, head);

        if (head.size == 0) {
            return -1;
        }

        int result = head.first;
        PersistentLinkedListElement<E> current;

        for (int i = 0; i < listIndex; i++) {
            Pair<Node<PersistentLinkedListElement<E>>, Integer> pair = getLeaf(head, result);
            current = pair.getKey().value.get(pair.getValue());
            result = current.next;
        }

        return result;
    }

    public PersistentLinkedList<E> conj(E newElement) {
        PersistentLinkedList<E> result = new PersistentLinkedList<>(this);
        result.add(newElement);
        return result;
    }

    protected Node<PersistentLinkedListElement<E>> findLeafForNewElement(HeadList<PersistentLinkedListElement<E>> head) {
        if (isFull(head)) {
            throw new IndexOutOfBoundsException("collection is full");
        }

        head.size += 1;
        head.sizeTree += 1;

        Node<PersistentLinkedListElement<E>> currentNode = head.root;
        for (int level = bitPerNode * (depth - 1); level > 0; level -= bitPerNode) {
            int widthIndex = ((head.sizeTree - 1) >> level) & mask;

            Node<PersistentLinkedListElement<E>> tmp, newNode;
            if (currentNode.child == null) {
                currentNode.child = new LinkedList<>();
                newNode = new Node<>();
                currentNode.child.add(newNode);
            } else {
                if (widthIndex == currentNode.child.size()) {
                    newNode = new Node<>();
                    currentNode.child.add(newNode);
                } else {
                    tmp = currentNode.child.get(widthIndex);
                    newNode = new Node<>(tmp);
                    currentNode.child.set(widthIndex, newNode);
                }
            }

            currentNode = newNode;
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
        HeadList<PersistentLinkedListElement<E>> head = new HeadList<>();
        undo.push(head);
        redo.clear();
    }

    private PersistentLinkedListElement<E> getValueFromLeaf(HeadList<PersistentLinkedListElement<E>> head, int index) {
        return getLeaf(head, index).getKey().value.get(index & mask);
    }

    @Override
    public E get(int index) {
        return get(getCurrentHead(), index);
    }

    private PersistentLinkedListElement<E> getPersistentLinkedListElement(HeadList<PersistentLinkedListElement<E>> head, int index) {
        checkListIndex(index);
        int treeIndex = getTreeIndex(index);
        if (treeIndex == -1) {
            throw new IndexOutOfBoundsException("getTreeIndex == -1");
        }

        return getLeaf(head, treeIndex).getKey().value.get(treeIndex & mask);
    }

    private E get(HeadList<PersistentLinkedListElement<E>> head, int index) {
        if (index == 0) {
            return getValueFromLeaf(head, head.first).value;
        }

        if (index == head.size - 1) {
            return getValueFromLeaf(head, head.last).value;
        }

        return getPersistentLinkedListElement(head, index).value;
    }

    protected Pair<Node<PersistentLinkedListElement<E>>, Integer> getLeaf(HeadList<PersistentLinkedListElement<E>> head, int index) {
        checkTreeIndex(index, head);

        Node<PersistentLinkedListElement<E>> node = head.root;
        for (int level = bitPerNode * (depth - 1); level > 0; level -= bitPerNode) {
            int widthIndex = (index >> level) & mask;
            node = node.child.get(widthIndex);
        }

        return new Pair<>(node, index & mask);
    }

    private CopyResult<PersistentLinkedListElement<E>, HeadList<PersistentLinkedListElement<E>>> copyLeaf(HeadList<PersistentLinkedListElement<E>> head, int index) {
        if (isFull()) {
            throw new IllegalStateException("array is full");
        }
        checkTreeIndex(index, head);

        HeadList<PersistentLinkedListElement<E>> newHead = new HeadList<>(head, 0);
        Node<PersistentLinkedListElement<E>> currentNode = newHead.root;
        for (int level = bitPerNode * (depth - 1); level > 0; level -= bitPerNode) {
            int widthIndex = (index >> level) & mask;
            Node<PersistentLinkedListElement<E>> tmp, newNode;
            tmp = currentNode.child.get(widthIndex);
            newNode = new Node<>(tmp);
            currentNode.child.set(widthIndex, newNode);
            currentNode = newNode;
        }

        return new CopyResult<>(currentNode, index & mask, newHead);
    }

    public int getVersionCount() {
        return undo.size() + redo.size();
    }

    public PersistentLinkedList<E> assoc(int index, E element) {
        PersistentLinkedList<E> result = new PersistentLinkedList<>(this);
        result.set(index, element);
        return result;
    }

    @Override
    public E set(int index, E element) {
        return set(getCurrentHead(), index, element);
    }

    private E set(HeadList<PersistentLinkedListElement<E>> prevHead, int index, E element) {
        E oldResult = get(index);
        checkListIndex(index, prevHead);
        CopyResult<PersistentLinkedListElement<E>, HeadList<PersistentLinkedListElement<E>>> copyResult
                = copyLeaf(prevHead, getTreeIndex(prevHead, index));
        HeadList<PersistentLinkedListElement<E>> newHead = copyResult.head;
        PersistentLinkedListElement<E> newNode = new PersistentLinkedListElement<>(copyResult.leaf.value.get(copyResult.leafInnerIndex));
        newNode.value = element;
        copyResult.leaf.value.set(copyResult.leafInnerIndex, newNode);

        undo.push(newHead);
        redo.clear();
        tryParentUndo(element);

        return oldResult;
    }

    @Override
    public E remove(int index) {
        return remove(getCurrentHead(), index);
    }

    private E remove(HeadList<PersistentLinkedListElement<E>> prevHead, int index) {
        if (isFull(2)) {
            throw new IllegalStateException("array is full");
        }

        HeadList<PersistentLinkedListElement<E>> newHead = null;
        checkListIndex(index, prevHead);
        E result = get(index);

        if (prevHead.size == 1) {
            undo.push(new HeadList<>());
            redo.clear();
            return result;
        }

        int treeIndex = getTreeIndex(prevHead, index);
        PersistentLinkedListElement<E> mid = getLeaf(prevHead, treeIndex).getKey().value.get(treeIndex & mask);

        if (mid.prev == -1) {
            int nextIndex = index + 1;
            int treeNextIndex = getTreeIndex(nextIndex);

            newHead = copyLeaf(prevHead, nextIndex).head;

            PersistentLinkedListElement<E> nextPersistentLinkedListElement = getPersistentLinkedListElement(newHead, nextIndex);
            PersistentLinkedListElement<E> newNextPersistentLinkedListElement = new PersistentLinkedListElement<>(nextPersistentLinkedListElement);
            newNextPersistentLinkedListElement.prev = -1;

            Pair<Node<PersistentLinkedListElement<E>>, Integer> leafNext = getLeaf(newHead, treeNextIndex);
            leafNext.getKey().value.set(treeNextIndex & mask, newNextPersistentLinkedListElement);

            newHead.first = treeNextIndex;

            finishRemove(newHead);
            return result;
        }

        if (mid.next == -1) {
            int prevIndex = index - 1;
            int treePrevIndex = getTreeIndex(prevIndex);

            newHead = copyLeaf(prevHead, prevIndex).head;

            PersistentLinkedListElement<E> prevPersistentLinkedListElement = getPersistentLinkedListElement(newHead, prevIndex);
            PersistentLinkedListElement<E> newPrevPersistentLinkedListElement = new PersistentLinkedListElement<>(prevPersistentLinkedListElement);
            newPrevPersistentLinkedListElement.next = -1;

            Pair<Node<PersistentLinkedListElement<E>>, Integer> leafPrev = getLeaf(newHead, treePrevIndex);
            leafPrev.getKey().value.set(treePrevIndex & mask, newPrevPersistentLinkedListElement);

            newHead.last = treePrevIndex;

            finishRemove(newHead);
            return result;
        }

        int nextIndex = index + 1;
        int treeNextIndex = getTreeIndex(nextIndex);

        newHead = copyLeaf(prevHead, nextIndex).head;

        PersistentLinkedListElement<E> nextPersistentLinkedListElement = getPersistentLinkedListElement(newHead, nextIndex);
        PersistentLinkedListElement<E> newNextPersistentLinkedListElement = new PersistentLinkedListElement<>(nextPersistentLinkedListElement);
        newNextPersistentLinkedListElement.prev = mid.prev;

        Pair<Node<PersistentLinkedListElement<E>>, Integer> leafNext = getLeaf(newHead, treeNextIndex);
        leafNext.getKey().value.set(treeNextIndex & mask, newNextPersistentLinkedListElement);

        int prevIndex = index - 1;
        int treePrevIndex = getTreeIndex(prevIndex);

        newHead = copyLeaf(newHead, prevIndex).head;

        PersistentLinkedListElement<E> prevPersistentLinkedListElement = getPersistentLinkedListElement(newHead, prevIndex);
        PersistentLinkedListElement<E> newPrevPersistentLinkedListElement = new PersistentLinkedListElement<>(prevPersistentLinkedListElement);
        newPrevPersistentLinkedListElement.next = mid.next;

        Pair<Node<PersistentLinkedListElement<E>>, Integer> leafPrev = getLeaf(newHead, treePrevIndex);
        leafPrev.getKey().value.set(treePrevIndex & mask, newPrevPersistentLinkedListElement);

        if (newHead.deadList == null) {
            newHead.deadList = new ArrayDeque<>();
        } else {
            newHead.deadList = new ArrayDeque<>(newHead.deadList);
        }

        newHead.deadList.push(treeIndex);

        finishRemove(newHead);
        return result;
    }

    private void finishRemove(HeadList<PersistentLinkedListElement<E>> newHead) {
        newHead.size--;
        undo.push(newHead);
        redo.clear();
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

    public class PersistentListIterator<E2> implements java.util.Iterator<E2> {
        PersistentLinkedListElement<E> current;
        HeadList<PersistentLinkedListElement<E>> head;
        int i = 0;

        public PersistentListIterator(HeadList<PersistentLinkedListElement<E>> head) {
            this.head = head;
            if (head.size == 0) {
                return;
            }

            Pair<Node<PersistentLinkedListElement<E>>, Integer> tmp = getLeaf(head, head.first);
            current = tmp.getKey().value.get(tmp.getValue());
        }

        public PersistentListIterator() {
            this(getCurrentHead());
        }

        @Override
        public boolean hasNext() {
            return head.size > i;
        }

        @Override
        @SuppressWarnings("unchecked")
        public E2 next() {
            E2 result = (E2) current.value;
            i++;
            if (!hasNext()) {
                return result;
            }
            Pair<Node<PersistentLinkedListElement<E>>, Integer> tmp = getLeaf(head, current.next);
            current = tmp.getKey().value.get(tmp.getValue());
            return result;
        }

        @Override
        public void remove() {
        }
    }
}
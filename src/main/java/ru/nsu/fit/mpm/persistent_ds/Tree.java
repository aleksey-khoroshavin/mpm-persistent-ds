package ru.nsu.fit.mpm.persistent_ds;
import java.util.ArrayList;

public class Tree<E> {

    private Node<E> root;
    private int height = 3;

    public Tree() {
        root = new Node<>();
        root.setChildren(new ArrayList<>(4));
    }

}
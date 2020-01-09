package com.leelovejava.operation.structures.redblock;

/**
 * 节点
 *
 * @author leelovejava
 */
public class RBTreeNode {
    private final boolean RED = false;
    private final boolean BLACK = true;
    private int key;
    private boolean color;
    private RBTreeNode left;
    private RBTreeNode right;
    private RBTreeNode parent;

    public RBTreeNode(int key) {
        this.key = key;
        this.color = RED;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public boolean getColor() {
        return color;
    }

    public void setColor(boolean color) {
        this.color = color;
    }

    public RBTreeNode getLeft() {
        return left;
    }

    public void setLeft(RBTreeNode left) {
        this.left = left;
    }

    public RBTreeNode getRight() {
        return right;
    }

    public void setRight(RBTreeNode right) {
        this.right = right;
    }

    public RBTreeNode getParent() {
        return parent;
    }

    public void setParent(RBTreeNode parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        return "RBTreeNode{" +
                ",key=" + key +
                ", color=" + color +
                '}';
    }
}
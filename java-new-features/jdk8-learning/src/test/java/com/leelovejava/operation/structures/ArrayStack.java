package com.leelovejava.operation.structures;

/**
 * 栈的主要机制可以用数组来实现，也可以用链表来实现，下面用数组来实现栈的基本操作
 * 数据项入栈和出栈的时间复杂度均为O(1)。这也就是说，栈操作所消耗的时间不依赖于栈中数据项的个数，因此操作时间很短。栈不需要比较和移动操作
 */
class ArrayStack {

    private long[] a;

    private int size;

    /**
     * 栈数组的大小
     */
    private int top;

    /**
     * 栈顶
     *
     * @param maxSize
     */
    public ArrayStack(int maxSize) {
        this.size = maxSize;
        this.a = new long[size];
        this.top = -1;
        //表示空栈
    }

    /**
     * 入栈
     *
     * @param value
     */
    public void push(long value) {
        if (isFull()) {
            System.out.println("栈已满！");
            return;
        }
        a[++top] = value;

    }

    public long peek() {
        // 返回栈顶内容，但不删除
        if (isEmpty()) {
            System.out.println("栈中没有数据");
            return 0;
        }
        return a[top];
    }

    public long pop() {
        // 弹出栈顶内容，删除
        if (isEmpty()) {
            System.out.println("栈中没有数据！");
            return 0;

        }

        return a[top--];
    }

    public int size() {
        return top + 1;

    }

    public boolean isEmpty() {
        return (top == -1);
    }

    public boolean isFull() {
        return (top == size - 1);
    }

    public void display() {
        for (int i = top; i >= 0; i--) {
            System.out.print(a[i] + " ");
        }
        System.out.println("");
    }
}
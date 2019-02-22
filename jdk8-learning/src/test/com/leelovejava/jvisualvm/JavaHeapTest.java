package com.leelovejava.jvisualvm;

/**
 * VisualVM 内存堆Heap
 * @author tianhao
 */
public class JavaHeapTest {
    public final static int OUTOFMEMORY = 200000000;
    
    private String oom;

    private int length;
    
    StringBuffer tempOOM = new StringBuffer();

    public JavaHeapTest(int leng) {
        this.length = leng;
       
        int i = 0;
        while (i < leng) {
            i++;
            try {
                tempOOM.append("a");
            } catch (OutOfMemoryError e) {
               e.printStackTrace();
               break;
            }
        }
        this.oom = tempOOM.toString();

    }

    public String getOom() {
        return oom;
    }

    public int getLength() {
        return length;
    }

    public static void main(String[] args) {
        JavaHeapTest javaHeapTest = new JavaHeapTest(OUTOFMEMORY);
        System.out.println(javaHeapTest.getOom().length());

        while (true) {
            System.out.println(1);
        }
    }

}
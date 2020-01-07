package com.leelovejava.interview.sort;

/**
 * 十大排序算法
 * 文档 十大经典排序算法(java实现+动画) https://blog.csdn.net/meibenxiang/article/details/92796909
 *
 * @author y.x
 * @date 2019/12/21
 */
public class SortTest {
    /**
     * 冒泡排序
     *
     * @param array
     */
    private static int[] bubbleSort(int[] array) {
        int temp;
        // 第一个 for 循环控制排序要走多少趟， 最多做 n-1 趟排序
        for (int i = 0; i < array.length - 1; i++) {
            // 第 2 个 for 循环控制每趟比较多少次
            for (int j = 0; j < array.length - 1 - i; j++) {
                if (array[j + 1] < array[j]) {
                    temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
        }
        return array;
    }

    public static void main(String[] args) {
        int[] array = new int[]{3, 2, 1, 3};
        int[] array2 = bubbleSort(array);
        for (int i = 0; i < array2.length; i++) {
            System.out.println(array2[i]);
        }
    }

    /**
     * 二分查找
     *
     * @param srcArray
     * @param des
     * @return
     */
    public static int binarySearch(int[] srcArray, int des) {
        int low = 0;
        int height = srcArray.length - 1;
        while (low <= height) {
            int middle = (low + height) / 2;
            if (des == srcArray[middle]) {
                return middle;
            } else if (des < srcArray[middle]) {
                height = middle - 1;
            } else {
                low = middle + 1;
            }
        }
        return -1;
    }

    /**
     * 递归方式 二分查找
     *
     * @param dataset
     * @param data
     * @param beginIndex
     * @param endIndex
     * @return
     */
    public static int binarySearch(int[] dataset, int data, int beginIndex, int endIndex) {
        int midIndex = (beginIndex + endIndex) / 2;
        if (data < dataset[beginIndex] || data > dataset[endIndex] || beginIndex > endIndex) {
            return -1;
        }
        if
        (data < dataset[midIndex]) {
            return binarySearch(dataset, data, beginIndex, midIndex - 1);
        } else if (data > dataset[midIndex]) {
            return binarySearch(dataset, data, midIndex + 1, endIndex);
        } else {
            return midIndex;
        }
    }

    /**
     * 插入排序：
     * 初始时假设第一个记录自成一个有序序列， 其余记录为无序序列。 接着从第二个记录开
     * 始， 按照记录的大小依次将当前处理的记录插入到其之前的有序序列中， 直至最后一个记
     * 录插入到有序序列中为止
     *
     * @param a
     */
    public static void insertSort(int[] a) {
        int temp;
        for (int i = 1; i < a.length; i++) {
            for (int j = i; j > 0; j--) {
                if (a[j - 1] > a[j]) {
                    temp = a[j - 1];
                    a[j - 1] = a[j];
                    a[j] = temp;
                }
            }
        }
    }

    /**
     * 选择排序
     * 把最小或者最大的选择出来对于给定的一组记录， 经过第一轮比较后得到最小的记录， 然后将该记录与第一个记录的
     * 位置进行交换； 接着对不包括第一个记录以外的其他记录进行第二轮比较， 得到最小的记
     * 录并与第二个记录进行位置交换； 重复该过程， 直到进行比较的记录只有一个时为止
     *
     * @param a
     */
    public static void selectSort(int[] a) {
        // 记住需要判断输入的数据
        if (a == null || a.length <= 0) {
            return;
        }
        for (int i = 0; i < a.length; i++) {
            int min = i;
            for (int j = i + 1; j < a.length; j++) {
                if (a[j] < a[min]) {
                    min = j;
                }
            }
            if (i != min) {
                int tmp = a[min];
                a[min] = a[i];
                a[i] = tmp;
            }
        }
    }


    /**
     * 快速排序：
     * 基于分治的思想， 是冒泡排序的改进型。 首先在数组中选择一个基准点（该基准点的
     * 选取可能影响快速排序的效率， 后面讲解选取的方法） ， 然后分别从数组的两端扫描
     * 数组， 设两个指示标志（lo 指向起始位置， hi 指向末尾)， 首先从后半部分开始， 如果
     * 发现有元素比该基准点的值小， 就交换 lo 和 hi 位置的值， 然后从前半部分开始扫秒，
     * 发现有元素大于基准点的值， 就交换 lo 和 hi 位置的值， 如此往复循环， 直到 lo>=hi,然
     * 后把基准点的值放到 hi 这个位置。 一次排序就完成了。 以后采用递归的方式分别对前
     * 半部分和后半部分排序， 当前半部分和后半部分均有序时该数组就自然有序了
     *
     * @param array
     * @param lo
     * @param hi
     * @return
     */
    public static int partition(int[] array, int lo, int hi) {
        //固定的切分方式， 将 lo 下标对应的点标 记 为 基 准 点
        int key = array[lo];
        while (lo < hi) {
            //从后半部分向前扫描
            while (array[hi] >= key && hi > lo) {
                hi--;
            }
            array[lo] = array[hi];
            while (array[lo] <= key && hi > lo) {
                // 从前半部分向后扫描
                lo++;
            }
            array[hi] = array[lo];
        }
        array[hi] = key;
        return hi;
    }

    /**
     * 堆排序
     * 基本思想:
     * 堆的概念：
     * n个元素的序列{k1，k2，…,kn}当且仅当满足下列关系之一时，称之为堆。
     * 　　情形1：ki <= k2i 且ki <= k2i+1 （最小堆）
     * 　　情形2：ki >= k2i 且ki >= k2i+1 （最大堆）
     * 　　其中i=1,2,…,n/2向下取整;
     * 堆排序：
     * 把待排序的序列看作是一棵顺序存储的二叉树，调整它们的存储顺序，使之成为一个最大堆，这时堆的根节点数最大。然后，将根节点与堆的最后一个节点交换，并对前面n-1个数重新调整使之成为堆，依此类推，最后得到有n个节点的有序序列。
     * 从算法描述来看，堆排序需要两个过程，一是建立堆，二是堆结果。
     * 说明：若想得到升序序列，则建立最大堆，若想得到降序序列，则建立最小堆。
     *
     * @param element
     */
    public static void heapSort(int[] element) {
        // step1:建堆
        int length = element.length;
        for (int i = length / 2 - 1; i >= 0; i--) {
            adjustHeap(element, i, length - 1);
        }
        // step2:交换位置,调整堆结构
        int tmp;
        for (int j = length - 1; j >= 0; j--) {
            tmp = element[j];
            element[j] = element[0];
            element[0] = tmp;
            adjustHeap(element, 0, j - 1);
        }
    }

    public static void adjustHeap(int[] element, int start, int end) {
        int tmp = element[start];
        for (int i = 2 * start + 1; i <= end; i = 2 * i + 1) {
            //定位父节点的左右孩子值较大的节点
            if (i < end && element[i] < element[i + 1]) {
                i++;
            }
            //父节点比左右孩子值都大,则跳出循环
            if (tmp > element[i]) {
                break;
            }
            //进行下一轮的筛选
            element[start] = element[i];
            start = i;
        }
        element[start] = tmp;
    }

    /**
     * 归并排序
     * 是把待排序序列分为若干个子序列，每个子序列是有序的，然后再把有序子序列合并为整体有序序列。
     *
     * @param element
     * @param left
     * @param right
     */
    public static void mergeSort(int[] element, int left, int right) {
        if (left < right) {
            int mid = (left + right) / 2;
            //左边进行递归排序
            mergeSort(element, left, mid);
            //右边进行递归排序
            mergeSort(element, mid + 1, right);
            //左右两部分进行合并处理
            merge(element, left, mid, right);
        }
    }


    public static void merge(int[] element, int left, int middle, int right) {
        int[] tmpElement = new int[element.length];
        int index = left;
        int mid = middle + 1;
        int tmpIndex = left;
        while (left <= middle && mid <= right) {
            if (element[left] < element[mid]) {
                tmpElement[index++] = element[left++];
            } else {
                tmpElement[index++] = element[mid++];
            }
        }
        while (left <= middle) {
            tmpElement[index++] = element[left++];
        }
        while (mid <= right) {
            tmpElement[index++] = element[mid++];
        }

        while (tmpIndex <= right) {
            element[tmpIndex] = tmpElement[tmpIndex++];
        }
    }

    public static void sort(int[] array, int lo, int hi) {
        if (lo >= hi) {
            return;
        }
        int index = partition(array, lo, hi);
        sort(array, lo, index - 1);
        sort(array, index + 1, hi);
    }
}

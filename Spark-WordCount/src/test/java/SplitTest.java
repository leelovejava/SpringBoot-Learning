import java.util.ArrayList;
import java.util.List;

public class SplitTest {
    public static void main(String[] args) {
        List<Object> list = new ArrayList<Object>();
        int size = 500;
        for (int i = 0; i < size; i++) {
            list.add(new Object());
        }

        System.out.println(splitList(list, splitSize(list.size()), 1).size());
    }

    public static int splitSize(int size) {
        if (size <= 50) {
            return 1;
        } else if (size > 50 && size <= 500) {
            return size % 50 == 0 ? size / 50 : (size / 50) + 1;
        } else if (size > 500) {
            return size % 500 == 0 ? size / 500 : (size / 500) + 1;
        }
        return 0;
    }

    public static List<Object> splitList(List<Object> list, int maxPage, int currenPage) {
        if (currenPage > maxPage) {
            return null;
        }

        int size = list.size();
        int startIndex;
        int endIndex;
        // 静态库人员的数据,分批处理,处理规则:1-500是50一次,500以上200一次
        if (size <= BatchImportListConstant.SPLIT_MIN_SIZE) {
            return list;
        } else if (size > BatchImportListConstant.SPLIT_MIN_SIZE && size < BatchImportListConstant.SPLIT_MAX_SIZE) {
            startIndex = currenPage == 1 ? 0 : 50 * (currenPage - 1);
            endIndex = currenPage == maxPage ? size : 50 * currenPage;
            if (endIndex > size) {
                endIndex = size;
            }
            return list.subList(startIndex, endIndex);
        } else if (size >= BatchImportListConstant.SPLIT_MAX_SIZE) {
            startIndex = currenPage == 1 ? 0 : 200 * (currenPage - 1);
            endIndex = (currenPage == maxPage ? size : 200 * currenPage);
            System.out.println("endIndex:" + endIndex + ",startIndex:" + startIndex + ",maxPage:" + maxPage);
            endIndex=600;
            size=500;
            if (endIndex > size) {
                endIndex = size;
            }
            return list.subList(startIndex, endIndex);
        }
        return null;
    }
}

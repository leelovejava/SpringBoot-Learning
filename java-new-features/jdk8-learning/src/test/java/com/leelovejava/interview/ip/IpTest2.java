package com.leelovejava.interview.ip;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 海量日志数据提取某日访问百度次数最多的那个IP
 *
 * @author y.x
 * @date 2019/12/21
 * @see 'https://blog.csdn.net/linmiansheng/article/details/19290879'
 */
public class IpTest2 {

    public static void main(String[] args) {
        // -Xms100m -Xmx100m -Xmn80m -Xss80m

        // 1) 生成
        /// IpTest2.generateIpsFile();

        // 2) 拆分
        /// IpTest2.divideIpsFile();

        // 3) 计算
        IpTest2.calculate();
    }

    /**
     * 生成IP的文件地址
     */
    private static String FILE_PATH = "D:\\study\\ip\\1.txt";
    /**
     * 拆分IP的文件夹
     */
    private static String FOLDER = "D:\\study\\ip\\2";
    /**
     * 生成的IP个数
     */
    private static Integer MAX_NUM = 100000000;
    /**
     * hash个数
     */
    private static Integer HASH_NUM = 1024;

    /**
     * 生成ip
     * 1）首先，生成1亿条数据，为了产生更多的重复ip，前面两节就不变了，只随机生成后面的2节
     *
     * @return
     */
    private static String generateIp() {
        return "192.168." + (int) (Math.random() * 255) + "."
                + (int) (Math.random() * 255) + "\n";
    }

    /**
     * 生成ip文件
     */
    private static void generateIpsFile() {
        File file = new File(FILE_PATH);
        try (FileWriter fileWriter = new FileWriter(file)) {

            for (int i = 0; i < MAX_NUM; i++) {
                fileWriter.write(generateIp());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 取模函数
     *
     * @param ip
     * @return
     */
    private static String hash(String ip) {
        return String.valueOf(ipToLong(ip) % HASH_NUM);
    }

    /**
     * 将字符串的ip转换成长整数
     * 1、通过String的split方法按.分隔得到4个长度的数组
     * 2、通过左移位操作（<<）给每一段的数字加权，第一段的权为2的24次方，第二段的权为2的16次方，第三段的权为2的8次方，最后一段的权为1
     *
     * @param strIp 字符串ip
     * @return
     */
    private static long ipToLong(String strIp) {
        String[] ip = strIp.split("\\.");
        return (Long.parseLong(ip[0]) << 24) + (Long.parseLong(ip[1]) << 16) + (Long.parseLong(ip[2]) << 8) + Long.parseLong(ip[3]);
    }

    /**
     * 将十进制整数形式转换成127.0.0.1形式的ip地址
     * 将整数形式的IP地址转化成字符串的方法如下：
     * 1、将整数值进行右移位操作（>>>），右移24位，右移时高位补0，得到的数字即为第一段IP。
     * 2、通过与操作符（&）将整数值的高8位设为0，再右移16位，得到的数字即为第二段IP。
     * 3、通过与操作符吧整数值的高16位设为0，再右移8位，得到的数字即为第三段IP。
     * 4、通过与操作符吧整数值的高24位设为0，得到的数字即为第四段IP。
     *
     * @param longIp
     * @return
     */
    public static String longToIP(long longIp) {
        StringBuffer sb = new StringBuffer();
        // 直接右移24位
        sb.append(longIp >>> 24);
        sb.append(".");
        // 将高8位置0，然后右移16位
        sb.append((longIp & 0x00FFFFFF) >>> 16);
        sb.append(".");
        // 将高16位置0，然后右移8位
        sb.append((longIp & 0x0000FFFF) >>> 8);
        sb.append(".");
        // 将高24位置0
        sb.append(longIp & 0x000000FF);
        return sb.toString();
    }

    /**
     * 拆分文件
     */
    private static void divideIpsFile() {
        File file = new File(FILE_PATH);
        Map<String, StringBuilder> map = new HashMap<>();
        int count = 0;
        try {
            if (!file.exists()) {
                file.mkdirs();
            }

            FileReader fileReader = new FileReader(file);
            BufferedReader br = new BufferedReader(fileReader);
            String ip;

            while ((ip = br.readLine()) != null) {
                String hashIp = hash(ip);
                if (map.containsKey(hashIp)) {
                    StringBuilder sb = map.get(hashIp);
                    sb.append(ip).append("\n");
                    map.put(hashIp, sb);
                } else {
                    StringBuilder sb = new StringBuilder(ip);
                    sb.append("\n");
                    map.put(hashIp, sb);
                }
                count++;
                if (count == 4000000) {
                    Iterator<String> it = map.keySet().iterator();
                    while (it.hasNext()) {
                        String fileName = it.next();
                        File ipFile = new File(FOLDER + "/" + fileName + ".txt");
                        if (!ipFile.exists()) {
                            ipFile.createNewFile();
                        }
                        FileWriter fileWriter = new FileWriter(ipFile, true);
                        StringBuilder sb = map.get(fileName);
                        fileWriter.write(sb.toString());
                        fileWriter.close();
                    }
                    count = 0;
                    map.clear();
                }
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 3）文件拆分了之后，接下来我们就要分别读取这1000个小文件，统计其中每个IP出现的次数。
     **/
    private static void calculate() {
        Map<String, Integer> map = new HashMap<>();
        File folder = new File(FOLDER);
        File[] files = folder.listFiles();
        FileReader fileReader;
        BufferedReader br;
        // 3.1）第一步要读取每个文件，将其中的ip放到一个Map中，然后调用count()方法，找出map中最大访问次数的ip，将ip和最多访问次数存到另外一个map中
        for (File file : files) {
            try {
                fileReader = new FileReader(file);
                br = new BufferedReader(fileReader);
                String ip;
                Map<String, Integer> tmpMap = new HashMap<>();
                while ((ip = br.readLine()) != null) {
                    if (tmpMap.containsKey(ip)) {
                        int count = tmpMap.get(ip);
                        tmpMap.put(ip, count + 1);
                    } else {
                        tmpMap.put(ip, 0);
                    }
                }
                fileReader.close();
                br.close();
                count(tmpMap, map);
                tmpMap.clear();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Map<String, Integer> finalMap = new HashMap<>();
        // 3.2）当1000个文件都读取完之后，我们就会产生一个有1000条记录的map，里面存储了每个文件中访问次数最多的ip，我们再调用count()方法，找出这个map中访问次数最大的ip,
        // 即这1000个文件中，哪个文件中的最高访问量的IP，才是真正最高的，好像小组赛到决赛一样。。。。
        count(map, finalMap);
        // 3.3）在这里没有用到什么堆排序和快速排序，因为只需要一个最大值，所以只要拿当前的最大值跟接下来的值判断就好，其实也相当跟只有一个元素的堆的堆顶元素比较
        Iterator<String> it = finalMap.keySet().iterator();
        while (it.hasNext()) {
            String ip = it.next();
            System.out.println("result IP : " + ip + " | count = " + finalMap.get(ip));
        }

    }

    /**
     * 找出map中最大访问次数的ip
     *
     * @param pMap      需计算的map
     * @param resultMap ip和最多访问次数的结果集
     */
    private static void count(Map<String, Integer> pMap, Map<String, Integer> resultMap) {
        Iterator<Map.Entry<String, Integer>> it = pMap.entrySet().iterator();
        int max = 0;
        String resultIp = "";
        while (it.hasNext()) {
            Map.Entry<String, Integer> entry = it.next();
            if (entry.getValue() > max) {
                max = entry.getValue();
                resultIp = entry.getKey();
            }
        }
        resultMap.put(resultIp, max);
    }

}

package com.leelovejava.interview.ip;

import java.io.*;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * 海量日志数据提取某日访问百度次数最多的那个IP
 *
 * @author y.x
 * @date 2019/12/21
 * @see 'https://blog.csdn.net/weixin_30664051/article/details/94984624'
 */
public class IpTest {

    /**
     * 首先是这一天，并且是访问百度的日志中的 IP 取出来，逐个写入到一个大文件中。注意到IP 是 32 位的，最多有个 2^32 个 IP。
     * 同样可以采用映射的方法， 比如模 1000，把整个大文件映射为 1000 个小文件，再找出每个小文中出现频率最大的 IP（可以采用 hash_map进行频率统计，然后再找出频率最大 的几个）及相应的频率。
     * 然后再在这 1000 个最大的IP 中，找出那个频率最大的 IP，即为所求。
     * 或者如下阐述（雪域之鹰）：
     * 算法思想：分而治之+Hash
     * （1）IP 地址最多有2^32=4G种取值情况，所以不能完全加载到内存中处理；
     * （2）可以考虑采用“分而治之”的思想，按照IP地址的Hash(IP)%1024值，把海量IP日志分别存储到1024个小文件中。这样，每个小文件最多包含4MB个IP地址；
     * （3）对于每一个小文件，可以构建一个IP为 key，出现次数为value的Hash map，同时记录当前出现次数最多的那个IP地址；
     * （4）可以得到1024个小文件中的出现次数最多的IP，再依据常规的排序算法得到总体上出现次数最多的IP
     *
     * @param args
     */
    public static void main(String[] args) {
        IpTest ipTest = new IpTest();
        // 生成ip文件
        ///ipTest.gernBigFile(new File("D:\\study\\1.txt"), 100000000);
        ipTest.splitFile4(new File("D:\\study\\ip\\1.txt"), "D:\\study\\ip\\", 100000000);
    }

    /**
     * 生成大文件
     *
     * @param ipFile
     * @param numberOfLine 生成ip个数
     */
    public void gernBigFile(File ipFile, long numberOfLine) {

        long startTime = System.currentTimeMillis();
        try (FileWriter fw = new FileWriter(ipFile, true);
             BufferedWriter bw = new BufferedWriter(fw)) {


            SecureRandom random = new SecureRandom();
            for (int i = 0; i < numberOfLine; i++) {
                bw.write("10." + random.nextInt(255) + "." + random.nextInt(255) + "." + random.nextInt(255) + "\n");
                if ((i + 1) % 1000 == 0) {
                    bw.flush();
                }
            }
            bw.flush();

            System.err.println(System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 大文件分割为小文件
     *
     * @param ipFile
     * @param directoryPath 小文件的目录 e:/tmp/ip1/
     * @param numberOfFile
     */
    public void splitFile4(File ipFile, String directoryPath, int numberOfFile) {
        Map<Integer, BufferedWriter> bwMap = new HashMap<>();
        Map<Integer, List<String>> dataMap = new HashMap<>();


        BufferedWriter bw = null;
        FileWriter fw = null;
        long startTime = System.currentTimeMillis();
        try (FileReader fr = new FileReader(ipFile);
             BufferedReader br = new BufferedReader(fr)) {

            String ipLine = br.readLine();
            // 先创建文件及流对象方便使用
            for (int i = 0; i < numberOfFile; i++) {
                File file = new File(directoryPath + i + ".txt");
                bwMap.put(i, new BufferedWriter(new FileWriter(file, true)));
                dataMap.put(i, new LinkedList<String>());
            }
            while (ipLine != null) {
                int hashCode = ipLine.hashCode();
                hashCode = hashCode < 0 ? -hashCode : hashCode;
                int fileNum = hashCode % numberOfFile;
                List<String> list = dataMap.get(fileNum);
                list.add(ipLine + "\n");
                if (list.size() % 1000 == 0) {
                    BufferedWriter writer = bwMap.get(fileNum);
                    for (String line : list) {
                        writer.write(line);
                    }
                    writer.flush();
                    list.clear();
                }
                ipLine = br.readLine();
            }
            for (int fn : bwMap.keySet()) {
                List<String> list = dataMap.get(fn);
                BufferedWriter writer = bwMap.get(fn);
                for (String line : list) {
                    writer.write(line);
                }
                list.clear();
                writer.flush();
                writer.close();
            }
            bwMap.clear();
            long endTime = System.currentTimeMillis();
            System.err.println(endTime - startTime);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fw != null) {
                    fw.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

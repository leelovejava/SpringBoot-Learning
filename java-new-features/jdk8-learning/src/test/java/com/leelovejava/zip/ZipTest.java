package com.leelovejava.zip;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.*;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 压缩20M文件从30秒到1秒的优化过程
 * https://www.jianshu.com/p/25b328753017
 *
 * @author leelovejava
 * @date 2020/12/2 10:28
 **/
public class ZipTest {
    // zip压缩包所存放的位置
    private static final String ZIP_FILE = "D:\\logs\\test\\aa.zip";
    // 图片的目录
    private static final String JPG_FILE_DIRECTORY = "D:\\logs\\test\\photo\\";

    public static void main(String[] args) {
        ///zipFileNoBuffer();
        ///zipFileBuffer();
        ///zipFileChannel();
        ///zipFileMap();
        ///zipFilePip();
    }

    /**
     * 未使用buffer缓存,30s
     */
    public static void zipFileNoBuffer() {
        File zipFile = new File(ZIP_FILE);
        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile))) {
            //开始时间
            long beginTime = System.currentTimeMillis();
            File[] files = new File(JPG_FILE_DIRECTORY).listFiles();
            for (File file : files) {
                try (InputStream input = new FileInputStream(file)) {
                    zipOut.putNextEntry(new ZipEntry(file.getName()));
                    int temp;
                    // 调用本地方法与原生操作系统进行交互，从磁盘中读取数据。每读取一个字节的数据就调用一次本地方法与操作系统交互，是非常耗时的
                    while ((temp = input.read()) != -1) {
                        zipOut.write(temp);
                    }
                }
            }
            printInfo(beginTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 第一次优化过程-从30秒到2秒,缓冲区BufferInputStream
     */
    public static void zipFileBuffer() {
        File zipFile = new File(ZIP_FILE);
        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(zipOut)) {
            //开始时间
            long beginTime = System.currentTimeMillis();
            File[] files = new File(JPG_FILE_DIRECTORY).listFiles();
            for (File file : files) {
                try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file))) {
                    zipOut.putNextEntry(new ZipEntry(file.getName()));
                    int temp;
                    // 如果使用缓冲区的话（这里假设初始的缓冲区大小足够放下30000字节的数据）那么只需要调用一次就行。
                    // 因为缓冲区在第一次调用read()方法的时候会直接从磁盘中将数据直接读取到内存中。随后再一个字节一个字节的慢慢返回。
                    while ((temp = bufferedInputStream.read()) != -1) {
                        bufferedOutputStream.write(temp);
                    }
                }
            }
            printInfo(beginTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 第二次优化过程-从2秒到1秒,使用Channel
     */
    public static void zipFileChannel() {
        // 开始时间
        long beginTime = System.currentTimeMillis();
        File zipFile = new File(ZIP_FILE);
        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
             WritableByteChannel writableByteChannel = Channels.newChannel(zipOut)) {
            File[] files = new File(JPG_FILE_DIRECTORY).listFiles();
            for (File file : files) {
                try (FileChannel fileChannel = new FileInputStream(file).getChannel()) {
                    zipOut.putNextEntry(new ZipEntry(file.getName()));
                    // transferTo:零拷贝
                    fileChannel.transferTo(0, file.length(), writableByteChannel);
                }
            }
            printInfo(beginTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Version 4 使用Map内存映射文件
     * 内存中开辟了一段直接缓冲区,与数据直接作交互
     */
    public static void zipFileMap() {
        // 开始时间
        long beginTime = System.currentTimeMillis();
        File zipFile = new File(ZIP_FILE);
        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
             WritableByteChannel writableByteChannel = Channels.newChannel(zipOut)) {
            File[] files = new File(JPG_FILE_DIRECTORY).listFiles();
            for (File file : files) {

                zipOut.putNextEntry(new ZipEntry(file.getName()));

                // 内存中的映射文件
                MappedByteBuffer mappedByteBuffer = new RandomAccessFile(file.getPath(), "r").getChannel()
                        .map(FileChannel.MapMode.READ_ONLY, 0, file.length());

                writableByteChannel.write(mappedByteBuffer);
            }
            printInfo(beginTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Version 5 使用Pip
     * Java NIO 管道是2个线程之间的单向数据连接。Pipe有一个source通道和一个sink通道。其中source通道用于读取数据，sink通道用于写入数据。
     * 可以看到源码中的介绍，大概意思就是写入线程会阻塞至有读线程从通道中读取数据。如果没有数据可读，读线程也会阻塞至写线程写入数据。直至通道关闭。
     * Whether or not a thread writing bytes to a pipe will block until another thread reads those bytes
     */
    public static void zipFilePip() {

        long beginTime = System.currentTimeMillis();
        try (WritableByteChannel out = Channels.newChannel(new FileOutputStream(ZIP_FILE))) {
            Pipe pipe = Pipe.open();
            // 异步任务
            CompletableFuture.runAsync(() -> runTask(pipe));

            // 获取读通道
            ReadableByteChannel readableByteChannel = pipe.source();
            ByteBuffer buffer = ByteBuffer.allocate(
                    ((int) FileUtils.sizeOfDirectory(new File(JPG_FILE_DIRECTORY))) * 10);
            while (readableByteChannel.read(buffer) >= 0) {
                buffer.flip();
                out.write(buffer);
                buffer.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        printInfo(beginTime);

    }

    /**
     * 异步任务
     *
     * @param pipe
     */
    public static void runTask(Pipe pipe) {

        try (ZipOutputStream zos = new ZipOutputStream(Channels.newOutputStream(pipe.sink()));
             WritableByteChannel out = Channels.newChannel(zos)) {

            File[] files = new File(JPG_FILE_DIRECTORY).listFiles();
            for (File file : files) {
                zos.putNextEntry(new ZipEntry(file.getName()));

                FileChannel jpgChannel = new FileInputStream(new File(file.getAbsolutePath())).getChannel();

                jpgChannel.transferTo(0, file.length(), out);

                jpgChannel.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * noBuffer,fileSize:20M,consume time:93916
     * buffer,fileSize:20M,consume time:4478
     * channel,fileSize:20M,consume time:2711
     * map,fileSize:20M,consume time:2395
     * pip,fileSize:20M,consume time:3312
     *
     * @param time 开始时间
     */
    private static void printInfo(long time) {
        long size = FileUtils.sizeOfDirectory(new File(JPG_FILE_DIRECTORY));
        System.out.println("fileSize:" + (size / 1024 / 1024) + "M");
        System.out.println("consume time:" + (System.currentTimeMillis() - time));
    }
}

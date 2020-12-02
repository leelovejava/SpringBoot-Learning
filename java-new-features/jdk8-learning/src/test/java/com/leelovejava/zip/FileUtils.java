package com.leelovejava.zip;

import java.io.File;
import java.nio.file.Files;
import java.util.Objects;

/**
 * 文件工具类
 * @author leelovejava
 * org.apache.commons.io.FileUtils
 */
public class FileUtils {

    public static long sizeOfDirectory(File directory) {
        checkDirectory(directory);
        return sizeOfDirectory0(directory);
    }

    private static void checkDirectory(File directory) {
        if (!directory.exists()) {
            throw new IllegalArgumentException(directory + " does not exist");
        } else if (!directory.isDirectory()) {
            throw new IllegalArgumentException(directory + " is not a directory");
        }
    }

    private static long sizeOfDirectory0(File directory) {
        File[] files = directory.listFiles();
        if (files == null) {
            return 0L;
        } else {
            long size = 0L;
            File[] var4 = files;
            int var5 = files.length;

            for (int var6 = 0; var6 < var5; ++var6) {
                File file = var4[var6];
                if (!isSymlink(file)) {
                    size += sizeOf0(file);
                    if (size < 0L) {
                        break;
                    }
                }
            }

            return size;
        }
    }

    public static boolean isSymlink(File file) {
        Objects.requireNonNull(file, "file");
        return Files.isSymbolicLink(file.toPath());
    }

    private static long sizeOf0(File file) {
        return file.isDirectory() ? sizeOfDirectory0(file) : file.length();
    }

}

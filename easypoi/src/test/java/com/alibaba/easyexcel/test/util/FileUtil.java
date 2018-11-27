package com.alibaba.easyexcel.test.util;

import java.io.InputStream;

public class FileUtil {

    public static InputStream getResourcesFileInputStream(String fileName) {
        try {
            return Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

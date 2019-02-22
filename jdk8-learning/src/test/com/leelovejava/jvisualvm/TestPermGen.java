package com.leelovejava.jvisualvm;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * 永久保留区域PermGen
 * @author tianhao
 */
public class TestPermGen {
    
    private static List<Object> insList = new ArrayList<Object>();

    public static void main(String[] args) throws Exception {

        permLeak();
    }

    private static void permLeak() throws Exception {
        for (int i = 0; i < 1000; i++) {
            URL[] urls = getURLS();
            URLClassLoader urlClassloader = new URLClassLoader(urls, null);
            Class<?> logfClass = Class.forName("org.apache.commons.logging.LogFactory", true,urlClassloader);
            Method getLog = logfClass.getMethod("getLog", String.class);
            Object result = getLog.invoke(logfClass, "TestPermGen");
            insList.add(result);
            System.out.println(i + ": " + result);
        }
    }

    private static URL[] getURLS() throws MalformedURLException {
        File libDir = new File("C:/Users/wadexu/.m2/repository/commons-logging/commons-logging/1.1.1");
        File[] subFiles = libDir.listFiles();
        int count = subFiles.length;
        URL[] urls = new URL[count];
        for (int i = 0; i < count; i++) {
            urls[i] = subFiles[i].toURI().toURL();
        }
        return urls;
    }

    
}
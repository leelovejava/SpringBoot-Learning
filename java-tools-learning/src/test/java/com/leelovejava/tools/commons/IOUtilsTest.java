package com.leelovejava.tools.commons;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * IOUtils对IO操作的封装
 *
 * @author y.x
 * @date 2019/11/25
 */
public class IOUtilsTest {
    public void test01() {
        // 拷贝流
        ///IOUtils.copy(InputStream input, OutputStream output);

        // 从流中读取内容，转为list
        ///List<String> line = IOUtils.readLines(InputStream input, Charset encoding);
    }

    /**
     * 对文件操作类的封装
     * @throws IOException
     */
    public void test02() throws IOException {
        File file = new File("/show/data.text");
        // 按行读取文件
        List<String> lines = FileUtils.readLines(file, "UTF-8");
        // 将字符串写入文件
        FileUtils.writeStringToFile(file, "test", "UTF-8");
        // 文件复制
        File srcFile = new File("/show/srcFile");
        File destFile = new File("/show/destFile");

        FileUtils.copyFile(srcFile, destFile);
    }
}

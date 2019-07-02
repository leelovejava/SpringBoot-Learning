package com.leelovejava.easyexcel;


import com.alibaba.easyexcel.test.listen.ExcelListener;
import com.alibaba.easyexcel.test.model.ReadModel0;
import com.alibaba.easyexcel.test.util.FileUtil;
import com.alibaba.excel.ExcelReader;

import java.io.IOException;
import java.io.InputStream;

public class ReadTest {
    /**
     * 07版本excel读数据量大于1千行，内部采用回调方法.
     *
     * @throws IOException 简单抛出异常，真实环境需要catch异常,同时在finally中关闭流
     */
    public static void saxReadListStringV2007() throws IOException {
        InputStream inputStream = FileUtil.getResourcesFileInputStream("1.xlsx");
        ExcelListener excelListener = new ExcelListener();
        // InputStream in, ExcelTypeEnum excelTypeEnum, Object customContent, AnalysisEventListener eventListener
        ExcelReader excelReader = new ExcelReader(inputStream, ReadModel0.class, excelListener);
        long startTime = System.currentTimeMillis();
        excelReader.read();
        inputStream.close();
        System.out.print("所需的时间:");
        System.out.print(System.currentTimeMillis()-startTime);
    }

    public static void main(String[] args) {
        try {
            saxReadListStringV2007();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

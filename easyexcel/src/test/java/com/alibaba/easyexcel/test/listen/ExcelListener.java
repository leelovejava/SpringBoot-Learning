package com.alibaba.easyexcel.test.listen;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * easyexcel 解析监听器
 */
public class ExcelListener extends AnalysisEventListener {


    private List<Object> data = new ArrayList<Object>();

    /**
     * 每解析一行会回调invoke()方法。
     *
     * @param object
     * @param context
     */
    @Override
    public void invoke(Object object, AnalysisContext context) {
        ///System.out.println("getCurrentSheet:"+context.getCurrentSheet());
        if (data.size() <= 100) {
            data.add(object);
        } else {
            doSomething();
            data = new ArrayList<Object>();
        }
    }

    /**
     * 整个excel解析结束执行
     *
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        doSomething();
    }

    /**
     * 入库调用接口
     */
    public void doSomething() {
        for (Object o : data) {
            System.out.println("object:" + o.toString());
        }
    }
}
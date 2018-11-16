package com.alibaba.easyexcel.test.model;

import com.alibaba.excel.annotation.ExcelProperty;

import java.math.BigDecimal;
import java.util.Date;

public class ReadModel0 {

    @ExcelProperty(index = 2)
    private String birthday;

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
}

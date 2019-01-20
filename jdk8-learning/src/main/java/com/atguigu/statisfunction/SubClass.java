package com.atguigu.statisfunction;

import com.atguigu.statisfunction.MyFun;
import com.atguigu.statisfunction.MyInterface;

public class SubClass /*extends MyClass*/ implements MyFun, MyInterface {

    @Override
    public String getName() {
        return MyInterface.super.getName();
    }

}

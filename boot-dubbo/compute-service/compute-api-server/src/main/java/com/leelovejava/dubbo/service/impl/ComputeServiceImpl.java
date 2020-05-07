package com.leelovejava.dubbo.service.impl;

import com.leelovejava.dubbo.service.ComputeService;

/**
 * @author zhaiyc
 * @date 2016/7/14
 */
public class ComputeServiceImpl implements ComputeService {

    @Override
    public Integer add(int a, int b) {
        return a + b;
    }

}

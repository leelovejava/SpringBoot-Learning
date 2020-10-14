package com.leelovejava.boot.jwt.util;

import com.leelovejava.boot.jwt.vo.ResultVo;

/**
 * @author leelovejava
 */
public class ResultVOUtil {
    public static ResultVo error(String code, String msg) {
        return new ResultVo(code, msg);
    }

    public static ResultVo success(String data) {
        return new ResultVo("200", "成功", data);
    }
}

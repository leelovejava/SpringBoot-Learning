package com.didispace.jwt.util;

import com.didispace.jwt.vo.ResultVo;

public class ResultVOUtil {
    public static ResultVo error(String code, String msg) {
        return new ResultVo(code, msg);
    }

    public static ResultVo success(String data) {
        return new ResultVo("200", "成功", data);
    }
}

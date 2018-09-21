package com.didispace.jwt.vo;

/**
 * 返回结果 封装对象
 */
public class ResultVo {
    /**
     * 状态码
     */
    private String code;
    /**
     * 消息
     */
    private String msg;
    /**
     * 数据
     */
    private Object data;

    public ResultVo() {
    }

    public ResultVo(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResultVo(String code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}

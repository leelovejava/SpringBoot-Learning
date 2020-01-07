package com.leelovejava.rabbit.service;

import com.leelovejava.rabbit.transaction.ApiConstants;
import com.leelovejava.rabbit.transaction.ResponseBase;
import org.springframework.stereotype.Component;

/**
 * @author 翟永超
 */
@Component
public class BaseApiService {

    public ResponseBase setResultError(Integer code, String msg) {
        return setResult(code, msg, null);
    }

    /**
     * 返回错误，可以传msg
     *
     * @param msg
     * @return
     */
    public ResponseBase setResultError(String msg) {
        return setResult(ApiConstants.HTTP_RES_CODE_500, msg, null);
    }

    /**
     * 返回成功，可以传data值
     *
     * @param data
     * @return
     */
    public ResponseBase setResultSuccess(Object data) {
        return setResult(ApiConstants.HTTP_RES_CODE_200, ApiConstants.HTTP_RES_CODE_200_VALUE, data);
    }

    /**
     * 返回成功，沒有data值
     *
     * @return
     */
    public ResponseBase setResultSuccess() {
        return setResult(ApiConstants.HTTP_RES_CODE_200, ApiConstants.HTTP_RES_CODE_200_VALUE, null);
    }

    /**
     * 返回成功，沒有data值
     *
     * @param msg
     * @return
     */
    public ResponseBase setResultSuccess(String msg) {
        return setResult(ApiConstants.HTTP_RES_CODE_200, msg, null);
    }

    /**
     * 通用封装
     *
     * @param code
     * @param msg
     * @param data
     * @return
     */
    public ResponseBase setResult(Integer code, String msg, Object data) {
        return new ResponseBase(code, msg, data);
    }
}

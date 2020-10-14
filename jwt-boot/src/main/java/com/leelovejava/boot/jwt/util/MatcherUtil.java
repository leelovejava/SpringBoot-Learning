package com.leelovejava.boot.jwt.util;

import java.util.regex.Pattern;

/**
 * 验证工具类
 * @author leelovejava
 */
public class MatcherUtil {
    /**
     * 正则验证是否是邮箱,邮箱为true
     *
     * @param usernameOrEmail 用户名或者是 邮箱
     * @return
     */
    public static boolean matcherEmail(String usernameOrEmail) {
        return Pattern.matches("^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$", usernameOrEmail);
    }
}

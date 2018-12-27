package com.battcn.controller;

import com.battcn.annotation.DateTime;
import com.battcn.pojo.Book;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;

/**
 * 参数校验
 *
 * @author Levin
 * @since 2018/6/04 0031
 */
@Validated
@RestController
public class ValidateController {


    /**
     * http:localhost:8080/test1
     *
     * @param name 姓名
     */
    @GetMapping("/test1")
    public String test1(String name) {
        if (name == null) {
            throw new NullPointerException("name 不能为空");
        }
        if (name.length() < 2 || name.length() > 10) {
            throw new RuntimeException("name 长度必须在 2 - 10 之间");
        }
        return "success";
    }

    /**
     * 控制层验证,service亦可
     * 默认情况下，如果校验失败会抛javax.validation.ConstraintViolationException异常，可以用统一异常处理去对这些异常做处理
     * http://localhost:8080/test2
     *
     * @param name
     * @return
     * @Validated： 开启数据有效性校验，添加在类上即为验证方法，添加在方法参数中即为验证参数对象。（添加在方法上无效）
     * @NotBlank： 被注释的字符串不允许为空（value.trim() > 0 ? true : false）
     * @Length： 被注释的字符串的大小必须在指定的范围内
     * @NotNull： 被注释的字段不允许为空(value ! = null ? true : false)
     * @DecimalMin： 被注释的字段必须大于或等于指定的数值
     */
    @GetMapping("/test2")
    public String test2(@NotBlank(message = "name 不能为空") @Length(min = 2, max = 10, message = "name 长度必须在 {min} - {max} 之间") String name) {
        return "success";
    }

    /**
     * 前端报400异常
     * http://localhost:8080/test3
     *
     * @param book
     * @return
     */
    @GetMapping("/test3")
    public String test3(@Validated Book book) {
        return "success";
    }

    /**
     * 自定义验证注解
     * 验证失败,抛出javax.validation.ConstraintViolationException异常
     * http://localhost:8080/test4?date=111111
     *
     * @param date 日期
     * @return
     */
    @GetMapping("/test4")
    public String test(@DateTime(message = "您输入的格式错误，正确的格式为：{format}", format = "yyyy-MM-dd HH:mm") String date) {
        return "success";
    }


}
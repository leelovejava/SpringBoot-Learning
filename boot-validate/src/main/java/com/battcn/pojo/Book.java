package com.battcn.pojo;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author Levin
 * @since 2018/6/5 0005
 */
public class Book {

    /**
     * javax.validation的方法
     *
     * @NotNull 限制必须不为null
     * @NotEmpty 验证注解的元素值不为 null 且不为空（字符串长度不为0、集合大小不为0）
     * @NotBlank 验证注解的元素值不为空（不为null、去除首位空格后长度为0），不同于@NotEmpty，@NotBlank只应用于字符串且在比较时会去除字符串的空格
     * @Pattern(value) 限制必须符合指定的正则表达式
     * @Size(max,min) 限制字符长度必须在 min 到 max 之间（也可以用在集合上）
     * @Email 验证注解的元素值是Email，也可以通过正则表达式和flag指定自定义的email格式
     * @Max(value) 限制必须为一个不大于指定值的数字
     * @Min(value) 限制必须为一个不小于指定值的数字
     * @DecimalMax(value) 限制必须为一个不大于指定值的数字
     * @DecimalMin(value) 限制必须为一个不小于指定值的数字
     * @Null 限制只能为null（很少用）
     * @AssertFalse 限制必须为false （很少用）
     * @AssertTrue 限制必须为true （很少用）
     * @Past 限制必须是一个过去的日期
     * @Future 限制必须是一个将来的日期
     * @Digits(integer,fraction) 限制必须为一个小数，且整数部分的位数不能超过 integer，小数部分的位数不能超过 fraction （很少用）
     */

    private Integer id;

    @NotBlank(message = "name 不允许为空")
    @Length(min = 2, max = 10, message = "name 长度必须在 {min} - {max} 之间")
    private String name;

    //@NotEmpty(message="密码不能为空")
    //@Length(min=6,message="密码长度不能小于6位")
    //private String passWord;

    @NotNull(message = "price 不允许为空")
    @DecimalMin(value = "0.1", message = "价格不能低于 {value}")
    private BigDecimal price;

    //@Email(message="请输入正确的邮箱")
    //private String email;

    //@Pattern(regexp = "^(\\d{18,18}|\\d{15,15}|(\\d{17,17}[x|X]))$", message = "身份证格式错误")
    //private String idCard;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
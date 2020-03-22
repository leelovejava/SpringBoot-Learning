package com.didispace.web;

import com.didispace.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 程序猿DD
 * @version 1.0.0
 * @blog http://blog.didispace.com
 */
@Controller
public class HelloController {

    @ResponseBody
    @RequestMapping("/hello")
    public String hello() {
        return "Hello World";
    }

    @RequestMapping("/")
    public String index(ModelMap map) {
        map.addAttribute("host", "http://blog.didispace.com");
        return "index";
    }

    /**
     * Thymeleaf之变量
     *
     * @param model
     * @return
     */
    @GetMapping("show2")
    public String show2(Model model) {
        User user = new User();
        user.setAge(21);
        user.setName("Jack Chen");
        user.setFriend(new User("李小龙", 30));

        model.addAttribute("user", user);
        return "show2";
    }

    /**
     * 在环境变量中添加日期类型对象
     *
     * @param model
     * @return
     */
    @GetMapping("show3")
    public String show3(Model model) {
        model.addAttribute("today", new Date());

        User user = new User();
        user.setAge(21);
        user.setName("二狗");
        user.setRole("admin");
        user.setSex(false);
        model.addAttribute("user", user);

        List<User> users = new ArrayList<>(5);
        for (int i = 0, len = 4; i < len; i++) {
            users.add(new User("土狗" + i, Math.round(2)));
        }
        model.addAttribute("users", users);

        return "show3";
    }

}
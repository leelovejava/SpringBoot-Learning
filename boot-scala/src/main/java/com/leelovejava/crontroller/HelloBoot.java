package com.leelovejava.crontroller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author tianhao
 */
@RestController
public class HelloBoot {


    @RequestMapping(value = "/sayHello", method = RequestMethod.GET)
    public String sayHello(){
        return "Hello Boot....";
    }

}

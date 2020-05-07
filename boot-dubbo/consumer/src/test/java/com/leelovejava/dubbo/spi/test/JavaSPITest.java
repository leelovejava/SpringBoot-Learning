package com.leelovejava.dubbo.spi.test;

import com.leelovejava.dubbo.spi.Robot;
import org.junit.Test;

import java.util.ServiceLoader;

/**
 * Java SPI 示例
 *
 * @author leelovejava
 */
public class JavaSPITest {

    @Test
    public void sayHello() throws Exception {
        ServiceLoader<Robot> serviceLoader = ServiceLoader.load(Robot.class);
        System.out.println("Java SPI");
        serviceLoader.forEach(Robot::sayHello);
    }
}

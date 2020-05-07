package com.leelovejava.dubbo.spi.test;

import com.leelovejava.dubbo.spi.Robot;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.junit.Test;

/**
 * Dubbo SPI 示例
 *
 * @author leelovejava
 * @see 'http://dubbo.apache.org/zh-cn/docs/source_code_guide/dubbo-spi.html'
 */
public class DubboSPITest {

    /**
     * 与 Java SPI 实现类配置不同，Dubbo SPI 是通过键值对的方式进行配置，这样我们可以按需加载指定的实现类。另外，在测试 Dubbo SPI 时，需要在 Robot 接口上标注 @SPI 注解
     * @throws Exception
     */
    @Test
    public void sayHello() throws Exception {
        ExtensionLoader<Robot> extensionLoader =
                ExtensionLoader.getExtensionLoader(Robot.class);
        Robot optimusPrime = extensionLoader.getExtension("optimusPrime");
        optimusPrime.sayHello();
        Robot bumblebee = extensionLoader.getExtension("bumblebee");
        bumblebee.sayHello();
    }
}

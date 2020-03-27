package com.leelovejava.spring.proxy.jdk;

/**
 * 测试类
 *
 * @author leelovejava
 */
public class Test {
    /**
     * 1. 业务接口
     * 2. 实现了业务接口的业务类
     * 3. 实现了InvocationHandler接口的handler代理类
     */

    /**
     * jdk动态代理会生成一个动态代理类，生成相应的字节码，然后通过ClassLoader加载字节码；
     * 该实例继承了Proxy类，并实现了业务接口，在实现的方法里通过反射调用了InvocationHandler接口实现类的invoke()回调方法；
     *
     * @param args
     * @throws Throwable
     */
    public static void main(String[] args) throws Throwable {
        UserService userService = new UserServiceImpl();
        ServiceInvocationHandler handler = new ServiceInvocationHandler(userService);

        // 根据目标生成代理对象
        UserService proxy = (UserService) handler.getProxy();
        proxy.addUser();
    }
}
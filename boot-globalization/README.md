# 一、国际化
## 1. 简述
国际化是什么，简单地说就是，在不修改内部代码的情况下，根据不同语言及地区显示相应的语言界面。

## 2. Spring官方解释
Spring中对国际化文件支持的基础接口是`MessageSource`。
参照Spring对于`MessageSource`解释的官方文档：
> Strategy interface for resolving messages, with support for the parameterization and internationalization of such messages.
Spring provides two out-of-the-box implementations for production:
> `ResourceBundleMessageSource`, built on top of the standard ResourceBundle
`ReloadableResourceBundleMessageSource`, being able to reload message definitions without restarting the VM

意思为：
> Spring提供了两种开箱即用的实现，一种是标准实现，一种是运行时可重新加载。

***
***
本文允许转载，转载本文时请加上本文链接：
https://blog.csdn.net/nthack5730/article/details/82870368
https://www.jianshu.com/p/a354d3f849ec
对于爬虫网站随意爬取以及转载不加原文链接的，本人保留追究法律责任的权力！
***
***

# 二、SpringBoot中使用MessageSource国际化
## 1. SpringBoot自动化配置国际化支持
Spring Boot已经对i18n国际化做了自动配置，自动配置类为：
```java
org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration
```

使用`MessageSource`时只要`@Autowired`就行：
```java
@Autowired
private MessageSource messageSource;
```
而Spring在启动的时候装备的实现类是：
```java
org.springframework.context.support.ResourceBundleMessageSource
```

但是很多人会说：我用了Autowired为什么调用`messageSource.getMessage(...)`却返回空内容？
这是因为SpringBoot对国际化properties文件路径默认设定是在`message`文件夹下，找不到文件夹，所以就没有信息返回呗。
下面会进行如何自定义i18n国际化配置讲述。


## 2. 常见国际化支持配置参数解释
`MessageSource`国际化配置中有几个参数是常见的，在这里必须要说明下，因为知道遮几个参数才能理解下面的配置：
> `basename`：默认的扫描的国际化文件名为messages，即在resources建立messages_xx.properties文件，可以通过逗号指定多个，如果不指定包名默认从classpath下寻找。
> `encoding`：默认的编码为UTF-8，也可以改为GBK等等
> `cacheSeconds`：加载国际化文件的缓存时间，单位为秒，默认为永久缓存。
> `fallbackToSystemLocale`：当找不到当前语言的资源文件时，如果为true默认找当前系统的语言对应的资源文件如messages_zh_CN.properties，如果为false即加载系统默认的如messages.properties文件。


## 3. 自定义i18n国际化配置
很多时候我们要修改自动配置中的某些配置参数，例如message.properties文件所在的文件夹路径、properties文件的编码格式等等，可以用以下两种方式进行配置修改，选一个就好：
1. 修改Bean，直接返回一个配置Bean
2. 使用配置文件（比较推荐这种做法）

### 3.1 在application.yml配置文件中
在配置文件中加入下面的配置：（application.properties配置文件麻烦自行转换下）
```yml
spring: 
  messages:
    basename: i18n/messages
    encoding: UTF-8
```

### 3.2 用Bean进行代码配置
在`@Configuration`的`类`下面加入Bean配置就好，下面是完整的类代码：
```java
@Configuration
public class MessageSourceConfig {

    @Bean(name = "messageSource")
    public ResourceBundleMessageSource getMessageSource() throws Exception {
        ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
        resourceBundleMessageSource.setDefaultEncoding("UTF-8");
        resourceBundleMessageSource.setBasenames("i18n/messages");
        return resourceBundleMessageSource;
    }
}
```

其中：
```java
resourceBundleMessageSource.setBasenames("i18n/messages");
```
指定了`i18n`文件的位置，这是个在`i18n`文件夹下的`Resources Bundle`文件集合，新建的时候在IDEA里面选择的是`Resource Bundle`类型，名字写的是`messages`。这个很重要，对应上方的配置文件！


> 注意：如果返回值不是`MessageSource`类型就要在`@Bean`上面指定`name`参数，否则无法使用`@Autowired`进行自动装入，这个是SpringIOC的东西，大家可以去查找对应的资料。



## 4. 使用i18n国际化
### 4.1 写入国际化文件
根据上面的配置，在`resouces-i18n`下面加入`Resource Bundle`类型文件：
名称为`messages`，然后加入以下两种类型：
> zh-CN
> en_US

完成之后会生成下面三个文件：
1. 【默认】messages.properties
2. 【英文】messages_en_US.properties
3. 【中文】messages_zh_CN.properties

在里面写入同样的字段：
```properties
hello=xxx
```
其中`xxx`在不同文件里面写不同的语句（你能分辨出来就行）


### 4.2 代码中使用MessageSource
在需要用到的类中`@Autowired`就可以使用了：
```java
@Autowired
private MessageSource messageSource;
```

然后使用：
```java
messageSource.getMessage(code, args, defaultMessage, locale);
```
就可以获得对应的信息内容。


### 4.3 MessageSource接口中的方法
MessageSource中有三个方法，分别是：
```java
String getMessage(String code, @Nullable Object[] args, @Nullable String defaultMessage, Locale locale);

String getMessage(String code, @Nullable Object[] args, Locale locale) throws NoSuchMessageException;

String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException;
```

常用的是第一个（主要是其他两个会抛出NoSuchMessageException，不太方便），下面是第一个方法中的参数解释：
> `code`：信息的键，properties中的key
> `args`：系统运行时参数，可为空
> `defaultMessage`：默认信息，可为空
> `locale`：区域信息，我们可以通过`java.util.Locale`类下面的静态常量找到对应的值。如：简体中文就是`zh_CN`；英文就是`en_US`，详见`java.util.Locale`中的常量值。


**测试用例：**
```java
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.main.web-application-type=reactive")
public class TestMessageSource {
    private static final Logger logger = LoggerFactory.getLogger(TestMessageSource.class);

    @Autowired
    private MessageSource messageSource;

    @Test
    public void testGetMessage() {
        logger.info(messageSource.getMessage("hello", null, "", null));
    }
}
```
测试用例中使用了`null`的`locale`参数，这里会输出`messages.properties`中`hello`的值，这个参数会在下面4.4节中说明原因。



### 4.4 指定和默认的国际化信息
上面4.3中说明了`MessageSource`接口的三个方法，我们最常用的是第一个不抛出异常的方法。其中有一个是`locale`参数，这个参数的作用就是指定**语言环境**。
在`java.util.Locale`类中定义了很多的语言环境，如：
```java
...
static public final Locale SIMPLIFIED_CHINESE = createConstant("zh", "CN");
static public final Locale CHINA = SIMPLIFIED_CHINESE;
static public final Locale UK = createConstant("en", "GB");
static public final Locale US = createConstant("en", "US");
static public final Locale CANADA = createConstant("en", "CA");
...
```
`MessageSource.getMessage(...)`所有方法中，都有`Locale`参数，指定区域信息。
当调用时，要如果指定位置为：`Locale.CHINA`，这时`ResourceBundelMessageSource`会从`messages_zh_CN.properties`中寻找对应的键值。
当给出`Locale`参数值为`null`，空的区域信息；或者**对应的properties文件中没有找到对应的键值对**，那么`ResourceBundelMessageSource`默认会从`messages.properties`中寻找键，当都找不到的时候，会返回空字符串。



# 三、SpringMVC使用国际化
## 装配到代码中
有了上面的配置，只要使用：
```java
@Autowired
private MessageSource messageSource;
```
就能将MessageSource注入。


## 使用规范
通常，我们都会在`Controller`、`ControllerAdvice`中进行使用，最好**不在`Service`中使用**，这样统一开发规范，`Service`可以返回`properties`相对应的`key`给上层，由`Controller层`统一处理。
这种做法的原因是在遇到抛异常给`统一异常处理`（`ControllerAdvice`下的`ExceptionHandler`）时，异常处理能正确获取到对应的信息。


## 简化使用
上面的使用例子中，使用：
```java
messageSource.getMessage("hello", null, "", null)
```
用了4个参数，这4个参数对应的都是地理位置等信息，但这类参数每次在`Controller`调用的时候都要放一遍，确实很不方便，因为我们只要传进的是`key`。
**我们可以考虑让包装一个类去屏蔽这些参数。**
当然，下面的例子是忽略地理位置信息的，如果需要带上地理位置信息，可以考虑使用拦截器方式通过前端传回语言信息，再去对应的地方拿对应的信息。

```java
@Component
public class MessageSourceUtil {

    @Autowired
    private MessageSource messageSource;

    public String getMessage(String code) {
        return getMessage(code, null);
    }

    public String getMessage(String code, Object[] args) {
        return getMessage(code, args, "");
    }

    public String getMessage(String code, Object[] args, String defaultMsg) {
        //这里使用比较方便的方法，不依赖request.
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(code, args, defaultMsg, locale);
    }

}
```

上面使用`@Component`注解来指定该类是组件，这样讲就能直接在`Controller`层中使用：
```java
@Autowired
private MessageSourceUtil messageSourceUtil;
```
然后使用：
```java
messageSourceUtil.getMessage(String);
```

可以看到我们使用的是`LocaleContextHolder.getLocale();`来处理`locale`位置参数，这里指定的是服务器的位置。与客户地理位置误关。如果需要根据客户不同位置，可以使用拦截器方式，请求的时候带上位置信息（最好放在header），然后在拦截器中获取，带参数到这里。当然，上面的类中`LocaleContextHolder`也需要做对应的修改。

## 使用例子
### 在Controller中使用
```java
@RestController
@RequestMapping("/")
public class BaseController {

    @Autowired
    private MessageSourceUtil messageSourceUtil;

    /**
     * Ping一下，神清气爽
     *
     * @param str
     * @return
     */
    @RequestMapping("/ping")
    public ResponseEntity ping(String str) {
        return ResponseEntity.ok(messageSourceUtil.getMessage(str));
    }
}
```


### 在全局异常处理中使用
```java
@ControllerAdvice
public class ExceptionHandlerUtil {
    @Autowired
    private MessageSourceUtil messageSourceUtil;

    /**
     * 全局处理业务的异常
     * 通过{@link MessageUtil}直接返回Service中抛出的信息
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity handleBussinessException(Exception ex) {
        return ResponseEntity.badRequest().body(messageSourceUtil.getMessage(ex.getMessage()));
    }
}
```

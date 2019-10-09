# 1. zuul动态路由

```sql
CREATE TABLE `gateway_api_route` (
   `id` varchar(50) NOT NULL,
   `path` varchar(255) NOT NULL,
   `service_id` varchar(50) DEFAULT NULL,
   `url` varchar(255) DEFAULT NULL,
   `retryable` tinyint(1) DEFAULT NULL,
   `enabled` tinyint(1) NOT NULL,
   `strip_prefix` int(11) DEFAULT NULL,
   `api_name` varchar(255) DEFAULT NULL,
   PRIMARY KEY (`id`)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8

INSERT INTO gateway_api_route (id, path, service_id, retryable, strip_prefix, url, enabled) VALUES ('order-service', '/order/**', 'order-service',0,1, NULL, 1);
```

你可以自己用简单的spring mvc+前端页面封装一个可视化的网关管理工作台，如果新开发了一个服务之后，就可以在这个界面上配置一下，说某个服务对应某个url路径，修改，增删改查

> http://localhost:9000/order/order/create?productId=1&userId=1&count=2&totalPrice=50

生产级，企业级的功能，网关的动态路由


# 2. 基于网关实现灰度发布

准备一个数据库和一个表（也可以用Apollo配置中心、Redis、ZooKeeper，其实都可以），放一个灰度发布启用表

id service_id path enable_gray_release

```sql
CREATE TABLE `gray_release_config` (
   `id` int(11) NOT NULL AUTO_INCREMENT,
   `service_id` varchar(255) DEFAULT NULL,
   `path` varchar(255) DEFAULT NULL,
   `enable_gray_release` int(11) DEFAULT NULL,
   PRIMARY KEY (`id`)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8
```


在zuul里面加入下面的filter，可以在zuul的filter里定制ribbon的负载均衡策略

```xml
<dependency>
    <groupId>io.jmnarloch</groupId>
    <artifactId>ribbon-discovery-filter-spring-cloud-starter</artifactId>
    <version>2.1.0</version>
</dependency>
```


写一个zuul的filter，对每个请求，zuul都会调用这个filter
```java
@Configuration
public class GrayReleaseFilter extends ZuulFilter {

@Autowired
private JdbcTemplate jdbcTemplate;

    @Override
    public int filterOrder() {
        return PRE_DECORATION_FILTER_ORDER - 1;
    }
 
    @Override
    public String filterType() {
        return PRE_TYPE;
    }
 
    @Override
    public boolean shouldFilter() {
    	
    }
 
    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();

        Random random = new Random();
        int seed = random.getInt() * 100;

        if (seed = 50) {
            // put the serviceId in `RequestContext`
            RibbonFilterContextHolder.getCurrentContext()
                    .add("version", "new");
        }  else {
            RibbonFilterContextHolder.getCurrentContext()
                    .add("version", "old");
        }
        
        return null;
    }
}
```

eureka: instance: metadata-map: version: new
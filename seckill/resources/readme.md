## 秒杀
### 1. 无限制

### 2. 乐观锁

```xml
<update id="updateByOptimistic" parameterType="com.crossoverJie.seconds.kill.pojo.Stock">
    update stock
    <set>
        sale = sale + 1,
        version = version + 1,
    </set>
    WHERE id = #{id,jdbcType=INTEGER}
    AND version = #{version,jdbcType=INTEGER}
</update>
```

```java
public class OrderService{
    @Override
    public int createOptimisticOrder(int sid) throws Exception {
        //校验库存
        Stock stock = checkStock(sid);
        //乐观锁更新库存
        saleStockOptimistic(stock);
        //创建订单
        int id = createOrder(stock);
        return id;
    }
    private void saleStockOptimistic(Stock stock) {
        int count = stockService.updateStockByOptimistic(stock);
        if (count == 0){
            throw new RuntimeException("并发更新库存失败") ;
        }
    }
}
```

#### 提高吞吐量

为了进一步提高秒杀时的吞吐量以及响应效率，web 和 Service 进行横向扩展

* web 利用 Nginx 进行负载。
* Service 多台应用。

### 3. 分布式限流


使用RateLimiter实现限流

描述：当我们去秒杀一些商品时，此时可能会因为访问量太大而导致系统崩溃，此时要使用限流来进行限制访问量，当达到限流阀值，后续请求会被降级；降级后的处理方案可以是：返回排队页面（高峰期访问太频繁，等一会重试）、错误页等。

实现：项目使用RateLimiter来实现限流，RateLimiter是guava提供的基于令牌桶算法的限流实现类，通过调整生成token的速率来限制用户频繁访问秒杀页面，从而达到防止超大流量冲垮系统。（令牌桶算法的原理是系统会以一个恒定的速度往桶里放入令牌，而如果请求需要被处理，则需要先从桶里获取一个令牌，当桶里没有令牌可取时，则拒绝服务）

```java
@Configuration
public class RedisLimitConfig {
    private Logger logger = LoggerFactory.getLogger(RedisLimitConfig.class);
    @Value("${redis.limit}")
    private int limit;
    @Autowired
    private JedisConnectionFactory jedisConnectionFactory;
    @Bean
    public RedisLimit build() {
        RedisLimit redisLimit = new RedisLimit.Builder(jedisConnectionFactory, RedisToolsConstant.SINGLE)
                .limit(limit)
                .build();
        return redisLimit;
    }
}
```

分布式限流实现
```java
/**
 * limit traffic
 * @return if true
 */
public class RedisLimit{
    public boolean limit() {
        //get connection
        Object connection = getConnection();
        Object result = limitRequest(connection);
        if (FAIL_CODE != (Long) result) {
            return true;
        } else {
            return false;
        }
    }
    private Object limitRequest(Object connection) {
        Object result = null;
        String key = String.valueOf(System.currentTimeMillis() / 1000);
        if (connection instanceof Jedis){
            result = ((Jedis)connection).eval(script, Collections.singletonList(key), Collections.singletonList(String.valueOf(limit)));
            ((Jedis) connection).close();
        }else {
            result = ((JedisCluster) connection).eval(script, Collections.singletonList(key), Collections.singletonList(String.valueOf(limit)));
            try {
                ((JedisCluster) connection).close();
            } catch (IOException e) {
                logger.error("IOException",e);
            }
        }
        return result;
    }
    private Object getConnection() {
        Object connection ;
        if (type == RedisToolsConstant.SINGLE){
            RedisConnection redisConnection = jedisConnectionFactory.getConnection();
            connection = redisConnection.getNativeConnection();
        }else {
            RedisClusterConnection clusterConnection = jedisConnectionFactory.getClusterConnection();
            connection = clusterConnection.getNativeConnection() ;
        }
        return connection;
    }
}
```


web端:
```java
public class OrderController{
    /**
     * 乐观锁更新库存 限流
     * @param sid
     * @return
     */
    @SpringControllerLimit(errorCode = 200)
    @RequestMapping("/createOptimisticLimitOrder/{sid}")
    @ResponseBody
    public String createOptimisticLimitOrder(@PathVariable int sid) {
        logger.info("sid=[{}]", sid);
        int id = 0;
        try {
            id = orderService.createOptimisticOrder(sid);
        } catch (Exception e) {
            logger.error("Exception",e);
        }
        return String.valueOf(id);
    }
}
```

### 4. Redis 缓存
改造service

* 每次查询库存时走 Redis。
* 扣库存时更新 Redis。
* 需要提前将库存信息写入 Redis（手动或者程序自动都可以）

```java
public class OrderService{
    @Override
    public int createOptimisticOrderUseRedis(int sid) throws Exception {
        //检验库存，从 Redis 获取
        Stock stock = checkStockByRedis(sid);
        //乐观锁更新库存 以及更新 Redis
        saleStockOptimisticByRedis(stock);
        //创建订单
        int id = createOrder(stock);
        return id ;
    }
    private Stock checkStockByRedis(int sid) throws Exception {
        Integer count = Integer.parseInt(redisTemplate.opsForValue().get(RedisKeysConstant.STOCK_COUNT + sid));
        Integer sale = Integer.parseInt(redisTemplate.opsForValue().get(RedisKeysConstant.STOCK_SALE + sid));
        if (count.equals(sale)){
            throw new RuntimeException("库存不足 Redis currentCount=" + sale);
        }
        Integer version = Integer.parseInt(redisTemplate.opsForValue().get(RedisKeysConstant.STOCK_VERSION + sid));
        Stock stock = new Stock() ;
        stock.setId(sid);
        stock.setCount(count);
        stock.setSale(sale);
        stock.setVersion(version);
        return stock;
    }    
    /**
     * 乐观锁更新数据库 还要更新 Redis
     * @param stock
     */
    private void saleStockOptimisticByRedis(Stock stock) {
        int count = stockService.updateStockByOptimistic(stock);
        if (count == 0){
            throw new RuntimeException("并发更新库存失败") ;
        }
        //自增
        redisTemplate.opsForValue().increment(RedisKeysConstant.STOCK_SALE + stock.getId(),1) ;
        redisTemplate.opsForValue().increment(RedisKeysConstant.STOCK_VERSION + stock.getId(),1) ;
    }
}
```

### 5. Kafka 异步
利用同步转异步来提高性能,将写订单以及更新库存的操作进行异步化，利用 Kafka 来进行解耦和队列的作用

每当一个请求通过了限流到达了 Service 层通过了库存校验之后就将订单信息发给 Kafka ，这样一个请求就可以直接返回了

消费程序再对数据进行入库落地。

因为异步了，所以最终需要采取回调或者是其他提醒的方式提醒用户购买完成


```java
@Transactional(rollbackFor = Exception.class)
@Service(value = "DBOrderService")
public class OrderServiceImpl implements OrderService {

    private Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);


    @Resource(name = "DBStockService")
    private com.crossoverJie.seconds.kill.service.StockService stockService;

    @Autowired
    private StockOrderMapper orderMapper;

    @Autowired
    private RedisTemplate<String,String> redisTemplate ;


    @Autowired
    private KafkaProducer kafkaProducer ;

    @Value("${kafka.topic}")
    private String kafkaTopic ;

    @Override
    public int createWrongOrder(int sid) throws Exception{

        //校验库存
        Stock stock = checkStock(sid);

        //扣库存
        saleStock(stock);

        //创建订单
        int id = createOrder(stock);

        return id;
    }

    @Override
    public int createOptimisticOrder(int sid) throws Exception {

        //校验库存
        Stock stock = checkStock(sid);

        //乐观锁更新库存
        saleStockOptimistic(stock);

        //创建订单
        int id = createOrder(stock);

        return id;
    }

    @Override
    public int createOptimisticOrderUseRedis(int sid) throws Exception {
        //检验库存，从 Redis 获取
        Stock stock = checkStockByRedis(sid);

        //乐观锁更新库存 以及更新 Redis
        saleStockOptimisticByRedis(stock);

        //创建订单
        int id = createOrder(stock);
        return id ;
    }

    @Override
    public void createOptimisticOrderUseRedisAndKafka(int sid) throws Exception {

        // 检验库存，从 Redis 获取
        Stock stock = checkStockByRedis(sid);

        // 利用 Kafka 创建订单
        kafkaProducer.send(new ProducerRecord(kafkaTopic,stock)) ;
        logger.info("send Kafka success");

    }

    private Stock checkStockByRedis(int sid) throws Exception {
        Integer count = Integer.parseInt(redisTemplate.opsForValue().get(RedisKeysConstant.STOCK_COUNT + sid));
        Integer sale = Integer.parseInt(redisTemplate.opsForValue().get(RedisKeysConstant.STOCK_SALE + sid));
        if (count.equals(sale)){
            throw new RuntimeException("库存不足 Redis currentCount=" + sale);
        }
        Integer version = Integer.parseInt(redisTemplate.opsForValue().get(RedisKeysConstant.STOCK_VERSION + sid));
        Stock stock = new Stock() ;
        stock.setId(sid);
        stock.setCount(count);
        stock.setSale(sale);
        stock.setVersion(version);

        return stock;
    }

    /**
     * 乐观锁更新数据库 还要更新 Redis
     * @param stock
     */
    private void saleStockOptimisticByRedis(Stock stock) {
        int count = stockService.updateStockByOptimistic(stock);
        if (count == 0){
            throw new RuntimeException("并发更新库存失败") ;
        }
        // 自增
        redisTemplate.opsForValue().increment(RedisKeysConstant.STOCK_SALE + stock.getId(),1) ;
        redisTemplate.opsForValue().increment(RedisKeysConstant.STOCK_VERSION + stock.getId(),1) ;
    }

    private Stock checkStock(int sid) {
        Stock stock = stockService.getStockById(sid);
        if (stock.getSale().equals(stock.getCount())) {
            throw new RuntimeException("库存不足");
        }
        return stock;
    }

    private void saleStockOptimistic(Stock stock) {
        int count = stockService.updateStockByOptimistic(stock);
        if (count == 0){
            throw new RuntimeException("并发更新库存失败") ;
        }
    }


    private int createOrder(Stock stock) {
        StockOrder order = new StockOrder();
        order.setSid(stock.getId());
        order.setName(stock.getName());
        return orderMapper.insertSelective(order);
    }

    private int saleStock(Stock stock) {
        stock.setSale(stock.getSale() + 1);
        return stockService.updateStockById(stock);
    }
}
```

```java
public class ConsumerGroup {
    private static Logger LOGGER = LoggerFactory.getLogger(ConsumerGroup.class);
    /**
     * 线程池
     */
    private ExecutorService threadPool;

    private List<ConsumerTask> consumers ;

    public ConsumerGroup(int threadNum, String groupId, String topic, String brokerList) {
        LOGGER.info("kafka parameter={},{},{},{}",threadNum,groupId,topic,brokerList);
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("consumer-pool-%d").build();

        threadPool = new ThreadPoolExecutor(threadNum, threadNum,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());


        consumers = new ArrayList<ConsumerTask>(threadNum);
        for (int i = 0; i < threadNum; i++) {
            ConsumerTask consumerThread = new ConsumerTask(brokerList, groupId, topic);
            consumers.add(consumerThread);
        }
    }

    /**
     * 执行任务
     */
    public void execute() {
        for (ConsumerTask runnable : consumers) {
            threadPool.submit(runnable) ;
        }
    }
}
```

```java
public class ConsumerTask implements Runnable {
    private static Logger LOGGER = LoggerFactory.getLogger(ConsumerTask.class);


    /**
     * 每个线程维护KafkaConsumer实例
     */
    private final KafkaConsumer<String, String> consumer;

    private Gson gson ;

    private OrderService orderService;

    public ConsumerTask(String brokerList, String groupId, String topic) {
        this.gson = SpringBeanFactory.getBean(Gson.class) ;
        this.orderService = SpringBeanFactory.getBean(OrderService.class) ;

        Properties props = new Properties();
        props.put("bootstrap.servers", brokerList);
        props.put("group.id", groupId);
        //自动提交位移
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");




        this.consumer = new KafkaConsumer<String, String>(props);
        consumer.subscribe(Arrays.asList(topic));
    }


    @Override
    public void run() {
        boolean flag = true;
        while (flag) {
            // 使用200ms作为获取超时时间
            ConsumerRecords<String, String> records = consumer.poll(200);

            for (ConsumerRecord<String, String> record : records) {
                // 简单地打印消息
                LOGGER.info("==="+record.value() + " consumed " + record.partition() +
                        " message with offset: " + record.offset());

                dealMessage(record.value()) ;
            }
        }


    }

    /**
    * 处理消息
     * @param value
     */
    private void dealMessage(String value) {
        try {

            Stock stock = gson.fromJson(value, Stock.class);
            LOGGER.info("consumer stock={}",JSON.toJSONString(stock));

            //创建订单
            orderService.createOptimisticOrderUseRedisAndKafka(stock);

        }catch (RejectedExecutionException e){
            LOGGER.error("rejected message = " + value);
        }catch (Exception e){
            LOGGER.error("unknown exception",e);
        }
    }
}
```

### 7.使用数学公式验证码
描述：点击秒杀前，先让用户输入数学公式验证码，验证正确才能进行秒杀。

好处：

    防止恶意的机器人和爬虫
    分散用户的请求
    
实现：

    前端通过把商品id作为参数调用服务端创建验证码接口
    服务端根据前端传过来的商品id和用户id生成验证码，并将商品id+用户id作为key，生成的验证码作为value存入redis，同时将生成的验证码输入图片写入imageIO让前端展示
    将用户输入的验证码与根据商品id+用户id从redis查询到的验证码对比，相同就返回验证成功，进入秒杀；不同或从redis查询的验证码为空都返回验证失败，刷新验证码重试

### 6.总结
* 尽量将请求拦截在上游。
* 还可以根据 `UID` 进行限流。
* 最大程度的减少请求落到 DB。
* 多利用缓存。
* 同步操作异步化。
* fail fast，尽早失败，保护应用


* 简单的 web 层，service 层( dubbo 通信)，数据库不做限制，出现超卖现象。
* 数据库采用乐观锁更新，库存总数放入 Redis(并不会经常更新)
* web 层，service 层分布式部署，提高吞吐量。
* web 层做限流，每个 UID 限制每秒 N 次请求，多余的请求直接过滤(不能将过多的请求到 DB)。
* service 层限流，先用单机限流，如令牌桶算法。再用 Redis 做全局限流。
* 异步创建订单，将创建订单用 `Kafka` 解耦。


### 
https://gitee.com/52itstyle/spring-boot-seckill

[淘宝大秒系统设计详解](https://blog.csdn.net/heyc861221/article/details/80122167)
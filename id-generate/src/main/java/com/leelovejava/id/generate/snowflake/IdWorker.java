package com.leelovejava.id.generate.snowflake;

/**
 * Twitter_Snowflake的java实现
 *
 * @author tianhao
 * @see 'https://mp.weixin.qq.com/s/dhQ8BCPKfQqMMm9QxpFwow'
 */
public class IdWorker {

    /**
     *
     * 漫画：什么是SnowFlake算法？
     * https://blog.csdn.net/bjweimengshu/article/details/80162731
     */

    /**
     * snowflake
     * 1).概述:
     * 分布式系统中，有一些需要使用全局唯一ID的场景，这种时候为了防止ID冲突可以使用36位的UUID，但是UUID有一些缺点，首先他相对比较长，另外UUID一般是无序的。
     *
     * 有些时候我们希望能使用一种简单一些的ID，并且希望ID能够按照时间有序生成。
     *
     * 而twitter的snowflake解决了这种需求，最初Twitter把存储系统从MySQL迁移到Cassandra，因为Cassandra没有顺序ID生成机制，所以开发了这样一套全局唯一ID生成服务
     *
     * 2).核心思想：
     *      使用一个64 bit的long型的数字作为全局唯一id，这64个bit中，其中1个bit是不用的，然后用其中的41 bit作为毫秒数，用10 bit作为工作机器id，12 bit作为序列号
     * 3).结构:
     * snowflake的结构如下(每部分用-分开):
     *
     * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000
     *
     * 第一位为未使用，接下来的41位为毫秒级时间(41位的长度可以使用69年)，然后是5位datacenterId和5位workerId(10位的长度最多支持部署1024个节点） ，最后12位是毫秒内的计数（12位的计数顺序号支持每个节点每毫秒产生4096个ID序号）
     *
     * 一共加起来刚好64位，为一个Long型。(转换成字符串后长度最多19)
     *
     * snowflake生成的ID整体上按照时间自增排序，并且整个分布式系统内不会产生ID碰撞（由datacenter和workerId作区分），并且效率较高。经测试snowflake每秒能够产生26万个ID
     *
     * 上面第一个部分，是1个bit：0，这个是无意义的
     *
     * 上面第二个部分是41个bit：表示的是时间戳
     *
     * 上面第三个部分是5个bit：表示的是机房id，10001
     *
     * 上面第四个部分是5个bit：表示的是机器id，1 1001
     *
     * 上面第五个部分是12个bit：表示的序号，就是某个机房某台机器上这一毫秒内同时生成的id的序号，0000 00000000
     *
     * 3.1) bit：是不用的，为啥呢？
     *  因为二进制里第一个bit为如果是1，那么都是负数，但是我们生成的id都是正数，所以第一个bit统一都是0
     * 3.2) 41 bit：表示的是时间戳，单位是毫秒
     *  41 bit可以表示的数字多达2^41 - 1，也就是可以标识2 ^ 41 - 1个毫秒值，换算成年就是表示69年的时间
     * 3.3) 10 bit：记录工作机器id，代表的是这个服务最多可以部署在2^10台机器上，也就是1024台机器
     *              但是10 bit里5个bit代表机房id，5个bit代表机器id。意思就是最多代表2 ^ 5个机房（32个机房），每个机房里可以代表2 ^ 5个机器（32台机器）
     * 3.4) 12 bit：这个是用来记录同一个毫秒内产生的不同id
     * 12 bit可以代表的最大正整数是2 ^ 12 - 1 = 4096，也就是说可以用这个12bit代表的数字来区分同一个毫秒内的4096个不同的id
     *
     * 简单来说，你的某个服务假设要生成一个全局唯一id，那么就可以发送一个请求给部署了snowflake算法的系统，由这个snowflake算法系统来生成唯一id。
     *
     * 这个snowflake算法系统首先肯定是知道自己所在的机房和机器的，比如机房id = 17，机器id = 12。
     *
     * 接着snowflake算法系统接收到这个请求之后，首先就会用二进制位运算的方式生成一个64 bit的long型id，64个bit中的第一个bit是无意义的。
     *
     * 接着41个bit，就可以用当前时间戳（单位到毫秒），然后接着5个bit设置上这个机房id，还有5个bit设置上机器id。
     *
     * 最后再判断一下，当前这台机房的这台机器上这一毫秒内，这是第几个请求，给这次生成id的请求累加一个序号，作为最后的12个bit
     *
     * 这个算法可以保证说，一个机房的一台机器上，在同一毫秒内，生成了一个唯一的id。可能一个毫秒内会生成多个id，但是有最后12个bit的序号来区分开来。
     *
     *
     * 总之就是用一个64bit的数字中各个bit位来设置不同的标志位，区分每一个id
     */

    /**
     * 机器id
     */
    private long workerId;
    /**
     * 机房id
     */
    private long datacenterId;
    /**
     * 一毫秒内生成的多个id的最新序号
     */
    private long sequence;

    public IdWorker(long workerId, long datacenterId, long sequence) {

        // sanity check for workerId
        // 这儿不就检查了一下，要求就是你传递进来的机房id和机器id不能超过32，不能小于0
        if (workerId > maxWorkerId || workerId < 0) {

            throw new IllegalArgumentException(
                    String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }

        if (datacenterId > maxDatacenterId || datacenterId < 0) {

            throw new IllegalArgumentException(
                    String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        }

        this.workerId = workerId;
        this.datacenterId = datacenterId;
        this.sequence = sequence;
    }

    private long twepoch = 1288834974657L;

    private long workerIdBits = 5L;
    private long datacenterIdBits = 5L;

    /**
     * 二进制运算，就是5 bit最多只能有31个数字，也就是说机器id最多只能是32以内
     */
    private long maxWorkerId = -1L ^ (-1L << workerIdBits);

    /**
     * 5 bit最多只能有31个数字，机房id最多只能是32以内
     */
    private long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);
    private long sequenceBits = 12L;

    private long workerIdShift = sequenceBits;
    private long datacenterIdShift = sequenceBits + workerIdBits;
    private long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
    private long sequenceMask = -1L ^ (-1L << sequenceBits);

    private long lastTimestamp = -1L;

    public long getWorkerId() {
        return workerId;
    }

    public long getDatacenterId() {
        return datacenterId;
    }

    public long getTimestamp() {
        return System.currentTimeMillis();
    }

    /**
     * 这个是核心方法，通过调用nextId()方法，让当前这台机器上的snowflake算法程序生成一个全局唯一的id
     *
     * @return
     */
    public synchronized long nextId() {

        // 这儿就是获取当前时间戳，单位是毫秒
        long timestamp = timeGen();

        if (timestamp < lastTimestamp) {
            System.err.printf(
                    "clock is moving backwards. Rejecting requests until %d.", lastTimestamp);
            throw new RuntimeException(
                    String.format("Clock moved backwards. Refusing to generate id for %d milliseconds",
                            lastTimestamp - timestamp));
        }


        // 下面是说假设在同一个毫秒内，又发送了一个请求生成一个id
        // 这个时候就得把seqence序号给递增1，最多就是4096
        if (lastTimestamp == timestamp) {

            // 这个意思是说一个毫秒内最多只能有4096个数字，无论你传递多少进来，
            //这个位运算保证始终就是在4096这个范围内，避免你自己传递个sequence超过了4096这个范围
            sequence = (sequence + 1) & sequenceMask;

            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }

        } else {
            sequence = 0;
        }

        // 这儿记录一下最近一次生成id的时间戳，单位是毫秒
        lastTimestamp = timestamp;

        // 这儿就是最核心的二进制位运算操作，生成一个64bit的id
        // 先将当前时间戳左移，放到41 bit那儿；将机房id左移放到5 bit那儿；将机器id左移放到5 bit那儿；将序号放最后12 bit
        // 最后拼接起来成一个64 bit的二进制数字，转换成10进制就是个long型
        return ((timestamp - twepoch) << timestampLeftShift) |
                (datacenterId << datacenterIdShift) |
                (workerId << workerIdShift) | sequence;
    }

    private long tilNextMillis(long lastTimestamp) {

        long timestamp = timeGen();

        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }

    //---------------测试---------------
    public static void main(String[] args) {

        IdWorker worker = new IdWorker(1, 1, 1);

        for (int i = 0; i < 30; i++) {
            System.out.println(worker.nextId());
        }
    }


    /**
     * snowflake算法一个小小的改进思路
     *
     * 其实在实际的开发中，这个snowflake算法可以做一点点改进。
     *
     * 因为大家可以考虑一下，我们在生成唯一id的时候，一般都需要指定一个表名，比如说订单表的唯一id。
     *
     * 所以上面那64个bit中，代表机房的那5个bit，可以使用业务表名称来替代，比如用00001代表的是订单表。
     *
     * 因为其实很多时候，机房并没有那么多，所以那5个bit用做机房id可能意义不是太大。
     *
     * 这样就可以做到，snowflake算法系统的每一台机器，对一个业务表，在某一毫秒内，可以生成一个唯一的id，一毫秒内生成很多id，用最后12个bit来区分序号对待
     */
}
package zk;

import org.apache.zookeeper.*;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * zkClient
 *
 * @author tianhao
 */
public class SimpleZkClientTest {
    /**
     * 连接字符串
     */
    private static final String connectString = "192.168.9.232:2181";
    /**
     * 超时时间 2秒
     */
    private static final int sessionTimeout = 2000;

    private ZooKeeper zkClient = null;

    @Before
    public void init() throws Exception {
        zkClient = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                // 收到事件通知后的回调函数（应该是我们自己的事件处理逻辑）
                System.out.println(event.getType() + "---" + event.getPath());
                try {
                    zkClient.getChildren("/", true);
                } catch (Exception e) {
                }
            }
        });

    }

    /**
     * 数据的增删改查
     *
     * @throws InterruptedException
     * @throws KeeperException
     */

    /**
     * 创建数据节点到zk中
     *
     * @throws KeeperException
     * @throws InterruptedException
     */
    @Test
    public void testCreate() throws KeeperException, InterruptedException {
        // 参数1：要创建的节点的路径 参数2：节点大数据 参数3：节点的权限 参数4：节点的类型
        String nodeCreated = zkClient.create("/idea", "hellozk".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        //上传的数据可以是任何类型，但都要转成byte[]
        System.out.println(nodeCreated);
    }

    /**
     * 判断znode是否存在
     *
     * @throws Exception
     */
    @Test
    public void testExist() throws Exception {
        Stat stat = zkClient.exists("/idea", false);
        System.out.println(stat == null ? "not exist" : "exist");


    }

    /**
     * 获取子节点
     *
     * @throws Exception
     */
    @Test
    public void getChildren() throws Exception {
        List<String> children = zkClient.getChildren("/", true);
        for (String child : children) {
            System.out.println(child);
        }
        Thread.sleep(Long.MAX_VALUE);
    }

    /**
     * 获取znode的数据
     *
     * @throws Exception
     */
    @Test
    public void getData() throws Exception {

        byte[] data = zkClient.getData("/idea", false, null);
        System.out.println(new String(data));

    }

    /**
     * 删除znode
     *
     * @throws Exception
     */
    @Test
    public void deleteZnode() throws Exception {

        //参数2：指定要删除的版本，-1表示删除所有版本
        zkClient.delete("/idea", -1);


    }

    /**
     * 删除znode
     *
     * @throws Exception
     */
    @Test
    public void setData() throws Exception {
        ///zkClient.create("/app1", "app1".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        zkClient.setData("/app1", "imissyou angelababy".getBytes(), -1);

        byte[] data = zkClient.getData("/app1", false, null);
        System.out.println(new String(data));

    }


}

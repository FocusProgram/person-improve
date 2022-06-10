package com.example.zookeeper.client;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @Author Mr.Kong
 * @Description Zookeeper客户端连接
 * @Date 2020/3/9 13:52
 */
public class Connect {

    //连接地址
    private static final String IP_ADDRESS = "192.168.80.130:2181";

    //延时时长
    private static final int SESSIN_TIME_OUT = 2000;

    //并发信号量，初始值为1
    private static final CountDownLatch countDownLatch = new CountDownLatch(1);

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {

        ZooKeeper zooKeeper = new ZooKeeper(IP_ADDRESS, SESSIN_TIME_OUT, new Watcher() {
            //监听事件
            @Override
            public void process(WatchedEvent watchedEvent) {
                // 获取事件状态
                Event.KeeperState keeperState = watchedEvent.getState();
                // 获取事件类型
                Event.EventType eventType = watchedEvent.getType();

                if (Event.KeeperState.SyncConnected == keeperState) {
                    if (Event.EventType.None == eventType) {
                        countDownLatch.countDown();
                        System.out.println("开启连接............");
                    }
                }
            }
        });

        // 通过信号量检测，当countDownLatch的值为0时才能执行以下代码(保证客户端成功连接服务器端后执行)
        countDownLatch.await();
        // 创建节点
        /**
         * EPHEMERAL 临时节点
         * EPHEMERAL_SEQUENTIAL 临时节点，当临时节点存在时会自动新增
         * PERSISTENT 永久节点
         * PERSISTENT_SEQUENTIAL 永久节点，当永久节点存在时会自动新增
         **/
        String result = zooKeeper.create("/kongqi", "name".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL);
        System.out.println("result:" + result);
        Thread.sleep(1000 * 10);
        zooKeeper.close();
    }

}

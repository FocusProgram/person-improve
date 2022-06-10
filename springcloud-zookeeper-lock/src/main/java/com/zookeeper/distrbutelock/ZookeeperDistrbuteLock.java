package com.zookeeper.distrbutelock;

import com.zookeeper.abstractlock.ZookeeperAbstractLock;
import org.I0Itec.zkclient.IZkDataListener;

import java.util.concurrent.CountDownLatch;

/**
 * @Auther: Mr.Kong
 * @Date: 2020/4/8 10:07
 * @Description:
 */
public class ZookeeperDistrbuteLock extends ZookeeperAbstractLock {

    @Override
    protected void waitLock() {

        IZkDataListener iZkDataListener = new IZkDataListener() {
            /**
             * 节点发生改变时事件通知
             * @param dataPath
             * @param data
             * @throws Exception
             */
            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {

            }

            /**
             * 节点删除是事件通知
             * @param dataPath
             * @throws Exception
             */
            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                System.out.println("删除的节点路径为" + dataPath);
                // 唤醒等待的线程
                if (countDownLatch != null) {
                    countDownLatch.countDown();
                }
            }
        };

        //注册监听事件
        zkClient.subscribeDataChanges(PATH, iZkDataListener);

        if (zkClient.exists(PATH)) {
            countDownLatch = new CountDownLatch(1);
            try {
                countDownLatch.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //删除监听事件
        zkClient.unsubscribeDataChanges(PATH, iZkDataListener);

    }

    @Override
    protected boolean tryLock() {
        try {
            zkClient.createEphemeral(PATH);
            System.out.println("获取lock锁成功");
            return true;
        } catch (Exception e) {
            System.out.println("获取lock锁失败");
            return false;
        }
    }
}

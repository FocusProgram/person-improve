package com.example.zookeeperlock;

import org.I0Itec.zkclient.IZkDataListener;

import java.util.concurrent.CountDownLatch;

/**
 * @Author Mr.Kong
 * @Description
 * @Date 2020/3/9 15:56
 */
public class ZookeeperDistrbuteLock extends ZookeeperAbstractLock {

    @Override
    boolean tryLock() {
        try {
            // 创建节点成功返回true
            zkClient.createEphemeral(lockPath);
            return true;
        } catch (Exception e) {
            // 创建节点成功返回false
            return false;
        }
    }

    @Override
    void waitLock() {

        // 使用zk临时事件监听
        IZkDataListener iZkDataListener = new IZkDataListener() {

            // 节点被删除时通知
            @Override
            public void handleDataDeleted(String path) throws Exception {
<<<<<<< HEAD
                System.out.println(path+"节点被删除时通知");
=======
                System.out.println(path + "节点删除通知");
>>>>>>> f44ca4a16d2de84d5d0a97d2f3e0848294094950
                if (countDownLatch != null) {
                    countDownLatch.countDown();
                }
            }

            // 节点被改变时通知
            @Override
            public void handleDataChange(String arg0, Object arg1) throws Exception {

            }
        };
        // 注册事件通知
        zkClient.subscribeDataChanges(lockPath, iZkDataListener);
        if (zkClient.exists(lockPath)) {
            countDownLatch = new CountDownLatch(1);
            try {
                countDownLatch.await();
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        // 监听完毕后，移除事件通知
        zkClient.unsubscribeDataChanges(lockPath, iZkDataListener);
    }

}

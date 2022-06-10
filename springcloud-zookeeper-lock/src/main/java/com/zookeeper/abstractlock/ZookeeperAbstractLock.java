package com.zookeeper.abstractlock;

import com.zookeeper.lock.CustomLock;
import org.I0Itec.zkclient.ZkClient;

import java.util.concurrent.CountDownLatch;

/**
 * @Auther: Mr.Kong
 * @Date: 2020/4/8 09:59
 * @Description: 基于模板方法设计模式实现分布式锁
 */
public abstract class ZookeeperAbstractLock implements CustomLock {

    // zk连接地址
    private String CONNECTSTRING = "114.55.34.44:2181";

    // 创建zk连接
    protected ZkClient zkClient = new ZkClient(CONNECTSTRING);

    // zk节点创建路径目录
    protected String PATH = "/lock";

    // 通过定义计数器标识创建临时节点状态
    protected CountDownLatch countDownLatch = new CountDownLatch(1);

    /**
     * 获取锁
     */
    @Override
    public void getLock() {
        if (tryLock()) {
            System.out.println("获取lock锁的资源");
        } else {
            // 等待
            waitLock();
            // 重新获取锁资源
            getLock();
        }

    }

    /**
     * 释放锁
     */
    @Override
    public void unLock() {
        if (zkClient != null) {
            zkClient.close();
            System.out.println("释放lock锁资源");
        }
    }

    protected abstract void waitLock();

    protected abstract boolean tryLock();
}

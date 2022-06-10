package com.example.zookeeperlock;

import org.I0Itec.zkclient.ZkClient;

import java.util.concurrent.CountDownLatch;

/**
 * @Author Mr.Kong
 * @Description 基于模板方法设计模式将重复的代码实现
 * @Date 2020/3/9 15:56
 */
public abstract class ZookeeperAbstractLock implements ExtLock {
    // 集群连接地址
    protected String CONNECTION = "114.55.34.44:2181";

    // zk客户端连接
    protected ZkClient zkClient = new ZkClient(CONNECTION);

    // path路径
    protected String lockPath = "/path";

    // 信号量
    protected CountDownLatch countDownLatch = new CountDownLatch(1);

    // 获取锁
    abstract boolean tryLock();

    // 等待锁
    abstract void waitLock();

    // 获取锁
	@Override
	public void getLock() {
		// 实现步骤：
		// 1.连接Zookeeper,创建lock临时节点
		// 2.如果节点创建成功，执行业务逻辑；如果节点创建失败，进行等待
		// 3.使用事件监听机制，进行节点监听，如果节点被删除，则重新进入获取锁资源
		if (tryLock()) {
			System.out.println("####获取锁成功######");
		} else {
			waitLock();
			getLock();
		}
	}

	// 释放锁
    @Override
    public void unLock() {
        if (zkClient != null) {
            System.out.println("------->释放锁成功<-------");
            zkClient.close();
        }
    }

}

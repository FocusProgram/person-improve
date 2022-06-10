package com.zookeeper.lock;

/**
 * @Auther: Mr.Kong
 * @Date: 2020/4/8 09:52
 * @Description: 自定义分布式锁的接口
 */
public interface CustomLock {

    /**
     * 获取锁
     */
    public void getLock();

    /**
     * 释放锁
     */
    public void unLock();
}

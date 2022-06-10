package com.example.zookeeperlock;

/**
 * @Author Mr.Kong
 * @Description
 * @Date 2020/3/9 15:56
 */
public interface ExtLock {

    // 获取锁
    public void getLock();

    // 释放锁
    public void unLock();
}

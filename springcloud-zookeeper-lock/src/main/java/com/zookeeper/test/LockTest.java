package com.zookeeper.test;

import com.zookeeper.service.OrderService;

/**
 * @Auther: Mr.Kong
 * @Date: 2020/4/8 10:33
 * @Description: 多线程模拟生成订单号使用Zookeeper分布式锁实现
 */
public class LockTest {

    /**
     * 根据cpu的数量动态的配置核心线程数和最大线程数
     */
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    /**
     * 核心线程数 = CPU核心数 + 1
     */
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    /**
     * 线程池最大线程数 = CPU核心数 * 2 + 1
     */
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    /**
     * 非核心线程闲置时超时1s
     */
    private static final int KEEP_ALIVE = 1;

    public static void main(String[] args) {

        System.out.println("开始生成订单号......");
        long begin = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            new Thread(new OrderService()).start();
        }
        long end = System.currentTimeMillis();
        long result = end - begin;
        System.out.println("执行消耗时长：" + result / 1000 + "s");
    }
}

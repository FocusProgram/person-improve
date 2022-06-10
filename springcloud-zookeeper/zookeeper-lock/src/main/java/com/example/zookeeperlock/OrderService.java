package com.example.zookeeperlock;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author Mr.Kong
 * @Description
 * @Date 2020/3/9 15:56
 */
public class OrderService implements Runnable {

    private OrderNumGenerator orderNumGenerator = new OrderNumGenerator();

    private ExtLock extLock = new ZookeeperDistrbuteLock();

    @Override
    public void run() {
        getNumber();
    }

    public void getNumber() {
        try {
            extLock.getLock();
            String number = orderNumGenerator.getNumber();
            System.out.println("线程:" + Thread.currentThread().getName() + ",生成订单id:" + number);
        } catch (Exception e) {

        } finally {
            extLock.unLock();
        }
    }

    public static void main(String[] args) {
        System.out.println("多线程生成number开始：");
//        只能创建一个Zookeeper连接
//        OrderService orderService = new OrderService();
//        for (int i = 0; i < 10; i++) {
//            new Thread(new OrderService()).start();
//        }

        ExecutorService executorService = Executors.newFixedThreadPool(100);
        for (int i = 0; i < 10; i++) {
            executorService.execute(new OrderService());
        }

    }

}

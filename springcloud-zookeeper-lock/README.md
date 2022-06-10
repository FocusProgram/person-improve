**分布式锁-基于Zookeeper实现**

---

- **文章目录**

* [1\. Zookeeper安装部署](#1-zookeeper%E5%AE%89%E8%A3%85%E9%83%A8%E7%BD%B2)
  * [1\.1 安装部署参考文档](#11-%E5%AE%89%E8%A3%85%E9%83%A8%E7%BD%B2%E5%8F%82%E8%80%83%E6%96%87%E6%A1%A3)
  * [1\.2 Zookeeper客户端工具](#12-zookeeper%E5%AE%A2%E6%88%B7%E7%AB%AF%E5%B7%A5%E5%85%B7)
* [2\. 实现原理](#2-%E5%AE%9E%E7%8E%B0%E5%8E%9F%E7%90%86)
* [3\. 具体实现](#3-%E5%85%B7%E4%BD%93%E5%AE%9E%E7%8E%B0)
  * [3\.1 引入Gradle依赖](#31-%E5%BC%95%E5%85%A5gradle%E4%BE%9D%E8%B5%96)
  * [3\.2 具体业务实现](#32-%E5%85%B7%E4%BD%93%E4%B8%9A%E5%8A%A1%E5%AE%9E%E7%8E%B0)
    * [3\.2\.1 创建CustomLock接口](#321-%E5%88%9B%E5%BB%BAcustomlock%E6%8E%A5%E5%8F%A3)
    * [3\.2\.2 创建ZookeeperAbstractLock抽象类](#322-%E5%88%9B%E5%BB%BAzookeeperabstractlock%E6%8A%BD%E8%B1%A1%E7%B1%BB)
    * [3\.2\.3 创建ZookeeperDistrbuteLock抽象类](#323-%E5%88%9B%E5%BB%BAzookeeperdistrbutelock%E6%8A%BD%E8%B1%A1%E7%B1%BB)
    * [3\.2\.4 使用Zookeeper实现分布式锁](#324-%E4%BD%BF%E7%94%A8zookeeper%E5%AE%9E%E7%8E%B0%E5%88%86%E5%B8%83%E5%BC%8F%E9%94%81)
    * [3\.2\.5 源码参考地址](#325-%E6%BA%90%E7%A0%81%E5%8F%82%E8%80%83%E5%9C%B0%E5%9D%80)

# 1. Zookeeper安装部署

## 1.1 安装部署参考文档

分布式注册中心-Zookeeper： [https://blog.csdn.net/qq_41112063/article/details/105312845](https://blog.csdn.net/qq_41112063/article/details/105312845)

## 1.2 Zookeeper客户端工具

下载地址：[https://issues.apache.org/jira/secure/attachment/12436620/ZooInspector.zip](https://issues.apache.org/jira/secure/attachment/12436620/ZooInspector.zip)

# 2. 实现原理

- 多个JVM同时在根节点下创建临时节点（/lock）
- 创建临时节点（/lock）成功,则获取锁成功，执行响应的逻辑代码
- 创建临时节点（/lock）失败,则获取锁失败，通过订阅节点删除通知事件，如果节点删除则重新获取锁，否则就一直等待
- 执行响应业务逻辑完成后关闭连接，临时节点删除，锁释放

# 3. 具体实现

## 3.1 引入Gradle依赖

```
dependencies {
    compile group: 'com.101tec', name: 'zkclient', version: '0.11'
}
```

## 3.2 具体业务实现

### 3.2.1 创建CustomLock接口

```
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
```

### 3.2.2 创建ZookeeperAbstractLock抽象类

```
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
```

### 3.2.3 创建ZookeeperDistrbuteLock抽象类

```
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
```

### 3.2.4 使用Zookeeper实现分布式锁

```
public class NumberGenerator {

    // 生成订单号规则
    private static int count = 0;

    public String getNumber() {
//        try {
//            Thread.sleep(200);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        return simpleDateFormat.format(new Date()) + "-" + ++count;
    }
}
```

```
public class OrderService implements Runnable {

    private NumberGenerator numberGenerator = new NumberGenerator();

    private CustomLock customLock = new ZookeeperDistrbuteLock();

    @Override
    public void run() {
        getNumber();
    }

    public void getNumber() {
        try {
            customLock.getLock();
            String number = numberGenerator.getNumber();
            System.out.println(Thread.currentThread().getName() + "生成订单号：" + number);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            customLock.unLock();
        }
    }
}
```

```
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
```

### 3.2.5 源码参考地址

[https://github.com/FocusProgram/person-improve/tree/main/springcloud-zookeeper-lock](https://github.com/FocusProgram/person-improve/tree/main/springcloud-zookeeper-lock)

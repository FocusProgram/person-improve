package com.zookeeper.test;

import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.ZooDefs;

/**
 * @Auther: Mr.Kong
 * @Date: 2020/4/8 14:21
 * @Description: Zookeeper常用API
 */
public class ZookeeperApiTest {
    public static void main(String[] args) {

        // zk连接地址
        String CONNECTSTRING = "114.55.34.44:2181";

        // 创建zk连接
        ZkClient zkClient = new ZkClient(CONNECTSTRING, 5000);

        /**
         * ACL权限列表
         * ZooDefs.Ids.OPEN_ACL_UNSAFE 完全开放
         * ZooDefs.Ids.ANYONE_ID_UNSAFE 任何人可以访问
         * ZooDefs.Ids.CREATOR_ALL_ACL 创建该node的连接拥有所有权限
         * ZooDefs.Ids.READ_ACL_UNSAFE 所有客户端可读
         * ZooDefs.Ids.AUTH_IDS 创建者拥有访问权限
         */

        // zkClient.createEphemeral() 创建临时节点
        // zkClient.createEphemeralSequential() 创建临时顺序节点
        // zkClient.createPersistent() 创建持久节点
        // zkClient.createPersistentSequential() 创建持久顺序节点

        /**
         * 创建临时节点
         */
//        zkClient.createEphemeral("/kongqi", "年龄24", ZooDefs.Ids.OPEN_ACL_UNSAFE);

        /**
         * 创建临时顺序节点
         */
//        zkClient.createEphemeralSequential("/kongqi/ege", "年龄24",ZooDefs.Ids.OPEN_ACL_UNSAFE);


        /**
         * 创建持久节点
         */
//        zkClient.createPersistent("/kongqi");
//        zkClient.createPersistent("/kongqi/name","Mr.Kong",ZooDefs.Ids.OPEN_ACL_UNSAFE);
//        zkClient.createPersistent("/kongqi/age","23",ZooDefs.Ids.OPEN_ACL_UNSAFE);

        /**
         * 创建持久顺序节点
         */
        if (!zkClient.exists("/kongqi")) {
            zkClient.createPersistent("/kongqi");
        }
        zkClient.createPersistentSequential("/kongqi/name", "Mr.Kong", ZooDefs.Ids.OPEN_ACL_UNSAFE);
        zkClient.createPersistentSequential("/kongqi/age", "23", ZooDefs.Ids.OPEN_ACL_UNSAFE);
    }
}

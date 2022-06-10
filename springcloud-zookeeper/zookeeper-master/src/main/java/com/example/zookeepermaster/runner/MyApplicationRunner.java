package com.example.zookeepermaster.runner;

import com.example.zookeepermaster.master.ElectionMaster;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @Author Mr.Kong
 * @Description
 * @Date 2020/3/9 17:00
 */
@Component
public class MyApplicationRunner implements ApplicationRunner {
    private ZkClient zkClient = new ZkClient("192.168.80.130:2181");

    private String path = "/election";

    @Value("${server.port}")
    private String serverPort;

    // 启动后执行方法
    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("项目启动成功...");
        // 1.项目启动的时候会在zk上创建一个相同的临时节点
        // 2.谁能够创建成功谁就是为主服务器
        createEphemeral();
        // 3.使用服务监听节点是否被删除，如果接受到节点被删除的话，重新开始选举（重新开始创建节点）
        zkClient.subscribeDataChanges(path, new IZkDataListener() {
            // 节点如果被删除后 ，返回通知
            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                System.out.println("开始重新选举策略。。。");
                createEphemeral();
            }
            /// 当节点值发生变化的之后，事件通知
            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {

            }
        });
    }

    private void createEphemeral() {
        try {
            zkClient.createEphemeral(path);
            System.out.println("serverPort:" + serverPort + "，选举成功....");
            ElectionMaster.isSurvival = true;
        } catch (Exception e) {
            System.out.println("该节点已经存在");
            ElectionMaster.isSurvival = false;
        }
    }
}

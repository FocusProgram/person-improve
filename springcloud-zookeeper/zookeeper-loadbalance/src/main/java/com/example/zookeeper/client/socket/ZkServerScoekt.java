package com.example.zookeeper.client.socket;

import org.I0Itec.zkclient.ZkClient;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Author Mr.Kong
 * @Description 实现socket服务端
 * @Date 2020/3/9 14:55
 */
public class ZkServerScoekt implements Runnable {

    private static int port = 8002;

    public static void main(String[] args) throws IOException {

        ZkServerScoekt server = new ZkServerScoekt(port);
        Thread thread = new Thread(server);
        thread.start();
    }

    public ZkServerScoekt(int port) {
        ZkServerScoekt.port = port;
    }

    // 启动注册服务
    private void regServer() {
        ZkClient zkClient = new ZkClient("192.168.80.130:2181", 6000, 1000);
        if(!zkClient.exists("/socket")){
            zkClient.createPersistent("/socket");
        }
        String path = "/socket/server" + port;
        if (zkClient.exists(path)) {
            zkClient.delete(path);
        }
        // 创建临时节点
        zkClient.createEphemeral(path, "127.0.0.1:" + port);
    }

    @Override
    public void run() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            regServer();
            System.out.println("Server start port:" + port);
            Socket socket = null;
            while (true) {
                socket = serverSocket.accept();
                new Thread(new ServerHandler(socket)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (Exception e2) {

            }
        }
    }

}

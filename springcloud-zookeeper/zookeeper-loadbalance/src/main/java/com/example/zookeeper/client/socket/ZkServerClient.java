package com.example.zookeeper.client.socket;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author Mr.Kong
 * @Description 实现socket客户端
 * @Date 2020/3/9 14:53
 */
public class ZkServerClient{

    public static List<String> listServer = new ArrayList<String>();

    public static String parent = "/socket";

    public static void main(String[] args) {
        initServer();
        ZkServerClient client = new ZkServerClient();
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String name;
            try {
                name = console.readLine();
                if ("exit".equals(name)) {
                    System.exit(0);
                }
                client.send(name);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 注册所有server
    public static void initServer() {

        final ZkClient zkClient = new ZkClient("192.168.80.130:2181", 6000, 1000);
        List<String> children = zkClient.getChildren(parent);
        getChilds(zkClient, children);
        // 监听事件
        zkClient.subscribeChildChanges(parent, new IZkChildListener() {

            @Override
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                getChilds(zkClient, currentChilds);
            }
        });
    }

    private static void getChilds(ZkClient zkClient, List<String> currentChilds) {
        listServer.clear();
        for (String p : currentChilds) {
            String pathValue = (String) zkClient.readData(parent + "/" + p);
            listServer.add(pathValue);
        }
        serverCount = listServer.size();
        System.out.println("从zk读取到信息:" + listServer.toString());

    }

    // 请求次数
    private static int reqestCount = 1;
    // 服务数量
    private static int serverCount = 0;

    // 获取当前server信息
    public static String getServer() {
        // 实现负载均衡
        String serverName = listServer.get(reqestCount % serverCount);
        ++reqestCount;
        System.out.println("当前负载均衡轮询节点为"+serverName);
        return serverName;
    }

    public void send(String name) {
        String server = ZkServerClient.getServer();
        String[] cfg = server.split(":");
        Socket socket = null;
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            socket = new Socket(cfg[0], Integer.parseInt(cfg[1]));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println(name);
            while (true) {
                String resp = in.readLine();
                if (resp == null)
                    break;
                else if (resp.length() > 0) {
                    System.out.println("Receive : " + resp);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}

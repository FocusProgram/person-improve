package com.example.zookeepermaster.controller;

import com.example.zookeepermaster.master.ElectionMaster;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Mr.Kong
 * @Description
 * @Date 2020/3/9 16:56
 */
@RestController
public class PageController {

    @Value("${server.port}")
    private String serverPort;

    @RequestMapping("/getServerInfo")
    public String getServerInfo() {
        return "serverPort:" + serverPort + (ElectionMaster.isSurvival ? "选举为主服务器" : "该服务器为从服务器");
    }
}

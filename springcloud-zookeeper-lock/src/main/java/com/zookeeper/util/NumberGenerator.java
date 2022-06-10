package com.zookeeper.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Auther: Mr.Kong
 * @Date: 2020/4/8 10:26
 * @Description: 生成订单号
 */
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

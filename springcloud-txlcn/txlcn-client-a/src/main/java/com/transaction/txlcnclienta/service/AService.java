package com.transaction.txlcnclienta.service;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.transaction.txlcnclienta.client.BFeign;
import com.transaction.txlcnclienta.mapper.AMapper;
import com.transaction.txlcnclienta.pojo.Demo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Auther: Mr.Kong
 * @Date: 2020/3/20 15:51
 * @Description:
 */
@Service
public class AService {

    @Autowired
    private AMapper aMapper;

    @Autowired
    private BFeign bFeign;

    @Transactional
    @LcnTransaction
    public void save(Demo entity) {
        // 调用本地事务
        aMapper.save(entity);
//        int i = 1/0; // 这里抛出异常属于本地事务需要加@Transactional注解

        // 调用分布式事务，调用B服务的BService.save()方法
        // 此方法若抛出异常，使用@LcnTransaction注解就可以使上面数据回滚
        bFeign.save();
    }
}
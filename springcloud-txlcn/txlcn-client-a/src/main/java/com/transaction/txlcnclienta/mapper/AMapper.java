package com.transaction.txlcnclienta.mapper;

import com.transaction.txlcnclienta.pojo.Demo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Auther: Mr.Kong
 * @Date: 2020/3/20 15:49
 * @Description:
 */
@Mapper
public interface AMapper {


    @Insert("insert into t_demo(kid, demo_field, group_id, create_time,app_name) values(#{kid}, #{demoField}, #{groupId}, #{createTime},#{appName})")
    void save(Demo demo);

}

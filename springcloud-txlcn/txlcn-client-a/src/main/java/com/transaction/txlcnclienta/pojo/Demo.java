package com.transaction.txlcnclienta.pojo;

import com.codingapi.txlcn.common.util.id.RandomUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Auther: Mr.Kong
 * @Date: 2020/3/20 15:47
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Demo {
    private Long id;
    private String kid = RandomUtils.randomKey();
    private String demoField;
    private String groupId;
    private Date createTime;
    private String appName;
}

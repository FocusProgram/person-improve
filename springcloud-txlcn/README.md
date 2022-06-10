**TX-LCN**

---

- **文章目录**

* [1\. TX\-LCN是什么？](#1-tx-lcn%E6%98%AF%E4%BB%80%E4%B9%88)
* [2\. 框架定位](#2-%E6%A1%86%E6%9E%B6%E5%AE%9A%E4%BD%8D)
* [3\. 解决方案](#3-%E8%A7%A3%E5%86%B3%E6%96%B9%E6%A1%88)
* [4\. SpringCloud集成TX\-LCN](#4-springcloud%E9%9B%86%E6%88%90tx-lcn)
  * [4\.1 TX\-LCN模块说明](#41-tx-lcn%E6%A8%A1%E5%9D%97%E8%AF%B4%E6%98%8E)
  * [4\.2 TM 配置与启动](#42-tm-%E9%85%8D%E7%BD%AE%E4%B8%8E%E5%90%AF%E5%8A%A8)
    * [4\.2\.1 所需配置](#421-%E6%89%80%E9%9C%80%E9%85%8D%E7%BD%AE)
    * [4\.2\.2 导入数据库脚本](#422-%E5%AF%BC%E5%85%A5%E6%95%B0%E6%8D%AE%E5%BA%93%E8%84%9A%E6%9C%AC)
    * [4\.2\.3 TM版本下载](#423-tm%E7%89%88%E6%9C%AC%E4%B8%8B%E8%BD%BD)
    * [4\.2\.4 源码编译](#424-%E6%BA%90%E7%A0%81%E7%BC%96%E8%AF%91)
    * [4\.2\.4 启动](#424-%E5%90%AF%E5%8A%A8)
  * [4\.3 TC 微服务模块](#43-tc-%E5%BE%AE%E6%9C%8D%E5%8A%A1%E6%A8%A1%E5%9D%97)
    * [4\.3\.1 微服务模块](#431-%E5%BE%AE%E6%9C%8D%E5%8A%A1%E6%A8%A1%E5%9D%97)
    * [4\.3\.2 引入maven依赖](#432-%E5%BC%95%E5%85%A5maven%E4%BE%9D%E8%B5%96)
    * [4\.3\.3 TC开启分布式事务注解](#433-tc%E5%BC%80%E5%90%AF%E5%88%86%E5%B8%83%E5%BC%8F%E4%BA%8B%E5%8A%A1%E6%B3%A8%E8%A7%A3)
  * [4\.4 <a href="https://github.com/FocusProgram/person-improve/tree/main/springcloud-txlcn">springcloud继承tx\-lcn源码</a>](#44-springcloud%E7%BB%A7%E6%89%BFtx-lcn%E6%BA%90%E7%A0%81)

# 1. TX-LCN是什么？

> [TX-LCN](https://github.com/codingapi/tx-lcn) 框架在2017年6月份发布第一个版本，从开始的1.0，已经发展到了5.0版本。
> LCN名称是由早期版本的LCN框架命名，在设计框架之初的1.0 ~ 2.0的版本时框架设计的步骤是如下,各取其首字母得来的LCN命名。
>
> 锁定事务单元（lock）
> 确认事务模块状态(confirm)
> 通知事务(notify)
>
> 5.0以后由于框架兼容了LCN、TCC、TXC三种事务模式，为了避免区分LCN模式，特此将LCN分布式事务改名为TX-LCN分布式事务框架。

# 2. 框架定位

> LCN并不生产事务，LCN只是本地事务的协调工
>
> TX-LCN定位于一款事务协调性框架，框架其本身并不操作事务，而是基于对事务的协调从而达到事务一致性的效果。

# 3. 解决方案

> 在一个分布式系统下存在多个模块协调来完成一次业务。那么就存在一次业务事务下可能横跨多种数据源节点的可能。TX-LCN将可以解决这样的问题。
>
> 例如存在服务模块A 、B、 C。A模块是mysql作为数据源的服务，B模块是基于redis作为数据源的服务，C模块是基于mongo作为数据源的服务。若需要解决他们的事务一致性就需要针对不同的节点采用不同的方案，并且统一协调完成分布式事务的处理。

![](https://image.focusprogram.top/lcn.png)

> 方案：
>
> 若采用TX-LCN分布式事务框架，则可以将A模块采用LCN模式、B/C采用TCC模式就能完美解决。

# 4. SpringCloud集成TX-LCN

## 4.1 TX-LCN模块说明

> Tx-Client(TC) Tx-Manager(TM). TC作为微服务下的依赖，TM是独立的服务

## 4.2 TM 配置与启动

### 4.2.1 所需配置

> jdk1.8+
>
> mysql5.6+
>
> redis3.2+

### 4.2.2 导入数据库脚本

```
/*
 Navicat Premium Data Transfer

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 100309
 Source Host           : localhost:3306
 Source Schema         : tx-manager

 Target Server Type    : MySQL
 Target Server Version : 100309
 File Encoding         : 65001

 Date: 29/12/2018 18:35:59
*/
CREATE DATABASE IF NOT EXISTS  `tx-manager` DEFAULT CHARSET utf8 COLLATE utf8_general_ci;
USE `tx-manager`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_tx_exception
-- ----------------------------
DROP TABLE IF EXISTS `t_tx_exception`;
CREATE TABLE `t_tx_exception`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `group_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `unit_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `mod_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `transaction_state` tinyint(4) NULL DEFAULT NULL,
  `registrar` tinyint(4) NULL DEFAULT NULL,
  `ex_state` tinyint(4) NULL DEFAULT NULL COMMENT '0 待处理 1已处理',
  `remark` varchar(10240) NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 967 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

```

### 4.2.3 TM版本下载

[https://github.com/codingapi/tx-lcn/releases](https://github.com/codingapi/tx-lcn/releases)

### 4.2.4 源码编译

编辑配置文件/tx-lcn/txlcn-tm/src/main/resources/application.properties如下：

```
spring.application.name=TransactionManager
server.port=7970
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.url=jdbc:mysql://114.55.34.44:3306/tx-manager?characterEncoding=UTF-8
spring.datasource.username=root
spring.datasource.password=root

#spring.jpa.database-platform=org.hibernate.dialect.MySQL5InnoDBDialect
#spring.jpa.hibernate.ddl-auto=update


mybatis.configuration.map-underscore-to-camel-case=true
mybatis.configuration.use-generated-keys=true

#tx-lcn.logger.enabled=true
# TxManager Host Ip
#tx-lcn.manager.host=127.0.0.1
# TxClient连接请求端口
#tx-lcn.manager.port=8070
# 心跳检测时间(ms)
#tx-lcn.manager.heart-time=15000
# 分布式事务执行总时间
#tx-lcn.manager.dtx-time=30000
#参数延迟删除时间单位ms
#tx-lcn.message.netty.attr-delay-time=10000
#tx-lcn.manager.concurrent-level=128
# 开启日志
#tx-lcn.logger.enabled=true
#logging.level.com.codingapi=debug
#redis 主机
spring.redis.host=114.55.34.44
#redis 端口
spring.redis.port=6379
#redis 密码
spring.redis.password=root
```

注意：pom.xml文件中添加如下配置，否则无法正常运行

```
<build>
  <plugins>
  	<plugin>
  		<groupId>org.springframework.boot</groupId>
 		<artifactId>spring-boot-maven-plugin</artifactId>
  	</plugin>
  </plugins>
 </build>
```

编译：

```
mvn clean  package -Dmaven.test.skip=true
```

编译成功显示如下：

![](https://image.focusprogram.top/20200313201811.png)

### 4.2.4 启动

```
java -jar txlcn-tm-5.0.2.RELEASE.jar
```

成功启动，访问 [http://127.0.0.1:7970/admin/index.html#/login](http://127.0.0.1:7970/admin/index.html#/login) 默认密码 codingapi

![](https://image.focusprogram.top/20200313212742.jpg)

## 4.3 TC 微服务模块

### 4.3.1 微服务模块

![](https://image.focusprogram.top/quick_arch.png)

> 服务A作为DTX发起方，远程调用服务B

### 4.3.2 引入maven依赖

```
<dependency>
    <groupId>com.codingapi.txlcn</groupId>
    <artifactId>txlcn-tc</artifactId>
    <version>5.0.2.RELEASE</version>
</dependency>

<dependency>
    <groupId>com.codingapi.txlcn</groupId>
    <artifactId>txlcn-txmsg-netty</artifactId>
    <version>5.0.2.RELEASE</version>
</dependency>
```

### 4.3.3 TC开启分布式事务注解

在主类上使用@EnableDistributedTransaction

```
@SpringBootApplication
@EnableDistributedTransaction
public class DemoAApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoDubboClientApplication.class, args);
    }

}
```


TC微服务A业务方法配置

```
@Service
public class ServiceA {
    
    @Autowired
    private ValueDao valueDao; //本地db操作
    
    @Autowired
    private ServiceB serviceB;//远程B模块业务
    
    @LcnTransaction //分布式事务注解
    @Transactional //本地事务注解
    public String execute(String value) throws BusinessException {
        // step1. call remote service B
        String result = serviceB.rpc(value);  // (1)
        // step2. local store operate. DTX commit if save success, rollback if not.
        valueDao.save(value);  // (2)
        valueDao.saveBackup(value);  // (3)
        return result + " > " + "ok-A";
    }
}
```

TC微服务B业务方法配置

```
@Service
public class ServiceB {
    
    @Autowired
    private ValueDao valueDao; //本地db操作
    
    @LcnTransaction //分布式事务注解
    @Transactional  //本地事务注解
    public String rpc(String value) throws BusinessException {
        valueDao.save(value);  // (4)
        valueDao.saveBackup(value);  // (5)
        return "ok-B";
    }
}
```

TC配置信息说明
默认之配置为TM的本机默认端口

> tx-lcn.client.manager-address=127.0.0.1:8070 

## 4.4 [springcloud集成tx-lcn源码](https://github.com/FocusProgram/person-improve/tree/main/springcloud-txlcn)
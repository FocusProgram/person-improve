## quartz + springboot + httpclient

### 项目功能

```
1、springboot集成quartz，使用druid连接池 
2、支持http请求任务定时调度，当前支持get、post表单(formdata)、post Json三种请求类型，并记录返回内容
3、通过web界面进行任务管理，包括任务暂停、恢复、修改、历史记录、历史任务查看功能
4、支持调用接口(/quartz/httpJob/add)进行http任务添加  
5、根据jobname或jobgroup进行查询
```

### 部署方式

```
1、执行sql目录下的create_table.sql文件，建立quartz以及httpjob需要的数据库表  
2、修改application.properties中的数据库连接方式
3、访问http://localhost:8080/httpJob.html可通过web界面进行httpjob管理
```

### 组件版本

```
1、quartz 2.2.3
<dependency>
    <groupId>org.quartz-scheduler</groupId>
    <artifactId>quartz</artifactId>
    <version>2.2.3</version>
</dependency>
<dependency>
    <groupId>org.quartz-scheduler</groupId>
    <artifactId>quartz-jobs</artifactId>
    <version>2.2.3</version>
</dependency>

2、springboot 2.1.0
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.1.0.RELEASE</version>
    <relativePath/>
</parent>

3、jdbc 5.1.46
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>5.1.46</version>
    <scope>runtime</scope>
</dependency>

4、httpclient 4.5.6
<dependency>
    <groupId>org.apache.httpcomponents</groupId>
    <artifactId>httpclient</artifactId>
    <version>4.5.6</version>
</dependency>
```

### 注意事项

`1、调用接口添加http定时请求任务时，requestType(请求类型)必填，只支持填入"GET"、 "POST_JSON"、 "POST_FORM",分别对应get, post json和post form-data三种类型;`  
`2、调用接口添加http定时请求任务时，params(请求参数)选填，若填写，必须组装为合法的json字符串格式。`  

### demo

  HTTP请求任务管理  
  ![](https://image.focusprogram.top/20190909172137.png)  

  HTTP请求任务执行记录  
  ![](https://image.focusprogram.top/20190909172248.png)

  HTTP请求任务添加  
  ![](https://image.focusprogram.top/20190909172218.png)

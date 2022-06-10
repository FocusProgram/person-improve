**Spring Cloud Gateway**

---

- **文章目录**

* [1\. 什么是Spring Cloud Gateway?](#1-%E4%BB%80%E4%B9%88%E6%98%AFspring-cloud-gateway)
* [2\. 核心概念](#2-%E6%A0%B8%E5%BF%83%E6%A6%82%E5%BF%B5)
* [3\. 功能](#3-%E5%8A%9F%E8%83%BD)
* [4\. springcloud集成Spring Cloud Gateway](#4-springcloud%E9%9B%86%E6%88%90spring-cloud-gateway)
  * [4\.1 环境准备](#41-%E7%8E%AF%E5%A2%83%E5%87%86%E5%A4%87)
  * [4\.2 order\-service模块](#42-order-service%E6%A8%A1%E5%9D%97)
  * [4\.3 gateway模块](#43-gateway%E6%A8%A1%E5%9D%97)
    * [4\.3\.1 Gateway网关配置](#431-gateway%E7%BD%91%E5%85%B3%E9%85%8D%E7%BD%AE)
    * [4\.3\.2 Gateway跨域访问](#432-gateway%E8%B7%A8%E5%9F%9F%E8%AE%BF%E9%97%AE)
    * [4\.3\.3 Gateway过滤器](#433-gateway%E8%BF%87%E6%BB%A4%E5%99%A8)
    * [4\.3\.4 Gateway请求匹配](#434-gateway%E8%AF%B7%E6%B1%82%E5%8C%B9%E9%85%8D)
      * [4\.3\.4\.1 head匹配](#4341-head%E5%8C%B9%E9%85%8D)
      * [4\.3\.4\.2 Host匹配](#4342-host%E5%8C%B9%E9%85%8D)
      * [4\.3\.4\.3 请求方法匹配](#4343-%E8%AF%B7%E6%B1%82%E6%96%B9%E6%B3%95%E5%8C%B9%E9%85%8D)
    * [4\.3\.5 Gateway熔断Hystrix](#435-gateway%E7%86%94%E6%96%ADhystrix)
    * [4\.3\.6 Gateway重试路由](#436-gateway%E9%87%8D%E8%AF%95%E8%B7%AF%E7%94%B1)
    * [4\.3\.7 Gateway限流](#437-gateway%E9%99%90%E6%B5%81)
    * [4\.3\.8 Gateway自定义Gatewayfilter](#438-gateway%E8%87%AA%E5%AE%9A%E4%B9%89gatewayfilter)
    * [4\.3\.9 Gateway自定义GlobalFilter](#439-gateway%E8%87%AA%E5%AE%9A%E4%B9%89globalfilter)

# 1. 什么是Spring Cloud Gateway?

> [Spring cloud gateway](https://spring.io/projects/spring-cloud-gateway#overview) 是spring官方基于Spring 5.0、Spring Boot2.0和Project Reactor等技术开发的网关，Spring Cloud Gateway旨在为微服务架构提供简单、有效和统一的API路由管理方式，Spring Cloud Gateway作为Spring Cloud生态系统中的网关，目标是替代Netflix Zuul，其不仅提供统一的路由方式，并且还基于Filer链的方式提供了网关基本的功能，例如：安全、监控/埋点、限流等。

# 2. 核心概念

![](http://image.focusprogram.top/20200513225643.png)

> 网关提供API全托管服务，丰富的API管理功能，辅助企业管理大规模的API，以降低管理成本和安全风险，包括协议适配、协议转发、安全策略、防刷、流量、监控日志等贡呢。一般来说网关对外暴露的URL或者接口信息，我们统称为路由信息。如果研发过网关中间件或者使用过Zuul的人，会知道网关的核心是Filter以及Filter Chain（Filter责任链）。Sprig Cloud Gateway也具有路由和Filter的概念。下面介绍一下Spring Cloud Gateway中几个重要的概念。
>
> - 路由。路由是网关最基础的部分，路由信息有一个ID、一个目的URL、一组断言和一组Filter组成。如果断言路由为真，则说明请求的URL和配置匹配
>
> - 断言。Java8中的断言函数。Spring Cloud Gateway中的断言函数输入类型是Spring5.0框架中的ServerWebExchange。Spring Cloud Gateway中的断言函数允许开发者去定义匹配来自于http request中的任何信息，比如请求头和参数等。
>
> - 过滤器。一个标准的Spring webFilter。Spring cloud gateway中的filter分为两种类型的Filter，分别是Gateway Filter和Global Filter。过滤器Filter将会对请求和响应进行修改处理

# 3. 功能

> - Built on Spring Framework 5, Project Reactor and Spring Boot 2.0 基于 Spring Framework 5、 Project Reactor 和 Spring Boot 2.0构建
> - Able to match routes on any request attribute 能够匹配任何请求属性上的路由
> - Predicates and filters are specific to routes 谓词和筛选器特定于路由
> - Hystrix Circuit Breaker integration Hystrix 断路器一体化
> - Spring Cloud DiscoveryClient integration 集成 Spring Cloud DiscoveryClient
> - Easy to write Predicates and Filters 易于编写谓词和过滤器
> - Request Rate Limiting 请求速率限制
> - Path Rewriting 路径重写

# 4. springcloud集成Spring Cloud Gateway

## 4.1 环境准备

[consul详细参考](https://github.com/FocusProgram/person-improve/tree/main/springcloud-consul)

## 4.2 order-service模块

引入maven依赖

```
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-consul-discovery</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

编辑application.yml
 
```
server:
  port: 9000

spring:
  application:
    name: order-service

  cloud:
    consul:
      # 指定consul的ip地址和端口
      host: 192.168.80.110
      port: 8500
      discovery:
        # 指定服务版本信息
        tags: version=1.0,auth=Mr.Kong
        # 是否需要注册到consul，默认为true
        register: true
        # 注册的实例ID (唯一标志)
        instance-id: ${spring.application.name}:${spring.cloud.client.ip-address}
        # 服务名称
        service-name: ${spring.application.name}
        # 服务请求端口
        port: ${server.port}
        # 指定开启ip地址注册
        prefer-ip-address: true
        # 当前服务请求ip
        ip-address: ${spring.cloud.client.ip-address}
        # 指定consul心跳检测地址
        health-check-url: http://${spring.cloud.client.ip-address}:${server.port}/actuator/health
        # 指定consul心跳检测间隔
        health-check-interval: 15s
        

# 管理endpoints端点详细信息
management:
  endpoints:
    web:
      exposure:
        include: "*" #暴露所有节点
    health:
      sensitive: false #关闭过滤敏感信息
  endpoint:
    health:
      show-details: ALWAYS  #显示详细信息
```

添加OrderController

```
@RestController
@RequestMapping("order")
public class OrderController {

    @GetMapping("info")
    public String getOrder() {
        return "order";
    }

}
```

配置启动类OrderServiceApplication

```
@SpringBootApplication
@EnableDiscoveryClient
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }

}
```

## 4.3 gateway模块

引入maven依赖

```
<!-- gateway -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>
<!-- consul注册发现 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-consul-discovery</artifactId>
</dependency>
<!-- 健康检查 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

编辑配置文件application.yml

```
server:
  port: 8000

spring:
  application:
    name: service-gateway

  cloud:
    consul:
      # 指定consul的ip地址和端口
      host: 192.168.80.110
      port: 8500
      discovery:
        # 指定服务版本信息
        tags: version=1.0,auth=Mr.Kong
        # 是否需要注册到consul，默认为true
        register: true
        # 注册的实例ID (唯一标志)
        instance-id: ${spring.application.name}:${spring.cloud.client.ip-address}
        # 服务名称
        service-name: ${spring.application.name}
        # 服务请求端口
        port: ${server.port}
        # 指定开启ip地址注册
        prefer-ip-address: true
        # 当前服务请求ip
        ip-address: ${spring.cloud.client.ip-address}
        # 指定consul心跳检测地址
        health-check-url: http://${spring.cloud.client.ip-address}:${server.port}/actuator/health
        # 指定consul心跳检测间隔
        health-check-interval: 15s

    gateway:
      discovery:
        locator:
          # 开启服务注册和发现功能
          enabled: true
          # 请求路径上的服务名配置为小写（因为服务注册的时候，向注册中心注册时将服务名转成大写的了）
          lower-case-service-id: true
          
# 管理endpoints端点详细信息
management:
  endpoints:
    web:
      exposure:
        include: "*" #暴露所有节点
    health:
      sensitive: false #关闭过滤敏感信息
  endpoint:
    health:
      show-details: ALWAYS  #显示详细信息
```

配置启动类GatewayApplication

```
@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

}
```

### 4.3.1 Gateway网关配置

编辑配置文件application.yml

```
spring:
  cloud:
    gateway:
      discovery:
        locator:
          # 开启服务注册和发现功能
          enabled: true
          # 请求路径上的服务名配置为小写（因为服务注册的时候，向注册中心注册时将服务名转成大写的了）
          lower-case-service-id: true
      routes:
        # 通过请求路径匹配
        - id: order-service
          uri: lb://order-service
          predicates:
            - Path=/consumer/**
            - Method=GET
          filters:
            # consumer/oder/info自动转换请求路径为service-consumer/order/info
            - StripPrefix=1
```

> 如上路由配置，配置了一个Path的predicates，将以/consumer/**开头的请求都会转发到uri为lb://order-service的地址上，lb://order-service(consul服务注册中心地址)，即order-service服务的负载均衡地址

访问

```
curl http://localhost:8000/consumer/order/info
```

![](http://image.focusprogram.top/20200517151628.png)

### 4.3.2 Gateway跨域访问

编辑配置文件application.yml

```
spring:
  cloud:
    gateway:
      # 允许来自https://docs.spring.io的get请求进行访问,并且表明服务器允许请求头中携带字段Content-Type
      globalcors:
        corsConfigurations:
          '[/**]':
           allowedOrigins: "https://docs.spring.io"
           allowedMethods:
            - GET
           allowHeaders:
            - Content-Type
```

> 如上配置,允许来自https://docs.spring.io的get请求进行访问,并且表明服务器允许请求头中携带字段Content-Type

### 4.3.3 Gateway过滤器

> Spring Cloud Gateway的filter生命周期不像Zuul那么丰富，它只有两个：“pre”和“post”：
>
> - pre:这种过滤器在请求被路由之前调用。可以利用这个过滤器实现身份验证、在集群中选择请求的微服务、记录调试的信息。
>
> - post：这种过滤器在路由到服务器之后执行。这种过滤器可用来为响应添加HTTP Header、统计信息和指标、响应从微服务发送给客户端等。
>
> Spring Cloud gateway的filter分为两种：GatewayFilter和Globalfilter。GlobalFilter会应用到所有的路由上，而Gatewayfilter将应用到单个路由或者一个分组的路由上。
>
> 利用Gatewayfilter可以修改请求的http的请求或者是响应，或者根据请求或者响应做一些特殊的限制。更多时候可以利用Gatewayfilter做一些具体的路由配置。

**Gatewayfilter-AddRequestParameter过滤**

编辑配置文件application.yml

```
spring:
  cloud:
    gateway:
      routes:
        # 添加指定参数
        - id: paramter_route
          uri: http://localhost:9000/order/getParamter
          predicates:
            - Method=GET
          filters:
            # 在指定映射路径的请求都会获取到该参数
            - AddRequestParameter=name,Mr.Kong
```

> 上述配置中指定了转发的地址，设置所有的GET方法都会自动添加name=Mr.Kong,当请求符合上述路由条件时，即可在后端服务上接收到Gateway网关添加的参数。

访问

```
curl http://localhost:8000/consumer/order/getParamter
```

![](http://image.focusprogram.top/20200517151751.png)

**Gatewayfilter-StripPrefix gateway filter过滤**

编辑配置文件application.yml

```
spring:
  cloud:
    gateway:
      routes:
        # 添加指定参数
        - id: paramter_route
          uri: http://localhost:9000/order/getParamter
          predicates:
            - Method=GET
          filters:
            # consumer/oder/info自动转换请求路径为service-consumer/order/info
            - StripPrefix=1
```

> 当client端使用http://localhost:8000/consumer/order/info路径进行请求时，如果根据上述进行配置Gateway会将请求转换为http://localhost:8000/order-service/order/info。以此作为前端请求的最终目的地

### 4.3.4 Gateway请求匹配

#### 4.3.4.1 head匹配

编辑配置文件application.yml

```
spring:
  cloud:
    gateway:
      routes:
        # 通过header匹配
        - id: head_route
          uri: http://www.baidu.com
          predicates:
            - Header=X-Request-Id, \d+
            - Method=GET
```

访问

```
curl http://localhost:8000 -H "X-Request-Id:13951113338"
```

![](http://image.focusprogram.top/20200517152538.png)

#### 4.3.4.2 Host匹配

编辑配置文件application.yml

```
spring:
  cloud:
    gateway:
      routes:
         # 通过Host匹配
         - id: host_route
          uri: http://www.baidu.com
          predicates:
            - Host=**.baidu.com
```

访问

```
curl http://localhost:8000 -H "Host: www.baidu.com"
```

![](http://image.focusprogram.top/20200517153009.png)

#### 4.3.4.3 请求方法匹配

编辑配置文件application.yml

```
spring:
  cloud:
    gateway:
      routes:
        # 通过请求方式路由
        - id: method_route
          uri: http://www.baidu.com
          predicates:
            - Method=GET
```

访问

```
curl http://localhost:8000
```

![](http://image.focusprogram.top/20200517153416.png)


### 4.3.5 Gateway熔断Hystrix

引入maven依赖

```
<!-- 熔断 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
</dependency>
```

编辑配置文件application.yml

```
spring:
  cloud:
    gateway:
      routes:
        # hystrix路由
        - id: hystrix_route
          uri: lb://order-service
          predicates:
          - Path=/consumer/**
          filters:
          - StripPrefix=1
          - RewritePath=/consumer/(?<path>.*), /$\{path}
          - name: Hystrix
            args:
              name: fallbackcmd
              fallbackUri: forward:/fallback/order
              
# 超时时间，若不设置超时时间则有可能无法触发熔断
hystrix:
  command:
    fallbackcmd:
      execution:
        isolation:
          thread:
            timeoutInMilliseconds: 5000
```

> fallbackUri: forward:/fallback配置了 fallback 时要会调的路径，当调用 Hystrix 的 fallback 被调用时，请求将转发到/fallback这个 URI，并以此路径的返回值作为返回结果
>
> 上述配置中给出了熔断之后返回路径，因此，在Gateway服务模块添加/fallback路径，以作为服务熔断时的返回路径

```
@RestController
@RequestMapping("/fallback")
public class GatewayController {

    @GetMapping("/order")
    public String fallback(){
        return "抱歉，order-service服务暂时不可用";
    }

}
```

停止order-service,再次访问

```
curl http://localhost:8000/consumer/order/info
```

![](http://image.focusprogram.top/20200517160425.png)

### 4.3.6 Gateway重试路由

> Retry GatewayFilter通过四个参数来控制重试机制，参数说明如下：
>
> - retries：重试次数，默认值是 3 次。
>
> - statuses：HTTP 的状态返回码，取值请参考：org.springframework.http.HttpStatus。
> 
> - methods：指定哪些方法的请求需要进行重试逻辑，默认值是 GET 方法，取值参考：org.springframework.http.HttpMethod。
>
> - series：一些列的状态码配置，取值参考：org.springframework.http.HttpStatus.Series。符合的某段状态码才会进行重试逻辑，默认值是 SERVER_ERROR，值是 5，也就是 5XX(5 开头的状态码)，共有5个值。

编辑配置文件application.yml

```
spring:
  cloud:
    gateway:
      routes:
        # 重试机制
        - id: retry_route
          uri: lb://order-service
          predicates:
            - Path=/consumer/**
          filters:
            - StripPrefix=1
            - name: Retry
              args:
                # 重试次数，默认为三次
                retries: 3
                # HTTP返回状态码
                status: 500
                # 指定哪些方法需要执行重试逻辑
                methods: GET
                # 一些列的状态码配置，取值参考：org.springframework.http.HttpStatus.Series。符合的某段状态码才会进行重试逻辑，默认值是 SERVER_ERROR，值是 5，也就是 5XX(5 开头的状态码)，共有5个
                series: SERVER_ERROR
```

### 4.3.7 Gateway限流

引入maven依赖

```
<!-- 限流 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis-reactive</artifactId>
</dependency>
```

编辑配置文件application.yml

```
spring:
  cloud:
    gateway:
      routes:
        # redis限流
        - id: rate_limit_route
          uri: lb://order-consumer
          predicates:
            - Path=/consumer/**
          filters:
            - StripPrefix=1
            - name: RequestRateLimiter
              args:
                # 用于限流的解析器的Bean对象的名字。它使用SpEL表达式#{@beanName}从Spring容器中获取bean对象
                key-resolver: "#{@hostAddrKeyResolver}"
                # 令牌通每秒填充平均速率
                redis-rate-limiter.replenishRate: 1
                # 令牌桶的总容量
                redis-rate-limiter.burstCapacity: 3
                
# 配置redis链接信息
  redis:
    host: 114.55.34.44
    port: 6379
    password: root
```

> 在上面的配置文件中，配置了Redis的信息，并配置了RequestRateLimiter的限流过滤器，该过滤器需要配置三个参数：
>
> - BurstCapacity：令牌桶的总容量。
> 
> - replenishRate：令牌通每秒填充平均速率。
>
> - Key-resolver：用于限流的解析器的Bean对象的名字。它使用SpEL表达式#{@beanName}从Spring容器中获取bean对象。
>
> 注意：filter下的name必须是RequestRateLimiter。

Key-resolver参数后面的bean需要自己实现，然后注入到Spring容器中。KeyResolver需要实现resolve方法，比如根据ip进行限流，则需要用hostAddress去判断。实现完KeyResolver之后，需要将这个类的Bean注册到Ioc容器中。还可以根据uri限流，同hostname限流是一样的。例如以ip限流为例，在gateway模块中添加以下实现:

```
public class HostAddrKeyResolver implements KeyResolver {

    @Override
    public Mono<String> resolve(ServerWebExchange exchange) {
        return Mono.just(exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
    }

    public HostAddrKeyResolver hostAddrKeyResolver() {
        return new HostAddrKeyResolver();
    }
}
```

把该类注入到spring容器中：

```
@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    /**
     * 注入限流
     *
     * @return
     */
    @Bean
    public HostAddrKeyResolver hostAddrKeyResolver() {
        return new HostAddrKeyResolver();
    }

}
```

基于上述配置，可以对请求基于ip的访问进行限流。

### 4.3.8 Gateway自定义Gatewayfilter

Spring Cloud Gateway内置了过滤器，能够满足很多场景的需求。当然，也可以自定义过滤器。在Spring Cloud Gateway自定义过滤器，过滤器需要实现GatewayFilter和Ordered这两个接口。

下面的例子实现了Gatewayfilter，它可以以log日志的形式记录每次请求耗费的时间，具体实现如下：

```
public class RequestTimeFilter implements GatewayFilter, Ordered {

    private static final Log log = LogFactory.getLog(GatewayFilter.class);

    private static final String REQUEST_TIME_BEGIN = "requestTimeBegin";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        exchange.getAttributes().put(REQUEST_TIME_BEGIN, System.currentTimeMillis());
        return chain.filter(exchange).then(
                Mono.fromRunnable(() -> {
                    Long startTime = exchange.getAttribute(REQUEST_TIME_BEGIN);
                    if (startTime != null) {
                        log.info("请求路径：" + exchange.getRequest().getURI().getRawPath() + "消耗时间: " + (System.currentTimeMillis() - startTime) + "ms");
                    }
                })
        );
    }

    /**
     * 定义过滤器的优先级，值越大优先级越小
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }

}
```

接下来将该过滤器注册到router中:

```
@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    /**
     * 注入限流
     *
     * @return
     */
    @Bean
    public HostAddrKeyResolver hostAddrKeyResolver() {
        return new HostAddrKeyResolver();
    }

    /**
     * 将过滤器注册到route
     */
    @Bean
    public RouteLocator customerRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/user/**")
                        .filters(f -> f.filter(new RequestTimeFilter())
                                .addResponseHeader("X-Response-Default-Foo", "Default-Bar"))
                        .uri("http://localhost:8504/user/info")
                        .order(0)
                        .id("customer_filter_router")
                )
                .build();
    }

}
```

### 4.3.9 Gateway自定义GlobalFilter

> Spring Cloud Gateway根据作用范围分为GatewayFilter和GlobalFilter，二者区别如下：
>
> GatewayFilter : 需要通过spring.cloud.routes.filters 配置在具体路由下，只作用在当前路由上或通过spring.cloud.default-filters配置在全局，作用在所有路由上。
>
> GlobalFilter:全局过滤器，不需要在配置文件中配置，作用在所有的路由上，最终通过GatewayFilterAdapter包装成GatewayFilterChain可识别的过滤器，它为请求业务以及路由的URI转换为真实业务服务的请求地址的核心过滤器，不需要配置，系统初始化时加载，并作用在每个路由上。

```
public class TokenFilter implements GlobalFilter, Ordered {

    Logger logger = LoggerFactory.getLogger(TokenFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = exchange.getRequest().getQueryParams().getFirst("token");
        if (token == null || token.isEmpty()) {
            logger.info("token 为空，无法进行访问.");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
```
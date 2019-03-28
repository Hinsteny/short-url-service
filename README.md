## short-url-service
----

### 技术选型
jdk8, mysql8.0, spring5.1, mybatis3, liquibase, log4j2

### 构建

```
mvn clean compile 

```

### 启动

```
mvn spring-boot:run

```

### 访问

1. 创建短连接

```
curl post http://localhost:8080/api/short 

{
  "url":"http://wwww.baidu.com",
  "customKey":"baidu"
}

result: "https://996.icu/aqiyi"


=======================================
curl post http://localhost:8080/api/short 

{
  "url":"http://wwww.aqiyi.com",
  "customKey":"aqiyi",
  "withHttpHead": 0
}

result: "996.icu/aqiyi"

=======================================
curl post http://localhost:8080/api/short 

{
  "url":"http://wwww.tengxun.com",
  "withHttpHead": 0
}

result": "996.icu/b8c434a4397741e79a2cf4f849bfde42"

```

2. 获取短链接所对应的原url
```
curl get http://localhost:8080/api/short?shortId=baidu

{
  "url":"http://wwww.baidu.com",
  "customKey":"baidu"
}

result: "http://wwww.baidu.com"
===================================================
curl get http://localhost:8080/api/short?shortId=b8c434a4397741e79a2cf4f849bfde42

{
  "url":"http://wwww.baidu.com",
  "customKey":"baidu"
}

result": "http://wwww.tengxun.com"


```


### 待优化

1. 访问统计，可以采用拦截器处理, 然后发消息, 由消费者进行统计计数
2. 短连接生成算法, 可以使用更短, 更紧凑的处理算法;

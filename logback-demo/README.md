### 说明
```
Logback的理论基本和log4j2差不多，配置属性也类似，详细的按照文档配置即可
```

### 与Spring结合使用主要是增加了两个标签

#### springProperty
```xml
 <!-- 属性文件:在properties文件中找到对应的配置项 -->
    <springProperty scope="context" name="logging.path"  source="logging.path"/>
```
#### springProfile

```xml

  <springProfile name="staging">
        <!-- configuration to be enabled when the "staging" profile is active -->
    </springProfile>
    <springProfile name="dev, staging">
        <!-- configuration to be enabled when the "dev" or "staging" profiles are active -->
    </springProfile>

    <springProfile name="!production">
        <!-- configuration to be enabled when the "production" profile is not active -->
    </springProfile>

```

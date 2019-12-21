### 关键概念
- LoggerConfig: 日志配置, 用于整合多个 Appender, 进行日志打印.
- Appender: 追加器, 用于操作 Layout 和 Manager, 往单一目的地进行日志打印.
- Layout: 布局, 用于把 LogEvent 日志事件序列化成字节序列, 不同 Layout 实现具有不同的序列化方式.
- Manager: 管理器, 用于管理输出目的地, 如: RollingFileManager 用于管理文件滚动以及将字节序列写入到指定文件中
- Filter: 过滤器, 用于对 LogEvent 日志事件加以过滤, LoggerConfig 和 Appender 都可以配置过滤器, 也就是说日志事件会经过一总一分两层过滤.

### 日志等级
```
   FATAL &gt; ERROR &gt; WARN &gt; INFO &gt; DEBUG &gt; TRACE &gt; ALL

   Configuration status="OFF"：
   这个status配置的是，Log4j2 组件本身日志级别，指的是如果 Log4j2 本身出错，打印出的日志级别配置。
```
### 日志名称、继承体系
- 名称和继承体系
```
Logger中存在与JAVA体系类似的继承关系,Logger中的超类是RootLogger，它是所有Logger的父类

Logger的继承关系是通过**名称**隐式实现的
比如：一个名为 com.a 就是 com.a.b 的父logger,而 com.a 的父类则是 RootLogger.
```

### 日志结构
![](https://oscimg.oschina.net/oscnet/up-64d02cbcf71db1b7bd9622b9c97d7b048d3.png)
### 各种日志框架适配
```
slf4j提供了各种各样的适配器，用来将某种日志框架委托给slf4
```
通过适配转换为新的日志框架输出方案
![](https://oscimg.oschina.net/oscnet/up-aa632ded041938ad66175e28e76a7c52e34.png)

然后根据需要填入对应的适配器以及新的日志框架桥接器以及引入新的日志框架
比如
![](https://oscimg.oschina.net/oscnet/up-63e5bfe1fae6c9dbe311cffa633532675ec.png)


就可以统一为log4j2日志输出了
- 注意：程序根据classpath依赖的桥接器类型，和日志框架类型，判断出logger.info应该以什么框架输出！
- 特别注意了，如果classpath中不小心引了两个桥接器，那会直接报错的！
### 报错出现死循环
![](https://oscimg.oschina.net/oscnet/up-33d341c70a131af2e38a8461d6cac195cac.png)
```
如上图所示，在这种情况下，你调用了slf4j-api，就会陷入死循环中！
1,slf4j-api去调了slf4j-log4j12
2,slf4j-log4j12又去调用了log4j
3,log4j去调用了log4j-over-slf4j
4,log4j-over-slf4j又调了slf4j-api，陷入死循环
```
### 日志配置属性解析
- 类图

![](http://upload-images.jianshu.io/upload_images/7979147-a8d2a88cdcfe5018.png)
- 默认配置
```xml
<!--?xml version="1.0" encoding="UTF-8"?-->
<!-- OFF > FATAL > ERROR > WARN > INFO > DEBUG > TRACE > ALL log4j2日志等级 -->
<configuration status="WARN">
    <appenders>
        <console name="Console" target="SYSTEM_OUT">
            <patternlayout pattern="%d{HH：mm：ss.SSS} [%t] %-5level %logger{36} - %msg%n" />
        </console>
    </appenders>
    <loggers>
        <root level="error">
            <appenderref ref="Console" />
        </root>
    </loggers>
</configuration>
```
- interpolator 插值器
```
在配置文件中, 基本上所有的值的配置都可以通过参数占位符引用环境变量信息, 格式为:${prefix:key}.

当参数占位符 ${prefix:key} 带有 prefix 前缀时, Interpolator 会从指定 prefix 对应的 StrLookup 实例中
进行 key 查询

当参数占位符 ${key} 没有 prefix 时, Interpolator 则会从默认查找器中进行查询.
prefix 主要有：sys、env等十多种

Interpolator 中默认支持的 StrLookup 
查找方式如下
(StrLookup 查找器实现类均在 org.apache.logging.log4j.core.lookup 包下)
```
- 属性（Properties）配置
```xml
<!--?xml version="1.0" encoding="UTF-8"?-->
<configuration>
    <properties>
        <property name="customKey_1">customValue_1</property>
        <property name="customKey_2">customValue_2</property>
    </properties>
</configuration>
```
** ** 注意：Properties 元素一定要配置在最前面, 否则不生效.****
-  Appenders
```
简单说 Appender 就是一个管道，定义了日志内容的去向 (保存位置)。
配置一个或者多个Filter，Filter的过滤机制和Servlet的Filter有些差别，下文会进行说明。
配置Layout来控制日志信息的输出格式。
配置Policies以控制日志何时 (When) 进行滚动。
配置Strategy以控制日志如何 (How) 进行滚动。

 框架支持多种Appender实现
 ConsoleAppender，FileAppender，RollingFileAppender，

 AsyncAppender，RollingRandomAccessFileAppender，RandomAccessFileAppender，

 JdbcAppender，JpaAppender，KafkaAppender，RoutingAppender
```
常用Appender介绍
1. ConsoleAppender
```
 控制台追加器, 用于把日志输出到控制台, 一般本地调试时使用。
 对应标签<console>
```
示例
```xml
<!--?xml version="1.0" encoding="UTF-8"?-->
<configuration status="warn" dest="err" verbose="false">

    <appenders>
        <!-- follow和direct不能同时为true,如果follow为true则会跟随底层输出流的变化,direct为true则固定指向输出流 -->
        <console name="console" target="SYSTEM_OUT" follow="false" direct="true">
            <patternlayout pattern="%date{yyyy-MM-dd HH:mm:ss.SSS} %C.%M %message%n" />
        </console>
    </appenders>
    <loggers>
        <root additivity="true" level="error" includelocation="true">
            <appenderref ref="console" level="info" />
        </root>
    </loggers>

</configuration>
```
2. RollingFileAppender
```
文件滚动追加器, 用于向本地磁盘文件中追加日志, 同时可以通过触发策略 (TriggeringPolicy)

和滚动策略 (RolloverStrategy) 控制日志文件的分片, 避免日志文件过大

对应标签<rollingfile>
```
触发策略：
```
   · TimeBasedTriggeringPolicy: 基于时间周期性触发滚动, 一般按天滚动
   · SizeBasedTriggeringPolicy: 基于文件大小触发滚动, 可以控制单个日志文件的大小上限
 ```
 
 滚动策略
 ```
 · DefaultRolloverStrategy: 默认滚动策略
	 该策略内部维护一个最小索引和最大索引, 每次滚动时, 会删除历史文件,
	 
	 之后剩余文件全部进行一轮重命名, 最后创建新的不带有索引后缀的文件进行日志追加
```
![](http://upload-images.jianshu.io/upload_images/6451155-8632a61ad6dc472c.png)

```
· DirectWriteRolloverStrategy: 直接写滚动策略
	该策略内部会维护一个一直自增的文件索引, 每次滚动时直接创建新的带有索引后缀的文件进行日志追加, 
	
	同步清理历史的文件.
```
![](http://upload-images.jianshu.io/upload_images/6451155-76e1906a1fddddde.png)

示例
```xml
<!--?xml version="1.0" encoding="UTF-8"?-->
<configuration status="warn" dest="err" verbose="false">

    <properties>
        <property name="logDir">/Users/lixin46/workspace/demo/logdemo/logs</property>
        <property name="pattern">%date{yyyy-MM-dd HH:mm:ss.SSS} %C.%M %message%n</property>
    </properties>
    <appenders>
        <console name="console">
            <patternlayout pattern="${pattern}" />
        </console>
        <!-- 使用DirectWriteRolloverStrategy策略时,不需要配置fileName -->
        <rollingfile name="fileAppender" filename="${logDir}/request.log" filepattern="${logDir}/request.log-%d-%i">
            <patternlayout pattern="${pattern}" />
            <!-- 所有策略中,只要任意策略满足就会触发滚动 -->
            <policies>
                <!-- 滚动时间周期,只有数量,单位取决于filePattern中%d的配置 -->
                <timebasedtriggeringpolicy interval="1" />
                <sizebasedtriggeringpolicy size="10b" />
            </policies>
            <!-- 限制最多保留5个文件,索引自增 -->
            <!--<DirectWriteRolloverStrategy maxFiles="5"/>-->
            <!-- 限制最多保留5个文件,索引从2到6 -->
            <defaultrolloverstrategy fileIndex="max" min="2" max="6" />
        </rollingfile>
    </appenders>

    <loggers>
        <root level="info">
            <appenderref ref="console" level="info" />
            <appenderref ref="fileAppender" level="info" />
        </root>
    </loggers>

</configuration>
```
3.FileAppender
```
 主要用于本地测试
 
```
```xml
<!--?xml version="1.0" encoding="UTF-8"?-->
<configuration status="warn" name="MyApp" packages="">
  <appenders>
	  <!-- append，boolean，指定是否是追加写入（append=true，默认情况），还是覆盖写入（append=false）-->
    <file name="MyFile" filename="logs/app.log" append="true">
      <patternlayout>
        <pattern>%d %p %c{1.} [%t] %m%n</pattern>
      </patternlayout>
    </file>
  </appenders>
  <loggers>
    <root level="error">
      <appenderref ref="MyFile" />
    </root>
  </loggers>
</configuration>
```
4,RandomAccessFileAppender
```
和FileAppender类似，但是使用了ByteBuffer+RandomAccessFile的方式来代替BufferedOutputStream
```
5.RollingRandomAccessFileAppender
```
和RollingFileAppender类似，使用了ByteBuffer+RandomAccessFile的方式代替BufferedOutputStream
```

6.RoutingAppender
```
通过路由规则来评价一个 log event 后，决定它下一个被发往的 appender。

RoutingAppender 有一个重要的参数名为 routes，是 Routes 型数据，用来描述该 appender 的路由规则
```
7.SMTPAppender
```
SMTPAppender 主要用来给指定的 E-mail 发送 log event

（这种情况一般用在 event 的安全级别超过 ERROR 或 FATAL 时，event 的安全分级可以参考【此文】之日志记录小节）。

SMTPAppender 有很多重要的参数以完成 log event 发送到指定 E-mail。
```
8.SocketAppender
```
将 log event 输出到一个远程服务器上（需指定服务器名和端口号），数据可以以任意指定的格式经由 TCP 或 UDP 协议发
```
9.AsynchAppender
```
引用其他Appender,被引用的Appender可以做到异步输出日志

一个 LogEvent 异步地写入多个不同输出地。

在 AsynchAppender 中有一个参数，名为 “appender-ref”，用来指定要发送到的 appender 的名称。

AsynchAppender 维护了一个队列，队列中存放了需要异步发送的 LogEvent，

队列中 LogEvent 的个数可以通过“bufferSize” 参数来指定。

另外，还有一个 “blocking” 参数来指定是否对 AsynchAppender 的 LogEvent 队列上锁，如果 blocking=true，

那么在队列满员的情况下，新到达的 LogEvent 将等待，直到有空位。

若 blocking=false，那么在队列满员的情况下，将把新到的 LogEvent 转到 error appender
```
- PatternLayout
```
模式布局是我们最常使用的, 它通过 PatternProcessor 模式解析器, 对模式字符串进行解析,

得到一个 List<patternconverter> 转换器列表和 List<formattinginfo> 格式信息列表.

在 PatternLayout 序列化时, 会遍历每个 PatternConverter, 从 LogEvent 中取不同的值进行序列化输出
```
- Filters
```
Filters 决定日志事件能否被输出。过滤条件有三个值：ACCEPT(接受)，DENY(拒绝)，NEUTRAL(中立)
log4j2中的过滤器ACCEPT和DENY之后，后续的过滤器就不会执行了，只有在NEUTRAL的时候才会执行后续的过滤器

全局 Filter 节点<filters> 必须放在<properties>节点之后

过滤器作用域

1.全局范围

 即直接配置在 configuration 最外层：该作用域的 Filter 直接过滤日志信息而不传递至 Logger 做进一步处理；

若配置多个全局 filter，则有且仅有一个起效，同时 logger 定义的 level 也将失效 (若 filter 中配置了 level)；

Logger 和 Appender 的 filter 将覆盖全局的 filter

2.Logger 范围

配置在某个具体的 Logger 中，该 filter 不会传递至父级的 Logger 中，即使 additivity 配置为 true

3.Appender 范围

作用于 Appender 是否处理日志过滤操作

4.Appender Reference 范围

作用于 Logger 路由该过滤操作至某个 appender 上

```
常见的Filters

1.LevelRangeFilter

minLevel 需要配置的是高级别，maxLevel 配置的是低级别
```xml
<levelrangefilter minLevel="fatal" maxLevel="info" onMatch="ACCEPT" onMismatch="DENY" />
```
2.TimeFilter
```xml
<timefilter start="05:00:00" end="05:30:00" onMatch=" NEUTRAL " onMismatch="DENY" />
```
3.ThresholdFilter

 这个 Filter 负责按照所配置的日志级别过滤 Log Event，等于或超出所配置级别的 log Event 将返回 onMatch 结果。（threshold ： 阈值, 临界值; 门槛，入口）
```xml
<thresholdfilter level="TRACE" onMatch="NEUTRAL" onMismatch="DENY" />
```
- Logger
```
分为两个 Root(必须配置) 和 Logger
```
示例：
```xml
<!--?xml version="1.0" encoding="UTF-8"?-->
<configuration status="warn" dest="err" verbose="false">

    <appenders>
        <console name="console">
            <patternlayout pattern="%date{yyyy-MM-dd HH:mm:ss.SSS} %C.%M %message%n" />
        </console>
    </appenders>
    <loggers>
        <root level="error" includelocation="true">
            <appenderref ref="console" level="info">
                <thresholdfilter level="warn" onMatch="NEUTRAL" onMismatch="DENY" />
            </appenderref>
            <property name="customeKey">customeValue</property>
            <thresholdfilter level="warn" onMatch="NEUTRAL" onMismatch="DENY" />
        </root>
        <logger name="com.lixin" additivity="true" level="info" includelocation="true">
            <appenderref ref="console" level="info">
                <thresholdfilter level="warn" onMatch="NEUTRAL" onMismatch="DENY" />
            </appenderref>
            <property name="customeKey">customeValue</property>
            <thresholdfilter level="warn" onMatch="NEUTRAL" onMismatch="DENY" />
        </logger>
    </loggers>

</configuration>
```
解析：
```
- additivity: 日志可加性, 如果配置为 true, 则在日志打印时, 会通过 Logger 继承关系递归调用父 Logger 引用的 Appender 进行日志打印.
注意: 该属性默认为 true. 在递归打印日志时, 会忽略父 Logger 的 level 配置

- level: 用于控制允许打印的日志级别上线, 在配置示例中, 只有级别 &lt;=info 的 LogEvent 才会被放行, 级别优先级顺序为 OFF<fatal<error<warn<info<debug<trace<all 注意: level 属性的配置时可选的, 在获取 时会通过 logger 继承关系递归获取, rootlogger 的级别默认为 error, 其他默认为 null. 也就是说, 如果全都不配置 的话, 则所有 级别都默认为 error. - includelocation: 如果配置为 true, 则打印日志时可以附带日志点源码位置信息输出. 同步日志上下文默认为 异步默认为 false. loggerconfig 元素下可以单独配置 property 元素, 添加属性键值对, 这些属性会在每次打印日志时, 被追加到 logevent 的 contextdata 中 支持配置过滤器, 在判断是否打印日志时, 先过滤器判断过滤, 然后再级别判断过滤 appenderref: 顾名思义, 就是配置当前 引用的 appender. 同时, appenderref 也支持配置 和 filter, 进行更细粒度的日志过滤 等于总开关, 则为各个子开关, 两个开关都通过才能打印日志 ``` 最后整体的配置示例 ```xml <?xml version="1.0" encoding="UTF-8" ?>
<!-- 设置log4j2的自身log级别为INFO -->
<!-- OFF > FATAL > ERROR > WARN > INFO > DEBUG > TRACE > ALL -->
<configuration status="INFO" monitorinterval="30">
	<properties>
    	<property name="filePath">${sys:catalina.home}/logs</property>
  	</properties>
    <appenders>
        <console name="Console" target="SYSTEM_OUT">
        	<filters>
            	<!-- 过滤器 仅放行DEBUG及以上级别的日志 （OFF，FATAL，ERROR，WARN，INFO）	 -->
        		<!-- 如果不是要调试项目，请不要将日志等级调到DEBUG及以下。	-->
                <thresholdfilter level="INFO" />
                <!-- 过滤器 仅放行FATAL以下级别的日志 （INFO，DEBUG，TRACE，ALL）	 -->
                <thresholdfilter level="OFF" onMatch="DENY" onMismatch="NEUTRAL" />
            </filters>
            <patternlayout pattern="%d{yyyy-MM-dd HH:mm:ss,SSS} [%p] - %l - %m%n" />
        </console>
 
 		<!-- 将所有日志输出到 Redirect_Logger.log 并设置不累加 -->
 		<file name="log" filename="${filePath}/Redirect_logger.log" append="false">
 			<filters>
            	<!-- 过滤器 仅放行DEBUG及以上级别的日志 （OFF，FATAL，ERROR，WARN，INFO） -->
                <thresholdfilter level="DEBUG" />
                <!-- 过滤器 仅放行FATAL以下级别的日志 （INFO，DEBUG，TRACE，ALL） -->
                <thresholdfilter level="FATAL" onMatch="DENY" onMismatch="NEUTRAL" />
            </filters>
	       <patternlayout pattern="%d{yyyy-MM-dd HH:mm:ss,SSS} [%p] - %l - %m%n" />
    	</file>
 		<!-- 这个会打印出所有的info及以下级别的信息，每次大小超过size，则这size大小的日志会自动存入按年份-月份建立的文件夹下面并进行压缩，作为存档-->
        <rollingfile name="RollingFileInfo" filename="${sys:catalina.home}/logs/info.log" filepattern="${sys:catalina.home}/logs/info-%d{yyyy-MM-dd}-%i.log">
            <!--控制台只输出level及以上级别的信息（onMatch），其他的直接拒绝（onMismatch）-->        
            <filters>
            	<!-- 过滤器 仅放行INFO及以上级别的日志 （OFF，FATAL，ERROR，WARN，INFO） -->
                <thresholdfilter level="INFO" />
                <!-- 过滤器 仅放行WARN以下级别的日志 （INFO，DEBUG，TRACE，ALL） -->
                <thresholdfilter level="WARN" onMatch="DENY" onMismatch="NEUTRAL" />
            </filters>
            <!-- 格式化文件输出的日志格式 -->
            <patternlayout pattern="%d{yyyy-MM-dd HH:mm:ss,SSS} [%p] - %l - %m%n" />
            <policies>
                <timebasedtriggeringpolicy interval="24" modulate="true" />
                <sizebasedtriggeringpolicy size="1MB" />
            </policies>
        </rollingfile>
    </appenders>
 
    <loggers>
        <!--过滤掉spring和mybatis的一些无用的DEBUG信息-->
        <logger name="org.springframework" level="INFO"></logger>
        <logger name="org.mybatis" level="INFO"></logger>
        <root level="ALL">
            <appender-ref ref="Console" />
            <appender-ref ref="log" />
            <appender-ref ref="RollingFileInfo" />
        </root>
    </loggers>
</configuration>
```
### 引入xiclude
```
XML配置文件可以包含XInclude引入其他文件
```
示例
```xml
<!--?xml version="1.0" encoding="UTF-8"?-->
<configuration xmlns:xi="http://www.w3.org/2001/XInclude" status="warn" name="XIncludeDemo">
  <properties>
    <property name="filename">xinclude-demo.log</property>
  </properties>
  <thresholdfilter level="debug" />
  <xi:include href="log4j-xinclude-appenders.xml" />
  <xi:include href="log4j-xinclude-loggers.xml" />
</configuration>
```
log4j-xinclude-appenders.xml:
```xml
<!--?xml version="1.0" encoding="UTF-8"?-->
<appenders>
  <console name="STDOUT">
    <patternlayout pattern="%m%n" />
  </console>
  <file name="File" filename="${filename}" bufferedio="true" immediateflush="true">
    <patternlayout>
      <pattern>%d %p %C{1.} [%t] %m%n</pattern>
    </patternlayout>
  </file>
</appenders>
```
log4j-xinclude-loggers.xml:
```xml
<!--?xml version="1.0" encoding="UTF-8"?-->
<loggers>
  <logger name="org.apache.logging.log4j.test1" level="debug" additivity="false">
    <threadcontextmapfilter>
      <keyvaluepair key="test" value="123" />
    </threadcontextmapfilter>
    <appenderref ref="STDOUT" />
  </logger>

  <logger name="org.apache.logging.log4j.test2" level="debug" additivity="false">
    <appenderref ref="File" />
  </logger>

  <root level="error">
    <appenderref ref="STDOUT" />
  </root>
</loggers>
```

### 异步模式
```
用异步日志进行输出时，日志输出语句与业务逻辑语句并不是在同一个线程中运行，而是有专门的线程用于进行日志输出操作，处理业务
逻辑的主线程不用等待即可执行后续业务逻辑

Log4j2 中的异步日志实现方式有 AsyncAppender 和 AsyncLogger 两种

AsyncAppender 采用了 ArrayBlockingQueue 来保存需要异步输出的日志事件；
AsyncLogger 则使用了 Disruptor 框架来实现高吞吐(建议采用)

```
- 全局异步模式（建议）

1. maven 增加 disruptor 依赖，Log4j2 版本 2.9 及以上时需要 disruptor-3.3.4.jar 或更高版本；Log4j2 版本 2.9 以下时需要 disruptor-3.0.0.jar 或更高版本
```xml
<dependency>
     <groupid>com.lmax</groupid>
     <artifactid>disruptor</artifactid>
     <version>3.3.4</version>
</dependency>
```
2. 将系统属性 log4j2.contextSelector 设置为org.apache.logging.log4j.core.async.AsyncLoggerContextSelector

方式一：添加一个名字为 log4j2.component.properties 的文件，放到 classpath 下面

增加属性：Log4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector

方式二：加载 JVM 启动参数里

-DLog4j2.contextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector

方式三：在主程序代码开头，加上系统属性的代码

System.setProperty("Log4jContextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");

3. 修改配置文件的使用RandomAccessFile或RollingRandomAccessFile

- 异步与同步混合使用

1. 增加disruptor依赖，同上
2. 使用 <asyncroot> 或 <asyncLogger> 配置来指定需要异步的记录器

	注意：配置只能包含一个根记录器（<root> 或 &lt; asyncRoot &gt; 元素），但可以组合异步和非异步记录器。例如，包含 &lt; asyncLogger &gt; 元素的配置文件也可以包含同步记录器的 &lt; Root &gt; 和 &lt; Logger &gt; 元素

### 与spring-boot结合
1. 去掉spring-boot默认的日志框架，引入log4j2的日志框架
2. 配置log4j的配置文件即可

- spring-boot的profiles结合使用
	
	根据不同profile使用不同的配置文件
	```
	在application.properties中根据环境的选择动态指定logging.config
	```

### 参考
```
https://www.jianshu.com/p/d09e4d3a5f2b
https://www.jianshu.com/p/0c882ced0bf5
https://www.cnblogs.com/rjzheng/p/10042911.html
```

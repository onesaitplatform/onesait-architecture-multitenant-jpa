# Multitenant JPA Library

## Introduction
A multitenant architecture allows a single software instance to be used by two different clients, so internally and transparently to the user, the data must be divided for each of them.

This library has been created with this idea in mind, which will speed up the process of implementing this architecture when we use JPA in our microservices. And that almost automatically, with a simple configuration, we will have a multitenant microservice, which will access one DB or another depending on the tenant that arrives with each request.

## Usage
This library is used for multitenant with JPA, the JPA spring data part did not change compared to a normal project, it is only important that the DBs (of each tenant) have exactly the same structure.

To use the library we must include within our `pom.xml`:

```xml
<dependency>
     <groupId>com.minsait.onesait.architecture</groupId>
     <artifactId>architecture-multitenant-jpa</artifactId>
     <version>${onesait.multitenant.version}</version>
</dependency>
```

## Setup
Once the library is added, we must add the following configuration in the `application.yml`:

```yaml
architecture.multitenant-jpa:
  tenantProvider: <TENANT_PROVIDER>
  tenantField: <TENANT_FIELD>
  datasources: 
      -
        tenantId: <TENANT_ID_1>
        url: <DB_URL_1>
        username: <DB_USER_1>
        password: <DB_PASSWORD_1>
        driverClassName: <DB_DRIVER_CLASS_1>
      -
        tenantId: <TENANT_ID_2>
        url: <DB_URL_2>
        username: <DB_USER_2>
        password: <DB_PASSWORD_2>
        driverClassName: <DB_DRIVER_CLASS_2>
      -
        (...)
      -
        tenantId: <TENANT_ID_N>
        url: <DB_URL_N>
        username: <DB_USER_N>
        password: <DB_PASSWORD_N>
        driverClassName: <DB_DRIVER_CLASS_N>
```

* **<TENANT_PROVIDER>**:(*Optional) Indicates where the tenant of the request should obtain the library from, it has two options: TOKEN (it comes in a field of the JWT token) or HEADER (it comes in a header). By default the value is TOKEN.
* **<TENANT_FIELD>**:(*Optional) Indicates the name of the field where the tenant comes from, both in the jwt and in the header. By default, the value is tenant for the token or X-TENANT-ID in case it comes in the header.
* **<TENANT_ID_N>**: Identifier of each of the tenants used.
* **<DB_URL_N>**: Url of the DB for each one of the tenants. Example: jdbc:mysql://localhost:3306/uni_dev_A
* **<DB_USER_N>**: Database user for each of the tenants.
* **<DB_PASSWORD_N>**: User password for each of the tenants.
* **<DB_DRIVER_CLASS_N>**: DB Drive for each of the tenants: Example: com.mysql.cj.jdbc.Driver.

## Logs
Having a multitenant microservice, it is possible that you also want to have multitenant logs, for this the library automatically saves in an MDC context variable the value of the tenantId that arrives in the request (token or header), to be able to be used from the logback through an appender of type `"ch.qos.logback.classic.sift.SiftingAppender"` , below we can see three basic examples, one for file logs, another in the console and one to send to fluentd:

### File
Using this appender, a log file will be generated for each tenant in the folder indicated in the LOG_PATH property of the logback.

**logback-spring.xml**
```xml
<appender name="MultitenantFileAppender"
    class="ch.qos.logback.classic.sift.SiftingAppender">
    <discriminator>
        <key>tenantId</key>
        <defaultValue>noTenant</defaultValue>
    </discriminator>
    <sift>
        <appender name="fileAppender"
            class="ch.qos.logback.core.FileAppender">
            <file>${LOG_PATH}/${tenantId}.log</file>
            <rollingPolicy
                class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
                <fileNamePattern>${LOG_PATH}/${LOG_FILE}-${tenantId}-%d{yyyy-MM-dd}-${PID}_%i.log
                </fileNamePattern>
                <maxFileSize>100MB</maxFileSize>
                <maxHistory>5</maxHistory>
                <totalSizeCap>500MB</totalSizeCap>
            </rollingPolicy>
            <encoder>
                <pattern>%d [%thread] %level %mdc %logger{50} - %msg%n</pattern>
            </encoder>
        </appender>
    </sift>
</appender>
```

### Terminal
Using this appender we will print the logs by console, adding the tenant of each of the logs with the following format: `<TIME_STAMP> <LEVEL> [<TENANT_ID>] [<APP_NAME>,...] [<THREAD>]`

**logback-spring.xml**
```xml
<appender name="MultitenantStdoutAppender"
    class="ch.qos.logback.classic.sift.SiftingAppender">
    <discriminator>
        <key>tenantId</key>
        <defaultValue>noTenant</defaultValue>
    </discriminator>
    <sift>
        <appender name="stdoutAppender" class="ch.qos.logback.core.ConsoleAppender">
            <encoder>
                <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} %highlight(%-5level)[${tenantId}] [${APP_NAME:-},%X{X-B3-TraceId:-},%X{X-B3-SpanId:-},%X{X-Span-Export:-}] [%thread] %logger{50} - %msg%n</pattern>
            </encoder>
        </appender>
    </sift>
</appender>
```

### Fluentd
Finally we have an appender that modifies the tag with which the log is sent to fluentd, adding the tenant to the application name: `<APP_NAME>-<TENANT_ID>`

**logback-spring.xml**
```xml
<appender name="MultitenantFluentdAppender"
    class="ch.qos.logback.classic.sift.SiftingAppender">
    <discriminator>
        <key>tenantId</key>
        <defaultValue>noTenant</defaultValue>
    </discriminator>
    <sift>
        <appender name="FLUENT_TEXT"
            class="ch.qos.logback.more.appenders.DataFluentAppender">
            <tag>{{artifactId}}-${tenantId}</tag>
            <label>normal</label>
            <remoteHost>127.0.0.1</remoteHost><!--Servidor o túnel-->
            <port>24224</port>
            <useEventTime>false</useEventTime>
        </appender>
    </sift>
</appender>
```

Finally, as always, add the desired appenders to each profile in which you do want to use:

**logback-spring.xml**
```xml
<springProfile name="dev,default,local">
    <root level="INFO">
        <appender-ref ref="MultitenantStdoutAppender" />
        <appender-ref ref="MultitenantFileAppender" />
        (...)
    </root>
</springProfile>
```
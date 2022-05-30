# Librería Multitenant JPA

## Introducción

Una arquitectura multitenant nos permite que una única instancia de software sea usada por dos clientes distintos, por lo que de manera interna y de forma transparente al usuario se deberán tener divididos los datos para cada uno de ellos.

Con esta idea se ha creado esta librería, que nos acelerará el proceso de implementar esta arquitectura cuando hacemos uso de JPA en nuestros microservicios. Y que de manera casi automática, con una simple configuración, tendremos un microservicio multitenant, que accederá a una BD o a otra según el tenant que llegue con cada petición.

## Uso

Esta librería se utiliza para multitenant con JPA, la parte de spring data JPA no cambio frente a un proyecto normal, tan solo es importante que las DBs (de cada tenant) tengan exactamente la misma estructura.

Para usar la librería hay que incluirla dentro de nuestro `pom.xml`:

```xml
<dependency>
     <groupId>com.minsait.onesait.architecture</groupId>
     <artifactId>architecture-multitenant-jpa</artifactId>
     <version>${onesait.multitenant.version}</version>
</dependency>
```

## Configuración

Una vez añadida la librería deberemos añadir la siguiente configuración en el `application.yml`:

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

* **<TENANT_PROVIDER>**:(*Opcional) Indica de donde debe obtener la librería el tenant de la petición, tiene dos opciones: TOKEN(viene en un campo del token JWT) o HEADER(viene en un header). Por defecto el valor es TOKEN.
* **<TENANT_FIELD>**:(*Opcional) Indica el nombre del campo donde viene el tenant, tanto en el jwt como en la cabecera. Por defecto el valor es tenant para el token o X-TENANT-ID para el caso de que venga en el header.
* **<TENANT_ID_N>**: Identificador de cada uno de los tenants que se utilizarán.
* **<DB_URL_N>**: Url de la BD para cada uno de los tenants. Ej: jdbc:mysql://localhost:3306/uni_dev_A
* **<DB_USER_N>**: Usuario de la BD para cada uno de los tenants.
* **<DB_PASSWORD_N>**: Contraseña del usuario para cada uno de los tenants.
* **<DB_DRIVER_CLASS_N>**: Drive de la BD para cada uno de los tenants: Ej: com.mysql.cj.jdbc.Driver.

## Logs
Al tener un microservicio multitenant, es posible que también se quiera tener logs multitenant, para ello la librería de manera automática guardando en una variable de contexto MDC el valor del tenantId que llega en la petición(token o header), para poder ser usado desde el logback mediante un appender de tipo `"ch.qos.logback.classic.sift.SiftingAppender"` , a continuación podemos ver tres ejemplos básicos, uno para logs en fichero, otro en consola y uno para enviar a fluentd:

### Fichero
Con este appender se nos generará un fichero de log por cada tenant en la carpeta indicada en la propiedad LOG_PATH del logback.

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

### Consola
Con este appender nos imprimirá los logs por consola, añadiendo el tenant de cada uno de los logs con el siguiente formato: `<TIME_STAMP> <LEVEL> [<TENANT_ID>] [<APP_NAME>,...] [<THREAD>]`

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
Por último tenemos un appender que modifica la tag con la que se envía el log a fluentd, añadiendo el tenant al nombre de aplicacion: `<APP_NAME>-<TENANT_ID>`

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

Por ultimo, como siempre se deben añadir los appender deseados a cada profile en el que si quiera usar:

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
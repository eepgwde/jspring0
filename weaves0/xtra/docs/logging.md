# Spring0 - Logging

It is also very useful to have access to logging. This is also more
complicated than might appear.  There are a number of logging
frameworks. SLF4J provided a method of bridging them together to have
one configuration file.

Then one of the frameworks, Log4J, went to version 2. It provided new
frameworks and implemented the bridging to other frameworks.

## Spring uses logback

So logback.xml is the default system. It uses logback.xml

https://lankydan.dev/2019/01/09/configuring-logback-with-spring-boot

You can debug the logging system using

 mvn test -Dorg.springframework.boot.logging.LoggingSystem=none

Spring takes logging on the command-line of the VM
 -Dlogging.level.org.springframework=TRACE
 -Dlogging.level.com.example=INFO

Directly to a Jar
 java -jar target/spring-boot-XXX-0.0.1-SNAPSHOT.jar --trace
 mvn spring-boot:run
   -Dspring-boot.run.arguments=--logging.level.org.springframework=TRACE,--logging.level.com.baeldung=TRACE

## Logging Diagnostics

You can debug the logging system using

mvn test -Dorg.springframework.boot.logging.LoggingSystem=none

You can force Spring to use Log4j2 with this in your pom.xml

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-log4j2</artifactId>
    </dependency>

And using this on the command-line

 -Dorg.springframework.boot.logging.LoggingSystem=org.springframework.boot.logging.log4j2.Log4J2LoggingSystem

See https://programming.vip/docs/spring-boot2-using-log4j2.html

This will fail, you need to exclude the logging package.

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-logging</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-log4j2</artifactId>
</dependency>

And maybe set this:

  mvn test -Dorg.springframework.boot.logging.LoggingSystem=org.springframework.boot.logging.log4j2.Log
4J2LoggingSystem 2>&1 | tee make.log

You can diagnose what is going on with logging by using the Apache diagnostics.

     mvn test -Dtest=RunXtraTests -Dorg.apache.commons.logging.diagnostics.dest=STDOUT 2>&1 | tee make.log

You will need a file `commons-logging.properties` in a
src/test/resources. I use `xtra/weaves/src/test/resources` with the
`xtra` profile.

This file contains one line:
`org.apache.commons.logging.log=org.apache.commons.logging.impl.Log4JLogger`
and this needs the log4j implementation. Log4J can collect from other systems including
Apache. [See this at SLF4J](http://www.slf4j.org/legacy.html)

# Logging - Log4j version 1 over SLF4J

This is the older system. It still works.

There are two versions of log4j uses a log4j.properties file. (It also supports an XML version of that file.)

This can then interwork with SLF4J. This needs all these dependencies to be present in the pom.xml:

    <dependency>
      <groupId>log4j</groupId>
      <artifactId>log4j</artifactId>
      <version>1.2.17</version>
    </dependency>

    <dependency>
      <groupId>org.slf4j</groupId>
      <artifactId>slf4j-api</artifactId>
      <version>1.7.32</version>
    </dependency>

    <dependency>
	<groupId>org.slf4j</groupId>
	<artifactId>slf4j-log4j12</artifactId>
	<version>1.7.32</version>
	<scope>test</scope>
    </dependency>

    <dependency>
      <groupId>org.slf4j</groupId>
      <artifactId>jcl-over-slf4j</artifactId>
      <version>1.7.32</version>
    </dependency>

    <dependency>
      <groupId>org.slf4j</groupId>
      <artifactId>jul-to-slf4j</artifactId>
      <version>1.7.32</version>
    </dependency>

With that you can use a log4j.properties file. The levels have TRACE as lowest.

TRACE Level < DEBUG Level < INFO Level < WARN Level < ERROR Level < FATAL Level.

And there are ALL and OFF.

The whole Log4J version 1 properties file is given here, because they are easy to lose.
This traces at level DEBUG. The console only gets WARN and above. The file gets DEBUG
and above. The org.springframework classes are restricted to INFO and above.

    # Levels:
    # TRACE Level 	DEBUG Level 	INFO Level 	WARN Level 	ERROR Level 	FATAL Level

    # At debug level, two appenders to different outputs.
    log4j.rootLogger=DEBUG, consoleAppender, fileAppender

    ## On the console, only WARN and above 
    log4j.appender.consoleAppender=org.apache.log4j.ConsoleAppender
    log4j.appender.consoleAppender.layout=org.apache.log4j.PatternLayout
    log4j.appender.consoleAppender.layout.ConversionPattern=[%t] %-5p %c %x - %m%n

    log4j.appender.consoleAppender.filter.a=org.apache.log4j.varia.LevelRangeFilter
    log4j.appender.consoleAppender.filter.a.LevelMin=WARN
    log4j.appender.consoleAppender.filter.a.LevelMax=FATAL

    ## To file, we write all levels
    log4j.appender.fileAppender=org.apache.log4j.FileAppender
    log4j.appender.fileAppender.append=false
    log4j.appender.fileAppender.layout=org.apache.log4j.PatternLayout
    log4j.appender.fileAppender.layout.ConversionPattern=[%t] %-5p %c %x - %m%n
    log4j.appender.fileAppender.File=D:/weaves/spring0.log

    log4j.appender.fileAppender.filter.a=org.apache.log4j.varia.LevelRangeFilter
    log4j.appender.fileAppender.filter.a.LevelMin=DEBUG
    log4j.appender.fileAppender.filter.a.LevelMax=FATAL

    ### Restrict packages

    # Print only messages of level INFO or above in the package org.springframework and below.
    log4j.logger.org.springframework=INFO

# Logging over Log4J2 

SLF4J is able to bridge many logging frameworks. Log4j2 can now do that.

The POM.xml file contains the new logging framework. Here are the dependencies.

    <!-- log4j2 uses a bridge to log4j configured by log4j2.xml in resources -->
    <dependency>
      <groupId>org.apache.logging.log4j</groupId>
      <artifactId>log4j-1.2-api</artifactId>
      <version>2.14.1</version>
    </dependency>

    <!-- to Apache Commons Logging -->
    <dependency>
      <groupId>org.apache.logging.log4j</groupId>
      <artifactId>log4j-jcl</artifactId>
      <version>2.14.1</version>
    </dependency>

    <!-- to Java Util Logging -->
    <dependency>
      <groupId>org.apache.logging.log4j</groupId>
      <artifactId>log4j-jul</artifactId>
      <version>2.14.1</version>
    </dependency>

    <dependency>
      <groupId>org.apache.logging.log4j</groupId>
      <artifactId>log4j-api</artifactId>
      <version>2.14.1</version>
    </dependency>
    <dependency>
      <groupId>org.apache.logging.log4j</groupId>
      <artifactId>log4j-core</artifactId>
      <version>2.14.1</version>
    </dependency>



## Configuration

The new configuration is log4j2.xml. It is possible to use a log4j2.properties file too.

### Levels

The levels are now the other way round!

TRACE Level > DEBUG Level > INFO Level > WARN Level > ERROR Level > FATAL Level

### Example Log4j2.xml

    <?xml version="1.0" encoding="UTF-8"?>

    <!-- weaves -->
    <!-- LevelRangeFilter now has TRACE as maxLevel and FATAL as minLevel -->

    <Configuration status="debug" name="spring0">
      <Properties>
	<Property name="logPath">log</Property>
	<Property name="rollingFileName">spring0</Property>

	<Property name="logPattern">"[%highlight{%-5level}] %d{DEFAULT} %c{-10}.%M() - %msg%n%throwable{short.lineNumber}"</Property>
      </Properties>

      <ThresholdFilter level="debug"/>

      <Appenders>
	<Console name="console" target="SYSTEM_OUT" follow="true">
	  <PatternLayout pattern="${logPattern}" />
	  <LevelRangeFilter maxLevel="INFO" minLevel="FATAL" onMatch="ACCEPT" onMismatch="DENY"/>
	</Console>

	<RollingFile name="rollingFile" fileName="${logPath}/${rollingFileName}.log" 
		     filePattern="${logPath}/${rollingFileName}_%d{yyyy-MM-dd}-%i.log">
	  <PatternLayout pattern="${logPattern}" />
	  <LevelRangeFilter maxLevel="DEBUG" minLevel="FATAL" onMatch="ACCEPT" onMismatch="DENY"/>
	  <Policies>
	    <!-- Causes a rollover if the log file is older than the current JVM's start time -->
	    <OnStartupTriggeringPolicy />
	    <!-- Causes a rollover once the date/time pattern no longer applies to the active file -->
	    <TimeBasedTriggeringPolicy interval="1" modulate="true" />
	  </Policies>
	</RollingFile>
      </Appenders>

      <Loggers>

	<Logger name="org.springframework" level="info">
	  <!-- drops all org.springframework below info -->
	  <AppenderRef ref="rollingFile" />
	</Logger>

	<Logger name="de.acando" level="info">
	  <AppenderRef ref="console" />
	</Logger>

	<Root level="all">
	  <AppenderRef ref="rollingFile" />
	</Root>

      </Loggers>
    </Configuration>

# Using Logging in code

See AbstractAppPage.java for examples.

This application does not need strict logging. But using any use of 
System.out.println or System.err.println should be a log message. 
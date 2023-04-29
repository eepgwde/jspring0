# Spring0 - Logging

It is also very useful to have access to logging. This is also more
complicated than might appear.  There are a number of logging
frameworks.

One of the frameworks, Log4J, went to version 2. It provided new
frameworks and implemented the bridging to other frameworks.

## Spring uses logback

So logback.xml is the default system. It uses logback.xml and Spring also looks
for logback-spring.xml 
https://lankydan.dev/2019/01/09/configuring-logback-with-spring-boot

Spring takes logging on the command-line of the VM
 -Dlogging.level.org.springframework=TRACE
 -Dlogging.level.com.example=INFO

Directly to a Jar
 java -jar target/spring-boot-XXX-0.0.1-SNAPSHOT.jar --trace
 mvn spring-boot:run
   -Dspring-boot.run.arguments=--logging.level.org.springframework=TRACE,--logging.level.com.baeldung=TRACE

### Logging Diagnostics

You can debug the logging system using

   mvn test -Dorg.springframework.boot.logging.LoggingSystem=none

## Switching to Log4j2

It is problematic using log4j2. I recommend sticking with logback.

You can force Spring to use Log4j2 with this in your pom.xml

And using this on the command-line

  -Dorg.springframework.boot.logging.LoggingSystem=org.springframework.boot.logging.log4j2.Log4J2LoggingSystem

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

See https://programming.vip/docs/spring-boot2-using-log4j2.html

And maybe set this:

  mvn test
  -Dorg.springframework.boot.logging.LoggingSystem=org.springframework.boot.logging.log4j2.Log4J2LoggingSystem 2>&1 | tee make.log

You might be able to diagnose what is going on with logging by using the Apache diagnostics.

     mvn test -Dtest=RunXtraTests -Dorg.apache.commons.logging.diagnostics.dest=STDOUT 2>&1 | tee make.log

You will need a file `commons-logging.properties` in a
src/test/resources. I use `xtra/weaves/src/test/resources` with the
`xtra` profile.

This file contains one line:
`org.apache.commons.logging.log=org.apache.commons.logging.impl.Log4JLogger`
and this needs the log4j implementation. Log4J can collect from other systems including
Apache. [See this at SLF4J](http://www.slf4j.org/legacy.html)

## Configuring logback

https://github.com/qos-ch/logback/tree/master/logback-examples/src/main/resources/chapters/configuration

# Using Logging in code

If using logback it is best to use the org.slf4j packages.
The logging packages do vector to the underlying implementation.

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyApp1 {
  final static Logger logger = LoggerFactory.getLogger(MyApp1.class);

and then logger.debug(mesg, exception) 

This application does not need strict logging. But using any use of 
System.out.println or System.err.println should be a log message. 

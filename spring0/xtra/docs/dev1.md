# Spring0 - Developing with Spring Boot

Spring-Boot is further development of Spring. It provides the whole framework and packages
the project's classes to be used with other Spring-Boot projects.

Spring is a Dependency Injection system and Spring-Boot is an inversion of control
framework. That means rather than an application developer building a framework and
plugging bits of vendor code into it. This uses a vendor framework and the developer plugs
his code into that.

Dependency injection means that an object that is composed of other objects can easily be
created by annotating the class of the Composing Object. The objects that are injected with
an annotation are components of the software, just like the other classes, but because
they have been injected they are known as Beans. They are related the to the original
Beans used by Enterprise Java Beans in the early releases of the Java SDK.

The Spring Beans can initialize themselves by loading from properties files or databases.

Spring can be bewildering the first time you use it. It does help to remember how it
works. It uses the static main() method to carry out a ComponentScan, that will scan its
own packages first. If it finds any @Configuration annotated classes, it will scan those.

# This Application

This spring0 application is a working example of Spring-Boot. To run it use 

 mvn spring-boot:run
 
The program will run in the foreground, it is providing a web-server. You can kill it or
background it if you don't intend to do any testing with it as a web-server.

You can use: 

 mvn spring-boot:run -Dspring-boot.run.arguments="--exit" 
 
and it will exit directly.

It logs what beans are available. The log file contains a listing: use grep 'main] INFO'
to find them. Beans are through of as objects, so, by the rules of camel case, they
are in lower-case.

From the log file, you should be able to find 'helloController', but not 'element0'

# ComponentScan

Spring needs to be told to scan the packages for classes to use as Components.

https://www.baeldung.com/spring-component-scanning

In this Spring-Boot application, the Application class, in package com.example.springboot
has the annotation @SpringBootApplication. All packages where that tag has been used, and
all packages below it, are scanned.

The source of the class ai.centa.clients.Element0 also has annotations: @Component.

To include these, you need to explicitly add a @ComponentScan to include its package
directory. This is the line to be added after @SpringBootApplication.

    @ComponentScan(basePackages = { "com.example.springboot", "ai.centa.clients" })

If you now run mvn springboot:run and check the log files, you should see element0 
appears.

ComponentScan has to be at the entry point to the code: ie. main()

### XML 

It is useful to use an XML file to specify component scanning and to build beans.
These files are usually called applicationContext.xml

You can use one directly with: 

 @SpringBootApplication
 @ImportResource({"classpath*:applicationContext.xml"})
 
#### Using a Configuration class 
 
If you don't have access to the main() method - it may be in a JAR. You can search add a
class in the package where you believe main() is. And use ImportResource from there.

See com.example.springboot.Configurer 


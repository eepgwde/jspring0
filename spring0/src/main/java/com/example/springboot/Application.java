package com.example.springboot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.util.Arrays;

// ComponentScan
// Specify explicitly
// @ComponentScan(basePackages = { "com.example.springboot", "ai.centa.clients" })
// Or use an XML file on the classpath
// @ImportResource({"classpath*:applicationContext.xml"})

@SpringBootApplication
public class Application {

    final static Logger LOG = LoggerFactory.getLogger(Application.class);
    PropertySourcesPlaceholderConfigurer bean;

    protected static ApplicationContext applicationContext;

    public static void main(String[] args) {
        applicationContext = SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            LOG.info("Bean listing: " + Arrays.toString(args));
            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            for (String beanName : beanNames) {
                LOG.info(beanName);
                if (beanName.startsWith("applicationProperties")) {
                    bean = ctx.getBean(beanName, PropertySourcesPlaceholderConfigurer.class);
                }
            }

            for (String x : args) {
                if (x.equals("--exit")) {
                    LOG.info("exit called");
                    SpringApplication.exit(ctx);
                }
            }
            ai.centa.weaves0.Wrappers.it().halt();
        };
    }

}

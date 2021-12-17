package ai.centa.clients;

import ai.centa.weaves0.Wrappers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

// @Component


@Configuration
@PropertySource("classpath:application.properties")
public class Element0 {

    Element0() {
        Wrappers.it().halt();
    }

    @Value("${java.io.tmpdir}") String tdir;

    @Value("${element0.name}") String name;

}
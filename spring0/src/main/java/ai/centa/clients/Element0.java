package ai.centa.clients;

import ai.centa.weaves0.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

// @Component

@Component
public class Element0 {

    public Element0() {
        Wrappers.it().halt();
    }

    @Value("string value")
    public String stringValue;

    @Value("${java.io.tmpdir}")
    public String tdir;

    @Value("${element0.name}") public String name;

    public String name0;

    @Autowired
    public void setValues(@Value(value = "some value") String value) {
        this.name0 = value;
    }

    @PostConstruct
    void audit() {
        Wrappers.it().halt();
    }

    @Autowired
    PropertySourcesPlaceholderConfigurer applicationProperties0;

}

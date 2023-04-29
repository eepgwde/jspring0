package ai.centa.clients;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.Serializable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ai.centa.weaves0.Wrappers;

// import static org.junit.Assert.assertNotNull;

/*** Testing for the util.Wrappers singleton. ***/
// @FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SpringBootTest(classes = com.example.springboot.Application.class)
public class TestWrappers {
    private static final Log LOG = LogFactory.getLog(TestWrappers.class);
    static int counter = -1;

    @Autowired
    Element0 element0;

    @BeforeEach
    public void setUp() throws Exception {
        LOG.info(element0.tdir);
        LOG.info(element0.name);
        LOG.info(element0.stringValue);
        LOG.info(element0.name0);
    }

    @Test
    public void feature00() {
        LOG.info("instance");
        Wrappers w = Wrappers.it();
        assertNotNull(w);
        LOG.debug(w.toString());
        Throwable exception = assertThrows(UnsupportedOperationException.class, () -> {
            throw new UnsupportedOperationException("Not supported");
        });
        assertEquals(exception.getMessage(), "Not supported");
    }

    // This is useful!
    @Test
    public void feature02() {
        LOG.info("instance: stackTrace");
        Throwable e = assertThrows(RuntimeException.class, () -> {
            throw new RuntimeException("trace");
        });

        java.lang.Throwable t = (Throwable) e;
        StringBuffer sb = Wrappers.it().stackTrace(t);
        LOG.info(sb.toString());
    }

}

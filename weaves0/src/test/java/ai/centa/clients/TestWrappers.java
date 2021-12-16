package clients;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import util.Wrappers;

import java.io.File;
import java.io.Serializable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.Assert.assertNotNull;

/*** Testing for the util.Wrappers singleton. ***/
// @FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(JUnitPlatform.class)
@SelectPackages("ai.centa.clients")
public class TestWrappers {
    private static final Log LOG = LogFactory.getLog(TestWrappers.class);
    static int counter = -1;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void feature00() {
        LOG.info("instance");
        Wrappers w = Wrappers.it();
        assertNotNull(w);
        LOG.debug(w.toString());
    }

    // This is useful!
    @Test(expected = RuntimeException.class)
    public void feature02() {
        LOG.info("instance: stackTrace");
        RuntimeException e = new RuntimeException("trace");
        java.lang.Throwable t = (Throwable) e;
        StringBuffer sb = Wrappers.it().stackTrace(t);
        LOG.info(sb.toString());
        throw e;
    }

    @Test
    public void feature04() {
        LOG.info("instance: stackTrace: all in one");
        LOG.debug(Wrappers.it().stackTrace(new RuntimeException("feature04")).toString());
    }

    @Test
    public void feature06() {
        LOG.info("push some markers");

        String s ;

        s = Wrappers.it().incMarker("feature06");
        LOG.info(s);

        s = Wrappers.it().incMarker("feature06");
        LOG.info(s);

        LOG.info(Wrappers.it().getMarkers());
        LOG.info(Wrappers.it().getMarkers(true));
    }

    @Test
    public void feature08() {
        LOG.info("push some states");

        String s;

        s = Wrappers.it().pushState("feature06", "feature06-state");
        LOG.info(s);

        s = Wrappers.it().pushState("feature06", "feature06-state2");
        LOG.info(s);

        s = Wrappers.it().pushState("feature06", "feature06-state");
        LOG.info(s);
    }

    @Test
    public void feature10() {
        Stream<String> streamA = Stream.of("A", "B", "C");
        Stream<String> streamB  = Stream.of("Apple", "Banana", "Carrot", "Doughnut");

        Wrappers.Zipper<String, String, Map.Entry<String, String>> zipper;
        zipper = new Wrappers.Zipper<String, String, Map.Entry<String, String>>();

        Stream<Map.Entry<String, String>> list = Wrappers.it().zip(streamA, streamB, zipper);

        list.forEach(o ->LOG.info(o));
    }

    @Test
    public void feature20() {
        LOG.info("write to a directory: " + System.getProperty("java.io.tmpdir"));

        Set<Serializable> m = Wrappers.it().getStates(Wrappers.OTYPE.CHARACTER);

        Supplier<Stream<Map.Entry<File,Serializable>>> supplier
                = () -> Wrappers.it().toStream(m, null);

        Stream<Map.Entry<File,Serializable>> l = supplier.get();
        if (!supplier.get().findAny().isPresent()) {
            LOG.info("list is empty");
            return;
        }
        supplier.get().forEach(x -> LOG.info(x));

        Wrappers.it().toDisk(supplier.get(), Wrappers.OTYPE.CHARACTER);
    }

}

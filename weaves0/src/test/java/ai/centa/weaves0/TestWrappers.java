package ai.centa.weaves0;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.Formatter;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestWrappers {
    private static final Log LOG = LogFactory.getLog(TestWrappers.class);
    static int counter = -1;

    public class Failer implements FunctionWithException<Integer, String, RuntimeException>,
            Function0WithException<String, RuntimeException> {
        StringBuilder sbuf = new StringBuilder();
        Formatter fmt = new Formatter(sbuf);

        @Override
        public String apply(Integer i0) throws RuntimeException {
            String result;
            boolean isEven = 0 == (i0.intValue() % 2);

            String tag = isEven ? "even" : "odd";
            fmt.format("int: %d is %s", i0, tag);
            result = (String) sbuf.toString();

            if (!isEven)
                throw new RuntimeException(result);
            return result;
        }

        @Override
        public String apply() throws RuntimeException {
            counter++;
            String result;
            boolean isEven = 0 == (counter % 2);

            String tag = isEven ? "even" : "odd";
            fmt.format("int: %d is %s", counter, tag);
            result = (String) sbuf.toString();

            if (!isEven)
                throw new RuntimeException(result);
            return result;
        }

    }

    Failer failer;
    Wrappers fwrapper; // same package, so I don't need a singleton.

    @Before
    public void setUp() throws Exception {
        failer = new Failer();
        // In its own package, it is not a Singleton.
        fwrapper = new Wrappers();
    }

    @Test
    public void test00Even() {
        LOG.info("even: result expected");
        String s = failer.apply(2);
        LOG.info(s);
    }

    @Test(expected = RuntimeException.class)
    public void test02Odd() {
        LOG.info("odd: exception before result");
        String s = failer.apply(1);
        LOG.info(s);
    }

    @Test
    public void test04Even() {
        LOG.info("even: result expected");
        Function<Integer, Optional<String>> f1 = fwrapper.wrapper(i0 -> failer.apply(i0));
        Optional<String> s = f1.apply(2);
        LOG.info(s.orElse("empty"));
    }

    @Test
    public void test06Odd() {
        LOG.info("odd: odd, internal exception, empty is expected");
        Function<Integer, Optional<String>> f1 = fwrapper.wrapper(i0 -> failer.apply(i0));
        Optional<String> s = f1.apply(1);
        LOG.info(s.orElse("empty"));
    }

    @Test
    public void test20Even() {
        LOG.info("even: result expected");
        Supplier<Optional<String>> f1 = fwrapper.wrapper(() -> failer.apply());
        Optional<String> s = f1.get();
        LOG.info(s.orElse("empty"));
    }

    @Test
    public void test22odd() {
        LOG.info("odd: internal exception, empty is expected");
        Supplier<Optional<String>> f1 = fwrapper.wrapper(() -> failer.apply());
        Optional<String> s = f1.get();
        LOG.info(s.orElse("empty"));
    }

    @Test
    public void properties30() {
        LOG.info("properties: " + fwrapper.properties.toString());
    }

}

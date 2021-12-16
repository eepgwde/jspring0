package ai.centa.weaves0;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Utility singleton that also provides methods to wrap other methods that return exceptions.
 * <p>
 * The wrapper functions use some functional programming together with java.util.Optional to do
 * the wrapping.
 * <p>
 * For a function in Failer::fwithex that takes an integer and returns a string.
 * We use Wrapper to create a lambda and apply it.
 *
 * <pre>
 * Function&lt;Integer, Optional&lt;String&gt;&gt; f1 = w.wrapper(i0 -&gt; failer.fwithex(i0));
 * Optional&lt;String&gt; s = f1.apply(2);
 * </pre>
 * <p>
 * It must be used as a Singleton outside its own package. It has a logger and formatters.
 * <p>
 * See the test class {@link clients.TestWrappers} for other examples of use.
 *
 * @implNote This test utility has been placed in the src/main/java section. That is not strictly necessary.
 * Usually with test utilities, they have to be deactivated when running in production.
 */
public class Wrappers {

    /**
     * Some initialization code to load a properties file on the classpath.
     *
     * @param path location within src/test/resources of the path.
     * @return a properties instance.
     */
    protected static Properties load(String path) {
        Properties prop = new Properties();

        try (InputStream inputStream = Wrappers.class.getResourceAsStream(path)) {
            prop.load(inputStream);
        } catch (Exception e) {
            int nop = 1; // for breakpoints
        }
        return (prop);
    }

    /**
     * location of the properties file in the classpath.
     */
    protected Properties properties = load("/Wrappers.properties");
    /**
     * Default debug level is 0
     * When the level is zero, {@link incMarker} and {@link pushState} will not collect.
     */
    protected Integer debugLevel = 0;

    /** Destination for captured state files. */
    protected File logDir = null;

    /**
     * The Wrappers object is a singleton.
     * Check the properties to see the degree of debug needed. Non-existent or zero disables most functions.
     * And runs a task on shutdown {@link OnShutdown}.
     */
    protected Wrappers() {
        String string = null;

        string = properties.getProperty("debug.level", "0");
        this.debugLevel = Integer.parseInt(string);
        log_.debug("debug.level: " + string + " " + this.debugLevel);

        string = properties.getProperty("debug.logdir", System.getProperty("java.io.tmpdir"));
        this.logDir = new File(string);

        if (debugLevel > 0) {
            OnShutdown shutDownTask = new OnShutdown(this);
            Runtime.getRuntime().addShutdownHook(shutDownTask);
        }
    }

    /**
     * the singleton instance.
     */
    static private Wrappers this_ = null;

    /**
     * A helper that adds a method to be run at shutdown.
     *
     * @implNote Not run if debugLevel is 0.
     */
    public static class OnShutdown extends Thread {
        Wrappers owner;

        OnShutdown(Wrappers owner) {
            this.owner = owner;
        }

        /**
         * On shutdown use Wrappers to dump some state.
         */
        @Override
        public void run() {
            owner.log().debug("shutdown: markers: " + owner.getMarkers(true).toString());
            owner.snapshot(owner.logDir);
        }
    }

    /**
     * The singleton.
     *
     * @return the reference {@link this_} to the only Wrappers instance.
     */
    public static Wrappers it() {
        if (this_ == null) this_ = new Wrappers();
        return this_;
    }

    /**
     * This provides a network of breakpoints.
     */
    public void halt() {
        if (debugLevel > 0) {
            log_.debug("halt");
        }
    }

    public <T> void halt(Exception e, T... args) {
        if (debugLevel > 0) {
            log_.debug("halt");
            for (T arg : args) {
                log_.debug(arg);
            }
            StringBuffer s = stackTrace(e);
            log_.debug(s);
        }
    }

    /// For logging
    protected static final Log log_ = LogFactory.getLog(Wrappers.class);

    /// For log messages
    protected StringBuilder sbuf_ = new StringBuilder();
    /// For log messages
    protected Formatter fmt_ = new Formatter(sbuf_);

    /**
     * Get the singleton's log.
     *
     * @return a log from the logging system.
     */
    public Log log() {
        return log_;
    }

    /**
     * Get a string from the string builder and then reset it.
     * The string will contain whatever was formatted.
     *
     * @return the string buffer used by {@link fmt_}
     */
    public String sbuf() {
        String s1 = sbuf_.toString();
        sbuf_.delete(0, sbuf_.length());
        return s1;
    }

    /**
     * Get the formatter so that you can format messages use {@link sbuf()} to get the result.
     *
     * @return the formatter.
     */
    public Formatter fmt() {
        return fmt_;
    }

    // Used by {@link stackTrace}.
    StringWriter writer = new StringWriter(1024);
    protected PrintWriter printWriter = new PrintWriter(writer, true);

    /**
     * Prepare a stackTrace for logging.
     * If you don't know where you are or want to know when a piece of code is activated.
     * Create a new exception and take its stack trace.
     * A useful one liner is this:
     * <code>LOG.debug(Wrappers.it().stackTrace(new RuntimeException("feature04")).toString());</code>
     *
     * @param e an exception with a stack trace.
     * @return the stack trace of the throwable as a single string with embedded carriage-line returns.
     */
    public StringBuffer stackTrace(java.lang.Throwable e) {
        synchronized (writer) {
            e.printStackTrace(printWriter);
            return (writer.getBuffer());
        }
    }

    /**
     * This is used to collect state markers whilst debugging.
     */
    protected Map<String, Integer> states_ = new HashMap<>();

    /**
     * All the states collected by {@link pushState()}
     */
    protected Set<java.io.Serializable> states1_ = new HashSet<Serializable>();

    /**
     * Get the collected states.
     * This returns {@link states1_} which is collected from {@link pushState()}
     *
     * @param type0 is a dummy parameter - it distinguishes this from the other method.
     * @return the set of all the states collected, {@link states1_}
     */
    public Set<java.io.Serializable> getStates(OTYPE type0) {
        return states1_;
    }

    /**
     * Get the state index and the collection of states.
     * This is the results of using {@link pushState()}.
     * The first map is of the state name and the hashcode of the object that were pushed. This is the index.
     * The second map is of the hashcode to the underlying object. This is the collection.
     *
     * @return a tuple of the index and the collection of states.
     */
    public Map.Entry<Map<String, Integer>, Map<Integer, java.io.Serializable>> getStates() {
        Map<Integer, Serializable> result =
                states1_.stream().collect(Collectors.toMap(Serializable::hashCode,
                        Function.identity()));

        Map.Entry<Map<String, Integer>, Map<Integer, java.io.Serializable>> entry =
                new AbstractMap.SimpleEntry<Map<String, Integer>, Map<Integer, java.io.Serializable>>(states_, result);
        return entry;
    }

    public enum OTYPE {
        SERIALIZED,
        CHARACTER
    }

    /**
     * This prepares a data set to be written to disk.
     * <p>
     * File names are generated from the hashcode of the items. A w- string is used a prefix.
     *
     * @param items
     * @param ddir  the destination directory, if null a temporary directory is created.
     * @return a stream of zipped file and serializable objects.
     * @apiNote Because a Stream object is designed to be used once, you should use a Supplier to regenerate the
     * stream.
     * {@code Supplier<Stream<Map.Entry<File,Serializable>>> splr = () -> Wrapper.it().toStream(this, type0, ddir)}
     */
    public Stream<Map.Entry<File, Serializable>> toStream(Set<Serializable> items, File ddir) {
        Stream<Map.Entry<File, Serializable>> result = Stream.empty();
        File tdir;
        try {
            if (ddir == null) tdir = Files.createTempDirectory("w-").toFile();
            else tdir = ddir;
        } catch (IOException io) {
            return (result);
        }

        // make some safe file names
        List<File> fnames = items.stream().map(
                x -> new File(tdir, "w-0x" + Integer.toHexString(x.hashCode()))
                ).collect(Collectors.toList());

        // get a Zipper from Wrappers.
        Wrappers.Zipper<File, Serializable, Map.Entry<File, Serializable>> zipper
                = new Wrappers.Zipper<File, Serializable, Map.Entry<File, Serializable>>();

        result = Wrappers.it().zip(fnames.stream(), items.stream(), zipper);

        return (result);
    }

    protected void s2fWrite(File f1, Serializable s) {
        try {
            FileWriter fileWriter = new FileWriter(f1);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.print(s);
            printWriter.close();
        } catch (IOException io) {
            fmt_.format("s2fwrite failed: %s", f1.toString(), (s != null) ? "non-null string" : "null string");
            log_.warn(sbuf());
        }
    }

    /**
     * Write a stream of tuples of filenames and contents to disk.
     * This works with {@link toStream()}.
     *
     * @param strm  a zipped stream of pairs: filename and serializable contents.
     * @param type0 whether to write as characters or as binary serialized.
     * @apiNote IO exceptions are swallowed
     */
    public void toDisk(Stream<Map.Entry<File, Serializable>> strm, OTYPE type0) {
        strm.forEach(x -> s2fWrite(x.getKey(), x.getValue()));
    }

    /**
     * Make a snapshot to disk of the state variables.
     */
    public void snapshot(File ddir) {
        Set<Serializable> m = getStates(Wrappers.OTYPE.CHARACTER);

        // Use toStream to get some filenames.
        Supplier<Stream<Map.Entry<File, Serializable>>> supplier
                = () -> toStream(m, ddir);

        Stream<Map.Entry<File, Serializable>> l = supplier.get();
        if (!supplier.get().findAny().isPresent()) {
            log_.warn("snapshot: list is empty");
            return;
        }
        toDisk(supplier.get(), Wrappers.OTYPE.CHARACTER);
    }

    /**
     * Store an object against a versioned marker string.
     * <p>
     * The marker string is stored in {@link markers_}. Its
     *
     * @param marker is a string, it will be put into {@link markers_}
     * @param state  any serialisable object is stored
     * @return
     */
    public String pushState(String marker, java.io.Serializable state) {
        if (debugLevel <= 0) return (incMarker(marker));

        String s = incMarker(marker);
        Integer hc = state.hashCode();
        log_.debug("pushState: marker: " + s + " " + Integer.toHexString(hc));
        states_.put(s, hc);
        states1_.add(state);
        return s;
    }

    /**
     * Use this to hold state counters in debugging.
     */
    protected Map<String, Integer> markers_ = new HashMap<>();

    public Map<String, Integer> getMarkers() {
        return markers_;
    }

    public Map<String, String> getMarkers(boolean hex) {
        Stream<String> v = markers_.values().stream().map( x -> "0x" + Integer.toHexString(x));
        Stream<String> k = markers_.keySet().stream();

        Wrappers.Zipper<String, String, Map.Entry<String, String>> zipper;
        zipper = new Wrappers.Zipper<String, String, Map.Entry<String, String>>();
        Stream<Map.Entry<String, String>> list = Wrappers.it().zip(k, v, zipper);
        return list.collect(Collectors.toMap(p -> p.getKey(), p-> p.getValue()));
    }

    public String incMarker(String s) {
        if (debugLevel < -0) {
            fmt_.format("%s: %d", s, 0);
            return sbuf();
        }

        Integer i;
        if (markers_.containsKey(s)) {
            i = markers_.get(s);
            i++;
            // This should not be necessary.
            markers_.put(s, i);
        } else {
            i = 1;
            markers_.put(s, i);
        }

        fmt_.format("%s: %d", s, i);
        return sbuf();
    }

    /**
     * Provides an apply method for {@link zip()}
     *
     * @param <K>
     * @param <V>
     * @param <X>
     * @Note I often use {@code Map.Entry<K,V>} like Pair or Tuple.
     * This has a cast to the output form X.
     */
    public static class Zipper<K, V, X> implements BiFunction<K, V, X> {
        @Override
        public X apply(K k, V v) {
            Map.Entry<K, V> entry = new AbstractMap.SimpleEntry<K, V>(k, v);
            return (X) entry;
        }
    }

    /**
     * Zip two streams together.
     * {@link https://stackoverflow.com/questions/17640754/zipping-streams-using-jdk8-with-lambda-java-util-stream-streams-zip/46230233#46230233}
     *
     * To get a zipper use this.
     {@code
     Wrappers.Zipper<String, String, Map.Entry<String, String>> zipper;
     zipper = new Wrappers.Zipper<String, String, Map.Entry<String, String>>();
     }
     *
     * @param a      one stream
     * @param b      the other
     * @param zipper a function to join the two usually {@code Map.Entry<K, V>}
     * @param <A>    Some stream type
     * @param <B>    Some stream type
     * @param <C>    Some output stream type
     * @return a zipped stream, one-to-one pairs from a and b.
     */
    public <A, B, C> Stream<C> zip(Stream<? extends A> a,
                                   Stream<? extends B> b,
                                   BiFunction<? super A, ? super B, ? extends C> zipper) {
        Objects.requireNonNull(zipper);
        Spliterator<? extends A> aSpliterator = Objects.requireNonNull(a).spliterator();
        Spliterator<? extends B> bSpliterator = Objects.requireNonNull(b).spliterator();

        // Zipping looses DISTINCT and SORTED characteristics
        int characteristics = aSpliterator.characteristics() & bSpliterator.characteristics() &
                ~(Spliterator.DISTINCT | Spliterator.SORTED);

        long zipSize = ((characteristics & Spliterator.SIZED) != 0)
                ? Math.min(aSpliterator.getExactSizeIfKnown(), bSpliterator.getExactSizeIfKnown())
                : -1;

        Iterator<A> aIterator = Spliterators.iterator(aSpliterator);
        Iterator<B> bIterator = Spliterators.iterator(bSpliterator);
        Iterator<C> cIterator = new Iterator<C>() {
            @Override
            public boolean hasNext() {
                return aIterator.hasNext() && bIterator.hasNext();
            }

            @Override
            public C next() {
                return zipper.apply(aIterator.next(), bIterator.next());
            }
        };

        Spliterator<C> split = Spliterators.spliterator(cIterator, zipSize, characteristics);
        return (a.isParallel() || b.isParallel())
                ? StreamSupport.stream(split, true)
                : StreamSupport.stream(split, false);
    }

    // Functional wrappers follow

    /**
     * This provides a lambda function that wraps another function that throws an exception.
     * If the function does throw an exception, then the Optional does not have a result present.
     * This version supports a function that takes one argument.
     *
     * @param fe  the function to wrap, often use class notation Class::method.
     * @param <T> The type of input parameter
     * @param <R> The type of the result, usually an Optional
     * @param <E> The type of the Exception
     * @return a lambda function that takes one argument and returns an Optional.
     */
    public <T, R, E extends Exception>
    Function<T, Optional<R>> wrapper(FunctionWithException<T, R, E> fe) {
        return arg -> {
            Optional<R> r;
            try {
                r = Optional.of(fe.apply(arg));
            } catch (Exception e) {
                r = Optional.empty();
            }
            return r;
        };
    }

    /**
     * The no parameter version of {@link wrapper()}
     *
     * @param fe
     * @param <R>
     * @param <E>
     * @return
     */
    public <R, E extends Exception>
    Supplier<Optional<R>> wrapper(Function0WithException<R, E> fe) {
        return () -> {
            Optional<R> r;
            try {
                r = Optional.of(fe.apply());
            } catch (Exception e) {
                r = Optional.empty();
            }
            return r;
        };
    }
}

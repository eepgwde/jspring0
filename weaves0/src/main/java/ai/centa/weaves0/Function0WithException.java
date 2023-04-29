package ai.centa.weaves0;


/** A template for functions that return exceptions.
 * Used for trapping exceptions.
 *
 * @param <R> Return type
 * @param <E> Exception type
 */
@FunctionalInterface
public interface Function0WithException<R, E extends Exception> {
    R apply() throws E;
}
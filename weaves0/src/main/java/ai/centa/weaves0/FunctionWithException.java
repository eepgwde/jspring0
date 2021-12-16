package ai.centa.weaves0;


/** A template for functions that return exceptions.
 * Used for trapping exceptions.
 *
 * @param <T> Input parameter
 * @param <R> Return type
 * @param <E> Exception type
 */
@FunctionalInterface
public interface FunctionWithException<T, R, E extends Exception> {
    R apply(T t) throws E;
}
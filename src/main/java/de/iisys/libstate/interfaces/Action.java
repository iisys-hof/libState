package de.iisys.libstate.interfaces;

/**
 * An Action defines a piece of code to execute within a given context T.<br>
 * It throws every exception by default for delegated exception handling.
  * @param <T> the context the action is running in
 */
@FunctionalInterface
public interface Action<T> {

    /**
     * Executes the defined piece of code in the given context.
     *
     * @param parent the given context
     * @throws Exception
     */
    void run(T parent) throws Exception;

}

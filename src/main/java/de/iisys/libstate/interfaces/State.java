package de.iisys.libstate.interfaces;

import java.util.List;
import java.util.Map;

/**
 * A State is a context with internally held memory executing {@link Action}s
 * that is able to transit between each other by given {@link Transition}s.
  */
public interface State {

    /**
     * Set the internal memory of the state to the given {@link Map}.
     *
     * @param memory the memory to set
     */
    void setMemory(Map<Object, Object> memory);

    /**
     * Returns the internal memory {@link Map} of the state by identity.
     *
     * @return the memory
     */
    Map<Object, Object> getMemory();

    /**
     * Returns the internal memory {@link Map} of the state by cloning it so
     * that future changes of the clone do not change the internal memory.
     *
     * @return a new clone of the memory
     */
    Map<Object, Object> cloneMemory();

    /**
     * Returns a value saved in the internal memory that is saved under the
     * given key.
     *
     * @param <T> the type of the value to automatically cast to
     * @param key the key to look up
     * @return the value that is saved under the given key, or null if not found
     */
    <T> T get(Object key);

    /**
     * Saves a value under the given key in the internal memory and returns it.
     *
     * @param <T> the type of the value given
     * @param key the key under which the value will be saved
     * @param value the value to save
     * @return the same value given
     */
    <T> T put(Object key, T value);

    /**
     * Removes the value saved under the given key and returns it.
     *
     * @param <T> the type of the value to automatically cast to
     * @param key the key to remove with its saved value
     * @return the value that was saved under the key, may be null
     */
    <T> T remove(Object key);

    /**
     * The {@link Action} to execute when the state is entered from a different
     * state. Will not be executed when the previous state was the same.
     *
     * @return the defined action
     */
    Action<State> entryAction();

    /**
     * The {@link Action} to always execute within this state, also when
     * {@link Transition}s do round trips.
     *
     * @return the defined action
     */
    Action<State> doAction();

    /**
     * The {@link Action} to execute when the state is left towards a different
     * state. Will not be executed when the next state is the same.
     *
     * @return the defined action
     */
    Action<State> exitAction();

    /**
     * Sets the {@link Transition}s for transfering between this state and the
     * other states defined within the given transitions.
     *
     * @param transitions the transitions valid for this state
     */
    void setTransitions(List<Transition<State, State>> transitions);

    /**
     * Returns the defined {@link Transition}s of this state that connect this
     * state with other states defined within the returned transitions.
     *
     * @return the transitions
     */
    List<Transition<State, State>> getTransitions();

}

package de.iisys.libstate;

import de.iisys.libstate.interfaces.Action;
import de.iisys.libstate.interfaces.State;
import de.iisys.libstate.interfaces.Transition;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Class that defines every method on interface {@link State}. A State is a
 * context with internally held memory executing {@link Action}s that is able to
 * transit between each other by given {@link Transition}s.
  */
public class BasicState implements State {

    private Object identification;

    private Map<Object, Object> memory;
    private List<Transition<State, State>> transitions;

    private Action<State> entryAction;
    private Action<State> doAction;
    private Action<State> exitAction;

    /**
     * Initializes {@link #identification} of {@link BasicState} with the given
     * identification object.
     *
     * @param identification
     */
    public BasicState(Object identification) {
        this(identification, null);
    }

    /**
     * Initializes {@link #identification} and {@link #doAction} of
     * {@link BasicState} with the given objects.
     *
     * @param identification
     * @param doAction starts the action
     */
    public BasicState(Object identification, Action<State> doAction) {
        this(identification, null, doAction, null);
    }

    /**
     * Initializes
     * {@link #identificatio}, {@link #entryAction}, {@link #doAction} and
     * {@link #exitAction} with the given objects and creates a new HashMap.
     *
     * @param identification
     * @param entryAction
     * @param doAction
     * @param exitAction
     */
    public BasicState(Object identification, Action<State> entryAction, Action<State> doAction, Action<State> exitAction) {
        this.identification = identification;
        this.entryAction = entryAction;
        this.doAction = doAction;
        this.exitAction = exitAction;

        setMemory(new HashMap<>());
    }

    /**
     * Multiplys the hash value with 11 and adds the identification objects.
     *
     * @return the hash
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 11 * hash + Objects.hashCode(this.identification);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BasicState other = (BasicState) obj;
        return Objects.equals(this.identification, other.identification);
    }

    /**
     * Gets the state identification.
     *
     * @return the state identification
     */
    public Object getIdentification() {
        return identification;
    }

    @Override
    public void setMemory(Map<Object, Object> memory) {
        this.memory = memory;
    }

    /**
     * Gets the internal memory of the state to the given {@link Map}.
     *
     * @return the memory to get
     */
    @Override
    public Map<Object, Object> getMemory() {
        return memory;
    }

    /**
     * Returns the internal memory {@link Map} of the state by cloning it so
     * that future changes of the clone do not change the internal memory.
     *
     * @return return the cloned memory {@link Map}
     */
    @Override
    public Map<Object, Object> cloneMemory() {
        return new HashMap<>(getMemory());
    }

    /**
     * Returns a value saved in the internal memory that is saved under the
     * given key.
     *
     * @param <T> the type of the value to automatically cast to
     * @param key the key to look up
     * @return the value that is saved under the given key, or null if not found
     */
    @Override
    public <T> T get(Object key) {
        return (T) getMemory().get(key);
    }

    /**
     * Saves a value under the given key in the internal memory and returns it.
     *
     * @param <T> the type of the value given
     * @param key the key under which the value will be saved
     * @param value the value to save
     * @return the same value given
     */
    @Override
    public <T> T put(Object key, T value) {
        getMemory().put(key, value);
        return value;
    }

    /**
     * Removes the value saved under the given key and returns it.
     *
     * @param <T> the type of the value to automatically cast to
     * @param key the key to remove with its saved value
     * @return the value that was saved under the key, may be null
     */
    @Override
    public <T> T remove(Object key) {
        return (T) getMemory().remove(key);
    }

    /**
     * The {@link Action} to execute when the state is entered from a different
     * state. Will not be executed when the previous state was the same.
     *
     * @return the defined action
     */
    @Override
    public Action<State> entryAction() {
        return entryAction;
    }

    /**
     * The {@link Action} to always execute within this state, also when
     * {@link Transition}s do round trips.
     *
     * @return the defined action
     */
    @Override
    public Action<State> doAction() {
        return doAction;
    }

    /**
     * The {@link Action} to execute when the state is left towards a different
     * state. Will not be executed when the next state is the same.
     *
     * @return the defined action
     */
    @Override
    public Action<State> exitAction() {
        return exitAction;
    }

    /**
     * Sets the {@link Transition}s for transfering between this state and the
     * other states defined within the given transitions.
     *
     * @param transitions the transitions valid for this state
     */
    @Override
    public void setTransitions(List<Transition<State, State>> transitions) {
        this.transitions = transitions;
    }

    /**
     * Returns the defined {@link Transition}s of this state that connect this
     * state with other states defined within the returned transitions.
     *
     * @return the transitions
     */
    @Override
    public List<Transition<State, State>> getTransitions() {
        return transitions;
    }

    @Override
    public String toString() {
        return "BasicState{" + "identification=" + identification + '}';
    }

}

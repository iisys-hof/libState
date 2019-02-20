package de.iisys.libstate.interfaces;

import java.util.function.Function;

/**
 * A transition maps one state to another depending on the defined conditions.
 * It can execute an {@link Action} as well while transfering.
  * @param <S> the source state, may be null if destination is first state
 * @param <D> the destination state, may be null if source is last state
 */
public interface Transition<S extends State, D extends State> {

    /**
     * Defines the {@link Action} to execute while transferring between the two
     * states.
     *
     * @return the action
     */
    Action<Transition<S, D>> action();

    /**
     * Defines the condition to check if this transition may be executed within
     * the given context of the two states given.
     *
     * @return the condition
     */
    Function<Transition<S, D>, Boolean> condition();

    /**
     * Returns the source {@link State} of this transition.
     *
     * @return the source state, may be null if the destination is the first
     * state
     */
    default S getSource() {
        return null;
    }

    /**
     * Returns the destination {@link State} of this transition.
     *
     * @return the destination state, may be null if the source is the last
     * state
     */
    default D getDestination() {
        return null;
    }

}

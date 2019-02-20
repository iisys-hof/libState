package de.iisys.libstate;

import de.iisys.libstate.interfaces.Action;
import de.iisys.libstate.interfaces.State;
import de.iisys.libstate.interfaces.Transition;
import java.util.function.Function;

/**
 * Class that defines every method on {@link State}. A transition maps one state
 * to another depending on the defined conditions. It can execute an
 * {@link Action} as well while transfering.
  * @param <S>
 * @param <D>
 */
public class BasicTransition<S extends State, D extends State> implements Transition<S, D> {

    private S source;
    private D destination;

    private Action<Transition<S, D>> action;
    private Function<Transition<S, D>, Boolean> condition;

    /**
     * Initializes the source {@link State} of the transition and the
     * destination the {@link State} of the transition.
     *
     * @param source the source of the transition
     * @param destination the destination of the transition
     */
    public BasicTransition(S source, D destination) {
        this(source, destination, null);
    }

    /**
     * Initializes the source {@link State} of the transition and the
     * destination the {@link State} of the transition and the action to execute
     * while transferring between the two states.
     *
     * @param source the source of the transition
     * @param destination the destination of the transition
     * @param action the action to execute
     */
    public BasicTransition(S source, D destination, Action<Transition<S, D>> action) {
        this(source, destination, action, null);
    }

    /**
     * Initializes the source {@link State} of the transition and the
     * destination the {@link State} of the transition and the action to execute
     * while transferring between the two states and the condition to check if
     * this transition may be executed within the given context of the two
     * states given.
     *
     * @param source the source of the transition
     * @param destination the destination of the transition
     * @param action the action to execute
     * @param condition to check
     */
    public BasicTransition(S source, D destination, Action<Transition<S, D>> action, Function<Transition<S, D>, Boolean> condition) {
        this.source = source;
        this.destination = destination;
        this.action = action;
        this.condition = condition;
    }

    /**
     * Defines the condition to check if this transition may be executed within
     * the given context of the two states given.
     *
     * @return the condition
     */
    @Override
    public Function<Transition<S, D>, Boolean> condition() {
        return condition;
    }

    /**
     * Defines the {@link Action} to execute while transferring between the two
     * states.
     *
     * @return the action
     */
    @Override
    public Action<Transition<S, D>> action() {
        return action;
    }

    /**
     * Returns the source {@link State} of this transition.
     *
     * @return the source state, may be null if the destination is the first
     * state
     */
    @Override
    public S getSource() {
        return source;
    }

    /**
     * Returns the destination {@link State} of this transition.
     *
     * @return the destination state, may be null if the source is the last
     * state
     */
    @Override
    public D getDestination() {
        return destination;
    }

}

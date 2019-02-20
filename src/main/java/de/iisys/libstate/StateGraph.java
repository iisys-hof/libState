package de.iisys.libstate;

import de.iisys.libstate.interfaces.Action;
import de.iisys.libstate.interfaces.State;
import de.iisys.libstate.interfaces.Transition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Class to create, register and remove transitions and states.
  */
public class StateGraph {

    /**
     * Enummeration for the identification of the states.
     */
    public enum Identification {
        INITIAL
    }

    private Map<Object, State> states;
    private Map<Object, List<Transition<State, State>>> transitions;

    /**
     * Creates a new HashMap for states and transitions.
     */
    public StateGraph() {
        states = new HashMap<>();
        transitions = new HashMap<>();
    }

    /**
     * Gets the states.
     *
     * @return the states
     */
    public Map<Object, State> getStates() {
        return states;
    }

    /**
     * Gets the transitions
     *
     * @return the transitions
     */
    public Map<Object, List<Transition<State, State>>> getTransitions() {
        return transitions;
    }

    /**
     * Registers the state with the given identification.
     *
     * @param identification
     */
    public void registerState(Object identification) {
        registerState(identification, null);
    }

    /**
     * Registers the state with the given identification and action of the
     * state.
     *
     * @param identification state identification
     * @param action the action of the state
     */
    public void registerState(Object identification, Action<State> action) {
        registerState(identification, null, action, null);
    }

    /**
     * Registers the state with the given identification, the action to execute
     * when the state is entered from a different state, the action to always
     * execute within this state, also when and the action to {@link Transition}
     * do round trips and the action to execute when the state is left towards a
     * different state.
     *
     * @param identification state identification
     * @param entryAction to execute when the state is entered from a different
     * state
     * @param doAction to always execute within this state
     * @param exitAction to exit to a different state
     */
    public void registerState(Object identification, Action<State> entryAction, Action<State> doAction, Action<State> exitAction) {
        states.put(identification, createState(identification, entryAction, doAction, exitAction));
    }

    /**
     * Creates a state with the given identification, the action to execute when
     * the state is entered from a different state, the action to always execute
     * within this state, also when and the action to {@link Transition} do
     * round trips and the action to execute when the state is left towards a
     * different state.
     *
     * @param identification state identification
     * @param entryAction to execute when the state is entered from a different
     * state
     * @param doAction to always execute within this state
     * @param exitAction to exit to a different state
     * @return the basic state
     */
    protected BasicState createState(Object identification, Action<State> entryAction, Action<State> doAction, Action<State> exitAction) {
        return new BasicState(identification, entryAction, doAction, exitAction);
    }

    /**
     * Removes the state with the given identification.
     *
     * @param identification state identification
     */
    public void deregisterState(Object identification) {
        states.remove(identification);
    }

    /**
     * Overrides the state with the given identification.
     *
     * @param identification state identification
     */
    public void overrideState(Object identification) {
        overrideState(identification, null);
    }

    /**
     * Overrides the state with the given identification and action of the
     * state.
     *
     * @param identification state identification
     * @param action the action of the state
     */
    public void overrideState(Object identification, Action<State> action) {
        overrideState(identification, null, action, null);
    }

    /**
     * Overrides the state with the given identification, the action to execute
     * when the state is entered from a different state, the action to always
     * execute within this state, also when and the action to {@link Transition}
     * do round trips and the action to execute when the state is left towards a
     * different state.
     *
     * @param identification state identification
     * @param entryAction to execute when the state is entered from a different
     * state
     * @param doAction to always execute within this state
     * @param exitAction to exit to a different state
     */
    public void overrideState(Object identification, Action<State> entryAction, Action<State> doAction, Action<State> exitAction) {
        deregisterState(identification);
        registerState(identification, entryAction, doAction, exitAction);
    }

    /**
     * Registers the transition with the given initial state.
     *
     * @param initialState
     */
    public void registerTransition(Object initialState) {
        registerTransition(null, initialState);
    }

    /**
     * Registers the transition with the given source state and the destination
     * state.
     *
     * @param sourceIdentification the source state
     * @param destinationIdentification the destination state
     */
    public void registerTransition(Object sourceIdentification, Object destinationIdentification) {
        registerTransition(sourceIdentification, destinationIdentification, null, null);
    }

    /**
     * Registers the transition with the given source state and the destination
     * state.
     *
     * @param sourceIdentification the source state
     * @param destinationIdentification the destination state
     */
    public void registerTransition(Object sourceIdentification, Object destinationIdentification, Function<Transition<State, State>, Boolean> condition) {
        registerTransition(sourceIdentification, destinationIdentification, condition, null);
    }

    /**
     * Registers the transition with the given source state and the destination
     * state and the action to execute while transfering between the two states.
     *
     * @param sourceIdentification the source state
     * @param destinationIdentification the destination state
     * @param action the action to execute
     */
    public void registerTransition(Object sourceIdentification, Object destinationIdentification, Action<Transition<State, State>> action) {
        registerTransition(sourceIdentification, destinationIdentification, null, action);
    }

    /**
     * Registers the transition with the given source state and the destination
     * state and the condition to check if this transition may be executed
     * within the given context of the two states given. The destination state
     * cannot be null. Also the transition cannot add a unregistered source and
     * destination state.
     *
     * @param sourceIdentification the source state
     * @param destinationIdentification the destination state
     * @param condition the condition
     * @param action the action to execute
     */
    public void registerTransition(Object sourceIdentification, Object destinationIdentification, Function<Transition<State, State>, Boolean> condition, Action<Transition<State, State>> action) {
        if (sourceIdentification == null) {
            sourceIdentification = Identification.INITIAL;
        }

        if (destinationIdentification == null) {
            throw new IllegalArgumentException("Destination State cannot be null.");
        }

        State source = states.get(sourceIdentification);
        if (sourceIdentification != Identification.INITIAL && source == null) {
            throw new IllegalStateException("Cannot add transition from unregistered source '" + sourceIdentification + "'.");
        }

        State destination = states.get(destinationIdentification);
        if (destination == null) {
            throw new IllegalStateException("Cannot add transition to unregistered destination '" + destinationIdentification + "'.");
        }

        List<Transition<State, State>> transitionList = transitions.get(sourceIdentification);
        if (transitionList == null) {
            transitions.put(sourceIdentification, transitionList = new ArrayList<>());
        }

        transitionList.add(createTransition(source, destination, action, condition));
    }

    /**
     * Creates a transition with the given source state and the destination
     * state and the condition to check if this transition may be executed
     * within the given context of the two states given.
     *
     * @param source the source state
     * @param destination the destination state
     * @param condition the condition
     * @param action the action to execute
     * @return the basic transition
     */
    protected BasicTransition<State, State> createTransition(State source, State destination, Action<Transition<State, State>> action, Function<Transition<State, State>, Boolean> condition) {
        return new BasicTransition<>(source, destination, action, condition);
    }

    /**
     * Removes the transition. Cannot remove transition from unregistered source
     * or destination.
     *
     * @param sourceIdentification
     * @param destinationIdentification
     */
    public void deregisterTransitions(Object sourceIdentification, Object destinationIdentification) {
        State source = states.get(sourceIdentification);
        if (sourceIdentification != Identification.INITIAL && source == null) {
            throw new IllegalStateException("Cannot remove transition from unregistered source '" + sourceIdentification + "'.");
        }

        State destination = states.get(destinationIdentification);
        if (destination == null) {
            throw new IllegalStateException("Cannot remove transition to unregistered destination '" + destinationIdentification + "'.");
        }

        List<Transition<State, State>> transitionList = transitions.get(sourceIdentification);
        transitionList.removeIf((transition) -> (sourceIdentification == Identification.INITIAL && transition.getSource() == null
                || transition.getSource().equals(source))
                && transition.getDestination().equals(destination));

        if (transitionList.isEmpty()) {
            transitions.remove(sourceIdentification);
        }
    }

    /**
     * Overrides the transition with the given initial state.
     *
     * @param initialState
     */
    public void overrideTransition(Object initialState) {
        overrideTransition(null, initialState);
    }

    /**
     * Overrides the transition with the given source state and destination
     * state.
     *
     * @param sourceIdentification the source state
     * @param destinationIdentification the destination state
     */
    public void overrideTransition(Object sourceIdentification, Object destinationIdentification) {
        overrideTransition(sourceIdentification, destinationIdentification, null, null);
    }

    /**
     * Overrides the transition with the given source state and the destination
     * state and the condition to check if this transition may be executed
     * within the given context of the two states given.
     *
     * @param sourceIdentification the source state
     * @param destinationIdentification the destination state
     * @param condition the condition
     */
    public void overrideTransition(Object sourceIdentification, Object destinationIdentification, Function<Transition<State, State>, Boolean> condition) {
        overrideTransition(sourceIdentification, destinationIdentification, condition, null);
    }

    /**
     * Overrides the transition with the given source state and the destination
     * state and the condition to check if this transition may be executed
     * within the given context of the two states given.
     *
     * @param sourceIdentification the source state
     * @param destinationIdentification the destination state
     * @param action the action to execute
     */
    public void overrideTransition(Object sourceIdentification, Object destinationIdentification, Action<Transition<State, State>> action) {
        overrideTransition(sourceIdentification, destinationIdentification, null, action);
    }

    /**
     * Overrides the transition with the given source state and the destination
     * state and the condition to check if this transition may be executed
     * within the given context of the two states given.
     *
     * @param sourceIdentification the source state
     * @param destinationIdentification the destination state
     * @param condition the condition
     * @param action the action to execute
     */
    public void overrideTransition(Object sourceIdentification, Object destinationIdentification, Function<Transition<State, State>, Boolean> condition, Action<Transition<State, State>> action) {
        deregisterTransitions(sourceIdentification, destinationIdentification);
        registerTransition(sourceIdentification, destinationIdentification, condition, action);
    }

}

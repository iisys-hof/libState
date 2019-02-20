package de.iisys.libstate;

import de.iisys.libstate.interfaces.State;
import de.iisys.libstate.interfaces.Transition;
import java.util.List;
import java.util.Map;

/**
 * Class to bake, run and stop the state graph.
  */
public class StateGraphRunner {

    private boolean stop;
    private State initialState;
    private Map<Object, Object> initialMemory;

    /**
     * Initializes {@link #initialState} reference with the given state graph
     * object.<br>
     * Chains the states together.
     *
     * @param stateGraph the state graph
     */
    public StateGraphRunner(StateGraph stateGraph) {
        initialState = bakeStateGraph(stateGraph);
    }

    /**
     * Maps the states and the transition and sorts them.<br>
     * Defines the condition to check if this transition may be executed within
     * the given context of the two states given.<br>
     * Sets the transitions.
     *
     * @param stateGraph the state graph
     * @return the destination of the initial transition
     */
    protected State bakeStateGraph(StateGraph stateGraph) {
        Map<Object, State> states = stateGraph.getStates();
        Map<Object, List<Transition<State, State>>> transitions = stateGraph.getTransitions();

        for (Map.Entry<Object, State> entry : states.entrySet()) {
            Object identification = entry.getKey();
            State state = entry.getValue();

            List<Transition<State, State>> transitionList = transitions.get(identification);
            if (transitionList != null) {
                transitionList.sort((left, right) -> left.condition() != null ? -1 : right.condition() != null ? 1 : 0);
                state.setTransitions(transitionList);
            }
        }

        List<Transition<State, State>> initialTransitions = transitions.get(StateGraph.Identification.INITIAL);
        if (initialTransitions != null && initialTransitions.size() == 1) {
            return initialTransitions.get(0).getDestination();
        }

        throw new UnsupportedOperationException("There are no or multiple INITIAL transitions defined, I don't know where to start. Please define one single INITIAL transition.");
    }

    /**
     * Runs the state graph.
     */
    public void run() {
        if (initialMemory != null) {
            // rewind initial state if we made a clone earlier
            initialState.setMemory(initialMemory);
        }
        // always make a fresh clone for the next run
        initialMemory = initialState.cloneMemory();

        State state = initialState;
        Transition<State, State> transition = null;
        while (!stop && state != null) {
            try {
                transition = executeState(state, transition);
                state = transition == null ? null : transition.getDestination();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

        stop = false;
    }

    /**
     * Stops the state graph.
     */
    public void stop() {
        stop = true;
    }

    /**
     * Executes the state.
     *
     * @param state the state
     * @param comingFromTransition the transition
     * @return the transition
     * @throws Exception
     */
    protected Transition<State, State> executeState(State state, Transition<State, State> comingFromTransition) throws Exception {
        // when we come from a different state (or the start) and do not turn rounds from the same to the same state, execute entry action
        if (state.entryAction() != null && (comingFromTransition == null || comingFromTransition.getSource() == null || !comingFromTransition.getSource().equals(state))) {
            state.entryAction().run(state);
        }

        // always execute main action
        if (state.doAction() != null) {
            state.doAction().run(state);
        }

        // get the right transition
        Transition<State, State> transition = null;
        List<Transition<State, State>> transitions = state.getTransitions();
        if (transitions != null) {
            for (int i = 0; i < transitions.size(); i++) {
                transition = transitions.get(i);
                if (transition != null && (transition.condition() != null ? transition.condition().apply(transition) : true)) {
                    break;
                }
            }
        }

        // when we go to a different state (or the end) and do not turn rounds, execute exit action
        if (state.exitAction() != null && (transition == null || transition.getDestination() == null || !transition.getDestination().equals(state))) {
            state.exitAction().run(state);
        }

        // now transit
        if (transition != null) {
            if (transition.action() != null) {
                transition.action().run(transition);
            }

            // copy changed state memory to new state
            State destination = transition.getDestination();
            destination.setMemory(state.getMemory());
        }

        return transition;
    }

}

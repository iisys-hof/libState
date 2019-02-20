package de.iisys.libstate;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Class to test the state graph.
  */
public class StateGraphTest {

    private StateGraph stateGraph;

    /**
     * Sets up a new state graph.
     */
    @Before
    public void setUp() {
        stateGraph = new StateGraph();
    }

    /**
     * TESTING. Registers the states and transitions and runs the state graph.
     */
    @Test
    public void testGraph() {
        List<Integer> stateRunList = new ArrayList<>();
        stateGraph.registerState("first", (state) -> {
            stateRunList.add(1);
            if (state.get("data") == null) {
                state.put("data", 1);
                assertEquals(1, (int) state.get("data"));
            } else {
                assertEquals(3, (int) state.get("data"));
            }
        });
        stateGraph.registerState("second", (state) -> {
            stateRunList.add(2);
            state.put("data", (int) state.get("data") + 1);
        });
        stateGraph.registerState("third", (state) -> {
            stateRunList.add(3);
            state.put("data", (int) state.get("data") + 1);
        });

        List<String> testList = new ArrayList<>();
        stateGraph.registerState("fourth", (state) -> {
            stateRunList.add(4);
            state.put("data", (int) state.get("data") + 1);
            testList.add("test");
        });

        stateGraph.registerTransition("first");
        stateGraph.registerTransition("first", "second", (transition) -> (int) transition.getSource().get("data") == 1);
        stateGraph.registerTransition("first", "fourth");
        stateGraph.registerTransition("second", "third");
        stateGraph.registerTransition("third", "first");

        // run
        StateGraphRunner stateGraphRunner = new StateGraphRunner(stateGraph);
        stateGraphRunner.run();

        // five state visits as first state is visited two times
        assertEquals(5, stateRunList.size());
        assertEquals(1, testList.size());

        // second run needs to behave exactly like the first run, so asserts in first state need to do the same
        stateRunList.clear();
        stateGraphRunner.run();

        // check if we made five state visits again which would not happen if internal memory is not cleared
        assertEquals(5, stateRunList.size());
        // testList is not set back, only internal state memory, so we have two nodes now
        assertEquals(2, testList.size());

        // third run just to be sure
        stateRunList.clear();
        stateGraphRunner.run();

        assertEquals(5, stateRunList.size());
        assertEquals(3, testList.size());
    }
}

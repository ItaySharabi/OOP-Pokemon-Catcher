package tests;

import api.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DWGraph_AlgoTest {

    static directed_weighted_graph g;
    static dw_graph_algorithms ga;

    @BeforeEach
    void makeGraph() {
        g = new DWGraph_DS();
        g.addNode(new NodeData(2));
        g.addNode(new NodeData(3));
        g.connect(2, 3, 1);
        ga = new DWGraph_Algo(g);

    }

    @Test
    void init() {

        ga.init(g);
        assertNotNull(ga);
        assertNotNull(ga.getGraph());

        ga.init(null);
        assertNotNull(ga);
        assertNotNull(ga.getGraph());

    }

    @Test
    void getGraph() {

        directed_weighted_graph g2 = ga.getGraph();
        g2.connect(3, 2, 2);
        ga.init(g2);

        directed_weighted_graph g3 = ga.getGraph(); //Get a shallow pointer to g2
        g3.removeEdge(3, 2); //Remove 3 --> 2 from g3
        assertNull(g2.getEdge(3, 2)); //g2 should also suffer from g3 removing an edge because of the shallow pointer.
        assertNull(ga.getGraph().getEdge(3, 2)); //So should ga.getGraph() ^.

    }

    @Test
    void copy() {
        directed_weighted_graph copied = ga.copy(); //Checks first if |V1|+|E1| == |V2|+|E2|
        assertEquals(copied.nodeSize() + copied.edgeSize(), ga.getGraph().nodeSize() + ga.getGraph().edgeSize());

        copied.removeEdge(2, 3);
        assertNotNull(ga.getGraph().getEdge(2, 3));

        copied.addNode(new NodeData(4));
        copied.addNode(new NodeData(1));
        copied.connect(1, 4, 2); //Set up 2 connectivity components
        copied.connect(4, 1, 2);
        copied.connect(2, 3, 2);
        copied.connect(3, 2, 2);
        ga.init(copied);

        directed_weighted_graph copied2 = ga.copy();
        assertNotNull(copied2.getEdge(2, 3));
        assertNotNull(copied2.getEdge(3, 2));
        assertNotNull(copied2.getEdge(1, 4));
        assertNotNull(copied2.getEdge(4, 1));

    }

    @Test
    void isConnected() {

        boolean b = false;

        ga.init(new DWGraph_DS());
        assertTrue(ga.isConnected()); // An empty graph should be connected

        directed_weighted_graph g2 = ga.getGraph();
        g2.addNode(new NodeData(1));
        ga.init(g2);
        assertFalse(ga.isConnected());

        g2.addNode(new NodeData(2));
        ga.init(g2);
        assertFalse(ga.isConnected());

        g2.connect(1, 2, 1);
        ga.init(g2);
        assertFalse(ga.isConnected());

        g2.connect(2, 1, 1);
        ga.init(g2);
        assertTrue(ga.isConnected());

//        System.out.println(g2); //Need to fix toString in DWGraph_DS

        g2.addNode(new NodeData(3));
        g2.addNode(new NodeData(4));
        g2.connect(3, 4, 1);
        g2.connect(4, 3, 2);
        ga.init(g2);
        assertFalse(ga.isConnected()); //Graph has 2 connectivity components.

        g2.connect(2, 3, 1);
        g2.connect(3, 2, 1);
        assertTrue(ga.isConnected()); //Graph has 1 connectivity components.


//        g.connect(3, 2, 2);
//        ga.init(g);
//        b = ga.isConnected(); //Should be connected (2->3, 3->2)
//        assertTrue(b);
//
//        g.addNode(new NodeData(1));
//        g.addNode(new NodeData(4));
//        g.connect(1, 4, 1);
//        g.connect(4, 1, 2);
//
//        ga = new DWGraph_Algo(g);
//        b = ga.isConnected();
//        assertFalse(b);
    }
}
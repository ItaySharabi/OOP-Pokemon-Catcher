package tests;

import api.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DWGraph_AlgoTest {

    static directed_weighted_graph g;
    static dw_graph_algorithms ga = new DWGraph_Algo();

    directed_weighted_graph makeGraph(int v_size, int e_size, double rnd) {

        g = new DWGraph_DS();
        int j = 0;
        double weight;
        int executions = 0;

        for(int i = 0; i < v_size; i++)
            g.addNode(new NodeData(i));

        int[] nodes = nodesToArray(g);
        while (j < e_size) {
            int a = (int) (Math.random()*nodes.length);
            int b = (int) (Math.random()*nodes.length);
            weight = (Math.random()*rnd);
            if (a != b && g.getEdge(a, b) == null) {
                g.connect(nodes[a], nodes[b], weight);
                j++;
                System.out.println("Connecting " + a + " to " + b);
            }
            executions++;
            if (executions == e_size*3) System.out.println("Required edges: " + e_size + ", actual: " + g.edgeSize());
        }
        System.out.println(executions + " Executions");
        ga = new DWGraph_Algo(g);
        return g;
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

        int v_size = 5, e_size = (v_size-1)*v_size;
        directed_weighted_graph g2 = makeGraph(v_size, e_size, 4);
        g2.connect(3, 2, 2);
        ga.init(g2);

        directed_weighted_graph g3 = ga.getGraph(); //Get a shallow pointer to g2
        assertEquals(g2.getEdge(3, 2).getWeight(), g3.getEdge(3, 2).getWeight(), 0.1);

        g3.removeEdge(3, 2); //Remove 3 --> 2 from g3
        assertNull(g2.getEdge(3, 2)); //g2 should also suffer from g3 removing an edge because of the shallow pointer.
        assertNull(ga.getGraph().getEdge(3, 2)); //So should ga.getGraph() ^.


    }

    @Test
    void copy() {
        directed_weighted_graph copied = ga.copy(); //Checks first if |V1|+|E1| == |V2|+|E2|
        assertEquals(copied.nodeSize() + copied.edgeSize(), ga.getGraph().nodeSize() + ga.getGraph().edgeSize());

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

        ga.init(makeGraph(0, 0, 0));
        assertTrue(ga.isConnected()); // An empty graph should be connected

        directed_weighted_graph g2 = ga.getGraph();
        g2.addNode(new NodeData(1));
        testConnectivity(true, g2); //Test a graph with 1 node - should be true.

        g2.addNode(new NodeData(2));
        testConnectivity(false, g2); //Test a graph with 2 nodes, no edges - should be false.

        g2.connect(1, 2, 1);
        testConnectivity(false, g2); //Test a graph with 2 nodes, 1 edge - should be false.

        g2.connect(2, 1, 1);
        testConnectivity(true, g2); //Test a graph with 2 nodes, 2 edges - should be true.

        g2 = makeGraph(10, 10*9, 5); //Make a full graph k10
        testConnectivity(true, g2); //Test graph with |V| nodes and |V|*(|V|-1) edges. - should be true.

        ga.init(makeGraph(4, 0, 0)); //Make a graph with 4 nodes, no edges.
        ga.getGraph().connect(1, 2, 1); //Connect the nodes to make a cycle.
        ga.getGraph().connect(2, 3, 1);
        ga.getGraph().connect(3, 0, 1);
        ga.getGraph().connect(0, 1, 1);
        testConnectivity(true, ga.getGraph()); //Test a graph with 4 nodes that make a cycle s.t the graph is connected.

        ga.getGraph().removeNode(3); //Remove a node from the cycle.
        testConnectivity(false, ga.getGraph()); //Test the same cycle after removing a node - should be false.
    }

    private int[] nodesToArray(directed_weighted_graph g) {
        int[] ans = new int[g.nodeSize()];
        int i = 0;
        for (node_data n : g.getV())
            ans[i++] = n.getKey();

        return ans;
    }

    @Test
    void testConnectivity(boolean req, directed_weighted_graph graph) {
        dw_graph_algorithms ga = new DWGraph_Algo(graph);
        assertTrue(req == ga.isConnected());
    }
}
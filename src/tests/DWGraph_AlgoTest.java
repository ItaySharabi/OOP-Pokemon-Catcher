package tests;

import api.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DWGraph_AlgoTest {

    static directed_weighted_graph g;
    static dw_graph_algorithms ga;
    static int V = 0, //Vertices
            E = 0, //Edges
            randomWeightSeed = 0;

    @BeforeAll
    static void init() {
        V = 5; //5 Vertices
        E = 20; //20 Edges
        randomWeightSeed = 1; //Random weight: [0, 1).
        g = new DWGraph_DS();
        ga = new DWGraph_Algo(g);
    }

    @Test
    void getGraph() {

        directed_weighted_graph g2 = makeGraph(V, E); // |V| = 5, |E| = 20 (Unless changed).
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
        g = makeGraph(V, E);
        ga.init(g);
        directed_weighted_graph copy = ga.copy();

        assertEquals(g, copy); //Check equality between Original graph - g and the copied graph.
        assertEquals(ga.getGraph(), copy); //Check equality between Original graph - g and the copied graph.
        assertEquals(ga.copy(), copy);

        if (g.nodeSize() > 1) {
            node_data n = null;
            for (node_data k : g.getV()) {n = k; break;} //Hold an existing node on the graph.

            g.removeNode(n.getKey()); //Remove a node from the original graph (which removes some edges probably).

            assertNotEquals(g, copy);
            assertNotEquals(ga.getGraph(), copy);
            assertNotEquals(ga.copy(), copy);
        }
    }

    void testConnectivity(boolean req, directed_weighted_graph graph) {
        dw_graph_algorithms ga = new DWGraph_Algo(graph);
        assertTrue(req == ga.isConnected());
    }

    /**
     * This method creates a few example graphs and tests their connectivity status.
     * So far everything works.
     */
    @Test
    void isConnected() {

        ga.init(makeGraph(0, 0));
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

        g2 = makeGraph(10, 10*9); //Make a full graph k10
        testConnectivity(true, g2); //Test graph with |V| nodes and |V|*(|V|-1) edges. - should be true.

        ga.init(makeGraph(4, 0)); //Make a graph with 4 nodes, no edges.
        ga.getGraph().connect(1, 2, 1); //Connect the nodes to make a cycle.
        ga.getGraph().connect(2, 3, 1);
        ga.getGraph().connect(3, 0, 1);
        ga.getGraph().connect(0, 1, 1);
        testConnectivity(true, ga.getGraph()); //Test a graph with 4 nodes that make a cycle s.t the graph is connected.

        ga.getGraph().removeNode(3); //Remove a node from the cycle.
        testConnectivity(false, ga.getGraph()); //Test the same cycle after removing a node - should be false.
        ga.init(makeGraph(7, 0));
        ga.getGraph().connect(0, 1, 1);
        ga.getGraph().connect(0, 2, 1);
        ga.getGraph().connect(1, 3, 1);
        ga.getGraph().connect(3, 1, 1);
        ga.getGraph().connect(1, 4, 1);
        ga.getGraph().connect(4, 1, 1);
        ga.getGraph().connect(2, 5, 1);
        ga.getGraph().connect(5, 2, 1);
        ga.getGraph().connect(5, 6, 1);
        ga.getGraph().connect(6, 5, 1);
        testConnectivity(false, ga.getGraph());

        ga.init(makeGraph(4, 0));
        ga.getGraph().connect(0, 1, 1);
        ga.getGraph().connect(1, 0, 1);
        ga.getGraph().connect(1, 2, 1);
        ga.getGraph().connect(2, 1, 1);
        ga.getGraph().connect(3, 2, 1);
        ga.getGraph().connect(2, 3, 1);
        ga.getGraph().connect(0, 3, 1);
        testConnectivity(true, ga.getGraph());

        ga.getGraph().removeEdge(2, 3);
        testConnectivity(true, ga.getGraph());

        ga.init(makeGraph(2, 0));
        makeFullGraph(ga.getGraph(), 4);
        testConnectivity(true, ga.getGraph());
        System.out.println("--------------------------------------------------");

        ga.init(makeGraph(5, 0));
        makeFullGraph(ga.getGraph(), 4);
        testConnectivity(true, ga.getGraph());
        System.out.println("--------------------------------------------------");

        ga.init(makeGraph(7, 0));
        testConnectivity(false, ga.getGraph());
        makeFullGraph(ga.getGraph(), 4);
        testConnectivity(true, ga.getGraph());
        System.out.println("--------------------------------------------------");
    }

    @Test
    void shortestPathTest() {
        V = 7;
        E = 7*6;
        g = makeGraph(V, E);
        ga.init(g);

        ga.getGraph().connect(0, 1, 1);
        ga.getGraph().connect(1, 2, 1);
        ga.getGraph().connect(2, 3, 1);
        ga.getGraph().connect(3, 4 ,1);
        ga.getGraph().connect(4, 0 ,1);

        double dist = ga.shortestPathDist(0, 4);
        assertNotEquals(4, dist); //There exists a shorter path

        ga.init(makeGraph(V, 0));
        makeFullGraph(ga.getGraph(), 1);

        /*
        Make sure the returned list from getShortestPath() contains the current updated weights,
        as received when calling shortestPathDist().
         */
        ga.init(makeGraph(7, 7*6));
        dist = ga.shortestPathDist(0, 6);
        List<node_data> path = ga.shortestPath(0, 6);
        assertEquals(path.get(path.size()-1).getWeight(), dist);

    }

    @Test
    void saveLoad() {

        String path = "data\\"; //Path to example graphs.
        final String file = "A"; //File name (The graph to test).

        for (int i = 0; i < 6; i++)
            testSaveLoad(path, file, i);
    }


    void testSaveLoad(String path, String file, int i) {
        dw_graph_algorithms ga2 = new DWGraph_Algo();
        ga.load(path + file + i); //Load next graph A0-A5
        ga2.init(ga.getGraph()); //Init other graph_algo with the loaded graph
        ga2.save("Test" + i); //Save the given graph
        ga2.load("Test" + i); //Load the given graph
        assertEquals(ga.getGraph(), ga2.getGraph()); //Make sure graphs are equal after saving and loading them.
    }



    /**
     * This method connects random edges on the given graph, untill the graph is fully connected.
     * Every edge is connected with a random weight in range: (double)[0, randomSeed - 1].
     * @param graph
     * @param randomSeed
     */
    private void makeFullGraph(directed_weighted_graph graph, double randomSeed) {

        int v = graph.nodeSize();
        int maxEdgeSize = v*(v-1);
        randomlyConnectGraph(graph, maxEdgeSize, randomSeed);
    }

    /**
     * This method connects random nodes on the given graph, until e_size edges exist on the graph.
     * @param graph - The graph to randomly connect
     * @param e_size - Required edge number (Max = |V| * (|V|-1), |V| = graph.nodeSize()).
     * @param rnd - A number that determines the range on the random weight for an edge - (double)[0, rnd-1].
     */
    private void randomlyConnectGraph(directed_weighted_graph graph, int e_size, double rnd) {

        if (graph == null || e_size < 0 || rnd < 0) return;
        int j = 0;
        double weight;

        int[] nodes = nodesToArray(graph);

        while (j < e_size) {
            int a = (int) (Math.random()*nodes.length);
            int b = (int) (Math.random()*nodes.length);
            weight = (Math.random()*rnd);
            if (a != b && graph.getEdge(a, b) == null) {
                graph.connect(nodes[a], nodes[b], weight);
                j++;
            } //If a connection could be made.
        } //While connecting.
    }

    /**
     * This method generates and returns an array, representing all nodes on the graph.
     * This helps Iterating over the graph if node keys are for example: 0, 13, 22, 4, 923, ...
     * @param g - A given graph.
     * @return an array of size |V| ([0, |V|-1], |V| = graph.nodeSize()).
     */
    private int[] nodesToArray(directed_weighted_graph g) {
        int[] ans = new int[g.nodeSize()];
        int i = 0;
        for (node_data n : g.getV())
            ans[i++] = n.getKey();

        return ans;
    }

    /**
     * This method simply creates and returns a directed, weighted graph with v_size nodes
     * and e_size edges.
     * @param v_size - Number of nodes for the graph.
     * @param e_size - Number of edges for the graph.
     * @return - directed_weighted_graph Object with v_size nodes and e_size edges.
     */
    directed_weighted_graph makeGraph(int v_size, int e_size) {

        g = new DWGraph_DS();

        for(int i = 0; i < v_size; i++)
            g.addNode(new NodeData(i));

        randomlyConnectGraph(g, e_size, randomWeightSeed);

        ga = new DWGraph_Algo(g);
        return g;
    }


}
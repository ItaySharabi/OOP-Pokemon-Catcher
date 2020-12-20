package tests;

import api.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Node;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class DWGraph_DSTest {

    static directed_weighted_graph g;



    @Test
    void getEdge() {

        g.removeEdge(0, 1);
        assertNull(g.getEdge(0, 1));

        //Trying to override edges with different weights:
        g.connect(0, 1, 2);
        assertEquals(2, g.getEdge(0, 1).getWeight());
        g.connect(0, 1, 1);
        assertEquals(1, g.getEdge(0, 1).getWeight());
        g.connect(0, 1, -1);
        assertEquals(1, g.getEdge(0, 1).getWeight());

        //If a node is removed check if the edges are removed
        g.removeNode(1);
        assertNull(g.getEdge(0, 1));

    }

    @Test
    void addNode() {
        g = new DWGraph_DS();
        node_data n = new NodeData(5);
        node_data n1 = new NodeData(n);
        g.addNode(n);
        assertEquals(1, g.nodeSize()); /**Should be SAME! */
        g.addNode(n1);
        assertEquals(1, g.nodeSize()); /**Should be SAME! */
        for (int i = 0; i < 10; i++)
            g.addNode(new NodeData(i));
        assertEquals(10, g.nodeSize());

    }

    @Test
    void removeNode() {

        g = makeGraph(10, 20);
        g.removeNode(3); //Remove same node twice.
        g.removeNode(3);
        assertNull(g.getNode(3)); //Make sure the node is gone.

        assertNull(g.getEdge(3, 1)); //Make sure edges are gone.

        Iterator<node_data> itr = g.getV().iterator();
        node_data n = null;
        while(itr.hasNext()) {
            n = itr.next();
            itr.remove(); //Saves my life this one <3.
            g.removeNode(n.getKey());
        }

        nodeSize(true, 0);

        g.addNode(new NodeData(5));
        nodeSize(true, 1);

        g.removeNode(5);
        nodeSize(false, 1);
        nodeSize(true, 0);

    }

    @Test
    void removeEdge() {

        g = makeGraph(10, 10*9);
        Iterator<edge_data> itr = g.getE(0).iterator();
        edge_data e = null;
        int size = g.edgeSize();

        while (itr.hasNext()) {
            e = itr.next();
            itr.remove();
            g.removeEdge(e.getSrc(), e.getDest());
            edgeSize(true, --size);
        }
    }


    void nodeSize(boolean req, int actualSize) {
        assertEquals(req, actualSize == g.nodeSize());
    }


    void edgeSize(boolean req, int actualSize) {
        assertEquals(req, actualSize == g.edgeSize());
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

        randomlyConnectGraph(g, e_size, 0);
        return g;
    }

}
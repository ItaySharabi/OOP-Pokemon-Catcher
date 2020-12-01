package tests;

import api.DWGraph_DS;
import api.NodeData;
import api.directed_weighted_graph;
import api.node_data;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Node;

import static org.junit.jupiter.api.Assertions.*;

class DWGraph_DSTest {

    static directed_weighted_graph g;

    @BeforeEach
    void makeGraph() {
        g = new DWGraph_DS();
        g.addNode(new NodeData(0));

        for (int i = 1; i < 100; i++) {
            g.addNode(new NodeData(i));
            int rnd = (int) (Math.random() * g.nodeSize());
            g.connect(i - 1, rnd, i);
            System.out.println(i - 1 + " --> " + rnd);
        }
    }

    /**
     * This test checks for null nodes on the graph.
     */
    @Test
    void getNode() {

        assertNotNull(g.getNode(0));

        g.removeNode(0);
        assertNull(g.getNode(0));

        g.addNode(new NodeData(0));
        g.addNode(new NodeData(0));

        assertNotNull(g.getNode(0));
    }

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

        node_data n1 = new NodeData(5);
        node_data n2 = new NodeData(n1);

        n1.setInfo("Hello");
        n2.setInfo("GoodBye");
        g.addNode(n1);
        g.addNode(n2);
        g.connect(n1.getKey(), n2.getKey(), 1);
        assertNotNull(g.getEdge(n1.getKey(), n2.getKey()));
        assertNotEquals(n1.getInfo(), g.getNode(g.getEdge(n1.getKey(), n2.getKey()).getSrc()).getInfo());
        System.out.println(n1.getInfo() + " Compared to " + n2.getInfo());
    }


    @Test
    void getV() {

    }

    @Test
    void getE() {
    }

    @Test
    void removeNode() {

        g.removeNode(3); //Remove same node twice.
        g.removeNode(3);
        assertNull(g.getNode(3)); //Make sure the node is gone.

        assertNull(g.getEdge(3, 1)); //Make sure edges are gone.


    }

    @Test
    void removeEdge() {
    }

    @Test
    void nodeSize() {
    }

    @Test
    void edgeSize() {
    }

    @Test
    void getMC() {
    }

}
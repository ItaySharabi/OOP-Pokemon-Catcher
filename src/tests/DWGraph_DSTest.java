package tests;

import api.DWGraph_DS;
import api.NodeData;
import api.directed_weighted_graph;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DWGraph_DSTest {

    static directed_weighted_graph g;

    @BeforeAll
    static void makeGraph() {
        g = new DWGraph_DS();
        g.addNode(new NodeData(0));
        g.addNode(new NodeData(1));
        g.addNode(new NodeData(2));
        g.addNode(new NodeData(3));

        g.connect(0, 1, 1);
        g.connect(1, 2, 1);
        g.connect(2, 3, 1);
        g.connect(3, 1, 1);
    }


    @Test
    void getNode() {

        assertNotNull(g.getNode(0));
        assertNotNull(g.getNode(1));
        assertNotNull(g.getNode(2));
        assertNotNull(g.getNode(3));
    }

    @Test
    void getEdge() {

    }

    @Test
    void addNode() {
    }


    @Test
    void getV() {
    }

    @Test
    void getE() {
    }

    @Test
    void removeNode() {
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
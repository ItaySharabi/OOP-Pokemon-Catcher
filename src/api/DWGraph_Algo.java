package api;

import java.util.*;

public class DWGraph_Algo implements dw_graph_algorithms{

    private directed_weighted_graph graph;

    public DWGraph_Algo() {
        graph = new DWGraph_DS();
    }

    public DWGraph_Algo(directed_weighted_graph g) {
        init(g);
    }

    /**
     * Init the graph on which this set of algorithms operates on.
     *
     * @param g
     */
    @Override
    public void init(directed_weighted_graph g) {
        if (g != null) graph = g;
        else graph = new DWGraph_DS();
    }

    /**
     * Return the underlying graph of which this class works.
     *
     * @return
     */
    @Override
    public directed_weighted_graph getGraph() {
        return graph;
    }

    /**
     * Compute a deep copy of this weighted graph.
     *
     * @return
     */
    @Override
    public directed_weighted_graph copy() {
        return new DWGraph_DS(this.graph);
    }

    /**
     * Returns true if and only if (iff) there is a valid path from each node to each
     * other node. NOTE: assume directional graph (all n*(n-1) ordered pairs).
     *
     * @return
     */
    @Override
    public boolean isConnected() {

        for (node_data n : graph.getV())
            if(!isConnectedBFS(n)) return false;

        return true;
    }

    private boolean isConnectedBFS(node_data start) {

        node_data curr = start;
        node_data neighbor = null;

        Queue<node_data> queue = new LinkedList<>();
        queue.add(curr);
        curr.setTag(1);

        while(!queue.isEmpty()) {
            curr = queue.poll();
            Collection<edge_data> outEdges = graph.getE(curr.getKey());
            if (outEdges.size() == 0) return false; //If a single node has no outgoing edges - the graph is not connected.

            for (edge_data e : outEdges) { //Iterate over outgoing edges from curr
                neighbor = graph.getNode(e.getDest());

                if (neighbor.getTag() == 0) { //If neighbor is not a visited node
                    queue.add(neighbor); //Add it to the queue
                    neighbor.setTag(1); //Mark it as visited
                }
            }
        }

        if (allNodesWereVisited(graph)) {resetTags(); return true;}

        return false;
    }

    private boolean allNodesWereVisited(directed_weighted_graph graph) {
        for (node_data n : graph.getV())
            if (n.getTag() == 0) return false;

        return true;
    }

    private node_data getFirstNode(directed_weighted_graph graph) {
        for (node_data n : graph.getV())
            return n;
        return null;
    }

    /**
     * returns the length of the shortest path between src to dest
     * Note: if no such path --> returns -1
     *
     * @param src  - start node
     * @param dest - end (target) node
     * @return
     */
    @Override
    public double shortestPathDist(int src, int dest) {
        return 0;
    }

    /**
     * returns the the shortest path between src to dest - as an ordered List of nodes:
     * src--> n1-->n2-->...dest
     * see: https://en.wikipedia.org/wiki/Shortest_path_problem
     * Note if no such path --> returns null;
     *
     * @param src  - start node
     * @param dest - end (target) node
     * @return
     */
    @Override
    public List<node_data> shortestPath(int src, int dest) {
        return null;
    }

    /**
     * Saves this weighted (directed) graph to the given
     * file name - in JSON format
     *
     * @param file - the file name (may include a relative path).
     * @return true - iff the file was successfully saved
     */
    @Override
    public boolean save(String file) {
        return false;
    }

    /**
     * This method load a graph to this graph algorithm.
     * if the file was successfully loaded - the underlying graph
     * of this class will be changed (to the loaded one), in case the
     * graph was not loaded the original graph should remain "as is".
     *
     * @param file - file name of JSON file
     * @return true - iff the graph was successfully loaded.
     */
    @Override
    public boolean load(String file) {
        return false;
    }

    private void resetTags() {
        for (node_data n : graph.getV())
            n.setTag(0);
    }
}

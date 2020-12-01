package api;

import org.w3c.dom.Node;

import java.util.*;

public class DWGraph_Algo implements dw_graph_algorithms{

    private static directed_weighted_graph graph;

    public DWGraph_Algo() {
        graph = new DWGraph_DS();
    }

    public DWGraph_Algo(directed_weighted_graph g) {
        init(g);
    }

    public static void main(String[] args) {
        dw_graph_algorithms ga = new DWGraph_Algo();
        directed_weighted_graph g = ga.getGraph();
        g.addNode(new NodeData(1));
        g.addNode(new NodeData(2));
        g.addNode(new NodeData(3));
        g.addNode(new NodeData(4));
        g.connect(1, 2, 1);
        g.connect(2, 3, 1);
        g.connect(3, 4, 1);
        g.connect(4, 1, 1);
        System.out.println(graph);
        ga.init(g);
        System.out.println(ga.shortestPath(1, 4));

        ga.getGraph().removeNode(3);
        System.out.println(ga.shortestPath(1, 4));

        ga.getGraph().connect(2, 4, 10);
        System.out.println(ga.shortestPath(1, 4));
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

        if (graph.getV().size() <= 1) return true;

        for (node_data n : graph.getV())
            if(!isConnectedBFS(n)) {resetTags(); return false;}

        resetTags();
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

        if (graph.getNode(src) == null || graph.getNode(dest) == null) return null;
        if (src == dest || graph.getE(src).size() == 0) return null;

//        List<node_data> path = new LinkedList<>(); //A list of visited vertices.
        HashMap<Integer, node_data> prevNode = new HashMap<>(); //A map of parent nodes. for (Integer) key, map (node_info) parent.

        PriorityQueue<node_data> pq = new PriorityQueue<>(graph.nodeSize(), new NodeComparator()); //The BFS queue

        boolean destinationFound = false;

        setWeightInfinity(); //Init all distances from node 'src' to infinity
        graph.getNode(src).setWeight(0); //The distance from src to src is 0.
        pq.add(graph.getNode(src)); //Add start node to the queue and start traversing.

        node_data curr, neighbor = null;
        double totalDist;

        while (!pq.isEmpty()) {

            curr = pq.poll();

            for (edge_data outEdge : graph.getE(curr.getKey())) {
                neighbor = graph.getNode(outEdge.getDest());
                System.out.println(outEdge);
                if (neighbor.getKey() == dest) destinationFound = true;
                totalDist = curr.getWeight() + outEdge.getWeight();

                if (totalDist < neighbor.getWeight()) { //If the total distance is less than the known distance from neighbor to src.
                    neighbor.setWeight(totalDist);
                    if (prevNode.putIfAbsent(neighbor.getKey(), curr) != null)
                        prevNode.put(neighbor.getKey(), curr);// Update the current calling node in prevNode.
                }

            if (neighbor.getTag() == 0 && !pq.contains(neighbor)) pq.add(neighbor);
            }
            curr.setTag(1);
        }

        if (destinationFound) System.out.println("Destination Found! distance = " + graph.getNode(dest).getWeight());
        else System.out.println("Fix path");

        return null;
    }

    private void setWeightInfinity() {

        for (node_data n : graph.getV()) n.setWeight(Double.MAX_VALUE + 1);
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

    @Override
    public String toString() {
        return graph.toString();
    }
}

class NodeComparator implements Comparator<node_data> {

    /**
     * Compares its two arguments for order.  Returns a negative integer,
     * zero, or a positive integer as the first argument is less than, equal
     * to, or greater than the second.<p>
     * <p>
     * The implementor must ensure that {@code sgn(compare(x, y)) ==
     * -sgn(compare(y, x))} for all {@code x} and {@code y}.  (This
     * implies that {@code compare(x, y)} must throw an exception if and only
     * if {@code compare(y, x)} throws an exception.)<p>
     * <p>
     * The implementor must also ensure that the relation is transitive:
     * {@code ((compare(x, y)>0) && (compare(y, z)>0))} implies
     * {@code compare(x, z)>0}.<p>
     * <p>
     * Finally, the implementor must ensure that {@code compare(x, y)==0}
     * implies that {@code sgn(compare(x, z))==sgn(compare(y, z))} for all
     * {@code z}.<p>
     * <p>
     * It is generally the case, but <i>not</i> strictly required that
     * {@code (compare(x, y)==0) == (x.equals(y))}.  Generally speaking,
     * any comparator that violates this condition should clearly indicate
     * this fact.  The recommended language is "Note: this comparator
     * imposes orderings that are inconsistent with equals."<p>
     * <p>
     * In the foregoing description, the notation
     * {@code sgn(}<i>expression</i>{@code )} designates the mathematical
     * <i>signum</i> function, which is defined to return one of {@code -1},
     * {@code 0}, or {@code 1} according to whether the value of
     * <i>expression</i> is negative, zero, or positive, respectively.
     *
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the
     * first argument is less than, equal to, or greater than the
     * second.
     * @throws NullPointerException if an argument is null and this
     *                              comparator does not permit null arguments
     * @throws ClassCastException   if the arguments' types prevent them from
     *                              being compared by this comparator.
     */
    @Override
    public int compare(node_data o1, node_data o2) {
        return o1.getWeight() > o2.getWeight() ?
                (int)(o1.getWeight() - o2.getWeight()) :
                (int)Math.abs(o1.getWeight() - o2.getWeight());
    }
}
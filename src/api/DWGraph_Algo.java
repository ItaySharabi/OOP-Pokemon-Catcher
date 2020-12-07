package api;

import com.google.gson.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class DWGraph_Algo implements dw_graph_algorithms {

    private directed_weighted_graph graph;

    public DWGraph_Algo() {
        graph = new DWGraph_DS();
    }

    public DWGraph_Algo(directed_weighted_graph g) {
        init(g);
    }

    public static void main(String[] args) {

        directed_weighted_graph graph = new DWGraph_DS();
        dw_graph_algorithms ga = new DWGraph_Algo();
        directed_weighted_graph g = ga.getGraph();
        g.addNode(new NodeData(0));
        g.addNode(new NodeData(1));
        g.addNode(new NodeData(2));
        g.addNode(new NodeData(3));
        g.addNode(new NodeData(4));
//        g.addNode(new NodeData(5));
//        g.addNode(new NodeData(6));
        g.connect(1, 0, 1);
        g.connect(0, 2, 1);
        g.connect(2, 4, 1);
        g.connect(1, 2, 1);
        g.connect(3, 2, 1);
        g.connect(4, 3, 1);
//        g.connect(1, 0, 1);
//        g.connect(5, 6, 1);
//        g.connect(6, 5, 1);
        ga.init(g);
        System.out.println(ga.isConnected());
//        g.addNode(new NodeData(5));
//        g.connect(0, 1, 8.33);
//        g.connect(1, 3, 2.1);
//        g.connect(3, 4, 4.9);
//        g.connect(0, 2, 4.2);
//        g.connect(0, 3, 3.3);
//        g.connect(3, 5, 15.7);
//        g.connect(0, 5, 100.4);
//        g.connect(4, 5, 1.4);
//        g.connect(5, 4, 13.4);
//        g.connect(1, 4, 17.4);
//        g.connect(2, 5, 11.5);
//        g.connect(5, 0, 0.5);
//        g.connect(3, 1, 7.5);
//        g.connect(3, 2, 1.96);
//        g.connect(2, 0, 13.7);
//        g.connect(4, 3, 3.7);
        System.out.println(graph);
        ga.init(g);
        System.out.println(ga.shortestPath(0, 5));
        ga.save("MyFile.txt");

        if (ga.copy().equals(ga.getGraph())) System.out.println("copy equals getGraph");
//        if (ga.getGraph().equals(ga.load("MyFile.txt"))) System.out.println("getGraph equals load"); //TODO: FIX load().


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
        if (graph.getV().size() <= 1) return true; //A graph with 0 or 1 nodes is a connected graph.

        for (node_data n : graph.getV()) //If isConnectedBFS returns false then the graph isn't connected.
            if (!isConnectedBFS(n))
                return false;

        return true;
    }

    private boolean isConnectedBFS(node_data start) {
        resetTags();

        node_data curr = start;
        node_data neighbor = null;
        resetTags(); // Was added to make sure all node's tags are reset.
        Queue<node_data> queue = new LinkedList<>();
        queue.add(curr);
        curr.setTag(1);

        while (!queue.isEmpty()) {
            curr = queue.poll();
            Collection<edge_data> outEdges = graph.getE(curr.getKey());
            if (outEdges.size() == 0)
                return false; //If a single node has no outgoing edges - the graph is not connected.

            for (edge_data e : outEdges) { //Iterate over outgoing edges from curr
                neighbor = graph.getNode(e.getDest());

                if (neighbor.getTag() == 0) { //If neighbor is not a visited node
                    queue.add(neighbor); //Add it to the queue
                    neighbor.setTag(1); //Mark it as visited
                }
            }
        }

        if (allNodesWereVisited(graph))
            return true;

        return false;
    }

    /**
     * This method checks if all nodes on the graph have been visited.
     * If one node was not visited then returns false - meaning one node was not reached through another node.
     *
     * @param graph - this graph.
     * @return true iff all nodes on the graph were visited.
     */
    private boolean allNodesWereVisited(directed_weighted_graph graph) {
        for (node_data n : graph.getV())
            if (n.getTag() == 0) return false;

        return true;
    }

    // Returns the first node encountered in this graph's node collection.
    // O(1).
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
        List<node_data> path = shortestPath(src, dest); //Execute a shortestPath Algo from src to dest.
        if (path == null) return -1;
        int size = path.size(); //Keep the size of the path.

        //If size > 0 -> There is a path from src to dest, return the total weight.
        if (size > 0) return path.get(size - 1).getWeight();

        return -1;
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
        resetTags();

        if (graph.getNode(src) == null || graph.getNode(dest) == null) return null;
        if (src == dest || graph.getE(src).size() == 0) return null;

        HashMap<Integer, node_data> prevNode = new HashMap<Integer, node_data>(); //A map of parent nodes. for (Integer) key, map (node_info) parent.

        PriorityQueue<node_data> pq = new PriorityQueue<node_data>(new NodeComparator()); //The BFS queue
        List<node_data> path = null;

        boolean destinationFound = false;
        node_data curr = null, neighbor = null;

        setWeightInfinity(); //Init all distances from node 'src' to infinity
        graph.getNode(src).setWeight(0); //The distance from src to src is 0.
        pq.add(graph.getNode(src)); //Add start node to the queue and start traversing.

        double totalDist;

        while (!pq.isEmpty()) {

            curr = pq.poll();
            System.out.println("Popped from queue: " + curr.getKey() + ", with weight: " + curr.getWeight());

            for (edge_data outEdge : graph.getE(curr.getKey())) {
                neighbor = graph.getNode(outEdge.getDest());
                if (neighbor == null) continue;
                System.out.println(outEdge); // SHOULD be REMOVE when we finish our tests
                if (neighbor.getKey() == dest) destinationFound = true;
                totalDist = curr.getWeight() + outEdge.getWeight();

                if (totalDist < neighbor.getWeight()) { //If the total distance is less than the known distance from neighbor to src.
                    neighbor.setWeight(totalDist);
                    if (prevNode.putIfAbsent(neighbor.getKey(), curr) != null)//If the neighbor's key isn't associate with curr's node
                        prevNode.put(neighbor.getKey(), curr);// Update the current calling node in prevNode.
                }
                if (!pq.contains(neighbor) && neighbor.getTag() == 0) pq.add(neighbor);
            }
            curr.setTag(1);
        }

        if (destinationFound)
            path = rebuildPath(src, dest, prevNode);

        return path;
    }

    private List<node_data> rebuildPath(int src, int dest, HashMap<Integer, node_data> prevNode) {
        List<node_data> path = new LinkedList<node_data>();

        node_data current = graph.getNode(dest), next = null;
        current.setTag(0);
        path.add(current);

        while (current.getKey() != src) {
            next = prevNode.get(current.getKey()); //Extract the node who called current node.
            next.setTag(0); //Set tag to 0 (resetTags()) because nodes are deep copied with tag == 1.
            path.add(new NodeData(next)); //Add a deep copy of 'next' to the list.
            current = next; //Increment current node.
        }

        Collections.reverse(path);
        return path; //Return a path with all copied nodes on the requested path.
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
    @Override // Was checking from internet, NEED to check together!
    public boolean save(String file) {
//        try {
////            Gson GraphGson = new GsonBuilder().create(); // create a jSon object
//            Gson GraphGson= new Gson(); // create a jSon object
//            String jsonString = GraphGson.toJson(graph); // create a string object that contain the data of graph object
//            File Ofile = new File(file); // crate a file
//            PrintWriter pw = new PrintWriter(Ofile); // create a printWriter object that contain our "file"
//            pw.write(jsonString); // add the jsonString object that contain the graph to pw object
//            pw.close(); // close this pw object
//            System.out.println("Graph save successful");
//            return true;
//        } catch (FileNotFoundException e) { // if something want wrong return false
//            System.out.println("Graph save failed");
//            e.printStackTrace();
//            return false;
//        }

        Gson json = new GsonBuilder().create();
        JsonObject graph = new JsonObject();
        JsonArray nodes = new JsonArray();
        JsonArray edges = new JsonArray();


        for (node_data node : this.graph.getV()) {
            JsonObject v = new JsonObject();
            v.addProperty("pos", node.getLocation().x() + "," + node.getLocation().y() + "," + node.getLocation().z());
            v.addProperty("id", node.getKey());
            nodes.add(v);

            for (edge_data outEdge : this.graph.getE(node.getKey())) {
                JsonObject edge = new JsonObject();
                edge.addProperty("src", outEdge.getSrc());
                edge.addProperty("w", outEdge.getWeight());
                edge.addProperty("dest", outEdge.getDest());
                edges.add(edge);
            } //Add all edges to the JSONArray Object
        } //Add all nodes to the JSONArray Object

        graph.add("Edges", edges);
        graph.add("Nodes", nodes);

        File f = new File(file);
        try {
            FileWriter writer = new FileWriter(f);
            writer.write(json.toJson(graph));
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to save graph");
            return false;
        }


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
    @Override // NEED TO CHECK , was created by watching Simon Pikalov video guide.
    public boolean load(String file) { //TODO: THIS METHOD DOES NOT WORK.
//        try {
////            Gson GraphGson = new Gson(); // Create a jSon object
//            GsonBuilder GraphGson = new GsonBuilder();
//            JsonDeserializer<directed_weighted_graph> deserializerGraph = new JsonDeserializer<directed_weighted_graph>() { // inner class
//                public directed_weighted_graph deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
//
//                    JsonObject jsonObject = json.getAsJsonObject(); // get the Json Object
//                    JsonArray edges = jsonObject.get("Edges").getAsJsonArray(); // define edges to "Edges" on json
//                    JsonArray nodes = jsonObject.get("Nodes").getAsJsonArray(); // define nodes to "Nodes" on json
//                    directed_weighted_graph g = new DWGraph_DS(); // Init graph
//
//                    for (JsonElement i : nodes) { // start to build graph's nodes
//                        int node_id = i.getAsJsonObject().get("id").getAsInt();
//                        // should add location object here due that node data contain geo_location object.
//                        node_data n = new NodeData(node_id); // create node data by node id.
//                        g.addNode(n); // add this node data to this new graph
//                    }
//                    for (JsonElement i : edges) { // supposed to connect those node to edges by "Edges" json String
//                        int src = i.getAsJsonObject().get("src").getAsInt();
//                        int dest = i.getAsJsonObject().get("dest").getAsInt();
//                        double w = i.getAsJsonObject().get("w").getAsDouble();
//                        g.connect(src, dest, w);
//                    }
//                    return g;
//                }
//            };
//            GraphGson.registerTypeAdapter(directed_weighted_graph.class, deserializerGraph);
//            Gson customGraph = GraphGson.create();
//            graph = customGraph.fromJson(file, directed_weighted_graph.class); // Read the json string and place this graph object on graph.
//            System.out.println("Graph loaded successful");
//            return true;
//        } catch (NullPointerException e) // If file == null
//        {
//            System.out.println("Graph loaded failed");
//            return false;
//        }

        Gson graph = new GsonBuilder().create();
        String graphJSON;
        directed_weighted_graph loadedGraph;
        try {
            FileReader reader = new FileReader(new File(file));
            int fileSize = reader.read();
            if (fileSize > 0)
                loadedGraph = (directed_weighted_graph) graph.fromJson(reader, DWGraph_DS.class);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

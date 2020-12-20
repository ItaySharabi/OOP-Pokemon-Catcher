package gameClient;

import Server.Game_Server_Ex2;
import api.*;
import com.google.gson.*;
import gameClient.util.Point3D;

import java.util.*;

public class Ex2 implements Runnable {

    private static List<CL_Pokemon> _pokemons;
    private static List<CL_Agent> _agents;
    private static game_service _game;
    private static Arena _ar;
    private static MyFrame _win;
    private static HashMap<Integer, HashMap<Integer, List<node_data>>> allRoutes;
    private static HashMap<Integer, HashMap<Integer, Double>> allRoutesDist;
    private static List<node_data> agentCurrentPath;
    private static dw_graph_algorithms graphAlgo;
    private static directed_weighted_graph graph;
    private static Thread client;
    private static int _level;
    private static int _id;

    public Ex2(String id, String level_number) {
        _id = Integer.parseInt(id);
        _level = Integer.parseInt(level_number);
    }
    public Ex2(){

    }
    public static void main(String[] args) {

        //TODO: Login screen.

//        for (int i = _level; i < 24; _level++) {
            try {
                if (client != null)
                    client.join();
                client = new Thread(new Ex2(args[0], args[1]));
//                client=new Thread(new Ex2());
//                _id=31364947;
//                _level=-1;
                _game = Game_Server_Ex2.getServer(_level);
                loginScreen(_id);
                init();
                client.start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
//    }

    /**
     * This method instantiates the game.
     * It is done by initializing the Arena object '_ar', the game graph,
     * and pokemons and agents lists, and determining the starting node
     * for each agent to start the game on. This is calculated by
     * summing all pokemons values on all edges on the graph, meaning they will
     * start the game catching the pokemons that currently worth most.
     */
    private static void init() {

        _ar = new Arena(); //Init a new Arena.
        graphAlgo = new DWGraph_Algo(loadGraph(_game.getGraph())); //Init graph algo class with the game graph.
        graph = graphAlgo.getGraph(); //Get a reference to the game graph.

        _pokemons = Arena.json2Pokemons(_game.getPokemons()); //Create a Pokemon list from a Json.


        for (int i = 0; i < _pokemons.size(); i++) //Iterate over all pokemons
            Arena.updateEdge(_pokemons.get(i), graph); //And match them with the right edge on the graph

        _ar.setGraph(graph);
        _ar.setPokemons(_pokemons); //Set the arena with the generated info.

        initiallySetGameAgents(); // Decide the starting nodes for all game agents.
        _ar.setAgents(_agents);

        _win = new MyFrame("test Ex2"); //Initialize the game window
        _win.setSize(1000, 700);
        _win.update(_ar, _game);
        _win.show();

        //Calculate all shortest paths on the graph from one node to another,
        //and store that data in HashMaps 'allRoutes' and 'allRoutesDist'
        //to save the total distance of each path.
        allRoutes = new HashMap<Integer, HashMap<Integer, List<node_data>>>();
        calcAllPaths(graphAlgo);
        allRoutesDist = new HashMap<Integer, HashMap<Integer, Double>>();
        calcAllPathsDist(graphAlgo);
    }

    /**
     * Main game thread logic:
     * Start the game using the game server obj '_game'.startGame().
     * While game is not out of time, just loop agent movement along
     * the graph.
     */
    @Override
    public synchronized void run() {
        _game.startGame();
        _win.setTitle("Time left: " + _game.timeToEnd() + " " + _game.toString());

        while (_game.isRunning()) {

            try {
                moveAgents();
                _win.repaint();
                client.sleep(sleepWell());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String res = _game.toString();

        System.out.println(res);
//        System.exit(0);
    }

    /**
     * Moves each of the agents along the edge,
     * in case the agent is on a node the next destination (next edge) is chosen (randomly).
     */
    private static synchronized void moveAgents() throws InterruptedException {

        moveAndUpdate();
        boolean isStuck = false;
        agentCurrentPath = new LinkedList<>();
        CL_Agent ag;
        int dest;

        for (int i = 0; i < _ar.getAgents().size(); i++) {
            ag = _ar.getAgents().get(i);
            if (getBestPokemon(ag)) {// Match given agent with the best pokemon on the ideal edge.
                agentCurrentPath = getShortestPathTo(ag, ag.get_curr_fruit().get_edge().getSrc());
                trackPokemonsOnList(agentCurrentPath); //TODO: Isn't setting isTracked
            } else //agent has nowhere to go and needs to go the lowest amount of moves.
                isStuck = true;

            int id = ag.getID();
            if (!isStuck) {
                if (agentCurrentPath.size() >= 1) //There is a valid route for the agent.
                    dest = agentCurrentPath.get(1).getKey(); // Next dest will always be at index 1 on the list.
                else {
                    dest = ag.get_curr_fruit().get_edge().getDest(); // Catch the pokemon
                }
            } else { //If "stuck" -- has no pokemon to hunt.
                dest = getMinimalNode(ag.getCurrNode());
            }
            _game.chooseNextEdge(id, dest);
        }
    }

    /**
     * This method is called inorder to call
     * _game.move() method which moves all agents along the
     * graph and runs more game logic.
     * After this method is called an update is needed so all
     * other game info objects needs to update as well.
     */
    public static void moveAndUpdate() {
        String lg = _game.move(); // Need to use at least 10 times in 1 sec according to boaz instruction
        _win.setTitle("Time left: " + _game.timeToEnd() / 1000 + " " + _game.toString());
        _agents = Arena.getAgents(lg, graph); //receive the last update for agents locations after game.move().
        _ar.setAgents(_agents); //Update agents list.
        String fs = _game.getPokemons(); //Receive new pokemons json as String.
        _pokemons = Arena.json2Pokemons(fs); //update pokemons list.

        for (CL_Pokemon poke : _pokemons)
            Arena.updateEdge(poke, graph); //Update graph edges to present all existing pokes.

        _ar.setPokemons(_pokemons);//Update the new pokemons list in the arena.
    }

    /**
     * This method is used to parse a json String into
     * a graph object 'DW_GraphDS'.
     *
     * @param json - a json representing a directed, weighted graph.
     * @return a directed_weighted_graph graph.
     */
    public static directed_weighted_graph loadGraph(String json) {
        directed_weighted_graph newGraph = new DWGraph_DS();
        JsonObject graph = new JsonParser().parse(json).getAsJsonObject();
        JsonArray edges = graph.getAsJsonArray("Edges"); //Get The "Edges" member from the Json Value.
        JsonArray nodes = graph.getAsJsonArray("Nodes"); //Get The "Edges" member from the Json Value.

        for (JsonElement node : nodes) {

            int key = ((JsonObject) node).get("id").getAsInt();
            double x, y, z;
            String pos = ((JsonObject) node).get("pos").getAsString();
            String[] posArr = pos.split(",");
            x = Double.parseDouble(posArr[0]);
            y = Double.parseDouble(posArr[1]);
            z = Double.parseDouble(posArr[2]);
            geo_location location = new Point3D(x, y, z);
            node_data n = new NodeData(key); //Insert into node_data n values from Json.
            n.setLocation(location); //Insert into node_data n location values from Json that created.
            newGraph.addNode(n);
        }

        for (JsonElement edge : edges) {
            int src = ((JsonObject) edge).get("src").getAsInt(); //Receive src
            double weight = ((JsonObject) edge).get("w").getAsDouble(); //Receive weight
            int dest = ((JsonObject) edge).get("dest").getAsInt(); //Receive dest

            edge_data e = new EdgeData(src, dest, weight); //Build a new edge with given args
            newGraph.connect(e.getSrc(), e.getDest(), e.getWeight()); //Connect between nodes on the new graph
        }
        return newGraph;
    }

    /**
     * This method returns the list representing the path of nodes to travel
     * from an agents current node to a destination 'pokeDest'.
     *
     * @param agent
     * @param pokeDest
     * @return a List<node_data>
     */
    public static List<node_data> getShortestPathTo(CL_Agent agent, int pokeDest) {
        return allRoutes.get(agent.getSrcNode()).get(pokeDest);
    }

    /**
     * This method sets the game agents for the first time.
     * Fill an array of size [agents.capacity()] with the best pokemons to start from
     * and set each agent with a pokemon to start catching.
     */
    public static void initiallySetGameAgents() {

        //Receive info from the game server for agent capacity.
        JsonElement gameElement = JsonParser.parseString(_game.toString());
        JsonObject gameServerObjects = gameElement.getAsJsonObject();
        JsonElement gameServerElements = gameServerObjects.get("GameServer");
        JsonObject gameServerObject = gameServerElements.getAsJsonObject();
        int agentCapacity = gameServerObject.get("agents").getAsInt();
        int[] occupiedNodes = new int[agentCapacity];

        CL_Pokemon[] pokemon = new CL_Pokemon[agentCapacity]; //
        int initialNode;
        for (int i = 0; i < agentCapacity; i++) {
            pokemon[i] = getInitialMaxRatio();
            if (pokemon[i] == null) //Means all pokemons are tracked and the next agent will "wonder".
                initialNode = bestInitialNode(occupiedNodes); //Find the best arbitrary node to send the agent to.
            else
                initialNode = pokemon[i].get_edge().getSrc();

            occupiedNodes[i] = initialNode;
            _game.addAgent(initialNode);
            pokemon[i].setisTracked(true);
        }
        _agents = Arena.getAgents(_game.getAgents(), graphAlgo.getGraph());
        for (int i = 0; i < agentCapacity; i++) {
            _agents.get(i).set_curr_fruit(pokemon[i]);
        }
    }

    /**
     * This method finds the best initial node to set
     * as a starting point for some agent.
     *
     * @param nodesNotToUse - an array of 'occupied' nodes, that other agent start from.
     * @return the best node to start for an agent.
     */
    private static int bestInitialNode(int[] nodesNotToUse) {
        node_data occupied;
        boolean isAvailable = false;
        int ans = 0;
        for (node_data node : graph.getV()) {

            for (int i = 0; i < nodesNotToUse.length; i++) {
                occupied = graph.getNode(nodesNotToUse[i]);
                if (!node.equals(occupied)) {
                    isAvailable = true;
                    ans = node.getKey();
                }
            }
            if (isAvailable)
                return ans;
        }

        /*
        If reached this return statement, all nodes on the graph are currently occupied,
        and cannot find any free node, so return just any node.
         */
        return graph.getNode(nodesNotToUse[0]).getKey();
    }

    /**
     * This method calcs the lowest edge by weight and returns its destination.
     * node's key.
     *
     * @param from - the node to look from.
     * @return the integer of the lowest destination node.
     */
    private static int getMinimalNode(node_data from) {
        double min = Double.MAX_VALUE;
        int ans = 0;
        for (edge_data e : graph.getE(from.getKey())) {
            if (e.getWeight() < min) {
                ans = e.getSrc();
                min = e.getWeight();
            }
        }
        return ans;
    }

    /**
     * This method returns the pokemon with the max value that is not tracked by another
     * agent.
     * Calculation is the sum of values for all pokemons on a certain edge,
     * divided by the weight of the edge.
     *
     * @return the pokemon with the lowest ratio (Best decision).
     */
    public static CL_Pokemon getInitialMaxRatio() { // Created for init agents
        CL_Pokemon poke = null;
        double minRatio = Double.MAX_VALUE, weight, value, ratio;

        for (CL_Pokemon pokei : _pokemons) {
            if (!pokei.getisTracked()) {

                weight = pokei.get_edge().getWeight();
                value = sumEdgeValue(pokei.get_edge());
                ratio = weight / value;

                if (minRatio > ratio) {
                    minRatio = ratio;
                    poke = pokei;
                }
            }
        }
        if (poke == null) return null;
        trackPokemonsOnEdge(poke.get_edge()); //Marks all pokemons on the edge as tracked.
        return poke;
    }

    /**
     * This method calculates and matches a given agent 'ag'
     * with the best pokemon available to catch.
     * This calculation is different from the initial matching calculation
     * by taking in account the path weight to travel.
     * If a match is made, mark the pokemon as 'Tracked'.
     *
     * @param ag - the agent to choose a pokemon for.
     * @return true or false if the given agent has been matched with a pokemon or not.
     */
    public synchronized static boolean getBestPokemon(CL_Agent ag) {
        double dist, minRatio = Double.MAX_VALUE; //minRatio gives the best Pokemon.
        double value, minpath;
        if (ag.get_curr_fruit() != null) return false; //TODO Tried something throws NULLPointer
        CL_Pokemon pokemon = null;
        List<node_data> path; //Execute a shortestPath Algo from src to dest.

        for (CL_Pokemon poke : _pokemons) {
            if (poke.getisTracked()) continue;

            path = allRoutes.get(ag.getSrcNode()).get(poke.get_edge().getSrc());//Execute a shortestPath Algo from src to dest.
            if (path != null) {
                minpath = allRoutesDist.get(ag.getSrcNode()).get(poke.get_edge().getDest());// dist between curr ag to curr poke
            } else minpath = poke.get_edge().getWeight();

            value = sumEdgeValue(poke.get_edge());
            dist = minpath / value; // Pokemon value-dist ratio.

            if (minRatio > dist) {
                pokemon = poke;
                minRatio = dist;
            }

        }
        ag.set_curr_fruit(pokemon);
        if (ag.get_curr_fruit() == null) return false;
        pokemon.setisTracked(true);

        return true;
    }

    /**
     * Calculates the distance of the path.
     *
     * @param path
     * @return
     */
    public static double pathDist(List<node_data> path) {
        double weight = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            node_data node1 = path.get(i);
            node_data node2 = path.get(i + 1);
            weight += graph.getEdge(node1.getKey(), node2.getKey()).getWeight();

        }
        return weight;
    }

    /**
     * This method sums all pokemon values that are associated with the given edge.
     *
     * @param edge
     * @return
     */
    private static double sumEdgeValue(edge_data edge) {
        double sum = 0;
        for (CL_Pokemon poke : _pokemons) {
            if (poke.get_edge().equals(edge)) sum += poke.getValue();
        }
        return sum;
    }

    /**
     * This method marks all pokemons on a given path as tracked pokemons.
     *
     * @param path
     */
    public static void trackPokemonsOnList(List<node_data> path) {

        edge_data edge;

        for (int i = 0; i < path.size() - 1; i++) {
            node_data node1 = path.get(i);
            node_data node2 = path.get(i + 1);
            edge = graph.getEdge(node1.getKey(), node2.getKey());
            trackPokemonsOnEdge(edge);
        }
    }

    /**
     * This method marks all Pokemons on the given edge 'e' as tracked.
     *
     * @param e
     */
    public static void trackPokemonsOnEdge(edge_data e) {
        for (CL_Pokemon poke : _pokemons) {
            if (!poke.getisTracked()) {
                if (e.equals(poke.get_edge())) {
                    poke.setisTracked(true);
                }
            }
        }
    }

    /**
     * This method computes and stores all shortest paths from
     * all nodes on the graph to all others.
     * Stores the data in a HashMap<K,V> allRoutes.
     *
     * @param graphAlgo
     */
    public static void calcAllPaths(dw_graph_algorithms graphAlgo) { // need to change a little
        directed_weighted_graph graph = graphAlgo.getGraph();
        //added empty map to add values after
        Iterator<node_data> itr = graph.getV().iterator();

        while (itr.hasNext()) {
            HashMap<Integer, List<node_data>> pathsMap = new HashMap<Integer, List<node_data>>(); //Init new HashMap for a node
            allRoutes.put(itr.next().getKey(), pathsMap); //Put (node, pathsMap).
        }

        //Iterate over all graph nodes
        for (node_data node : graph.getV()) {
            int src = node.getKey();

            for (node_data destNode : graph.getV()) {
                int dest = destNode.getKey();
                List<node_data> list = graphAlgo.shortestPath(src, dest);
                allRoutes.get(src).put(dest, list);
            }
        }
    }

    /**
     * This method computes and stores all shortest paths distance from
     * all nodes on the graph to all others.
     * Stores the data in a HashMap<K,V> allRoutesDist.
     *
     * @param graphAlgo
     */
    public static void calcAllPathsDist(dw_graph_algorithms graphAlgo) { // need to change a little
        directed_weighted_graph graph = graphAlgo.getGraph();
        //added empty map to add values after
        Iterator<node_data> itr = graph.getV().iterator();

        while (itr.hasNext()) {
            HashMap<Integer, Double> innerHashMap = new HashMap<Integer, Double>(); //Init new HashMap for a node
            allRoutesDist.put(itr.next().getKey(), innerHashMap); //Put (node, pathsMap).
        }

        //Iterate over all graph nodes
        for (node_data node : graph.getV()) {
            int src = node.getKey();

            for (node_data destNode : graph.getV()) {
                int dest = destNode.getKey();
                double weight = pathDist(allRoutes.get(src).get(dest));
                allRoutesDist.get(src).put(dest, weight);
            }
        }
    }


    private static void loginScreen(int id) {

        if (_game.login(id)) {
            System.out.println("Logged in with: " + id);
        } else {
            System.out.println("Failed to log in");
        }
    }

    /**
     * This method will return the number of millis
     * to make the game thread sleep intelligently.
     * If an agent is "close enough" to a pokemon, the
     * thread will sleep less --> about x ms 0<x<1.
     * Else the thread can sleep a bit more during the game.
     *
     * @return - The ideal time to sleep.
     */
    private static long sleepWell() {
        long sleep = 70; //1
        double distFromPoke = 0;
        int agentCurrNode = 0, pokeSrcNode = 0;
        CL_Pokemon pokemon;

        for (CL_Agent agent : _agents) {
            pokemon = agent.get_curr_fruit();
            if (pokemon == null) continue;
            pokeSrcNode = pokemon.get_edge().getSrc();
            agentCurrNode = agent.getSrcNode();
            distFromPoke = agent.get_pos().distance(agent.get_curr_fruit().getLocation());
            if (agentCurrNode == pokeSrcNode) {
                if (distFromPoke < 0.1)
                    return calcSDT(agent);
            } else if (sleep <= 90) {
                sleep += 40;
            }
        }
        return sleep;

    }

    /**
     * Main method to calc ideal sleep time.
     * This method was given by the course staff.
     *
     * @param agent
     * @return
     */
    public static long calcSDT(CL_Agent agent) {
        agent.set_SDT((long) agent.getSpeed());
        return agent.get_sg_dt();
    }
}

package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import gameClient.Arena;
import gameClient.CL_Agent;
import gameClient.CL_Pokemon;

import java.util.ArrayList;
import java.util.List;

public class GameService implements game_service{
    dw_graph_algorithms graph;
    List<CL_Agent> agents;
    List<CL_Pokemon> pokemons;
    Arena arenaGame;
    Boolean gameStatus;

    public GameService(){
        graph=new DWGraph_Algo();
        pokemons=new ArrayList<CL_Pokemon>();
        agents=new ArrayList<CL_Agent>();
        gameStatus=false;
    }

    public GameService(dw_graph_algorithms g,List<CL_Pokemon> pokemons,List<CL_Agent> agents){
        graph=g;
        this.pokemons=pokemons;
        this.agents=agents;
        gameStatus=false;
    }
    /**
     * Returns a JSON representation of graph as a JSON String.
     *
     * @return
     */
    @Override
    public String getGraph() {
        Arena g=new Arena();
        graph.init(g.getGraph());
        Gson json = new GsonBuilder().create();
        JsonObject JsonGraph = new JsonObject();
        JsonArray nodes = new JsonArray();
        JsonArray edges = new JsonArray();

        for (node_data node : graph.getGraph().getV()) {
            JsonObject v = new JsonObject();
            double x = node.getLocation().x(), y = node.getLocation().y(), z = node.getLocation().z();
            v.addProperty("pos", x + "," + y + "," + z);
            v.addProperty("id", node.getKey());
            nodes.add(v);
            for (edge_data outEdge : graph.getGraph().getE(node.getKey())) {
                JsonObject edge = new JsonObject();
                edge.addProperty("src", outEdge.getSrc());
                edge.addProperty("w", outEdge.getWeight());
                edge.addProperty("dest", outEdge.getDest());
                edges.add(edge);
            } //Add all edges to the JSONArray Object
        } //Add all nodes to the JSONArray Object
        JsonGraph.add("Edges", edges);
        JsonGraph.add("Nodes", nodes);

        return JsonGraph.getAsString();
    }

    /**
     * Returns a JSON string, representing all Pokemons (fixed bonus coin).
     *
     * @return
     */
    @Override
    public String getPokemons() {
        return null;
    }

    /**
     * Returns a JSON string, representing all the Agents.
     *
     * @return
     */
    @Override
    public String getAgents() {
        String getAgentsStr="";
        for (CL_Agent agent:agents)
        {
            getAgentsStr+=agent.toString()+"\n";
        }
        return getAgentsStr;
    }

    /**
     * This method allows the user to add & locate the agents,
     * all should be located in order to start a game.
     *
     * @param start_node - the vertex in the graph from which the agent will start.
     * @return
     */
    @Override
    public boolean addAgent(int start_node) {
        if(graph.getGraph().getNode(start_node)!=null && graph.getGraph()!=null)
        {
            CL_Agent agent=new CL_Agent(graph.getGraph(),start_node);
            return true;
        }
        return false;
    }

    /**
     * Start a new game
     *
     * @return the time (new Date().getTime()) if a new game was started, else -1.
     */
    @Override
    public long startGame() {
        if(graph.getGraph()!=null) { // if there is a graph
            arenaGame = new Arena();
            arenaGame.setGraph(graph.getGraph());
            arenaGame.setAgents(agents);
            arenaGame.setPokemons(pokemons);
            gameStatus=true;
            return System.currentTimeMillis();
        } // if there is no graph for arena
        gameStatus=false;
        return -1;
    }

    /**
     * Returns the current status of the game (true: is running, false: NOT running).
     *
     * @return
     */
    @Override
    public boolean isRunning() {
        return gameStatus;
    }

    /**
     * Stops the game, after this method the isRunning() will return false
     *
     * @return
     */
    @Override
    public long stopGame() {
        if(isRunning()) {
            gameStatus = false;
            return System.currentTimeMillis();
        }
        else return -1; // NEED TO CHECK
    }

    /**
     * This method is the main logical functionality, allows the client algorithm
     * to direct each agent to the "next" edge.
     *
     * @param id        - the agent id, as received from the the JSON String
     * @param next_node - the next edge defined as (src,next_node)
     * @return the time the action was performed (-1 if not performed).
     */
    @Override
        public long chooseNextEdge(int id, int next_node) {
        return 0;
    }

    /**
     * return the number of mili-seconds till the game is over
     *
     * @return
     */
    @Override
    public long timeToEnd() {
        return 0;
    }

    /**
     * moves all the agents along each edge,
     * if the agent is on the node
     * (nothing is done - requires to chooseNextEdge(int id, int next_node)
     *
     * @return a JSON like String - representing status of all the agents.
     */
    @Override
    public String move() {
        return null;
    }

    /**
     * Performs a login - so the results of the game will be stored in the data-base after the game,
     * requires Internet connection. The following data is stored: id, level, number of moves, grade & time.
     *
     * @param id
     * @return: true iff the user was successfully logged-in to the server.
     */
    @Override
    public boolean login(long id) {
        return false;
    }
}

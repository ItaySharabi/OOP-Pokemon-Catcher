package gameClient;

import Server.Game_Server_Ex2;
import api.game_service;
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
    private static HashMap<Integer, HashMap<Integer, List<node_data>>> dijkstraAllMap; // TODO: should rename
    private static List<node_data> agentCurrentPath;
    private static dw_graph_algorithms graphAlgo;
    private static directed_weighted_graph graph;


    public static void main(String[] args) {

        int level = 1;
        _game = Game_Server_Ex2.getServer(level);
        init();
        Thread client = new Thread(new Ex2());
        client.start();

    }

    private static void init() {
        graphAlgo = new DWGraph_Algo(loadGraph(_game.getGraph()));

        graph=graphAlgo.getGraph();
        _pokemons = Arena.json2Pokemons(_game.getPokemons());

        _ar = new Arena();

        for (int i = 0; i < _pokemons.size(); i++) {
            Arena.updateEdge(_pokemons.get(i), graph);
        }
        _ar.setGraph(graph);
        _ar.setPokemons(_pokemons);

        SetGameAgents();
        _ar.setAgents(_agents);

        _win = new MyFrame("test Ex2");
        _win.setSize(1000, 700);
        _win.update(_ar);
        _win.show();
        System.out.println("poke \n" + _pokemons);
        System.out.println("agents \n" + _agents);
        dijkstraAllMap = new HashMap<Integer, HashMap<Integer, List<node_data>>>();
        dijkstraAll(graphAlgo);
    }

    @Override
    public void run() {
        _game.startGame();
        _win.setTitle("Ex2 - OOP: (NONE trivial Solution) " + _game.toString());
        long dt = 100; // Created for thread's sleep

        while (_game.isRunning()) {
            moveAgants(_game, _ar.getGraph());
            try {
                    _win.repaint();
                Thread.sleep(dt);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String res = _game.toString();

        System.out.println(res);
        System.exit(0);
    }
    /**
     * Moves each of the agents along the edge,
     * in case the agent is on a node the next destination (next edge) is chosen (randomly).
     *
     * @param game
     * @param graph
     * @param
     */
    private static void moveAgants(game_service game, directed_weighted_graph graph) {
        String lg = game.move(); // Need to use at least 10 times in 1 sec according to boaz instruction
        _agents = Arena.getAgents(lg, graph); //receive the last update for agents locations after game.move().
        _ar.setAgents(_agents);
        String fs = game.getPokemons();
        _pokemons = Arena.json2Pokemons(fs);
        for(CL_Pokemon poke:_pokemons)
            Arena.updateEdge(poke,graph);
        _ar.setPokemons(_pokemons);

        agentCurrentPath = new LinkedList<>();
        CL_Agent ag;
        for (int i = 0; i < _ar.getAgents().size(); i++) {
           ag=_ar.getAgents().get(i);

            getBestPokemon(ag); // Match given agent with the best pokemon
            agentCurrentPath = makeListAgents(ag, ag.get_curr_fruit().get_edge().getSrc());
            int id = ag.getID();
            int dest;
            if(agentCurrentPath!=null) { // list exist
                if (agentCurrentPath.size() >= 1)
                    dest = agentCurrentPath.get(1).getKey(); // Next dest will always be at index 1 on the list.
                else dest=ag.get_curr_fruit().get_edge().getDest(); // Catch the pokemon
            }
            else dest=ag.get_curr_fruit().get_edge().getDest(); // Catch the pokemon
            game.chooseNextEdge(id, dest);
//            ag.setCurrNode(dest);
//            int src = ag.getSrcNode();
//            edge_data pokEdge= pok.get_edge();
//            if(pokType<0) // type = -1
//                pokdest=pok.get_edge().getDest();
//            else pokdest=pok.get_edge().getSrc(); // type = 1
//            if(agentPath.get(id)==null)
//                agentPath.put(id,gra.shortestPath(src,pokdest));
            double v = ag.getValue();
//            if(dest==-1) {
//                dest=agentPath.get(id).remove(ag.getSrcNode()).getKey();
//                System.out.println("Agent: "+id+", val: "+v+"   turned to node: "+dest);
//            }
        }
    }

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

    public static void dijkstraAll(dw_graph_algorithms graphAlgo) { // need to change a little
        directed_weighted_graph graph = graphAlgo.getGraph();
        //added empty map to add values after
        for (node_data nodeData : graph.getV()) {
            HashMap<Integer, List<node_data>> innerMap = new HashMap<Integer, List<node_data>>();
            dijkstraAllMap.put(nodeData.getKey(), innerMap);
        }

        for (node_data srcData : graph.getV()) {
            int src = srcData.getKey();
            for (node_data destData : graph.getV()) {
                int dest = destData.getKey();
                List<node_data> list = graphAlgo.shortestPath(src, dest);
                dijkstraAllMap.get(src).put(dest, list);
            }
        }
    }

    public static List<node_data> makeListAgents(CL_Agent agent, int pokeDest) {
        return dijkstraAllMap.get(agent.getSrcNode()).get(pokeDest);
    }

    public static void SetGameAgents() {
        JsonElement gameElement = JsonParser.parseString(_game.toString());
        JsonObject gameServerObjects = gameElement.getAsJsonObject();
        JsonElement gameServerElements = gameServerObjects.get("GameServer");
        JsonObject gameServerObject = gameServerElements.getAsJsonObject();
        int agentCapacity = gameServerObject.get("agents").getAsInt();
        CL_Pokemon[] pokemon = new CL_Pokemon[agentCapacity];
        for (int i = 0; i < agentCapacity; i++) {
            pokemon[i] = getInitialMaxRatio();
            _game.addAgent(pokemon[i].get_edge().getSrc());
            pokemon[i].setisTracked(true);
        }
        _agents = Arena.getAgents(_game.getAgents(), graphAlgo.getGraph());
        for (int i = 0; i < agentCapacity; i++) {
            _agents.get(i).set_curr_fruit(pokemon[i]);
        }
    }

    /**
     * This method returns the pokemon with the max value that is not tracked by another
     * agent.
     *
     * @return
     */
    public static CL_Pokemon getInitialMaxRatio() { // Created for init agents
        CL_Pokemon poke =null;
        double minRatio=Double.MAX_VALUE,weight,value,ratio;
        for (CL_Pokemon pokei : _pokemons) {
            if (!pokei.getisTracked()) {

                weight=pokei.get_edge().getWeight();
                value=pokei.getValue();
                ratio= weight/value;

                if (minRatio > ratio) {
                    minRatio = ratio;
                    poke = pokei;
                }

            }
        }
        return poke;
    }

    public static void getBestPokemon(CL_Agent ag){
        double dist,minRatio=Double.MAX_VALUE; //minRatio gives the best Pokemon.
        double value,minpath;
        CL_Pokemon pokemon=null;
        List<node_data> path; //Execute a shortestPath Algo from src to dest.
        for (CL_Pokemon poke:_pokemons) {
            if (!poke.getisTracked()) {
                path = dijkstraAllMap.get(ag.getSrcNode()).get(poke.get_edge().getSrc());//Execute a shortestPath Algo from src to dest.
                if(path!=null) {
                    minpath = path.get(path.size() - 1).getWeight() + poke.get_edge().getWeight();// dist between curr ag to curr poke
                }
               else minpath=poke.get_edge().getWeight();
                value = poke.getValue();
                dist = minpath/value; // Pokemon value-dist ratio.

                if (minRatio > dist) {
                    pokemon =poke;
                    minRatio=dist;
                }
            }
        }
        ag.set_curr_fruit(pokemon);
        pokemon.setisTracked(true);
    }

}

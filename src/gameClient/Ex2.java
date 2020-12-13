package gameClient;

import Server.Game_Server_Ex2;
import api.game_service;
import api.*;
import com.google.gson.*;
import gameClient.util.Point3D;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Ex2 implements Runnable {

    private static List<CL_Pokemon> _pokemons;
    private static List<CL_Agent> _agents;
    private static game_service _game;
    private static Arena _ar;
    private static MyFrame _win;
    private static HashMap<Integer,HashMap<Integer,List<node_data>>> dijkstraAllMap ;
    public static void main(String[] args) {

        int level = 0;
        _game = Game_Server_Ex2.getServer(level);
        init(_game);
//        Thread client = new Thread(new Ex2());
//        client.start();

//        _pokemons = Arena.json2Pokemons(_game.getPokemons()); //json2Pokemons() not implemented yet.
//        dw_graph_algorithms ga = new DWGraph_Algo(loadGraphFromJson(_game.getGraph()));
//        _agents=Arena.getAgents(_game.getAgents(), ga.getGraph());
//        System.out.println(_agents);

//        JsonObject json;
//        json = new JsonParser().parse(_game.getPokemons()).getAsJsonObject();
//        JsonArray pokemons = json.getAsJsonArray("Pokemons");
//
//        for (JsonElement pokemon : pokemons) {
//            JsonObject poke = pokemon.getAsJsonObject();
//            String s = poke.toString();
//            System.out.println(s);
//            _pokemons.add(CL_Pokemon.init_from_json(s));
//
//        }
//        for(int a = 0;a<_pokemons.size();a++) {
//            Arena.updateEdge(_pokemons.get(a),ga.getGraph());
//        }
//        System.out.println(_pokemons);
//
//        Thread client = new Thread(new Ex2());
//        client.start();
//        File f = new File("Arena1");
//        f.deleteOnExit();
//        f.delete();


    }
    private static void init(game_service game) {
//        int level = 0;
//        _game = Game_Server_Ex2.getServer(level);
        dw_graph_algorithms ga = new DWGraph_Algo(loadGraphFromJson(_game.getGraph()));
        _pokemons = Arena.json2Pokemons(_game.getPokemons());
        _ar =new Arena();
        _ar.setGraph(ga.getGraph());
        _ar.setPokemons(_pokemons);
        for(int i = 0;i<_pokemons.size();i++) {
            Arena.updateEdge(_pokemons.get(i), ga.getGraph());
        }
        SetGameAgents();
        _agents=Arena.getAgents(_game.getAgents(), ga.getGraph());
        _ar.setAgents(_agents);
        System.out.println("poke \n"+_pokemons);
        System.out.println("agents \n"+_agents);
        dijkstraAllMap=new HashMap<Integer, HashMap<Integer, List<node_data>>>();
        dijkstraAll(ga);
    }
    private static directed_weighted_graph loadGraphFromJson(String json) {

        dw_graph_algorithms ga = new DWGraph_Algo();
        try {
            FileWriter writer = new FileWriter("Arena1.json");
            writer.write(_game.getGraph());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ga.load("Arena1.json");
        return ga.getGraph();
    }
    private static void init1(game_service game) {
        String g = game.getGraph();
        String fs = game.getPokemons();
        directed_weighted_graph gg = game.getJava_Graph_Not_to_be_used();
        //gg.init(g);
        _ar = new Arena();
        _ar.setGraph(gg);
        _ar.setPokemons(Arena.json2Pokemons(fs));
        _win = new MyFrame("test Ex2");
        _win.setSize(1000, 700);
        _win.update(_ar);


        _win.show();
        String info = game.toString();
        JSONObject line;
        try {
            line = new JSONObject(info);
            JSONObject ttt = line.getJSONObject("GameServer");
            int rs = ttt.getInt("agents");
            System.out.println(info);
            System.out.println(game.getPokemons());
            int src_node = 0;  // arbitrary node, you should start at one of the pokemon
            ArrayList<CL_Pokemon> cl_fs = Arena.json2Pokemons(game.getPokemons());
            for(int a = 0;a<cl_fs.size();a++) { Arena.updateEdge(cl_fs.get(a),gg);}
            for(int a = 0;a<rs;a++) {
                int ind = a%cl_fs.size();
                CL_Pokemon c = cl_fs.get(ind);
                int nn = c.get_edge().getDest();
                if(c.getType()<0 ) {nn = c.get_edge().getSrc();}

                game.addAgent(nn);
            }
        }
        catch (JSONException e) {e.printStackTrace();}
    }

    @Override
    public void run() {
//        int scenario_num = 0;
//        game_service game = Game_Server_Ex2.getServer(scenario_num);// you have [0,23] games
//        //	int id = 999;
//        //	game.login(id);
        String g = _game.getGraph();
//        String pks = _game.getPokemons();
        directed_weighted_graph gg = _game.getJava_Graph_Not_to_be_used();
//        init(_game);

        _game.startGame();
       _win.setTitle("Ex2 - OOP: (NONE trivial Solution) "+_game.toString());
        int ind=0;
        long dt=100;

        while(_game.isRunning()) {
            moveAgants(_game, gg);
            try {
                if(ind%1==0) {_win.repaint();}
                Thread.sleep(dt);
                ind++;
            }
            catch(Exception e) {
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
     * @param game
     * @param graph
     * @param
     */
    private static void moveAgants(game_service game, directed_weighted_graph graph) {
        String lg = game.move();
        _agents = Arena.getAgents(lg, graph);
        _ar.setAgents(_agents);
        //ArrayList<OOP_Point3D> rs = new ArrayList<OOP_Point3D>();
        String fs =  game.getPokemons();
        _pokemons = Arena.json2Pokemons(fs);
        Iterator<CL_Pokemon> iter= _pokemons.iterator();
//        dw_graph_algorithms gra=new DWGraph_Algo();
//        gra.init(graph);
        _ar.setPokemons(_pokemons);
        List<node_data>[] agentlist=new List[_ar.getAgents().size()];
        for(int i=0;i<_ar.getAgents().size();i++) {
            CL_Agent ag = _ar.getAgents().get(i);
            CL_Pokemon pok=iter.next();
            int pokType=pok.getType();
            if(agentlist[i]==null) {
//                ag.set_curr_fruit(pok);
                if (pokType < 0) agentlist[i] = makeListAgents(ag, pok.get_edge().getDest()); // type = -1
                else agentlist[i] = makeListAgents(ag, pok.get_edge().getSrc()); // type = 1
            }
            int id = ag.getID();
            int src = ag.getSrcNode();
            int dest=-1;
//            agentlist[i].remove(ag.getCurrNode().getKey()).getKey();
//            if(ag.getNextNode()!=-1)
                dest = agentlist[i].get(ag.get_curr_edge().getDest()).getKey(); //
            ag.setCurrNode(dest);

//            edge_data pokEdge= pok.get_edge();
//            if(pokType<0) // type = -1
//                pokdest=pok.get_edge().getDest();
//            else pokdest=pok.get_edge().getSrc(); // type = 1
//            if(agentPath.get(id)==null)
//                agentPath.put(id,gra.shortestPath(src,pokdest));
//            double v = ag.getValue();
//            if(dest==-1) {
//                dest=agentPath.get(id).remove(ag.getSrcNode()).getKey();
////                dest = nextNode(graph, src);
                game.chooseNextEdge(id, dest);
//                System.out.println("Agent: "+id+", val: "+v+"   turned to node: "+dest);
//            }
        }
    }

    /**
     * a very simple random walk implementation!
     * @param g
     * @param src
     * @return
     */
    private static int nextNode(directed_weighted_graph g, int src) {
        int ans = -1;
        Collection<edge_data> ee = g.getE(src);
        Iterator<edge_data> itr = ee.iterator();
        int s = ee.size();
        int r = (int)(Math.random()*s);
        int i=0;
        while(i<r) {itr.next();i++;}
        ans = itr.next().getDest();
        return ans;
    }
    public static directed_weighted_graph loadGraph(String json) {
        directed_weighted_graph newGraph = new DWGraph_DS();
        JsonObject graph = new JsonParser().parse(json).getAsJsonObject();
        JsonArray edges = graph.getAsJsonArray("Edges"); //Get The "Edges" member from the Json Value.
        JsonArray nodes = graph.getAsJsonArray("Nodes"); //Get The "Edges" member from the Json Value.

        for (JsonElement node : nodes) {

            int key = ((JsonObject) node).get("id").getAsInt();
            double x,y,z;
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
    public static void dijkstraAll(dw_graph_algorithms graphAlgo){ // need to change a little
        directed_weighted_graph graph = graphAlgo.getGraph();
        //added empty map to add values after
        for (node_data nodeData : graph.getV()){
            HashMap<Integer, List<node_data>> innerMap = new HashMap<Integer, List<node_data>>();
            dijkstraAllMap.put(nodeData.getKey(), innerMap);
        }

        for (node_data srcData :graph.getV()){
            int src = srcData.getKey();
            for(node_data destData :graph.getV()){
                int dest = destData.getKey();
                List<node_data> list = graphAlgo.shortestPath(src, dest);
                dijkstraAllMap.get(src).put(dest, list);
            }
        }
    }

    public static void SetGameAgents(){
//        List<CL_Pokemon> gotOne= _ar.getPokemons();
        JsonElement gameElement = JsonParser.parseString(_game.toString());
        JsonObject pokemonsObject = gameElement.getAsJsonObject();
        JsonElement gameServerElement = pokemonsObject.get("GameServer");
        JsonObject gameServerObject = gameServerElement.getAsJsonObject();
        int agantCapcity= gameServerObject.get("agents").getAsInt();
        for (int i = 0; i <agantCapcity; i++){
            if(i+1 > _pokemons.size()){
                i=agantCapcity;
            }
            _game.addAgent(_pokemons.get(i).get_edge().getSrc());
//            _agents.get(i).set_curr_fruit(_pokemons.get(i));
        }
    }
    public static List<node_data> makeListAgents(CL_Agent agent ,int pokedest){
        return dijkstraAllMap.get(agent.getSrcNode()).get(pokedest);
    }

}

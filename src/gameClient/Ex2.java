package gameClient;

import Server.Game_Server_Ex2;
import api.game_service;
import api.*;
import com.google.gson.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Ex2 {

    private static List<CL_Pokemon> _pokemons;
    private static List<CL_Agent> _agents;
    private static game_service _game;
    private static Arena _ar;
    private static MyFrame _win;
    public static void main(String[] args) {

        int level = 23;
        _game = Game_Server_Ex2.getServer(level);
        _pokemons = Arena.json2Pokemons(_game.getPokemons()); //json2Pokemons() not implemented yet.
        init(_game);
        dw_graph_algorithms ga = new DWGraph_Algo(loadGraphFromJson(_game.getGraph()));
        _agents=Arena.getAgents(_game.getAgents(), ga.getGraph());
        System.out.println(_agents);

        JsonObject json;
        json = new JsonParser().parse(_game.getPokemons()).getAsJsonObject();
        JsonArray pokemons = json.getAsJsonArray("Pokemons");

        for (JsonElement pokemon : pokemons) {
            JsonObject poke = pokemon.getAsJsonObject();
            String s = poke.toString();
            System.out.println(s);
            _pokemons.add(CL_Pokemon.init_from_json(s));
        }

        System.out.println(_pokemons);

        File f = new File("Arena1");

        f.deleteOnExit();
        f.delete();


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
    private static void init(game_service game) {
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
}

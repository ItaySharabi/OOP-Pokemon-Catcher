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
import java.util.LinkedList;
import java.util.List;

public class Ex2 {

    private static List<CL_Pokemon> _pokemons;
    private static List<CL_Agent> _agents;
    private static game_service _game;

    public static void main(String[] args) {

        int level = 23;
        _game = Game_Server_Ex2.getServer(level);
        _pokemons = Arena.json2Pokemons(_game.getPokemons()); //json2Pokemons() not implemented yet.


        dw_graph_algorithms ga = new DWGraph_Algo(loadGraphFromJson(_game.getGraph()));


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
            FileWriter writer = new FileWriter("Arena1");
            writer.write(_game.getGraph());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
            ga.load("Arena1");
        return ga.getGraph();
    }
}

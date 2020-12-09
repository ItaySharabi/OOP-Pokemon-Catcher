package gameClient;

import Server.Game_Server_Ex2;
import api.game_service;
import api.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Ex2 {

    static List<CL_Pokemon> _pokemons;

    public static void main(String[] args) {

        int level = 13;
        game_service game = Game_Server_Ex2.getServer(level);
        _pokemons = new LinkedList<CL_Pokemon>();

        dw_graph_algorithms ga = new DWGraph_Algo();
        try {
            FileWriter writer = new FileWriter("Arena1");
            writer.write(game.getGraph());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ga.load("Arena1");

        JsonObject json;
        json = new JsonParser().parse(game.getPokemons()).getAsJsonObject();
        JsonArray pokemons = json.getAsJsonArray("Pokemons");

        for (JsonElement pokemon : pokemons) {
            JsonObject poke = pokemon.getAsJsonObject();
            String s = poke.toString();
            System.out.println(s);
        }


    }
}

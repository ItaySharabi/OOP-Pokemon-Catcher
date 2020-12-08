package gameClient;

import Server.Game_Server_Ex2;
import api.game_service;
import api.*;

public class Ex2 {

    public static void main(String[] args) {

        int level = 0;
        game_service game = Game_Server_Ex2.getServer(level);
        directed_weighted_graph gameGraph = new DWGraph_DS();
        dw_graph_algorithms ga = new DWGraph_Algo(gameGraph);
    }
}

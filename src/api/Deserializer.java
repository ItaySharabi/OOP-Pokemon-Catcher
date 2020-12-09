package api;

import com.google.gson.*;
import gameClient.util.Point3D;

import java.lang.reflect.Type;

public class Deserializer {
    JsonDeserializer <directed_weighted_graph> des = new JsonDeserializer<directed_weighted_graph>() {
        @Override
        public directed_weighted_graph deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

            directed_weighted_graph newGraph = new DWGraph_DS();
            JsonObject graph = jsonElement.getAsJsonObject();
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
    };
}

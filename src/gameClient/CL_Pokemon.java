package gameClient;
import api.EdgeData;
import api.edge_data;
import api.geo_location;
import api.node_data;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gameClient.util.Point3D;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CL_Pokemon {
    private edge_data _edge;
    private double _value;
    private int _type;
    private Point3D _pos;
    private double min_dist;
    private int min_ro;

    public CL_Pokemon(Point3D p, int t, double v, double s, edge_data e) {
        _type = t;
        //	_speed = s;
        _value = v;
        set_edge(e);
        _pos = p;
        min_dist = -1;
        min_ro = -1;
    }

    public CL_Pokemon(Point3D location1, int type,double value ,edge_data edge) {
        _type = type;
        _value = value;
        set_edge(edge);
        _pos = location1;
        min_dist = -1;
        min_ro = -1;
    }


    public static CL_Pokemon init_from_json(String json) {
        try {
            edge_data edge=new EdgeData();
            double value;
            int type;
            JsonObject pokeJson = new JsonParser().parse(json).getAsJsonObject();
            JsonObject pocemonJson = pokeJson.getAsJsonObject("Pokemon");

            String pos = pocemonJson.get("pos").getAsString();
            String[] posArr = pos.split(",");
            double x = Double.parseDouble(posArr[0]);
            double y = Double.parseDouble(posArr[1]);
            double z = Double.parseDouble(posArr[2]);
            geo_location location = new Point3D(x, y, z);
            Point3D location1=new Point3D(x,y,z);

            value = pocemonJson.get("value").getAsDouble();
            type = pocemonJson.get("type").getAsInt();
            CL_Pokemon pokemon=new CL_Pokemon(location1,type,value,edge);

            return pokemon;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }
    public String toString() {return "Pokemon:{v="+_value+", t="+_type+", pos="+_pos+" ,edge ="+_edge+"}";}
    public edge_data get_edge() {
        return _edge;
    }

    public void set_edge(edge_data _edge) {
        this._edge = _edge;
    }

    public Point3D getLocation() {
        return _pos;
    }
    public int getType() {return _type;}
    //	public double getSpeed() {return _speed;}
    public double getValue() {return _value;}

    public double getMin_dist() {
        return min_dist;
    }

    public void setMin_dist(double mid_dist) {
        this.min_dist = mid_dist;
    }

    public int getMin_ro() {
        return min_ro;
    }

    public void setMin_ro(int min_ro) {
        this.min_ro = min_ro;
    }
}
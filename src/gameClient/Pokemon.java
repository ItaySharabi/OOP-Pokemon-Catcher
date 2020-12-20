package gameClient;
import api.EdgeData;
import api.edge_data;
import api.geo_location;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gameClient.util.Point3D;

/**
 * This class contains all of the game pokemon's characteristics like
 * position, current edge, and so on..
 * We have personally added a boolean field 'isTracked'
 * which indicates if this pokemon is currently being tracked or not.
 * If not - lets other agent the opportunity to catch it.
 */
public class Pokemon {
    private edge_data _edge;
    private double _value;
    private int _type;
    private geo_location _pos;
    private double min_dist;
    private int min_ro;
    private boolean isTracked;

    /**
     * Default given Constructor
     * @param p - pokemon location.
     * @param t - pokemon type.
     * @param v - pokemon value.
     * @param s - pokemon speed (currently not used).
     * @param e - pokemon edge.
     */
    public Pokemon(geo_location p, int t, double v, double s, edge_data e) {
        _type = t;
        _value = v;
        set_edge(e);
        _pos = p;
        min_dist = -1;
        min_ro = -1;
        isTracked = false;
    }

    /**
     * Default given Constructor
     * @param location1 - pokemon location.
     * @param type - pokemon type.
     * @param value - pokemon value.
     * @param edge - pokemon edge.
     */
    public Pokemon(geo_location location1, int type, double value , edge_data edge) {
        _type = type;
        _value = value;
        set_edge(edge);
        _pos = location1;
        min_dist = -1;
        min_ro = -1;
        isTracked = false;
    }


    public static Pokemon init_from_json(String json) {
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

            value = pocemonJson.get("value").getAsDouble();
            type = pocemonJson.get("type").getAsInt();
            Pokemon pokemon=new Pokemon(location,type,value,edge);

            return pokemon;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }
    public String toString() {return "Pokemon:{v="+_value+", t="+_type+", pos="+_pos+" ,edge ="+_edge+" ,isTrack= "+ getIsTracked()+"}";}
    public edge_data get_edge() {
        return _edge;
    }

    public void set_edge(edge_data _edge) {
        this._edge = _edge;
    }

    public geo_location getLocation() {
        return _pos;
    }
    public int getType() {return _type;}
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

    /**
     * If an agent is currently going towards this pokemon,
     * then this method sets the field 'isTracked' with true.
     * Other agents will not be able to track this pokemon if so.
     * @param isTracked - is an agent tracking this pokemon?
     */
    public void setIsTracked(boolean isTracked) {
        this.isTracked=isTracked;
    }

    /**
     * Simple getter.
     */
    public boolean getIsTracked() {
        return this.isTracked;
    }
}
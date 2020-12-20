package api;

import gameClient.util.Point3D;

import java.util.Objects;

/**
 * This class represents a node on a graph.
 * A node has a unique key, a tag,
 * data (String) and location.
 */
public class NodeData implements node_data {

    private int key, tag;
    private double weight;
    private String info;
    private geo_location location;

    /**
     * Constructor to build a new node by key.
     * @param key
     */
    public NodeData(int key){
        this.key=key;
        weight=0;
        tag=0;
        info="";
        location = new Point3D(0, 0, 0);
    }

    /**
     * Copy constructor to build a new node for another node.
     * @param n
     */
    public NodeData(node_data n)
    {
        key = n.getKey();
        weight = n.getWeight();
        tag = n.getTag();
        info = n.getInfo();
        location = n.getLocation();
    }

    /**
     * Returns the key (id) associated with this node.
     *
     * @return - node's key.
     */
    @Override
    public int getKey() {
        return key;
    }

    /**
     * Returns the location of this node, if
     * none return null.
     *
     * @return - the current node location.
     */
    @Override
    public geo_location getLocation() {
        return location;
    }

    /**
     * Allows changing this node's location.
     *
     * @param p - new new location  (position) of this node.
     */
    @Override
    public void setLocation(geo_location p) {
        if (p != null) location = p;
    }

    /**
     * Returns the weight associated with this node.
     *
     * @return the weight from this node to another node on the graph.
     */
    @Override
    public double getWeight() {
        return weight;
    }

    /**
     * Allows changing this node's weight.
     *
     * @param w - the new weight
     */
    @Override
    public void setWeight(double w) {
        weight = w;
    }

    /**
     * Returns the remark (meta data) associated with this node.
     *
     * @return the information associated with this node.
     */
    @Override
    public String getInfo() {
        return info;
    }

    /**
     * Allows changing the remark (meta data) associated with this node.
     *
     * @param s - The new information to set.
     */
    @Override
    public void setInfo(String s) {
        if (s != null) info = s;
    }

    /**
     * Temporal data (aka color: e,g, white, gray, black)
     * which can be used be algorithms
     *
     * @return - the current tag of this node.
     */
    @Override
    public int getTag() {
        return tag;
    }

    /**
     * Allows setting the "tag" value for temporal marking an node - common
     * practice for marking by algorithms.
     *
     * @param t - the new value of the tag
     */
    @Override
    public void setTag(int t) {
        tag = t;
    }


    @Override
    public String toString() {
        return key+""+"["+weight+"]";
    }

    /**
     * Overriding equals method to compare nodes by keys and info only.
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeData nodeData = (NodeData) o;
        return key == nodeData.key &&
                Objects.equals(info, nodeData.info);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, info);
    }
}

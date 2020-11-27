package api;

public class NodeData implements node_data {

    private int key, tag;
    private double weight;
    private String info;
    private geo_location location;
    private static int keyGenerator = 0;

    public NodeData()
    {
        key = keyGenerator++;
        weight = 0;
        tag = 0;
        info = " ";
        location = null;
    }

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
     * @return
     */
    @Override
    public int getKey() {
        return key;
    }

    /**
     * Returns the location of this node, if
     * none return null.
     *
     * @return
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
     * @return
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
     * @return
     */
    @Override
    public String getInfo() {
        return info;
    }

    /**
     * Allows changing the remark (meta data) associated with this node.
     *
     * @param s
     */
    @Override
    public void setInfo(String s) {
        if (s != null) info = s;
    }

    /**
     * Temporal data (aka color: e,g, white, gray, black)
     * which can be used be algorithms
     *
     * @return
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
}

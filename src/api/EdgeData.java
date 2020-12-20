package api;

import java.util.Objects;

/**
 * This class represents an edge from one node to another on a given graph.
 * Edges has some fields like from where to where (src to dest), a tag,
 * information (String) and weight.
 * An edge is one sided on the graph data structure we're running,
 * meaning if there's an edge from src to dest, then not necessarily there's an edge
 * from dest to src.
 */
public class EdgeData implements edge_data{

    private int src, dest, tag;
    private String info;
    private double weight;

    /**
     * Empty constructor
     */
    public EdgeData() {
        src = 0;
        dest = 0;
        tag = 0;
        info = " ";
        weight = 0;
    }

    /**
     * Copy constructor
     * @param copy - other edge
     */
    public EdgeData(edge_data copy) {
        src = copy.getSrc();
        dest = copy.getDest();
        tag = copy.getTag();
        info = copy.getInfo();
        weight = copy.getWeight();
    }
    /**
     * Constructor.
     */
    public EdgeData(int src, int dest, double weight) {
        this.src = src;
        this.dest = dest;
        this.weight = weight;
        tag = 0;
        info = "Edge from " + src + " to " + dest;
    }
    /**
     * The id of the source node of this edge.
     *
     * @return - the souce node of this edge.
     */
    @Override
    public int getSrc() {
        return src;
    }

    /**
     * The id of the destination node of this edge
     *
     * @return - the dest node of this edge.
     */
    @Override
    public int getDest() {
        return dest;
    }

    /**
     * @return the weight of this edge (positive value).
     */
    @Override
    public double getWeight() {
        return weight;
    }

    /**
     * Returns the remark (meta data) associated with this edge.
     *
     * @return - the information associated with this edge.
     */
    @Override
    public String getInfo() {
        return info;
    }

    /**
     * Allows changing the remark (meta data) associated with this edge.
     *
     * @param s - the new information.
     */
    @Override
    public void setInfo(String s) {
        if (s != null) info = s;
    }

    /**
     * Temporal data (aka color: e,g, white, gray, black)
     * which can be used be algorithms
     *
     * @return - the current edge tag.
     */
    @Override
    public int getTag() {
        return tag;
    }

    /**
     * This method allows setting the "tag" value for temporal marking an edge - common
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
        return src+"-->"+dest+" ["+weight+"]";
    }

    /**
     * Override equals() method inorder to compare
     * edges by their src and dest nodes, weights and info.
     * @param o - compare this edge with edge 'o'
     * @return - true or false if equal.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EdgeData edgeData = (EdgeData) o;
        return src == edgeData.src &&
                dest == edgeData.dest &&
                Double.compare(edgeData.weight, weight) == 0 &&
                Objects.equals(info, edgeData.info);
    }

    @Override
    public int hashCode() {
        return Objects.hash(src, dest, info, weight);
    }
}

package api;

import api.node_data;

import java.util.Comparator;
/**
 * Compare 2 nodes by their weight.
 * (double)   x >= 0 iff o1 >= o2
 *                  x < 0 otherwise.
 */
public class NodeComparator implements Comparator<node_data> {

    /**
     * Compare 2 nodes by their weight.
     * @param o1
     * @param o2
     * @return (double) x >= 0 iff o1 >= o2
     *                  x < 0 otherwise.
     */
    @Override
    public int compare(node_data o1, node_data o2) {
        return Double.compare(o1.getWeight(), o2.getWeight());
    }
}
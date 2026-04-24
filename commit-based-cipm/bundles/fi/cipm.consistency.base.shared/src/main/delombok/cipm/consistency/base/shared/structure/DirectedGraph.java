package cipm.consistency.base.shared.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

/**
 * A simple graph data structure that represents a directed graph. It provides access to edges of
 * the nodes in O(1).
 * 
 * @author David Monschein
 *
 * @param <N>
 *            type of the nodes
 * @param <E>
 *            type of the edges
 */
public class DirectedGraph<N, E> {
    private Set<N> nodes;
    private Map<Pair<N, N>, E> edgeProjection;
    private Map<N, List<Pair<N, E>>> outgoingEdges;
    private Map<N, List<Pair<N, E>>> incomingEdges;

    /**
     * Creates a new empty graph.
     */
    public DirectedGraph() {
        this.nodes = new HashSet<>();
        this.edgeProjection = new HashMap<>();
        this.outgoingEdges = new HashMap<>();
        this.incomingEdges = new HashMap<>();
    }

    /**
     * Gets all nodes of the graph.
     * 
     * @return all nodes of the graph
     */
    public Set<N> getNodes() {
        return nodes;
    }

    /**
     * Gets all outgoing edges of a given node. All of them are cached so the access only needs
     * constant time.
     * 
     * @param node
     *            the node
     * @return the outgoing edges of the specified node
     */
    public List<Pair<N, E>> getOutgoingEdges(String node) {
        return outgoingEdges.get(node);
    }

    /**
     * Gets all ingoing edges of a given node. All of them are cached so the access only needs
     * constant time.
     * 
     * @param node
     *            the node
     * @return the ingoing edges of the specified node
     */
    public List<Pair<N, E>> getIncomingEdges(String node) {
        return incomingEdges.get(node);
    }

    /**
     * Gets the number of incoming edges of a node.
     * 
     * @param node
     *            the node
     * @return the number of incoming edges of the node
     */
    public int incomingEdges(N node) {
        return this.incomingEdges.containsKey(node) ? this.incomingEdges.get(node)
            .size() : 0;
    }

    /**
     * Gets the number of outgoing edges of a node.
     * 
     * @param node
     *            the node
     * @return the number of outgoing edges of the node
     */
    public int outgoingEdges(N node) {
        return this.outgoingEdges.containsKey(node) ? this.outgoingEdges.get(node)
            .size() : 0;
    }

    /**
     * Modifies the value of an edge.
     * 
     * @param from
     *            the start point of the edge
     * @param to
     *            the target of the edge
     * @param value
     *            the new value to set
     */
    public void modifyEdge(N from, N to, E value) {
        this.edgeProjection.put(Pair.of(from, to), value);

        if (!outgoingEdges.containsKey(from)) {
            outgoingEdges.put(from, new ArrayList<>());
        }
        outgoingEdges.get(from)
            .add(Pair.of(to, value));

        if (!incomingEdges.containsKey(to)) {
            incomingEdges.put(to, new ArrayList<>());
        }
        incomingEdges.get(to)
            .add(Pair.of(from, value));
    }

    /**
     * Adds an edge to the graph with a given value.
     * 
     * @param from
     *            source of the edge
     * @param to
     *            target of the edge
     * @param data
     *            value of the edge
     */
    public void addEdge(N from, N to, E data) {
        if (!nodes.contains(from)) {
            this.addNode(from);
        }
        if (!nodes.contains(to)) {
            this.addNode(to);
        }
        this.modifyEdge(from, to, data);
    }

    /**
     * Adds a node to the graph.
     * 
     * @param node
     *            the node content
     */
    public void addNode(N node) {
        this.nodes.add(node);
    }

    /**
     * Determines whether an edge between two nodes exists.
     * 
     * @param from
     *            source of the edge
     * @param to
     *            target of the edge
     * @return true if there is an edge with the given source and target, false otherwise
     */
    public boolean hasEdge(N from, N to) {
        return this.edgeProjection.containsKey(Pair.of(from, to));
    }

    /**
     * Gets the number of edges within the whole graph.
     * 
     * @return number of edges within the whole graph
     */
    public int edges() {
        return this.edgeProjection.size();
    }

    /**
     * Gets the edge with a given source and target.
     * 
     * @param n1
     *            source of the edge
     * @param n2
     *            target of the edge
     * @return the edge or null if it does not exist
     */
    public E getEdge(N n1, N n2) {
        return this.edgeProjection.get(Pair.of(n1, n2));
    }

    /**
     * Gets a list of all edges. The result is a list of pairs (source node, target node).
     * 
     * @return list of all edges as a list of (source, target) pairs
     */
    public List<Pair<N, N>> getEdges() {
        return this.edgeProjection.entrySet()
            .stream()
            .map(e -> Pair.of(e.getKey()
                .getLeft(),
                    e.getKey()
                        .getRight()))
            .collect(Collectors.toList());
    }

}

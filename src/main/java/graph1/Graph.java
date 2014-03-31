package graph1;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Graph<V, E extends Edge<V>> {

    private Set<V> vertices = new HashSet<V>();
    private Set<E> edges = new HashSet<E>();

    public void addVertex(V vertex) {
        vertices.add(vertex);
    }

    public Set<V> getVertices() {
        return Collections.unmodifiableSet(vertices);
    }

    public void addEdge(E edge) {
        if (!vertices.contains(edge.getV1()))
            throw new IllegalArgumentException("Vertex doesn't belong to graph1: " + edge.getV1());

        if (!vertices.contains(edge.getV2()))
            throw new IllegalArgumentException("Vertex doesn't belong to graph1: " + edge.getV2());

        edges.add(edge);
    }

    public Set<E> getEdges() {
        return Collections.unmodifiableSet(edges);
    }
}

package graph;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Graph<V, E extends Edge<V>> {

    private final Set<V> vertices = new HashSet<V>();
    private final Set<E> edges = new HashSet<E>();

    public void addVertex(V vertex) {
        vertices.add(vertex);
    }

    public Set<V> getVertices() {
        return Collections.unmodifiableSet(vertices);
    }

    public E findEdge(V vertex1, V vertex2) {
        if (vertex1.equals(vertex2))
            throw new IllegalArgumentException("vertex1 and vertex2 are the same");

        for (E edge : getAllEdgesForVertex(vertex1))
            if (edge.hasVertex(vertex2))
                return edge;

        return null;
    }

    public void addEdge(E edge) {
        if (!vertices.contains(edge.getVertex1()))
            throw new IllegalArgumentException("Vertex doesn't belong to graph: " + edge.getVertex1());
        if (!vertices.contains(edge.getVertex2()))
            throw new IllegalArgumentException("Vertex doesn't belong to graph: " + edge.getVertex2());

        edges.add(edge);
    }

    public Set<E> getEdges() {
        return Collections.unmodifiableSet(edges);
    }

    public Set<E> getAllEdgesForVertex(V vertex) {
        Set<E> edgesForVertex = new HashSet<E>();
        for (E edge : edges)
            if (edge.hasVertex(vertex))
                edgesForVertex.add(edge);

        return edgesForVertex;
    }

}

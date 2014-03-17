package graph;

public abstract class AbstractEdge<V> implements Edge<V>  {

    protected final V vertex1;
    protected final V vertex2;

    public AbstractEdge(V vertex1, V vertex2) {
        this.vertex1 = vertex1;
        this.vertex2 = vertex2;
    }

    public final V getVertex1() {
        return vertex1;
    }

    public final V getVertex2() {
        return vertex2;
    }

    @Override
    public final boolean hasVertex(V vertex) {
        return vertex1.equals(vertex) || vertex2.equals(vertex);
    }

    @Override
    public String toString() {
        return "Edge{" + vertex1 + "," + vertex2 + "}";
    }
}

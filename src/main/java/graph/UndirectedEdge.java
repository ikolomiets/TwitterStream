package graph;

public class UndirectedEdge<V> extends AbstractEdge<V>  {

    public UndirectedEdge(V vertex1, V vertex2) {
        super(vertex1, vertex2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UndirectedEdge edge = (UndirectedEdge) o;

        return vertex1.equals(edge.vertex1) ? vertex2.equals(edge.vertex2) : vertex1.equals(edge.vertex2) && vertex2.equals(edge.vertex1);
    }

    @Override
    public int hashCode() {
        return vertex1.hashCode() ^ vertex2.hashCode();
    }

}

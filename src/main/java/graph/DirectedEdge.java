package graph;

public class DirectedEdge<V> extends AbstractEdge<V> {

    public DirectedEdge(V vertex1, V vertex2) {
        super(vertex1, vertex2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DirectedEdge edge = (DirectedEdge) o;

        if (!vertex1.equals(edge.vertex1)) return false;
        //noinspection RedundantIfStatement
        if (!vertex2.equals(edge.vertex2)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = vertex1.hashCode();
        result = 31 * result + vertex2.hashCode();
        return result;
    }

}

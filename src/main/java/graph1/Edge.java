package graph1;

public abstract class Edge<V> {

    protected final V v1;
    protected final V v2;

    public Edge(V v1, V v2) {
        if (v1 == null)
            throw new IllegalArgumentException("v1 == null");

        if (v2 == null)
            throw new IllegalArgumentException("v2 == null");

        if (v1.equals(v2))
            throw new IllegalArgumentException("v1 == v2");

        this.v1 = v1;
        this.v2 = v2;
    }

    public final V getV1() {
        return v1;
    }

    public final V getV2() {
        return v2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Edge edge = (Edge) o;

        if (v1.equals(edge.v1)) {
            return v2.equals(edge.v2);
        } else {
            return v1.equals(edge.v2) && v2.equals(edge.v1);
        }
    }

    @Override
    public int hashCode() {
        int result = v1.hashCode();
        result = 31 * result + v2.hashCode();
        return result;
    }
}

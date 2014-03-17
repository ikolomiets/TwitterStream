package graph;

public interface Edge<V> {

    V getVertex1();

    V getVertex2();

    boolean hasVertex(V vertex);

}

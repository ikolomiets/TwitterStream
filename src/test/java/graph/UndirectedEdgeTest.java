package graph;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(JUnit4.class)
public class UndirectedEdgeTest {

    private Edge<String> edge1;
    private Edge<String> edge2;
    private Edge<String> edge3;
    private Edge<String> edge4;

    @Before
    public void setUp() throws Exception {
        edge1 = new UndirectedEdge<String>("gosha", "alena");
        edge2 = new UndirectedEdge<String>("alena", "gosha");
        edge3 = new UndirectedEdge<String>("gosha", "alena");
        edge4 = new UndirectedEdge<String>("gosha", "igor");
    }

    @Test
    public void testEquals() throws Exception {
        assertEquals(edge1, edge2);
        assertEquals(edge1, edge3);
        assertFalse(edge1.equals(edge4));
    }

    @Test
    public void testHashCode() throws Exception {
        assertEquals(edge1.hashCode(), edge2.hashCode());
    }

}

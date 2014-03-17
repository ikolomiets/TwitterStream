package graph;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(JUnit4.class)
public class DirectedEdgeTest {

    private Edge<String> edge1;
    private Edge<String> edge2;
    private Edge<String> edge3;

    @Before
    public void setUp() throws Exception {
        edge1 = new DirectedEdge<String>("gosha", "alena");
        edge2 = new DirectedEdge<String>("gosha", "alena");
        edge3 = new DirectedEdge<String>("alena", "gosha");
    }

    @Test
    public void testEquals() throws Exception {
        assertEquals(edge1, edge2);
        assertFalse(edge1.equals(edge3));
    }

    @Test
    public void testHashCode() throws Exception {
        assertEquals(edge1.hashCode(), edge2.hashCode());
    }
}

import graph.UndirectedEdge;

public class MentionEdge extends UndirectedEdge<String> {

    private long counter = 1;

    public MentionEdge(String user1, String user2) {
        super(user1, user2);
    }

    public long getCounter() {
        return counter;
    }

    public void increment() {
        counter++;
    }

}

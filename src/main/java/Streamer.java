import twitter4j.Status;
import twitter4j.StatusAdapter;
import twitter4j.TwitterStreamFactory;

public class Streamer {

    public static void main(String[] args) {

        twitter4j.TwitterStream twitter = new TwitterStreamFactory().getInstance();
        twitter.addListener(new StatusAdapter() {
            public void onStatus(Status status) {
                System.out.println(status.getUser().getName() + " : " + status.getText());
            }
        });
        twitter.sample();


    }

}

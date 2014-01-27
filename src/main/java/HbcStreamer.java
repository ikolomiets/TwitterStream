import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import twitter4j.*;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationContext;
import twitter4j.json.DataObjectFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class HbcStreamer {

    private static DateFormat dateFormat = new SimpleDateFormat("HH:mm");

    public static void main(String[] args) throws InterruptedException, TwitterException {
        Twitter twitter = TwitterFactory.getSingleton();

        List<Long> userIds = new ArrayList<Long>();
        for (String arg : args) {
            if (!arg.startsWith("@"))
                continue;

            String screenName = arg.substring(1);
            User user = twitter.users().showUser(screenName);
            System.out.println("Resolve " + arg + " to " + user.getId());
            userIds.add(user.getId());
        }

        BlockingQueue<String> queue = new LinkedBlockingQueue<String>(1000);

        StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();
        endpoint.trackTerms(Lists.newArrayList(args));
        endpoint.followings(userIds);
        endpoint.stallWarnings(true);

        Client client = new ClientBuilder()
                .hosts(Constants.STREAM_HOST)
                .endpoint(endpoint)
                .authentication(getAuthentication())
                .processor(new StringDelimitedProcessor(queue))
                .build();

        client.connect();

        //noinspection InfiniteLoopStatement
        while (true) {
            String msg = queue.take();
            Status status;
            try {
                status = DataObjectFactory.createStatus(msg);
            } catch (TwitterException e) {
                System.out.println("Can't parse status: " + msg);
                e.printStackTrace();
                continue;
            }

            String createdAt = dateFormat.format(status.getCreatedAt());
            System.out.println(createdAt + " @" + status.getUser().getScreenName() + ": " + status.getText());
        }
    }

    private static Authentication getAuthentication() {
        Configuration configuration = ConfigurationContext.getInstance();

        String accessToken = configuration.getOAuthAccessToken();
        String accessTokenSecret = configuration.getOAuthAccessTokenSecret();
        String consumerKey = configuration.getOAuthConsumerKey();
        String consumerSecret = configuration.getOAuthConsumerSecret();

        return new OAuth1(consumerKey, consumerSecret, accessToken, accessTokenSecret);
    }

}

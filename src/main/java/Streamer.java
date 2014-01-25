import twitter4j.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Streamer {

    private static DateFormat dateFormat = new SimpleDateFormat("HH:mm");

    public static void main(String[] args) throws TwitterException {

        Twitter twitter = TwitterFactory.getSingleton();


        List<Long> userIdList = new ArrayList<Long>();
        for (String arg : args) {
            if (!arg.startsWith("@"))
                continue;

            String screenName = arg.substring(1);
            User user = twitter.users().showUser(screenName);
            System.out.println("Resolve " + arg + " to " + user.getId());
            userIdList.add(user.getId());
        }

        long[] userIds = new long[userIdList.size()];
        for (int i = 0; i < userIdList.size(); i++)
            userIds[i] = userIdList.get(i);

        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
        twitterStream.addListener(new StatusAdapter() {
            public void onStatus(Status status) {
                String createdAt = dateFormat.format(status.getCreatedAt());
                System.out.println(createdAt + " " + status.getUser().getScreenName() + ": " + status.getText());
            }
        });

        FilterQuery filterQuery = new FilterQuery();
        filterQuery.track(args);
        filterQuery.follow(userIds);

        twitterStream.filter(filterQuery);
    }

}

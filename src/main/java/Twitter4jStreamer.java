import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import rx.util.functions.Action0;
import rx.util.functions.Action1;
import rx.util.functions.Func1;
import twitter4j.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Twitter4jStreamer {

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

        final FilterQuery filterQuery = new FilterQuery();
        filterQuery.track(args);
        filterQuery.follow(userIds);

        final TwitterStream twitterStream = new TwitterStreamFactory().getInstance();

        Observable<Status> statusObservable = Observable.create(new Observable.OnSubscribeFunc<Status>() {
            @Override
            public Subscription onSubscribe(final Observer<? super Status> observer) {
                final AtomicBoolean isSubscribed = new AtomicBoolean(true);

                twitterStream.addListener(new StatusAdapter() {
                    public void onStatus(Status status) {
                        if (isSubscribed.get())
                            observer.onNext(status);
                    }

                    public void onException(Exception ex) {
                        observer.onError(ex);
                    }
                });

                twitterStream.filter(filterQuery);

                return Subscriptions.create(new Action0() {
                    @Override
                    public void call() {
                        isSubscribed.set(false);
                    }
                });
            }
        });

        statusObservable.map(new Func1<Status, Status>() {
            @Override
            public Status call(Status status) {
                printStatus(status);
                return status;
            }
        }).buffer(1, TimeUnit.MINUTES).subscribe(new Action1<List<Status>>() {
            @Override
            public void call(List<Status> statuses) {
                System.out.println("\t\t" + statuses.size() + " TPM");
            }
        });

    }

    private static void printStatus(Status status) {
        String createdAt = dateFormat.format(status.getCreatedAt());
        String text = createdAt + " @" + status.getUser().getScreenName() + " ";
        if (status.isRetweet()) {
            Status retweetedStatus = status.getRetweetedStatus();
            text += "RT @" + retweetedStatus.getUser().getScreenName() + ": " + retweetedStatus.getText();
        } else if (status.getInReplyToScreenName() != null) {
            text += "RPL @" + status.getInReplyToScreenName() + ": " + status.getText();
        } else {
            text += ": " + status.getText();
        }

        System.out.println(text);
    }

}

import rx.Observable;
import rx.subscriptions.BooleanSubscription;
import twitter4j.*;

import java.util.concurrent.TimeUnit;

public class SampleStreamMeeter {

    public static void main(String[] args) throws TwitterException {
        final TwitterStream twitterStream = new TwitterStreamFactory().getInstance();

        Observable<Status> observableStream = Observable.create((Observable.OnSubscribe<Status>) subscriber -> {
            StatusListener listener = new StatusAdapter() {
                public void onStatus(Status status) {
                    if (subscriber.isUnsubscribed())
                        return;

                    subscriber.onNext(status);
                }

                @Override
                public void onException(Exception ex) {
                    if (subscriber.isUnsubscribed())
                        return;

                    subscriber.onError(ex);
                }
            };

            subscriber.add(BooleanSubscription.create(() -> twitterStream.removeListener(listener)));
            twitterStream.addListener(listener);

            twitterStream.sample();
        });

        observableStream.buffer(1, TimeUnit.MINUTES).subscribe(statuses -> System.out.println("\t\t" + statuses.size() + " TPM"));
    }

}

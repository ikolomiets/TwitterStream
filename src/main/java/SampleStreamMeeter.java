import rx.Observable;
import rx.Subscription;
import twitter4j.*;

import java.util.concurrent.TimeUnit;

public class SampleStreamMeeter {

    public static void main(String[] args) throws TwitterException {
        final TwitterStream twitterStream = new TwitterStreamFactory().getInstance();

        Observable<Status> statusObservable = Observable.create(observer -> {
            final StatusAdapter listener = new StatusAdapter() {
                public void onStatus(Status status) {
                    observer.onNext(status);
                }
            };
            twitterStream.addListener(listener);

            twitterStream.sample();

            return new Subscription() {
                boolean isUnsubscribed = false;

                @Override
                public void unsubscribe() {
                    twitterStream.removeListener(listener);
                    isUnsubscribed = false;
                }
            };
        });

        statusObservable.buffer(1, TimeUnit.MINUTES).subscribe(statuses -> System.out.println("\t\t" + statuses.size() + " TPM"));
    }

}

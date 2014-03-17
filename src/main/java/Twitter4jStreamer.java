import graph.Graph;
import org.slf4j.Logger;
import org.slf4j.impl.SimpleLoggerFactory;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.util.functions.Action1;
import twitter4j.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

public class Twitter4jStreamer {

    private static Logger logger = new SimpleLoggerFactory().getLogger(Twitter4jStreamer.class.getName());
    private static DateFormat dateFormat = new SimpleDateFormat("HH:mm");

    private static Graph<String, MentionEdge> mentionGraph = new Graph<String, MentionEdge>();

    public static void main(String[] args) throws TwitterException {
        Twitter twitter = TwitterFactory.getSingleton();

        List<Long> userIdList = new ArrayList<Long>();
        for (String arg : args) {
            if (!arg.startsWith("@"))
                continue;

            String screenName = arg.substring(1);
            User user = twitter.users().showUser(screenName);
            logger.info("Resolve " + arg + " to " + user.getId());
            userIdList.add(user.getId());
        }

        long[] userIds = new long[userIdList.size()];
        for (int i = 0; i < userIdList.size(); i++)
            userIds[i] = userIdList.get(i);

        final FilterQuery filterQuery = new FilterQuery();
        filterQuery.track(args);
        filterQuery.follow(userIds);

        final List<Observer<? super Status>> observers = new CopyOnWriteArrayList<Observer<? super Status>>();
        final TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
        twitterStream.addListener(new StatusAdapter() {
            public void onStatus(Status status) {
                for (Observer<? super Status> observer : observers) {
                    observer.onNext(status);
                }
            }

            public void onException(Exception ex) {
                logger.error("onException", ex);
                logger.info("Trying to recover...");
                twitterStream.filter(filterQuery);
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                logger.warn("onDeletionNotice: " + statusDeletionNotice.toString());
            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                logger.warn("onTrackLimitationNotice: numberOfLimitedStatuses=" + numberOfLimitedStatuses);
            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
                logger.warn("onScrubGeo: userId=" + userId + ", upToStatusId=" + upToStatusId);
            }

            @Override
            public void onStallWarning(StallWarning warning) {
                logger.warn("onStallWarning: " + warning.toString());
            }
        });
        twitterStream.filter(filterQuery);

        Observable<Status> statusObservable = Observable.create(new Observable.OnSubscribeFunc<Status>() {
            @Override
            public Subscription onSubscribe(final Observer<? super Status> observer) {
                observers.add(observer);
                return new Subscription() {
                    @Override
                    public void unsubscribe() {
                        observers.remove(observer);
                    }
                };
            }
        });

        statusObservable.subscribe(new Action1<Status>() {
            @Override
            public void call(Status status) {
                printStatus(status);
            }
        });

        statusObservable.buffer(1, TimeUnit.MINUTES).subscribe(new Action1<List<Status>>() {
            @Override
            public void call(List<Status> statuses) {
                System.out.println("\t\t" + statuses.size() + " TPM");
            }
        });
    }

    private static void printStatus(Status status) {
        Set<String> participants = new HashSet<String>();
        participants.add(status.getUser().getScreenName());
        for (UserMentionEntity userMentionEntity : status.getUserMentionEntities())
            participants.add(userMentionEntity.getScreenName());

        String createdAt = dateFormat.format(status.getCreatedAt());
        String text = createdAt + " @" + status.getUser().getScreenName();
        if (status.isRetweet()) {
            Status retweetedStatus = status.getRetweetedStatus();
            text += " RTW @" + retweetedStatus.getUser().getScreenName() + ": " + retweetedStatus.getText();
        } else if (status.getInReplyToScreenName() != null) {
            text += " RPL @" + status.getInReplyToScreenName() + ": " + status.getText();
        } else {
            text += " TWT: " + status.getText();
        }

        text += " :::: " + participants;

        Iterator<String> startIterator = participants.iterator();
        while (startIterator.hasNext()) {
            String start = startIterator.next();
            startIterator.remove();
            for (String end : participants) {
                MentionEdge edge = mentionGraph.findEdge(start, end);
                if (edge != null) {
                    edge.increment();
                } else {
                    mentionGraph.addVertex(start);
                    mentionGraph.addVertex(end);
                    mentionGraph.addEdge(new MentionEdge(start, end));
                }
            }
        }

        System.out.println(text);

        dumpGraph();
    }

    private static void dumpGraph() {
        logger.info("============ Dump graph ==========");
        logger.info("Vertices: " + mentionGraph.getVertices().size());
        logger.info("Edges: " + mentionGraph.getEdges().size());

        final Map<String, Integer> userMentions = new HashMap<String, Integer>();
        for (String user : mentionGraph.getVertices()) {
            int total = 0;
            for (MentionEdge edge : mentionGraph.getAllEdgesForVertex(user))
                total += edge.getCounter();

            userMentions.put(user, total);
        }

        Map<String, Integer> sortedUserMentions = new TreeMap<String, Integer>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                int i = userMentions.get(o2) - userMentions.get(o1);
                return i != 0 ? i : o1.compareTo(o2);
            }
        });
        sortedUserMentions.putAll(userMentions);

        for (String user : sortedUserMentions.keySet())
            logger.info(user + " mentioned " + sortedUserMentions.get(user) + " times");
    }

}

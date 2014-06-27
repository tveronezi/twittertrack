package twittertrack.tests;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import twittertrack.service.MapBuilder;
import twittertrack.service.bean.ApplicationData;
import twittertrack.service.bean.TwitterConnection;
import twittertrack.service.bean.TwitterImpl;
import twittertrack.service.data.Tweet;
import twittertrack.service.data.Tweets;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;
import java.text.ParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class TestTwitterRequest {

    private static final String JNDI = "java:global/twittertrack/TwitterConnectionImpl";

    private String loadContent(String path) {
        return new Scanner(TestTwitterRequest.class.getResourceAsStream(path), "UTF-8").useDelimiter("\\A").next();
    }

    @Test
    public void testRequest() throws NamingException {
        Context context = EJBContainer.createEJBContainer().getContext();
        TwitterConnection twitter = (TwitterConnection) context.lookup(JNDI);
        String expected = loadContent("/1_tweet.json");
        String result = twitter.executeGet(TwitterConnection.USER_TIMELINE_URL, new MapBuilder()
                        .put("screen_name", "tveronezi")
                        .put("count", "1")
                        .getMap()
        );
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testDataParsing() throws ParseException, ExecutionException, InterruptedException {
        TwitterConnection connection = EasyMock.createNiceMock(TwitterConnection.class);
        connection.executeGet(TwitterConnection.USER_TIMELINE_URL, new MapBuilder()
                        .put("screen_name", "tveronezi")
                        .put("count", TwitterImpl.TWEETS)
                        .getMap()
        );
        EasyMock.expectLastCall().andReturn(loadContent("/30_tweets.json"));
        EasyMock.replay(connection);
        TwitterImpl twitter = new TwitterImpl();
        ApplicationData data = new ApplicationData();
        data.setTweets(new Tweets());
        data.getTweets().getUserTweets().put("tveronezi", new HashSet<Tweet>());
        ReflectionUtil.setProperty("connection", connection, twitter);
        ReflectionUtil.setProperty("applicationData", data, twitter);
        List<Tweet> tweets = twitter.getTweets("tveronezi").get();
        Assert.assertEquals(30, tweets.size());
        Assert.assertEquals(
                Long.valueOf(TwitterImpl.SIMPLE_DATE_FORMAT.parse("Tue Jun 03 08:50:50 +0000 2014").getTime()),
                tweets.get(0).getCreatedAt());
    }


}

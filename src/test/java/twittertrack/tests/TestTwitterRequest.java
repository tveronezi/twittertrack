/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package twittertrack.tests;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import twittertrack.MapBuilder;
import twittertrack.bean.data.ApplicationData;
import twittertrack.bean.data.TweetsData;
import twittertrack.bean.service.TwitterConnection;
import twittertrack.bean.service.TwitterImpl;
import twittertrack.data.Tweet;
import twittertrack.data.Tweets;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;
import java.text.ParseException;
import java.util.List;
import java.util.Scanner;
import java.util.TreeSet;
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
        TwitterImpl impl = new TwitterImpl();
        List<Tweet> expected = impl.buildTweetsList(loadContent("/1_tweet.json"));
        List<Tweet> result = impl.buildTweetsList(twitter.executeGet(TwitterConnection.USER_TIMELINE_URL, new MapBuilder()
                        .put("screen_name", "tveronezi")
                        .put("count", "1")
                        .getMap()
        ));
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testDataParsing() throws ParseException, ExecutionException, InterruptedException {
        TwitterConnection connection = EasyMock.createNiceMock(TwitterConnection.class);
        final String count = "100";
        connection.executeGet(TwitterConnection.USER_TIMELINE_URL, new MapBuilder()
                        .put("screen_name", "tveronezi")
                        .put("count", count)
                        .getMap()
        );
        EasyMock.expectLastCall().andReturn(loadContent("/30_tweets.json"));
        EasyMock.replay(connection);
        TwitterImpl twitter = new TwitterImpl();
        TweetsData data = new TweetsData();
        ApplicationData appData = new ApplicationData();
        appData.setTwitterProperty(ApplicationData.TWITTER_MAX_TWEETS, count);
        data.setTweets(new Tweets());
        data.getTweets().getUserTweets().put("tveronezi", new TreeSet<Tweet>());
        ReflectionUtil.setProperty("connection", connection, twitter);
        ReflectionUtil.setProperty("tweetsData", data, twitter);
        ReflectionUtil.setProperty("applicationData", appData, twitter);
        List<Tweet> tweets = twitter.getTweets("tveronezi").get();
        Assert.assertEquals(30, tweets.size());
        Assert.assertEquals(
                Long.valueOf(TwitterImpl.SIMPLE_DATE_FORMAT.parse("Tue Jun 03 08:50:50 +0000 2014").getTime()),
                tweets.get(0).getCreatedAt());
    }


}

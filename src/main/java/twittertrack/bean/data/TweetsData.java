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

package twittertrack.bean.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twittertrack.data.Tweet;
import twittertrack.data.Tweets;

import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

@Singleton
public class TweetsData {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @EJB
    private ApplicationData applicationData;

    private Tweets tweets = new Tweets();

    @Lock(LockType.READ)
    public SortedSet<Tweet> getTweets(String user) {
        final NavigableSet<Tweet> result = new TreeSet<>(tweets.getUserTweets().get(user));
        return Collections.unmodifiableSortedSet(result);
    }

    @Lock(LockType.READ)
    public Tweets getTweets() {
        return tweets;
    }

    @Lock(LockType.WRITE)
    public void setTweets(Tweets tweets) {
        this.tweets = tweets;
    }

    @Lock(LockType.READ)
    public Set<String> getUsers() {
        return Collections.unmodifiableSet(tweets.getUserTweets().keySet());
    }

    @Lock(LockType.WRITE)
    public void addUser(String user) {
        final Map<String, SortedSet<Tweet>> map = tweets.getUserTweets();
        if (!map.containsKey(user)) {
            map.put(user, new TreeSet<Tweet>());
        }
    }

    @Lock(LockType.WRITE)
    public void updateTweets(List<Tweet> userTweets) {
        final Map<String, SortedSet<Tweet>> map = tweets.getUserTweets();
        final String strMaxTweets = applicationData.getTwitterProperty(ApplicationData.TWITTER_MAX_TWEETS);
        final int maxTweets = Integer.parseInt(strMaxTweets);
        for (Tweet tweet : userTweets) {
            final String userName = tweet.getUser().getName();
            addUser(userName); // making sure we map this user
            final SortedSet<Tweet> myTweets = map.get(userName);
            myTweets.add(tweet);
            while (myTweets.size() > maxTweets) {
                final Tweet last = myTweets.last();
                log.debug("Removing old tweet. https://twitter.com/{}/status/{}",
                        last.getUser().getName(),
                        last.getIdentifier()
                );
                myTweets.remove(last);
            }
        }
    }

    @Lock(LockType.READ)
    public Set<Tweet> getLoadedTweets() {
        final Set<Tweet> result = new TreeSet<>();
        final Map<String, SortedSet<Tweet>> map = tweets.getUserTweets();
        for (String user : map.keySet()) {
            result.addAll(map.get(user));
        }
        return result;
    }
}

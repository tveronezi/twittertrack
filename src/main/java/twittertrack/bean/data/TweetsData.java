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

import twittertrack.data.Tweet;
import twittertrack.data.Tweets;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@Singleton
public class TweetsData {

    private Tweets tweets;

    @Lock(LockType.READ)
    public Set<Tweet> getTweets(String user) {
        final Set<Tweet> result = new TreeSet<>(tweets.getUserTweets().get(user));
        return Collections.unmodifiableSet(result);
    }

    @Lock(LockType.READ)
    public Tweets getTweets() {
        return this.tweets;
    }

    @Lock(LockType.WRITE)
    public void setTweets(Tweets tweets) {
        this.tweets = tweets;
    }

    @Lock(LockType.READ)
    public Set<String> getUsers() {
        return Collections.unmodifiableSet(this.tweets.getUserTweets().keySet());
    }

    @Lock(LockType.WRITE)
    public void addUser(String user) {
        final Map<String, Set<Tweet>> map = tweets.getUserTweets();
        if (!map.containsKey(user)) {
            map.put(user, new TreeSet<Tweet>());
        }
    }

    @Lock(LockType.WRITE)
    public void updateTweets(List<Tweet> userTweets) {
        final Map<String, Set<Tweet>> map = tweets.getUserTweets();
        for (Tweet tweet : userTweets) {
            final String userName = tweet.getUser().getName();
            addUser(userName); // making sure we map this user
            final Set<Tweet> myTweets = map.get(userName);
            myTweets.add(tweet);
        }
    }

    @Lock(LockType.READ)
    public Set<Tweet> getLoadedTweets() {
        final Set<Tweet> result = new TreeSet<>();
        final Map<String, Set<Tweet>> map = tweets.getUserTweets();
        for (String user : map.keySet()) {
            result.addAll(map.get(user));
        }
        return result;
    }
}

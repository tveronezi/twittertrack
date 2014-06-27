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

package twittertrack.service.bean;

import twittertrack.service.data.Tweet;
import twittertrack.service.data.Tweets;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

@Singleton
public class ApplicationData {

    public static final File DEFAULT_TWITTER_PROPS = new File(System.getProperty("user.home"), "twitter.properties");
    public static final String TWITTER_PROPS = System.getProperty(
            "twitter_properties", DEFAULT_TWITTER_PROPS.getPath()
    );

    public static final String TWITTER_API_KEY = "api_key";
    public static final String TWITTER_API_SECRET = "api_secret";
    public static final String TWITTER_API_BEARER_TOKEN = "api_bearer_token";
    public static final String TWITTER_USERS = "twitter_users";

    private Properties twitterProperties = new Properties();
    private Tweets tweets;

    @Lock(LockType.READ)
    public String getTwitterProperty(String key) {
        return this.twitterProperties.getProperty(key);
    }

    @Lock(LockType.WRITE)
    public void setTwitterProperties(Properties properties) {
        this.twitterProperties.putAll(properties);
    }

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
    public void updateTweets(List<Tweet> userTweets) {
        final Map<String, Set<Tweet>> map = tweets.getUserTweets();
        for (Tweet tweet : userTweets) {
            final Set<Tweet> myTweets = map.get(tweet.getUser());
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

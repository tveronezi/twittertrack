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

package twittertrack.bean.service;

import org.json.JSONArray;
import twittertrack.ApplicationException;
import twittertrack.JsonUtil;
import twittertrack.MapBuilder;
import twittertrack.bean.data.ApplicationData;
import twittertrack.bean.data.TweetsData;
import twittertrack.data.Tweet;
import twittertrack.data.TweetUser;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.Future;

@Stateless
public class TwitterImpl {

    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy");

    @EJB
    private TwitterConnection connection;

    @EJB
    private TweetsData tweetsData;

    @EJB
    private ApplicationData applicationData;

    @Asynchronous
    public Future<List<Tweet>> getTweets(String user) {
        final MapBuilder params = new MapBuilder()
                .put("screen_name", user)
                .put("count", applicationData.getTwitterProperty(ApplicationData.TWITTER_MAX_TWEETS));
        final SortedSet<Tweet> tweetSet = tweetsData.getTweets(user);
        if (!tweetSet.isEmpty()) {
            params.put("since_id", tweetSet.first().getIdentifier());
        }
        final String json = connection.executeGet(TwitterConnection.USER_TIMELINE_URL, params.getMap());
        return new AsyncResult<>(buildTweetsList(json));
    }

    @SuppressWarnings("unchecked")
    public List<Tweet> buildTweetsList(String jsonText) {
        final List<Object> parsed = JsonUtil.getList(new JSONArray(jsonText));
        final List<Tweet> result = new ArrayList<>();
        for (Object jsonObj : parsed) {
            result.add(buildTweetObject((Map<String, Object>) jsonObj));
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private Tweet buildTweetObject(Map<String, Object> json) {
        final Map<String, Object> user = (Map<String, Object>) json.get("user");
        try {
            final String id = (String) json.get("id_str");
            final Tweet tweet = new Tweet();
            //The json provider converts it to number. We force it to be a string.
            tweet.setIdentifier("_" + id);
            tweet.setUser(buildUser(user));
            tweet.setAuthor(buildOriginalUser(json));
            tweet.setMentions(buildMentions(json));
            tweet.setContent(json.get("text").toString());
            tweet.setCreatedAt(SIMPLE_DATE_FORMAT.parse(json.get("created_at").toString()).getTime());
            return tweet;
        } catch (ParseException e) {
            throw new ApplicationException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private Set<TweetUser> buildMentions(Map<String, Object> json) {
        if (!json.containsKey("entities")) {
            return null;
        }
        final Map<String, Object> entities = (Map<String, Object>) json.get("entities");
        if (!entities.containsKey("user_mentions")) {
            return null;
        }
        final List<Map<String, Object>> mentions = (List<Map<String, Object>>) entities.get("user_mentions");
        final Set<TweetUser> users = new HashSet<>();
        for (Map<String, Object> user : mentions) {
            users.add(buildUser(user));
        }
        return users;
    }

    @SuppressWarnings("unchecked")
    private TweetUser buildOriginalUser(Map<String, Object> json) {
        if (!json.containsKey("retweeted_status")) {
            return null;
        }
        final Map<String, Object> status = (Map<String, Object>) json.get("retweeted_status");
        final Map<String, Object> user = (Map<String, Object>) status.get("user");
        return buildUser(user);
    }

    private TweetUser buildUser(Map<String, Object> json) {
        return buildUser((String) json.get("id_str"), (String) json.get("screen_name"));
    }

    private TweetUser buildUser(String id, String name) {
        final TweetUser user = new TweetUser();
        //The json provider converts it to number. We force it to be a string.
        user.setIdentifier("_" + id);
        user.setName(name);
        return user;
    }

}

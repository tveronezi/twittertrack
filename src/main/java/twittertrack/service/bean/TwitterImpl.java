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

import org.json.JSONArray;
import twittertrack.service.ApplicationException;
import twittertrack.service.JsonUtil;
import twittertrack.service.MapBuilder;
import twittertrack.service.data.Tweet;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

@Stateless
public class TwitterImpl {

    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy");
    public static final String TWEETS = "30";
    private static final String LINK_PATTERN = "https://twitter.com/{0}/status/{1}";

    @EJB
    private TwitterConnection connection;

    @EJB
    private ApplicationData applicationData;

    @SuppressWarnings("unchecked")
    @Asynchronous
    public Future<List<Tweet>> getTweets(String user) {
        final MapBuilder params = new MapBuilder()
                .put("screen_name", user)
                .put("count", TWEETS);
        final Set<Tweet> tweetSet = this.applicationData.getTweets(user);
        if (!tweetSet.isEmpty()) {
            params.put("since_id", tweetSet.iterator().next().getId());
        }
        final String json = this.connection.executeGet(TwitterConnection.USER_TIMELINE_URL, params.getMap());
        final List<Object> parsed = JsonUtil.getList(new JSONArray(json));
        final List<Tweet> result = new ArrayList<>();
        for (Object jsonObj : parsed) {
            result.add(buildTweetObject((Map<String, Object>) jsonObj, user));
        }
        return new AsyncResult<>(result);
    }

    private Tweet buildTweetObject(Map<String, Object> json, String user) {
        try {
            final String id = json.get("id").toString();
            final Tweet tweet = new Tweet();
            tweet.setId(id);
            tweet.setUser(user);
            tweet.setContent(json.get("text").toString());
            tweet.setCreatedAt(SIMPLE_DATE_FORMAT.parse(json.get("created_at").toString()).getTime());
            tweet.setLink(MessageFormat.format(LINK_PATTERN, user, id));
            return tweet;
        } catch (ParseException e) {
            throw new ApplicationException(e);
        }
    }

}

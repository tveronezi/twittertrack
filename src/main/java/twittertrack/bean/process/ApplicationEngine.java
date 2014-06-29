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

package twittertrack.bean.process;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twittertrack.ApplicationException;
import twittertrack.bean.data.ApplicationData;
import twittertrack.bean.data.TweetsData;
import twittertrack.data.Tweet;
import twittertrack.data.Tweets;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;

@Singleton
@DependsOn({"ApplicationData", "TweetsData", "TwitterRobot"})
@Startup
public class ApplicationEngine {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @EJB
    private ApplicationData applicationData;

    @EJB
    private TweetsData tweetsData;

    @EJB
    private TwitterRobot twitterRobot;

    @PostConstruct
    public void startup() {
        log.info("Starting twittertrack...");
        loadTwitterProperties();
        if (!loadPreloadedTweets()) {
            final Tweets tweets = new Tweets();
            final Map<String, SortedSet<Tweet>> userTweets = new HashMap<>();
            final String users = applicationData.getTwitterProperty(ApplicationData.TWITTER_USERS);
            if (users != null && !"".equals(users.trim())) {
                for (String raw : users.trim().split(",")) {
                    final String user = raw.trim();
                    userTweets.put(user, new TreeSet<Tweet>());
                }
            }
            tweets.setUserTweets(userTweets);
            tweetsData.setTweets(tweets);
        }
        twitterRobot.fire();
        log.info("twittertrack is up and running.");
    }

    @PreDestroy
    public void shutdown() {
        try (final ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(getTweetsFile()))) {
            out.writeObject(tweetsData.getTweets());
        } catch (IOException e) {
            log.warn("Impossible to save preloaded tweets.", e);
        }
    }

    private File getTweetsFile() {
        final File tempDir = new File(System.getProperty("java.io.tmpdir"), "twittertract");
        return new File(tempDir, "tweets.bean");
    }

    private boolean loadPreloadedTweets() {
        final File tweetsFile = getTweetsFile();
        final File tempDir = tweetsFile.getParentFile();
        if (!tempDir.mkdirs() && !tempDir.exists()) {
            log.warn("'{}' does not exist. Impossible to load tweets from disk.", tempDir.getPath());
            return false;
        }
        if (!tweetsFile.exists()) {
            log.info("'{}' does not exist. The system does not have preloaded tweets.", tweetsFile.getPath());
            return false;
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(tweetsFile))) {
            tweetsData.setTweets(Tweets.class.cast(in.readObject()));
        } catch (IOException | ClassNotFoundException e) {
            log.warn("Impossible to load tweets from disk", e);
        }
        return true;
    }

    private void loadTwitterProperties() {
        final Properties properties;
        try (final InputStream fis = new FileInputStream(ApplicationData.TWITTER_PROPS)) {
            properties = new Properties();
            properties.load(fis);
        } catch (FileNotFoundException e) {
            log.error("System property 'twitter_properties' and '{}' not found.",
                    ApplicationData.DEFAULT_TWITTER_PROPS.getPath());
            throw new ApplicationException(e);
        } catch (IOException e) {
            throw new ApplicationException(e);
        }
        if (properties.getProperty(ApplicationData.TWITTER_API_KEY) == null) {
            log.error("Your '{}' file does not contain the {} property.",
                    ApplicationData.TWITTER_PROPS, ApplicationData.TWITTER_API_KEY
            );
            throw new ApplicationException("Bad twitter properties file.");
        }
        if (properties.getProperty(ApplicationData.TWITTER_API_SECRET) == null) {
            log.error("Your '{}' file does not contain the {} property.",
                    ApplicationData.TWITTER_PROPS, ApplicationData.TWITTER_API_KEY
            );
            throw new ApplicationException("Bad twitter properties file.");
        }
        if (properties.getProperty(ApplicationData.TWITTER_API_BEARER_TOKEN) != null) {
            log.info("It looks like your '{}' file already have a twitter access token. The system will try to use it.",
                    ApplicationData.TWITTER_PROPS
            );
        }
        String maxTweets = String.valueOf(ApplicationData.DEFAULT_MAX_TWEETS);
        if (properties.getProperty(ApplicationData.TWITTER_MAX_TWEETS) != null) {
            if (StringUtils.isNumeric(properties.getProperty(ApplicationData.TWITTER_MAX_TWEETS))) {
                maxTweets = properties.getProperty(ApplicationData.TWITTER_MAX_TWEETS);
            } else {
                log.warn("{} is not numeric ['{}']. The system will use the default '{}' value instead.",
                        ApplicationData.TWITTER_MAX_TWEETS,
                        properties.getProperty(ApplicationData.TWITTER_MAX_TWEETS),
                        ApplicationData.DEFAULT_MAX_TWEETS
                );
            }
        }
        properties.put(ApplicationData.TWITTER_MAX_TWEETS, maxTweets.trim());
        applicationData.setTwitterProperties(properties);
    }

}

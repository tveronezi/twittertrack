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

import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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
        loadUsers();
        twitterRobot.fire();
        log.info("twittertrack is up and running.");
    }

    private void loadUsers() {
        final String strUsers = applicationData.getTwitterProperty(ApplicationData.TWITTER_USERS);
        if (strUsers == null || "".equals(strUsers.trim())) {
            return;
        }
        for (String user : strUsers.trim().split(",")) {
            tweetsData.addUser(user.trim());
        }
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

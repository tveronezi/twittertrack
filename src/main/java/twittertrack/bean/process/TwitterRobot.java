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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twittertrack.bean.data.TweetsData;
import twittertrack.bean.service.TwitterImpl;
import twittertrack.data.Tweet;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.TimerService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Singleton
public class TwitterRobot {
    private final Logger log = LoggerFactory.getLogger("twittertrack");

    private static final long RELOAD_INTERVAL = TimeUnit.MINUTES.toMillis(5);

    @Resource
    private TimerService timerService;

    @EJB
    private TweetsData tweetsData;

    @EJB
    private TwitterImpl twitter;

    @Timeout
    public void fire() {
        final List<Future<List<Tweet>>> processes = new ArrayList<>();
        for (String user : this.tweetsData.getUsers()) {
            processes.add(twitter.getTweets(user));
        }
        for (Future<List<Tweet>> process : processes) {
            try {
                final List<Tweet> tweets = process.get(1, TimeUnit.MINUTES);
                this.tweetsData.updateTweets(tweets);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                log.warn("Error while processing user tweets", e);
            }
        }
        timerService.createTimer(RELOAD_INTERVAL, null);
    }
}

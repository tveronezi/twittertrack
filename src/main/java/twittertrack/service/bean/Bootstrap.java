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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twittertrack.service.ApplicationException;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Singleton
@Startup
public class Bootstrap {

    private final Logger log = LoggerFactory.getLogger("twittertrack");

    private static final File DEFAULT_TWITTER_PROPS = new File(System.getProperty("user.home"), "twitter.properties");
    private static final String TWITTER_PROPS = System.getProperty(
            "twitter_properties", DEFAULT_TWITTER_PROPS.getPath()
    );

    private static final String TWITTER_API_KEY = "api_key";
    private static final String TWITTER_API_SECRET = "api_secret";
    private Properties twitterProperties;

    @PostConstruct
    public void prepare() {
        log.debug("Starting application...");
        try (final InputStream fis = new FileInputStream(TWITTER_PROPS)) {
            this.twitterProperties = new Properties();
            this.twitterProperties.load(fis);
        } catch (FileNotFoundException e) {
            log.error("System property 'twitter_properties' and '{}' not found.", DEFAULT_TWITTER_PROPS.getPath());
            throw new ApplicationException(e);
        } catch (IOException e) {
            throw new ApplicationException(e);
        }
        validateTwitterProperties();
    }

    private void validateTwitterProperties() {
        if (this.twitterProperties.getProperty(TWITTER_API_KEY) == null) {
            log.error("Your '{}' file does not contain the {} property.", TWITTER_PROPS, TWITTER_API_KEY);
            throw new ApplicationException("Bad twitter properties file.");
        }
        if (this.twitterProperties.getProperty(TWITTER_API_SECRET) == null) {
            log.error("Your '{}' file does not contain the {} property.", TWITTER_PROPS, TWITTER_API_KEY);
            throw new ApplicationException("Bad twitter properties file.");
        }
    }

}

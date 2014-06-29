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

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twittertrack.ApplicationException;
import twittertrack.JsonUtil;
import twittertrack.bean.data.ApplicationData;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

@Stateless
public class TwitterConnectionImpl implements TwitterConnection {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final String OATH_TOKEN_PATH = "https://api.twitter.com/oauth2/token";

    @EJB
    private ApplicationData application;

    private String obtainBearerToken() {
        final String result;
        final String existingToken = application.getTwitterProperty(ApplicationData.TWITTER_API_BEARER_TOKEN);
        if (existingToken == null) {
            try {
                final Request req = Request.Post(OATH_TOKEN_PATH)
                        .addHeader("Authorization", getAuthorizationValue())
                        .bodyForm(Form.form().add("grant_type", "client_credentials").build());

                final String jsonStr = req.execute().returnContent().asString();
                log.info("New bearer token loaded from '{}'.", OATH_TOKEN_PATH);
                final Map<String, Object> json = JsonUtil.getMap(new JSONObject(jsonStr));
                result = (String) json.get("access_token");
                application.setTwitterProperty(ApplicationData.TWITTER_API_BEARER_TOKEN, result);
            } catch (IOException e) {
                throw new ApplicationException(e);
            }
        } else {
            result = existingToken;
        }
        return "Bearer " + result;
    }

    private String getAuthorizationValue() {
        final String raw = application.getTwitterProperty(ApplicationData.TWITTER_API_KEY) + ":"
                + application.getTwitterProperty(ApplicationData.TWITTER_API_SECRET);
        return "Basic " + Base64.encodeBase64String(raw.getBytes());
    }

    @Override
    public String executeGet(String url, Map<String, String> parameters) {
        final String path;
        try {
            final URIBuilder builder = new URIBuilder(url);
            for (String key : parameters.keySet()) {
                builder.setParameter(key, parameters.get(key));
            }
            path = builder.build().toString();
        } catch (URISyntaxException e) {
            throw new ApplicationException(e);
        }
        final Request req = Request.Get(path).addHeader("Authorization", obtainBearerToken());
        final String content;
        try {
            content = req.execute().returnContent().asString();
        } catch (IOException e) {
            throw new ApplicationException(e);
        }
        return content;
    }

}

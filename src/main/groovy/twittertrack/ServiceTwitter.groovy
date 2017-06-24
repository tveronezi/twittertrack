package twittertrack

import groovy.json.JsonSlurper
import org.apache.http.HttpEntity
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils

import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import java.nio.charset.StandardCharsets
import java.text.DateFormat
import java.text.SimpleDateFormat

@ApplicationScoped
class ServiceTwitter {
    @Inject
    ServiceTwitterAuth auth

    private static final DateFormat DT_FORM = new SimpleDateFormat("EEE MMM dd hh:mm:ss zzzz yyyy")

    Collection<DtoTweet> listTweets(String user, Integer counter) {
        def data = HttpClients.custom().build().withCloseable { CloseableHttpClient httpclient ->
            def req = new HttpGet("https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=$user&count=$counter")
            req.addHeader('Authorization', "Bearer ${auth.accessToken}")
            String jsonStr = httpclient.execute(req).withCloseable { CloseableHttpResponse resp ->
                HttpEntity entity = resp.getEntity()
                return EntityUtils.toString(entity, StandardCharsets.UTF_8)
            }
            return new JsonSlurper().parseText(jsonStr)
        }
        return data.collect({ tweet ->
            def value = tweet.retweeted_status ?: tweet
            new DtoTweet(
                    user: tweet.user.screen_name as String,
                    author: value.user.screen_name as String,
                    date: DT_FORM.parse(value.created_at as String),
                    id: value.id as String,
                    content: value.text as String
            )
        })
    }

}

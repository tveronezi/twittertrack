package twittertrack

import groovy.json.JsonSlurper
import org.apache.http.HttpEntity
import org.apache.http.NameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils

import javax.annotation.PostConstruct
import javax.ejb.Singleton
import javax.ejb.Startup
import java.nio.charset.StandardCharsets

@Singleton
@Startup
class ServiceTwitterAuth {
    private static final String TOKEN = "${System.getProperty('api_key')}:${System.getProperty('api_secret')}"
            .bytes.encodeBase64().toString()

    private String accessToken

    @PostConstruct
    void init() {
        this.accessToken = HttpClients.custom().build().withCloseable { CloseableHttpClient httpclient ->
            def post = new HttpPost("https://api.twitter.com/oauth2/token")
            post.setEntity(new UrlEncodedFormEntity(new ArrayList<NameValuePair>().with {
                it.add(new BasicNameValuePair('grant_type', 'client_credentials'))
                return it
            }))
            post.addHeader('Authorization', "Basic $TOKEN")
            String jsonStr = httpclient.execute(post).withCloseable { CloseableHttpResponse resp ->
                HttpEntity entity = resp.getEntity()
                return EntityUtils.toString(entity, StandardCharsets.UTF_8)
            }
            return new JsonSlurper().parseText(jsonStr).access_token
        }
    }

    String getAccessToken() {
        return accessToken
    }
}

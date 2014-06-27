package twittertrack.service.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Tweets implements Serializable {
    private Map<String, Set<Tweet>> userTweets = new HashMap<>();

    public Map<String, Set<Tweet>> getUserTweets() {
        return userTweets;
    }

    public void setUserTweets(Map<String, Set<Tweet>> userTweets) {
        this.userTweets = userTweets;
    }
}

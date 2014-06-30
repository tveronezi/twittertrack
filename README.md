twittertrack
=========

### Quick start ###

You can run the application by following these simple steps.

1. Create a **twitter.properties** file under your user home directory (Example: **/home/tveronezi/twitter.properties**)
2. Checkout this source code
3. Go to the root of your application and run **mvn clean install tomee:run**
4. Open <http://localhost:8080/twittertrack>

### twitter.properties content sample ###

    #Twitter API key and secret
    api_key=<YOUR API KEY>
    api_secret=<YOUR API SECRET>

    #User accounts to be loaded
    twitter_users=AppDirect,laughingsquid,techcrunch

    #Number max of tweets per user account
    twitter_max_tweets=30

### Dependency ###

1. >= Maven 3.x.x 
2. >= Java 1.7
3. Twitter API key and secret (Create one here: [https://apps.twitter.com/](https://apps.twitter.com/))

import ch.qos.logback.core.status.StatusListener;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;


class TwitterManager {
    private Twitter twitter;
    private StatusListener statusListener;

    TwitterManager() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setOAuthConsumerKey("Opon8ooKZHMeo8Y7kx8BbF5b3");
        cb.setOAuthConsumerSecret("y6JQN4dLmOlxTGKk4VBNKC9yjjt1bTnUI4E8agk0cKkThQspEe");
        cb.setOAuthAccessToken("1036790880232005632-LNgEhHxv0vkLDN1oV13QATCxKShZ4x");
        cb.setOAuthAccessTokenSecret("V4Nixt18DdexCADWY92rSgCNkwN8OloNnR9YZcI22JcsW");
        TwitterFactory tf = new TwitterFactory(cb.build());
        this.twitter = tf.getInstance();
        //this.statusListener = new StatusAdapter();
    }

    String createTweet(String tweet) {
        String tweetLink = null;
        try {
            Status status = twitter.updateStatus(tweet);
            long tweetID = status.getId();
            tweetLink = "https://twitter.com/" + status.getUser().getScreenName() + "/status/" + tweetID;
        } catch (TwitterException e) {
            System.out.println("There was an error trying to post the tweet: " + e);
        }
        return tweetLink;
    }
}

/*
        RequestToken requestToken = twitter.getOAuthRequestToken();
        System.out.println("Authorization URL: \n"
                + requestToken.getAuthorizationURL());

        AccessToken accessToken = null;

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (null == accessToken) {
            try {
                System.out.print("Input PIN here: ");
                String pin = br.readLine();

                accessToken = twitter.getOAuthAccessToken(requestToken, pin);

            } catch (TwitterException te) {

                System.out.println("Failed to get access token, caused by: "
                        + te.getMessage());

                System.out.println("Retry input PIN");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        */
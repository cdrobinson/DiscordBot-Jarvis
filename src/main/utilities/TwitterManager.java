import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.event.Event;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import net.dv8tion.jda.core.entities.TextChannel;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

class TwitterManager implements Runnable{

    private Client twitterClient;
    private BlockingQueue<String> msgQueue;
    private TextChannel outputChannel;

    TwitterManager(TextChannel outputChannel) {
        ConfigManager cm = new ConfigManager();
        this.outputChannel = outputChannel;
        /* Set up your blocking queues: Be sure to size these properly based on expected TPS of your stream */
        this.msgQueue = new LinkedBlockingQueue<>(1000);
        BlockingQueue<Event> eventQueue = new LinkedBlockingQueue<>(100);

        /* Declare the host you want to connect to, the endpoint, and authentication (basic auth or oauth) */
        Hosts twitterHosts = new HttpHosts(Constants.STREAM_HOST);
        StatusesFilterEndpoint twitterEndpoint = new StatusesFilterEndpoint();
        // Optional: set up some followings and track terms
        List<Long> followings = Lists.newArrayList();
        for (String following : cm.getProperty("followings").split(", ")) {
            followings.add(Long.valueOf(following));
        }
        List<String> terms = Lists.newArrayList();
        terms.addAll(Arrays.asList(cm.getProperty("terms").split(", ")));
        twitterEndpoint.followings(followings);
        twitterEndpoint.trackTerms(terms);

        // These secrets should be read from a config file
        Authentication twitterAuth = new OAuth1(cm.getProperty("consumerKey"), cm.getProperty("consumerSecret"), cm.getProperty("token"), cm.getProperty("tokenSecret"));
        ClientBuilder builder = new ClientBuilder()
                .name(cm.getProperty("watcherName"))                           // optional: mainly for the logs
                .hosts(twitterHosts)
                .authentication(twitterAuth)
                .endpoint(twitterEndpoint)
                .processor(new StringDelimitedProcessor(msgQueue))
                .eventMessageQueue(eventQueue);                          // optional: use this if you want to process client events
        this.twitterClient = builder.build();
        // Attempts to establish a connection.
        twitterClient.connect();
    }

    @Override
    public void run() {
        // on a different thread, or multiple different threads....
        while (!twitterClient.isDone()) {
            try {
                String msg = msgQueue.take();
                if (!isRetweet(msg) && !isReply(msg)) {
                    this.outputChannel.sendMessage(getTweetLink(msg)).queue();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isRetweet(String jsonString) {
        JSONObject tweet = new JSONObject(jsonString);
        try {
            tweet.getJSONObject("retweeted_status");
            return true;
        } catch (JSONException e) {
            return !e.getMessage().equals("JSONObject[\"retweeted_status\"] not found.");
        }
    }

    private boolean isReply(String jsonString) {
        JSONObject tweet = new JSONObject(jsonString);
        try {
            tweet.getLong("in_reply_to_status_id");
            return true;
        } catch (JSONException e) {
            System.out.println(e.getMessage());
            return !e.getMessage().equals("JSONObject[\"in_reply_to_status_id\"] is not a long.");
        }
    }

    private String getTweetLink(String jsonString) {
        JSONObject tweet = new JSONObject(jsonString);
        JSONObject user = tweet.getJSONObject("user");
        return "\nhttps://twitter.com/" + user.get("screen_name") + "/status/" + tweet.get("id");
    }
}
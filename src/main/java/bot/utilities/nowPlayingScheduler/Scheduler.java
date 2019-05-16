/*
 * Copyright (c) 2018 Chris Robinson. All rights reserved.
 */

package bot.utilities.nowPlayingScheduler;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Game;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.MINUTES;

public class Scheduler implements Runnable {

    private JDA api;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private Integer counter;
    private ArrayList<String> nowPlayingList;

    public Scheduler(JDA api) {
        this.api = api;
        this.counter = 0;
        MongoDbConnector mongoDbConnector = new MongoDbConnector();
        nowPlayingList = mongoDbConnector.getNowPlayingList();
        mongoDbConnector.endConnection();
    }

    @Override
    public void run() {
        playScheduler();
    }

    private void playScheduler() {
        Runnable changeGame = () -> {
            api.getPresence().setGame(Game.playing(nowPlayingList.get(counter)));
            counter += 1;
            if (counter == nowPlayingList.size()) {
                counter = 0;
            }
        };
        Runnable updateGameList = () -> {
            MongoDbConnector mongoDbConnector = new MongoDbConnector();
            nowPlayingList = mongoDbConnector.getNowPlayingList();
            mongoDbConnector.endConnection();
        };
        scheduler.scheduleAtFixedRate(changeGame, 0, 10, MINUTES);
        scheduler.scheduleAtFixedRate(updateGameList, 0, 30, MINUTES);
        //ScheduledFuture<?> nowPlayingHandler = scheduler.scheduleAtFixedRate(changeGame, 3, 10, MINUTES);
        //Runnable canceller = () -> nowPlayingHandler.cancel(false);
        //scheduler.schedule(canceller, 1, HOURS);
    }

}

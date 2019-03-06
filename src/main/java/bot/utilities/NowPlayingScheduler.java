/*
 * Copyright (c) 2018 Chris Robinson. All rights reserved.
 */

package bot.utilities;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Game;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.MINUTES;

public class NowPlayingScheduler implements Runnable {

    private JDA api;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private Integer counter;
    private final String[] gameList = {"Minecraft VR", "Use !help for help"};

    public NowPlayingScheduler(JDA api) {
        this.api = api;
        this.counter = 0;
    }

    private void playScheduler() {
        Runnable changeGame = () -> {
            String game = gameList[counter];
            api.getPresence().setGame(Game.playing(game));
            counter += 1;
            if (counter == gameList.length) {
                counter = 0;
            }
        };
        scheduler.scheduleAtFixedRate(changeGame, 0, 10, MINUTES);
        //ScheduledFuture<?> nowPlayingHandler = scheduler.scheduleAtFixedRate(changeGame, 3, 10, MINUTES);
        //Runnable canceller = () -> nowPlayingHandler.cancel(false);
        //scheduler.schedule(canceller, 1, HOURS);
    }

    @Override
    public void run() {
        playScheduler();
    }
}

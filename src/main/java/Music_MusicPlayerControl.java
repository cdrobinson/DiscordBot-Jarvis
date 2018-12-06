import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;

public class Music_MusicPlayerControl extends ListenerAdapter {
    private static final int DEFAULT_VOLUME = 10; //(0 - 150, where 100 is default max volume)
    private final AudioPlayerManager playerManager;
    private final Map<String, Music_GuildMusicManager> musicManagers;

    Music_MusicPlayerControl() {
        java.util.logging.Logger.getLogger("org.apache.http.client.protocol.ResponseProcessCookies").setLevel(Level.OFF);
        this.playerManager = new DefaultAudioPlayerManager();
        playerManager.registerSourceManager(new YoutubeAudioSourceManager());
        playerManager.registerSourceManager(new SoundCloudAudioSourceManager());
        playerManager.registerSourceManager(new BandcampAudioSourceManager());
        playerManager.registerSourceManager(new VimeoAudioSourceManager());
        playerManager.registerSourceManager(new TwitchStreamAudioSourceManager());
        playerManager.registerSourceManager(new HttpAudioSourceManager());
        playerManager.registerSourceManager(new LocalAudioSourceManager());

        musicManagers = new HashMap<>();
    }

    //Prefix for all commands: .
    //Example:  .play
    //Current commands
    // play         - Plays songs from the current queue. Starts playing again if it was previously paused
    // play [url]   - Adds a new song to the queue and starts playing if it wasn't playing already
    // pplay        - Adds a playlist to the queue and starts playing if not already playing
    // pause        - Pauses audio playback
    // stop         - Completely stops audio playback, skipping the current song.
    // skip         - Skips the current song, automatically starting the next
    // nowplaying   - Prints information about the currently playing song (title, current time)
    // np           - alias for nowplaying
    // list         - Lists the songs in the queue
    // volume [val] - Sets the volume of the MusicPlayer [10 - 100]
    // restart      - Restarts the current song or restarts the previous song if there is no current song playing.
    // repeat       - Makes the player repeat the currently playing song
    // reset        - Completely resets the player, fixing all errors and clearing the queue.
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getGuild().getId().equals("237059614384848896")) return;
        if (!event.getChannel().getName().equals("music")) return;
        System.out.println("Message received in music channel");

        String[] command = event.getMessage().getContentDisplay().split(" ", 2);
        if (!command[0].startsWith("."))    //message doesn't start with prefix.
            return;

        Guild guild = event.getGuild();
        Music_GuildMusicManager guildMusicManager = getMusicManager(guild);
        AudioPlayer player = guildMusicManager.player;
        Music_TrackScheduler scheduler = guildMusicManager.scheduler;

        if (".play".equals(command[0])) {
            if (command.length == 1) //It is only the command to start playback (probably after pause)
            {
                if (player.isPaused()) {
                    player.setPaused(false);
                    event.getChannel().sendMessage("Playback as been resumed.").queue();
                } else if (player.getPlayingTrack() != null) {
                    event.getChannel().sendMessage("Player is already playing!").queue();
                } else if (scheduler.queue.isEmpty()) {
                    event.getChannel().sendMessage("The current audio queue is empty! Add something to the queue first!").queue();
                }
            } else    //Commands has 2 parts, .play and url.
            {
                event.getChannel().sendTyping().queue();
                guild.getAudioManager().setSendingHandler(guildMusicManager.sendHandler);
                guild.getAudioManager().openAudioConnection(event.getMember().getVoiceState().getChannel());
                loadAndPlay(guildMusicManager, event.getChannel(), command[1], false);
            }
        } else if (".pplay".equals(command[0]) && command.length == 2) {
            event.getChannel().sendTyping().queue();
            guild.getAudioManager().setSendingHandler(guildMusicManager.sendHandler);
            guild.getAudioManager().openAudioConnection(event.getMember().getVoiceState().getChannel());
            loadAndPlay(guildMusicManager, event.getChannel(), command[1], true);
        } else if (".skip".equals(command[0])) {
            event.getChannel().sendTyping().queue();
            scheduler.nextTrack();
            event.getChannel().sendMessage("The current track was skipped.").queue();
        } else if (".pause".equals(command[0])) {
            if (player.getPlayingTrack() == null) {
                event.getChannel().sendMessage("Cannot pause or resume player because no track is loaded for playing.").queue();
                return;
            }
            player.setPaused(!player.isPaused());
            if (player.isPaused())
                event.getChannel().sendMessage("The player has been paused.").queue();
            else
                event.getChannel().sendMessage("The player has resumed playing.").queue();
        } else if (".stop".equals(command[0])) {
            scheduler.queue.clear();
            player.stopTrack();
            player.setPaused(false);
            event.getChannel().sendMessage("Playback has been completely stopped and the queue has been cleared.").queue();
            guild.getAudioManager().setSendingHandler(null);
            guild.getAudioManager().closeAudioConnection();
        } else if (".volume".equals(command[0])) {
            if (command.length == 1) {
                event.getChannel().sendMessage("Current player volume: **" + player.getVolume() + "**").queue();
            } else {
                try {
                    int newVolume = Math.max(10, Math.min(100, Integer.parseInt(command[1])));
                    int oldVolume = player.getVolume();
                    player.setVolume(newVolume);
                    event.getChannel().sendMessage("Player volume changed from `" + oldVolume + "` to `" + newVolume + "`").queue();
                } catch (NumberFormatException e) {
                    event.getChannel().sendMessage("`" + command[1] + "` is not a valid integer. (10 - 100)").queue();
                }
            }
        } else if (".restart".equals(command[0])) {
            AudioTrack track = player.getPlayingTrack();
            if (track == null)
                track = scheduler.lastTrack;

            if (track != null) {
                event.getChannel().sendMessage("Restarting track: " + track.getInfo().title).queue();
                player.playTrack(track.makeClone());
            } else {
                event.getChannel().sendMessage("No track has been previously started, so the player cannot replay a track!").queue();
            }
        } else if (".repeat".equals(command[0])) {
            scheduler.setRepeating(!scheduler.isRepeating());
            event.getChannel().sendMessage("Player was set to: **" + (scheduler.isRepeating() ? "repeat" : "not repeat") + "**").queue();
        } else if (".reset".equals(command[0])) {
            synchronized (musicManagers) {
                scheduler.queue.clear();
                player.destroy();
                guild.getAudioManager().setSendingHandler(null);
                musicManagers.remove(guild.getId());
            }

            guildMusicManager = getMusicManager(guild);
            guild.getAudioManager().setSendingHandler(guildMusicManager.sendHandler);
            event.getChannel().sendMessage("The player has been completely reset!").queue();

        } else if (".nowplaying".equals(command[0]) || ".np".equals(command[0])) {
            AudioTrack currentTrack = player.getPlayingTrack();
            if (currentTrack != null) {
                String title = currentTrack.getInfo().title;
                String position = getTimestamp(currentTrack.getPosition());
                String duration = getTimestamp(currentTrack.getDuration());

                String nowplaying = String.format("**Playing:** %s\n**Time:** [%s / %s]",
                        title, position, duration);

                event.getChannel().sendMessage(nowplaying).queue();
            } else
                event.getChannel().sendMessage("The player is not currently playing anything!").queue();
        } else if (".list".equals(command[0])) {
            Queue<AudioTrack> queue = scheduler.queue;
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (queue) {
                if (queue.isEmpty()) {
                    event.getChannel().sendMessage("The queue is currently empty!").queue();
                } else {
                    int trackCount = 0;
                    long queueLength = 0;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Current Queue: Entries: ").append(queue.size()).append("\n");
                    for (AudioTrack track : queue) {
                        queueLength += track.getDuration();
                        if (trackCount < 10) {
                            sb.append("`[").append(getTimestamp(track.getDuration())).append("]` ");
                            sb.append(track.getInfo().title).append("\n");
                            trackCount++;
                        }
                    }
                    sb.append("\n").append("Total Queue Time Length: ").append(getTimestamp(queueLength));

                    event.getChannel().sendMessage(sb.toString()).queue();
                }
            }
        } else if (".shuffle".equals(command[0])) {
            if (scheduler.queue.isEmpty()) {
                event.getChannel().sendMessage("The queue is currently empty!").queue();
                return;
            }

            scheduler.shuffle();
            event.getChannel().sendMessage("The queue has been shuffled!").queue();
        }
    }

    private void loadAndPlay(Music_GuildMusicManager guildMusicManager, final MessageChannel channel, String url, final boolean addPlaylist) {
        final String trackUrl;

        //Strip <>'s that prevent discord from embedding link resources
        if (url.startsWith("<") && url.endsWith(">"))
            trackUrl = url.substring(1, url.length() - 1);
        else
            trackUrl = url;

        playerManager.loadItemOrdered(guildMusicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                String msg = "Adding to queue: " + track.getInfo().title;
                if (guildMusicManager.player.getPlayingTrack() == null)
                    msg += "\nand the Player has started playing;";

                guildMusicManager.scheduler.queue(track);
                channel.sendMessage(msg).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();
                List<AudioTrack> tracks = playlist.getTracks();


                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().get(0);
                }

                if (addPlaylist) {
                    channel.sendMessage("Adding **" + playlist.getTracks().size() + "** tracks to queue from playlist: " + playlist.getName()).queue();
                    tracks.forEach(guildMusicManager.scheduler::queue);
                } else {
                    channel.sendMessage("Adding to queue " + firstTrack.getInfo().title + " (first track of playlist " + playlist.getName() + ")").queue();
                    guildMusicManager.scheduler.queue(firstTrack);
                }
            }

            @Override
            public void noMatches() {
                channel.sendMessage("Nothing found by " + trackUrl).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                channel.sendMessage("Could not play: " + exception.getMessage()).queue();
            }
        });
    }

    private Music_GuildMusicManager getMusicManager(Guild guild) {
        String guildId = guild.getId();
        Music_GuildMusicManager mng = musicManagers.get(guildId);
        if (mng == null) {
            synchronized (musicManagers) {
                mng = musicManagers.get(guildId);
                if (mng == null) {
                    mng = new Music_GuildMusicManager(playerManager);
                    mng.player.setVolume(DEFAULT_VOLUME);
                    musicManagers.put(guildId, mng);
                }
            }
        }
        return mng;
    }

    private static String getTimestamp(long milliseconds) {
        int seconds = (int) (milliseconds / 1000) % 60;
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);

        if (hours > 0)
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        else
            return String.format("%02d:%02d", minutes, seconds);
    }

}
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

/**
 * Holder for both the player and a track scheduler for one guild.
 */
class Music_GuildMusicManager {
    /**
     * Audio player for the guild.
     */
    final AudioPlayer player;
    /**
     * Track scheduler for the player.
     */
    final Music_TrackScheduler scheduler;
    /**
     * Wrapper around AudioPlayer to use it as an AudioSendHandler.
     */
    final AudioPlayerSendHandler sendHandler;

    /**
     * Creates a player and a track scheduler.
     *
     * @param manager Audio player manager to use for creating the player.
     */
    Music_GuildMusicManager(AudioPlayerManager manager) {
        player = manager.createPlayer();
        scheduler = new Music_TrackScheduler(player);
        sendHandler = new AudioPlayerSendHandler(player);
        player.addListener(scheduler);
    }
}
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.exceptions.GuildUnavailableException;
import net.dv8tion.jda.core.exceptions.HierarchyException;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.core.managers.GuildController;

import java.util.List;

class RoleChanger {

    static void makeRegisteredUser(User author) {
        List<Guild> guildList = author.getMutualGuilds();
        Guild specificGuild;
        for (Guild guild : guildList) {
            String guildName = guild.getName();
            if (guildName.equals("Cardinal Esports")) {
                specificGuild = guild;
                Member serverUser = specificGuild.getMember(author);
                GuildController guildController = guild.getController();
                try {
                    guildController.addSingleRoleToMember(serverUser, guild.getRolesByName("BSU Student", true).get(0)).complete();
                    guildController.removeSingleRoleFromMember(serverUser, guild.getRolesByName("Community Member", true).get(0)).complete();
                } catch (IllegalArgumentException | HierarchyException | InsufficientPermissionException | GuildUnavailableException e) {
                    System.out.println("There was an error editing the roles for the user " + serverUser.getEffectiveName() + "\nError: " + e);
                }
            }
        }
    }
}

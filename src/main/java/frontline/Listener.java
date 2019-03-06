/*
 * Copyright (c) 2018 Chris Robinson. All rights reserved.
 */

package frontline;

import bot.configuration.ConfigManager;
import bot.utilities.HelpMessageBuilder;
import bot.utilities.UserInputManager;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.Arrays;
import java.util.List;

public class Listener extends ListenerAdapter {

    private final ConfigManager cm = new ConfigManager();
    private final String commandPrefix = cm.getCommandPrefix();

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (!event.getGuild().getId().equals("237059614384848896")) return;
        String message = event.getMessage().getContentRaw();
        if (message.startsWith(commandPrefix)) {
            String command = message.split(commandPrefix)[1].toLowerCase();
            switch (command) {
                case "chirpchirp":
                    List<Guild> mutualGuilds = event.getMember().getUser().getMutualGuilds();
                    for (Guild guild : mutualGuilds) {
                        if (guild.getId().equals("260565533575872512")) {
                            List<Role> userRoles = guild.getMemberById(event.getMember().getUser().getId()).getRoles();
                            for (Role role : userRoles) {
                                if (role.getId().equals("443151138062073866")) {
                                    event.getGuild().getController().addSingleRoleToMember(event.getMember(), event.getGuild().getRoleById("451495511724130305")).queue();
                                }
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
            if (event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                List<String> messageList = Arrays.asList(Arrays.asList(message.split(commandPrefix)).get(1).split(" "));
                List<String> commandParameters = messageList.subList(1, messageList.size());
                switch (command) {
                    case "say":
                        StringBuilder whatToSay = new StringBuilder();
                        for (String word : commandParameters) {
                            whatToSay.append(word);
                            whatToSay.append(" ");
                        }
                        event.getGuild().getTextChannelsByName("general", true).get(0).sendMessage(whatToSay).queue();
                        System.out.printf("You told me to say this in #general chat: %s", whatToSay);
                        break;
                    case "perms":
                        List<Permission> authorPerms = event.getMember().getPermissions();
                        StringBuilder permList = new StringBuilder();
                        permList.append("```");
                        for (Permission permission : authorPerms) {
                            permList.append(permission.getName());
                            permList.append("\r");
                        }
                        permList.append("```");
                        event.getChannel().sendMessage(permList.toString()).queue();
                        break;
                    case "vote":
                        UserInputManager.createPoll(event);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (!event.getGuild().getId().equals("237059614384848896")) return;
        String welcomeMessage = "Welcome to the Frontline!";
        event.getMember().getUser().openPrivateChannel().queue((userPM) -> userPM.sendMessage(welcomeMessage).queue());

        //Checks if the new member is part of the Cardinal Esports discord server (260565533575872512) and if they have the BSU Student role (443151138062073866)
        //If they have both of those, then gives them the Cardinals role (451495511724130305) on Frontline
        List<Guild> mutualGuilds = event.getUser().getMutualGuilds();
        for (Guild guild : mutualGuilds) {
            if (guild.getId().equals("260565533575872512")) {
                List<Role> userRoles = guild.getMemberById(event.getUser().getId()).getRoles();
                for (Role role : userRoles) {
                    if (role.getId().equals("443151138062073866")) {
                        event.getGuild().getController().addSingleRoleToMember(event.getMember(), event.getGuild().getRoleById("451495511724130305")).queue();
                    }
                }
            }
        }
    }
}

/*
 * Copyright (c) 2018 Chris Robinson. All rights reserved.
 */

package frontline;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;

public class BsuChecker {

    public static void cardinalChecker(MessageReceivedEvent messageReceivedEvent){
        List<Guild> mutualGuilds = messageReceivedEvent.getMember().getUser().getMutualGuilds();
        for (Guild guild : mutualGuilds) {
            if (guild.getId().equals("260565533575872512")) {
                List<Role> userRoles = guild.getMemberById(messageReceivedEvent.getMember().getUser().getId()).getRoles();
                for (Role role : userRoles) {
                    if (role.getId().equals("443151138062073866")) {
                        messageReceivedEvent.getGuild().getController().addSingleRoleToMember(messageReceivedEvent.getMember(), messageReceivedEvent.getGuild().getRoleById("451495511724130305")).queue();
                    }
                }
            }
        }
    }
}

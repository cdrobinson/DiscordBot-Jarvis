/*
 * Copyright (c) 2018 Chris Robinson. All rights reserved.
 */

package reactionRole;

import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Role;

public class RoleData {

    private String roleID;
    private String roleName;
    private String roleDescription;
    private Emote roleEmote;
    private Role discordRole;

    RoleData(String roleID, String roleName, String roleDescription, Emote roleEmote, Role discordRole) {
        this.roleID = roleID;
        this.roleName = roleName;
        this.roleDescription = roleDescription;
        this.roleEmote = roleEmote;
        this.discordRole = discordRole;
    }

    String getRoleID() {
        return roleID;
    }

    String getRoleName() {
        return roleName;
    }

    String getRoleDescription() {
        return roleDescription;
    }

    Emote getRoleEmote() {
        return roleEmote;
    }

    Role getDiscordRole() {
        return discordRole;
    }
}

package net.wesjd.pvpacademy.team;

import net.wesjd.pvpacademy.PvPAcademy;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Team {

    private final String name;
    private final ChatColor color;
    private final int maxSize;

    public Team(String name, ChatColor color, int maxSize) {
        this.name = name;
        this.color = color;
        this.maxSize = maxSize;
    }

    public void addPlayer(Player player) {
        PvPAcademy.get().getTeamRegistry().joinTeam(player, this);
    }

    public String getName() {
        return name;
    }

    public ChatColor getColor() {
        return color;
    }

    public int getMaxSize() {
        return maxSize;
    }

}

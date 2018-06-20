package net.wesjd.pvpacademy.team;

import net.wesjd.pvpacademy.util.PlayerMap;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TeamRegistry {

    private final Map<String, Team> teams = new HashMap<>();
    private final PlayerMap<String> playerTeam = new PlayerMap<>();

    public void registerTeam(Team team) {
        teams.put(team.getName(), team);
    }

    public Optional<Team> getTeamByName(String name) {
        return Optional.ofNullable(teams.get(name));
    }

    public Optional<Team> getTeam(Player player) {
        final String team = playerTeam.get(player);
        if(team == null) return Optional.empty();
        else return getTeamByName(team);
    }

    public void joinTeam(Player player, Team team) {
        playerTeam.put(player, team.getName());
        player.sendMessage(ChatColor.BLUE + "You have joined " + ChatColor.GRAY + team.getName());
    }

    public void leaveTeam(Player player) {
        playerTeam.remove(player);
    }

}

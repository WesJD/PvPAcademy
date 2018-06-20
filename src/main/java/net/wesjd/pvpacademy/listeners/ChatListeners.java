package net.wesjd.pvpacademy.listeners;

import net.wesjd.pvpacademy.PvPAcademy;
import net.wesjd.pvpacademy.event.PlayerLeaveGameEvent;
import net.wesjd.pvpacademy.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Arrays;
import java.util.Optional;

public class ChatListeners implements Listener {

    private final PvPAcademy main;

    public ChatListeners(PvPAcademy main) {
        this.main = main;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        final Player player = e.getPlayer();
        final String format = (player.isOp() ? ChatColor.RED + "[Op] " : ChatColor.WHITE + "") + "%s" + ChatColor.WHITE + ": %s";

        final Optional<Team> possibleTeam = main.getTeamRegistry().getTeam(player);
        if(possibleTeam.isPresent()) {
            final Team team = possibleTeam.get();
            e.setFormat(team.getColor() + "(" + team.getName() + ") " + format);
        } else e.setFormat(format);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        main.getArena().makeSpectator(e.getPlayer());
        Arrays.stream(main.getWelcomeMessage()).forEach(e.getPlayer()::sendMessage);
        e.setJoinMessage(ChatColor.GREEN.toString() + ChatColor.BOLD + "> " + ChatColor.GREEN + e.getPlayer().getName());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        e.setQuitMessage(ChatColor.RED.toString() + ChatColor.BOLD + "< " + ChatColor.RED + e.getPlayer().getName());
        Bukkit.getPluginManager().callEvent(new PlayerLeaveGameEvent(e.getPlayer()));
    }

}

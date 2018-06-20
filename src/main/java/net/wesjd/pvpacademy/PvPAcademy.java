package net.wesjd.pvpacademy;

import net.wesjd.pvpacademy.command.SuicideCommand;
import net.wesjd.pvpacademy.game.Arena;
import net.wesjd.pvpacademy.listeners.ChatListeners;
import net.wesjd.pvpacademy.listeners.DeathListeners;
import net.wesjd.pvpacademy.listeners.InteractListeners;
import net.wesjd.pvpacademy.listeners.WorldListeners;
import net.wesjd.pvpacademy.team.Team;
import net.wesjd.pvpacademy.team.TeamRegistry;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class PvPAcademy extends JavaPlugin {

    private TeamRegistry teamRegistry;
    private Arena arena;
    private WorldListeners worldListeners;

    private Location mapCenter;
    private Pair<Location, Location> mapBounds;
    private final String[] welcomeMessage = new String[] {
            ChatColor.YELLOW + "Welcome to " + ChatColor.RED + "PvPAcademy!",
            ChatColor.YELLOW + "This is an SG simulator server to train and develop your pvp skills.",
            ChatColor.YELLOW + "Click on your clock to get started.",
            ChatColor.RED + "WARNING: " + ChatColor.YELLOW + "This server is in beta and you should expect bugs."
    };

    @Override
    public void onEnable() {
        teamRegistry = new TeamRegistry();
        arena = new Arena(this);

        mapCenter = new Location(Bukkit.getWorld("world"), -34, 19.5, 840.9);
        mapBounds = ImmutablePair.of(new Location(Bukkit.getWorld("world"),-81, 0, 880), //y doesn't
                                     new Location(Bukkit.getWorld("world"),6, 0, 801));  //matter here

        getCommand("suicide").setExecutor(new SuicideCommand(this));

        final PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new ChatListeners(this), this);
        pluginManager.registerEvents(new DeathListeners(this), this);
        pluginManager.registerEvents(new InteractListeners(this), this);
        pluginManager.registerEvents(worldListeners = new WorldListeners(this), this);

        teamRegistry.registerTeam(new Team("Specs", ChatColor.GRAY, -1)); //spectating
        teamRegistry.registerTeam(new Team("Tributes", ChatColor.GREEN, 50)); //playing the game
        teamRegistry.registerTeam(new Team("Waiting", ChatColor.YELLOW, 7)); //waiting to join the game
    }

    @Override
    public void onDisable() {
        worldListeners.restoreAll();
    }

    public Location getMapCenter() {
        return mapCenter;
    }

    public Pair<Location, Location> getMapBounds() {
        return mapBounds;
    }

    public String[] getWelcomeMessage() {
        return welcomeMessage;
    }

    public Arena getArena() {
        return arena;
    }

    public TeamRegistry getTeamRegistry() {
        return teamRegistry;
    }

    public static PvPAcademy get() {
        return getPlugin(PvPAcademy.class);
    }

}

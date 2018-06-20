package net.wesjd.pvpacademy.game;

import net.wesjd.pvpacademy.PvPAcademy;
import net.wesjd.pvpacademy.event.PlayerJoinGameEvent;
import net.wesjd.pvpacademy.event.PlayerLeaveGameEvent;
import net.wesjd.pvpacademy.game.items.ItemGenerator;
import net.wesjd.pvpacademy.team.Team;
import net.wesjd.pvpacademy.team.TeamRegistry;
import net.wesjd.pvpacademy.util.PlayerMap;
import net.wesjd.pvpacademy.util.RandomTeleporter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class Arena {

    private final ItemGenerator itemGenerator = new ItemGenerator();
    private final RandomTeleporter randomTeleporter = new RandomTeleporter();
    private final PlayerMap<Double> luckLevel = new PlayerMap<>();
    private final PvPAcademy main;
    private final TeamRegistry teamRegistry;

    public Arena(PvPAcademy main) {
        this.main = main;
        this.teamRegistry = main.getTeamRegistry();
    }

    public void play(Player player) {
        luckLevel.putIfAbsent(player, 0D);
        teamRegistry.getTeamByName("Waiting").ifPresent(team -> team.addPlayer(player));

        final PlayerInventory inventory = player.getInventory();
        inventory.clear();
        inventory.setArmorContents(null);
        itemGenerator.fillInventory(inventory, (int) Math.floor(luckLevel.get(player))); //floor (round down) luck amount, always

        player.teleport(main.getMapCenter());
        player.setLevel(0);
        player.setSaturation(20F);
        player.setHealth(20D);
        player.setFoodLevel(20);
        player.setFallDistance(0F);
        player.setFireTicks(0);

        new BukkitRunnable() {
            int timeLeft = 5;
            @Override
            public void run() {
                try {
                    if(player.isOnline()) {
                        if(timeLeft == 0) {
                            player.setFlying(false);
                            player.setAllowFlight(false);
                            player.spigot().setCollidesWithEntities(true);
                            Bukkit.getOnlinePlayers().forEach(plr -> plr.showPlayer(player));
                            if(player.getGameMode() != GameMode.SURVIVAL) player.setGameMode(GameMode.SURVIVAL);

                            player.sendMessage("");
                            player.sendMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Fight other players!");
                            player.sendMessage("");
                            player.playSound(player.getLocation(), Sound.ENDERDRAGON_GROWL, 2F, 1F);
                            player.playEffect(player.getLocation().add(0, 1, 0), Effect.ENDER_SIGNAL, 1);

                            player.setFallDistance(0F);
                            randomTeleporter.teleportRandomly(player, main.getMapBounds());
                            teamRegistry.getTeamByName("Tributes").ifPresent(team -> team.addPlayer(player));
                            Bukkit.getPluginManager().callEvent(new PlayerJoinGameEvent(player));
                            cancel();
                        } else {
                            player.sendMessage(ChatColor.GREEN + "Spawning in " + ChatColor.GRAY + timeLeft + "...");
                            if(timeLeft == 1) player.sendMessage(ChatColor.GOLD + "Get ready!");
                            timeLeft--;
                            player.playSound(player.getLocation(), Sound.CLICK, 1F, 1F);
                        }
                    } else cancel();
                } catch (ExecutionException ex) {
                    ex.printStackTrace();
                }
            }
        }.runTaskTimer(PvPAcademy.get(), 0L, 20L);
    }

    public void makeSpectator(Player player) {
        teamRegistry.getTeamByName("Specs").ifPresent(team -> team.addPlayer(player));
        player.teleport(main.getMapCenter());
        player.setLevel(0);
        player.setSaturation(20F);
        player.setHealth(20D);
        player.setFoodLevel(20);
        player.setFallDistance(0F);
        player.setFireTicks(0);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.spigot().setCollidesWithEntities(false);
        Bukkit.getOnlinePlayers().forEach(plr -> plr.hidePlayer(player));
        Bukkit.getOnlinePlayers().forEach(plr -> {
            final Optional<Team> possibleTeam = main.getTeamRegistry().getTeam(plr);
            if(possibleTeam.isPresent()) {
                final Team team = possibleTeam.get();
                if(team.getName().equals("Specs") || team.getName().equals("Waiting")) player.hidePlayer(plr);
            }
        });

        final PlayerInventory inventory = player.getInventory();
        inventory.clear();
        inventory.setArmorContents(null);
        {
            final ItemStack stack = new ItemStack(Material.WATCH);
            final ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + "Click to Join");
            meta.setLore(Arrays.asList(ChatColor.GRAY + "Join the fun!"));
            stack.setItemMeta(meta);
            inventory.setItem(0, stack);
        }
        {
            final ItemStack stack = new ItemStack(Material.ENCHANTED_BOOK);
            final ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + "Spectate");
            meta.setLore(Arrays.asList(ChatColor.GRAY + "View players in the game"));
            stack.setItemMeta(meta);
            inventory.setItem(1, stack);
        }
        {
            final ItemStack stack = new ItemStack(Material.ARROW);
            final ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + "Go to Map Center");
            meta.setLore(Arrays.asList(ChatColor.GRAY + "Teleport to the center of the map."));
            stack.setItemMeta(meta);
            inventory.setItem(8, stack);
        }

        if(luckLevel.containsKey(player)) //to make sure they are actually leaving the game, because this method is called when you login too
            Bukkit.getPluginManager().callEvent(new PlayerLeaveGameEvent(player));
    }

    public double getLuck(Player player) {
        return luckLevel.get(player);
    }

    public void addLuck(Player player, double amount) {
        luckLevel.put(player, luckLevel.getOrDefault(player, 0D) + amount);
    }

}

package net.wesjd.pvpacademy.listeners;

import net.wesjd.pvpacademy.PvPAcademy;
import net.wesjd.pvpacademy.team.Team;
import net.wesjd.pvpacademy.util.PlayerMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.text.DecimalFormat;
import java.util.Optional;

public class DeathListeners implements Listener {

    private final PlayerMap<Integer> killStreak = new PlayerMap<>();
    private final DecimalFormat format = new DecimalFormat("##.#");
    private final PvPAcademy main;

    public DeathListeners(PvPAcademy main) {
        this.main = main;
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if(e.getEntity() instanceof Player) {
            final Player player = (Player) e.getEntity();
            final Optional<Team> possibleTeam = main.getTeamRegistry().getTeam(player);
            if(!possibleTeam.isPresent() || !possibleTeam.get().getName().equals("Tributes")) {
                e.setCancelled(true);
                return;
            }

            Entity damager = null;
            if(e instanceof EntityDamageByEntityEvent) {
                final Entity eventDamager = ((EntityDamageByEntityEvent) e).getDamager();
                if(eventDamager instanceof Projectile) {
                    final Projectile projectile = (Projectile) eventDamager;
                    projectile.remove();
                    if(projectile.getShooter() instanceof Entity) damager = (Entity) projectile.getShooter();
                } else damager = eventDamager;
            }

            Player playerDamager = null;
            if(damager instanceof Player) {
                playerDamager = (Player) damager;
                final Optional<Team> possibleDamagerTeam = main.getTeamRegistry().getTeam(playerDamager);
                if(!possibleDamagerTeam.isPresent() || !possibleDamagerTeam.get().getName().equals("Tributes")) {
                    e.setCancelled(true);
                    return;
                }
            }

            if(e.getDamage() >= player.getHealth()) { //will die
                e.setCancelled(true);

                //TODO - drop a random special item?

                player.getWorld().playSound(player.getLocation(), Sound.IRONGOLEM_DEATH, 1F, 1F);
                main.getArena().makeSpectator(player);
                killStreak.remove(player);

                String deathMessage = "died due to unknown causes.";
                switch(e.getCause()) {
                    case FALL:
                        deathMessage = "hit the ground pretty hard.";
                        break;
                    case MELTING:
                    case FIRE_TICK:
                    case LAVA:
                    case FIRE:
                        deathMessage = "burnt to a crisp.";
                        break;
                    case SUICIDE:
                        deathMessage = "decided to take their own life.";
                        break;
                    case CONTACT:
                    case THORNS:
                        deathMessage = "was prickled to death.";
                        break;
                    case DROWNING:
                        deathMessage = "tried to breathe under water.";
                        break;
                    case PROJECTILE:
                        if(playerDamager != null) deathMessage = "was shot to death by " + ChatColor.GRAY + playerDamager.getName() + ".";
                        else if(damager != null) deathMessage = "was shot by a " + damager.getType().getName() + ".";
                        else deathMessage = "was shot to death.";
                        break;
                    case SUFFOCATION:
                        deathMessage = "couldn't breathe.";
                        break;
                    case LIGHTNING:
                        deathMessage = "was electrocuted.";
                        break;
                    case BLOCK_EXPLOSION:
                    case ENTITY_EXPLOSION:
                        deathMessage = "blew up.";
                        break;
                    case STARVATION:
                        deathMessage = "starved.";
                        break;
                    case WITHER:
                    case ENTITY_ATTACK:
                        if(playerDamager != null) deathMessage = "was killed by " + ChatColor.GRAY + playerDamager.getName() + ".";
                        else if(damager != null) deathMessage = "was killed by a " + damager.getType().getName() + ".";
                        else deathMessage = "was killed by a strange creature.";
                        break;
                    case FALLING_BLOCK:
                        deathMessage = "was smashed to bits by a falling block";
                        break;
                    case POISON:
                    case MAGIC:
                        deathMessage = "fell to witchery.";
                        break;
                    case VOID:
                        deathMessage = "fell into the abyss.";
                        break;
                }
                Bukkit.broadcastMessage(ChatColor.GRAY + player.getName() + " " + ChatColor.BLUE + deathMessage);

                double luck = main.getArena().getLuck(player);
                player.sendMessage("");
                player.sendMessage(ChatColor.RED + "LIFE STATS:");
                player.sendMessage(ChatColor.RED + "* " + ChatColor.YELLOW + "Streak of " + killStreak.getOrDefault(player, 0) + " kills");
                player.sendMessage(ChatColor.RED + "* " + ChatColor.YELLOW + "Total of " + luck + " (" + ((int) Math.floor(luck)) + ") luck");

                if(playerDamager != null) {
                    player.sendMessage(ChatColor.GREEN.toString() + ChatColor.UNDERLINE + "Your killer had " + ChatColor.YELLOW + ChatColor.UNDERLINE +
                            format.format(playerDamager.getHealth() / 2) + " \u2665 " + ChatColor.GREEN + ChatColor.UNDERLINE + "left");
                    player.sendMessage("");

                    main.getArena().addLuck(playerDamager, 0.15D); //.2 luck each kill
                    killStreak.put(playerDamager, killStreak.getOrDefault(playerDamager, 0) + 1);

                    final int kills = killStreak.get(playerDamager);
                    switch(kills) {
                        case 5:
                        case 7:
                            Bukkit.broadcastMessage(ChatColor.GRAY + playerDamager.getName() + ChatColor.BLUE + " IS ON A " + kills + " KILL STREAK!");
                            break;
                        case 10:
                            playerDamager.getWorld().playSound(playerDamager.getLocation(), Sound.ENDERDRAGON_HIT, 2F, 0F);
                            Bukkit.broadcastMessage(ChatColor.GRAY + playerDamager.getName() + ChatColor.YELLOW + " IS ON A " + kills + " KILL STREAK!");
                            break;
                        case 15:
                            playerDamager.getWorld().playSound(playerDamager.getLocation(), Sound.ENDERDRAGON_DEATH, 4F, 1F);
                            Bukkit.broadcastMessage(ChatColor.GRAY + playerDamager.getName() + ChatColor.RED + " IS ON A " + kills + " KILL STREAK! HOLY SH*T!");
                            break;
                    }
                }
            }
        }
    }

}

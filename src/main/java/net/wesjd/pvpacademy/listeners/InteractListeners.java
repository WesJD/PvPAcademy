package net.wesjd.pvpacademy.listeners;

import net.wesjd.pvpacademy.PvPAcademy;
import net.wesjd.pvpacademy.inventory.SpectateInventory;
import net.wesjd.pvpacademy.team.Team;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Optional;

public class InteractListeners implements Listener {

    private final PvPAcademy main;

    public InteractListeners(PvPAcademy main) {
        this.main = main;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        final Player player = e.getPlayer();
        if(e.getAction() != Action.PHYSICAL && e.getItem() != null) {
            final Optional<Team> possibleTeam = main.getTeamRegistry().getTeam(player);
            if(possibleTeam.isPresent()) {
                final Team team = possibleTeam.get();
                if(team.getName().equals("Specs")) {
                    //TODO - handle more items
                    switch(e.getItem().getType()) {
                        case WATCH: //clock
                            main.getArena().play(player);
                            break;
                        case ARROW:
                            player.teleport(main.getMapCenter());
                            player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1F, 1F);
                            break;
                        case ENCHANTED_BOOK:
                            new SpectateInventory(player);
                            break;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e) {
        final Player player = (Player) e.getEntity();
        final Optional<Team> possibleTeam = main.getTeamRegistry().getTeam(player);
        if(possibleTeam.isPresent() && possibleTeam.get().getName().equals("Specs"))
            if(e.getFoodLevel() < player.getFoodLevel())
                e.setCancelled(true);
    }

}

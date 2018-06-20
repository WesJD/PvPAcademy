package net.wesjd.pvpacademy.inventory;

import net.wesjd.pvpacademy.PvPAcademy;
import net.wesjd.pvpacademy.event.PlayerJoinGameEvent;
import net.wesjd.pvpacademy.event.PlayerLeaveGameEvent;
import net.wesjd.pvpacademy.team.TeamRegistry;
import net.wesjd.pvpacademy.util.AbstractInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SpectateInventory extends AbstractInventory {

    private final ListenUp listener = new ListenUp();
    private final TeamRegistry teamRegistry;
    private List<Player> inInventory;

    public SpectateInventory(Player player) {
        super(player, 54, "Spectate Players", true);
        this.teamRegistry = PvPAcademy.get().getTeamRegistry();
        this.inInventory = Bukkit.getOnlinePlayers().stream()
                .filter(plr -> teamRegistry.getTeam(plr).map(team -> team.getName().equals("Tributes")).orElse(false))
                .collect(Collectors.toList());
        super.open();
        Bukkit.getPluginManager().registerEvents(listener, PvPAcademy.get());
    }

    @Override
    protected void build() {
        if(inInventory.isEmpty()) {
            final ItemStack stack = new ItemStack(Material.BARRIER);
            final ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(ChatColor.RED + "No one is playing!");
            stack.setItemMeta(meta);
            set(22, stack);
        } else {
            for(int i=0; i < inInventory.size(); i++) {
                final Player plr = inInventory.get(i);

                final ItemStack stack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                final SkullMeta meta = (SkullMeta) stack.getItemMeta();
                meta.setDisplayName(ChatColor.GREEN + plr.getName());
                meta.setOwner(plr.getName());
                meta.setLore(Arrays.asList(ChatColor.GREEN + "Click to teleport."));
                stack.setItemMeta(meta);

                set(i, stack, (clicker, type) -> {
                    clicker.teleport(plr.getLocation());
                    close();
                });
            }
        }
    }

    @Override
    protected void onClose() {
        HandlerList.unregisterAll(listener);
    }

    private class ListenUp implements Listener {

        @EventHandler
        public void onGameJoin(PlayerJoinGameEvent e) {
            inInventory.add(e.getPlayer());
            build();
        }

        @EventHandler
        public void onGameLeave(PlayerLeaveGameEvent e) {
            inInventory.remove(e.getPlayer());
            build();
        }

    }

}

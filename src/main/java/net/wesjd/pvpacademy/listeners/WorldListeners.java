package net.wesjd.pvpacademy.listeners;

import net.jodah.expiringmap.ExpirationListener;
import net.jodah.expiringmap.ExpiringMap;
import net.wesjd.pvpacademy.PvPAcademy;
import net.wesjd.pvpacademy.team.Team;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class WorldListeners implements Listener {

    private final Map<Block, Material> toRestore = ExpiringMap.builder()
            .expiration(3, TimeUnit.MINUTES)
            .expirationListener((ExpirationListener<Block, Material>) (block, material) -> {
                block.getState().setType(material);
                block.getState().update(true, true);
            })
            .build();
    private final Material[] allowedToBreakAndPlace = new Material[] {
            Material.WEB,
            Material.FLINT_AND_STEEL,
            Material.FIRE
    };
    private final PvPAcademy main;

    public WorldListeners(PvPAcademy main) {
        this.main = main;
    }

    public void restoreAll() {
        toRestore.clear();
    }

    private boolean handleBlockEvent(BlockEvent e, Player player) {
        final Optional<Team> possibleTeam = main.getTeamRegistry().getTeam(player);
        if(possibleTeam.isPresent()) {
            final Team team = possibleTeam.get();
            if(team.getName().equals("Tributes"))
                if(Arrays.stream(allowedToBreakAndPlace).anyMatch(mat -> mat == e.getBlock().getType()))
                    return false;
        }

        if(player.isOp() && player.getGameMode() == GameMode.CREATIVE) return false;

        return true;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        e.setCancelled(handleBlockEvent(e, e.getPlayer()));
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        final Player player = e.getPlayer();
        final boolean allowed = handleBlockEvent(e, player);
        if(allowed && !(player.isOp() && player.getGameMode() == GameMode.CREATIVE)) toRestore.put(e.getBlock(), e.getBlockReplacedState().getType());
        e.setCancelled(allowed);
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent e) {
        e.setCancelled(true);
    }

}

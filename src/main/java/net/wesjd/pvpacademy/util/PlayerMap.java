package net.wesjd.pvpacademy.util;

import net.wesjd.pvpacademy.PvPAcademy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;
import java.util.function.BiConsumer;

public class PlayerMap<V> {

    private final Map<UUID, V> wrapped = new HashMap<>();

    public PlayerMap() {
        Bukkit.getPluginManager().registerEvents(new LeaveListener(), PvPAcademy.get());
    }

    public void put(Player player, V value) {
        wrapped.put(player.getUniqueId(), value);
    }

    public void putIfAbsent(Player player, V value) {
        wrapped.putIfAbsent(player.getUniqueId(), value);
    }

    public void putAll(Map<Player, V> input) {
        final Map<UUID, V> values = new HashMap<>();
        input.forEach((player, value) -> values.put(player.getUniqueId(), value));
        wrapped.putAll(values);
    }

    public V get(Player player) {
        return wrapped.get(player.getUniqueId());
    }

    public V getOrDefault(Player player, V defaultValue) {
        return wrapped.getOrDefault(player.getUniqueId(), defaultValue);
    }

    public void remove(Player player) {
        wrapped.remove(player.getUniqueId());
    }

    public boolean containsKey(Player player) {
        return wrapped.containsKey(player.getUniqueId());
    }

    public boolean containsValue(V value) {
        return wrapped.containsValue(value);
    }

    public Collection<V> values() {
        return wrapped.values();
    }

    public Set<Map.Entry<UUID, V>> entrySet() {
        return wrapped.entrySet();
    }

    public void forEach(BiConsumer<Player, V> action) {
        wrapped.forEach((uuid, value) -> action.accept(Bukkit.getPlayer(uuid), value));
    }

    private class LeaveListener implements Listener {

        @EventHandler
        public void onLeave(PlayerQuitEvent e) {
            remove(e.getPlayer());
        }

    }

}

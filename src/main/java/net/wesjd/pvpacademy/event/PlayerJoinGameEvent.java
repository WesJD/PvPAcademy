package net.wesjd.pvpacademy.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerJoinGameEvent extends PlayerEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    public PlayerJoinGameEvent(Player who) {
        super(who);
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

}

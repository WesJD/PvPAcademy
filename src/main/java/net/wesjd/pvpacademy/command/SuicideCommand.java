package net.wesjd.pvpacademy.command;

import com.google.common.base.Function;
import net.wesjd.pvpacademy.PvPAcademy;
import net.wesjd.pvpacademy.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SuicideCommand extends AbstractCommand {

    public SuicideCommand(PvPAcademy main) {
        super(main);
    }

    @Override
    public void onCmd(Player player, String[] args) {
        final Optional<Team> possibleTeam = super.main.getTeamRegistry().getTeam(player);
        if(possibleTeam.isPresent()) {
            final Team team = possibleTeam.get();
            if(!team.getName().equals("Tributes")) player.sendMessage(ChatColor.RED + "You aren't supposed to die!");
            else {
                player.sendMessage(ChatColor.GRAY + "Goodbye now...");
                final Map<EntityDamageEvent.DamageModifier, Double> modifiers = new HashMap<>();
                modifiers.put(EntityDamageEvent.DamageModifier.BASE, 20D);
                final Map<EntityDamageEvent.DamageModifier, Function<Double, Double>> modifierFunctions = new HashMap<>();
                modifierFunctions.put(EntityDamageEvent.DamageModifier.BASE, input -> null);
                Bukkit.getPluginManager().callEvent(new EntityDamageEvent(player, EntityDamageEvent.DamageCause.SUICIDE, modifiers, modifierFunctions));
            }
        } else player.sendMessage(ChatColor.RED + "wat.");
    }

}

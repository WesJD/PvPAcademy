package net.wesjd.pvpacademy.command;

import net.wesjd.pvpacademy.PvPAcademy;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class AbstractCommand implements CommandExecutor {

    protected final PvPAcademy main;

    public AbstractCommand(PvPAcademy main) {
        this.main = main;
    }

    public abstract void onCmd(Player player, String[] args);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) onCmd((Player) sender, args);
        else sender.sendMessage(ChatColor.RED + "You can't execute this command!");
        return false;
    }

}

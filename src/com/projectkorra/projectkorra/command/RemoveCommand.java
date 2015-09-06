package com.projectkorra.projectkorra.command;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.event.PlayerChangeElementEvent;
import com.projectkorra.projectkorra.event.PlayerChangeElementEvent.Result;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Executor for /bending remove. Extends {@link PKCommand}.
 */
public class RemoveCommand extends PKCommand {

    public RemoveCommand() {
        super("remove", "/bending remove [Player]", "This command will remove the element of the targeted [Player]. The player will be able to re-pick their element after this command is run on them, assuming their Bending was not permaremoved.", new String[]{"remove", "rm"});
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if (!hasPermission(sender) || !correctLength(sender, args.size(), 1, 1)) {
            return;
        }

        Player player = Bukkit.getPlayer(args.get(0));
        if (player == null) {
            sender.sendMessage(ChatColor.RED + "That player is not online.");
            return;
        }

        BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(player.getName());
        if (bPlayer == null) {
            GeneralMethods.createBendingPlayer(player.getUniqueId(), player.getName());
            bPlayer = GeneralMethods.getBendingPlayer(player);
        }
        GeneralMethods.removeUnusableAbilities(player.getName());
        bPlayer.getElements().clear();
        GeneralMethods.saveElements(bPlayer);
        sender.sendMessage(ChatColor.GREEN + "You have removed the bending of " + ChatColor.DARK_AQUA + player.getName());
        player.sendMessage(ChatColor.GREEN + "Your bending has been removed by " + ChatColor.DARK_AQUA + sender.getName());
        Bukkit.getServer().getPluginManager().callEvent(new PlayerChangeElementEvent(sender, player, null, Result.REMOVE));
    }

    /**
     * Checks if the CommandSender has the permission 'bending.admin.remove'. If
     * not, it tells them they don't have permission to use the command.
     *
     * @return True if they have the permission, false otherwise
     */
    @Override
    public boolean hasPermission(CommandSender sender) {
        if (sender.hasPermission("bending.admin." + getName())) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        return false;
    }
}

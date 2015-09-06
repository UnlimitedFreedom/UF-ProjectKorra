package com.projectkorra.projectkorra.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Executor for /bending check. Extends {@link PKCommand}.
 */
public class CheckCommand extends PKCommand {

    public CheckCommand() {
        super("check", "/bending check", "Checks if ProjectKorra is up to date.", new String[]{"check", "chk"});
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if (!hasPermission(sender)) {
            return;
        } else if (args.size() > 0) {
            help(sender, false);
            return;
        }
            sender.sendMessage(ChatColor.YELLOW + "You have the latest version of " + ChatColor.GOLD + "ProjectKorra");
        }
}

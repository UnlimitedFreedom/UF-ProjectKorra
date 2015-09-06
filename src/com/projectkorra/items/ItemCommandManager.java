package com.projectkorra.items;

import com.projectkorra.items.attribute.Attribute;
import com.projectkorra.items.attribute.AttributeList;
import com.projectkorra.items.customs.CustomItem;
import com.projectkorra.items.customs.ItemDisplay;
import com.projectkorra.items.customs.ItemEquip;
import com.projectkorra.projectkorra.ProjectKorra;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;

public class ItemCommandManager {

    public ItemCommandManager() {
        init();
    }

    /**
     * Any command that relates to "/bending items" will be ran through this new
     * CommandExecutor. It displays any help information related to a specific
     * command, opens up display inventories, and gives items to specific
     * players.
     */
    public void init() {
        CommandExecutor exe = new CommandExecutor() {
            @SuppressWarnings("deprecation")
            @Override
            public boolean onCommand(CommandSender s, Command c, String label, String[] args) {
                if (args.length >= 1 && aliasChecker(args[0], Messages.RELOAD_ALIAS)) {
                    if (!s.hasPermission("bendingitems.command.reload")) {
                        s.sendMessage(Messages.NO_PERM);
                        return true;
                    }

                    new ItemConfigManager();
                    s.sendMessage(Messages.CONFIG_RELOADED);
                    return true;
                } /* GIVING */ else if (args.length >= 1 && aliasChecker(args[0], Messages.GIVE_ALIAS)) {
                    if (!s.hasPermission("bendingitems.command.give")) {
                        s.sendMessage(Messages.NO_PERM);
                        return true;
                    } else if (args.length == 1) {
                        s.sendMessage(Messages.GIVE_USAGE);
                        return true;
                    } else if (args.length >= 2 && aliasChecker(args[1], Messages.DISPLAY_ALIAS)) {
                        s.sendMessage(ChatColor.YELLOW + " ---- " + ChatColor.GOLD + "Item Names " + ChatColor.YELLOW + "----");
                        for (CustomItem citem : CustomItem.itemList) {
                            s.sendMessage(ChatColor.YELLOW + citem.getName());
                        }
                        return true;
                    } else if (args.length == 2 && !(s instanceof Player)) {
                        s.sendMessage(Messages.PLAYER_ONLY);
                        return true;
                    }

                    Player player = null;
                    CustomItem citem = null;
                    int qty = 0;
                    if (args.length == 2) {
                        citem = CustomItem.getCustomItem(args[1]);
                        player = (Player) s;
                    } else if (args.length == 3) {
                        citem = CustomItem.getCustomItem(args[1]);
                        player = ProjectKorra.plugin.getServer().getPlayer(args[2]);
                        if (player == null) {
                            player = (Player) s;
                            try {
                                qty = Integer.parseInt(args[2]);
                            } catch (Exception e) {
                                // If the qty wasn't parsable then they were attemping to send a
                                // name
                                s.sendMessage(Messages.INVALID_PLAYER);
                                return true;
                            }
                        }
                    } else if (args.length >= 4) {
                        citem = CustomItem.getCustomItem(args[1]);
                        player = ProjectKorra.plugin.getServer().getPlayer(args[2]);
                        try {
                            qty = Integer.parseInt(args[3]);
                        } catch (Exception e) {
                        }
                    }

                    if (player == null) {
                        s.sendMessage(Messages.INVALID_PLAYER);
                        return true;
                    } else if (citem == null) {
                        s.sendMessage(Messages.ITEM_NOT_FOUND);
                        return true;
                    }

                    if (qty < 1) {
                        qty = citem.getQuantity();
                    }
                    ItemStack istack = citem.generateItem();
                    istack.setAmount(qty);
                    player.getInventory().addItem(istack);
                    return true;
                } /*
                 * Stats "bi stats" lets a user view all of the stats that are
                 * currently in existence for their version of the plugin. They
                 * can also filter out search options by using
                 * "bi stats <filter>".
                 */ else if (args.length >= 1 && aliasChecker(args[0], Messages.STATS_ALIAS)) {
                    if (!s.hasPermission("bendingitems.command.stats")) {
                        s.sendMessage(Messages.NO_PERM);
                        return true;
                    } else if (args.length >= 1) {
                        int page = 1;
                        String phrase = null;
                        if (args.length == 2) {
                            try {
                                page = Integer.parseInt(args[1]);
                            } catch (Exception e) {
                                phrase = args[1];
                            }
                        } else if (args.length >= 3) {
                            phrase = args[1];
                            try {
                                page = Integer.parseInt(args[2]);
                            } catch (Exception e) {
                            }
                        }

                        ArrayList<Attribute> attribs = new ArrayList<Attribute>(AttributeList.ATTRIBUTES);
                        if (phrase != null) {
                            for (int i = 0; i < attribs.size(); i++) {
                                Attribute att = attribs.get(i);
                                if (!att.getName().toLowerCase().contains(phrase.toLowerCase())) {
                                    attribs.remove(i);
                                    i--;
                                }
                            }
                        }

                        int maxPage = (attribs.size() - 1) / Messages.LINES_PER_PAGE;
                        page -= 1;
                        page = page < 0 ? 0 : page;
                        page = page > maxPage ? maxPage : page;
                        int pageIndex = page * Messages.LINES_PER_PAGE;
                        s.sendMessage(ChatColor.YELLOW + " ---- " + ChatColor.GOLD + "Stats " + ChatColor.YELLOW + "-- " + ChatColor.GOLD + "Page " + ChatColor.RED + (page + 1) + ChatColor.GOLD + "/" + ChatColor.RED + (maxPage + 1) + ChatColor.YELLOW + " ----");
                        for (int i = pageIndex; i < pageIndex + Messages.LINES_PER_PAGE; i++) {
                            if (i >= 0 && i < attribs.size()) {
                                Attribute att = attribs.get(i);
                                s.sendMessage(ChatColor.YELLOW + att.getName() + ": " + ChatColor.WHITE + att.getDesc());
                            } else {
                                break;
                            }
                        }
                        if (phrase != null) {
                            s.sendMessage(ChatColor.GOLD + "Phrase: " + ChatColor.RED + phrase);
                        }
                        return true;
                    }
                } /* ITEMS */ else if (args.length >= 1 && aliasChecker(args[0], Messages.ITEMS_ALIAS)) {
                    if (!s.hasPermission("bendingitems.command.items")) {
                        s.sendMessage(Messages.NO_PERM);
                        return true;
                    } else if (!(s instanceof Player)) {
                        s.sendMessage(Messages.PLAYER_ONLY);
                        return true;
                    }

                    Player player = (Player) s;
                    boolean showStats = false;
                    if (args.length >= 2 && aliasChecker(args[1], Messages.STATS_ALIAS)) {
                        if (!s.hasPermission("bendingitems.command.items.stats")) {
                            s.sendMessage(Messages.NO_PERM);
                            return true;
                        }
                        showStats = true;
                    }
                    new ItemDisplay(player, showStats);
                    return true;
                } /*
                 * EQUIP "bi equip" lets a user "equip" a custom item. Whenever
                 * a player switches slots while holding the item then the item
                 * will continue to follow the player's slot, as long as the
                 * slot is empty.
                 */ else if (args.length >= 1 && aliasChecker(args[0], Messages.EQUIP_ALIAS)) {
                    if (!s.hasPermission("bendingitems.command.equip")) {
                        s.sendMessage(Messages.NO_PERM);
                        return true;
                    } else if (!(s instanceof Player)) {
                        s.sendMessage(Messages.PLAYER_ONLY);
                        return true;
                    }

                    Player player = (Player) s;
                    new ItemEquip(player);
                    return true;
                }

                s.sendMessage(Messages.USAGE);
                return true;
            }
        };
        ProjectKorra.plugin.getCommand("projectkorraitems").setExecutor(exe);
    }

    /**
     * Determines if a String is contained within the array.
     *
     * @param s the string to check
     * @param alias an array of alias
     * @return true if s was in alias
     */
    public boolean aliasChecker(String s, String[] alias) {
        for (String ali : alias) {
            if (s.equalsIgnoreCase(ali)) {
                return true;
            }
        }
        return false;
    }
}

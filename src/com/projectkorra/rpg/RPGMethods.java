package com.projectkorra.rpg;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.airbending.AirMethods;
import com.projectkorra.projectkorra.chiblocking.ChiMethods;
import com.projectkorra.projectkorra.earthbending.EarthMethods;
import com.projectkorra.projectkorra.firebending.FireMethods;
import com.projectkorra.projectkorra.storage.DBConnection;
import com.projectkorra.projectkorra.waterbending.WaterMethods;

public class RPGMethods {

    static ProjectKorra plugin;

    public RPGMethods(ProjectKorra plugin) {
        RPGMethods.plugin = plugin;
    }

    public static boolean isSozinsComet(World world) {
        WorldEvents comet = WorldEvents.SozinsComet;
        if (!getEnabled(WorldEvents.SozinsComet)) {
            return false;
        }
        int freq = getFrequency(comet);

        long days = world.getFullTime() / 24000;
        if (days % freq == 0) {
            return true;
        }
        return false;
    }

    public static boolean isLunarEclipse(World world) {
        WorldEvents eclipse = WorldEvents.LunarEclipse;
        if (!getEnabled(eclipse)) {
            return false;
        }
        int freq = getFrequency(eclipse);

        long days = world.getFullTime() / 24000;
        if (days % freq == 0) {
            return true;
        }
        return false;
    }

    public static boolean isSolarEclipse(World world) {
        WorldEvents eclipse = WorldEvents.SolarEclipse;
        if (!getEnabled(eclipse)) {
            return false;
        }
        int freq = getFrequency(eclipse);

        long days = world.getFullTime() / 24000;
        if (days % freq == 0) {
            return true;
        }
        return false;
    }

    public static boolean getEnabled(WorldEvents we) {
        return ProjectKorra.plugin.getConfig().getBoolean("WorldEvents." + we.toString() + ".Enabled");
    }

    public static int getFrequency(WorldEvents we) {
        return ProjectKorra.plugin.getConfig().getInt("WorldEvents." + we.toString() + ".Frequency");
    }

    public static double getFactor(WorldEvents we) {
        return ProjectKorra.plugin.getConfig().getDouble("WorldEvents." + we.toString() + ".Factor");
    }

    public static void randomAssign(BendingPlayer player) {
        double rand = Math.random();
        double earthchance = ProjectKorra.plugin.getConfig().getDouble("ElementAssign.Percentages.Earth");
        double firechance = ProjectKorra.plugin.getConfig().getDouble("ElementAssign.Percentages.Fire");
        double airchance = ProjectKorra.plugin.getConfig().getDouble("ElementAssign.Percentages.Air");
        double waterchance = ProjectKorra.plugin.getConfig().getDouble("ElementAssign.Percentages.Water");
        double chichance = ProjectKorra.plugin.getConfig().getDouble("ElementAssign.Percentages.Chi");

        if (ProjectKorra.plugin.getConfig().getBoolean("ElementAssign.Enabled")) {
            if (rand < earthchance) {
                assignElement(player, Element.Earth, false);
                return;
            }

            if (rand < waterchance + earthchance) {
                assignElement(player, Element.Water, false);
                return;
            }

            if (rand < airchance + waterchance + earthchance) {
                assignElement(player, Element.Air, false);
                return;
            }

            if (rand < firechance + airchance + waterchance + earthchance) {
                assignElement(player, Element.Fire, false);
                return;
            }

            if (rand < chichance + firechance + airchance + waterchance + earthchance) {
                assignElement(player, Element.Chi, true);
                return;
            }

            String defaultElement = ProjectKorra.plugin.getConfig().getString("ElementAssign.Default");
            Element e = Element.Earth;

            if (defaultElement.equalsIgnoreCase("Chi")) {
                assignElement(player, Element.Chi, true);
                return;
            }

            if (defaultElement.equalsIgnoreCase("Water")) {
                e = Element.Water;
            }
            if (defaultElement.equalsIgnoreCase("Earth")) {
                e = Element.Earth;
            }
            if (defaultElement.equalsIgnoreCase("Fire")) {
                e = Element.Fire;
            }
            if (defaultElement.equalsIgnoreCase("Air")) {
                e = Element.Air;
            }

            assignElement(player, e, false);
            return;
        }
    }

    private static void assignElement(BendingPlayer player, Element e, Boolean chiblocker) {
        player.setElement(e);
        GeneralMethods.saveElements(player);
        if (!chiblocker) {
            if (e.toString().equalsIgnoreCase("Earth")) {
                Bukkit.getPlayer(player.getUUID()).sendMessage(ChatColor.RED + "You have been born as an " + EarthMethods.getEarthColor() + e.toString() + "bender!");
            }
            if (e.toString().equalsIgnoreCase("Fire")) {
                Bukkit.getPlayer(player.getUUID()).sendMessage(ChatColor.RED + "You have been born as a " + FireMethods.getFireColor() + e.toString() + "bender!");
            }
            if (e.toString().equalsIgnoreCase("Water")) {
                Bukkit.getPlayer(player.getUUID()).sendMessage(ChatColor.RED + "You have been born as a " + WaterMethods.getWaterColor() + e.toString() + "bender!");
            }
            if (e.toString().equalsIgnoreCase("Air")) {
                Bukkit.getPlayer(player.getUUID()).sendMessage(ChatColor.RED + "You have been born as an " + AirMethods.getAirColor() + e.toString() + "bender!");
            }
        } else {
            Bukkit.getPlayer(player.getUUID()).sendMessage(ChatColor.RED + "You have been raised as a " + ChiMethods.getChiColor() + e.toString() + "blocker!");
        }
    }

    public static void setAvatar(UUID uuid) {
        plugin.getConfig().set("Avatar.Current", uuid.toString());
        plugin.saveConfig();
        Player player = Bukkit.getPlayer(uuid);
        BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(player.getName());
        String element = "none";
        if (bPlayer.getElements().contains(Element.Air)) {
            element = "air";
        }
        if (bPlayer.getElements().contains(Element.Water)) {
            element = "water";
        }
        if (bPlayer.getElements().contains(Element.Earth)) {
            element = "earth";
        }
        if (bPlayer.getElements().contains(Element.Fire)) {
            element = "fire";
        }
        if (bPlayer.getElements().contains(Element.Chi)) {
            element = "chi";
        }

        /*
         * Gives them the elements
         */
        if (!bPlayer.getElements().contains(Element.Air)) {
            bPlayer.addElement(Element.Air);
        }
        if (!bPlayer.getElements().contains(Element.Water)) {
            bPlayer.addElement(Element.Water);
        }
        if (!bPlayer.getElements().contains(Element.Earth)) {
            bPlayer.addElement(Element.Earth);
        }
        if (!bPlayer.getElements().contains(Element.Fire)) {
            bPlayer.addElement(Element.Fire);
        }

        DBConnection.sql.modifyQuery("INSERT INTO pk_avatars (uuid, player, element) VALUES ('" + uuid.toString() + "', '" + player.getName() + "', '" + element + "')");
    }

    public static boolean isCurrentAvatar(UUID uuid) {
        String currAvatar = plugin.getConfig().getString("Avatar.Current");
        if (currAvatar == null) {
            return false;
        }
        UUID uuid2 = UUID.fromString(currAvatar);
        if (uuid.toString().equalsIgnoreCase(uuid2.toString())) {
            return true;
        }
        return false;
    }

    public static boolean hasBeenAvatar(UUID uuid) {
        if (isCurrentAvatar(uuid)) {
            return true;
        }
        ResultSet rs2 = DBConnection.sql.readQuery("SELECT * FROM pk_avatars WHERE uuid = '" + uuid.toString() + "'");
        try {
            if (rs2.next()) {
                return true;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}

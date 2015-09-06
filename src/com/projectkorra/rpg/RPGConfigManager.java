package com.projectkorra.rpg;

import com.projectkorra.projectkorra.ProjectKorra;
import org.bukkit.configuration.file.FileConfiguration;

public class RPGConfigManager {

    static ProjectKorra plugin;

    public RPGConfigManager(ProjectKorra plugin) {
        RPGConfigManager.plugin = plugin;
        configCheck();
    }

    public static void configCheck() {
        FileConfiguration config = ProjectKorra.plugin.getConfig();

        config.addDefault("WorldEvents.SolarEclipse.Enabled", true);
        config.addDefault("WorldEvents.SolarEclipse.Frequency", 20);
        config.addDefault("WorldEvents.SolarEclipse.Factor", 0.0);
        config.addDefault("WorldEvents.SolarEclipse.Element", "Fire");
        config.addDefault("WorldEvents.SolarEclipse.Time", "Day");

        config.addDefault("WorldEvents.LunarEclipse.Enabled", true);
        config.addDefault("WorldEvents.LunarEclipse.Frequency", 40);
        config.addDefault("WorldEvents.LunarEclipse.Factor", 0.0);
        config.addDefault("WorldEvents.LunarEclipse.Element", "Water");
        config.addDefault("WorldEvents.LunarEclipse.Time", "Night");

        config.addDefault("WorldEvents.SozinsComet.Enabled", true);
        config.addDefault("WorldEvents.SozinsComet.Frequency", 100);
        config.addDefault("WorldEvents.SozinsComet.Factor", 5.0);
        config.addDefault("WorldEvents.SozinsComet.Element", "Fire");
        config.addDefault("WorldEvents.SozinsComet.Time", "Day");

        config.addDefault("Abilities.AvatarStateOnFinalBlow", true);

        config.addDefault("ElementAssign.Enabled", false);
        config.addDefault("ElementAssign.Default", "Earth");
        config.addDefault("ElementAssign.Percentages.Earth", 0.205);
        config.addDefault("ElementAssign.Percentages.Water", 0.205);
        config.addDefault("ElementAssign.Percentages.Air", 0.205);
        config.addDefault("ElementAssign.Percentages.Fire", 0.205);
        config.addDefault("ElementAssign.Percentages.Earth", 0.18);

        config.addDefault("Storage.engine", "sqlite");

        config.addDefault("Storage.MySQL.host", "localhost");
        config.addDefault("Storage.MySQL.port", 3306);
        config.addDefault("Storage.MySQL.pass", "");
        config.addDefault("Storage.MySQL.db", "minecraft");
        config.addDefault("Storage.MySQL.user", "root");

        config.options().copyDefaults(true);
        plugin.saveConfig();
    }

}

package com.projectkorra.projectkorra;

import com.projectkorra.items.ItemCommandManager;
import com.projectkorra.items.ItemConfigManager;
import com.projectkorra.items.abilityupdater.AbilityUpdater;
import com.projectkorra.items.attribute.AttributeListener;
import com.projectkorra.items.customs.ItemDisplay;
import com.projectkorra.items.customs.ItemListener;
import com.projectkorra.projectkorra.ability.AbilityModuleManager;
import com.projectkorra.projectkorra.ability.combo.ComboManager;
import com.projectkorra.projectkorra.ability.combo.ComboModuleManager;
import com.projectkorra.projectkorra.ability.multiability.MultiAbilityManager;
import com.projectkorra.projectkorra.ability.multiability.MultiAbilityModuleManager;
import com.projectkorra.projectkorra.airbending.AirbendingManager;
import com.projectkorra.projectkorra.chiblocking.ChiblockingManager;
import com.projectkorra.projectkorra.command.Commands;
import com.projectkorra.projectkorra.configuration.ConfigManager;
import com.projectkorra.projectkorra.earthbending.EarthbendingManager;
import com.projectkorra.projectkorra.firebending.FirebendingManager;
import com.projectkorra.projectkorra.storage.DBConnection;
import com.projectkorra.projectkorra.util.MetricsLite;
import com.projectkorra.projectkorra.util.RevertChecker;
import com.projectkorra.projectkorra.util.Updater;
import com.projectkorra.projectkorra.util.logging.PKLogHandler;
import com.projectkorra.projectkorra.waterbending.WaterbendingManager;
import com.projectkorra.rpg.RPGConfigManager;
import com.projectkorra.rpg.RPGListener;
import com.projectkorra.rpg.RPGMethods;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import me.StevenLawson.ProjectKorra.ProjectKorraHandler;
import org.bukkit.plugin.PluginDescriptionFile;

@SuppressWarnings("NonConstantLogger")
public class ProjectKorra extends JavaPlugin {

    public static ProjectKorra plugin;
    public static Logger log;
    public static PKLogHandler handler;
    public static long time_step = 1;
    public Updater updater;
    public AbilityModuleManager abManager;
    
    @Override
    public void onLoad() {
        ProjectKorra.plugin = this;
        ProjectKorra.log = this.getLogger();
        ProjectKorraHandler.setLogger(log);
    }

    @Override
    @SuppressWarnings({"ResultOfObjectAllocationIgnored", "CallToPrintStackTrace", "LoggerStringConcat"})
    public void onEnable() {
        try {
            File logFolder = new File(getDataFolder(), "Logs");
            if (!logFolder.exists()) {
                logFolder.mkdirs();
            }
            handler = new PKLogHandler(logFolder + File.separator + "ERROR.%g.log");
            log.getParent().addHandler(handler);
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
        new ConfigManager(this);
        new RPGConfigManager(this);
        new ItemCommandManager();
        new ItemConfigManager();
        new GeneralMethods(this);
        new RPGMethods(this);
        new Commands(this);
        abManager = new AbilityModuleManager(this);
        new MultiAbilityModuleManager();
        new MultiAbilityManager();
        new ComboModuleManager();
        new ComboManager();
        
        RPGConfigManager.configCheck();

        DBConnection.host = plugin.getConfig().getString("Storage.MySQL.host");
        DBConnection.port = plugin.getConfig().getInt("Storage.MySQL.port");
        DBConnection.pass = plugin.getConfig().getString("Storage.MySQL.pass");
        DBConnection.db = plugin.getConfig().getString("Storage.MySQL.db");
        DBConnection.user = plugin.getConfig().getString("Storage.MySQL.user");
        DBConnection.init();
        if (DBConnection.isOpen() == false) {
            //Message is logged by DBConnection
            return;
        }
        
        plugin.getServer().getPluginManager().registerEvents(new ItemListener(), this);
        plugin.getServer().getPluginManager().registerEvents(new AttributeListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new RPGListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PKListener(this), this);
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this, new BendingManager(), 0, 1);
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this, new AirbendingManager(this), 0, 1);
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this, new WaterbendingManager(this), 0, 1);
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this, new EarthbendingManager(this), 0, 1);
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this, new FirebendingManager(this), 0, 1);
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this, new ChiblockingManager(this), 0, 1);
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(this, new RevertChecker(this), 0, 200);

        for (Player player : Bukkit.getOnlinePlayers()) {
            GeneralMethods.createBendingPlayer(player.getUniqueId(), player.getName());
        }
        
        AbilityUpdater.startUpdater();
        AbilityUpdater.startCleanup();

        try {
            MetricsLite metrics = new MetricsLite(this);
            metrics.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        GeneralMethods.deserializeFile();
        GeneralMethods.startCacheCleaner(GeneralMethods.CACHE_TIME);
        PluginDescriptionFile pdfFile = this.getDescription();
        log.info(pdfFile.getName() + " Version " + pdfFile.getVersion() + " has been enabled!");

    }

    @Override
    @SuppressWarnings("LoggerStringConcat")
    public void onDisable() {
        GeneralMethods.stopBending();
        if (DBConnection.isOpen != false) {
            DBConnection.sql.close();
        }
        handler.close();
        PluginDescriptionFile pdfFile = this.getDescription();
        log.info(pdfFile.getName() + " has been disabled!");
        ItemDisplay.cleanup();
    }

}

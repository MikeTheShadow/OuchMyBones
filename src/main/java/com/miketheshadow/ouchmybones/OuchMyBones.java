package com.miketheshadow.ouchmybones;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class OuchMyBones extends JavaPlugin {

    public static FileConfiguration configuration;

    public static OuchMyBones INSTANCE;

    public static FileConfiguration dataStorage;

    @Override
    public void onEnable() {
        INSTANCE = this;
        if(!loadConfig()) {
            Bukkit.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        loadBackupFile();
        DataStorage.loadInjuredPlayers();
        (new ConstantAilments()).runTaskTimer(this,1,1);
        this.getServer().getPluginManager().registerEvents(new InteractEvents(),this);
        this.getServer().getPluginCommand("injure").setExecutor(new InjureCommand());
        this.getServer().getPluginCommand("injure").setTabCompleter(new InjureTabComplete());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private static File fileConfig;

    public static File databaseFile;

    public void loadBackupFile() {
        databaseFile = new File(getDataFolder().getAbsolutePath() + "\\dataStorage.yml");
        if(!databaseFile.exists()) {
            try {
                databaseFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        dataStorage = YamlConfiguration.loadConfiguration(databaseFile);
    }

    public boolean loadConfig() {
        fileConfig = new File(getDataFolder().getAbsolutePath() + "\\config.yml");
        if(!fileConfig.exists()){

            try {
                if(!getDataFolder().mkdir() || !fileConfig.createNewFile()) {
                    Bukkit.getServer().getConsoleSender().sendMessage("Failed to create config! Stopping...");
                    this.getServer().getPluginManager().disablePlugin(this);
                    return false;
                }
                setupConfig();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        configuration = this.getConfig();
        return true;
    }

    public void setupConfig() throws IOException {
        FileConfiguration config = this.getConfig();
        config.set("broken_arm_message_right","Your right arm is broken!");
        config.set("broken_arm_message_left","Your left arm is broken!");
        config.set("missing_arm_message_right","Your right arm is missing!");
        config.set("missing_arm_message_left","Your left arm is missing!");
        config.set("missing_leg_message","Your leg is missing!");
        config.set("broken_leg_message","Your leg is broken!");
        config.set("missing_both_legs_message","Your legs are missing!");
        config.set("blindness_activated","You have been blinded!");
        config.set("headache_reminder","You have a nasty headache!");
        config.set("concussion_reminder","You have a nasty concussion!");
        config.set("maimed_reminder","You have been maimed and cannot place blocks!");
        config.set("dizzy_duration_seconds",10);
        config.save(fileConfig);
    }

}

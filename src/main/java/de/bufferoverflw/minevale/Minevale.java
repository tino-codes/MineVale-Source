package de.bufferoverflw.minevale;

import de.bufferoverflw.minevale.commands.BreakBlockCommand;
import de.bufferoverflw.minevale.commands.HoloCommand;
import de.bufferoverflw.minevale.commands.SignCommand;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class Minevale extends JavaPlugin {

    private static Minevale instance;
    private static File holoFile;
    private static FileConfiguration holoConfig;
    private static File cooldownFile;
    private static FileConfiguration cooldownConfig;


    public static Minevale getInstance() {
        return instance;
    }

    public static FileConfiguration getHoloConfig() {
        return holoConfig;
    }

    public static void saveHoloConfig() {
        try {
            holoConfig.save(holoFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static FileConfiguration getCooldownConfig() {
        return cooldownConfig;
    }

    public static void saveCooldownConfig() {
        try {
            cooldownConfig.save(cooldownFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        holoFile = new File("./plugins/Minevale", "holos.yml");
        cooldownFile = new File("./plugins/Minevale", "cooldowns.yml");

        if (!cooldownFile.exists()) {
            saveResource("cooldowns.yml", false);
        }

        cooldownConfig = YamlConfiguration.loadConfiguration(cooldownFile);

        if (!this.holoFile.exists()) {

            saveResource("holos.yml", false);

        }

        holoConfig = YamlConfiguration.loadConfiguration(this.holoFile);

        if (Bukkit.getPluginManager().getPlugin("PlotSquared") == null) {

            this.getLogger().warning("PlotSquared muss installiert sein!");

            this.getPluginLoader().disablePlugin(this);
        }

        this.getLogger().info("Das Plugin PlotSquared ist auf dem Server.");

        getCommand("breakblock").setExecutor(new BreakBlockCommand());
        getCommand("holo").setExecutor(new HoloCommand());
        getCommand("sign").setExecutor(new SignCommand());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

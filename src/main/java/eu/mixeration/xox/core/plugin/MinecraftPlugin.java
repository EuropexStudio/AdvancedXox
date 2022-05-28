
package eu.mixeration.xox.core.plugin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public abstract class MinecraftPlugin extends JavaPlugin {
    public abstract void registerHandlers();
    public abstract void registerCommands();

    public abstract void registerEvents();
    public abstract void setInstance();

    @Override
    public void onLoad() {
        setInstance();

        // Setup data folder
        if (!this.getDataFolder().exists()) {
            if (!this.getDataFolder().mkdir()) {
                Bukkit.getLogger().log(Level.SEVERE, "[XOX] Unable to create configuration folder!");
                Bukkit.getPluginManager().disablePlugin(this);
            }
        }

        PluginDescriptionFile pluginDescriptionFile = this.getDescription();
        Bukkit.getLogger().log(Level.INFO, String.format("[XOX] Loaded %s version %s by Mixeration.", pluginDescriptionFile.getName(), pluginDescriptionFile.getVersion()));
    }

    @Override
    public void onEnable() {
        registerHandlers();
        registerEvents();
        registerCommands();
    }

    @Override
    public abstract void onDisable();
}

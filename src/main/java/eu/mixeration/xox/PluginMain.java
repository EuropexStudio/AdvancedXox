package eu.mixeration.xox;

import eu.mixeration.xox.command.Xox;
import eu.mixeration.xox.core.plugin.MinecraftPlugin;
import eu.mixeration.xox.core.plugin.config.BoardsConfig;
import eu.mixeration.xox.core.plugin.config.Lobby_Config;
import eu.mixeration.xox.core.plugin.config.MainConfig;
import eu.mixeration.xox.core.plugin.config.Message_Config;
import eu.mixeration.xox.core.util.ActionBarUtil;
import eu.mixeration.xox.event.PlayerActionEvents;
import eu.mixeration.xox.event.PlayerBlockEvents;
import eu.mixeration.xox.event.PlayerMoveEvents;
import eu.mixeration.xox.event.PlayerStateEvents;
import eu.mixeration.xox.handler.BoardHandler;
import eu.mixeration.xox.handler.ConfigHandler;
import eu.mixeration.xox.handler.GameHandler;
import eu.mixeration.xox.handler.MenuHandler;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.Objects;
import java.util.logging.Level;

@Getter
public final class PluginMain extends MinecraftPlugin {
    private Message_Config _lang = new Message_Config(this, "messages.yml");
    private Lobby_Config _Str = new Lobby_Config(this, "storage.yml");

    @Getter
    private static PluginMain instance;

    private ConfigHandler configHandler;
    private GameHandler gameHandler;
    private BoardHandler boardHandler;
    private MenuHandler menuHandler;

    private ActionBarUtil actionBarUtil;

    @Override
    public void registerHandlers() {
        actionBarUtil = new ActionBarUtil();
        actionBarUtil.runLoop();

        configHandler = new ConfigHandler();
        gameHandler = new GameHandler();
        boardHandler = new BoardHandler();
        menuHandler = new MenuHandler();

        configHandler.registerConfiguration(new MainConfig());
        configHandler.registerConfiguration(new BoardsConfig());
        configHandler.loadConfigurations();

        boardHandler.loadBoards();
        gameHandler.generateGames();

        _lang.create();
        _Str.create();
    }

    @Override
    public void registerCommands() {
        Objects.requireNonNull(this.getCommand("Xox")).setExecutor(new Xox(this));
    }

    @Override
    public void registerEvents() {
        this.getServer().getPluginManager().registerEvents(new PlayerStateEvents(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerActionEvents(this), this);

        if (MainConfig.getConfig().isBlockProtection()) {
            Bukkit.getLogger().log(Level.INFO, "[XOX] Enabling block protection...");
            this.getServer().getPluginManager().registerEvents(new PlayerBlockEvents(this), this);
        }

        if (MainConfig.getConfig().isPlayerMoveEvents()) {
            Bukkit.getLogger().log(Level.INFO, "[XOX] Enabling player movement events...");
            this.getServer().getPluginManager().registerEvents(new PlayerMoveEvents(this), this);
        }
    }

    @Override
    public void setInstance() {
        instance = this;
    }

    @Override
    public void onDisable() {

    }

}

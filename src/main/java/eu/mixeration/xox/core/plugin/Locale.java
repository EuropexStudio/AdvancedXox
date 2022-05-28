

package eu.mixeration.xox.core.plugin;

import eu.mixeration.xox.core.plugin.config.Message_Config;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Locale {
    public static final String PLUGIN_PREFIX = Message_Config.getConfig().getString("messages.prefix");
    public static final String RELOADED = Message_Config.getConfig().getString("messages.message-file-reloaded");

    public static final String COMMAND_VERSION = Message_Config.getConfig().getString("messages.command.version");
    public static final String COMMAND_HELP_TITLE = Message_Config.getConfig().getString("messages.command.help");
    public static final String COMMAND_HELP_ENTRY = Message_Config.getConfig().getString("messages.command.entry");

    public static final String COMMAND_BOARD_CREATE_CANCEL = Message_Config.getConfig().getString("messages.board.cancelled");
    public static final String COMMAND_BOARD_CREATE_START_L1 = Message_Config.getConfig().getString("messages.board.create.line-1");
    public static final String COMMAND_BOARD_CREATE_START_L2 = Message_Config.getConfig().getString("messages.board.create.line-2");
    public static final String COMMAND_BOARD_LIST_TITLE = Message_Config.getConfig().getString("messages.board.create.list-title");
    public static final String COMMAND_BOARD_LIST_VALUE = Message_Config.getConfig().getString("messages.board.create.list-value");

    public static final String EVENT_GAME_JOIN = Message_Config.getConfig().getString("messages.event.join");
    public static final String EVENT_GAME_LEAVE = Message_Config.getConfig().getString("messages.event.left");
    public static final String EVENT_GAME_START = Message_Config.getConfig().getString("messages.event.start");
    public static final String EVENT_GAME_WAITING = Message_Config.getConfig().getString("messages.event.waiting");
    public static final String EVENT_GAME_WINNER = Message_Config.getConfig().getString("messages.event.winner");

    public static final String ACTIONBAR_GAME_STATUS = Message_Config.getConfig().getString("messages.action-bar-prefix");

    public static final String ERROR_PREFIX = Message_Config.getConfig().getString("messages.error.prefix");
    public static final String ERROR_PERMISSION_DENIED = ERROR_PREFIX + Message_Config.getConfig().getString("messages.error.no-permission");
    public static final String ERROR_NOT_PLAYER = ERROR_PREFIX + Message_Config.getConfig().getString("messages.error.must-be-player");
    public static final String ERROR_BOARD_CREATE = ERROR_PREFIX + Message_Config.getConfig().getString("messages.error.board-create-cancelled");
    public static final String ERROR_BLOCK_BREAK_EVENT_DENY = ERROR_PREFIX + Message_Config.getConfig().getString("messages.error.event-cancelled");
    public static final String ERROR_BLOCK_PLACE_EVENT_DENY = ERROR_PREFIX + Message_Config.getConfig().getString("messages.error.event-cancelled");
    public static final String ERROR_BOARD_REMOVE = ERROR_PREFIX + Message_Config.getConfig().getString("messages.error.board-not-found");
    public static final String ERROR_NOT_IN_GAME = ERROR_PREFIX + Message_Config.getConfig().getString("messages.error.not-in-game");
    public static final String ERROR_GAME_JOIN_FAIL = ERROR_PREFIX + Message_Config.getConfig().getString("messages.error.unable-to-join");
    public static final String ERROR_COLOUR_SELECT_IN_USE = ERROR_PREFIX + Message_Config.getConfig().getString("messages.error.other-player");
    public static final String ERROR_NOT_YOUR_TURN = ERROR_PREFIX + Message_Config.getConfig().getString("messages.error.not-your-turn");

    public static final String SUCCESS_PREFIX = Message_Config.getConfig().getString("messages.success.prefix");
    public static final String SUCCESS_BOARD_CREATE = SUCCESS_PREFIX + Message_Config.getConfig().getString("messages.success.board-created");
    public static final String SUCCESS_BOARD_REMOVE_SUCCESS = SUCCESS_PREFIX + Message_Config.getConfig().getString("messages.success.board-removed");
    public static final String SUCCESS_COLOUR_SELECTED = SUCCESS_PREFIX + Message_Config.getConfig().getString("messages.success.color-selected");

    public static final String GAMESTATE_NONE = Message_Config.getConfig().getString("messages.game-stats.none");
    public static final String GAMESTATE_WAITING = Message_Config.getConfig().getString("messages.game-stats.waiting");
    public static final String GAMESTATE_INGAME = Message_Config.getConfig().getString("messages.game-stats.in-game");
    public static final String GAMESTATE_END = Message_Config.getConfig().getString("messages.game-stats.end");

    public static final String GAMESTATE_WAITING_DESCRIPTION = Message_Config.getConfig().getString("messages.game-stats.waiting-for-players");
    public static final String GAMESTATE_INGAME_DESCRIPTION = Message_Config.getConfig().getString("messages.game-stats.current-turn");
    public static final String GAMESTATE_END_DESCRIPTION = Message_Config.getConfig().getString("messages.game-stats.winner");

    public static final String MENU_COLOURSELECTION_TITLE = Message_Config.getConfig().getString("messages.menu.choose-your-color");


    /**
     * Send a message to a CommandSender.
     * If sender is a Player, the message will be sent with the plugin's message prefix.
     *
     * @param sender CommandSender the entity to send the message to.
     * @param message String the message to send.
     */
    public static void sendMessage(CommandSender sender, String message) {
        if (sender instanceof Player) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',PLUGIN_PREFIX + message));
        } else {
            sender.sendMessage(ChatColor.stripColor(message));
        }
    }
    /**
     * Send a formatted message to a CommandSender.
     * If sender is a Player, the message will be sent with the plugin's message prefix.
     *
     * @param sender CommandSender the entity to send the message to.
     * @param message String the message to send.
     * @param format Object[] format objects
     */
    public static void sendMessage(CommandSender sender, String message, Object... format) {
        sendMessage(sender, String.format(message, format));
    }
}

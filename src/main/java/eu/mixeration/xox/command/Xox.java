package eu.mixeration.xox.command;

import eu.mixeration.xox.PluginMain;
import eu.mixeration.xox.core.objects.Board;
import eu.mixeration.xox.core.objects.Game;
import eu.mixeration.xox.core.plugin.Locale;
import eu.mixeration.xox.core.plugin.config.Lobby_Config;
import eu.mixeration.xox.core.plugin.config.Message_Config;
import eu.mixeration.xox.core.plugin.Permissions;
import eu.mixeration.xox.core.util.LobbyUtil;
import eu.mixeration.xox.handler.BoardHandler;
import eu.mixeration.xox.handler.GameHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

@Data
@AllArgsConstructor
public class Xox implements CommandExecutor {
    private PluginMain pluginMain;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        BoardHandler boardHandler = pluginMain.getBoardHandler();
        GameHandler gameHandler = pluginMain.getGameHandler();

        switch (args.length) {
            case 1: {
                if (args[0].equalsIgnoreCase("version")) {
                    Locale.sendMessage(sender, Locale.COMMAND_VERSION, PluginMain.getInstance().getDescription().getVersion());
                    return true;
                } else if(args[0].equalsIgnoreCase("setmainlobby")) {
                    if(isPlayer(sender) && Permissions.tryPerm(sender, Permissions.COMMAND_ADMIN)) {
                        Player player = (Player) sender;
                        LobbyUtil.setSpawn(player, "lobby");
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&lXOX &7Main lobby selected."));
                    }
                } else if (args[0].equalsIgnoreCase("board") && Permissions.tryPerm(sender, Permissions.COMMAND_ADMIN)) {
                    Locale.sendMessage(sender, Locale.COMMAND_HELP_TITLE);
                    Locale.sendMessage(sender, Locale.COMMAND_HELP_ENTRY, label, "board list", "View a list of known boards");
                    Locale.sendMessage(sender, Locale.COMMAND_HELP_ENTRY, label, "board remove [id]", "Removes a board (either at location or by specifying an id)");
                    Locale.sendMessage(sender, Locale.COMMAND_HELP_ENTRY, label, "board create", "Creates a board");
                    return true;
                } else if(args[0].equalsIgnoreCase("reload-lang")) {
                    Message_Config.reloadConfig();
                    Message_Config.saveConfig();
                    Locale.sendMessage(sender, Locale.RELOADED);
                } else if (args[0].equalsIgnoreCase("leave")) {
                    if (isPlayer(sender)) {
                        Player player = (Player) sender;
                        Game game = gameHandler.getGameForPlayer(player);

                        if (game == null) {
                            Locale.sendMessage(sender, Locale.ERROR_NOT_IN_GAME);
                        } else {
                            gameHandler.removeFromGame(player);
                            LobbyUtil.sendSpawn(((Player) sender).getPlayer(), "lobby");
                        }
                    }
                    return true;
                } else {
                    if (Permissions.tryPerm(sender, Permissions.COMMAND_ADMIN)) {
                        Locale.sendMessage(sender, Locale.COMMAND_HELP_ENTRY, label, "board", "Access board specific commands");
                        Locale.sendMessage(sender, Locale.COMMAND_HELP_ENTRY, label, "reload-lang", "Reload message file");
                        Locale.sendMessage(sender, Locale.COMMAND_HELP_ENTRY, label, "setmainlobby", "Set main lobby for Xox");
                    }

                    Locale.sendMessage(sender, Locale.COMMAND_HELP_ENTRY, label, "leave", "Leave the current game");
                    Locale.sendMessage(sender, Locale.COMMAND_HELP_ENTRY, label, "version", "View plugin information");
                    Locale.sendMessage(sender, Locale.COMMAND_HELP_ENTRY, label, "help", "View plugin commands");
                }
                break;
            }
            case 2: {
                // Board Command
                if (args[0].equalsIgnoreCase("board") && Permissions.tryPerm(sender, Permissions.COMMAND_ADMIN)) {
                    // Create Subcommand
                    if (args[1].equalsIgnoreCase("create")) {
                        if (isPlayer(sender)) {
                            Player player = (Player) sender;

                            UUID id = player.getUniqueId();
                            if (boardHandler.isBoardCreator(id)) {
                                // Cancel process
                                boardHandler.removeBoardCreator(id);

                                player.sendMessage("");
                                Locale.sendMessage(sender, Locale.COMMAND_BOARD_CREATE_CANCEL);
                                player.sendMessage("");
                            } else {
                                // Start process
                                boardHandler.addBoardCreator(id);

                                player.sendMessage("");
                                Locale.sendMessage(sender, Locale.COMMAND_BOARD_CREATE_START_L1);
                                Locale.sendMessage(sender, Locale.COMMAND_BOARD_CREATE_START_L2, label);
                                player.sendMessage("");
                            }
                        }
                    } else if (args[1].equalsIgnoreCase("list")) {
                        // Process a board list command
                        Locale.sendMessage(sender, Locale.COMMAND_BOARD_LIST_TITLE, boardHandler.getBoards().values().size());

                        for (Board board : boardHandler.getBoards().values()) {
                            Location location = board.getCenterVector().getBlockLocation();
                            Locale.sendMessage(sender, Locale.COMMAND_BOARD_LIST_VALUE, location.getBlockX(), location.getBlockY(), location.getBlockZ(), board.getId());
                        }
                    } else if (args[1].equalsIgnoreCase("remove")) {
                        // Process a board remove command
                        // This variant has does not have a third argument, so we will try to find the nearest one
                        if (isPlayer(sender)) {
                            Player player = (Player) sender;
                            Board board = boardHandler.getBoardClosestToLocation(player.getLocation(), 5);

                            if (board == null) {
                                Locale.sendMessage(sender, Locale.ERROR_BOARD_REMOVE);
                            } else {
                                boardHandler.destroyBoard(board);
                                Locale.sendMessage(sender, Locale.SUCCESS_BOARD_REMOVE_SUCCESS);
                            }
                        }
                    } else {
                        Locale.sendMessage(sender, Locale.COMMAND_HELP_TITLE);
                        Locale.sendMessage(sender, Locale.COMMAND_HELP_ENTRY, label, "board list", "View a list of known boards");
                        Locale.sendMessage(sender, Locale.COMMAND_HELP_ENTRY, label, "board remove [id]", "Removes a board (either at location or by specifying an id)");
                        Locale.sendMessage(sender, Locale.COMMAND_HELP_ENTRY, label, "board create", "Creates a board");
                        return true;
                    }
                    return true;
                } else {
                    Locale.sendMessage(sender, Locale.COMMAND_HELP_TITLE);
                    Locale.sendMessage(sender, Locale.COMMAND_HELP_ENTRY, label, "board list", "View a list of known boards");
                    Locale.sendMessage(sender, Locale.COMMAND_HELP_ENTRY, label, "board remove [id]", "Removes a board (either at location or by specifying an id)");
                    Locale.sendMessage(sender, Locale.COMMAND_HELP_ENTRY, label, "board create", "Creates a board");
                    return true;
                }
            }
            case 3: {
                // Board Command
                if (args[0].equalsIgnoreCase("board") && Permissions.tryPerm(sender, Permissions.COMMAND_ADMIN)) {
                    // Remove Command with Arguments
                    if (args[1].equalsIgnoreCase("remove")) {
                        String boardIdInput = args[2];
                        UUID boardId = null;

                        try {
                            boardId = UUID.fromString(boardIdInput);
                        } catch (IllegalArgumentException ignored) {

                        }

                        Board board = boardHandler.getBoardById(boardId);

                        if (board == null) {
                            Locale.sendMessage(sender, Locale.ERROR_BOARD_REMOVE);
                        } else {
                            boardHandler.destroyBoard(board);
                            Locale.sendMessage(sender, Locale.SUCCESS_BOARD_REMOVE_SUCCESS);
                        }
                    }
                }
                return true;
            }
        }

        // Help Menu
        Locale.sendMessage(sender, Locale.COMMAND_HELP_TITLE);

        if (Permissions.tryPerm(sender, Permissions.COMMAND_ADMIN)) {
            Locale.sendMessage(sender, Locale.COMMAND_HELP_ENTRY, label, "board", "Access board specific commands");
            Locale.sendMessage(sender, Locale.COMMAND_HELP_ENTRY, label, "reload-lang", "Reload message file");
            Locale.sendMessage(sender, Locale.COMMAND_HELP_ENTRY, label, "setmainlobby", "Set main lobby for Xox");
        }

        Locale.sendMessage(sender, Locale.COMMAND_HELP_ENTRY, label, "leave", "Leave the current game");
        Locale.sendMessage(sender, Locale.COMMAND_HELP_ENTRY, label, "version", "View plugin information");
        Locale.sendMessage(sender, Locale.COMMAND_HELP_ENTRY, label, "help", "View plugin commands");

        return true;
    }

    private boolean isPlayer(CommandSender sender) {
        boolean player = (sender instanceof Player);

        if (!player) {
            Locale.sendMessage(sender, Locale.ERROR_NOT_PLAYER);
        }

        return player;
    }
}

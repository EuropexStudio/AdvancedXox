
package eu.mixeration.xox.handler;

import eu.mixeration.xox.PluginMain;
import eu.mixeration.xox.core.objects.Board;
import eu.mixeration.xox.core.objects.BoardPosition;
import eu.mixeration.xox.core.objects.Game;
import eu.mixeration.xox.core.objects.Gamestate;
import eu.mixeration.xox.core.plugin.Locale;
import eu.mixeration.xox.core.plugin.config.MainConfig;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class GameHandler {
    private final PluginMain plugin;

    @Getter
    private final List<Game> games = new ArrayList<>();

    public GameHandler() {
        this.plugin = PluginMain.getInstance();
    }

    public void generateGames() {
        BoardHandler boardHandler = plugin.getBoardHandler();

        for (UUID boardId : boardHandler.getBoards().keySet()) {
            Board board = boardHandler.getBoardById(boardId);

            if (board == null) {
                Bukkit.getLogger().log(Level.SEVERE, "[XOX] Unable to create game for board " + boardId.toString() + "!");
            } else {
                generateGame(board);
            }
        }
    }

    public void generateGame(Board board) {
        Game game = new Game(UUID.randomUUID(), board.getId());
        games.add(game);
        game.changeGamestate(Gamestate.WAITING);
    }

    public Game getGameForPlayer(Player player) {
        for (Game game : games) {
            if (game.getPlayer1Id() == player.getUniqueId() || game.getPlayer2Id() == player.getUniqueId()) {
                return game;
            }
        }

        return null;
    }

    public Game getGameForBoard(Board board) {
        for (Game game : games) {
            if (game.getBoard() != null && game.getBoard().equals(board)) {
                return game;
            }
        }
        return null;
    }

    public boolean tryAddToGame(Player player, Game game) {
        if (player == null) {
            return false;
        }
        boolean successful = false;
        if (game.getPlayer1Id() == null) {
            game.setPlayer1Id(player.getUniqueId());
            successful = true;
        } else if (game.getPlayer2Id() == null) {
            game.setPlayer2Id(player.getUniqueId());
            successful = true;
        }
        if (successful) {
            game.sendPlayersMessage(Locale.EVENT_GAME_JOIN, player.getDisplayName());
            checkGame(game);

            plugin.getMenuHandler().createColourSelectionMenu(player);
        }
        return successful;
    }

    public void removeFromGame(Player player) {
        Game game = getGameForPlayer(player);
        if (game != null) {
            game.sendPlayersMessage(Locale.EVENT_GAME_LEAVE, player.getDisplayName());
            if (game.getPlayer1Id() == player.getUniqueId()) {
                game.setPlayer1Id(null);
            } else if (game.getPlayer2Id() == player.getUniqueId()) {
                game.setPlayer2Id(null);
            }
            plugin.getActionBarUtil().clearActionBarMessage(player);
            checkGame(game);
        }
    }

    public void checkGame(Game game) {
        if ((game.getGamestate() == Gamestate.INGAME || game.getGamestate() == Gamestate.END) && (game.getPlayer1Id() == null || game.getPlayer2Id() == null)) {
            game.changeGamestate(Gamestate.WAITING);
            return;
        }
        switch (game.getGamestate()) {
            case NONE: {
                break;
            }
            case WAITING: {
                if (game.getPlayer1Id() != null && game.getPlayer2Id() != null) {
                    game.changeGamestate(Gamestate.INGAME);
                    return;
                } else {
                    game.sendPlayersActionBar(Locale.ACTIONBAR_GAME_STATUS, Locale.GAMESTATE_WAITING_DESCRIPTION);
                    game.sendPlayersMessage(Locale.EVENT_GAME_WAITING);
                }
                break;
            }
            case INGAME: {
                UUID currentTurnPlayerId = null;
                if (game.getCurrentTurn() == 1) {
                    currentTurnPlayerId = game.getPlayer1Id();
                } else if (game.getCurrentTurn() == 2) {
                    currentTurnPlayerId = game.getPlayer2Id();
                }
                List<BoardPosition> boardPositions = game.findThreeInARow();
                if (boardPositions != null) {
                    game.setWinnerId(game.getPlayerTurn(boardPositions.get(0))); // Pretty crude but it works
                    game.changeGamestate(Gamestate.END);
                    return;
                }
                if (game.isGameATie()) {
                    game.setWinnerId(null);
                    game.changeGamestate(Gamestate.END);
                    return;
                }
                Player player = Bukkit.getPlayer(currentTurnPlayerId);
                game.sendPlayersActionBar(Locale.ACTIONBAR_GAME_STATUS, String.format(Locale.GAMESTATE_INGAME_DESCRIPTION, player.getDisplayName()));
                break;
            }
            case END: {
                String winnerName;
                UUID winnerId = game.getWinnerId();
                if (winnerId == null) {
                    winnerName = "Tie!";
                } else {
                    Player winnerPlayer = Bukkit.getPlayer(game.getWinnerId());

                    if (winnerPlayer == null) {
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(game.getWinnerId());
                        winnerName = offlinePlayer.getName();
                    } else {
                        winnerName = winnerPlayer.getDisplayName();
                    }
                }

                game.sendPlayersMessage(Locale.EVENT_GAME_WINNER, winnerName);
                game.sendPlayersActionBar(Locale.ACTIONBAR_GAME_STATUS, String.format(Locale.GAMESTATE_END_DESCRIPTION, winnerName));
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (game.getGamestate() == Gamestate.END) {
                        game.changeGamestate(Gamestate.WAITING);
                    }
                }, 20 * MainConfig.getConfig().getEndOfRoundSeconds());
                BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                    if (game.getGamestate() == Gamestate.END && game.getBoard() != null) {
                        List<BoardPosition> boardPositions = game.findThreeInARow();
                        if ((boardPositions == null || boardPositions.size() != 3) && game.isGameATie()) {
                            boardPositions = new ArrayList<>(Arrays.asList(BoardPosition.values()));
                        }
                        if (boardPositions != null && !boardPositions.isEmpty()) {
                            for (BoardPosition boardPosition : boardPositions) {
                                ItemFrame itemFrame = game.getBoard().getBoardItem(boardPosition).getItemFrame();
                                if (itemFrame.getItem().getType() == Material.AIR) {
                                    UUID placedItemPlayerId = game.getPlayerTurn(boardPosition);
                                    if (placedItemPlayerId == game.getPlayer1Id()) {
                                        itemFrame.setItem(game.getPlayer1Item(), false);
                                    } else if (placedItemPlayerId == game.getPlayer2Id()) {
                                        itemFrame.setItem(game.getPlayer2Item(), false);
                                    }
                                } else {
                                    itemFrame.setItem(new ItemStack(Material.AIR), false);
                                }
                            }
                            return;
                        }
                    }

                    game.cancelWinAnimationTask();
                }, 0, 5);
                game.setWinTaskAnimationId(bukkitTask.getTaskId());
                break;
            }
        }
    }

    public void destroyGame(Game game) {
        games.remove(game);
    }
}
